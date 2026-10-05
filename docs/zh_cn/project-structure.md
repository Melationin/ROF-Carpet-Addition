# 项目结构

Java 源码位于 `src/main/java/com/carpet/rof/`，按功能域组织。功能实现与 `mixin/` 中对应的相对目录保持一致；规则设置和辅助类放在所属功能包内。Mixin 使用的访问接口统一放在 `mixinAccessor/`，不按功能域拆分。

```text
com/carpet/rof/
├── ROFCarpetServer.java          # Mod 入口与服务器生命周期
├── ROFEntry.java
├── ROFSettings.java              # 规则注册
├── ROFCommands.java              # 命令注册
├── ROFLoggers.java               # 日志注册预留入口
├── Docs.java                     # 文档生成
├── advancement/                 # 进度规则与触发限制
├── annotation/                  # 主项目使用的注解
├── carpet/                      # 共享规则基类、校验器和 Carpet 功能
│   └── fakePlayerTick/
├── command/                     # 通用命令
│   ├── extraChunkData/
│   └── loadedChunkFinder/       # 已加载区块查询命令与查询状态
├── debug/                       # 调试命令、计时器与统计
├── entity/                      # 实体功能
│   ├── ISE/                     # 实体 ID 命令与规则
│   ├── drops/
│   ├── enderPearl/
│   ├── merge/
│   ├── mobAi/
│   │   ├── betterNoAi/
│   │   └── piglinRules/
│   └── oec/
│       └── lithium/
├── event/                       # 生命周期事件
├── logger/
│   └── packetLogger/            # 数据包日志及其命令
├── mixinAccessor/               # 集中存放 Mixin 访问接口
├── packetRules/                 # 数据包规则
├── utils/                       # 通用工具
│   └── singleTaskWorker/
├── world/                       # 世界功能
│   ├── blockChange/
│   ├── chunkTick/
│   │   ├── async/               # 异步执行与区块快照
│   │   │   ├── randomTick/
│   │   │   └── spawner/
│   │   └── spawnStatistic/
│   ├── explosion/
│   ├── extraWorldData/
│   │   └── extraChunkDatas/
│   └── portal/
└── mixin/                       # 注入代码，沿用对应功能域的目录
```

例如，`entity/oec/` 的实现由 `mixin/entity/oec/` 注入；`world/chunkTick/async/` 的实现由 `mixin/world/chunkTick/async/` 注入。`EntityAccessor`、`ServerLevelAccessor`、`LevelChunkAccessor` 等接口，以及 `OecEntityAccess`、`OecStorageHolder` 等由 Mixin 实现的访问接口，都集中放在 `mixinAccessor/`。新增同类接口也放在此目录。

原目录迁移关系：

| 原目录或类 | 新位置 |
| --- | --- |
| `rules/advancement/` | `advancement/` |
| `rules/drops/`、`enderPearl/`、`merge/`、`oec/` | `entity/` 下的同名功能目录 |
| `rules/mobAi/` | `entity/mobAi/` |
| `rules/betterNoAi/`、`rules/piglinRules/` | `entity/mobAi/` 下的同名功能目录 |
| `rules/explosion/`、`rules/portal/` | `world/` 下的同名功能目录 |
| `rules/asyncWorldgen/` | `world/chunkTick/async/` |
| `rules/spawnStatistic/` | `world/chunkTick/spawnStatistic/` |
| `rules/fakePlayerTick/` | `carpet/fakePlayerTick/` |
| `rules/packerRules/` | `packetRules/` |
| `rules/BaseSetting.java`、`rules/ListSettingValidator.java` | `carpet/` |
| `extraWorldData/` | `world/extraWorldData/` |
| `rules/extraChunkDatas/` | `world/extraWorldData/extraChunkDatas/` |
| `extraWorldData/asyncWorldgen/DelayedChunkSnapshot.java` | `world/chunkTick/async/` |
| `extraWorldData/extraChunkDatas/ChunkLoadedFinder.java` | `command/loadedChunkFinder/` |
| `commands/` | 通用命令迁入 `command/`，调试命令迁入 `debug/` |
| `commands/LoadedChunkFinderCommand.java` | `command/loadedChunkFinder/` |
| `commands/ISECommand.java` | `entity/ISE/` |
| `commands/loggerCommand/PacketLoggerCommand.java` | `logger/packetLogger/` |
| `mixinAccessor/` | 保留原目录，集中存放 Mixin 访问接口 |
| 各功能包内由 Mixin 实现的 `*Access`、`*Accessor`、`OecStorageHolder` 接口 | `mixinAccessor/` |

规则和命令仍通过 `@ROFRule`、`@ROFCommand` 注册。`processor/` 根据类的实际全限定名生成 `com.carpet.rof.generated` 下的注册列表，移动类后通过编译重新生成，无需手写列表或修改构建产物。处理器也支持 `@ROFLogger`；目前 `ROFLoggers` 的注册实现尚未启用，数据包日志由其命令直接控制。

Mixin 注册在 `src/main/resources/RofCarpetAddition.mixins.json`，访问拓宽配置在 `src/main/resources/CarpetRofAddition.aw`。移动实现类时同步更新包声明、普通导入和静态导入，并保留 Stonecutter 条件分支中的引用。仅当 Mixin 类自身移动时才修改其注册路径。
