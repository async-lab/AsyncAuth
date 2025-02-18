package club.asynclab.asyncraft.asyncauth.gui;

import club.asynclab.asyncraft.asyncauth.gui.widget.LabeledEditBox;
import club.asynclab.asyncraft.asyncauth.network.NetworkHandler;
import club.asynclab.asyncraft.asyncauth.network.packet.ClientQuitPacket;
import club.asynclab.asyncraft.asyncauth.network.packet.LoginRequestPacket;
import com.mojang.blaze3d.vertex.PoseStack;
import lombok.Setter;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

@OnlyIn(Dist.CLIENT)
public class LoginScreen extends Screen {

    private static final Component TITLE = new TranslatableComponent("gui.asyncauth.title_login");
    private EditBox passwordField;
    private Button submitButton;

    @Setter
    private Component errorMessage = null;

    public LoginScreen() {
        super(TITLE);
    }

    @Override
    protected void init() {
        super.init();
        int centerX = this.width / 2;

        this.passwordField = new LabeledEditBox(this.font,
                centerX - 100,  // x坐标
                100,            // y坐标
                200, 20,        // 宽度/高度
                new TranslatableComponent("gui.asyncauth.password"),
                new TextComponent(""));

        passwordField.setFormatter((text, pos) ->
                new TextComponent("*".repeat(text.length())).getVisualOrderText());

        this.addRenderableWidget(passwordField);

        this.submitButton = new Button(
                centerX - 50,   // x坐标
                150,            // y坐标
                100, 20,        // 宽度/高度
                new TranslatableComponent("gui.asyncauth.submit_login"),
                this::onSubmitButtonPressed);

        Button exitButton = new Button(
                20,
                this.height - 40,
                100, 20,
                new TranslatableComponent("gui.asyncauth.exit"),
                (btn) -> NetworkHandler.INSTANCE.sendToServer(new ClientQuitPacket())
        );

        this.addRenderableWidget(submitButton);
        this.addRenderableWidget(exitButton);
    }

    private void onSubmitButtonPressed(Button button) {
        String input = passwordField.getValue();
        if (input.isEmpty()) {
            errorMessage = new TranslatableComponent("msg.asyncauth.login_empty_password");
            return;
        }
        login();
        /*if (login(input)) {
            this.minecraft.setScreen(null);
            this.minecraft.player.displayClientMessage(
                    new TranslatableComponent("msg.asyncauth.login_success"), false);
        } else {
            errorMessage = new TranslatableComponent("msg.asyncauth.login_wrong_password");
            passwordField.setValue("");
        }*/
    }

    private void login() {
        NetworkHandler.INSTANCE.sendToServer(new LoginRequestPacket(passwordField.getValue()));
    }

    @Override
    public void render(@NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(poseStack);
        drawCenteredString(poseStack, this.font, this.title,
                this.width / 2, 20, 0xFFFFFF);

        if (errorMessage != null) {
            int errorY = submitButton.y - 10;
            drawCenteredString(poseStack, font, errorMessage, width/2, errorY, 0xFF0000);
        }

        super.render(poseStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public void onClose() {
    }


    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 257 || keyCode == 335) { // 主键盘和小键盘的Enter
            this.onSubmitButtonPressed(submitButton);
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

}
