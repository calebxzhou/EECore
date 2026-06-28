package com.endlessepoch.core.screen.creative;

import com.endlessepoch.core.gui.CyberButton;
import com.endlessepoch.core.api.energy.OmegaValue;
import com.endlessepoch.core.api.tier.VoltageTier;
import com.endlessepoch.core.blockentity.creative.CreativeGeneratorBlockEntity;
import com.endlessepoch.core.menu.creative.CreativeGeneratorMenu;
import com.endlessepoch.core.screen.CreativeMachineScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import java.math.BigInteger;

public class CreativeGeneratorScreen extends CreativeMachineScreen<CreativeGeneratorMenu> {

    public CreativeGeneratorScreen(CreativeGeneratorMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
    }

    @Override
    protected void createMainButtons(int startX, int cy) {
        CreativeGeneratorBlockEntity be = getMenu().getBlockEntity();
        if (be == null) return;

        cyberButtons.add(new CyberButton(
                startX, cy + BUTTON_Y, BUTTON_W, BUTTON_H,
                () -> {
                    CreativeGeneratorBlockEntity b = getMenu().getBlockEntity();
                    return b != null && b.isOutputEnabled()
                            ? Component.translatable("eecore.gui.generator.running").getString()
                            : Component.translatable("eecore.gui.generator.paused").getString();
                },
                () -> getMenu().toggleOutput()
        ));

        cyberButtons.add(new CyberButton(
                startX + BUTTON_W + BUTTON_GAP, cy + BUTTON_Y, BUTTON_W, BUTTON_H,
                Component.translatable("eecore.gui.generator.reset").getString(),
                () -> getMenu().resetToLV()
        ));

        cyberButtons.add(new CyberButton(
                startX + 2 * (BUTTON_W + BUTTON_GAP), cy + BUTTON_Y, BUTTON_W, BUTTON_H,
                () -> {
                    CreativeGeneratorBlockEntity b = getMenu().getBlockEntity();
                    return b != null && b.isLogToChat() ? "ON" : "OFF";
                },
                () -> {
                    CreativeGeneratorBlockEntity b = getMenu().getBlockEntity();
                    if (b != null) getMenu().setLogToChat(!b.isLogToChat());
                }
        ));
    }

    @Override
    protected void renderMainInfo(GuiGraphics g) {
        CreativeGeneratorBlockEntity be = getMenu().getBlockEntity();
        if (be == null) return;

        VoltageTier tier = be.getSelectedTier();
        String tierShort = tier != null ? tier.getShortName() : "LV";
        BigInteger output = be.getOutputPerTick();
        Component line1 = Component.translatable("eecore.gui.generator.tick_output",
                OmegaValue.of(output).toDisplayString(), tierShort);
        int tw1 = font.width(line1);
        int tx1 = leftPos + (imageWidth - tw1) / 2;
        g.drawString(font, line1, tx1, topPos + 50, style.textSecondary, false);
    }

    @Override
    protected void renderSettingsContent(GuiGraphics g) {
        CreativeGeneratorBlockEntity be = getMenu().getBlockEntity();
        if (be == null) return;

        String tierText = be.getSelectedTier().getShortName();
        int tw = font.width(tierText);
        int tx = leftPos + (imageWidth - tw) / 2;
        int ty = topPos + BUTTON_Y + (BUTTON_H - 8) / 2;
        g.drawString(font, Component.literal(tierText), tx, ty, style.textPrimary, false);
    }

    @Override
    protected Component getTitleText(boolean showSettings) {
        CreativeGeneratorBlockEntity be = getMenu().getBlockEntity();
        String tierShort = be != null ? be.getSelectedTier().getShortName() : "LV";
        if (showSettings) return Component.translatable("eecore.gui.settings.title");
        return Component.translatable("eecore.gui.generator.title", tierShort);
    }

    @Override
    protected boolean canShowArrows() {
        return true;
    }

    @Override
    protected void onTierDown() {
        CreativeGeneratorBlockEntity be = getMenu().getBlockEntity();
        if (be != null) {
            VoltageTier current = be.getSelectedTier();
            VoltageTier prev = current.prev();
            if (prev != current && prev != VoltageTier.ELV) getMenu().setTier(prev);
        }
    }

    @Override
    protected void onTierUp() {
        CreativeGeneratorBlockEntity be = getMenu().getBlockEntity();
        if (be != null) {
            VoltageTier current = be.getSelectedTier();
            VoltageTier next = current.next();
            if (next != current) getMenu().setTier(next);
        }
    }
}
