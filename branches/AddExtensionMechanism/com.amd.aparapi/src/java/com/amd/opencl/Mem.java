package com.amd.opencl;

public class Mem{
   long bits;
   
   int sizeInBytes;
   
   long readOnlyMemId;
   long writeOnlyMemId;
   long readWriteMemId;

   long address;

   Object instance;
   
   Program program;
}
