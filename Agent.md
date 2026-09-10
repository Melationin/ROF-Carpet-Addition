# 项目协作说明

## 项目与源码

- 本项目是面向生电服务器的 Fabric Carpet 扩展，主要通过规则和 Mixin 修改原版逻辑、减少卡顿；依赖 Carpet、Fabric API 和 Lithium。
- Minecraft 及相关 Mod 的源码都在 `F:\source`。确认原版行为、方法签名、Mixin 注入点及 Mod 兼容性时，优先查阅这里与目标版本匹配的源码。
- 主要代码在 `src/main/java/com/carpet/rof/`：`rules/` 为规则实现，`mixin/` 为注入，`extraWorldData/` 为世界与区块附加数据，`accessor/` 为访问接口，`utils/` 为工具与执行器。
- Mixin 注册文件为 `src/main/resources/RofCarpetAddition.mixins.json`，访问拓宽配置为 `src/main/resources/CarpetRofAddition.aw`，Mod 入口为 `ROFCarpetServer`。
- `processor/` 是规则、命令及日志注册相关的注解处理器子项目；生成内容应通过修改源代码或处理器更新，不直接修改构建产物。
- 修改前查看工作区差异，保留用户已有改动；只修改与当前任务有关的内容。

## 编码风格

以下规则总结自 `C:\Users\zhdds\Downloads\ROFCarpetAddition\src\main\java\com\carpet\rof\extraWorldData` 中的现有代码：

- Java 以 4 个空格缩进。类声明的左花括号通常另起一行；方法和控制语句同时存在换行与同行写法，修改时沿用所在文件、相邻代码的排法，不整文件重新格式化。
- 类名使用大驼峰，方法、字段和局部变量以小驼峰为主，常量使用大写下划线命名。名称直接表达数据或操作，例如 `chunkCache`、`addChunk`、`invalidateSpawning`；不为统一命名顺带重命名现有成员。
- 方法保持直接、紧凑，使用普通循环、条件判断和提前返回。简单的单语句判断可以沿用周围代码的单行写法；复杂分支使用代码块。
- 附加数据类直接持有状态和集合，集合常在字段声明处初始化；引用无需替换时使用 `final`。已有直接字段访问的设计不额外包装一层 getter/setter，也不为简单逻辑引入多余抽象。
- 类型清楚时可使用 `var`，集合初始化使用菱形语法。区块坐标等以 `long` 表示的数据，沿用现有 fastutil 原始类型集合和迭代器的使用方式。
- 相关字段和方法放在一起，以少量空行分隔逻辑组；空格、换行和 import 分组跟随相邻代码，不扩大纯格式变更。
- **只添加极为必要的注释**：仅解释代码本身无法清楚表达的约束、非直观原因或兼容性要求。不添加逐行解释、复述方法名的 Javadoc、装饰性分隔线或修改过程说明。
- 保留 Stonecutter 的 `//?` 条件标记和受控的注释代码分支；它们参与多版本源码处理，不能当成普通无用注释删除。版本差异沿用现有条件分支方式处理。

## 编译

- 使用仓库自带的 Gradle Wrapper。当前 Wrapper 为 Gradle 9.4.0，`build.gradle` 实际使用 Fabric Loom 1.15.5，`settings.gradle` 使用 Stonecutter 0.9.7；版本以这些文件的实际配置为准。
- 当前 `settings.gradle` 注册的 Minecraft 版本为 `26.1`、`26.2`，`stonecutter.gradle` 的活动版本为 `26.1`。`versions/1.21.*` 是保留的历史配置，不代表当前已启用的构建目标。
- 当前 26.x 主项目要求 JDK 25；`processor/build.gradle` 另行指定 Java 21 toolchain，因此还需让 Gradle 能找到 JDK 21。检查 `JAVA_HOME` 与 Wrapper 实际使用的 JVM，不只看 PATH 中的 `java`。
- 各版本依赖配置在 `versions/<版本>/gradle.properties`；根目录 `gradle.properties` 提供公共配置。不要只根据根配置判断实际依赖版本。
- 在项目根目录使用 PowerShell 执行，优先限定目标版本：

```powershell
# 检查 Gradle 与实际 JVM
.\gradlew.bat --version

# 编译目标版本
.\gradlew.bat :26.1:compileJava
.\gradlew.bat :26.2:compileJava

# 需要构建产物时执行，排除普通 test 任务
.\gradlew.bat :26.1:build -x test

# 仅在需要验证全部当前版本时执行
.\gradlew.bat build -x test
```

构建产物位于 `versions/<版本>/build/libs/`。非 Windows 环境将 `.\gradlew.bat` 换成 `./gradlew`。

可以尝试编译；如果因环境、JDK、网络、依赖解析等原因编译失败，**首次失败后最多再重试两次，即最多三次尝试**。达到上限立即停止编译尝试，直接向用户说明当前修改结果、执行过的命令、失败原因及未验证的部分。不要通过换参数、换构建任务或反复清缓存绕过重试上限，也不要把环境导致的未完成验证说成通过。

## 验证范围与 Mixin audit

- **用户没有要求时，不需要添加或运行编译和 Mixin 检查之外的任何测试**，包括单元测试、GameTest、性能基准、随机差分测试或启动正常游戏世界进行功能测试。已有设计文档中的额外验证建议不改变此规则。
- 根据修改内容选择必要的编译和 Mixin 检查；仅修改文档时不必启动构建或游戏。
- 项目已有 Mixin audit：`build.gradle` 定义 `serverMixinAudit` 与 `clientMixinAudit` 运行配置，入口为 `src/main/java/com/carpet/rof/utils/AutoMixinAuditExecutor.java`，由 `ROFCarpetServer.onInitialize()` 调用。
- 在项目根目录执行以下命令，按需要选择服务端或客户端，并将 `26.1` 替换成目标版本：

```powershell
.\gradlew.bat --no-parallel :26.1:runServerMixinAudit
.\gradlew.bat --no-parallel :26.1:runClientMixinAudit
```

- 任务自动传入 JVM 参数 `-Dcarpetrofaddition.mixin_audit=true`；审计只在 Fabric 开发环境且该属性为 `true` 时启用。手动使用 IDE 运行配置时，需要添加同一 JVM 参数。
- 审计调用 `MixinEnvironment.getCurrentEnvironment().audit()`。正常完成输出 `Mixin audit result: successful` 并以退出码 0 结束 JVM；捕获异常时输出 `Error when auditing mixin`、`Mixin audit result: failed` 并以退出码 1 结束。启动或注入阶段也可能提前失败，应结合完整日志和进程退出状态判断；没有执行到审计不能算通过。
- 审计结束会主动退出，不会保持服务器或客户端运行；通过 audit 只代表本次环境中的 Mixin 审计完成，不能据此声称游戏功能或性能测试通过。
- 所有版本的运行配置共用根目录 `run/`，日志通常在 `run/logs/latest.log`；串行执行，避免多个运行实例争用目录或覆盖日志。客户端审计需要可用的图形运行环境。
- `.github/workflows/build.yml` 中已有 CI 用法：`./gradlew --no-parallel runClientMixinAudit`，该步骤超时为 10 分钟。当前审计源码只调用 Mixin audit；不要根据旧设计文档声称它还会执行其他差分或功能测试。
