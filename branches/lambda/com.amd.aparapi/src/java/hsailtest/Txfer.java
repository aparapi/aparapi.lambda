package hsailtest;

import com.amd.aparapi.*;
import com.amd.aparapi.HSAILMethod;


public class Txfer {

   int[] in = new int[100];
   int[] out = new int[in.length];
   int m = 2;
   int a = 100;


   public void run(int id) {
          out[id] = in[id]*m +a;
   }

   public void test() throws ClassParseException{
      ClassModel classModel = ClassModel.getClassModel(Txfer.class);
      ClassModel.ClassModelMethod method = classModel.getMethod("run", "(I)V");

      String hsail =  new HSAILMethod(method).render(new HSAILRenderer().setShowComments(true)).toString();
      System.out.println(hsail);

       for (int i=0; i< in.length; i++){
           in[i]=i;
           out[i]=0;
       }

      new OkraRunner().run(hsail, in.length, this,  in.length);


       for (int i=0; i< in.length; i++){
           System.out.print("("+in[i]+","+out[i]+"),");
       }
   }




   public static void main(String[] args) throws AparapiException{
      (new Txfer()).test();

   }
}
