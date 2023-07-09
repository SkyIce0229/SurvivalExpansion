package tmmi.skyice.survivalexpansion.features;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;
import tmmi.skyice.survivalexpansion.SurvivalExpansionMod;
import tmmi.skyice.survivalexpansion.db.service.PlayerDataService;
import tmmi.skyice.survivalexpansion.db.table.PlayerData;
import tmmi.skyice.survivalexpansion.event.PlayerEvent;
import tmmi.skyice.survivalexpansion.config.HardModeConfig;
import tmmi.skyice.survivalexpansion.util.LogUtil;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;

public class HardModeHandle {

    public static HardModeConfig hardModeConfig;

    public static void initialize() {
        hardModeConfig = SurvivalExpansionMod.survivalExpansionToml.getHardModeConfig();
        registerEvent();
    }

    private static void registerEvent() {
        ServerPlayConnectionEvents.JOIN.register(HardModeHandle::onPlayJoin);
        PlayerEvent.DEAD.register(HardModeHandle::onDead);
    }

    private static void onPlayJoin(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server) {
        if (!hardModeConfig.isEnable()) {
            return;
        }
        String playerName = handler.player.getName().getString();
        //极限模式
        PlayerData playerData = PlayerDataService.getByNameOrDefault(playerName);
        LogUtil.debug("username:{}",playerData.getUsername());
        LogUtil.debug("time:{}",playerData.getLastRespawnSettlementTime());
        int respawnAvailable = playerData.getRespawnAvailable();
        if (respawnAvailable >= 0) {
            if (handler.player.interactionManager.getGameMode().equals(GameMode.SPECTATOR)) {
                handler.player.changeGameMode(GameMode.SURVIVAL);
            }
        } else {
            if (handler.player.interactionManager.getGameMode().equals(GameMode.SURVIVAL)) {
                handler.player.changeGameMode(GameMode.SPECTATOR);
            }
        }
    }

    private static void onDead(ServerPlayerEntity player) {
        if (!hardModeConfig.isEnable()) {
            return;
        }
        //死亡时调用
        //获取玩家名字
        String playerName = player.getName().getString();
        //获取玩家的data
        //除非没创建角色数据，不然不需要getByName直接orDefault
        PlayerData data = PlayerDataService.getByNameOrDefault(playerName);

        int respawnAvailable = data.getRespawnAvailable();
        if (respawnAvailable >= 0) {
            //如果可用复活次数大于等于0，就减少一次
            //这里要用copy，因为才copy后RespawnAvailable是空的，不能直接相加
            data.copy().setUsername(playerName).setRespawnAvailable(data.getRespawnAvailable() - 1).setLastRespawnSettlementTime(data.getLastRespawnSettlementTime()).insertOrUpdate();
        } else {
            long nextRespawnTime = data.getLastRespawnSettlementTime().getTime() + hardModeConfig.getRespawnAvailableCoolDown();
            String showTime = new Timestamp(nextRespawnTime).toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));
            //如果可用复活次数小于等于0，就移除玩家
            player.networkHandler.disconnect(Text.literal(Text.translatable("text.survival_expansion.dead_disconnect", showTime).getString()));

        }
    }
}
