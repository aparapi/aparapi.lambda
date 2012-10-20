package com.amd.aparapi.opencl;

import java.util.ArrayList;
import java.util.List;

import com.amd.aparapi.device.OpenCLDevice;
import com.amd.aparapi.jni.OpenCLJNI;

public class OpenCLPlatform extends OpenCLJNI {

   private final long platformId;

   private final String version;

   private final String vendor;

   private final String name;

   private final List<OpenCLDevice> devices = new ArrayList<OpenCLDevice>();

   OpenCLPlatform(long _platformId, String _version, String _vendor, String _name) {
      platformId = _platformId;
      version = _version;
      vendor = _vendor;
      name = _name;
   }

   @Override
   public String toString() {
      return ("PlatformId " + platformId + "\nName:" + vendor + "\nVersion:" + version);
   }

   public void addOpenCLDevice(OpenCLDevice device) {
      devices.add(device);
   }

   public List<OpenCLDevice> getOpenCLDevices() {
      return (devices);
   }

   public static List<OpenCLPlatform> getOpenCLPlatforms() {
      if (OpenCLLoader.isOpenCLAvailable()) {
         return (getPlatforms());
      } else {
         return (new ArrayList<OpenCLPlatform>());
      }
   }

   public String getName() {
      return (name);
   }

   public String getVersion() {
      return (version);
   }

   public String getVendor() {
      return (vendor);
   }
}
