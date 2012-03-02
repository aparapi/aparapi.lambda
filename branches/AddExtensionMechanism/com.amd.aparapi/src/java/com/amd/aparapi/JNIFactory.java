package com.amd.aparapi;

public class JNIFactory{
   static final JNI jni = new JNI();
   static JNI getJNI(){
      return(jni);
   }
}
