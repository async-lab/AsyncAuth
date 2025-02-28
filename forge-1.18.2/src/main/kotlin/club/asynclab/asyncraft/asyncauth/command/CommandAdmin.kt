package club.asynclab.asyncraft.asyncauth.command

import club.asynclab.asyncraft.asyncauth.common.enumeration.AuthStatus
import club.asynclab.asyncraft.asyncauth.common.enumeration.CommandStatus
import club.asynclab.asyncraft.asyncauth.common.enumeration.PermissionLevel
import club.asynclab.asyncraft.asyncauth.common.misc.Lang
import club.asynclab.asyncraft.asyncauth.misc.ModContext
import club.asynclab.asyncraft.asyncauth.misc.ModSetting
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import net.minecraft.Util
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.arguments.EntityArgument
import net.minecraft.network.chat.TranslatableComponent

object CommandAdmin {
    fun getBuilder(): LiteralArgumentBuilder<CommandSourceStack> {
        return Commands.literal("admin").then(
            Commands.literal("changepassword").then(
                Commands.argument("player", EntityArgument.player()).then(
                    Commands.argument("password", StringArgumentType.string()).executes(::changePassword)
                )
            )
        ).requires { it.hasPermission(PermissionLevel.ADMIN.level) }
    }

    @Throws(CommandSyntaxException::class)
    private fun changePassword(ctx: CommandContext<CommandSourceStack>): Int {
        val player = EntityArgument.getPlayer(ctx, "player")
        val password = StringArgumentType.getString(ctx, "password")
        val minLength = ModSetting.minLength.get()

        if (password.length < minLength) {
            ctx.source.sendFailure(TranslatableComponent(Lang.Auth.TOO_SHORT))
            return 1
        }

        val result = ModContext.Server.MANAGER_AUTH.changePassword(player.name.string, password)
        ctx.source.sendSuccess(TranslatableComponent(Lang.Auth.from(result)), false)
        player.sendMessage(TranslatableComponent(Lang.Commands.PASSWORD_CHANGED), Util.NIL_UUID)

        return if (result == AuthStatus.SUCCESS) CommandStatus.SUCCESS.status else CommandStatus.FAILED.status
    }
}