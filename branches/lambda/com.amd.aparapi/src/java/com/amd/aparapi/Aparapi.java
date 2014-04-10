package com.amd.aparapi;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Created by gfrost on 3/14/14.
 */
public class Aparapi {
    static public interface Lambda{
    }

    static public interface Mapper extends Lambda{
    }
    static public interface Terminal extends Lambda{
    }
    static public interface Reducer extends Lambda {
    }
    static public interface Int2IntMapper extends Mapper{
        int map(int _in);
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
    static public interface IntReducer extends Reducer{
        int reduce(int lhs, int rhs);
    }
    static public interface BooleanReducer extends Reducer{
        boolean reduce(int lhs, int rhs);
    }

    static int sum(int from, int to, Int2IntMapper im){
        int sum = 0;
        for (int i=from; i<to; i++){
            sum+=im.map(i);
        }
        return(sum);
    }
    static public class IntRange{
        int from;
        int to;
        public ParallelIntRange parallel(){
            return(new ParallelIntRange(this));
        }
        IntRange(int _from, int _to){
            from =_from;
            to =_to;
        }
        public void forEach(IntTerminal _ic){

            Device.seq().forEach(from, to, _ic);
        }
        public int sum(Int2IntMapper _im){
            return(Aparapi.sum(from, to, _im));
        }
        public int mapReduce(Int2IntMapper _im, IntReducer _ir){
            int result=0;
            for (int i=from; i<to;  i++){
                result=_ir.reduce(_im.map(i), result);
            }
            return(result);
        }
        public MapBuilder<?> map(Int2IntMapper _im){
            return(new MapBuilder(this, _im));
        }
    }
    static public class ParallelIntRange {
        IntRange intRange;
        ParallelIntRange(IntRange _intRange){
            intRange = _intRange;
        }
        public void forEach(IntTerminal _it){
            Device.hsa().forEach(intRange.from, intRange.to, _it);
        }
    }



    static public class ParallelArrayRange<T> {
        ArrayRange<T> arrayRange;
        ParallelArrayRange(ArrayRange _arrayRange){
            arrayRange = _arrayRange;
        }
        public void forEach(ObjectTerminal<T> _oc){
            for (T i:arrayRange.arr){
                _oc.accept(i);
            }
        }
    }



    static public class MapBuilder<T>{
        ArrayRange<T> arrayRange;
        IntRange intRange;
        Object2IntMapper<T> mapper;
        Int2IntMapper intMapper;
        MapBuilder(ArrayRange<T> _range, Object2IntMapper<T> _mapper){
            arrayRange = _range;
            mapper = _mapper;

        }
        MapBuilder(IntRange _range, Int2IntMapper _mapper){
            intRange = _range;
            intMapper = _mapper;

        }
        public int reduce(IntReducer _ir){
            int result=0;
            if (arrayRange!=null){
            for (int i=0; i<arrayRange.usableLength; i++){
                result=_ir.reduce(mapper.map(arrayRange.arr[i]), result);
            }
            }else if (intRange != null){
                for (int i=intRange.from; i<intRange.to; i++){
                   result=_ir.reduce(intMapper.map(i), result);
                }
            }
            return(result);
        }

        public T select(BooleanReducer _ir){
            T result=null;
            int best=0;
            if (arrayRange!=null){
                for (int i=0; i<arrayRange.usableLength; i++){
                    if (_ir.reduce(mapper.map(arrayRange.arr[i]), best)){
                        result = arrayRange.arr[i];
                        best = mapper.map(arrayRange.arr[i]);
                   }

                }
            }
            return(result);
        }

    }
    static public class ArrayRange<T>{
        T[] arr;
        int usableLength;
        ArrayRange(T[] _arr){
            arr = _arr;
            usableLength=_arr.length;
        }
        ArrayRange(T[] _arr, int _usableLength){
            arr = _arr;
            usableLength=_usableLength;
        }
        public ParallelArrayRange<T> parallel(){
            return(new ParallelArrayRange(this));
        }
        public void forEach(ObjectTerminal<T> _oc){
            for (int i=0; i<usableLength; i++){
                _oc.accept(arr[i]);
            }
        }
        public int sum(Object2IntMapper<T> _im){
            int sum = 0;
            for (int i=0; i<usableLength; i++){
                sum+=_im.map(arr[i]);
            }
            return(sum);
        }
        public int mapReduce(Object2IntMapper<T> _im, IntReducer _ir){
            int result=0;
            for (int i=0; i<usableLength; i++){
                result=_ir.reduce(_im.map(arr[i]), result);
            }
            return(result);
        }
        public MapBuilder<T> map(Object2IntMapper<T> _im){
            return(new MapBuilder<T>(this, _im));
        }

    }


    static public <T> ArrayRange<T> range(ArrayList<T> _list)  {
        T[] arr;
        try{
            Field f = ArrayList.class.getDeclaredField("elementData");
            f.setAccessible(true);
            arr = (T[])f.get(_list);

            return((ArrayRange<T>)new ArrayRange(arr, _list.size()));
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            return (null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return (null);
        }
    }

    static public <T> ArrayRange<T> range(T[] _arr)  {
        return((ArrayRange<T>)new ArrayRange(_arr));
    }

    static public IntRange range(int _from, int _to){
        return(new IntRange(_from, _to));
    }
    static public IntRange range(int _to){
        return(new IntRange(0, _to));
    }

}
