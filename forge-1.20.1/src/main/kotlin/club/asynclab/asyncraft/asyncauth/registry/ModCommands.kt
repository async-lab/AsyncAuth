package club.asynclab.asyncraft.asyncauth.registry

import club.asynclab.asyncraft.asyncauth.command.CommandAdmin
import net.minecraft.commands.Commands
import net.minecraftforge.event.RegisterCommandsEvent

object ModCommands {
    fun onRegisterCommands(event: RegisterCommandsEvent) {
        event.dispatcher.apply {
            val main = register(Commands.literal("asyncauth").then(CommandAdmin.getBuilder()))
            register(Commands.literal("aa").redirect(main))
        }
    }
}