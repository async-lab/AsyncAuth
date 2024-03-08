package club.asyncraft.asyncauth.common.util;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class CommandTabSupport {

    public static final String[] LOGIN_COMMANDS = new String[]{"/l", "/login"};

    public static final String[] REGISTER_COMMANDS = new String[]{"/reg", "/register"};

    public static void initCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
        for (String loginCommand : LOGIN_COMMANDS) {
            dispatcher.register(
                    Commands.literal(loginCommand.substring(1))
                            .requires((commandSourceStack) -> true)
                            .then(Commands.argument("password", StringArgumentType.word()))
            );
        }
        for (String registerCommand : REGISTER_COMMANDS) {
            dispatcher.register(
                    Commands.literal(registerCommand.substring(1))
                            .requires(commandSourceStack -> true)
                            .then(Commands.argument("password", StringArgumentType.word())
                                    .then(Commands.argument("confirm-password", StringArgumentType.word())))

            );
        }
    }

}
