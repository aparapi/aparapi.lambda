package hsailtest;

import com.amd.aparapi.Device;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Arrays;


public class NBodyFunc {

   public class Body{
      float x,y,z;
      float vx,vy,vz;
      float mass;
      Body(int width, int height){
          float maxDist = width / 2;
          float theta = (float) (Math.random() * Math.PI * 2);
          float phi = (float) (Math.random() * Math.PI * 2);
          float radius = (float) (Math.random() * maxDist);
          x = (float) (radius * Math.cos(theta) * Math.sin(phi)) + width / 2;
          y = (float) (radius * Math.sin(theta) * Math.sin(phi)) + height / 2;
          z = (float) (radius * Math.cos(phi));
          mass = (float)(Math.random()*20f+10f);
      }



       void updatePosition(Body[] bodies){
           float accx = 0.f;
           float accy = 0.f;
           float accz = 0.f;
           for(int i = 0; i < bodies.length; i++){
               Body other = bodies[i];
               if (this != other){
                   float dx = other.x-x;
                   float dy = other.y-y;
                   float dz = other.z-z;
                   float dist =  (float) Math.sqrt(((dx * dx) + (dy * dy) + (dz * dz) + .1f /* +.1f in case dx,dy,dz are 0!*/));
                   float invDist = 1f / dist;
                   float massInvDist_3 = other.mass * invDist * invDist * invDist;
                   accx += massInvDist_3 * dx;
                   accy += massInvDist_3 * dy;
                   accz += massInvDist_3 * dz;
               }
           }
           float delT = .05f;
           float delT_2 = delT/2;
           accx *= delT;
           accy *= delT;
           accz *= delT;
           x += vx * delT + accx * delT_2;
           y += vy * delT + accy * delT_2;
           z += vz * delT + accz * delT_2;
           vx+=accx;
           vy+=accy;
           vz+=accz;
       }
       void setPixel(int[] offscreenPixels, int width, int height, int x, int y,int rgb){
           if (x>=0 && x<width && y>=0 && y<height){
               offscreenPixels[x+y*width]=rgb;
           }
       }

       void setPixel(int[] offscreenPixels, int width, int height){
           int px =  (int)x;
           int py =  (int)y;
           int rgb = 0xffffff;
           setPixel(offscreenPixels, width, height, px-1, py, rgb);
           setPixel(offscreenPixels, width, height, px, py, rgb);
           setPixel(offscreenPixels, width, height, px+1, py, rgb);
           setPixel(offscreenPixels, width, height, px, py-1, rgb);
           setPixel(offscreenPixels, width, height, px, py+1, rgb);
       }
   }

   public static void main(String[] _args){
       (new NBodyFunc()).go(Device.getByName(_args[0]), Integer.parseInt(_args[1]));
   }

   void go(Device device, int bodyCount ){
      float frame = 0f;
      int width = Integer.getInteger("width", 1024);
      int height =  Integer.getInteger("height", 1024);
      BufferedImage offscreen = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
      int[] offscreenPixels= ((DataBufferInt) offscreen.getRaster().getDataBuffer()).getData();
      JFrame jframe = new JFrame("NBody");
      Body[] bodies = new Body[bodyCount];
       //Initialize palette values
       int[] palette = new int[16];
       for(int i = 0; i < palette.length; i++){
           float h = i / (float) palette.length;
           float b = 1.0f - h * h *h;
           palette[palette.length-1-i] = Color.HSBtoRGB(h, 1f, b);
       }

      Device.jtp().forEach(bodies.length, body -> {
         bodies[body] = new Body(width, height);
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
         Arrays.fill(offscreenPixels, 0);
         offscreen.getGraphics().setColor(Color.WHITE);
         offscreen.getGraphics().drawString(String.format("%5.2f\n",fps), 100, 100);
         device.forEach(bodies.length, gid -> {
            Body body = bodies[gid];
            body.updatePosition(bodies);
            body.setPixel(offscreenPixels, width, height);
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
