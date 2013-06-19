package hsailtest;

import com.amd.aparapi.*;
import java.util.function.IntConsumer;


public class SquaresLambda{




   public static void main(String[] args) throws AparapiException{
      ClassModel classModel = ClassModel.getClassModel(SquaresLambda.class);
      ClassModel.ClassModelMethod method = classModel.getMethod("lambda$0", "([I[II)V");

   //   method.getInstructions();

      int in[] = new int[10];
      int out[] = new int[in.length];

      OkraRunner runner = new OkraRunner();
      IntConsumer ic =  gid -> {
         in[gid]=gid;
         out[gid] = 4;/*in[gid] * in[gid];*/
      };

      HSAILRenderer renderer = new HSAILRenderer();
      //renderer.setShowLineNumbers(false);
      renderer.setShowComments(true);
      new HSAILMethod(method).render(renderer);
      System.out.println(renderer.toString());

      SquaresLambda s = new SquaresLambda();
      runner.run(renderer.toString(), in.length, s, in, out, in.length);
      for (int i=0; i< in.length; i++){
         System.out.print("("+in[i]+","+out[i]+"),");
      }
   }
}
