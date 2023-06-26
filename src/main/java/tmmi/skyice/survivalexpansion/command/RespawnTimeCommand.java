package tmmi.skyice.survivalexpansion.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

import net.minecraft.text.Text;
import tmmi.skyice.survivalexpansion.SurvivalExpansionMod;
import tmmi.skyice.survivalexpansion.db.entity.SurvivalExpansionPlayerData;
import tmmi.skyice.survivalexpansion.db.service.SurvivalExpansionPlayerDataServiceImpl;
import tmmi.skyice.survivalexpansion.util.LogUtil;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;


import static net.minecraft.server.command.CommandManager.*;

public class RespawnTimeCommand {

    public static LiteralArgumentBuilder<ServerCommandSource> RespawnTime() {
        return literal("respawnTime").executes(context -> {
                    ServerPlayerEntity player = context.getSource().getPlayer();
                    if (player != null) {
                        return getRespawnTime(player);
                    } else {
                        context.getSource().sendFeedback(() -> Text.literal(Text.translatable("text.survival_expansion.only_player_error").getString()), false);
                        return 0;
                    }
                })
                //上面的不带player目标的用法是给普通玩家用的，下面的带player目标的用法是给op及以上的玩家/控制台用的。
                .then(argument("player", EntityArgumentType.player()).requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2)).executes(context -> {
                    //获取玩家数据并执行命令
                    ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "player");
                    LogUtil.debug("register获取的player:{}", player);
                    getRespawnTime(player);
                    return 0;
                }));
    }

    private static int getRespawnTime(ServerPlayerEntity player) {
        String playerName = player.getName().getString();
        LogUtil.debug("玩家名：{}", playerName);
        LogUtil.debug("getRespawnTime获得的player：{}", player);
        SurvivalExpansionPlayerData data = SurvivalExpansionPlayerDataServiceImpl.getInstance().findByName(playerName);
        long nextRespawnTime = data.getLastRespawnSettlementTime().getTime() + SurvivalExpansionMod.settingConfig.getRespawnAvailableCoolDown();
        String showTime = new Timestamp(nextRespawnTime).toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));

        Text message = Text.literal(Text.translatable("text.survival_expansion.dead_time", showTime).getString());
        player.sendMessage(message, false);
        return 1;
    }
}
