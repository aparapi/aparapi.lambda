package hsailtest;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;


import com.amd.aparapi.Device;


public class ConvolutionLambda {






       // private byte imageIn[], imageOut[];


    int height;

    int width;

    BufferedImage outputImage;

    BufferedImage inputImage;

    byte[] inBytes;

    byte[] outBytes;

    Graphics2D gc;
    JLabel imageLabel;

    public void processPixel(float _convMatrix3x3[], int x, int y, int w, int h) {
            float accum = 0f;
            int count = 0;
            for (int dx = -3; dx < 6; dx += 3) {
                for (int dy = -1; dy < 2; dy += 1) {
                    int rgb = 0xff & inBytes[((y + dy) * w) + (x + dx)];

                    accum += rgb * _convMatrix3x3[count++];
                }
            }
            byte value = (byte) (Math.max(0, Math.min((int) accum, 255)));
            outBytes[y * w + x] = value;

        }


        public void applyConvolution(float[] _convMatrix3x3, Device _device) {
           long start=System.currentTimeMillis();
            _device.forEach(3 * width * height, i -> {
                int x = i % (width * 3);
                int y = i / (width * 3);

                if (x > 3 && x < (width * 3 - 3) && y > 1 && y < (height - 1)) {
                    processPixel(_convMatrix3x3, x, y, width * 3, height);
                }
            });
            outputImage.getGraphics().drawString(System.currentTimeMillis()-start+" ms",110,110);
            imageLabel.repaint();
        }

        ConvolutionLambda( File _file) throws IOException {

            JFrame frame = new JFrame("Convolution Viewer");

            inputImage = ImageIO.read(_file);

            height = inputImage.getHeight();

            width = inputImage.getWidth();

            outputImage = new BufferedImage(width, height, inputImage.getType());

            gc = outputImage.createGraphics();

            inBytes = ((DataBufferByte) inputImage.getRaster().getDataBuffer()).getData();
            outBytes = ((DataBufferByte) outputImage.getRaster().getDataBuffer()).getData();

            imageLabel = new JLabel();
            imageLabel.setIcon(new ImageIcon(outputImage));


            frame.getContentPane().add(imageLabel, BorderLayout.CENTER);
            frame.pack();
            frame.setVisible(true);
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        }


       public static void main(final String[] _args) throws IOException {

           float convMatrix3x3[] = new float[]{
                   0f,
                   -10f,
                   0f,
                   -10f,
                   40f,
                   -10f,
                   0f,
                   -20f,
                   0f,
           };

        ConvolutionLambda convolution = new ConvolutionLambda(  new File("C:\\Users\\user1\\aparapi\\branches\\lambda\\dsotm.jpg"));
        Device device = Device.hsa();
        for (int i=0; i<100; i++){
        for (float f = -50f; f<-10f; f+=.05f){
            convMatrix3x3[7]=f;
        convolution.applyConvolution(convMatrix3x3, device);
        }
            for (float f = -10f; f>-50f; f-=.05f){
                convMatrix3x3[7]=f;
                convolution.applyConvolution(convMatrix3x3, device);
            }
        }

    }


}







