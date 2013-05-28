package com.amd.okra;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class OkraUtil{
   // return whether the okra library is loadable
   private static boolean libExists = testOkraLibExists();
   private static AtomicLong nextFakeRef = new AtomicLong(0x76543210);
   private static Map<Object, Long> refMap = new HashMap<>();

   public static boolean okraLibExists(){
      return libExists;
   }

   private static boolean testOkraLibExists(){
      try{
         // referencing this static will cause library to try to load if not already loaded
         int version = OkraContext.version;
      }catch(UnsatisfiedLinkError e){
         return false;
      }
      // if we got this far, it exists
      return true;
   }

   public static long getRefHandle(Object obj){
      Long ref = refMap.get(obj);
      if(ref != null){
         return ref;
      }else{
         long handle = createRefHandle(obj);
         refMap.put(obj, handle);
         return handle;
      }
   }

   private static long createRefHandle(Object obj){
      if(okraLibExists()){
         return OkraContext.createRefHandle(obj);
      }else{
         // just make one up
         // this allows us to be called for codegen purposes without the okra library being present
         return nextFakeRef.getAndAdd(4);
      }
   }
}