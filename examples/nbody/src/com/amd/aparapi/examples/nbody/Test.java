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
public class Test {

   public class Camera extends KeyAdapter{

      private float xeye = 0f;

      private float yeye = 0f;

      private float zeye = 0f;

      private float xat = 0f;

      private float yat = 0f;

      private float zat = 0f;

      private float phi = (float)-Math.PI;
      private float theta = (float)-Math.PI;
      private float radius = 30f;

      float originx = 0f;
      float originy = 0f;
      float originz = 0f;

      float cos(float v){
         return((float)Math.cos( v));
      }
      float sin(float v){
         return((float)Math.sin( v));
      }


      void delta(){
           xeye = originx + radius*cos(phi)*sin(theta);
           yeye = originy + radius*sin(phi)*sin(theta);
           zeye = originz + radius*cos(theta);
      }


      Camera(){
         delta();
      }

      @Override public void keyPressed(KeyEvent e){
         int keyCode = e.getKeyCode();
         if (keyCode == KeyEvent.VK_LEFT) { 
            if (e.isControlDown()) { 
            }else{ 
               phi -= 0.1f;
               delta();
            }     
         } else if (keyCode == KeyEvent.VK_RIGHT) { 
            if (e.isControlDown()) { 
            }else{
               phi += 0.1f;
               delta();
            }

            /* if (keyCode == KeyEvent.VK_LEFT) { 
               if (e.isControlDown()) { 
            //  xat-=10f;
            xeye-=10f;
            }else{
            //  xat-=1f;
            xeye-=1f;
            }
            } else if (keyCode == KeyEvent.VK_RIGHT) { 
            if (e.isControlDown()) { 
            //  xat+=10f;
            xeye+=10f;
            }else{
            //  xat+=1f;
            xeye+=1f;
            } */
         }else if (keyCode == KeyEvent.VK_UP) { 
            if (e.isControlDown()) { 
               //yeye-=10f;
            }else{
               theta -= 0.1f;
               delta();
            }
         } else if (keyCode == KeyEvent.VK_DOWN) { 
            if (e.isControlDown()) { 
            }else{
               theta += 0.1f;
               delta();
            }
         }else if (keyCode == KeyEvent.VK_ADD) { 
            if (e.isControlDown()) { 
            }else{
               radius +=20f;
               delta();
            }
         } else if (keyCode == KeyEvent.VK_SUBTRACT) { 
            if (e.isControlDown()) { 
            }else{
               radius-=20f;
               delta();
            }
         }
         }
         void setView(GL2 _gl, int _width, int _height){
            final GLU glu = new GLU();
            glu.gluPerspective(45f, (double) _width / (double) _height , 0f, 1000f);
            glu.gluLookAt(xeye, yeye, zeye , xat, yat, zat, 0f, 1f, 0f);
           // _gl.glRotatef(-1*((float)phi+90.0f), 0, 1, 0); //2

            float[] modelview= new float[16];
            _gl.glGetFloatv(GL2.GL_MODELVIEW_MATRIX, modelview, 0);
            controls.setMatrix(modelview);
         }
      }


      Camera camera = new Camera();

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
            camera.setView(gl, width, height);

            float[] modelview= new float[16];
            gl.glGetFloatv(GL2.GL_MODELVIEW_MATRIX, modelview, 0);
            gl.glPushMatrix();{
               //gl.glMatrixMode(GL2.GL_MODELVIEW_MATRIX);
               System.out.println("[");
               for (int row=0; row<4; row++){
                  System.out.print("{");
                  for (int col=0; col<4; col++){
                     System.out.printf(" %5.2f ",modelview[row*4+col]);
                  }
                  System.out.println("}");
               }
               System.out.println("]");
               for(int i=0; i<3; i++ ) {
                  for(int j=0; j<3; j++ ) {
                     if ( i==j )
                        modelview[i*4+j] = 1f;
                     else
                        modelview[i*4+j] = 0f;
                  }
               }
               System.out.println("[");
               for (int row=0; row<4; row++){
                  System.out.print("{");
                  for (int col=0; col<4; col++){
                     System.out.printf(" %5.2f ",modelview[row*4+col]);
                  }
                  System.out.println("}");
               }
                   // gl.glLoadMatrixf(modelview, 0);
               gl.glBegin(GL2.GL_QUADS);{
                  for (int x = 0; x <3; x++){
                     for (int y = 0; y <3; y++){
                        for (int z = 0; z <3; z++){
                           gl.glColor3f((float)x*.3f+.1f, (float)y*.3f+.1f, (float)z*.3f+.1f);
                           float xcenter = (x-1)*20;
                           float ycenter = (y-1)*20;
                           float zcenter = (z-1)*20;

                           if (false){

                              gl.glTexCoord2f(0, 0); gl.glVertex3f(xcenter-10,ycenter-10,  zcenter);
                              gl.glTexCoord2f(0, 1); gl.glVertex3f(xcenter-10, ycenter+10, zcenter); 
                              gl.glTexCoord2f(1, 1); gl.glVertex3f(xcenter+10, ycenter+10, zcenter);
                              gl.glTexCoord2f(1, 0); gl.glVertex3f(xcenter+10, ycenter-10, zcenter);
                           }else{
                              gl.glTexCoord2f(0, 0); gl.glVertex3f(xcenter-10,ycenter-10,  zcenter);
                              gl.glTexCoord2f(1, 0); gl.glVertex3f(xcenter+10, ycenter-10, zcenter);
                              gl.glTexCoord2f(1, 1); gl.glVertex3f(xcenter+10, ycenter+10, zcenter);
                              gl.glTexCoord2f(0, 1); gl.glVertex3f(xcenter-10, ycenter+10, zcenter); 
                           }
                        }
                     }
                  }
               }
               gl.glEnd();
            }
            gl.glPopMatrix();
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
               final InputStream textureStream = Test.class.getResourceAsStream("particle.jpg");
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
                  framesPerSecondTextField.setText(String.format("%5.2f", framesPerSecond));
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
         Test test = new Test();
         test.main();
      }

   }
