package com.amd.aparapi;

public class AparapiExtensionImplementation{
   /**package **/ Range range;

   /**package**/ int[] globalId = new int[] {
         0,
         0,
         0
   };

   protected int getGlobalId(int _dim) {
      return (globalId[_dim]);
   }

   /** package **/ void setRange(Range _range) {
      range = _range;
   }

   protected int getGlobalSize(int _dim) {
      return (range.getGlobalSize(_dim));
   }

   protected int getLocalSize(int _dim) {
      return (range.getLocalSize(_dim));
   }

}
