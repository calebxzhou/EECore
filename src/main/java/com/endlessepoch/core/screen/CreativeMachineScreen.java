package com.endlessepoch.core.screen;

import com.endlessepoch.core.gui.CyberButton;
import com.endlessepoch.core.gui.CyberGUIStyle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class CreativeMachineScreen<T extends AbstractContainerMenu> extends AbstractContainerScreen<T> {

    protected static final int PLAYER_INVENTORY_X = 8;
    protected static final int PLAYER_INVENTORY_Y = 84;
    protected static final int HOTBAR_Y = 142;
    protected static final int SLOT_SIZE = 18;
    protected static final int BUTTON_Y = 28;
    protected static final int BUTTON_W = 40;
    protected static final int BUTTON_H = 16;
    protected static final int BUTTON_GAP = 4;
    protected static final int ARROW_SIZE = 16;
    protected static final int RAIN_COUNT = 16;

    protected CyberGUIStyle style = CyberGUIStyle.GREEN;

    protected final Random random = new Random();
    protected final List<RainChar> rainChars = new ArrayList<>();
    protected long lastRainUpdate = 0;

    protected long lastBlinkTime = 0;
    protected boolean blinkState = true;

    protected final List<CyberButton> cyberButtons = new ArrayList<>();
    protected boolean showSettings = false;
    protected CyberButton tierDownBtn, tierUpBtn;

    public CreativeMachineScreen(T menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
        this.inventoryLabelY = -1000;
        this.titleLabelY = -1000;
    }

    public void setStyle(CyberGUIStyle style) { this.style = style; }

    // Abstract methods for subclasses
    protected abstract void createMainButtons(int startX, int cy);
    protected abstract void renderMainInfo(GuiGraphics g);
    protected abstract void renderSettingsContent(GuiGraphics g);
    protected abstract Component getTitleText(boolean showSettings);
    protected abstract boolean canShowArrows();
    protected abstract void onTierDown();
    protected abstract void onTierUp();

    @Override
    protected void init() {
        this.cyberButtons.clear();
        this.children().clear();
        this.renderables.clear();

        super.init();

        int cx = (this.width - this.imageWidth) / 2;
        int cy = (this.height - this.imageHeight) / 2;

        rainChars.clear();
        for (int i = 0; i < RAIN_COUNT; i++) {
            rainChars.add(new RainChar());
        }
        lastBlinkTime = System.currentTimeMillis();

        int startX = cx + 24;
        createMainButtons(startX, cy);

        // Gear button (always visible)
        cyberButtons.add(new CyberButton(
                cx + this.imageWidth + 4, cy - 2, 14, 14,
                "⚙",
                () -> {
                    showSettings = !showSettings;
                    updateVisibility();
                    playClickSound();
                }
        ));

        tierDownBtn = new CyberButton(
                cx + 55, cy + BUTTON_Y, ARROW_SIZE, BUTTON_H,
                "◀",
                () -> { if (canShowArrows()) onTierDown(); }
        );
        tierDownBtn.visible = false;
        cyberButtons.add(tierDownBtn);

        tierUpBtn = new CyberButton(
                cx + 105, cy + BUTTON_Y, ARROW_SIZE, BUTTON_H,
                "▶",
                () -> { if (canShowArrows()) onTierUp(); }
        );
        tierUpBtn.visible = false;
        cyberButtons.add(tierUpBtn);

        updateVisibility();
    }

    protected void updateVisibility() {
        for (int i = 0; i < 3 && i < cyberButtons.size(); i++) {
            cyberButtons.get(i).visible = !showSettings;
        }
        // Gear button always visible
        if (cyberButtons.size() > 3) cyberButtons.get(3).visible = true;

        tierDownBtn.visible = showSettings && canShowArrows();
        tierUpBtn.visible = showSettings && canShowArrows();
    }

    protected void playClickSound() {
        Minecraft.getInstance().getSoundManager().play(
                net.minecraft.client.resources.sounds.SimpleSoundInstance.forUI(
                        SoundEvents.UI_BUTTON_CLICK, 1.0F
                )
        );
    }

    // Render loop

    @Override
    public void render(GuiGraphics g, int mx, int my, float pt) {
        super.renderBackground(g, mx, my, pt);
        super.render(g, mx, my, pt);
        this.renderTooltip(g, mx, my);

        for (CyberButton btn : cyberButtons) {
            btn.hovered = btn.isMouseOver(mx, my);
        }
    }

    @Override
    protected void renderBg(GuiGraphics g, float pt, int mx, int my) {
        g.fill(leftPos, topPos, leftPos + imageWidth, topPos + imageHeight, style.bgDark);

        updateRain();
        for (RainChar rc : rainChars) {
            rc.render(g, font, leftPos, topPos, imageWidth, 65);
        }

        renderMainBorder(g);
        renderTitle(g);
        drawPlayerInventory(g);
        drawSeparator(g);

        for (CyberButton btn : cyberButtons) {
            btn.render(g, font, style);
        }

        if (showSettings) {
            renderSettingsContent(g);
        } else {
            renderMainInfo(g);
        }
    }

    // Border

    protected void renderMainBorder(GuiGraphics g) {
        int b = style.border, gb = style.borderGlow, c = style.corner;
        g.fill(leftPos - 2, topPos - 2, leftPos + imageWidth + 2, topPos - 1, b);
        g.fill(leftPos - 2, topPos + imageHeight + 1, leftPos + imageWidth + 2, topPos + imageHeight + 2, b);
        g.fill(leftPos - 2, topPos - 2, leftPos - 1, topPos + imageHeight + 2, b);
        g.fill(leftPos + imageWidth + 1, topPos - 2, leftPos + imageWidth + 2, topPos + imageHeight + 2, b);

        g.fill(leftPos, topPos, leftPos + imageWidth, topPos + 1, gb);
        g.fill(leftPos, topPos + imageHeight - 1, leftPos + imageWidth, topPos + imageHeight, gb);
        g.fill(leftPos, topPos, leftPos + 1, topPos + imageHeight, gb);
        g.fill(leftPos + imageWidth - 1, topPos, leftPos + imageWidth, topPos + imageHeight, gb);

        g.fill(leftPos - 2, topPos - 2, leftPos + 3, topPos - 1, c);
        g.fill(leftPos - 2, topPos - 2, leftPos - 1, topPos + 3, c);
        g.fill(leftPos + imageWidth - 3, topPos - 2, leftPos + imageWidth + 2, topPos - 1, c);
        g.fill(leftPos + imageWidth + 1, topPos - 2, leftPos + imageWidth + 2, topPos + 3, c);
        g.fill(leftPos - 2, topPos + imageHeight + 1, leftPos + 3, topPos + imageHeight + 2, c);
        g.fill(leftPos - 2, topPos + imageHeight - 1, leftPos - 1, topPos + imageHeight + 2, c);
        g.fill(leftPos + imageWidth - 3, topPos + imageHeight + 1, leftPos + imageWidth + 2, topPos + imageHeight + 2, c);
        g.fill(leftPos + imageWidth + 1, topPos + imageHeight - 1, leftPos + imageWidth + 2, topPos + imageHeight + 2, c);
    }

    // Title

    protected void renderTitle(GuiGraphics g) {
        updateBlink();
        if (!blinkState) return;

        Component title = getTitleText(showSettings);
        int tw = font.width(title);
        int tx = leftPos + (imageWidth - tw) / 2;
        g.drawString(font, title, tx + 1, topPos + 6 + 1, 0x44000000, false);
        g.drawString(font, title, tx, topPos + 6, style.textPrimary, false);
    }

    // Inventory

    protected void drawPlayerInventory(GuiGraphics g) {
        int startX = leftPos + PLAYER_INVENTORY_X;
        int startY = topPos + PLAYER_INVENTORY_Y;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                drawSlotBorder(g, startX + col * SLOT_SIZE - 1, startY + row * SLOT_SIZE - 1);
            }
        }
        int hotbarY = topPos + HOTBAR_Y;
        for (int col = 0; col < 9; col++) {
            drawSlotBorder(g, startX + col * SLOT_SIZE - 1, hotbarY - 1);
        }
    }

    protected void drawSlotBorder(GuiGraphics g, int x, int y) {
        int size = SLOT_SIZE;
        g.fill(x + 1, y + 1, x + size - 1, y + 2, style.innerBorder);
        g.fill(x + 1, y + size - 2, x + size - 1, y + size - 1, style.innerBorder);
        g.fill(x + 1, y + 1, x + 2, y + size - 1, style.innerBorder);
        g.fill(x + size - 2, y + 1, x + size - 1, y + size - 1, style.innerBorder);
        int c = style.corner;
        g.fill(x + 1, y + 1, x + 3, y + 2, c);
        g.fill(x + 1, y + 1, x + 2, y + 3, c);
        g.fill(x + size - 3, y + 1, x + size - 1, y + 2, c);
        g.fill(x + size - 2, y + 1, x + size - 1, y + 3, c);
        g.fill(x + 1, y + size - 2, x + 3, y + size - 1, c);
        g.fill(x + 1, y + size - 3, x + 2, y + size - 1, c);
        g.fill(x + size - 3, y + size - 2, x + size - 1, y + size - 1, c);
        g.fill(x + size - 2, y + size - 3, x + size - 1, y + size - 1, c);
    }

    // Separator

    protected void drawSeparator(GuiGraphics g) {
        updateBlink();
        if (!blinkState) return;
        int sepY = topPos + 75;
        int leftX = leftPos + 12;
        int rightX = leftPos + imageWidth - 12;

        Component title = Component.translatable("eecore.gui.inventory_label");
        int tw = font.width(title);
        int tx = leftPos + (imageWidth - tw) / 2;

        for (int x = leftX; x < tx - 4; x += 3) {
            g.fill(x, sepY, x + 1, sepY + 1, style.border);
        }
        for (int x = tx + tw + 4; x < rightX; x += 3) {
            g.fill(x, sepY, x + 1, sepY + 1, style.border);
        }
        g.drawString(font, title, tx, sepY - 4, style.textSecondary, false);
    }

    // Matrix rain

    protected void updateRain() {
        long now = System.currentTimeMillis();
        if (now - lastRainUpdate < 40) return;
        lastRainUpdate = now;
        for (RainChar rc : rainChars) {
            rc.update();
        }
    }

    protected void updateBlink() {
        long now = System.currentTimeMillis();
        if (now - lastBlinkTime > 500) {
            blinkState = !blinkState;
            lastBlinkTime = now;
        }
    }

    // Mouse

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            for (CyberButton btn : cyberButtons) {
                if (btn.isMouseOver(mouseX, mouseY)) {
                    btn.action.run();
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void renderLabels(GuiGraphics g, int mx, int my) {}

    // ============================================================
    //  数字雨字符
    // ============================================================

    protected class RainChar {
        protected float x, y, speed;
        protected String charSet;
        protected char currentChar;
        protected final List<Character> trail = new ArrayList<>();
        protected int trailLength;
        protected long lastChangeTime;
        protected int changeInterval;
        protected static final String BINARY = "01";
        protected static final String TEXT = "EECORE";

        public RainChar() { reset(); }

        protected void reset() {
            x = random.nextInt(imageWidth - 20) + 10;
            y = -random.nextInt(40);
            speed = 0.5f + random.nextFloat() * 1.2f;
            charSet = random.nextBoolean() ? BINARY : TEXT;
            currentChar = charSet.charAt(random.nextInt(charSet.length()));
            trailLength = 3 + random.nextInt(5);
            trail.clear();
            for (int i = 0; i < trailLength; i++) {
                trail.add(charSet.charAt(random.nextInt(charSet.length())));
            }
            lastChangeTime = System.currentTimeMillis();
            changeInterval = 60 + random.nextInt(100);
        }

        public void update() {
            y += speed;
            long now = System.currentTimeMillis();
            if (now - lastChangeTime > changeInterval) {
                for (int i = trailLength - 1; i > 0; i--) {
                    trail.set(i, trail.get(i - 1));
                }
                trail.set(0, currentChar);
                currentChar = charSet.charAt(random.nextInt(charSet.length()));
                lastChangeTime = now;
            }
            if (y > 80) {
                reset();
                y = -random.nextInt(20);
            }
        }

        public void render(GuiGraphics g, Font font, int ox, int oy, int areaW, int areaH) {
            for (int i = 0; i < trailLength; i++) {
                float cy = y - i * (font.lineHeight + 2);
                if (cy < 0 || cy >= areaH) continue;
                char ch = trail.get(i);
                float norm = Math.max(0, Math.min(1, (oy + cy - oy) / (float) areaH));
                int color = interpolateColor(0xFF004400, 0xFF00AA00, norm);
                g.drawString(font, String.valueOf(ch), (int) (ox + x), (int) (oy + cy), color, false);
            }
            float curY = y - trailLength * (font.lineHeight + 2);
            if (curY >= 0 && curY < areaH) {
                float norm = Math.max(0, Math.min(1, (oy + curY - oy) / (float) areaH));
                int color = interpolateColor(0xFF00FF00, 0xFF88FF88, norm);
                g.drawString(font, String.valueOf(currentChar), (int) (ox + x), (int) (oy + curY), color, false);
            }
        }

        protected int interpolateColor(int c1, int c2, float f) {
            f = Math.max(0, Math.min(1, f));
            int a1 = (c1 >> 24) & 0xFF, r1 = (c1 >> 16) & 0xFF, g1 = (c1 >> 8) & 0xFF, b1 = c1 & 0xFF;
            int a2 = (c2 >> 24) & 0xFF, r2 = (c2 >> 16) & 0xFF, g2 = (c2 >> 8) & 0xFF, b2 = c2 & 0xFF;
            return ((int) (a1 + (a2 - a1) * f) << 24) |
                    ((int) (r1 + (r2 - r1) * f) << 16) |
                    ((int) (g1 + (g2 - g1) * f) << 8) |
                    (int) (b1 + (b2 - b1) * f);
        }
    }
}
