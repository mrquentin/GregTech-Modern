package com.gregtechceu.gtceu.integration.top.element;

import mcjty.theoneprobe.api.IProgressStyle;
import mcjty.theoneprobe.rendering.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class ProgressRender {

    public static void render(GuiGraphics guiGraphics, IProgressStyle style, int x, int y, int width, int height, float progress, Component text) {
        RenderHelper.drawThickBeveledBox(guiGraphics, x, y, x + width, y + height, 1, style.getBorderColor(), style.getBorderColor(), style.getBackgroundColor());
        if (progress > 0.0F) {
            var dx = (int) Math.min(progress * (width - 2), width - 2);
            if (style.getFilledColor() == style.getAlternatefilledColor()) {
                if (dx > 0) {
                    RenderHelper.drawThickBeveledBox(guiGraphics, x + 1, y + 1, x + dx + 1, y + height - 1, 1, style.getFilledColor(), style.getFilledColor(), style.getFilledColor());
                }
            } else {
                for (int xx = 0; xx < x + dx; xx++) {
                    int color = (xx & 1) == 0 ? style.getFilledColor() : style.getAlternatefilledColor();
                    RenderHelper.drawVerticalLine(guiGraphics, xx, y + 1, y + height - 1, color);
                }
            }
        }
        renderText(guiGraphics, x, y, width, text, style);
    }

    public static void renderText(GuiGraphics graphics, int x, int y, int w, Component text, IProgressStyle style) {
        if (style.isShowText()) {
            Minecraft mc = Minecraft.getInstance();
            Font render = mc.font;
            int textWidth = render.width(text.getVisualOrderText());
            switch (style.getAlignment()) {
                case ALIGN_BOTTOMRIGHT -> RenderHelper.renderText(mc, graphics, (x + w - 3) - textWidth, y + 2, text);
                case ALIGN_CENTER -> RenderHelper.renderText(mc, graphics, (x + (w / 2)) - (textWidth / 2), y + 2, text);
                case ALIGN_TOPLEFT -> RenderHelper.renderText(mc, graphics, x + 3, y + 2, text);
            }
        }
    }

}
