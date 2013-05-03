package kerneltest;

import com.amd.aparapi.AparapiException;
import com.amd.aparapi.ClassModel;
import com.amd.aparapi.ClassParseException;
import com.amd.aparapi.Device;
import com.amd.aparapi.OpenCLDevice;


public class SquaresBare{




   void run (int[] in, int[] out, int gid){
         out[gid] = in[gid]++ * in[gid];
      }



   public static void main(String[] args) throws AparapiException{
      ClassModel classModel = ClassModel.getClassModel(SquaresBare.class);
      ClassModel.ClassModelMethod method = classModel.getMethod("run", "([I[II)V");
      method.getInstructions();
   }
}
