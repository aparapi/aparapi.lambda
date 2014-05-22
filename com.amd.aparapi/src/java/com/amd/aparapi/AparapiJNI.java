package com.amd.aparapi;

import java.util.List;
import java.util.logging.Logger;

public class AparapiJNI{
   private static Logger logger = Logger.getLogger(Config.getLoggerName());

   static final AparapiJNI jni = new AparapiJNI();

   public static AparapiJNI getAparapiJNI(){
      return (jni);
   }

   native public byte[] getBytes(String className);

   native public void dumpLoadedClassNames();

}
