package hsailtest;

import com.amd.aparapi.Device;
import java.awt.Color;
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
      float mass;
      int paletteIndex;
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

      int[] palette = new int[]{
         Color.WHITE.getRGB(), 
            Color.YELLOW.brighter().brighter().getRGB(), 
            Color.YELLOW.brighter().getRGB(), 
            Color.YELLOW.getRGB(), 
            Color.YELLOW.darker().getRGB(), 
            Color.YELLOW.darker().darker().getRGB(), 
            Color.ORANGE.brighter().brighter().getRGB(), 
            Color.ORANGE.brighter().getRGB(), 
            Color.ORANGE.getRGB(), 
            Color.ORANGE.darker().getRGB(), 
            Color.ORANGE.darker().darker().getRGB(), 
            Color.PINK.brighter().brighter().getRGB(),
            Color.PINK.brighter().getRGB(),
            Color.PINK.getRGB(),
            Color.PINK.darker().getRGB(),
            Color.PINK.darker().darker().getRGB(),
            Color.RED.brighter().brighter().getRGB(),
            Color.RED.brighter().getRGB(),
            Color.RED.getRGB(),
            Color.RED.darker().getRGB(),
            Color.RED.darker().darker().getRGB(),
      };
      float maxDist = width / 4;
      Device.jtp().forEach(bodies.length, body -> {
         float theta = (float) (Math.random() * Math.PI * 2);
         float phi = (float) (Math.random() * Math.PI * 2);
         float radius = (float) (Math.random() * maxDist);
         bodies[body] = new Body();
         bodies[body].x = (float) (radius * Math.cos(theta) * Math.sin(phi)) + width / 2;
         bodies[body].y = (float) (radius * Math.sin(theta) * Math.sin(phi)) + height / 2;
         bodies[body].z = (float) (radius * Math.cos(phi));
         bodies[body].mass = (float)(Math.random()*20f+10f);
         bodies[body].paletteIndex = 0;
      });

      JComponent viewer = new JComponent(){
      };

      viewer.setPreferredSize(new Dimension(width, height));
      jframe.getContentPane().add(viewer);
      jframe.pack();
      jframe.setVisible(true);

      jframe.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
      long first = System.currentTimeMillis();
      float fps  =0;
      while(true){
         Arrays.fill(rgb, 0);
             offscreen.getGraphics().setColor(Color.WHITE);
             offscreen.getGraphics().drawString(String.format("%5.2f\n",fps), 100, 100);
         device.forEach(bodies.length, gid -> {
            Body thisBody = bodies[gid];
            float accx = 0.f;
            float accy = 0.f;
            float accz = 0.f;
            for(int i = 0; i < bodies.length; i++){
               if (gid != i){
                  Body otherBody = bodies[i];
               //if (thisBody != otherBody){ 
                  float dx = otherBody.x - thisBody.x;
                  float dy = otherBody.y - thisBody.y;
                  float dz = otherBody.z - thisBody.z;
                  float dist =  (float) Math.sqrt(((dx * dx) + (dy * dy) + (dz * dz) + .1f /* +.1f in case dx,dy,dz are 0!*/));
                  float invDist = 1f / dist;
                  float massInvDist_3 = otherBody.mass * invDist * invDist * invDist;
                  if (dist<0.6f){
                     //accx -= massInvDist_3 * dx;
                     //accy -= massInvDist_3 * dy;
                     //accz -= massInvDist_3 * dz;
                     thisBody.mass+=10f;
                     if (thisBody.paletteIndex<(palette.length-1)){
                        thisBody.paletteIndex++;
                     }
                  }else{
                     accx += massInvDist_3 * dx;
                     accy += massInvDist_3 * dy;
                     accz += massInvDist_3 * dz;
                  }
               }
            }
            float delT = .05f;
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
            if (x>1&&x<width-1&&y>1&&y<height-1){
               int color = palette[thisBody.paletteIndex];
               rgb[x-1+y*width]=color;
               rgb[x+y*width]=color;
               rgb[x+1+y*width]=color;
               rgb[x+(y-1)*width]=color;
               rgb[x+(y+1)*width]=color;
            }
         });
         long delta = System.currentTimeMillis()-first;
         frame+=1;
         if (delta > 1000){
             fps =(frame*1000)/delta; 
            
            first = System.currentTimeMillis() ;
            frame=0;
         }
         viewer.getGraphics().drawImage(offscreen, 0, 0, null);
      }
   }
}
