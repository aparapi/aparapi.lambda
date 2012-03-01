package com.amd.aparapi;

public class Device{
   public enum TYPE {
      UNKNOWN,
      GPU,
      CPU
   };

   private Platform platform;

   private long deviceId;

   private TYPE type = TYPE.UNKNOWN;

   private int maxComputeUnits;

   private int maxWorkItemDimensions;

   private long localMemSize;

   private long globalMemSize;

   private int maxWorkGroupSize;

   private int[] maxWorkItemSize = new int[] {
         0,
         0,
         0
   };

   Device(Platform _platform, long _deviceId, TYPE _type) {
      platform = _platform;
      deviceId = _deviceId;
      type = _type;

   }

   public TYPE getType() {
      return type;
   }

   public void setType(TYPE type) {
      this.type = type;
   }

   public int getMaxComputeUnits() {
      return maxComputeUnits;
   }

   public void setMaxComputeUnits(int _maxComputeUnits) {
      maxComputeUnits = _maxComputeUnits;
   }

   public int getMaxWorkItemDimensions() {
      return maxWorkItemDimensions;
   }

   public void setMaxWorkItemDimensions(int _maxWorkItemDimensions) {
      maxWorkItemDimensions = _maxWorkItemDimensions;
   }

   public long getLocalMemSize() {
      return localMemSize;
   }

   public void setLocalMemSize(long _localMemSize) {
      localMemSize = _localMemSize;
   }

   public long getGlobalMemSize() {
      return globalMemSize;
   }

   public void setGlobalMemSize(long _globalMemSize) {
      globalMemSize = _globalMemSize;
   }

   public int getMaxWorkGroupSize() {
      return maxWorkGroupSize;
   }

   public void setMaxWorkGroupSize(int _maxWorkGroupSize) {
      maxWorkGroupSize = _maxWorkGroupSize;
   }

   public int[] getMaxWorkItemSize() {
      return maxWorkItemSize;
   }

   public void setMaxWorkItemSize(int[] maxWorkItemSize) {
      this.maxWorkItemSize = maxWorkItemSize;
   }

   public String toString() {
      StringBuilder s = new StringBuilder("{");
      boolean first = true;
      for (int workItemSize : maxWorkItemSize) {
         if (first) {
            first = false;
         } else {
            s.append(", ");
         }
         s.append(workItemSize);
      }
      s.append("}");
      return ("Device " + deviceId + "\n  type:" + type + "\n  maxComputeUnits=" + maxComputeUnits + "\n  maxWorkItemDimensions="
            + maxWorkItemDimensions + "\n  maxWorkItemSizes=" + s + "\n  maxWorkWorkGroupSize=" + maxWorkGroupSize
            + "\n  globalMemSize=" + globalMemSize + "\n  localMemSize=" + localMemSize);
   }

   void setMaxWorkItemSize(int _dim, int _value) {
      maxWorkItemSize[_dim] = _value;
   }

   public long getDeviceId() {
      return (deviceId);
   }

   public Platform getPlatform() {
      return (platform);
   }

}
