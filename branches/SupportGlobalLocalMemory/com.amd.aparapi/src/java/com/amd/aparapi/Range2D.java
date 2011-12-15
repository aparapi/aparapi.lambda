package com.amd.aparapi;

public class Range2D extends Range{

   public Range2D(int _globalWidth, int _globalHeight, int _localWidth, int _localHeight) {
      super(_globalWidth, _localWidth);
      globalHeight = _globalHeight;
      localHeight = _localHeight;
      valid = valid && ((globalHeight % localHeight) == 0);
      dims = 2;
   }

   public Range2D(int _globalWidth, int _globalHeight) {
      this(_globalWidth, _globalHeight, 1, 1);
   }

   public int getNumGroups() {
      return ((globalWidth * globalHeight) / (localWidth * globalHeight));
   }

}
