package tmmi.skyice.survivalexpansion.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import tmmi.skyice.survivalexpansion.SurvivalExpansionMod;


import static net.minecraft.server.command.CommandManager.*;

public class CommandManager {
    public static void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralArgumentBuilder<ServerCommandSource> mainCommand = literal("SurvivalExpansion");
        //注册子命令help
        LiteralArgumentBuilder<ServerCommandSource> helpCommand = literal("help").executes(context -> {
            //发送命令
            helpMessage(context.getSource());
            return 1;
        });
        //将子命令注册到主命令
        mainCommand.then(helpCommand);
        if (SurvivalExpansionMod.generalConfig.isHardMode()) {
            mainCommand.then(RespawnTimeCommand.RespawnTime());
            mainCommand.then(RespawnAvailableCommand.RespawnAvailable());
        }
        //将主命令注册到分发器
        dispatcher.register(mainCommand);
        // 构建主命令节点
        LiteralCommandNode<ServerCommandSource> mainCommandNode = mainCommand.build();
        // 注册别称命令 "se"，将其重定向到主命令 "Survivalexpansion"
        dispatcher.register(literal("se").redirect(mainCommandNode));
    }
    private static void helpMessage(ServerCommandSource source) {
        source.sendFeedback(() -> Text.literal(Text.translatable("text.survival_expansion.help_command").getString()),false);
    }
}
