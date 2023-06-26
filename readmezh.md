<p align="center">
    <a href="readme.md">English</a> |
    <a href="readmezh.md">简体中文</a> 
</p>

# SurvivalExpansion(生存扩展)

这是一个Fabric mod，旨在为Minecraft原版生存或红石驱动的服务器提供额外的挑战和游戏扩展，它不会改变原版的游戏内容。纯净客户端也能进服。

欢迎在您自己的项目中使用它！

## 前置和要求

此mod需要fabricAPI和[toml-config](https://github.com/Fndream/toml-config)。

## 入门

安装非常简单。将mod放入所有要同步的服务器上的mods文件夹中。启动服务器将生成配置。在编辑配置之前，服务器不会运行。

## 注意事项

toml-configLib内置于mod中，在服务器启动时的初始化阶段自动检测和下载。如果你不信任这个库，请不要使用这个mod。

这个mod已知有以下问题:显示尚未找到。

## 玩法
当玩家第一次进入服务器时将获得3次重生，当玩家死亡超过3次时将被踢出服务器，再次进入将成为旁观者模式，并等待恢复重生次数。

重生每12小时恢复一次，最多3次。

等等......

其他玩法正在开发中，敬请期待。

## 配置文件
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

## 指令
```/survivalexpansion help ```                      ---打开帮助菜单

```/survivalexpansion respawnAvailable```           ---查看剩余复活次数

```/survivalexpansion respawnAvailable <player>``` ---查看<player>剩余复活次数,限管理员/控制台使用

```/survivalexpansion respawnTime```               ---查看下一次恢复体力的时间

```/survivalexpansion respawnTime <player>```      ---查看<player>下一次恢复体力的时间

## 问题和请求

如果你发现任何错误或小故障，一定要在问题下做一个错误报告，我会尽力解决它！同样，如果你对我应该添加的内容有一个很酷的想法，请告诉我，我会考虑添加它！

作者是国人，中文交流更方便！谢谢啦~

