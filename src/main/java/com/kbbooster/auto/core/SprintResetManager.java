package com.kbbooster.auto.core;

import com.kbbooster.auto.config.KBConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

/**
 * Logica central do Sprint Reset automatico.
 *
 * Mecanica (Minecraft 1.8.9):
 *  - O knockback extra do sprint depende do estado de sprint do jogador NO SERVIDOR no momento do golpe.
 *  - Quando o golpe causa dano com knockback, o servidor desliga o sprint desse jogador.
 *  - O cliente continua achando que esta correndo e, por isso, nao envia novo START_SPRINTING.
 *    Resultado: o proximo golpe sai sem o bonus de sprint, a menos que o sprint seja "reiniciado".
 *  - O W-tap manual faz exatamente isso: o cliente envia STOP_SPRINTING e depois START_SPRINTING.
 *
 * Este mod envia esses mesmos dois pacotes (C0BPacketEntityAction) apos um acerto,
 * sem mexer em nenhuma tecla do jogador e sem alterar o estado local de sprint.
 */
public final class SprintResetManager {

    private static final long CONFIRMED_FLASH_MS = 700L;

    private static int tickCounter = 0;
    private static int resetCount = 0;
    private static int lastResetTick = -1000;

    private static boolean pending = false;
    private static int pendingTargetId = -1;
    private static int pendingStartTick = 0;
    private static int pendingLastHurtTime = 0;

    private static long lastValidHitMs = 0L;

    private SprintResetManager() {
    }

    private static Minecraft mc() {
        return Minecraft.getMinecraft();
    }

    // ------------------------------------------------------------------ eventos

    /** Chamado quando o jogador local ataca uma entidade (AttackEntityEvent, lado cliente). */
    public static void onAttack(EntityPlayer attacker, Entity target) {
        if (!KBConfig.enabled) {
            return;
        }
        EntityPlayerSP player = mc().thePlayer;
        if (player == null || attacker != player) {
            return;
        }
        if (!(target instanceof EntityPlayer) || target == player) {
            return;
        }
        // Sem sprint no golpe nao houve bonus de knockback, entao nao ha o que resetar.
        if (!player.isSprinting()) {
            return;
        }

        if (!KBConfig.requireConfirmation) {
            lastValidHitMs = System.currentTimeMillis();
            tryReset(player);
            return;
        }

        EntityPlayer victim = (EntityPlayer) target;
        pending = true;
        pendingTargetId = victim.getEntityId();
        pendingStartTick = tickCounter;
        pendingLastHurtTime = victim.hurtTime;
    }

    /** Chamado no fim de cada tick do cliente. */
    public static void onClientTick() {
        tickCounter++;

        EntityPlayerSP player = mc().thePlayer;
        if (player == null || mc().theWorld == null || !KBConfig.enabled) {
            pending = false;
            return;
        }
        if (!pending) {
            return;
        }

        Entity entity = mc().theWorld.getEntityByID(pendingTargetId);
        if (!(entity instanceof EntityPlayer)) {
            pending = false;
            return;
        }

        EntityPlayer victim = (EntityPlayer) entity;
        // Quando o servidor confirma o dano, o cliente recebe o status de "ferido" e hurtTime sobe para 10.
        if (victim.hurtTime > pendingLastHurtTime) {
            pending = false;
            lastValidHitMs = System.currentTimeMillis();
            tryReset(player);
            return;
        }
        pendingLastHurtTime = victim.hurtTime;

        if (tickCounter - pendingStartTick > KBConfig.confirmWindowTicks) {
            pending = false; // nao confirmou a tempo: descarta (golpe bloqueado, sem dano, ping alto etc.)
        }
    }

    // ------------------------------------------------------------------ reset

    private static boolean tryReset(EntityPlayerSP player) {
        if (tickCounter - lastResetTick < KBConfig.cooldownTicks) {
            return false; // evita resets repetidos
        }
        if (!player.isSprinting() || player.ridingEntity != null) {
            return false; // nunca forca o sprint: so reinicia se o jogador ainda esta correndo
        }
        NetHandlerPlayClient net = mc().getNetHandler();
        if (net == null) {
            return false;
        }

        net.addToSendQueue(new C0BPacketEntityAction(player, C0BPacketEntityAction.Action.STOP_SPRINTING));
        net.addToSendQueue(new C0BPacketEntityAction(player, C0BPacketEntityAction.Action.START_SPRINTING));

        lastResetTick = tickCounter;
        resetCount++;
        return true;
    }

    // ------------------------------------------------------------------ controle

    public static void setEnabled(boolean value) {
        KBConfig.enabled = value;
        if (!value) {
            pending = false;
        }
        KBConfig.save();
    }

    public static void toggle() {
        setEnabled(!KBConfig.enabled);
        EntityPlayerSP player = mc().thePlayer;
        if (player != null) {
            String state = KBConfig.enabled
                    ? EnumChatFormatting.GREEN + "ON"
                    : EnumChatFormatting.RED + "OFF";
            player.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA + "[KB Booster Auto] "
                    + EnumChatFormatting.WHITE + "Sprint Reset: " + state));
        }
    }

    public static void resetCounter() {
        resetCount = 0;
    }

    // ------------------------------------------------------------------ leitura (HUD)

    public static int getResetCount() {
        return resetCount;
    }

    public static boolean isPending() {
        return pending;
    }

    public static boolean isRecentlyConfirmed() {
        return System.currentTimeMillis() - lastValidHitMs < CONFIRMED_FLASH_MS;
    }
}
