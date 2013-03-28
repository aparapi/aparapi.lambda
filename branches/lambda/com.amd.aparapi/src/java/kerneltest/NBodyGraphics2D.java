package kerneltest;

import com.amd.aparapi.Kernel;
import com.amd.aparapi.Range;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.WindowConstants;


public class NBodyGraphics2D{
   public static class NBodyKernel extends Kernel{

      final private int width;
      final private int height;
      final float delT = .005f;
      final float espSqr = 1.0f;
      final float mass = 5f;


      private final Range range;
      private final float[] xyz; // positions xy and z of bodies
      private final float[] vxyz; // velocity component of x,y and z of bodies
      volatile boolean done = false;

      public NBodyKernel(int _width, int _height, int _depth, int _bodies){

         width = _width;
         height = _height;

         range = Range.create(_bodies);
         xyz = new float[range.getGlobalSize(0) * 3];
         vxyz = new float[range.getGlobalSize(0) * 3];
         final float maxDist = width / 4;
         for(int body = 0; body < (range.getGlobalSize(0) * 3); body += 3){
            final float theta = (float) (Math.random() * Math.PI * 2);
            final float phi = (float) (Math.random() * Math.PI * 2);
            final float radius = (float) (Math.random() * maxDist);
            xyz[body + 0] = (float) (radius * Math.cos(theta) * Math.sin(phi)) + width / 2;
            xyz[body + 1] = (float) (radius * Math.sin(theta) * Math.sin(phi)) + height / 2;
            xyz[body + 2] = (float) (radius * Math.cos(phi));
         }
         setExplicit(true);
      }

      public void run(){
         //final int body = getGlobalId();
         final int count = getGlobalSize(0) * 3;
         final int globalId = getGlobalId() * 3;
         float accx = 0.f;
         float accy = 0.f;
         float accz = 0.f;
         for(int i = 0; i < count; i += 3){
            final float dx = xyz[i + 0] - xyz[globalId + 0];
            final float dy = xyz[i + 1] - xyz[globalId + 1];
            final float dz = xyz[i + 2] - xyz[globalId + 2];
            final float invDist = rsqrt((dx * dx) + (dy * dy) + (dz * dz) + espSqr);
            accx += mass * invDist * invDist * invDist * dx;
            accy += mass * invDist * invDist * invDist * dy;
            accz += mass * invDist * invDist * invDist * dz;
         }
         accx *= delT;
         accy *= delT;
         accz *= delT;
         xyz[globalId + 0] = xyz[globalId + 0] + (vxyz[globalId + 0] * delT) + (accx * .5f * delT);
         xyz[globalId + 1] = xyz[globalId + 1] + (vxyz[globalId + 1] * delT) + (accy * .5f * delT);
         xyz[globalId + 2] = xyz[globalId + 2] + (vxyz[globalId + 2] * delT) + (accz * .5f * delT);

         vxyz[globalId + 0] = vxyz[globalId + 0] + accx;
         vxyz[globalId + 1] = vxyz[globalId + 1] + accy;
         vxyz[globalId + 2] = vxyz[globalId + 2] + accz;
      }

      void next(){
         execute(range);
         done = true;

      }
   }

   static int fps = 0;
   static NBodyKernel kernel;

   public static void main(String[] _args){
      JFrame frame = new JFrame("NBody");
      final int width = 768;
      final int height = 768;
      final int bodies = 512;
      JComponent viewer = new JComponent(){
         @Override public void paintComponent(Graphics _g){
            Graphics2D g = (Graphics2D) _g;
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, width, height);
            g.setColor(Color.WHITE);

            if(kernel.done){

               kernel.get(kernel.xyz);
               for(int body = 0; body < bodies * 3; body += 3){
                  float x = kernel.xyz[body];
                  float y = kernel.xyz[body + 1];
                  g.fillOval((int) x, (int) y, (int) 10, (int) 10);
               }
               g.setColor(Color.WHITE);
               g.drawString("" + fps, 100, 100);
            }
         }
      };
      viewer.setPreferredSize(new Dimension(width, height));
      frame.getContentPane().add(viewer);
      frame.pack();
      frame.setVisible(true);
      kernel = new NBodyKernel(width, height, width, bodies);
      kernel.next();
      viewer.repaint();
      frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
      while(true){
         fps++;
         kernel.next();
         viewer.repaint();
      }
   }


}
