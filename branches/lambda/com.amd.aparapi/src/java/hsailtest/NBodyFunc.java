package hsailtest;

import com.amd.aparapi.Device;
import com.amd.aparapi.HSADevice;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;


public class NBodyFunc {

   public class Body{
      float x,y,z;
      float vx,vy,vz;
      float mass;

      void init(int width, int height, int id){
          float scaledId = ((float)id)/width;
          float theta =  (float)(scaledId * Math.PI * 2);
          float phi =  (float)(scaledId *  Math.PI * 2);
          float radius = scaledId * width / 2;
          mass = (float)(scaledId*20f+10f);
          x = (float) (radius * Math.cos(theta) * Math.sin(phi)) + width / 2;
          y = (float) (radius * Math.sin(theta) * Math.sin(phi)) + height / 2;
          z = (float) (radius * Math.cos(phi));
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
           vx += accx;
           vy += accy;
           vz += accz;
       }


       void draw(PixelRenderer _pr){
           int px =  (int)x;
           int py =  (int)y;

           int rgb = 0xffffff;
           _pr.x(px, py, rgb);

       }
   }



   public static void main(String[] _args){
       (new NBodyFunc()).go(Device.getByName(_args[0]), Integer.parseInt(_args[1]));
   }

   void go(Device device, int bodyCount ){


      PixelRenderer pr = new PixelRenderer(Integer.getInteger("width", 1024),  Integer.getInteger("height", 1024));
      JFrame jframe = new JFrame("NBody");
      Body[] bodies = new Body[bodyCount];
      Device.jtp().forEach(bodies.length, body -> {
           bodies[body] = new Body();
       });

       Device.hsa().forEach(bodies.length, body -> {
           bodies[body].init(pr.width, pr.height, body);
       });


      jframe.getContentPane().add(pr.component);
      jframe.pack();
      jframe.setVisible(true);

      jframe.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

      while(true){
         pr.clear();
         device.forEach(bodies.length, gid -> {
            Body body = bodies[gid];
            body.updatePosition(bodies);
            body.draw(pr);
         });
         pr.sync();
      }
   }
}
