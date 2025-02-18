package club.asynclab.asyncraft.asyncauth.gui;

import club.asynclab.asyncraft.asyncauth.gui.widget.LabeledEditBox;
import club.asynclab.asyncraft.asyncauth.network.NetworkHandler;
import club.asynclab.asyncraft.asyncauth.network.packet.ClientQuitPacket;
import club.asynclab.asyncraft.asyncauth.network.packet.LoginRequestPacket;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class LoginScreen extends Screen {

    private static final Component TITLE = Component.translatable("gui.asyncauth.title_login");
    private EditBox passwordField;
    private Button submitButton;

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
                Component.translatable("gui.asyncauth.password"),
                Component.literal(""));

        passwordField.setFormatter((text, pos) ->
                Component.literal("*".repeat(text.length())).getVisualOrderText());

        this.addRenderableWidget(passwordField);
        this.submitButton = Button.builder(Component.translatable("gui.asyncauth.submit_login"), this::onSubmitButtonPressed)
                .bounds(centerX - 50,150,100,20)
                .build();


        Button exitButton = Button.builder(Component.translatable("gui.asyncauth.exit"),btn -> NetworkHandler.INSTANCE.sendToServer(new ClientQuitPacket()))
                .bounds(20,this.height - 40,100,20)
                .build();

        this.addRenderableWidget(submitButton);
        this.addRenderableWidget(exitButton);
    }

    private void onSubmitButtonPressed(Button button) {
        String input = passwordField.getValue();
        if (input.isEmpty()) {
            errorMessage = Component.translatable("msg.asyncauth.login_empty_password");
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
        poseStack.drawCenteredString(this.font, this.title,
                this.width / 2, 20, 0xFFFFFF);

        if (errorMessage != null) {
            int errorY = submitButton.getY() - 10;
            poseStack.drawCenteredString(font, errorMessage, width/2, errorY, 0xFF0000);
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

    public void displayWrongPasswordInfo() {
        errorMessage = Component.translatable("msg.asyncauth.login_wrong_password");
    }

}
