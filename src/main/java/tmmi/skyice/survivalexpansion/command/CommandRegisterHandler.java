package tmmi.skyice.survivalexpansion.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import tmmi.skyice.survivalexpansion.features.HardModeHandle;

import static net.minecraft.server.command.CommandManager.*;

public class CommandRegisterHandler implements CommandRegistrationCallback {

    @Override
    public void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandManager.RegistrationEnvironment environment) {
        registerSurvivalExpansion(dispatcher);
    }

    public void registerSurvivalExpansion(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralArgumentBuilder<ServerCommandSource> mainCommand = literal("SurvivalExpansion")
                //将子命令注册到主命令
                //注册子命令Help
                .then(literal("help").executes(context -> {
                    //发送命令
                    context.getSource().sendFeedback(() -> Text.literal(Text.translatable("text.survival_expansion.help_command").getString()), false);
                    return 1;
                }));
        if (HardModeHandle.hardModeConfig.isEnable()) {
            mainCommand
                    .then(literal("respawnTime")
                            .executes(RespawnTimeCommandExecutor::respawnTime)
                            //上面的不带player目标的用法是给普通玩家用的，下面的带player目标的用法是给op及以上的玩家/控制台用的。
                            .then(argument("player", EntityArgumentType.player())
                                    .requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
                                    .executes(RespawnTimeCommandExecutor::respawnTimePlayer)))
                    .then(literal("respawnAvailable")
                            .executes(RespawnAvailableCommandExecutor::respawnAvailable)
                            .then(argument("player", EntityArgumentType.player())
                                    .requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
                                    .executes(RespawnAvailableCommandExecutor::respawnAvailablePlayer)))
                    .then(literal("dailyTasks")
                            .executes(DailyTaskCommandExecutor::dailyTask));

        }
        //将主命令注册到分发器
        dispatcher.register(mainCommand);
        // 构建主命令节点
        LiteralCommandNode<ServerCommandSource> mainCommandNode = mainCommand.build();
        // 注册别称命令 "se"，将其重定向到主命令 "Survivalexpansion"
        dispatcher.register(literal("se").redirect(mainCommandNode));
    }

}
