package hsailtest;

import com.amd.aparapi.*;


public class Squares{


   void run(int[] in, int[] out, int gid){
      out[gid] = in[gid] * in[gid];
   }


   public static void main(String[] args) throws AparapiException{
      ClassModel classModel = ClassModel.getClassModel(Squares.class);
      ClassModel.ClassModelMethod method = classModel.getMethod("run", "([I[II)V");
      method.getInstructions();

      OkraRunner runner = new OkraRunner();
      int in[] = new int[100];
      int out[] = new int[in.length];
      for (int i=0; i< in.length; i++){
         in[i]=i;
         out[i]=0;
      }
      RegISARenderer renderer = new RegISARenderer();
      new RegISA(method).render(renderer);
      System.out.println(renderer.toString());

      Squares s = new Squares();
      runner.run(renderer.toString(), in.length, s, in, out, in.length);
      for (int i=0; i< in.length; i++){
         System.out.println(i+" "+in[i]+" "+out[i]);

      }

         //System.out.println(InstructionHelper.getLabel(i,true, false, false));



   }
}
