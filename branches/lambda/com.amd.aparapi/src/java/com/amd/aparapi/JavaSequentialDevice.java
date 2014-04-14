package com.amd.aparapi;

public class JavaSequentialDevice extends Device{
   @Override
   public void forEach(int _to, final Aparapi.IntTerminal _intConsumer){

      forEach(0, _to, _intConsumer);
   }

   public void forEach(int _from, int _to, final Aparapi.IntTerminal _intConsumer){

      for (int t = _from; t<_to; t++){
         _intConsumer.accept(t);
      }
   }

}
