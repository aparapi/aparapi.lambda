package com.amd.aparapi;

public class AparapiExtensionImplementation{
   Range range;

   int[] globalId = new int[] {
         0,
         0,
         0
   };

   public int getGlobalId(int _dim) {
      return (globalId[_dim]);
   }

   public void setRange(Range _range) {
      range = _range;
   }

   public int getGlobalSize(int _dim) {
      return (range.getGlobalSize(_dim));
   }

   public int getLocalSize(int _dim) {
      return (range.getLocalSize(_dim));
   }

}
