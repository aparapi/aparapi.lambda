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

import java.awt.Point;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import org.jdom.Element;

public class HaarCascade{

   int width;

   int height;

   Stage[] stages;

   /** Detector constructor.
    * Builds, from a XML document (i.e. the result of parsing an XML file, the corresponding Haar cascade.
    * @param document The XML document (parsing of file generated by OpenCV) describing the Haar cascade.
    * 
    * http://code.google.com/p/jjil/wiki/ImplementingHaarCascade
    */
   public HaarCascade(org.jdom.Document document) {

      List<Stage> stageList = new LinkedList<Stage>();
      Element racine = (Element) document.getRootElement().getChildren().get(0);
      Scanner scanner = new Scanner(racine.getChild("size").getText());
      width = scanner.nextInt();
      height = scanner.nextInt();
      Iterator it = racine.getChild("stages").getChildren("_").iterator();
      while (it.hasNext()) {
         Element stage = (Element) it.next();
         float thres = Float.parseFloat(stage.getChild("stage_threshold").getText());
         //System.out.println(thres);
         Iterator it2 = stage.getChild("trees").getChildren("_").iterator();
         Stage st = new Stage(thres);

         System.out.println("create stage " + thres);
         while (it2.hasNext()) {
            Element tree = ((Element) it2.next());
            Tree t = new Tree(st);
            Iterator it4 = tree.getChildren("_").iterator();
            while (it4.hasNext()) {
               Element feature = (Element) it4.next();
               float thres2 = Float.parseFloat(feature.getChild("threshold").getText());
               int left_node = -1;
               float left_val = 0;
               boolean has_left_val = false;
               int right_node = -1;
               float right_val = 0;
               boolean has_right_val = false;
               Element e;
               if ((e = feature.getChild("left_val")) != null) {
                  left_val = Float.parseFloat(e.getText());
                  has_left_val = true;
               } else {
                  left_node = Integer.parseInt(feature.getChild("left_node").getText());
                  has_left_val = false;
               }

               if ((e = feature.getChild("right_val")) != null) {
                  right_val = Float.parseFloat(e.getText());
                  has_right_val = true;
               } else {
                  right_node = Integer.parseInt(feature.getChild("right_node").getText());
                  has_right_val = false;
               }
               Feature f = new Feature(t, thres2, left_val, left_node, has_left_val, right_val, right_node, has_right_val,
                     new Point(width, height));
               Iterator it3 = feature.getChild("feature").getChild("rects").getChildren("_").iterator();
               while (it3.hasNext()) {
                  String s = ((Element) it3.next()).getText().trim();
                  //System.out.println(s);
                  Rect r = Rect.fromString(s);
                  f.add(r);
               }

               t.addFeature(f);
            }
            st.addTree(t);
            // System.out.println("Number of nodes in tree " + t.features.size());
         }
         // System.out.println("Number of trees : " + st.trees.size());
         stageList.add(st);
      }
      stages = stageList.toArray(new Stage[0]);
      Rect.flatten();
      Feature.flatten();
      Tree.flatten();
      Stage.flatten();
   }

}
