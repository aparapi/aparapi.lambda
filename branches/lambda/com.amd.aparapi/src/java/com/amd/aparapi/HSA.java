package com.amd.aparapi;

/**
 * Methods in this class never get called. Instead they mark an intention
 * in a lambda to use HSA features which are unmappable to Java equivalents.
 * If you change the name of any method you will have to also change the
 * intrinsic mapping calls in the  HSA assembler.
 */
public class HSA{

   public static int getGridSize(){ // --> gridsize_u32 $s0, 0
      throw new IllegalStateException("you don't want to call this from Java");
   }

   public static int getWorkItemId(){ // workitemid_u32 $s0, 0;
      throw new IllegalStateException("you don't want to call this from Java");
   }

   public static int getWorkGroupId(){ // workgroupid_u32 $s0, 0;
      throw new IllegalStateException("you don't want to call this from Java");

   }

   public static int getLaneId(){ // laneid_u32 $s0;
      throw new IllegalStateException("you don't want to call this from Java");

   }

   public static int getWorkGroupSize(){ // workgroupsize_u32 $s0, 0;
      throw new IllegalStateException("you don't want to call this from Java");

   }

   public static int getCurrentWorkGroupSize(){ // currentworkgroupsize_u32 $s0, 0;
      throw new IllegalStateException("you don't want to call this from Java");

   }

   public static int getCountUpLane(){ // countuplane_u32 $s0;
      throw new IllegalStateException("you don't want to call this from Java");

   }

   public static int getMaskLane(){ // masklane_u32 $s0;
      throw new IllegalStateException("you don't want to call this from Java");

   }

   public static int getComputeUnitId(){ // cuid_u32 $s0;
      throw new IllegalStateException("you don't want to call this from Java");

   }

   public static long getClock(){ // clock_u64 $d0;
      throw new IllegalStateException("you don't want to call this from Java");

   }

   public static void barrier(){ // barrier_fgroup
      throw new IllegalStateException("you don't want to call this from Java");
   }

   public static int[] localIntX1(){ // 	align 4 group_u32 %run_cllocal_scratch[256];" + "\n" +
      throw new IllegalStateException("you don't want to call this from Java");
   }

   public static Object[] localObjectX1(){ // 	align 4 group_u32 %run_cllocal_scratch[256];" + "\n" +
      throw new IllegalStateException("you don't want to call this from Java");
   }

   public static float[] localFloatX1(){ // 	align 4 group_u32 %run_cllocal_scratch[256];" + "\n" +
      throw new IllegalStateException("you don't want to call this from Java");
   }

   public static int[] localIntX2(){ // 	align 4 group_u32 %run_cllocal_scratch[512];" + "\n" +
      throw new IllegalStateException("you don't want to call this from Java");
   }

}
