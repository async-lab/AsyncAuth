package club.asynclab.asyncraft.asyncauth.command;

import club.asynclab.asyncraft.asyncauth.AsyncAuth;
import club.asynclab.asyncraft.asyncauth.AuthManager;
import club.asynclab.asyncraft.asyncauth.ModConfig;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

import java.awt.*;

public class AdminCommands {

    public static LiteralArgumentBuilder<CommandSourceStack> adminCommand() {
        return Commands.literal("admin")
                .requires(commandSourceStack -> commandSourceStack.hasPermission(4))
                .then(Commands.literal("changepassword")
                        .then(Commands.argument("player", EntityArgument.player())
                                .then(Commands.argument("password", StringArgumentType.string())
                                .executes(AdminCommands::changePassword)
                        ))
                );
    }

    private static int changePassword(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(ctx, "player");
        String password = StringArgumentType.getString(ctx, "password");

        Entity sender = ctx.getSource().getEntity();
        int minLength = ModConfig.minLength.get();
        if (password.length() < minLength) {
            if (sender != null)
                sender.sendSystemMessage(Component.literal("Password must be at least " + minLength + " characters."));
            return 1;
        }

        boolean result = AuthManager.changePassword(player,password);

        if (sender != null)
            sender.sendSystemMessage(Component.literal(result ? "Succeed" : "Player not registered"));

        if (result) {
            player.sendSystemMessage(Component.literal("Password was changed by the administrator"));
            AsyncAuth.LOGGER.info("{} 's password was changed by administrator", player.getName().getString());
        }
        return 1;
    }

}
