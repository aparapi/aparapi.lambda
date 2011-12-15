package com.amd.aparapi;

public class Range{
   protected int globalWidth;

   protected int localWidth;

   protected int globalHeight;

   protected int localHeight;

   protected int globalDepth;

   protected int localDepth;

   protected boolean valid;

   protected int dims;

   public Range(int _globalWidth, int _localWidth) {
      globalWidth = _globalWidth;
      localWidth = _localWidth;
      valid = globalWidth % localWidth == 0;
      dims = 1;
   }

   public int getGlobalWidth() {
      return globalWidth;
   }

   public int getLocalWidth() {
      return localWidth;
   }

   public int getGlobalHeight() {
      return globalHeight;
   }

   public int getLocalHeight() {
      return localHeight;
   }

   public void setValid(boolean _valid) {
      valid = _valid;
   }

   public boolean isValid() {
      return (valid);
   }

   public int getGlobalDepth() {
      return globalDepth;
   }

   public int getLocalDepth() {
      return localDepth;
   }

   public int getNumGroups() {
      return (globalWidth / localWidth);
   }
}
