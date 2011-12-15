package com.amd.aparapi;

public class Range3D extends Range2D{

   public Range3D(int _globalWidth, int _globalHeight, int _globalDepth, int _localWidth, int _localHeight, int _localDepth) {
      super(_globalWidth, _globalHeight, _localWidth, _localHeight);
      globalDepth = _globalDepth;
      localDepth = _localDepth;
      valid = valid && (globalDepth % localDepth) == 0;
      dims = 3;
   }

   public Range3D(int _globalWidth, int _globalHeight, int _globalDepth) {
      this(_globalWidth, _globalHeight, _globalDepth, 1, 1, 1);
   }

   public int getNumGroups() {
      return ((globalWidth * globalHeight * globalDepth) / (localWidth * globalHeight * globalDepth));
   }
}
