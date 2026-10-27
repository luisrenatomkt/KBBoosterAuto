package com.kbbooster.auto.event;

import com.kbbooster.auto.core.SprintResetManager;
import com.kbbooster.auto.gui.KBSettingsScreen;
import com.kbbooster.auto.input.KeyBindings;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/** Eventos do barramento do FML (FMLCommonHandler.instance().bus()): tick e teclado. */
public class FmlEventHandler {

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        SprintResetManager.onClientTick();
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.thePlayer == null) {
            return;
        }
        if (KeyBindings.TOGGLE.isPressed()) {
            SprintResetManager.toggle();
        }
        if (KeyBindings.SETTINGS.isPressed() && mc.currentScreen == null) {
            mc.displayGuiScreen(new KBSettingsScreen());
        }
    }
}
