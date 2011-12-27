package com.amd.aparapi;

public class Range implements Cloneable{
   protected int globalWidth = 1;

   protected int localWidth = 1;

   protected int globalHeight = 1;

   protected int localHeight = 1;

   protected int globalDepth = 1;

   protected int localDepth = 1;

   protected boolean valid;

   protected int dims;

   boolean local = true;

   protected boolean hasLocal() {
      return (local);
   }

   private Range() {
   }

   Range(int _dims) {
      this();
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

   public int getGroupSize() {
      return (localWidth * localHeight * localDepth);
   }

   public int getNumGroups() {
      return ((globalWidth * globalHeight * globalDepth) / getGroupSize());
   }

   public static Range create(int _globalWidth, int _localWidth) {
      Range range = new Range(1);
      range.globalWidth = _globalWidth;
      range.localWidth = _localWidth;
      return (range);
   }

   public static Range create(int _globalWidth) {

      Range withoutLocal = create(_globalWidth, 1);
      withoutLocal.local = false;
      return (withoutLocal);
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
      Range withoutLocal = create2D(_globalWidth, _globalHeight, 1, 1);
      withoutLocal.local = false;
      return (withoutLocal);
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
      Range withoutLocal = create3D(_globalWidth, _globalHeight, _globalDepth, 1, 1, 1);
      withoutLocal.local = false;
      return (withoutLocal);

   }

   public int getDims() {
      return (dims);
   }

   @Override protected Object clone() {
      try {
         Range worker = (Range) super.clone();

         return worker;
      } catch (CloneNotSupportedException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
         return (null);
      }
   }

   public String toString() {
      StringBuilder sb = new StringBuilder();

      switch (dims) {
         case 1:
            if (!local) {
               sb.append(globalWidth);

            } else {
               sb.append("global:" + globalWidth + " local:" + localWidth);
            }
            break;
         case 2:
            sb.append("2D(");
            if (!local) {
               sb.append(globalWidth + "x" + globalHeight);
            } else {
               sb.append("global:" + globalWidth + "x" + globalHeight + " local:" + localWidth + "x" + localHeight);
            }
            sb.append(")");
            break;
         case 3:
            sb.append("3D");
            if (!local) {
               sb.append(globalWidth + "x" + globalHeight + "x" + globalDepth);
            } else {
               sb.append("global:" + globalWidth + "x" + globalHeight + "x" + globalDepth + " local:" + localWidth + "x"
                     + localHeight + "x" + localWidth);
            }
            sb.append(")");
            break;

      }
      return (sb.toString());
   }
}
