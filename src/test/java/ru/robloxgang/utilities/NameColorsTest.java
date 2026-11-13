package ru.robloxgang.utilities;

import net.minecraft.network.chat.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

class NameColorsTest {
    private final Gang.Member member=Gang.MEMBERS.getFirst();
    private Component paint(Component input) { return NameColors.apply(input,member.uuid(),member.initialName(),NameColors.Place.TAB,new Config(),Map.of()); }
    @Test void clientPriorityOverridesServerNameColorInEveryPlaceAndPreservesSurroundings() {
        Config config=new Config();config.serverColorPriority=false;
        for(NameColors.Place place:NameColors.Place.values())for(int serverColor:new int[]{0xFFFFFF,0x123456,member.color()}) {
            Component c=Component.empty().withStyle(Style.EMPTY.withColor(serverColor))
                .append(Component.literal("[Rank] "))
                .append(Component.literal(member.initialName()).withStyle(Style.EMPTY.withBold(true)))
                .append(Component.literal(" suffix"));
            Component colored=NameColors.apply(c,member.uuid(),member.initialName(),place,config,Map.of());
            assertEquals(c.getString(),colored.getString());
            colored.visit((style,text)->{
                if(text.isEmpty())return Optional.empty();
                assertEquals(text.equals(member.initialName())?member.color():serverColor,style.getColor().getValue());
                if(text.equals(member.initialName()))assertTrue(style.isBold());
                return Optional.empty();
            },Style.EMPTY);
        }
    }
    @Test void inheritedServerColorAndExplicitWhiteTakePriority() {
        for(int color:new int[]{0xFFFFFF,0x123456}) {
            Component c=Component.empty().withStyle(Style.EMPTY.withColor(color)).append(Component.literal(member.initialName()));
            assertSame(c,paint(c));
        }
    }
    @Test void colorsOnlyNameEvenWhenSplitAcrossComponents() {
        Component c=Component.literal("[Admin] ").withStyle(Style.EMPTY.withColor(0xAAAAAA));
        c=Component.empty().append(c).append(Component.literal("IlyaRoblox")).append(Component.literal("Pro52")).append(Component.literal(" suffix"));
        Component result=paint(c);
        assertEquals(c.getString(),result.getString());
        List<Integer> colors=new ArrayList<>();
        result.visit((style,text)->{for(int i=0;i<text.length();i++)colors.add(style.getColor()==null?-1:style.getColor().getValue());return Optional.empty();},Style.EMPTY);
        assertEquals(0xAAAAAA,colors.get(0));
        assertEquals(member.color(),colors.get(8));
        assertEquals(-1,colors.getLast());
    }
    @Test void oneServerColoredFragmentPreventsPartialRecoloring() {
        Component c=Component.empty().append(Component.literal("IlyaRoblox")).append(Component.literal("Pro52").withColor(0x00FFFF));
        assertSame(c,paint(c));
    }
    @Test void doesNotColorSubstringOfAnotherUsername() {
        Component c=Component.literal(member.initialName()+"_fake");
        assertSame(c,paint(c));
    }
    @Test void disabledFeatureAndMemberLeaveOriginalUntouched() {
        Config config=new Config();config.tab=false;
        Component c=Component.literal(member.initialName());
        assertSame(c,NameColors.apply(c,member.uuid(),member.initialName(),NameColors.Place.TAB,config,Map.of()));
        config.tab=true;config.disabledMembers.add(member.uuid().toString());
        assertSame(c,NameColors.apply(c,member.uuid(),member.initialName(),NameColors.Place.TAB,config,Map.of()));
    }
}
