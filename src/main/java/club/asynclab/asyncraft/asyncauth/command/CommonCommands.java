package club.asynclab.asyncraft.asyncauth.command;

import club.asynclab.asyncraft.asyncauth.AuthManager;
import club.asynclab.asyncraft.asyncauth.constant.ClientTextConstants;
import club.asynclab.asyncraft.asyncauth.util.ServerMessageUtils;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

import java.util.LinkedList;
import java.util.List;

public class CommonCommands {

    public static void registerCommands(LiteralArgumentBuilder<CommandSourceStack> main) {
        main.then(Commands.literal("changepassword")
                .requires(commandSourceStack -> true)
                        .then(Commands.argument("old_password", StringArgumentType.string())
                                .then(Commands.argument("new_password",StringArgumentType.string())
                                        .executes(CommonCommands::changePassword))));
    }

    /**
     * 更改自己的密码
     */
    private static int changePassword(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = ctx.getSource().getPlayer();
        if (player == null) return 1;
        String oldPassword = StringArgumentType.getString(ctx, "old_password");
        String newPassword = StringArgumentType.getString(ctx,"new_password");

        if (!AuthManager.verifyPasswordText(player,newPassword)) {
            return 1;
        }

        if (!AuthManager.checkPassword(player,oldPassword)) {
            ServerMessageUtils.sendTranslatableMessage(player, ClientTextConstants.OLD_PASSWORD_WRONG,new LinkedList<>());
            return 1;
        }

        if (AuthManager.changePassword(player,newPassword)) {
            ServerMessageUtils.sendTranslatableMessage(player,ClientTextConstants.CHANGE_PASSWORD_SUCCEED, List.of());
        }
        return 1;
    }

}
