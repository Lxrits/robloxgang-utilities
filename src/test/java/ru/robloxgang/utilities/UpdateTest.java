package ru.robloxgang.utilities;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.ZonedDateTime;
class UpdateTest {
 @Test void visualMaskPreservesOriginalAndStyles(){
  RobloxGangUtilities.config=new Config();
  var source=net.minecraft.network.chat.Component.literal("сорок ").append(net.minecraft.network.chat.Component.literal("два").withStyle(net.minecraft.ChatFormatting.RED));
  var filtered=Censor42.beforeWrap(source);
  assertEquals("сорок два",source.getString());assertEquals(source.getString(),filtered.getString());
  java.util.concurrent.atomic.AtomicInteger blurred=new java.util.concurrent.atomic.AtomicInteger();
  filtered.visit((style,text)->{if(style.getFont().toString().contains("robloxgang_utilities"))blurred.addAndGet(text.length());return java.util.Optional.empty();},net.minecraft.network.chat.Style.EMPTY);
  assertEquals(9,blurred.get());
  RobloxGangUtilities.config.blur42=false;assertSame(source,Censor42.beforeWrap(source));
  RobloxGangUtilities.config=new Config();
 }

 @Test void spamWindowAndCooldown(){SpamTrigger t=new SpamTrigger(3,1000,5000);assertFalse(t.press(100));assertFalse(t.press(1200));assertFalse(t.press(1300));assertTrue(t.press(1400));assertFalse(t.press(1500));assertFalse(t.press(6500));assertFalse(t.press(6600));assertTrue(t.press(6700));}
 @Test void minute42DoesNotReplace52(){ReminderClock a=new ReminderClock(42),b=new ReminderClock();ZonedDateTime t=ZonedDateTime.parse("2026-11-13T12:42:00+03:00");assertTrue(a.poll(t));assertFalse(a.poll(t.plusSeconds(10)));assertFalse(b.poll(t));assertTrue(b.poll(t.withMinute(52)));assertTrue(a.poll(t.plusHours(1)));}
 @Test void allMentions(){assertEquals(6,Censor42.ranges("abc42def 1420 Сорок два сорок-два forty_two FORTYTWO").size());assertTrue(Censor42.ranges("24 сорок пять fourteen").isEmpty());}
}
