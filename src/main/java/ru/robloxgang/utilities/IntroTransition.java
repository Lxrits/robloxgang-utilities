package ru.robloxgang.utilities;
/** Monotonic crossfade; -1 means the menu is not ready yet. */
public final class IntroTransition {
    public static final long DURATION_NANOS=800_000_000L;
    private long started=-1;
    public boolean started(){return started>=0;}
    public float opacity(long now,boolean menuReady){
        if(!menuReady)return 1;
        if(started<0)started=now;
        double t=Math.clamp((double)(now-started)/DURATION_NANOS,0,1);
        return (float)(1-t*t*(3-2*t));
    }
}
