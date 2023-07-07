package tmmi.skyice.survivalexpansion.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import net.minecraft.text.Text;
import tmmi.skyice.survivalexpansion.db.service.PlayerDataService;
import tmmi.skyice.survivalexpansion.db.table.PlayerData;
import tmmi.skyice.survivalexpansion.util.LogUtil;
import tmmi.skyice.survivalexpansion.features.HardModeHandle;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;

public class RespawnTimeCommandExecutor {

    public static int respawnTime(CommandContext<ServerCommandSource> context) {
        if (context.getSource().isExecutedByPlayer()){
            ServerPlayerEntity player = context.getSource().getPlayer();
            Text message = Text.literal(Text.translatable("text.survival_expansion.dead_time", getRespawnTime(player)).getString());
            player.sendMessage(message, false);
        } else {
            context.getSource().sendFeedback(() -> Text.literal(Text.translatable("text.survival_expansion.only_player_error").getString()), false);
        }
        return 1;
    }

    public static int respawnTimePlayer(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "player");
        Text message = Text.literal(Text.translatable("text.survival_expansion.respawn_available", getRespawnTime(player)).getString());
        player.sendMessage(message, false);
        return 0;
    }

    private static String getRespawnTime(ServerPlayerEntity player) {
        String playerName = player.getName().getString();
        LogUtil.debug("玩家名：{}", playerName);
        LogUtil.debug("getRespawnTime获得的player：{}", player);
        PlayerData data = PlayerDataService.getByName(playerName);
        long nextRespawnTime = data.getLastRespawnSettlementTime().getTime() + HardModeHandle.hardModeConfig.getRespawnAvailableCoolDown();

        return new Timestamp(nextRespawnTime).toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));
    }
}
