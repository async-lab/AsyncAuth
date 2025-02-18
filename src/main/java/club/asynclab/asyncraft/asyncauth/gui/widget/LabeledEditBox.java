package club.asynclab.asyncraft.asyncauth.gui.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
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
    public void renderButton(@NotNull PoseStack pose, int mouseX, int mouseY, float delta) {
        font.draw(pose, labelText,
                this.x,
                this.y - font.lineHeight - 2,
                0xFFFFFF); // 白色

        super.renderButton(pose, mouseX, mouseY, delta);
    }

}
