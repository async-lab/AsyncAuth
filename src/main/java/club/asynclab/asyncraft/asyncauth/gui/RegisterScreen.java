package club.asynclab.asyncraft.asyncauth.gui;

import club.asynclab.asyncraft.asyncauth.ModConfig;
import club.asynclab.asyncraft.asyncauth.gui.widget.LabeledEditBox;
import club.asynclab.asyncraft.asyncauth.network.NetworkHandler;
import club.asynclab.asyncraft.asyncauth.network.packet.ClientQuitPacket;
import club.asynclab.asyncraft.asyncauth.network.packet.LoginRequestPacket;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import lombok.Setter;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class RegisterScreen extends Screen {

    private static final Component TITLE = Component.translatable("gui.asyncauth.title_register");
    private EditBox passwordField;
    private EditBox confirmPasswordField;
    private Button submitButton;

    @Setter
    private Component errorMessage = null;

    public RegisterScreen() {
        super(TITLE);
    }

    @Override
    protected void init() {
        super.init();
        int centerX = this.width / 2;

        this.passwordField = new LabeledEditBox(this.font,
                centerX - 100,  // x坐标
                80,            // y坐标
                200, 20,        // 宽度/高度
                Component.translatable("gui.asyncauth.password"),
                Component.literal(""));

        passwordField.setFormatter((text, pos) ->
                Component.literal("*".repeat(text.length())).getVisualOrderText());

        this.confirmPasswordField = new LabeledEditBox(this.font,
                centerX - 100,
                120,
                200,20,
                Component.translatable("gui.asyncauth.confirm_password"),
                Component.literal(""));

        confirmPasswordField.setFormatter((text, pos) ->
                Component.literal("*".repeat(text.length())).getVisualOrderText());

        this.submitButton = Button.builder(Component.translatable("gui.asyncauth.submit_register"),this::onSubmitButtonPressed)
                .bounds(centerX - 50,   // x坐标
                        170,            // y坐标
                        100, 20)
                .build();

        Button exitButton = Button.builder(Component.translatable("gui.asyncauth.exit"),(btn) -> NetworkHandler.INSTANCE.sendToServer(new ClientQuitPacket()))
                .bounds(20,
                        this.height - 40,
                        100, 20)
                .build();

        this.addRenderableWidget(passwordField);
        this.addRenderableWidget(confirmPasswordField);
        this.addRenderableWidget(submitButton);
        this.addRenderableWidget(exitButton);
    }

    private void onSubmitButtonPressed(Button button) {
        String password = passwordField.getValue();
        if (password.isEmpty()) {
            errorMessage = Component.translatable("msg.asyncauth.login_empty_password");
            return;
        }
        String confirmPassword = confirmPasswordField.getValue();
        if (!confirmPassword.equals(password)) {
            errorMessage = Component.translatable("gui.asyncauth.password_inconsistency");
            return;
        }
        login();
    }

    private void login() {
        NetworkHandler.INSTANCE.sendToServer(new LoginRequestPacket(passwordField.getValue()));
    }

    @Override
    public void render(GuiGraphics poseStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(poseStack);
        poseStack.drawCenteredString( this.font, this.title,
                this.width / 2, 20, 0xFFFFFF);

        if (errorMessage != null) {
            int errorY = submitButton.getY() - 10;
            poseStack.drawCenteredString( font, errorMessage, width/2, errorY, 0xFF0000);
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
        if (keyCode == InputConstants.KEY_ESCAPE) {
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    public void displayPasswordTooShortMessage(String length) {
        errorMessage = Component.literal(Component.translatable("gui.asyncauth.password_too_short").getString().replace("%min%",length));
    }
}
