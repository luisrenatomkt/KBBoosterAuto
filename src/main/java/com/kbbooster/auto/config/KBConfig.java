package com.kbbooster.auto.config;

import net.minecraftforge.common.config.Configuration;

import java.io.File;

/**
 * Configuracoes do mod, salvas em config/kbboosterauto.cfg.
 * A tecla de ativacao fica nos Controles do Minecraft (options.txt), salva automaticamente pelo jogo.
 */
public final class KBConfig {

    private static final String CAT = "general";
    private static Configuration config;

    /** Sprint Reset automatico ligado. */
    public static boolean enabled = true;
    /** HUD visivel. */
    public static boolean hudEnabled = true;
    /** Se true, so reseta depois que o servidor confirma o dano no alvo (animacao de dano). */
    public static boolean requireConfirmation = true;
    /** Intervalo minimo entre dois resets, em ticks (1 tick = 50 ms). */
    public static int cooldownTicks = 6;
    /** Quantos ticks esperar pela confirmacao do acerto antes de descartar o ataque. */
    public static int confirmWindowTicks = 6;
    /** Posicao da HUD (pixels, canto superior esquerdo). */
    public static int hudX = 6;
    public static int hudY = 6;

    private KBConfig() {
    }

    public static void init(File file) {
        config = new Configuration(file);
        load();
    }

    public static void load() {
        if (config == null) {
            return;
        }
        enabled = config.get(CAT, "sprintResetEnabled", true,
                "Ativa o Sprint Reset automatico.").getBoolean(true);
        hudEnabled = config.get(CAT, "hudEnabled", true,
                "Mostra a HUD na tela.").getBoolean(true);
        requireConfirmation = config.get(CAT, "requireConfirmation", true,
                "Reseta somente apos confirmar o acerto (animacao de dano do alvo). Se false, reseta logo apos o ataque.").getBoolean(true);
        cooldownTicks = clamp(config.get(CAT, "cooldownTicks", 6,
                "Intervalo minimo entre resets, em ticks (1 tick = 50 ms). Faixa: 1 a 20.", 1, 20).getInt(6), 1, 20);
        confirmWindowTicks = clamp(config.get(CAT, "confirmWindowTicks", 6,
                "Janela, em ticks, para confirmar o acerto (depende do ping). Faixa: 2 a 20.", 2, 20).getInt(6), 2, 20);
        hudX = clamp(config.get(CAT, "hudX", 6, "Posicao X da HUD.", 0, 4000).getInt(6), 0, 4000);
        hudY = clamp(config.get(CAT, "hudY", 6, "Posicao Y da HUD.", 0, 4000).getInt(6), 0, 4000);

        if (config.hasChanged()) {
            config.save();
        }
    }

    public static void save() {
        if (config == null) {
            return;
        }
        config.get(CAT, "sprintResetEnabled", true).set(enabled);
        config.get(CAT, "hudEnabled", true).set(hudEnabled);
        config.get(CAT, "requireConfirmation", true).set(requireConfirmation);
        config.get(CAT, "cooldownTicks", 6).set(cooldownTicks);
        config.get(CAT, "confirmWindowTicks", 6).set(confirmWindowTicks);
        config.get(CAT, "hudX", 6).set(hudX);
        config.get(CAT, "hudY", 6).set(hudY);
        config.save();
    }

    public static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
