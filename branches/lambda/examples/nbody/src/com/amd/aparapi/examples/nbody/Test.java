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

    private float phi = 0f;
    private float theta = 0f;
    private float radius = 100f;

    private float INC = (float) (Math.PI/18); // 5 deg?

    float originx = 0f;
    float originy = 0f;
    float originz = 0f;

    float cos(float v){
      return((float)Math.cos( v));
    }
    float tan(float v){
      return((float)Math.tan( v));
    }
    float sin(float v){
      return((float)Math.sin( v));
    }

    Camera(){
      xeye = originx + radius*cos(phi)*sin(theta);
      yeye = originy + radius*sin(phi)*sin(theta);
      zeye = originz + radius*cos(theta);
    }

    @Override public void keyPressed(KeyEvent e){
      int keyCode = e.getKeyCode();
      if (e.isControlDown()) { }
      switch (keyCode){
        case KeyEvent.VK_LEFT: phi -= INC; break;
        case KeyEvent.VK_RIGHT: phi += INC; break;
        case KeyEvent.VK_UP: theta -= INC; break;
        case KeyEvent.VK_DOWN: theta += INC; break;
        case '=' : case KeyEvent.VK_ADD: radius += 20f; break;
        case '-' : case KeyEvent.VK_SUBTRACT: radius -= 20f; break;
      }
      xeye = originx + radius*cos(phi)*sin(theta);
      yeye = originy + radius*sin(phi)*sin(theta);
      zeye = originz + radius*cos(theta);
    }

    float getXeye(){return(xeye);}
    float getYeye(){return(yeye);}
    float getZeye(){return(zeye);}
    float getXat(){return(xat);}
    float getYat(){return(yat);}
    float getZat(){return(zat);}
    float getTheta(){return(theta);}
    float getPhi(){return(phi);}

  }


  Camera camera = new Camera();

  GLCapabilities caps = new GLCapabilities(null);
  GLProfile profile = caps.getGLProfile();
  // http://www.lighthouse3d.com/opengl/billboarding/index.php3?billSphe
  float mathsInnerProduct(float[] v,float[] q) {
    return ((v[0] * q[0] + v[1] * q[1] + v[2] * q[2]));
  }


  /* a = b x c */

  void mathsCrossProduct(float[] a,float[] b,float[] c) {
    a[0] = b[1] * c[2] - c[1] * b[2]; 
    a[1] = b[2] * c[0] - c[2] * b[0]; 
    a[2] = b[0] * c[1] - c[0] * b[1];
  }


  /* vector a = b - c, where b and c represent points*/

  void mathsVector(float[] a,float[] b,float[] c){ 
    a[0] = b[0] - c[0];	
    a[1] = b[1] - c[1];	
    a[2] = b[2] - c[2];
  }

  float sqrt(float f){
    return((float)Math.sqrt(f));
  }
  float acos(float f){
    return((float)Math.acos(f));
  }

  void mathsNormalize(float v[]) {
    float d = (sqrt((v[0]*v[0]) + (v[1]*v[1]) + (v[2]*v[2])));
    v[0] = v[0] / d;
    v[1] = v[1] / d;
    v[2] = v[2] / d;
  }


  void billboardBegin(GL2 gl, float camX, float camY, float camZ, float objPosX, float objPosY, float objPosZ) {
    float lookAt[] = new float[3];
    float objToCamProj[] = new float[3];
    float objToCam[] = new float[3];
    float upAux[] = new float[3];
    float modelview[] = new float[16];
    float angleCosine;

    gl.glPushMatrix();

    // objToCamProj is the vector in world coordinates from the 
    // local origin to the camera projected in the XZ plane
    objToCamProj[0] = camX - objPosX ;
    objToCamProj[1] = 0;
    objToCamProj[2] = camZ - objPosZ ;

    // This is the original lookAt vector for the object 
    // in world coordinates
    lookAt[0] = 0;
    lookAt[1] = 0;
    lookAt[2] = 1;


    // normalize both vectors to get the cosine directly afterwards
    mathsNormalize(objToCamProj);

    // easy fix to determine wether the angle is negative or positive
    // for positive angles upAux will be a vector pointing in the 
    // positive y direction, otherwise upAux will point downwards
    // effectively reversing the rotation.

    mathsCrossProduct(upAux,lookAt,objToCamProj);


    // compute the angle
    angleCosine = mathsInnerProduct(lookAt,objToCamProj);


    // perform the rotation. The if statement is used for stability reasons
    // if the lookAt and objToCamProj vectors are too close together then 
    // |angleCosine| could be bigger than 1 due to lack of precision
    if ((angleCosine < 0.99990) && (angleCosine > -0.9999))
      gl.glRotatef(acos(angleCosine)*180f/((float)Math.PI),upAux[0], upAux[1], upAux[2]);	

    // so far it is just like the cylindrical billboard. The code for the 
    // second rotation comes now
    // The second part tilts the object so that it faces the camera

    // objToCam is the vector in world coordinates from 
    // the local origin to the camera
    objToCam[0] = camX - objPosX;
    objToCam[1] = camY - objPosY;
    objToCam[2] = camZ - objPosZ;

    // Normalize to get the cosine afterwards
    mathsNormalize(objToCam);

    // Compute the angle between objToCamProj and objToCam, 
    //i.e. compute the required angle for the lookup vector

    angleCosine = mathsInnerProduct(objToCamProj,objToCam);


    // Tilt the object. The test is done to prevent instability 
    // when objToCam and objToCamProj have a very small
    // angle between them

    if ((angleCosine < 0.99990) && (angleCosine > -0.9999))
      if (objToCam[1] < 0)
        gl.glRotatef(acos(angleCosine)*180f/((float)Math.PI),1,0,0);	
      else
        gl.glRotatef(acos(angleCosine)*180f/((float)Math.PI),-1,0,0);	

  }

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

      GLU glu = new GLU();
      glu.gluPerspective(45f, (double) width / (double) height , 0f, 1000f);
      glu.gluLookAt(camera.getXeye(), camera.getYeye(), camera.getZeye() , camera.getXat(), camera.getYat(), camera.getZat(), 0f, 1f, 0f);
      float[] modelview= new float[16];
      gl.glGetFloatv(GL2.GL_MODELVIEW_MATRIX, modelview, 0);
      controls.setMatrix(modelview);
      controls.setTheta(camera.getTheta());
      controls.setPhi(camera.getPhi());


      gl.glPushMatrix();{
        if (false){
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
          //gl.glLoadMatrixf(modelview, 0);
        }
        gl.glBegin(GL2.GL_QUADS);{
          for (int x = 0; x <3; x++){
            for (int y = 0; y <3; y++){
              for (int z = 0; z <3; z++){
                gl.glColor3f((float)x*.3f+.1f, (float)y*.3f+.1f, (float)z*.3f+.1f);

                float xcenter = (x-1)*20;
                float ycenter = (y-1)*20;
                float zcenter = (z-1)*20;
                //float camX =modelview[12];
                //float camY =modelview[13];
                //float camZ =modelview[14];
                float camX =camera.getXeye();
                float camY =camera.getYeye();
                float camZ =camera.getZeye();

                System.out.println("cam x,y,z = "+camX+", "+camY+", "+camZ);
                billboardBegin(gl, camX, camY, camZ, xcenter, ycenter, zcenter) ;

                if (false){
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
                }else{
                  gl.glTexCoord2f(0, 0); gl.glVertex3f(-10,-10,  0);
                  gl.glTexCoord2f(1, 0); gl.glVertex3f(10, -10, 0);
                  gl.glTexCoord2f(1, 1); gl.glVertex3f(10, 10, 0);
                  gl.glTexCoord2f(0, 1); gl.glVertex3f(-10, +10, 0); 
                }
                gl.glPopMatrix();
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
    private JTextField framesPerSecondTextField = new JTextField("0", 5);
    private JTextField thetaField = new JTextField("0", 5);
    private JTextField phiField = new JTextField("0", 5);
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
      controlPanel.add(matrix);
      controlPanel.add(new JLabel("    theta:"));
      controlPanel.add(thetaField);
      controlPanel.add(new JLabel("    phi:"));
      controlPanel.add(phiField);
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

    void setTheta(float _theta){
      thetaField.setText(String.format("%5.2f", _theta));
    }
    void setPhi(float _phi){
      phiField.setText(String.format("%5.2f", _phi));
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
