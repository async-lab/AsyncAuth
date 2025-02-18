package club.asynclab.asyncraft.asyncauth.gui.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

/**
 * 带标签的输入框
 */
public class LabeledEditBox extends EditBox {

    private final Component labelText;
    private final Font font;

    public LabeledEditBox(Font font, int x, int y, int width, int height,
                          Component label, Component defaultValue) {
        super(font, x, y + font.lineHeight + 2, width, height, defaultValue);
        this.labelText = label;
        this.font = font;
    }

    @Override
    public void renderWidget(GuiGraphics pose, int mouseX, int mouseY, float delta) {
        pose.drawString(font,labelText,
                this.getX(),
                this.getY() - font.lineHeight - 2,
                0xFFFFFF);

        super.renderWidget(pose, mouseX, mouseY, delta);
    }


}
