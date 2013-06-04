package hsailtest;

import com.amd.aparapi.*;
import com.amd.aparapi.HSAILRenderer;


public class Fields {

   int fromId = 0;
   float toId = 0;

   public void run(int id) {
           if (id==24){
               fromId = id;
               toId = (float)id;
           }
   }

   public void test() throws ClassParseException{
      ClassModel classModel = ClassModel.getClassModel(Fields.class);
      ClassModel.ClassModelMethod method = classModel.getMethod("run", "(I)V");
      method.getInstructions();
      OkraRunner runner = new OkraRunner();

      HSAILRenderer renderer = new HSAILRenderer();
      renderer.setShowComments(true);
      new RegISA(method).render(renderer);
      System.out.println(renderer.toString());


      runner.run(renderer.toString(), 100, this,  100);
      System.out.println("fromId = "+fromId);
       System.out.println("toId = "+toId);
   }




   public static void main(String[] args) throws AparapiException{
      (new Fields()).test();

   }
}
