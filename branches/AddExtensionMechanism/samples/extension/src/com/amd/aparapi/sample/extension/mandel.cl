#define MAX_ITERATIONS 64

__kernel void createMandleBrot(
    float scale, 
    float offsetx, 
    float offsety, 
    __global int *rgb, 
    __global int *pallette
    ){
    int gid = get_global_id(0) + get_global_id(1)*get_global_size(0);
    float x = ((((float)(get_global_id(0)) * scale) - ((scale / 2.0f) * (float)get_global_size(0))) / (float)get_global_size(0)) + offsetx;
    float y = ((((float)(get_global_id(1)) * scale) - ((scale / 2.0f) * (float)get_global_size(1))) / (float)get_global_size(1)) + offsety;
    int count = 0;
    float zx = x;
    float zy = y;
    float new_zx = 0.0f;
    for (; count<MAX_ITERATIONS && ((zx * zx) + (zy * zy))<8.0f; count++){
        new_zx = ((zx * zx) - (zy * zy)) + x;
        zy = ((2.0f * zx) * zy) + y;
        zx = new_zx;
    }
    rgb[gid]  = pallette[count];
}

