package hsailtest;

import com.amd.aparapi.Device;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class InitBodyJUnit{

   public class Body{
      public float x, y, z;

      void init(int width, int height, int id){
         float scaledId = ((float)id)/width;
         float theta = (float)(scaledId*Math.PI*2);
         float phi = (float)(scaledId*Math.PI*2);
         float radius = scaledId*width/2;
         x = (float)(radius*Math.cos(theta)*Math.sin(phi))+width/2;
         y = (float)(radius*Math.sin(theta)*Math.sin(phi))+height/2;
         z = (float)(radius*Math.cos(phi));
      }
   }

   @Test
   public void test(){
      int width = 200;
      int height = 200;
      int len = JunitHelper.getPreferredArraySize();
      Body[] jtpBodies = new Body[len];
      Body[] hsaBodies = new Body[len];
      // we can't construct
      Device.jtp().forEach(len, body -> {
         jtpBodies[body] = new Body();
         jtpBodies[body].init(width, height, body);
         hsaBodies[body] = new Body();
      });

      Device.hsa().forEach(len, body -> {
         hsaBodies[body].init(width, height, body);
      });

      for (int i = 0; i<len; i++){
         if (!JunitHelper.withinTolerance(jtpBodies[i].x, hsaBodies[i].x)){
            assertTrue("body["+i+"] dx", false);
         }
         if (!JunitHelper.withinTolerance(jtpBodies[i].y, hsaBodies[i].y)){
            assertTrue("body["+i+"] dy", false);
         }
         if (!JunitHelper.withinTolerance(jtpBodies[i].y, hsaBodies[i].y)){
            assertTrue("body["+i+"] dz", false);
         }

      }
      assertTrue("same ", true);
   }
}
