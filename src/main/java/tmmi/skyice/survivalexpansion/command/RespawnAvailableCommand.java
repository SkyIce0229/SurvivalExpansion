package tmmi.skyice.survivalexpansion.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import tmmi.skyice.survivalexpansion.db.entity.SurvivalExpansionPlayerData;
import tmmi.skyice.survivalexpansion.db.service.SurvivalExpansionPlayerDataServiceImpl;
import tmmi.skyice.survivalexpansion.util.LogUtil;


import static net.minecraft.server.command.CommandManager.*;

public class RespawnAvailableCommand {
    public static LiteralArgumentBuilder<ServerCommandSource> RespawnAvailable() {
        return literal("respawnAvailable").executes(context -> {
            ServerPlayerEntity player = context.getSource().getPlayer();
            if (player != null) {
                return getRespawnAvailable(player);
            } else {
                context.getSource().sendFeedback(() -> Text.literal(Text.translatable("text.survival_expansion.only_player_error").getString()), false);
                return 0;
            }
        }).then(argument("player", EntityArgumentType.player()).requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2)).executes(context -> {
            ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "player");
            LogUtil.debug("ServerPlayerEntity player：{}", player);
            getRespawnAvailable(player);
            return 0;
        }));
    }

    private static int getRespawnAvailable(ServerPlayerEntity player) {
        String playerName = player.getName().getString();
        LogUtil.debug("玩家名：{}", playerName);
        LogUtil.debug("getRespawnAvailable获得的player：{}", player);
        SurvivalExpansionPlayerData data = SurvivalExpansionPlayerDataServiceImpl.getInstance().findByName(playerName);
        int respawnAvailable = data.getRespawnAvailable();

        Text message = Text.literal(Text.translatable("text.survival_expansion.respawn_available", respawnAvailable).getString());
        player.sendMessage(message, false);
        return 1;
    }
}
