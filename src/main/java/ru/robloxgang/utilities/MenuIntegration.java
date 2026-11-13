package ru.robloxgang.utilities;

import com.terraformersmc.modmenu.api.*;

public final class MenuIntegration implements ModMenuApi {
    @Override public ConfigScreenFactory<?> getModConfigScreenFactory() { return SettingsScreen::new; }
}
