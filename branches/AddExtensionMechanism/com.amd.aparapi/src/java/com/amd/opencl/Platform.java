package com.amd.opencl;

import java.util.ArrayList;
import java.util.List;

public class Platform{
   private long platformId;

   private String version;

   private String vendor;

   private List<Device> devices = new ArrayList<Device>();

   Platform(long _platformId, String _version, String _vendor) {
      platformId = _platformId;
      version = _version;
      vendor = _vendor;
   }

   public String toString() {
      return ("PlatformId " + platformId + "\nName:" + vendor + "\nVersion:" + version);
   }

   public void add(Device device) {
      devices.add(device);
   }

   public List<Device> getDevices() {
      return (devices);
   }

   public static List<Platform> getPlatforms() {
      return (OpenCLJNI.getJNI().getPlatforms());
   }

}
