package ru.robloxgang.utilities;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class IntroTransitionTest {
 @Test void transitionWaitsForReadinessAndFadesMonotonically(){
  IntroTransition t=new IntroTransition();long start=5000000000L;
  assertEquals(1,t.opacity(start,false));assertFalse(t.started());
  assertEquals(1,t.opacity(start+1000000000L,false));assertFalse(t.started());
  assertEquals(1,t.opacity(start+2000000000L,true));assertTrue(t.started());
  float last=1;for(int i=0;i<=800;i+=10){float v=t.opacity(start+2000000000L+i*1000000L,true);assertTrue(v<=last && v>=0);last=v;}
  assertEquals(.5f,newMidpoint(),.0001f);
  assertEquals(0,t.opacity(start+3000000000L,true));
 }
 private float newMidpoint(){IntroTransition t=new IntroTransition();t.opacity(0,true);return t.opacity(400000000L,true);}
}
