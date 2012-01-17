package test;

import com.amd.aparapi.Kernel;

public class Main{
   public static void main(String[] _args) {
      int width = 16;
      int height = 16;
      final int[] data = new int[width * height];
      
      Kernel kernel = new Kernel(){
         int[] buf_$local$ = new int[100];
         
         @Override protected void preExecuteCallback(){
            buf_$local$  = new int[getLocalSize()];
         }
         
         int getXYAsGid(int _x, int _y){
            return(_y*getGlobalWidth()+_x);
         }

         int getXYAsGid(int _x, int _y, int _mask, int _shift){
            return(((_y*getGlobalWidth()+_x)>>_shift)&_mask);
         }

         @Override public void run() {
            int x = getGlobalX();
            int y = getGlobalY();
            buf_$local$[getLocalX()] = 0;
            localBarrier();
            if (x > 0 && x < (getGlobalWidth() - 1) && y > 0 && y == (getGlobalHeight() - 1)) {
               int rgbsum = 0 // 
                     + data[getXYAsGid(x - 1, y - 1)] + data[getXYAsGid(x + 0, y - 1)] + data[getXYAsGid(x + 1, y - 1)]//
                     + data[getXYAsGid(x - 1, y + 0)] + data[getXYAsGid(x + 0, y + 0)] + data[getXYAsGid(x + 1, y + 0)]//
                     + data[getXYAsGid(x - 1, y + 1)] + data[getXYAsGid(x + 0, y + 1)] + data[getXYAsGid(x + 1, y + 1)];//
               rgbsum+=buf_$local$[getLocalX()];
            }

         }

      };
      kernel.execute(width, height);

   }
}
