package jeresources.fabric.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jeresources.config.Settings;
import jeresources.util.LogHelper;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;

public class ConfigFileHandler {
    public static void readFromConfig() {
        File configFile = getConfigFile();
        if (!configFile.exists()) {
            writeToConfig();
            return;
        }

        JsonObject root = new JsonObject();
        Gson gson = new Gson();
        try (FileReader file = new FileReader(configFile)) {
            root = JsonParser.parseReader(file).getAsJsonObject();
        } catch (Exception e) {
            backupCorruptedConfig(configFile);
            LogHelper.error("Failed to read JER config: " + e.getMessage());
            writeToConfig();
            return;
        }

        if (root.has("itemsPerColumn")) {
            Settings.ITEMS_PER_COLUMN = root.get("itemsPerColumn").getAsInt();
        }
        if (root.has("itemsPerRow")) {
            Settings.ITEMS_PER_ROW = root.get("itemsPerRow").getAsInt();
        }
        if (root.has("diyData")) {
            Settings.useDIYdata = root.get("diyData").getAsBoolean();
        }
        if (root.has("showDevData")) {
            Settings.showDevData = root.get("showDevData").getAsBoolean();
        }
        if (root.has("enchantsBlacklist")) {
            Settings.excludedEnchants = gson.fromJson(root.getAsJsonArray("enchantsBlacklist"), String[].class);
        }
        if (root.has("hiddenTabs")) {
            Settings.hiddenCategories = gson.fromJson(root.getAsJsonArray("hiddenTabs"), String[].class);
        }
        if (root.has("dimensionsBlacklist")) {
            Settings.excludedDimensions = Arrays.asList(gson.fromJson(root.getAsJsonArray("dimensionsBlacklist"), Integer[].class));
        }
        if (root.has("disableLootManagerReloading")) {
            Settings.disableLootManagerReloading = root.get("disableLootManagerReloading").getAsBoolean();
        }
        if (root.has("enable3DBlockPreview")) {
            Settings.enable3DBlockPreview = root.get("enable3DBlockPreview").getAsBoolean();
        }
        if (root.has("fallbackTo2DBlockPreviewOnError")) {
            Settings.fallbackTo2DBlockPreviewOnError = root.get("fallbackTo2DBlockPreviewOnError").getAsBoolean();
        }
        if (root.has("force2DPreviewNamespaces")) {
            Settings.force2DPreviewNamespaces = gson.fromJson(root.getAsJsonArray("force2DPreviewNamespaces"), String[].class);
        }
        if (root.has("force2DPreviewBlocks")) {
            Settings.force2DPreviewBlocks = gson.fromJson(root.getAsJsonArray("force2DPreviewBlocks"), String[].class);
        }

    }

    public static void writeToConfig() {
        JsonObject root = new JsonObject();
        Gson gson = new Gson();
        File configFile = getConfigFile();
        File parent = configFile.getParentFile();

        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            LogHelper.error("Failed to create config directory: " + parent.getAbsolutePath());
            return;
        }

        root.addProperty("itemsPerColumn", Settings.ITEMS_PER_COLUMN);
        root.addProperty("itemsPerRow", Settings.ITEMS_PER_ROW);
        root.addProperty("diyData", Settings.useDIYdata);
        root.addProperty("showDevData", Settings.showDevData);
        root.add("enchantsBlacklist", gson.toJsonTree(Settings.excludedEnchants));
        root.add("hiddenTabs", gson.toJsonTree(Settings.hiddenCategories));
        root.add("dimensionsBlacklist", gson.toJsonTree(Settings.excludedDimensions));
        root.addProperty("disableLootManagerReloading", Settings.disableLootManagerReloading);
        root.addProperty("enable3DBlockPreview", Settings.enable3DBlockPreview);
        root.addProperty("fallbackTo2DBlockPreviewOnError", Settings.fallbackTo2DBlockPreviewOnError);
        root.add("force2DPreviewNamespaces", gson.toJsonTree(Settings.force2DPreviewNamespaces));
        root.add("force2DPreviewBlocks", gson.toJsonTree(Settings.force2DPreviewBlocks));

        try (FileWriter file = new FileWriter(configFile)) {
            file.write(new GsonBuilder().setPrettyPrinting().create().toJson(root));
            file.flush();
        } catch (IOException e) {
            LogHelper.error("Failed to write JER config: " + e.getMessage());
        }
    }

    public static File getConfigFile() {
        return FabricLoader.getInstance().getConfigDir().resolve("jeresources.json").toFile();
    }

    private static void backupCorruptedConfig(File configFile) {
        if (!configFile.exists()) {
            return;
        }
        File backup = new File(configFile.getParentFile(), "jeresources.json.bak");
        try {
            Files.copy(configFile.toPath(), backup.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            LogHelper.error("Failed to backup corrupted config: " + e.getMessage());
        }
    }
}
