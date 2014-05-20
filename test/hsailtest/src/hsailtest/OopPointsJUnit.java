package hsailtest;

import com.amd.aparapi.Aparapi;
import com.amd.aparapi.Device;
import org.junit.Test;

import static junit.framework.Assert.assertTrue;

public class OopPointsJUnit{
   public static class P{
      public int x;
      public int y;
      public int v;

      int getX(){
         return (x);
      }

      int getY(){
         return (y);
      }

      int getXY(){
         return (getX()+getY());
      }

      void setX(int _x){
         x = _x;
      }

      void setY(int _y){
         y = _y;
      }

      void setV(int _v){
         v = _v;
      }

      void clear(){
         x = y = 0;
      }

      @Override
      public String toString(){
         return ("("+x+", "+y+", "+v+")");
      }
   }

   static void dump(String type, P[] points){
      JunitHelper.out(type+" ->");
      for (int i = 0; i<points.length; i++){
         if (i != 0){
            JunitHelper.out(", ");
         }
         JunitHelper.out(""+points[i]);
      }
      JunitHelper.nl();
   }

   @Test
   public void test(){

      int len = 12;
      P[] points = new P[len];

      for (int i = 0; i<len; i++){
         points[i] = new P();
      }

      Aparapi.IntTerminal ic = gid -> {
         P p = points[gid];

         p.setX(gid);
         p.setY(gid*2);
         p.setV(p.getXY());

      };

      Device.hsa().forEach(len, ic);
      dump("hsa", points);
      P[] hsaPoints = new P[points.length];
      for (int i = 0; i<points.length; i++){
         hsaPoints[i] = points[i];
         points[i] = new P();
      }
      Device.jtp().forEach(len, i -> points[i].clear());
      Device.jtp().forEach(len, ic);
      dump("jtp", points);

      for (int i = 0; i<points.length; i++){
         assertTrue("hsaPoint["+i+"]==points["+i+"]",
               hsaPoints[i].x == points[i].x &&
                     hsaPoints[i].y == points[i].y &&
                     hsaPoints[i].getXY() == points[i].getXY());

      }

   }
}
