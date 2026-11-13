package ru.robloxgang.utilities;

import com.google.gson.*;
import net.fabricmc.loader.api.FabricLoader;
import java.nio.file.*;
import java.io.*;
import java.util.*;
import org.slf4j.LoggerFactory;

public final class Config {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static Path path() { return FabricLoader.getInstance().getConfigDir().resolve("robloxgang-utilities.json"); }
    public boolean sneak = true, sneakToast = true, reminder = true, sound = true, quickChat = true;
    public boolean colors = true, tab = true, chat = true, nametag = true, fallback = true;
    public boolean serverColorPriority = true;
    public boolean barriers = true, intro = true, overdose = true, anthem = true, blur42 = true, reminder42 = true;
    public boolean reducedMotion = false;
    public int duration = 6, margin = 12;
    public float volume = 0.65f, scale = 1f;
    public Map<String, Integer> memberColors = new HashMap<>();
    public Set<String> disabledMembers = new HashSet<>();
    public static Config load() {
        Path PATH=path();
        try {
            if (Files.exists(PATH)) {
                Config c = GSON.fromJson(Files.readString(PATH), Config.class);
                if (c != null) { c.normalize(); return c; }
            }
        } catch (Exception e) { LoggerFactory.getLogger("RobloxGang Utilities").warn("Cannot read settings; using defaults", e); }
        return new Config();
    }
    public Config copy() { return GSON.fromJson(GSON.toJson(this), Config.class); }
    private void normalize() {
        duration = Math.clamp(duration, 2, 20); margin = Math.clamp(margin, 4, 64);
        volume = Float.isFinite(volume) ? Math.clamp(volume, 0, 1) : .65f;
        scale = Float.isFinite(scale) ? Math.clamp(scale, .75f, 1.5f) : 1;
        if (memberColors == null) memberColors = new HashMap<>();
        memberColors.entrySet().removeIf(e -> e.getValue() == null);
        memberColors.replaceAll((k,v) -> v & 0xFFFFFF);
        if (disabledMembers == null) disabledMembers = new HashSet<>();
    }
    public boolean save() {
        Path PATH=path();
        normalize();
        try {
            Files.createDirectories(PATH.getParent());
            Path temp = PATH.resolveSibling(PATH.getFileName() + ".tmp");
            Files.writeString(temp, GSON.toJson(this));
            try { Files.move(temp, PATH, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE); }
            catch (AtomicMoveNotSupportedException e) { Files.move(temp, PATH, StandardCopyOption.REPLACE_EXISTING); }
            return true;
        } catch (IOException e) { LoggerFactory.getLogger("RobloxGang Utilities").error("Cannot save settings", e); return false; }
    }
}
