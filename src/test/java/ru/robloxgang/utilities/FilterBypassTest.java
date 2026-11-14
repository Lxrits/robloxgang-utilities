package ru.robloxgang.utilities;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import net.minecraft.network.chat.*;
import java.util.*;
class FilterBypassTest {
 @Test void bypassesAreMatchedAsCompleteRanges(){
  for(String text:List.of("42","4-2","4.2","4,2","4 2","4...2","4___2","4 — . , 2","4\u200B2","4\u034F2","4\uFE0F2","4\u20602","4\uFEFF2","4\u00A02","4\u202F2","4\uDB40\uDC012")){
   var spans=Censor42.ranges(text);assertEquals(1,spans.size(),text);assertArrayEquals(new int[]{0,text.length()},spans.getFirst(),text);
  }
  assertTrue(Censor42.ranges("4 hello 2").isEmpty());
  assertTrue(Censor42.ranges("24 34").isEmpty());
  assertTrue(Censor42.ranges("4").isEmpty());assertTrue(Censor42.ranges("2").isEmpty());
 }
 @Test void stylesAndSupplementaryCharactersDoNotBreakOffsets(){
  RobloxGangUtilities.config=new Config();
  Component original=Component.literal("😀 4").append(Component.literal("...\uDB40\uDC01").withStyle(net.minecraft.ChatFormatting.RED)).append("2 suffix");
  var source=original.getVisualOrderText();List<Integer> blurred=new ArrayList<>();
  Censor42.apply(source).accept((index,style,cp)->{if(style.getFont().toString().contains("robloxgang_utilities"))blurred.add(cp);return true;});
  assertEquals(6,blurred.size());assertEquals((int)'4',(int)blurred.getFirst());assertEquals((int)'2',(int)blurred.getLast());
  assertEquals("😀 4...\uDB40\uDC012 suffix",original.getString());
  var filtered=Censor42.beforeWrap(original);assertTrue(filtered.getString().endsWith("2 suffix"));
 }
}
