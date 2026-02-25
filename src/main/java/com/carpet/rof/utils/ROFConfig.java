package com.carpet.rof.utils;

import com.carpet.rof.commands.RequirementModifyCommand;
import com.carpet.rof.event.ROFEvents;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.mojang.logging.LogUtils;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.WorldSavePath;
import org.slf4j.Logger;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.carpet.rof.commands.RequirementModifyCommand.requirementModifyMap;

/**
 * 轻量级 JSON 配置管理器。
 * <pre>
 * // 初始化（传入配置文件路径）
 * ROFConfig cfg = new ROFConfig(serverDir.resolve("config/rof.json"));
 * cfg.load();
 *
 * // 读取（带默认值）
 * boolean flag = cfg.get("myFlag", true);
 * int     val  = cfg.get("myInt",  42);
 * String  str  = cfg.get("myStr",  "hello");
 *
 * // 写入并保存
 * cfg.set("myFlag", false);
 * cfg.save();
 * </pre>
 */
public class ROFConfig {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final Path filePath;
    private JsonObject data = new JsonObject();

    public ROFConfig(Path filePath) {
        this.filePath = filePath;
    }

    public static ROFConfig INSTANCE;
    // ------------------------------------------------------------------ load / save

    /** 从文件加载配置；文件不存在时使用空配置。 */
    public void load() {
        if (!Files.exists(filePath)) {
            data = new JsonObject();
            return;
        }
        try (Reader reader = new InputStreamReader(Files.newInputStream(filePath), StandardCharsets.UTF_8)) {
            JsonElement el = JsonParser.parseReader(reader);
            data = el.isJsonObject() ? el.getAsJsonObject() : new JsonObject();
        } catch (Exception e) {
            LOGGER.error("[ROFConfig] Failed to load config from {}: {}", filePath, e.getMessage());
            data = new JsonObject();
        }
    }

    /** 将当前配置保存到文件。 */
    public void save() {
        try {
            Files.createDirectories(filePath.getParent());
            try (Writer writer = new OutputStreamWriter(Files.newOutputStream(filePath), StandardCharsets.UTF_8)) {
                GSON.toJson(data, writer);
            }
        } catch (Exception e) {
            LOGGER.error("[ROFConfig] Failed to save config to {}: {}", filePath, e.getMessage());
        }
    }

    // ------------------------------------------------------------------ generic get / set

    /**
     * 获取配置值，若 key 不存在则写入默认值并返回。
     *
     * @param key          配置键
     * @param defaultValue 默认值（支持 Boolean / Integer / Long / Double / Float / String）
     * @param <T>          值类型
     * @return 存储的值或默认值
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key, T defaultValue) {
        if (!data.has(key)) {
            return defaultValue;
        }
        JsonElement el = data.get(key);
        try {
            if (defaultValue instanceof Boolean)  return (T) (Boolean)  el.getAsBoolean();
            if (defaultValue instanceof Integer)  return (T) (Integer)  el.getAsInt();
            if (defaultValue instanceof Long)     return (T) (Long)     el.getAsLong();
            if (defaultValue instanceof Double)   return (T) (Double)   el.getAsDouble();
            if (defaultValue instanceof Float)    return (T) (Float)    el.getAsFloat();
            if (defaultValue instanceof String)   return (T)            el.getAsString();
            if (defaultValue instanceof JsonObject)   return (T)            el.getAsJsonObject();
        } catch (Exception e) {
            LOGGER.warn("[ROFConfig] Cannot parse key '{}', using default: {}", key, defaultValue);
            set(key, defaultValue);
        }
        return defaultValue;
    }
    public <T> JsonElement get(String key) {
        if (!data.has(key)) {
            return null;
        }
        return  data.get(key);
    }
    /**
     * 设置配置值（支持 Boolean / Integer / Long / Double / Float / String，
     * 其他类型序列化为 JSON 字符串）。
     */
    public <T> void set(String key, T value) {
        if (value instanceof Boolean b)  { data.addProperty(key, b); return; }
        if (value instanceof Number  n)  { data.addProperty(key, n); return; }
        if (value instanceof String  s)  { data.addProperty(key, s); return; }
        // 兜底：序列化为 JSON
        data.add(key, GSON.toJsonTree(value));
    }

    // ------------------------------------------------------------------ misc

    /** 检查 key 是否存在。 */
    public boolean has(String key) {
        return data.has(key);
    }

    /** 删除一个 key。 */
    public void remove(String key) {
        data.remove(key);
    }

    /** 返回底层 JsonObject 的副本（只读用途）。 */
    public JsonObject snapshot() {
        return data.deepCopy();
    }

    /** 清空所有配置项。 */
    public void clear() {
        data = new JsonObject();
    }

    @Override
    public String toString() {
        return "ROFConfig{file=" + filePath + ", keys=" + data.size() + "}";
    }
}
