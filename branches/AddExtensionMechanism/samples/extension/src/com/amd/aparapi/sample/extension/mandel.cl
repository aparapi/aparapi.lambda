__kernel void createMandlebrot(
   int maxIterations, 
   int width, 
   int height, 
   float scale, 
   float offsetx, 
   float offsety, 
   __global int *rgb, 
   __global int *pallette, 
){
   {
      int gid = get_global_id(0);
      float x = ((((float)(gid % width) * scale) - ((scale / 2.0f) * (float)width)) / (float)width) + offsetx;
      float y = ((((float)(gid / height) * scale) - ((scale / 2.0f) * (float)height)) / (float)height) + offsety;
      int count = 0;
      float zx = x;
      float zy = y;
      float new_zx = 0.0f;
      for (; count<64 && ((zx * zx) + (zy * zy))<8.0f; count++){
         new_zx = ((zx * zx) - (zy * zy)) + x;
         zy = ((2.0f * zx) * zy) + y;
         zx = new_zx;
      }
      rgb[gid]  = pallette[count];
      return;
   }
}

