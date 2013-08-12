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
   static int frame = 0;

    static final int width = Integer.getInteger("width", 768);

    static final int height =  Integer.getInteger("height", 768);
    static final BufferedImage offscreen = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    static final int[] rgb = ((DataBufferInt) offscreen.getRaster().getDataBuffer()).getData();

  public static class Body{
     float x,y,z;
     float vx, vy, vz;

  }

   public static void main(String[] _args){
      JFrame jframe = new JFrame("NBody");

      final Body[] bodies = new Body[512];

      final float delT = .005f;
      final float espSqr = 1.0f;
      final float mass = 20f;

      //final float[] xyz = new float[bodies * 3]; // positions xy and z of bodies
      //final float[] vxyz = new float[bodies * 3]; // velocity component of x,y and z of bodies

     if (true){
      final float maxDist = width / 4;
      Device.jtp().forEach(bodies.length, body -> {
         final float theta = (float) (Math.random() * Math.PI * 2);
         final float phi = (float) (Math.random() * Math.PI * 2);
         final float radius = (float) (Math.random() * maxDist);
         bodies[body] = new Body();
         bodies[body].x = (float) (radius * Math.cos(theta) * Math.sin(phi)) + width / 2;
         bodies[body].y = (float) (radius * Math.sin(theta) * Math.sin(phi)) + height / 2;
         bodies[body].z = (float) (radius * Math.cos(phi));
      });
     }  else{
      int side = (int)Math.sqrt(bodies.length)+1;
      int spread = width/side;
      System.out.println("side "+side);
       System.out.println("spread "+spread);

       int x=0;
       int y=0;
       for (int body = 0; body < bodies.length; body++){
           x += spread;
           if (x>width){
               x = 0;
               y+=spread;
           }
          bodies[body]=new Body();
          bodies[body].x = x;
          bodies[body].y = y;
          bodies[body].z = 0;
       }
       }

      JComponent viewer = new JComponent(){
      };

      viewer.setPreferredSize(new Dimension(width, height));
      jframe.getContentPane().add(viewer);
      jframe.pack();
      jframe.setVisible(true);
      Device device = Device.jtp();


      jframe.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
      for(frame = 0; frame < 10000; frame++){
          int[] rgbCopy = rgb;
          Arrays.fill(rgbCopy, 0);
          int w = width;
          int h = height;
          int max = bodies.length;
         device.forEach(max, gid -> {
            Body thisBody = bodies[gid];
            float accx = 0.f;
            float accy = 0.f;
            float accz = 0.f;
            for(int i = 0; i < max; i++){
               final float dx = bodies[i].x - thisBody.x;
               final float dy = bodies[i].y -  thisBody.y;
               final float dz = bodies[i].z -  thisBody.z;
               final float invDist = 1f / (float) Math.sqrt(((dx * dx) + (dy * dy) + (dz * dz) + espSqr));
               accx += mass * invDist * invDist * invDist * dx;
               accy += mass * invDist * invDist * invDist * dy;
               accz += mass * invDist * invDist * invDist * dz;
            }
            accx *= delT;
            accy *= delT;
            accz *= delT;

            thisBody.x += bodies[gid].vx * delT + (accx * .5f * delT);
            thisBody.y += bodies[gid].vy * delT + (accy * .5f * delT);
            thisBody.z += bodies[gid].vz * delT + (accz * .5f * delT);

            thisBody.vx +=  accx;
            thisBody.vy +=  accy;
            thisBody.vz +=  accz;
             int x =  (int)thisBody.x;
             int y =  (int)thisBody.y;
             if (x<w-1 && y<h-1 && x>0 && y>0){
                 rgbCopy[x+y*w]=0xffffff;
                 rgbCopy[x+1+y*w]=0xffffff;
                 rgbCopy[x-1+y*w]=0xffffff;
                 rgbCopy[x+(y+1)*w]=0xffffff;
                 rgbCopy[x+(y-1)*w]=0xffffff;
             }


         });

          viewer.getGraphics().drawImage(offscreen, 0, 0, null);
      }
   }


}
