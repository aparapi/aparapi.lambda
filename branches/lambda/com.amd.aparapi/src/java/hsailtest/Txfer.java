package hsailtest;

import com.amd.aparapi.AparapiException;
import com.amd.aparapi.ClassModel;
import com.amd.aparapi.ClassParseException;
import com.amd.aparapi.OkraRunner;
import com.amd.aparapi.RegISA;
import com.amd.aparapi.RegISARenderer;


public class Txfer {

   int[] in = new int[100];
   int[] out = new int[100];
   int m = 2;
   int a = 100;


   public void run(int id) {
          out[id] = in[id]*m +a;
   }

   public void test() throws ClassParseException{
      ClassModel classModel = ClassModel.getClassModel(Txfer.class);
      ClassModel.ClassModelMethod method = classModel.getMethod("run", "(I)V");
      method.getInstructions();
      OkraRunner runner = new OkraRunner();

      RegISARenderer renderer = new RegISARenderer();
      renderer.setShowComments(true);
      new RegISA(method).render(renderer);
      System.out.println(renderer.toString());
       for (int i=0; i< in.length; i++){
           in[i]=i;
           out[i]=0;
       }

      runner.run(renderer.toString(), 100, this,  100);
       for (int i=0; i< in.length; i++){
           System.out.print("("+in[i]+","+out[i]+"),");
       }
   }




   public static void main(String[] args) throws AparapiException{
      (new Txfer()).test();

   }
}
