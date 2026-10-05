# NBTHelper

`com.carpet.rof.utils.NBTHelper` 将 NBT 存档的版本差异集中在一个工具类里，供 Mixin 的 `@Coerce Object` 参数直接调用：

- 1.21.5 的实体存档参数是 `CompoundTag`。
- 1.21.6 及之后的实体存档读取、写入参数分别是 `ValueInput`、`ValueOutput`。
- 新版本中也可以直接传入 `CompoundTag`，用于世界附加数据等现有 NBT 数据。

## 实体存档注入

```java
import com.carpet.rof.utils.NBTHelper;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Inject(method = "readAdditionalSaveData", at = @At("HEAD"))
private void rof$readMobAi(@Coerce Object input, CallbackInfo ci)
{
    this.noBrainAi = NBTHelper.getBooleanOr(input, "NoBrainAI", false);
    this.remaining = NBTHelper.getIntOr(input, "remaining", -1);
}

@Inject(method = "addAdditionalSaveData", at = @At("HEAD"))
private void rof$writeMobAi(@Coerce Object output, CallbackInfo ci)
{
    NBTHelper.putBoolean(output, "NoBrainAI", this.noBrainAi);
    NBTHelper.putInt(output, "remaining", this.remaining);
}
```

注入点仍须在目标类上实际存在；`@Coerce Object` 统一参数类型，`NBTHelper` 负责调用对应版本的读写 API。

## 方法

所有方法都是静态方法，第一个参数为存档输入或输出对象，第二个参数为字段名。

| 数据 | 读取 | 写入 |
| --- | --- | --- |
| 布尔值 | `getBooleanOr(input, name, defaultValue)` | `putBoolean(output, name, value)` |
| byte / short / int / long / float / double | `getByteOr` / `getShortOr` / `getIntOr` / `getLongOr` / `getFloatOr` / `getDoubleOr` | `putByte` / `putShort` / `putInt` / `putLong` / `putFloat` / `putDouble` |
| 字符串 | `getStringOr(input, name, defaultValue)` | `putString(output, name, value)` |
| 可选字段 | `getInt` / `getLong` / `getString`，返回 `Optional` | 使用对应 `put…` 方法 |
| int 数组 | `getIntArray(input, name)`，返回 `Optional<int[]>` | `putIntArray(output, name, value)` |
| Codec 数据 | `read(input, name, codec)`，返回 `Optional<T>` | `store(output, name, codec, value)` |
| 子对象 | `readChild(input, name)` / `readChildOrEmpty(input, name)` | `writeChild(output, name)` |
| 删除字段 | — | `discard(output, name)` |

`get…Or` 在字段缺失或 NBT 类型不匹配时返回传入的默认值，数值类型沿用原版的数值转换行为。对象类型或读写方向传错会抛出 `IllegalArgumentException`。

## 嵌套数据与 Codec

```java
Object childOutput = NBTHelper.writeChild(output, "rof");
NBTHelper.putInt(childOutput, "count", 3);

Object childInput = NBTHelper.readChildOrEmpty(input, "rof");
int count = NBTHelper.getIntOr(childInput, "count", 0);

NBTHelper.store(output, "names", Codec.STRING.listOf(), names);
List<String> loadedNames = NBTHelper.read(input, "names", Codec.STRING.listOf())
        .orElseGet(List::of);

NBTHelper.store(output, "extra", CompoundTag.CODEC, extraTag);
```

`writeChild` 新建子对象并替换该字段的原值，返回的对象可以继续交给 `NBTHelper` 写入。`readChildOrEmpty` 对缺失字段返回空的读取对象，不修改原数据。

Codec 调用在新存档接口上保留其原有的注册表上下文与错误报告；直接传入 `CompoundTag` 时使用 `NbtOps.INSTANCE`，适用于不依赖注册表上下文的数据。

版本分支只写在 `NBTHelper` 中。Mixin 无需重复声明 `ValueInput`、`ValueOutput`、`CompoundTag` 分支。
