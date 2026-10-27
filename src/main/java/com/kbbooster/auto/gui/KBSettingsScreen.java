package com.kbbooster.auto.gui;

import com.kbbooster.auto.config.KBConfig;
import com.kbbooster.auto.core.SprintResetManager;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import java.io.IOException;

/** Tela de configuracoes. Tudo e salvo automaticamente ao alterar e ao fechar. */
public class KBSettingsScreen extends GuiScreen {

    private static final int ID_ENABLED = 1;
    private static final int ID_HUD = 2;
    private static final int ID_CONFIRM = 3;
    private static final int ID_CD_MINUS = 4;
    private static final int ID_CD_PLUS = 5;
    private static final int ID_WIN_MINUS = 6;
    private static final int ID_WIN_PLUS = 7;
    private static final int ID_ZERO = 8;
    private static final int ID_DONE = 9;
    private static final int ID_LABEL_CD = 10;
    private static final int ID_LABEL_WIN = 11;

    private GuiButton btnEnabled;
    private GuiButton btnHud;
    private GuiButton btnConfirm;
    private GuiButton lblCooldown;
    private GuiButton lblWindow;

    @Override
    public void initGui() {
        this.buttonList.clear();
        int cx = this.width / 2;
        int y = this.height / 2 - 70;

        btnEnabled = new GuiButton(ID_ENABLED, cx - 100, y, 200, 20, "");
        btnHud = new GuiButton(ID_HUD, cx - 100, y + 24, 200, 20, "");
        btnConfirm = new GuiButton(ID_CONFIRM, cx - 100, y + 48, 200, 20, "");
        this.buttonList.add(btnEnabled);
        this.buttonList.add(btnHud);
        this.buttonList.add(btnConfirm);

        this.buttonList.add(new GuiButton(ID_CD_MINUS, cx - 100, y + 72, 20, 20, "-"));
        lblCooldown = new GuiButton(ID_LABEL_CD, cx - 78, y + 72, 156, 20, "");
        lblCooldown.enabled = false;
        this.buttonList.add(lblCooldown);
        this.buttonList.add(new GuiButton(ID_CD_PLUS, cx + 80, y + 72, 20, 20, "+"));

        this.buttonList.add(new GuiButton(ID_WIN_MINUS, cx - 100, y + 96, 20, 20, "-"));
        lblWindow = new GuiButton(ID_LABEL_WIN, cx - 78, y + 96, 156, 20, "");
        lblWindow.enabled = false;
        this.buttonList.add(lblWindow);
        this.buttonList.add(new GuiButton(ID_WIN_PLUS, cx + 80, y + 96, 20, 20, "+"));

        this.buttonList.add(new GuiButton(ID_ZERO, cx - 100, y + 124, 200, 20, "Zerar contador de resets"));
        this.buttonList.add(new GuiButton(ID_DONE, cx - 100, y + 148, 200, 20, "Concluir"));

        refreshLabels();
    }

    private void refreshLabels() {
        btnEnabled.displayString = "Sprint Reset automatico: " + (KBConfig.enabled ? "ON" : "OFF");
        btnHud.displayString = "HUD: " + (KBConfig.hudEnabled ? "ON" : "OFF");
        btnConfirm.displayString = "Confirmar acerto antes de resetar: " + (KBConfig.requireConfirmation ? "SIM" : "NAO");
        lblCooldown.displayString = "Intervalo: " + KBConfig.cooldownTicks + " ticks (" + (KBConfig.cooldownTicks * 50) + " ms)";
        lblWindow.displayString = "Janela: " + KBConfig.confirmWindowTicks + " ticks (" + (KBConfig.confirmWindowTicks * 50) + " ms)";
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case ID_ENABLED:
                SprintResetManager.setEnabled(!KBConfig.enabled);
                break;
            case ID_HUD:
                KBConfig.hudEnabled = !KBConfig.hudEnabled;
                break;
            case ID_CONFIRM:
                KBConfig.requireConfirmation = !KBConfig.requireConfirmation;
                break;
            case ID_CD_MINUS:
                KBConfig.cooldownTicks = KBConfig.clamp(KBConfig.cooldownTicks - 1, 1, 20);
                break;
            case ID_CD_PLUS:
                KBConfig.cooldownTicks = KBConfig.clamp(KBConfig.cooldownTicks + 1, 1, 20);
                break;
            case ID_WIN_MINUS:
                KBConfig.confirmWindowTicks = KBConfig.clamp(KBConfig.confirmWindowTicks - 1, 2, 20);
                break;
            case ID_WIN_PLUS:
                KBConfig.confirmWindowTicks = KBConfig.clamp(KBConfig.confirmWindowTicks + 1, 2, 20);
                break;
            case ID_ZERO:
                SprintResetManager.resetCounter();
                break;
            case ID_DONE:
                KBConfig.save();
                this.mc.displayGuiScreen(null);
                return;
            default:
                break;
        }
        KBConfig.save();
        refreshLabels();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, "KB Booster Auto - Configuracoes",
                this.width / 2, this.height / 2 - 92, 0xFFFFFF);
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.drawCenteredString(this.fontRendererObj, "Teclas: Opcoes > Controles > KB Booster Auto",
                this.width / 2, this.height / 2 + 104, 0xAAAAAA);
    }

    @Override
    public void onGuiClosed() {
        KBConfig.save();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
