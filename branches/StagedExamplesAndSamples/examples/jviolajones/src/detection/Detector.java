package detection;

/**
This project is based on the open source jviolajones project created by Simon
Houllier and is used with his permission. Simon's jviolajones project offers 
a pure Java implementation of the Viola-Jones algorithm.

http://en.wikipedia.org/wiki/Viola%E2%80%93Jones_object_detection_framework

The original Java source code for jviolajones can be found here
http://code.google.com/p/jviolajones/ and is subject to the
gnu lesser public license  http://www.gnu.org/licenses/lgpl.html

Many thanks to Simon for his excellent project and for permission to use it 
as the basis of an Aparapi example.
**/

import java.awt.Rectangle;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

public class Detector{

   HaarCascade haarCascade;

   Detector(HaarCascade _haarCascade) {
      haarCascade = _haarCascade;
   }

   /** Returns the list of detected objects in an image applying the Viola-Jones algorithm.
    * 
    * The algorithm tests, from sliding windows on the image, of variable size, which regions should be considered as searched objects.
    * Please see Wikipedia for a description of the algorithm.
    * @param file The image file to scan.
    * @param baseScale The initial ratio between the window size and the Haar classifier size (default 2).
    * @param scale_inc The scale increment of the window size, at each step (default 1.25).
    * @param increment The shift of the window at each sub-step, in terms of percentage of the window size.
    * @return the list of rectangles containing searched objects, expressed in pixels.
    */
   public List<Rectangle> getFaces(String file, float baseScale, float scale_inc, float increment, int min_neighbors,
         boolean doCannyPruning) {

      try {
         BufferedImage image = ImageIO.read(new File(file));

         return getFaces(image, baseScale, scale_inc, increment, min_neighbors, doCannyPruning);
      } catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }

      return null;

   }

   public List<Rectangle> getFaces(BufferedImage image, float baseScale, float scale_inc, float increment, int min_neighbors,
         final boolean doCannyPruning) {

      final List<Rectangle> ret = new ArrayList<Rectangle>();
      final int width = image.getWidth();
      final int height = image.getHeight();
      final float maxScale = (Math.min((width + 0.f) / haarCascade.width, (height + 0.0f) / haarCascade.height));
      // final int[] imagePixels = new int[width * height];
      final int[] grayImage = new int[width * height];
      int[] img = new int[width * height];
      final int[] squares = new int[width * height];
      final StopWatch allTimer = new StopWatch("All");
      final StopWatch timer = new StopWatch();
      System.out.println(image);

      boolean javagrey = false;
      if (javagrey) {
         timer.start();
         ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
         ColorConvertOp op = new ColorConvertOp(cs, null);
         image = op.filter(image, null);

         // timer.print("greyimage");
         DataBuffer dataBuffer = image.getRaster().getDataBuffer();
         byte[] imagePixels = ((DataBufferByte) dataBuffer).getData();
         boolean local = true;
         if (local) {

            for (int i = 0; i < width; i++) {
               int col = 0;
               int col2 = 0;
               for (int j = i; j < height * width; j += width) {
                  int value = imagePixels[j] & 0xff;
                  if (doCannyPruning)
                     img[j] = value;
                  col += value;
                  grayImage[j] = (i > 0 ? grayImage[j - 1] : 0) + col; // NOT data parallel !
                  col2 += value * value;
                  squares[j] = (i > 0 ? squares[j - 1] : 0) + col2; // NOT data parallel
               }
            }
         } else {
            //  System.out.println(image);
            //  timer.print("greyimage");
            // timer.start();
            for (int i = 0; i < width; i++) {
               int col = 0;
               int col2 = 0;
               for (int j = 0; j < height; j++) {
                  int value = imagePixels[i + j * width] & 0xff;
                  if (doCannyPruning)
                     img[j] = value;
                  col += value;
                  grayImage[i + j * width] = (i > 0 ? grayImage[i - 1 + j * width] : 0) + col; // NOT data parallel !
                  col2 += value * value;
                  squares[i + j * width] = (i > 0 ? squares[i - 1 + j * width] : 0) + col2; // NOT data parallel
               }
            }
         }
         timer.print("grey and squares");

      } else {
         DataBuffer dataBuffer = image.getRaster().getDataBuffer();
         if (dataBuffer instanceof DataBufferByte) {
            // 
            byte[] imagePixels = ((DataBufferByte) dataBuffer).getData();

            timer.start();
            if (false) {

               for (int i = 0; i < width; i++) {
                  int col = 0;
                  int col2 = 0;
                  for (int j = i; j < height * width; j += width) {
                     int red = imagePixels[0 + 3 * j] & 0xff;
                     int green = imagePixels[1 + 3 * j] & 0xff;
                     int blue = imagePixels[2 + 3 * j] & 0xff;
                     int value = (30 * red + 59 * green + 11 * blue) / 100;
                     if (doCannyPruning)
                        img[j] = value;
                     col += value;
                     grayImage[j] = (i > 0 ? grayImage[j - 1] : 0) + col; // NOT data parallel !
                     col2 += value * value;
                     squares[j] = (i > 0 ? squares[j - 1] : 0) + col2; // NOT data parallel
                  }
               }
            } else {

               for (int j = 0; j < height; j++) {

                  int col = 0;
                  int col2 = 0;
                  for (int i = 0; i < width; i++) {
                     int red = imagePixels[0 + 3 * (i + j * width)] & 0xff;
                     int green = imagePixels[1 + 3 * (i + j * width)] & 0xff;
                     int blue = imagePixels[2 + 3 * (i + j * width)] & 0xff;
                     int value = (30 * red + 59 * green + 11 * blue) / 100;
                     if (doCannyPruning)
                        img[(i + j * width)] = value;
                     col += value;
                     grayImage[(i + j * width)] = (j > 0 ? grayImage[(i + j * width) - width] : 0) + col; // NOT data parallel !
                     col2 += value * value;
                     squares[(i + j * width)] = (j > 0 ? squares[(i + j * width) - width] : 0) + col2; // NOT data parallel
                  }
               }

            }

            timer.print("grey and squares byte");

         } else if (dataBuffer instanceof DataBufferInt) {
            //byte[] imagePixels = ((DataBufferByte) dataBuffer).getData();
            int imagePixels[] = null;
            boolean raw = true;
            timer.start();
            if (raw) {
               imagePixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
            } else {
               imagePixels = new int[width * height];
               timer.start();
               for (int i = 0; i < width; i++) {
                  for (int j = 0; j < height; j++) {
                     imagePixels[i + j * width] = image.getRGB(i, j);
                  }
               }

            }

            timer.print("greyscaler int");
            timer.start();
            for (int i = 0; i < width; i++) {
               int col = 0;
               int col2 = 0;
               for (int j = 0; j < height; j++) {

                  int c = imagePixels[i + j * width] & 0xff;
                  int red = (c & 0x00ff0000) >> 16;
                  int green = (c & 0x0000ff00) >> 8;
                  int blue = c & 0x000000ff;
                  int value = (30 * red + 59 * green + 11 * blue) / 100;
                  if (doCannyPruning)
                     img[j] = value;
                  col += value;
                  grayImage[i + j * width] = (i > 0 ? grayImage[i - 1 + j * width] : 0) + col; // NOT data parallel !
                  col2 += value * value;
                  squares[i + j * width] = (i > 0 ? squares[i - 1 + j * width] : 0) + col2; // NOT data parallel
               }
            }

            timer.print("grey and squares int");
         }

      }

      int[] cannyIsh = null;
      if (doCannyPruning) {
         timer.start();
         cannyIsh = getIntegralCanny(img, width, height);
         timer.print("canny pruning");
      }

      final int[] canny = cannyIsh;

      StopWatch faceDetectTimer = new StopWatch("face detection");
      faceDetectTimer.start();

      ExecutorService threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
      for (float scale = baseScale; scale < maxScale; scale *= scale_inc) {
         final int step_f = (int) (scale * haarCascade.width * increment);
         final int size_f = (int) (scale * haarCascade.width);
         final float scale_f = scale;

         for (int i = 0; i < width - size_f; i += step_f) {
            final int i_f = i;
            threadPool.execute(new Runnable(){
               public void run() {
                  for (int j = 0; j < height - size_f; j += step_f) {

                     if (doCannyPruning) {
                        int edges_density = canny[i_f + size_f + (j + size_f) * width] + canny[i_f + (j) * width]
                              - canny[i_f + (j + size_f) * width] - canny[i_f + size_f + (j) * width];
                        int d = edges_density / size_f / size_f;
                        if (d < 20 || d > 100)
                           continue;
                     }

                     Rectangle rectangle = haarCascade.getFeature(grayImage, squares, width, height, i_f, j, scale_f, size_f);
                     if (rectangle != null) {
                        synchronized (ret) {
                           ret.add(rectangle);
                        }
                     }
                  }
               }
            });
         }
      }
      threadPool.shutdown(); // we won't add anymore
      try {
         threadPool.awaitTermination(60, TimeUnit.SECONDS);
      } catch (InterruptedException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }

      faceDetectTimer.stop();
      allTimer.stop();
      return merge(ret, min_neighbors);
   }

   public int[] getIntegralCanny(int[] grayImage, int width, int height) {

      int[] canny = new int[grayImage.length];
      final StopWatch timer = new StopWatch();
      timer.start();
      for (int i = 2; i < width - 2; i++) {
         for (int j = 2; j < height - 2; j++) {
            int sum = 0;
            sum += 2 * grayImage[i - 2 + (j - 2) * width];
            sum += 4 * grayImage[i - 2 + (j - 1) * width];
            sum += 5 * grayImage[i - 2 + (j + 0) * width];
            sum += 4 * grayImage[i - 2 + (j + 1) * width];
            sum += 2 * grayImage[i - 2 + (j + 2) * width];
            sum += 4 * grayImage[i - 1 + (j - 2) * width];
            sum += 9 * grayImage[i - 1 + (j - 1) * width];
            sum += 12 * grayImage[i - 1 + (j + 0) * width];
            sum += 9 * grayImage[i - 1 + (j + 1) * width];
            sum += 4 * grayImage[i - 1 + (j + 2) * width];
            sum += 5 * grayImage[i + 0 + (j - 2) * width];
            sum += 12 * grayImage[i + 0 + (j - 1) * width];
            sum += 15 * grayImage[i + 0 + (j + 0) * width];
            sum += 12 * grayImage[i + 0 + (j + 1) * width];
            sum += 5 * grayImage[i + 0 + (j + 2) * width];
            sum += 4 * grayImage[i + 1 + (j - 2) * width];
            sum += 9 * grayImage[i + 1 + (j - 1) * width];
            sum += 12 * grayImage[i + 1 + (j + 0) * width];
            sum += 9 * grayImage[i + 1 + (j + 1) * width];
            sum += 4 * grayImage[i + 1 + (j + 2) * width];
            sum += 2 * grayImage[i + 2 + (j - 2) * width];
            sum += 4 * grayImage[i + 2 + (j - 1) * width];
            sum += 5 * grayImage[i + 2 + (j + 0) * width];
            sum += 4 * grayImage[i + 2 + (j + 1) * width];
            sum += 2 * grayImage[i + 2 + (j + 2) * width];

            canny[i + j * width] = sum / 159;
            //System.out.println(canny[i][j]);
         }
      }
      timer.print("canny convolution");
      timer.start();
      int[] grad = new int[grayImage.length];
      for (int i = 1; i < width - 1; i++) {
         for (int j = 1; j < height - 1; j++) {
            int grad_x = -canny[i - 1 + (j - 1) * width] + canny[i + 1 + (j - 1) * width] - 2 * canny[i - 1 + (j) * width] + 2
                  * canny[i + 1 + (j) * width] - canny[i - 1 + (j + 1) * width] + canny[i + 1 + (j + 1) * width];
            int grad_y = canny[i - 1 + (j - 1) * width] + 2 * canny[i + (j - 1) * width] + canny[i + 1 + (j - 1) * width]
                  - canny[i - 1 + (j + 1) * width] - 2 * canny[i + (j + 1) * width] - canny[i + 1 + (j + 1) * width];
            grad[i + j * width] = Math.abs(grad_x) + Math.abs(grad_y);
            //System.out.println(grad[i][j]);
         }
      }
      timer.print("canny convolution 2");
      timer.start();
      //JFrame f = new JFrame();
      //f.setContentPane(new DessinChiffre(grad));
      //f.setVisible(true);
      for (int i = 0; i < width; i++) {
         int col = 0;
         for (int j = 0; j < height; j++) {
            int value = grad[i + j * width];
            canny[i + j * width] = (i > 0 ? canny[i - 1 + j * width] : 0) + col + value; // NOT data parallel
            col += value;
         }
      }
      timer.print("canny convolution 3");
      return canny;

   }

   public List<Rectangle> merge(List<Rectangle> rects, int min_neighbors) {

      List<Rectangle> retour = new LinkedList<Rectangle>();
      int[] ret = new int[rects.size()];
      int nb_classes = 0;
      for (int i = 0; i < rects.size(); i++) {
         boolean found = false;
         for (int j = 0; j < i; j++) {
            if (equals(rects.get(j), rects.get(i))) {
               found = true;
               ret[i] = ret[j];
            }
         }
         if (!found) {
            ret[i] = nb_classes;
            nb_classes++;
         }
      }
      //System.out.println(Arrays.toString(ret));
      int[] neighbors = new int[nb_classes];
      Rectangle[] rect = new Rectangle[nb_classes];
      for (int i = 0; i < nb_classes; i++) {
         neighbors[i] = 0;
         rect[i] = new Rectangle(0, 0, 0, 0);
      }
      for (int i = 0; i < rects.size(); i++) {
         neighbors[ret[i]]++;
         rect[ret[i]].x += rects.get(i).x;
         rect[ret[i]].y += rects.get(i).y;
         rect[ret[i]].height += rects.get(i).height;
         rect[ret[i]].width += rects.get(i).width;
      }
      for (int i = 0; i < nb_classes; i++) {
         int n = neighbors[i];
         if (n >= min_neighbors) {
            Rectangle r = new Rectangle(0, 0, 0, 0);
            r.x = (rect[i].x * 2 + n) / (2 * n);
            r.y = (rect[i].y * 2 + n) / (2 * n);
            r.width = (rect[i].width * 2 + n) / (2 * n);
            r.height = (rect[i].height * 2 + n) / (2 * n);
            retour.add(r);
         }
      }

      return retour;

   }

   public boolean equals(Rectangle r1, Rectangle r2) {

      int distance = (int) (r1.width * 0.2);

      if (r2.x <= r1.x + distance && r2.x >= r1.x - distance && r2.y <= r1.y + distance && r2.y >= r1.y - distance
            && r2.width <= (int) (r1.width * 1.2) && (int) (r2.width * 1.2) >= r1.width) {

         return true;
      }
      if (r1.x >= r2.x && r1.x + r1.width <= r2.x + r2.width && r1.y >= r2.y && r1.y + r1.height <= r2.y + r2.height) {

         return true;
      }

      return false;

   }
}
