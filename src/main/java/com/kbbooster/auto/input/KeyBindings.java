package com.kbbooster.auto.input;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.input.Keyboard;

/**
 * Teclas do mod. Aparecem em Opcoes > Controles > "KB Booster Auto" e sao salvas pelo proprio Minecraft.
 */
public final class KeyBindings {

    public static final String CATEGORY = "KB Booster Auto";

    public static final KeyBinding TOGGLE =
            new KeyBinding("Ativar/Desativar Sprint Reset", Keyboard.KEY_K, CATEGORY);

    public static final KeyBinding SETTINGS =
            new KeyBinding("Abrir configuracoes", Keyboard.KEY_J, CATEGORY);

    private KeyBindings() {
    }

    public static void register() {
        ClientRegistry.registerKeyBinding(TOGGLE);
        ClientRegistry.registerKeyBinding(SETTINGS);
    }
}
