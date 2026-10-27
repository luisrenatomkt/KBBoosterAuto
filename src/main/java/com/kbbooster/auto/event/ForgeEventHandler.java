package com.kbbooster.auto.event;

import com.kbbooster.auto.core.SprintResetManager;
import com.kbbooster.auto.hud.HudRenderer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/** Eventos do barramento do Forge (MinecraftForge.EVENT_BUS). */
public class ForgeEventHandler {

    /**
     * Disparado por EntityPlayer.attackTargetEntityWithCurrentItem. No cliente, o pacote C02 de ataque
     * ja foi enfileirado antes deste evento, entao qualquer pacote nosso sai DEPOIS do ataque.
     * No singleplayer o evento tambem roda no servidor interno; por isso filtramos worldObj.isRemote.
     */
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onAttack(AttackEntityEvent event) {
        if (event.entityPlayer == null || !event.entityPlayer.worldObj.isRemote) {
            return;
        }
        SprintResetManager.onAttack(event.entityPlayer, event.target);
    }

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.type != RenderGameOverlayEvent.ElementType.ALL) {
            return;
        }
        HudRenderer.render();
    }
}
