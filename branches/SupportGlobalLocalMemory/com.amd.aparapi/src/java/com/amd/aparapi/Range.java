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
   
   final boolean local = true;
   
   protected boolean hasLocal(){
      return(local);
   }

   Range(int _dims) {
      dims = _dims;
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
      return ((globalWidth * globalHeight * globalDepth) / (localWidth * globalHeight * globalDepth));
   }

   public static Range create(int _globalWidth, int _localWidth) {
      Range range = new Range(1);
      range.globalWidth = _globalWidth;
      range.localWidth = _localWidth;
      return (range);
   }

   public static Range create(int _globalWidth) {
      return (create(_globalWidth, 1));
   }

   public static Range create2D(int _globalWidth, int _globalHeight, int _localWidth, int _localHeight) {
      Range range = new Range(2);
      range.globalWidth = _globalWidth;
      range.localWidth = _localWidth;
      range.globalHeight = _globalHeight;
      range.localHeight = _localHeight;
      return (range);
   }

   public static Range create2D(int _globalWidth, int _globalHeight) {
  
      return (create2D(_globalWidth, _globalHeight, 1, 1));
   }

   public static Range create3D(int _globalWidth, int _globalHeight, int _globalDepth, int _localWidth, int _localHeight,
         int _localDepth) {
      Range range = new Range(3);
      range.globalWidth = _globalWidth;
      range.localWidth = _localWidth;
      range.globalHeight = _globalHeight;
      range.localHeight = _localHeight;
      range.globalDepth = _globalDepth;
      range.localDepth = _localDepth;
      return (range);
   }

   public static Range create3D(int _globalWidth, int _globalHeight, int _globalDepth) {
      return (create3D(_globalWidth, _globalHeight, _globalDepth, 1, 1, 1));
   }

   public int getDims() {
      return (dims);
   }

}
