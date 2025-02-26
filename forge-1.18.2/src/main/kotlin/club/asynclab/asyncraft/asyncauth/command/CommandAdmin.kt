package club.asynclab.asyncraft.asyncauth.command

import club.asynclab.asyncraft.asyncauth.common.enumeration.CommandStatus
import club.asynclab.asyncraft.asyncauth.common.enumeration.PermissionLevel
import club.asynclab.asyncraft.asyncauth.misc.ModContext
import club.asynclab.asyncraft.asyncauth.misc.ModSetting
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.arguments.EntityArgument
import net.minecraft.network.chat.TextComponent

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
        val sender = ctx.source.entity
        val minLength = ModSetting.minLength.get()

        if (password.length < minLength) {
            sender?.sendMessage(TextComponent("Password must be at least $minLength characters."), sender.uuid)
            return 1
        }

        val result = ModContext.Server.MANAGER_AUTH.changePassword(player.name.string, password) ?: false

        sender?.sendMessage(TextComponent(if (result) "Succeed" else "Player not registered"), sender.uuid)

        if (!result) return CommandStatus.SUCCESS.status

        player.sendMessage(TextComponent("Password was changed by the administrator"), player.uuid)
        return CommandStatus.SUCCESS.status
    }
}