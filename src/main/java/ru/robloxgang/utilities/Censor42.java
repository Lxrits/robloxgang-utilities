package ru.robloxgang.utilities;
import java.util.*;
import java.util.regex.*;
import net.minecraft.network.chat.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
/** Render-only filtering: signed messages, profiles and server data are untouched. */
public final class Censor42 {
    private static final Pattern MATCH=Pattern.compile("42|сорок(?:а)?[\\s\\p{Z}\\p{P}_]*(?:два|две|двух|двум(?:я)?|втор(?:ой|ая|ое|ые|ого|ому|ую|ым|ом|ых|ыми))|forty[\\s\\p{Z}\\p{P}_]*two",Pattern.CASE_INSENSITIVE|Pattern.UNICODE_CASE);
    private static final FontDescription FONT=new FontDescription.Resource(ResourceLocation.fromNamespaceAndPath("robloxgang_utilities","blur"));
    public static List<int[]> ranges(String text){List<int[]> out=new ArrayList<>();Matcher m=MATCH.matcher(text);while(m.find())out.add(new int[]{m.start(),m.end()});return out;}
    public static FormattedText beforeWrap(FormattedText input){
        if(RobloxGangUtilities.config==null || !RobloxGangUtilities.config.blur42)return input;
        String text=input.getString();List<int[]> spans=ranges(text);if(spans.isEmpty())return input;
        boolean[] mask=new boolean[text.length()];for(int[] r:spans)Arrays.fill(mask,r[0],r[1],true);
        MutableComponent out=Component.empty();int[] offset={0};
        input.visit((style,part)->{
            for(int i=0;i<part.length();){int cp=part.codePointAt(i);Style target=style;
                if(mask[offset[0]]){if(!Character.isLetterOrDigit(cp))cp=' ';target=target.withFont(FONT).withBold(false).withItalic(false).withObfuscated(false).withUnderlined(false).withStrikethrough(false);}
                out.append(Component.literal(new String(Character.toChars(cp))).setStyle(target));
                int n=Character.charCount(part.codePointAt(i));i+=n;offset[0]+=n;
            }return Optional.empty();
        },Style.EMPTY);
        return out;
    }
    public static FormattedCharSequence apply(FormattedCharSequence input){
        if(RobloxGangUtilities.config==null || !RobloxGangUtilities.config.blur42)return input;
        StringBuilder text=new StringBuilder();List<Integer> chars=new ArrayList<>();List<Style> styles=new ArrayList<>();
        input.accept((i,style,cp)->{text.appendCodePoint(cp);chars.add(cp);styles.add(style);return true;});
        List<int[]> spans=ranges(text.toString());if(spans.isEmpty())return input;
        boolean[] mask=new boolean[text.length()];for(int[] r:spans)Arrays.fill(mask,r[0],r[1],true);
        return sink->{int offset=0;for(int i=0;i<chars.size();i++){int cp=chars.get(i);Style st=styles.get(i);if(mask[offset]){ if(!Character.isLetterOrDigit(cp))cp=' '; st=st.withFont(FONT).withBold(false).withItalic(false).withObfuscated(false).withUnderlined(false).withStrikethrough(false); } if(!sink.accept(offset,st,cp))return false;offset+=Character.charCount(cp);}return true;};
    }
}
