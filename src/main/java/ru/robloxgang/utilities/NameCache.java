package ru.robloxgang.utilities;

import com.google.gson.*;
import net.fabricmc.loader.api.FabricLoader;
import java.net.*;
import java.net.http.*;
import java.nio.file.*;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.LoggerFactory;

public final class NameCache {
    private record Entry(String name, long updated) {}
    private final Path path = FabricLoader.getInstance().getConfigDir().resolve("robloxgang-names.json");
    private final AtomicBoolean busy = new AtomicBoolean();
    private volatile Map<String, Entry> entries = Map.of();
    private volatile Map<String, String> currentNames = Map.of();
    public volatile String status = "";
    public NameCache() {
        try {
            if (Files.exists(path)) {
                Map<String, Entry> loaded = new HashMap<>();
                JsonObject root = JsonParser.parseString(Files.readString(path)).getAsJsonObject();
                for (Gang.Member m : Gang.MEMBERS) {
                    if (!root.has(m.uuid().toString())) continue;
                    JsonObject e = root.getAsJsonObject(m.uuid().toString());
                    String name = e.get("name").getAsString();
                    if (name.matches("[A-Za-z0-9_]{1,16}")) loaded.put(m.uuid().toString(), new Entry(name, e.get("updated").getAsLong()));
                }
                entries = Map.copyOf(loaded);
                publishNames();
            }
        } catch (Exception e) { LoggerFactory.getLogger("RobloxGang Utilities").warn("Cannot read name cache", e); }
    }
    public Map<String, String> names() {
        return currentNames;
    }
    private void publishNames() { Map<String,String> snapshot=new HashMap<>();entries.forEach((id,e)->snapshot.put(id,e.name));currentNames=Map.copyOf(snapshot); }
    public long lastUpdate() { return entries.values().stream().mapToLong(Entry::updated).min().orElse(0); }
    public boolean busy() { return busy.get(); }
    public void refresh(boolean force) {
        if (!busy.compareAndSet(false, true)) return;
        CompletableFuture.runAsync(() -> {
            int updated = 0, failed = 0;
            Map<String, Entry> next = new HashMap<>(entries);
            try (HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build()) {
                for (Gang.Member member : Gang.MEMBERS) {
                    String id = member.uuid().toString();
                    Entry cached = next.get(id);
                    long now = System.currentTimeMillis();
                    if (!force && cached != null && now >= cached.updated && now - cached.updated < Duration.ofHours(24).toMillis()) continue;
                    try {
                        URI uri = URI.create("https://sessionserver.mojang.com/session/minecraft/profile/" + id.replace("-", ""));
                        HttpResponse<String> r = http.send(HttpRequest.newBuilder(uri).timeout(Duration.ofSeconds(8)).header("User-Agent", "RobloxGangUtilities/1.0").GET().build(), HttpResponse.BodyHandlers.ofString());
                        if (r.statusCode() != 200 || r.body().length() > 100_000) throw new IllegalStateException("Profile HTTP " + r.statusCode());
                        JsonObject profile = JsonParser.parseString(r.body()).getAsJsonObject();
                        if (!profile.get("id").getAsString().equalsIgnoreCase(id.replace("-", ""))) throw new IllegalStateException("UUID mismatch");
                        String name = profile.get("name").getAsString();
                        if (!name.matches("[A-Za-z0-9_]{1,16}")) throw new IllegalStateException("Invalid username");
                        next.put(id, new Entry(name, now)); updated++;
                    } catch (Exception e) { failed++; LoggerFactory.getLogger("RobloxGang Utilities").warn("Profile lookup failed for {}: {}", id, e.toString()); }
                }
                entries = Map.copyOf(next);
                publishNames();
                if (updated > 0) {
                    Files.createDirectories(path.getParent());
                    Path temp = path.resolveSibling(path.getFileName() + ".tmp");
                    Files.writeString(temp, new GsonBuilder().setPrettyPrinting().create().toJson(next));
                    Files.move(temp, path, StandardCopyOption.REPLACE_EXISTING);
                }
                status = failed > 0 ? "API недоступен для " + failed + " игроков; используется кэш" : "Ники актуальны";
            } catch (Exception e) { status = "Не удалось сохранить кэш ников"; }
            finally { busy.set(false); }
        });
    }
}
