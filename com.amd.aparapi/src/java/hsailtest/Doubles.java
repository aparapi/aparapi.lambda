package hsailtest;

import com.amd.aparapi.*;


public class Doubles{

   double[] doubles = new double[32];

   public void run(int id) {
           doubles[id] = id;
   }

   public void test() throws ClassParseException{
      ClassModel classModel = ClassModel.getClassModel(Doubles.class);
      ClassModel.ClassModelMethod method = classModel.getMethod("run", "(I)V");
      method.getInstructions();
      OkraRunner runner = new OkraRunner();

      HSAILRenderer renderer = new HSAILRenderer();
      renderer.setShowComments(true);
      new HSAILMethod(method).render(renderer);
      System.out.println(renderer.toString());


      runner.run(renderer.toString(), doubles.length, this,  doubles.length);
      for (int i=0; i< doubles.length; i++){
         System.out.print(doubles[i]+", ");
      }
   }




   public static void main(String[] args) throws AparapiException{
      (new Doubles()).test();

   }
}
