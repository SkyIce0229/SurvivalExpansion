package tmmi.skyice.survivalexpansion;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;
import tmmi.skyice.survivalexpansion.command.CommandManager;
import tmmi.skyice.survivalexpansion.config.DatabaseConfig;
import tmmi.skyice.survivalexpansion.config.GeneralConfig;
import tmmi.skyice.survivalexpansion.config.SettingConfig;
import tmmi.skyice.survivalexpansion.config.SurvivalExpansionToml;
import tmmi.skyice.survivalexpansion.db.entity.SurvivalExpansionPlayerData;
import tmmi.skyice.survivalexpansion.db.service.SurvivalExpansionPlayerDataServiceImpl;
import tmmi.skyice.survivalexpansion.event.PlayerEvent;
import tmmi.skyice.survivalexpansion.init.Init;


import java.sql.*;
import java.time.format.DateTimeFormatter;



public class SurvivalExpansionMod implements DedicatedServerModInitializer, ServerPlayConnectionEvents.Join, PlayerEvent.Dead {
    public static final String MOD_ID = "survivalexpansion";
    public static String version;
    public static SurvivalExpansionToml survivalExpansionConfig;
    public static GeneralConfig generalConfig;
    public static DatabaseConfig databaseConfig;
    public static SettingConfig settingConfig;

    /**
     * Runs the mod initializer.
     */
    @Override
    public void onInitializeServer() {
        Init.init();
        if (generalConfig.isHardMode()){
            registerEvent();
            registerCommand();
        }
    }
    private void registerEvent() {
        ServerPlayConnectionEvents.JOIN.register(this);
        PlayerEvent.DEAD.register(this);
    }
    private void registerCommand() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            CommandManager.registerCommand(dispatcher);
        });
    }

    @Override
    public void onPlayReady(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server) {
        SurvivalExpansionPlayerData data = SurvivalExpansionPlayerDataServiceImpl.getInstance().findByName(handler.player.getName().getString());
        if (data == null) {
            data = new SurvivalExpansionPlayerData()
                    .setName(handler.player.getName().getString())
                    .setRespawnAvailable(settingConfig.getRespawnAvailableLimit())
                    .setLastRespawnSettlementTime(new Timestamp(System.currentTimeMillis()));
            SurvivalExpansionPlayerDataServiceImpl.getInstance().save(data);
        }
        int respawnAvailable = data.getRespawnAvailable();
        if (respawnAvailable > 0) {
            if (handler.player.interactionManager.getGameMode().equals(GameMode.SPECTATOR)) {
                handler.player.changeGameMode(GameMode.SURVIVAL);
            }
        } else {
            if (handler.player.interactionManager.getGameMode().equals(GameMode.SURVIVAL)) {
                handler.player.changeGameMode(GameMode.SPECTATOR);
            }
        }
    }
    @Override
    public void onDead(ServerPlayerEntity player) {
        //死亡时调用
        //获取玩家名字
        String playerName = player.getName().getString();
        //获取玩家的data
        SurvivalExpansionPlayerData data = SurvivalExpansionPlayerDataServiceImpl.getInstance().findByName(playerName);
        int respawnAvailable = data.getRespawnAvailable();
        if (respawnAvailable > 0) {
            //如果可用复活次数大于0，就减少一次
            data.copy().setRespawnAvailable(respawnAvailable - 1).setLastRespawnSettlementTime(data.getLastRespawnSettlementTime()).updateById();
        } else {
            long nextRespawnTime = data.getLastRespawnSettlementTime().getTime() + SurvivalExpansionMod.settingConfig.getRespawnAvailableCoolDown();
            String showTime = new Timestamp(nextRespawnTime).toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"));
            //如果可用复活次数小于等于0，就移除玩家
            player.networkHandler.disconnect(Text.literal(Text.translatable("text.survival_expansion.dead_disconnect",showTime).getString()));

        }
    }
}
