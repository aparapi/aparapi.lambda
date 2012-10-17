package com.amd.aparapi.samples;
// http://cr.openjdk.java.net/~briangoetz/lambda/lambda-translation.html
public class WheresMyLambdaByteCode{
   interface SAM{
      void run();
   }

   public static void run(SAM sam){
      sam.run();
   }

   public static void main(String[] _args) {
      run(()->{ System.out.println("here I am"); });
   }

}
