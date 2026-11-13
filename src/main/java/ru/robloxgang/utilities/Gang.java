package ru.robloxgang.utilities;

import java.util.*;

public final class Gang {
    public record Member(UUID uuid, String initialName, int color) {}
    public static final List<Member> MEMBERS = List.of(
        member("70f36ff1-0db4-451e-b849-ea33d0c501bf", "IlyaRobloxPro52", 0xFF5555),
        member("4ae06fca-7010-4109-a4bf-d92e47594fe1", "Lxrits", 0xFFFF55),
        member("541c832f-a706-4b3a-acbb-2e36fd6aecd6", "AndreyRobloxFan", 0x55FF55),
        member("d0ae516f-fb5d-4c30-a08f-38a709792ff5", "TimuRobloxLOL52", 0x5555FF),
        member("a9042d71-8e0b-423e-97c8-4df29a1e6908", "VladRobloxSHACAL", 0xAA00AA)
    );
    private static Member member(String id, String name, int color) { return new Member(UUID.fromString(id), name, color); }
    public static Member find(UUID uuid, String name, boolean fallback, Map<String, String> names) {
        for (Member m : MEMBERS) if (m.uuid.equals(uuid)) return m;
        if (fallback && name != null) for (Member m : MEMBERS) {
            String current = names.getOrDefault(m.uuid.toString(), m.initialName);
            if (current.equalsIgnoreCase(name)) return m;
            // Both supplied aliases are bootstrap values only, never retained after an API lookup.
            if (!names.containsKey(m.uuid.toString()) && m.initialName.equals("Lxrits") && name.equalsIgnoreCase("TimoshaRoblox52")) return m;
        }
        return null;
    }
}
