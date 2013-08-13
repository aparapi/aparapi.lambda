package hsailtest;

import com.amd.aparapi.Device;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Arrays;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.WindowConstants;


public class NBody{

   public class Body{
      float x,y,z;
      float vx,vy,vz;
   }

   public static void main(String[] _args){
      String mode = _args[0];
      int bodyCount = Integer.parseInt(_args[1]);
      Device device = mode.equals("hsa")?Device.hsa():
         (mode.equals("jtp")?Device.jtp():
             (mode.equals("seq")?Device.seq():
                (mode.equals("best")?Device.best():null)));
      if (device != null){
         NBody nb = new NBody();
         nb.go(device, bodyCount);
      }
   }

   void go(Device device, int bodyCount ){
      float frame = 0f;
      int width = Integer.getInteger("width", 768);
      int height =  Integer.getInteger("height", 768);
      BufferedImage offscreen = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
      int[] rgb = ((DataBufferInt) offscreen.getRaster().getDataBuffer()).getData();
      JFrame jframe = new JFrame("NBody");
      Body[] bodies = new Body[bodyCount];
      float delT = .05f;
      float espSqr = 1.0f;
      float mass = 20f;

      float maxDist = width / 4;
      Device.jtp().forEach(bodies.length, body -> {
         float theta = (float) (Math.random() * Math.PI * 2);
         float phi = (float) (Math.random() * Math.PI * 2);
         float radius = (float) (Math.random() * maxDist);
         bodies[body] = new Body();
         bodies[body].x = (float) (radius * Math.cos(theta) * Math.sin(phi)) + width / 2;
         bodies[body].y = (float) (radius * Math.sin(theta) * Math.sin(phi)) + height / 2;
         bodies[body].z = (float) (radius * Math.cos(phi));
      });

      JComponent viewer = new JComponent(){
      };

      viewer.setPreferredSize(new Dimension(width, height));
      jframe.getContentPane().add(viewer);
      jframe.pack();
      jframe.setVisible(true);

      jframe.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
      long first = System.currentTimeMillis();
      while(true){
         Arrays.fill(rgb, 0);
         device.forEach(bodies.length, gid -> {
            Body thisBody = bodies[gid];
            float accx = 0.f;
            float accy = 0.f;
            float accz = 0.f;
            for(int i = 0; i < bodies.length; i++){
               Body otherBody = bodies[i];
               float dx = otherBody.x - thisBody.x;
               float dy = otherBody.y - thisBody.y;
               float dz = otherBody.z - thisBody.z;
               float invDist = 1f / (float) Math.sqrt(((dx * dx) + (dy * dy) + (dz * dz) + espSqr));
               accx += mass * invDist * invDist * invDist * dx;
               accy += mass * invDist * invDist * invDist * dy;
               accz += mass * invDist * invDist * invDist * dz;
            }
            accx *= delT;
            accy *= delT;
            accz *= delT;

            thisBody.x += thisBody.vx * delT + (accx * .5f * delT);
            thisBody.y += thisBody.vy * delT + (accy * .5f * delT);
            thisBody.z += thisBody.vz * delT + (accz * .5f * delT);

            thisBody.vx +=  accx;
            thisBody.vy +=  accy;
            thisBody.vz +=  accz;
            int x =  (int)thisBody.x;
            int y =  (int)thisBody.y;
            for (int px =x-1; px<x+2; px++){ 
               if (px<width && y<height && px>=0 && y>0){
                  rgb[px+y*width]=0xffffff;
               }
            }
            for (int py =y-1; py<y+2; py+=2){ 
               if (x<height && py<height && x>=0 && py>=0){
                  rgb[x+py*width]=0xffffff;
               }
            }
         });
         viewer.getGraphics().drawImage(offscreen, 0, 0, null);
         long delta = System.currentTimeMillis()-first;
         frame+=1;
         if (delta > 1000){
            System.out.printf("fps=%5.2f\n",(frame*1000)/delta);
            first = System.currentTimeMillis() ;
            frame=0;
         }
      }
   }
}
