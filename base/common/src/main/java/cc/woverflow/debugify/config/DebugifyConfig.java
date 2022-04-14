package cc.woverflow.debugify.config;

import cc.woverflow.debugify.Debugify;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class DebugifyConfig {
    private final Path configPath = Debugify.configFolder.resolve("debugify.json");
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private final Map<String, Boolean> jsonBugFixes = new HashMap<>();

    private final Map<String, Map<String, Boolean>> bugFixCategories = new LinkedHashMap<>();
    public boolean defaultDisabled = false;
    public boolean optOutUpdater = false;

    private boolean preloaded = false;

    public void registerBugFix(String id, String category, boolean defaultState) {
        if (bugFixCategories.containsKey(id))
            return;

        boolean enabled = jsonBugFixes.getOrDefault(id, defaultState && !defaultDisabled);
        bugFixCategories.putIfAbsent(category, new LinkedHashMap<>());
        bugFixCategories.get(category).put(id, enabled);
    }

    public void preload() {
        if (preloaded) return;
        preloaded = true;

        if (!Files.exists(configPath)) {
            return;
        }

        Debugify.logger.info("Preloading Debugify");

        try {
            String jsonString = Files.readString(configPath);
            JsonObject json = gson.fromJson(jsonString, JsonObject.class);

            Map<String, Boolean> bugFixes = json.entrySet().stream()
                    .filter((entry) -> entry.getKey().startsWith("MC-") && entry.getValue().isJsonPrimitive() && entry.getValue().getAsJsonPrimitive().isBoolean())
                    .map((entry) -> new AbstractMap.SimpleEntry<>(entry.getKey(), entry.getValue().getAsBoolean()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            if (json.has("opt_out_updater")) {
                optOutUpdater = json.get("opt_out_updater").getAsBoolean();
            }
            if (json.has("default_disabled")) {
                defaultDisabled = json.get("default_disabled").getAsBoolean();
            }

            jsonBugFixes.clear();
            jsonBugFixes.putAll(bugFixes);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Debugify.logger.info("Done.");
    }

    public void save() {
        Debugify.logger.info("Saving Debugify");
        try {
            Files.deleteIfExists(configPath);

            JsonObject json = new JsonObject();
            bugFixCategories.forEach((category, bugFixes) -> bugFixes.forEach(json::addProperty));
            json.addProperty("opt_out_updater", optOutUpdater);
            if (defaultDisabled) {
                json.addProperty("default_disabled", true);
            }

            Files.createFile(configPath);
            Files.writeString(configPath, gson.toJson(json));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isBugFixEnabled(String bug) {
        return getBugFixesIgnoringCategories().getOrDefault(bug, false);
    }

    public Map<String, Map<String, Boolean>> getBugFixCategories() {
        return bugFixCategories;
    }
    public Map<String, Boolean> getBugFixesIgnoringCategories() {
        Map<String, Boolean> merged = new LinkedHashMap<>();
        bugFixCategories.forEach((category, bugFixes) -> merged.putAll(bugFixes));
        return merged;
    }

    public void setBugFixEnabled(String bug, boolean enabled) {
        bugFixCategories.forEach((category, bugFixes) -> bugFixes.forEach((id, currentState) -> {
            if (id.equals(bug)) {
                bugFixCategories.get(category).replace(id, enabled);
            }
        }));
    }

    public boolean doesJsonMatchConfig() {
        return jsonBugFixes.equals(getBugFixesIgnoringCategories());
    }
}
