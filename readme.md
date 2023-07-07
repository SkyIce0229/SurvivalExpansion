<p align="center">
    <a href="readme.md">English</a> |
    <a href="readmezh.md">简体中文</a> 
</p>

# SurvivalExpansion
This is a fabric mod designed to provide additional challenges and gameplay extensions to Minecraft vanilla survival or redstone-powered server within the official boundaries.

Feel free to use it in your own projects!


## Requirements
This mod requires Fabric API,[toml-config](https://github.com/Fndream/toml-config)

## Getting Started
Setup is very simple. Drop the mod into your mods folder. Starting a server will generate a config. The mod will not run until you edit the config.

## IMPORTANT NOTES
toml-configLib is built into the mod and is automatically detected and downloaded during the initialization phase when the server is started. Do not use this mod if you do not trust it.

This mod is known to have issues with: The display hasn't been found yet.

## Gameplay
When the player enters the server for the first time will receive 3 respawn times, when the player dies more than 3 times will be kicked out of the server, re-entering will become a spectator mode, and wait to recover the respawn times.

Respawn restores every 12 hours up to a maximum of 3 times.

And so on......

Other gameplay please wait for me to update, stay tuned!
## Config
```toml

[database]
# Mysql Server ip.
# 数据库服务器ip地址。
ip = "localhost"

# Mysql Server port
# 数据库服务器端口。
port = 3306

# Mysql Server username.
# 数据库服务器用户名。
username = "root"

# Mysql Server password.
# 数据库服务器密码。
password = "882378748"

# Mysql Server database name,If don't understand, please don't change it.
# 数据库服务器数据库名称,如果你不知道这是什么,请不要修改它。
database-name = "syncserver"


[general-config]
# Hard mode, if true, the player will be kicked out of the server after death 3 times.Default:true.
# 硬核模式,如果为true,玩家死亡3次后将被踢出服务器。默认值：true
hard-mode = true


[setting-config]
# Respawn available cool down time, unit: millisecond. Default:12 hours.
# 复活可用冷却时间，单位：毫秒。
-respawn-available-cool-down = 43200000

# Respawn available limit.Default:3.
# 复活可用次数。 默认 3 次。
-respawn-available-limit = 3


```

## Commands
```/survivalexpansion help ```                      ---open help Menu

```/survivalexpansion respawnAvailable```           ---Check remaining respawn times

```/survivalexpansion respawnAvailable <player>```  ---Check remaining respawn times for <player>

```/survivalexpansion respawnTime```                ---Check respawn time recovery

```/survivalexpansion respawnTime <player>```       ---Check respawn time recovery for <player>

```/survivalexpansion dailyTasks```       ---Open your daily tasks Gui

## Issues And Requests
If you find any bugs or glitches, be sure to make a bug report under issues and I will do my best to fix it! Just as well if you have a cool idea for something that I should add, let me know and I will consider adding it!
