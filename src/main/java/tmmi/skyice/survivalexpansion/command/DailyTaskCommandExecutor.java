package tmmi.skyice.survivalexpansion.command;


import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import tmmi.skyice.survivalexpansion.screen.factory.DailyTasksScreen;

public class DailyTaskCommandExecutor {
    public static final DailyTasksScreen DAILY_TASKS_SCREEN= new DailyTasksScreen();
    public static int dailyTask(CommandContext<ServerCommandSource> context) {
        if (context.getSource().isExecutedByPlayer()){
            ServerPlayerEntity player = context.getSource().getPlayer();
            player.openHandledScreen(DAILY_TASKS_SCREEN);
        }else {
            context.getSource().sendFeedback(() -> Text.literal(Text.translatable("text.survival_expansion.only_player_error").getString()), false);
        }
        return 1;
    }




}
