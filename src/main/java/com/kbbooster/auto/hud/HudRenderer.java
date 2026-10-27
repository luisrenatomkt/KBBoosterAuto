package com.kbbooster.auto.hud;

import com.kbbooster.auto.config.KBConfig;
import com.kbbooster.auto.core.SprintResetManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.EnumChatFormatting;

/** HUD minimalista desenhada sobre o jogo. */
public final class HudRenderer {

    private HudRenderer() {
    }

    public static void render() {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayerSP player = mc.thePlayer;
        if (!KBConfig.hudEnabled || player == null) {
            return;
        }
        if (mc.gameSettings.showDebugInfo || mc.gameSettings.hideGUI) {
            return;
        }

        FontRenderer fr = mc.fontRendererObj;

        String title = EnumChatFormatting.AQUA + "KB Booster Auto";
        String reset = "Sprint Reset: "
                + (KBConfig.enabled ? EnumChatFormatting.GREEN + "ON" : EnumChatFormatting.RED + "OFF");
        String sprint = "Sprint: "
                + (player.isSprinting() ? EnumChatFormatting.GREEN + "ON" : EnumChatFormatting.GRAY + "OFF");
        String count = "Resets: " + EnumChatFormatting.WHITE + SprintResetManager.getResetCount();

        String hitText;
        int hitColor;
        if (SprintResetManager.isRecentlyConfirmed()) {
            hitText = "Ataque valido";
            hitColor = 0xFF55FF55;
        } else if (SprintResetManager.isPending()) {
            hitText = "Verificando...";
            hitColor = 0xFFFFFF55;
        } else {
            hitText = "Ataque: --";
            hitColor = 0xFF777777;
        }

        int x = KBConfig.hudX;
        int y = KBConfig.hudY;
        int lineH = fr.FONT_HEIGHT + 2;
        int indicatorW = 10;

        int width = Math.max(Math.max(fr.getStringWidth(title), fr.getStringWidth(reset)),
                Math.max(fr.getStringWidth(sprint), fr.getStringWidth(count)));
        width = Math.max(width, indicatorW + fr.getStringWidth(hitText));

        Gui.drawRect(x - 4, y - 4, x + width + 4, y + lineH * 5 + 2, 0x90000000);

        fr.drawStringWithShadow(title, x, y, 0xFFFFFF);
        fr.drawStringWithShadow(reset, x, y + lineH, 0xFFFFFF);
        fr.drawStringWithShadow(sprint, x, y + lineH * 2, 0xFFFFFF);
        fr.drawStringWithShadow(count, x, y + lineH * 3, 0xFFFFFF);

        int iy = y + lineH * 4;
        Gui.drawRect(x, iy + 1, x + 6, iy + 7, hitColor);
        fr.drawStringWithShadow(hitText, x + indicatorW, iy, 0xFFFFFF);
    }
}
