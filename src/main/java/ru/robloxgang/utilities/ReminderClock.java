package ru.robloxgang.utilities;

import java.time.*;
import java.util.*;

/** One reminder per absolute local-hour occurrence, including reconnects and clock rollback. */
public final class ReminderClock {
    private final Set<Instant> fired = new HashSet<>();
    public boolean poll(ZonedDateTime now) {
        if (now.getMinute() != 52) return false;
        return fired.add(now.withMinute(0).withSecond(0).withNano(0).toInstant());
    }
}
