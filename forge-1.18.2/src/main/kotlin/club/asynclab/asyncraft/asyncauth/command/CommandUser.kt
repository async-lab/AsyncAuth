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
import net.minecraft.server.level.ServerPlayer


object CommandUser {
    fun getBuilder(): LiteralArgumentBuilder<CommandSourceStack> {
        return Commands.literal("changepassword").then(
            Commands.argument("old_password", StringArgumentType.string()).then(
                Commands.argument("new_password", StringArgumentType.string()).executes(::changePassword)
            )
        )
    }

    @Throws(CommandSyntaxException::class)
    private fun changePassword(ctx: CommandContext<CommandSourceStack>): Int {

        var player : ServerPlayer
        try {
            player = ctx.source.playerOrException
        } catch (e: CommandSyntaxException) {
            ctx.source.sendFailure(UtilComponent.getLiteralComponent("This command can only be executed by player."))
            return CommandStatus.FAILED.status
        }

        val source = ctx.source

        val oldPassword = StringArgumentType.getString(ctx, "old_password")
        val newPassword = StringArgumentType.getString(ctx, "new_password")
        val minLength = ModSetting.minLength.get()

        if (newPassword.length < minLength) {
            source.sendFailure(UtilComponent.getTranslatableComponent(Lang.Auth.TOO_SHORT))
            return 1
        }

        val managerAuth = ModContext.Server.MANAGER_AUTH
        val checkOld = managerAuth.login(player.name.string, oldPassword)
        if (checkOld != AuthStatus.SUCCESS) {
            source.sendFailure(UtilComponent.getTranslatableComponent(Lang.Auth.WRONG_OLD_PASSWORD))
            return CommandStatus.FAILED.status
        }

        val result = managerAuth.changePassword(player.name.string, newPassword)
        ctx.source.sendSuccess( UtilComponent.getTranslatableComponent(Lang.Auth.from(result)), false)
//        player.sendSystemMessage(UtilComponent.getTranslatableComponent(Lang.Commands.PASSWORD_CHANGED))

        return if (result == AuthStatus.SUCCESS) CommandStatus.SUCCESS.status else CommandStatus.FAILED.status
    }
}