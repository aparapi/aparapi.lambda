package hsailtest;

import com.amd.aparapi.*;


public class Squares{


   void run(int[] in, int[] out, int gid){

      //float f = 9f;
      //for (int i=0; i< 4; i++){
       // int f = in[gid];
       //  if (f >3f && gid <7){
            out[gid++] = in[gid] * in[++gid];
       //  }
        // boolean l = in[gid]<10f;
    //  }
   }


   public static void main(String[] args) throws AparapiException{
      ClassModel classModel = ClassModel.getClassModel(Squares.class);
      ClassModel.ClassModelMethod method = classModel.getMethod("run", "([I[II)V");
      method.getInstructions();

      OkraRunner runner = new OkraRunner();
      int in[] = new int[10];
      int out[] = new int[in.length];
      for (int i=0; i< in.length; i++){
         in[i]=i;

         out[i]=0;
      }
      RegISARenderer renderer = new RegISARenderer();
      //renderer.setShowLineNumbers(false);
      renderer.setShowComments(true);
      new RegISA(method).render(renderer);
      System.out.println(renderer.toString());

      Squares s = new Squares();
      runner.run(renderer.toString(), in.length-2, s, in, out, in.length-2);
      for (int i=0; i< in.length; i++){
         System.out.print("("+in[i]+","+out[i]+"),");

      }
      System.out.println();
       for (int i=0; i< in.length; i++){
           in[i]=i;

           out[i]=0;
       }
       Squares main = new Squares();
       for (int i=0; i< in.length-2; i++){
         main.run(in, out, i);
       }
       for (int i=0; i< in.length; i++){
           System.out.print("("+in[i]+","+out[i]+"),");

       }
         //System.out.println(InstructionHelper.getLabel(i,true, false, false));



   }
}
