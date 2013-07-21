/*
   Copyright (c) 2010-2011, Advanced Micro Devices, Inc.
   All rights reserved.

   Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
   following conditions are met:

   Redistributions of source code must retain the above copyright notice, this list of conditions and the following
   disclaimer. 

   Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
   disclaimer in the documentation and/or other materials provided with the distribution. 

   Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products
   derived from this software without specific prior written permission. 

   THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
   INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
   DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
   SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
   SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
   WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE 
   OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

   If you use the software (in whole or in part), you shall adhere to all applicable U.S., European, and other export
   laws, including but not limited to the U.S. Export Administration Regulations ("EAR"), (15 C.F.R. Sections 730 through
   774), and E.U. Council Regulation (EC) No 1334/2000 of 22 June 2000.  Further, pursuant to Section 740.6 of the EAR,
   you hereby certify that, except pursuant to a license granted by the United States Department of Commerce Bureau of 
   Industry and Security or as otherwise permitted pursuant to a License Exception under the U.S. Export Administration 
   Regulations ("EAR"), you will not (1) export, re-export or release to a national of a country in Country Groups D:1,
E:1 or E:2 any restricted technology, software, or source code you receive hereunder, or (2) export to Country Groups
D:1, E:1 or E:2 the direct product of such technology or software, if such foreign produced direct product is subject
to national security controls as identified on the Commerce Control List (currently found in Supplement 1 to Part 774
of EAR).  For the most current Country Group listings, or for additional information about the EAR or your obligations
under those regulations, please refer to the U.S. Bureau of Industry and Security's website at http://www.bis.doc.gov/. 
 */

package com.amd.aparapi.examples.nbody;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLException;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.fixedfunc.GLLightingFunc;
import javax.media.opengl.glu.GLU;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import com.amd.aparapi.Kernel;
import com.amd.aparapi.ProfileInfo;
import com.amd.aparapi.Range;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;

/**
 * NBody implementing demonstrating Aparapi kernels.
 * 
 * For a description of the NBody problem.
 * 
 * @see http://en.wikipedia.org/wiki/N-body_problem
 * 
 *      We use JOGL to render the bodies.
 * @see http://jogamp.org/jogl/www/
 * 
 * @author gfrost
 * 
 */
public class Main {

   public class NBodyKernel extends Kernel {
      protected final float delT = .005f;

      protected final float espSqr = 1.0f;

      protected final float mass = 5f;

      private final Range range;

      private final float[] xyz; // positions xy and z of bodies

      private final float[] vxyz; // velocity component of x,y and z of bodies

      /**
       * Constructor initializes xyz and vxyz arrays.
       * 
       * @param _bodies
       */
      public NBodyKernel(Range _range) {
         range = _range;
         // range = Range.create(bodies);
         xyz = new float[range.getGlobalSize(0) * 3];
         vxyz = new float[range.getGlobalSize(0) * 3];
         final float maxDist = 20f;
         for (int body = 0; body < (range.getGlobalSize(0) * 3); body += 3) {

            final float theta = (float) (Math.random() * Math.PI * 2);
            final float phi = (float) (Math.random() * Math.PI * 2);
            final float radius = (float) (Math.random() * maxDist);

            // get the 3D dimensional coordinates
            xyz[body + 0] = (float) (radius * Math.cos(theta) * Math.sin(phi));
            xyz[body + 1] = (float) (radius * Math.sin(theta) * Math.sin(phi));
            xyz[body + 2] = (float) (radius * Math.cos(phi));

            // divide into two 'spheres of bodies' by adjusting x

            //if ((body % 2) == 0) {
            //   xyz[body + 0] += maxDist * 1.5;
           // } else {
          //     xyz[body + 0] -= maxDist * 1.5;
          ////  }
         }
         setExplicit(true);
      }

      /**
       * Here is the kernel entrypoint. Here is where we calculate the position of each body
       */
      @Override
         public void run() {
            final int body = getGlobalId();
            final int count = getGlobalSize(0) * 3;
            final int globalId = body * 3;

            float accx = 0.f;
            float accy = 0.f;
            float accz = 0.f;

            final float myPosx = xyz[globalId + 0];
            final float myPosy = xyz[globalId + 1];
            final float myPosz = xyz[globalId + 2];
            for (int i = 0; i < count; i += 3) {
               final float dx = xyz[i + 0] - myPosx;
               final float dy = xyz[i + 1] - myPosy;
               final float dz = xyz[i + 2] - myPosz;
               final float invDist = rsqrt((dx * dx) + (dy * dy) + (dz * dz) + espSqr);
               final float s = mass * invDist * invDist * invDist;
               accx = accx + (s * dx);
               accy = accy + (s * dy);
               accz = accz + (s * dz);
            }
            accx = accx * delT;
            accy = accy * delT;
            accz = accz * delT;
            xyz[globalId + 0] = myPosx + (vxyz[globalId + 0] * delT) + (accx * .5f * delT);
            xyz[globalId + 1] = myPosy + (vxyz[globalId + 1] * delT) + (accy * .5f * delT);
            xyz[globalId + 2] = myPosz + (vxyz[globalId + 2] * delT) + (accz * .5f * delT);

            vxyz[globalId + 0] = vxyz[globalId + 0] + accx;
            vxyz[globalId + 1] = vxyz[globalId + 1] + accy;
            vxyz[globalId + 2] = vxyz[globalId + 2] + accz;
         }

      /**
       * Render all particles to the OpenGL context
       *
       * we need to rotate the texture to face the camera
       * http://fivedots.coe.psu.ac.th/~ad/jg2/ch16/jogl2.pdf     
       * 
       * @param gl
       */

      protected void render(GL2 gl) {
         gl.glBegin(GL2.GL_QUADS);

         for (int i = 0; i < (range.getGlobalSize(0) * 3); i += 3) {
            float x = xyz[i + 0];
            float y = xyz[i + 1];
            float z = xyz[i + 2];
            gl.glTexCoord2f(0, 1); gl.glVertex3f(x, y + 1, z);
            gl.glTexCoord2f(0, 0); gl.glVertex3f(x,y,z);
            gl.glTexCoord2f(1, 0); gl.glVertex3f(x + 1, y, z);
            gl.glTexCoord2f(1, 1); gl.glVertex3f(x + 1, y + 1, z);
         }
         gl.glEnd();
      }

   }


   public class Camera extends KeyAdapter{

      private float xLookAt, yLookAt, zLookAt;
      private float xStep, yStep, zStep;

      private float xeye = 0f;

      private float yeye = 0f;

      private float zeye = 100f;

      private float xat = 0f;

      private float yat = 0f;

      private float zat = 0f;

      private float pitchAngle = -90f;

      private float yawAngle = -90f;

      private final static float LOOK_AT_DIST = 30.0f;

      private final static float SPEED = 0.2f; // for camera movement
      private final static float ANGLE_INCR = 2.0f; // degrees
      private final static float HEIGHT_STEP = 1.0f;
      Camera(){
         xeye = 0f; yeye = 0f; zeye = LOOK_AT_DIST; // camera posn
         yawAngle = -90.0f; // along -z axis
         xStep = (float)Math.cos( Math.toRadians(yawAngle)); // step distances
         zStep = (float)Math.sin( Math.toRadians(yawAngle));
         yStep = (float)Math.sin( Math.toRadians(pitchAngle));
         xLookAt = xeye + (LOOK_AT_DIST * xStep); // look-at posn
         xLookAt = yeye + (LOOK_AT_DIST * yStep); // look-at posn
         zLookAt = zeye + (LOOK_AT_DIST * zStep);
      }

      @Override public void keyPressed(KeyEvent e){
         int keyCode = e.getKeyCode();
         if (keyCode == KeyEvent.VK_LEFT) { 
            if (e.isControlDown()) { 
               xeye += zStep * SPEED;
               zeye -= xStep * SPEED;
            }else{ 
               yawAngle -= ANGLE_INCR;
               xStep = (float)Math.cos(Math.toRadians(yawAngle));
               zStep = (float)Math.sin(Math.toRadians(yawAngle));
               System.out.println("yawAngle = "+yawAngle);
            }     
         } else if (keyCode == KeyEvent.VK_RIGHT) { 
            if (e.isControlDown()) { 
               xeye -= zStep * SPEED;
               zeye += xStep * SPEED;
            }else{
               yawAngle += ANGLE_INCR;
               xStep = (float)Math.cos(Math.toRadians(yawAngle));
               zStep = (float)Math.sin(Math.toRadians(yawAngle));
               System.out.println("yawAngle = "+yawAngle);
            }
         }else if (keyCode == KeyEvent.VK_UP) { 
            if (e.isControlDown()) { 
               yeye-=10f;
            }else{
               yeye-=1f;
            }
         } else if (keyCode == KeyEvent.VK_DOWN) { 
            if (e.isControlDown()) { 
               yeye+=10f;
            }else{
               yeye+=1f;
            }
         }else if (keyCode == KeyEvent.VK_ADD) { 
            if (e.isControlDown()) { 
               zeye-=10f;
            }else{
               zeye-=1f;
            }
         } else if (keyCode == KeyEvent.VK_SUBTRACT) { 
            if (e.isControlDown()) { 
               zeye+=10f;
            }else{
               zeye+=1f;
            }
         }
      }
      void setView(GL2 _gl, int _width, int _height){
         final GLU glu = new GLU();
         glu.gluPerspective(45f, (double) _width / (double) _height , 0f, 1000f);
         glu.gluLookAt(xeye, yeye, zeye , xat, yat, zat, 0f, 1f, 0f);
         //_gl.glRotatef(-1*((float)yawAngle+90.0f), 0, 1, 0); //2
         float[] modelview= new float[16];
         _gl.glGetFloatv(GL2.GL_MODELVIEW_MATRIX, modelview, 0);
         controls.setMatrix(modelview);
         if (false){
            System.out.println("[");
            for (int row=0; row<4; row++){
               System.out.print("{");
               for (int col=0; col<4; col++){
                  System.out.printf(" %5.2f ",modelview[row+col*4]);
               }
               System.out.println("}");
            }
            System.out.println("]");
         }
      }
   }


   Camera camera = new Camera();

   NBodyKernel kernel = new NBodyKernel(Range.create(Integer.getInteger("bodies", 8192)));
   GLCapabilities caps = new GLCapabilities(null);
   GLProfile profile = caps.getGLProfile();

   GLEventListener renderer = new GLEventListener(){
      private int width;

      private int height;
      private Texture texture;

      @Override public void dispose(GLAutoDrawable drawable) {
      }


      @Override public void display(GLAutoDrawable drawable) {

         final GL2 gl = drawable.getGL().getGL2();
         texture.enable(gl);
         texture.bind(gl);
         gl.glLoadIdentity();
         gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
         gl.glColor3f(1f, 1f, 1f);
         camera.setView(gl, width, height);
         if (controls.isRunning()) {
            kernel.execute(kernel.range);
            if (kernel.isExplicit()) {
               kernel.get(kernel.xyz);
            }
            final List<ProfileInfo> profileInfo = kernel.getProfileInfo();
            if ((profileInfo != null) && (profileInfo.size() > 0)) {
               for (final ProfileInfo p : profileInfo) {
                  System.out.print(" " + p.getType() + " " + p.getLabel() + ((p.getEnd() - p.getStart()) / 1000) + "us");
               }
               System.out.println();
            }
         }
         kernel.render(gl);
         gl.glFlush();
         controls.incFrame();

      }

      @Override public void init(GLAutoDrawable drawable) {
         final GL2 gl = drawable.getGL().getGL2();

         gl.glShadeModel(GLLightingFunc.GL_SMOOTH);
         gl.glEnable(GL.GL_BLEND ); 
         gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE);
         gl.glEnable(GL.GL_TEXTURE_2D);
         gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
         gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_NEAREST);
         try {
            final InputStream textureStream = Main.class.getResourceAsStream("particle.jpg");
            TextureData data = TextureIO.newTextureData(profile,textureStream, false, "jpg");
            texture = TextureIO.newTexture(data);
         } catch (final IOException e) {
            e.printStackTrace();
         } catch (final GLException e) {
            e.printStackTrace();
         }

      }

      @Override public void reshape(GLAutoDrawable drawable, int x, int y, int _width, int _height) {
         width = _width;
         height = _height;

         final GL2 gl = drawable.getGL().getGL2();
         gl.glViewport(0, 0, width, height);
      }

   };

   class Controls {
      private int frames;
      private long last = System.currentTimeMillis();
      private boolean running = false;
      private JPanel controlPanel = new JPanel(new FlowLayout());
      private JButton startButton = new JButton("Start");
      private JTextField framesPerSecondTextField = new JTextField("0", 5);
      private JTextField positionUpdatesPerMicroSecondTextField = new JTextField("0", 5);
      private JPanel matrix = new JPanel(new GridLayout(4,4));
      private JLabel[] grid = new JLabel[16];
      Controls(){
         for (int i=0; i<16; i++){
            grid[i] = new JLabel(String.format("%5.2f", (float)i));
         }
         matrix.add(grid[0]); matrix.add(grid[4]); matrix.add(grid[8]); matrix.add(grid[12]);
         matrix.add(grid[1]); matrix.add(grid[5]); matrix.add(grid[9]); matrix.add(grid[13]);
         matrix.add(grid[2]); matrix.add(grid[6]); matrix.add(grid[10]); matrix.add(grid[14]);
         matrix.add(grid[3]); matrix.add(grid[7]); matrix.add(grid[11]); matrix.add(grid[15]);

         startButton.addActionListener(new ActionListener() {
               @Override public void actionPerformed(ActionEvent e) {
               running = true;
               startButton.setEnabled(false);
               }
               });
         controlPanel.add(matrix);
         controlPanel.add(startButton);
         controlPanel.add(new JLabel(kernel.getExecutionMode().toString()));
         controlPanel.add(new JLabel("   Particles"));
         controlPanel.add(new JTextField("" + kernel.range.getGlobalSize(0), 5));
         controlPanel.add(new JLabel("FPS"));
         controlPanel.add(framesPerSecondTextField);
         controlPanel.add(new JLabel("Score("));
         controlPanel.add(new JLabel("<html><small>calcs</small><hr/><small>&micro;sec</small></html>"));
         controlPanel.add(new JLabel(")"));
         controlPanel.add(positionUpdatesPerMicroSecondTextField);
      }
      JPanel getContainer(){
         return(controlPanel);
      }
      void incFrame(){
         long now = System.currentTimeMillis();
         long time = now - last;
         frames++;
         if (time > 1000) { // We update the frames/sec every second
            if (running) {
               final float framesPerSecond = (frames * 1000.0f) / time;
               final int updatesPerMicroSecond = (int) ((framesPerSecond * kernel.range.getGlobalSize(0) * kernel.range
                        .getGlobalSize(0)) / 1000000);
               framesPerSecondTextField.setText(String.format("%5.2f", framesPerSecond));
               positionUpdatesPerMicroSecondTextField.setText(String.format("%4d", updatesPerMicroSecond));
            }
            frames = 0;
            last = now;
         }
      }
      void setMatrix(float[] matrix){
         for (int i=0; i<16; i++){
            grid[i].setText(String.format("%5.2f", matrix[i]));
         }
      }

      boolean isRunning(){
         return(running);
      }

   }

   Controls controls = new Controls();

   public void main() {

      final JFrame frame = new JFrame("NBody");
      final JPanel panel = new JPanel(new BorderLayout());
      panel.add(controls.getContainer(), BorderLayout.NORTH);

      caps.setDoubleBuffered(true);
      caps.setHardwareAccelerated(true);

      final GLCanvas canvas = new GLCanvas(caps);
      canvas.setPreferredSize(new Dimension(Integer.getInteger("width", 1024 ), Integer.getInteger("height", 1024)));
      canvas.addKeyListener(camera);
      canvas.addGLEventListener(renderer);
      panel.add(canvas, BorderLayout.CENTER);
      frame.getContentPane().add(panel, BorderLayout.CENTER);

      frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
      frame.pack();
      frame.setVisible(true);
      (new FPSAnimator(canvas, 100)).start();

   }
   public static void main(String[] args) {
      Main main = new Main();
      main.main();
   }

}
