package com.amd.aparapi;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;

/**
 * Created by gfrost on 3/14/14.
 */
public class Aparapi{
   static public interface Lambda{
   }

   static public interface Mapper extends Lambda{
   }

   static public interface Reducer extends Lambda{
   }

   static public interface Terminal extends Lambda{
   }

   static public interface Int2IntMapper extends Mapper{
      int map(int _in);
   }
   static public interface Int2BooleanMapper extends Mapper{
      boolean map(int _in);
   }

   static public interface IntTerminal extends Terminal{
      void accept(int t);
   }

   static public interface ObjectTerminal<T> extends Terminal{
      void accept(T t);
   }

   static public interface Object2IntMapper<T> extends Mapper{
      int map(T t);
   }

   static public interface Object2BooleanMapper<T> extends Mapper{
      boolean map(T t);
   }

   static public interface IntReducer extends Reducer{
      int reduce(int lhs, int rhs);
   }

   static public interface BooleanReducer extends Reducer{
      boolean reduce(int lhs, int rhs);
   }

   static public class BaseRange<T extends BaseRange<T>>{
      enum STATE{
         NONE(false, false), SEQUENTIAL(false, false){
            STATE mappedVersion(){
               return (SEQUENTIAL_MAPPED);
            }
         }, SEQUENTIAL_MAPPED(false, true), PARALLEL(true, false){
            STATE mappedVersion(){
               return (PARALLEL_MAPPED);
            }
         }, PARALLEL_MAPPED(true, true);
         boolean isParallel = false;
         boolean isMapped = false;

         STATE(boolean _isParallel, boolean _isMapped){
            isParallel = _isParallel;
            isMapped = _isMapped;
         }

         STATE mappedVersion(){
            return (this);
         }

      }

      STATE state = STATE.NONE;
      protected List<Lambda> nonTerminals = new ArrayList<Lambda>();

      BaseRange(STATE _initialState){
         state = _initialState;
      }

      public T parallel(){
         if (state.equals(STATE.SEQUENTIAL)){
            state = STATE.PARALLEL;
         }else{
            throw new IllegalStateException("cant move from "+state+" to "+STATE.PARALLEL);
         }
         return ((T)this);
      }
   }

   static public class IntRange extends BaseRange<IntRange>{
      int from;
      int to;

      IntRange(int _from, int _to){
         super(STATE.SEQUENTIAL);
         from = _from;
         to = _to;
      }

      public void forEach(IntTerminal _it){
         switch (state){
            case PARALLEL:
               Device.hsa().forEach(from, to, _it);
               break;
            case SEQUENTIAL:
               Device.seq().forEach(from, to, _it);
               break;
            default:
               throw new IllegalStateException("applying forEach applied to a stream in state "+state+" Not implemented yet");
         }
      }

      public IntRange map(Int2IntMapper _im){
         state = state.mappedVersion();

         nonTerminals.add(_im);
         return (this);
      }
      public int count(Int2BooleanMapper _im){
         switch(state){
            case PARALLEL:
               return(((HSADevice)Device.hsa()).count(from, to, _im));
              // throw new IllegalStateException("parallel hsa count reduction not implemented yet");
              // break;
            case SEQUENTIAL: {
               int count = 0;
               for (int i=from; i<to; i++){
                  if (_im.map(i)){
                     count++;
                  }
               }
               return(count);
            }
           // break;
            default:
               throw new IllegalStateException("count reduction not implemented for state "+state);

         }

      }

      public int reduce(IntReducer _ir){
         int result = 0;
         switch (state){
            case PARALLEL_MAPPED:
               throw new IllegalStateException("parallel hsa int reduction not implemented yet");
               // break;
            case SEQUENTIAL_MAPPED:{
               Int2IntMapper mapper = (Int2IntMapper)nonTerminals.get(nonTerminals.size()-1);

               //   Device.hsa().forEach(0, arrayRange.usableLength, mapper.map, _ir);
               for (int i = from; i<to; i++){
                  result = _ir.reduce(mapper.map(i), result);
               }

            }
            break;
            default:
               throw new IllegalStateException("reduction applied to a stream without a map.  Not implemented yet");

         }

         return (result);
      }

   }

   static public class ArrayRange<T> extends BaseRange<ArrayRange<T>>{
      T[] arr;
      int usableLength;

      ArrayRange(T[] _arr, int _usableLength){
         super(STATE.SEQUENTIAL);
         arr = _arr;
         usableLength = _usableLength;
      }

      public void forEach(ObjectTerminal<T> _oc){

         switch (state){
            case PARALLEL:
               Device.hsa().forEach(arr, usableLength, _oc);
               break;
            case SEQUENTIAL:
               for (int i = 0; i<usableLength; i++){
                  _oc.accept(arr[i]);
               }
               break;
            default:
               throw new IllegalStateException("applying forEach applied to a stream in state "+state+" Not implemented yet");
         }

      }

      public int sum(Object2IntMapper<T> _im){
         int sum = 0;

         switch (state){

            case SEQUENTIAL:
               for (int i = 0; i<usableLength; i++){
                  sum += _im.map(arr[i]);
               }
               break;
            default:
               throw new IllegalStateException("parallel hsa int reduction not implemented yet");
               //break;
         }
         return (sum);
      }

      public ArrayRange<T> map(Object2IntMapper<T> _im){
         nonTerminals.add(_im);
         return (this);
      }

      public T[] filter(Object2BooleanMapper<T> _im){
         switch (state){

            case SEQUENTIAL:
               ArrayList<T> list = new ArrayList<T>();
               for (T i : arr){
                  if (_im.map(i)){
                     list.add(i);
                  }
               }
               return (list.toArray((T[])new Object[0]));
            //break;
            default:

               throw new IllegalStateException("parallel hsa filter not implemented yet");
               //  break;
         }

      }

      public int reduce(IntReducer _ir){
         switch (state){
            case SEQUENTIAL_MAPPED:
               Object2IntMapper<T> mapper = null;

               mapper = (Object2IntMapper<T>)nonTerminals.get(nonTerminals.size()-1);

               int result = 0;

               for (int i = 0; i<usableLength; i++){
                  result = _ir.reduce(mapper.map(arr[i]), result);
               }

               return (result);
            // break;
            default:
               throw new IllegalStateException("parallel hsa int reduction not implemented yet");
               // break;
         }

      }

      public T select(BooleanReducer _ir){
         T result = null;
         switch (state){
            case SEQUENTIAL_MAPPED:{
               int best = 0;
               Object2IntMapper<T> mapper = (Object2IntMapper<T>)nonTerminals.get(nonTerminals.size()-1);
               for (int i = 0; result == null && i<usableLength; i++){
                  if (_ir.reduce(mapper.map(arr[i]), best)){
                     result = arr[i];
                     best = mapper.map(arr[i]);
                  }

               }
            }
            break;
            default:
               throw new IllegalStateException("reduction applied to a stream without a map.  Not implemented yet");
         }
         return (result);
      }

   }

   static public <T> ArrayRange<T> range(ArrayList<T> _list){
      T[] arr;
      try{
         Field f = ArrayList.class.getDeclaredField("elementData");
         f.setAccessible(true);
         arr = (T[])f.get(_list);
         return ((ArrayRange<T>)new ArrayRange(arr, _list.size()));
      }catch (NoSuchFieldException e){
         e.printStackTrace();
         return (null);
      }catch (IllegalAccessException e){
         e.printStackTrace();
         return (null);
      }
   }

   static public <T> ArrayRange<T> range(T[] _arr){
      return ((ArrayRange<T>)new ArrayRange(_arr, _arr.length));
   }

   static public IntRange range(int _from, int _to){
      return (new IntRange(_from, _to));
   }

   static public IntRange range(int _to){
      return (new IntRange(0, _to));
   }

}
