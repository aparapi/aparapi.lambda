package hsailtest;

import com.amd.aparapi.*;
import com.amd.aparapi.HSAILMethod;


public class Oop{

   public static class P{
      int x;
      int y;

      P(int _x, int _y){
         x = _x;
         y = _y;
      }
   }

   P[] points = new P[10];


   public void run(int id){
      points[id].x = id;
      points[id].y = id;
   }

   public void test() throws ClassParseException{
      ClassModel classModel = ClassModel.getClassModel(Oop.class);
      ClassModel.ClassModelMethod method = classModel.getMethod("run", "(I)V");
      method.getInstructions();
      OkraRunner runner = new OkraRunner();

      HSAILRenderer renderer = new HSAILRenderer();
      renderer.setShowComments(true);
      new HSAILMethod(method).render(renderer);
      System.out.println(renderer.toString());
      for(int i = 0; i < points.length; i++){
         points[i] = new P(0, 0);
      }

      runner.run(renderer.toString(), points.length, this, points.length);
      for(int i = 0; i < points.length; i++){
         System.out.print("(" + points[i].x + "," + points[i].y + "),");
      }
   }


   public static void main(String[] args) throws AparapiException{
      (new Oop()).test();

   }
}
