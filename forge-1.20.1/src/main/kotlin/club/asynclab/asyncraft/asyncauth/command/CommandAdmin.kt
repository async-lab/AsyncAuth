package club.asynclab.asyncraft.asyncauth.command

import club.asynclab.asyncraft.asyncauth.common.enumeration.AuthStatus
import club.asynclab.asyncraft.asyncauth.common.enumeration.CommandStatus
import club.asynclab.asyncraft.asyncauth.common.enumeration.PermissionLevel
import club.asynclab.asyncraft.asyncauth.common.misc.Lang
import club.asynclab.asyncraft.asyncauth.misc.ModContext
import club.asynclab.asyncraft.asyncauth.misc.ModSetting
import club.asynclab.asyncraft.asyncauth.util.UtilComponent
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.arguments.EntityArgument


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
            ctx.source.sendFailure(UtilComponent.getTranslatableComponent(Lang.Auth.TOO_SHORT))
            return 1
        }

        val result = ModContext.Server.MANAGER_AUTH.changePassword(player.name.string, password)
        ctx.source.sendSuccess({ UtilComponent.getTranslatableComponent(Lang.Auth.from(result)) }, false)
        player.sendSystemMessage(UtilComponent.getTranslatableComponent(Lang.Commands.PASSWORD_CHANGED))

        return if (result == AuthStatus.SUCCESS) CommandStatus.SUCCESS.status else CommandStatus.FAILED.status
    }
}