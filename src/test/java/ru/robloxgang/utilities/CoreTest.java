package ru.robloxgang.utilities;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.*;
import java.util.*;

class CoreTest {
    @Test void reminderFiresOnceAcrossReconnectAndClockRollback() {
        ReminderClock c=new ReminderClock();
        ZonedDateTime t=ZonedDateTime.of(2026,11,11,12,51,59,0,ZoneId.of("Europe/Moscow"));
        assertFalse(c.poll(t));assertTrue(c.poll(t.plusSeconds(1)));
        assertFalse(c.poll(t.plusSeconds(10)));assertFalse(c.poll(t.plusHours(1)));
        assertTrue(c.poll(t.plusHours(1).plusSeconds(1)));
        assertFalse(c.poll(t.plusSeconds(1)));
    }
    @Test void uuidWinsAndCurrentNameReplacesBootstrapAliases() {
        var yellow=Gang.MEMBERS.get(1);
        assertEquals(yellow,Gang.find(yellow.uuid(),"Renamed",false,Map.of()));
        assertEquals(yellow,Gang.find(UUID.randomUUID(),"timosharoblox52",true,Map.of()));
        Map<String,String> current=Map.of(yellow.uuid().toString(),"NewNickname");
        assertEquals(yellow,Gang.find(UUID.randomUUID(),"newnickname",true,current));
        assertNull(Gang.find(UUID.randomUUID(),"Lxrits",true,current));
        assertNull(Gang.find(UUID.randomUUID(),"TimoshaRoblox52",true,current));
        assertNull(Gang.find(UUID.randomUUID(),"NewNickname",false,current));
        assertNull(Gang.find(UUID.randomUUID(),"NewNickname123",true,current));
    }
    @Test void daylightSavingRepeatedHourIsTwoDifferentHours() {
        ReminderClock c=new ReminderClock();
        ZonedDateTime first=ZonedDateTime.of(2026,10,25,2,52,0,0,ZoneId.of("Europe/Berlin")).withEarlierOffsetAtOverlap();
        assertTrue(c.poll(first));assertTrue(c.poll(first.withLaterOffsetAtOverlap()));
        assertFalse(c.poll(first));
    }
}
