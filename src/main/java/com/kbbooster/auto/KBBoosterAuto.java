package com.kbbooster.auto;

import com.kbbooster.auto.config.KBConfig;
import com.kbbooster.auto.event.FmlEventHandler;
import com.kbbooster.auto.event.ForgeEventHandler;
import com.kbbooster.auto.input.KeyBindings;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

/**
 * Classe principal do mod. Apenas cliente: nao precisa estar instalado no servidor.
 */
@Mod(modid = KBBoosterAuto.MODID,
        name = KBBoosterAuto.NAME,
        version = KBBoosterAuto.VERSION,
        acceptedMinecraftVersions = "[1.8.9]",
        clientSideOnly = true,
        acceptableRemoteVersions = "*")
public class KBBoosterAuto {

    public static final String MODID = "kbboosterauto";
    public static final String NAME = "KB Booster Auto";
    public static final String VERSION = "1.0.0";

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        KBConfig.init(event.getSuggestedConfigurationFile());
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        KeyBindings.register();
        // No Forge 1.8.9 existem dois barramentos: eventos do Forge e eventos do FML (tick/teclado).
        MinecraftForge.EVENT_BUS.register(new ForgeEventHandler());
        FMLCommonHandler.instance().bus().register(new FmlEventHandler());
    }
}
