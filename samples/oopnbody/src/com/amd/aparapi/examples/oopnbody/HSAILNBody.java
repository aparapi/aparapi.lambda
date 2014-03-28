package com.amd.aparapi.examples.oopnbody ;

import com.amd.aparapi.Device;
import com.amd.aparapi.HSADevice;
import com.amd.aparapi.sample.common.PixelRenderer;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;


public class HSAILNBody {

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

       void draw(PixelRenderer _pr){
           int px =  (int)x;
           int py =  (int)y;
           _pr.cross(px, py, 0xffffff);
       }

       void updatePosition(Body[] bodies){
           Body thisBody = this;
           float accx = 0.f;
           float accy = 0.f;
           float accz = 0.f;
           for(int i = 0; i < bodies.length; i++){
               Body otherBody = bodies[i];
              if (otherBody == null) {
                  otherBody=otherBody;
              }else{
                   if (this != otherBody) {
                       float dx = otherBody.x - thisBody.x;
                       float dy = otherBody.y - thisBody.y;
                       float dz = otherBody.z - thisBody.z;
                       float dist = (float) Math.sqrt(((dx * dx) + (dy * dy) + (dz * dz) + .1f /* +.1f in case dx,dy,dz are 0!*/));
                       float invDist = 1f / dist;
                       float massInvDist_3 = otherBody.mass * invDist * invDist * invDist;
                       accx += massInvDist_3 * dx;
                       accy += massInvDist_3 * dy;
                       accz += massInvDist_3 * dz;
                   }
               }
           }
           float delT = .05f;
           float delT_2 = delT/2;
           accx *= delT;
           accy *= delT;
           accz *= delT;
           thisBody.x += thisBody.vx * delT + accx * delT_2;
           thisBody.y += thisBody.vy * delT + accy * delT_2;
           thisBody.z += thisBody.vz * delT + accz * delT_2;
           thisBody.vx += accx;
           thisBody.vy += accy;
           thisBody.vz += accz;
       }

   }



   public static void main(String[] _args){
       (new HSAILNBody()).go(Device.getByName(System.getProperty("mode", "hsa")), Integer.getInteger("bodies", 4096));
   }

   void go(Device device, int bodyCount ){


      PixelRenderer pr = new PixelRenderer(Integer.getInteger("width", 1024),  Integer.getInteger("height", 1024));
      JFrame jframe = new JFrame("NBody");
      Body[] bodies = new Body[bodyCount];
      Device.jtp().forEach(bodies.length, body -> {
           bodies[body] = new Body();
       });

       Device.hsa().forEach(bodies.length, bodyId -> {
           bodies[bodyId].init(pr.width, pr.height, bodyId);
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
