package com.amd.aparapi;

import java.lang.reflect.Field;
import java.util.ArrayList;

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

   static public class IntRange{
      int from;
      int to;

      IntRange(int _from, int _to){
         from = _from;
         to = _to;
      }

      public ParallelIntRange parallel(){
         return (new ParallelIntRange(this));
      }

      public void forEach(IntTerminal _it){

         Device.seq().forEach(from, to, _it);

      }

      public MappedIntRange map(Int2IntMapper _im){
         return (new MappedIntRange(this, _im));
      }

      public int count(Int2BooleanMapper _im){

         int count = 0;
         for (int i = from; i<to; i++){
            if (_im.map(i)){
               count++;
            }
         }
         return (count);

      }

   }

   static public class MappedIntRange{
      IntRange intRange;
      Int2IntMapper i2im;

      MappedIntRange(IntRange _intRange, Int2IntMapper _i2im){
         intRange = _intRange;
         i2im = _i2im;
      }

      public int reduce(IntReducer _ir){
         int result = 0;

         for (int i = intRange.from; i<intRange.to; i++){
            result = _ir.reduce(i2im.map(i), result);
         }

         return (result);
      }
   }

   static public class ArrayRange<T>{
      T[] arr;
      int usableLength;

      ArrayRange(T[] _arr, int _usableLength){

         arr = _arr;
         usableLength = _usableLength;
      }

      public ParallelArrayRange<T> parallel(){
         return (new ParallelArrayRange(this));
      }

      public void forEach(ObjectTerminal<T> _oc){

         for (int i = 0; i<usableLength; i++){
            _oc.accept(arr[i]);
         }

      }

      public int sum(Object2IntMapper<T> _im){
         int sum = 0;

         for (int i = 0; i<usableLength; i++){
            sum += _im.map(arr[i]);
         }

         return (sum);
      }

      public MappedArrayRange<T> map(Object2IntMapper<T> _im){

         return (new MappedArrayRange(this, _im));
      }

      public T[] filter(Object2BooleanMapper<T> _im){
         ArrayList<T> list = new ArrayList<T>();
         for (T i : arr){
            if (_im.map(i)){
               list.add(i);
            }
         }
         return (list.toArray((T[])new Object[0]));
      }

   }

   static public class MappedArrayRange<T>{
      ArrayRange<T> arrayRange;
      Object2IntMapper<T> o2im;

      MappedArrayRange(ArrayRange<T> _arrayRange, Object2IntMapper<T> _o2im){
         arrayRange = _arrayRange;
         o2im = _o2im;
      }

      public int reduce(IntReducer _ir){

         int result = 0;
         for (int i = 0; i<arrayRange.usableLength; i++){
            result = _ir.reduce(o2im.map(arrayRange.arr[i]), result);
         }
         return (result);
      }

      public T select(BooleanReducer _ir){
         T result = null;
         int best = 0;

         for (int i = 0; result == null && i<arrayRange.usableLength; i++){
            if (_ir.reduce(o2im.map(arrayRange.arr[i]), best)){
               result = arrayRange.arr[i];
               best = o2im.map(arrayRange.arr[i]);
            }

         }
         return (result);
      }
   }

   static public class ParallelIntRange{
      IntRange range;

      ParallelIntRange(IntRange _range){
         range = _range;
      }

      public void forEach(IntTerminal _it){
         Device.hsa().forEach(range.from, range.to, _it);
      }

      public int count(Int2BooleanMapper _im){
         return ((Device.hsa()).count(range.from, range.to, _im));
      }

      public MappedParallelIntRange map(Int2IntMapper _im){
         return (new MappedParallelIntRange(this, _im));
      }

   }

   static public class MappedParallelIntRange{
      ParallelIntRange parallelIntRange;
      Int2IntMapper i2im;

      MappedParallelIntRange(ParallelIntRange _parallelIntRange, Int2IntMapper _i2im){
         parallelIntRange = _parallelIntRange;
         i2im = _i2im;
      }

      public int reduce(IntReducer _ir){
         throw new IllegalStateException("MappedParallelIntRange.reduce not implemented");
      }
   }

   static public class ParallelArrayRange<T>{
      ArrayRange<T> range;

      ParallelArrayRange(ArrayRange<T> _range){
         range = _range;
      }

      public void forEach(ObjectTerminal<T> _ot){
         Device.hsa().forEach(range.arr, range.usableLength, _ot);
      }

      public ParallelMappedArrayRange<T> map(Object2IntMapper<T> _im){

         return (new ParallelMappedArrayRange(this, _im));
      }

      public T[] filter(Object2BooleanMapper<T> _im){
         throw new IllegalStateException("ParallelArrayRange.filter not implemented");

      }

      public T select(BooleanReducer _ir){
         throw new IllegalStateException("ParallelArrayRange.select not implemented");

      }

   }

   static public class ParallelMappedArrayRange<T>{
      ParallelArrayRange<T> parallelArrayRange;
      Object2IntMapper<T> o2im;

      ParallelMappedArrayRange(ParallelArrayRange<T> _parallelArrayRange, Object2IntMapper<T> _o2im){
         parallelArrayRange = _parallelArrayRange;
         o2im = _o2im;
      }

      public int reduce(IntReducer _ir){
         throw new IllegalStateException("ParallelMappedArrayRange.reduce not implemented");
      }

      public T select(BooleanReducer _ir){
         throw new IllegalStateException("ParallelMappedArrayRange.select not implemented");
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
