package ru.robloxgang.utilities;

import com.mojang.blaze3d.platform.InputConstants;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.*;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import java.time.*;
import ru.robloxgang.utilities.mixin.KeyMappingAccess;

public final class RobloxGangUtilities implements ClientModInitializer {
    public static Config config;
    public static NameCache names;
    public static KeyMapping sneakKey, chatKey;
    private final ReminderClock clock = new ReminderClock();
    private int secondTone = -1;
    private float toneVolume;
    private boolean sneakDown, chatDown;
    @Override public void onInitializeClient() {
        config = Config.load(); names = new NameCache(); names.refresh(false);
        KeyMapping.Category category = KeyMapping.Category.register(ResourceLocation.fromNamespaceAndPath("robloxgang_utilities", "main"));
        MemeEffects.init(category);
        sneakKey = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.robloxgang_utilities.sneak", InputConstants.KEY_G, category));
        chatKey = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.robloxgang_utilities.chat52", InputConstants.KEY_H, category));
        HudElementRegistry.addLast(ResourceLocation.fromNamespaceAndPath("robloxgang_utilities", "notifications"), (g, delta) -> { BarrierOutlines.render(g); MemeEffects.render(g); Notifications.render(g); });
        ClientTickEvents.END_CLIENT_TICK.register(this::tick);
    }
    private void tick(Minecraft client) {
        MemeEffects.tick(client); BarrierOutlines.tick(client);
        if (secondTone >= 0 && --secondTone == 0) { playTone(client, 1.5f, toneVolume); secondTone = -1; }
        boolean allowSneak=!sneakDown, allowChat=!chatDown;
        while (sneakKey.consumeClick()) {
            if (!allowSneak || !config.sneak || client.screen != null || client.player == null) continue;
            allowSneak=false;
            boolean toggle = !client.options.toggleCrouch().get();
            client.options.toggleCrouch().set(toggle);
            ((KeyMappingAccess)client.options.keyShift).robloxgang$reset();
            client.options.save();
            if (config.sneakToast) Notifications.show("Режим приседа", toggle ? "Переключение • Toggle" : "Удержание • Hold", 0xA78BFA, false);
        }
        while (chatKey.consumeClick()) {
            if (allowChat && config.quickChat && client.screen == null && client.player != null && client.getConnection() != null) {
                allowChat=false;
                client.player.connection.sendChat("52");
                client.gui.getChat().addRecentChat("52");
            }
        }
        sneakDown=sneakKey.isDown(); chatDown=chatKey.isDown();
        if (config.reminder && client.level != null && client.player != null) {
            ZonedDateTime now = ZonedDateTime.now();
            if (clock.poll(now)) {
                Notifications.show("Уже " + String.format("%02d:52", now.getHour()), "Время написать 52!", 0xFBBF24, true);
                chime();
            }
        }
    }
    public static void preview52() { Notifications.show("Уже " + String.format("%02d:52", LocalTime.now().getHour()), "Время написать 52!", 0xFBBF24, true); }
    public static void preview52(Config draft) { Notifications.show("Уже " + String.format("%02d:52", LocalTime.now().getHour()), "Время написать 52!", 0xFBBF24, true,draft); }
    public static void chime() {
        previewChime(config);
    }
    public static void previewChime(Config settings) {
        if (settings.sound) {
            playTone(Minecraft.getInstance(), 1f, settings.volume);
            // Schedule through the client tick without sleeping or using the audio thread.
            active.secondTone = 5;
            active.toneVolume = settings.volume;
        }
    }
    private static RobloxGangUtilities active;
    public RobloxGangUtilities() { active = this; }
    private static void playTone(Minecraft client, float pitch,float volume) {
        client.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.NOTE_BLOCK_CHIME.value(), pitch, volume));
    }
}
