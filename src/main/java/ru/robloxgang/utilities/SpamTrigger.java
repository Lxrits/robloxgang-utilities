package ru.robloxgang.utilities;
import java.util.ArrayDeque;
/** Counts physical press edges, never OS key-repeat events. Uses monotonic milliseconds. */
public final class SpamTrigger {
    private final ArrayDeque<Long> presses = new ArrayDeque<>();
    private final int count; private final long window, cooldown;
    private long next;
    public SpamTrigger(int count,long window,long cooldown){this.count=count;this.window=window;this.cooldown=cooldown;}
    public boolean press(long now){
        while(!presses.isEmpty() && now-presses.peekFirst()>window)presses.removeFirst();
        if(now<next)return false;
        presses.addLast(now);
        if(presses.size()<count)return false;
        presses.clear();next=now+cooldown;return true;
    }
    public void clear(){presses.clear();}
}
