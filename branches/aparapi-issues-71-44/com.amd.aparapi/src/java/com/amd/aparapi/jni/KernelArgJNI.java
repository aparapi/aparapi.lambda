package com.amd.aparapi.jni;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;

import com.amd.aparapi.jni.OpenCLJNI.UsedByJNICode;
import com.amd.aparapi.model.ClassModel;

/**
 * This class is intended to be used as a 'proxy' or 'facade' object for Java code to interact with JNI
 * <p>
 * Each field (or captured field in the case of an anonymous inner class) referenced by any bytecode reachable from the users Kernel.run(), will
 * need to be represented as a <code>KernelArg</code>.
 * 
 * @see com.amd.aparapi.Kernel#execute(int _globalSize)
 * 
 * @author gfrost
 * 
 */
public class KernelArgJNI {

   /**
    * The type of this KernelArg. Created by oring appropriate flags
    * 
    * @see ARG_BOOLEAN
    * @see ARG_BYTE
    * @see ARG_CHAR
    * @see ARG_FLOAT
    * @see ARG_INT
    * @see ARG_DOUBLE
    * @see ARG_LONG
    * @see ARG_SHORT
    * @see ARG_ARRAY
    * @see ARG_PRIMITIVE
    * @see ARG_READ
    * @see ARG_WRITE
    * @see ARG_LOCAL
    * @see ARG_GLOBAL
    * @see ARG_CONSTANT
    * @see ARG_ARRAYLENGTH
    * @see ARG_APARAPI_BUF
    * @see ARG_EXPLICIT
    * @see ARG_EXPLICIT_WRITE
    * @see ARG_OBJ_ARRAY_STRUCT
    * @see ARG_APARAPI_BUF_HAS_ARRAY
    * @see ARG_APARAPI_BUF_IS_DIRECT
    */
   @UsedByJNICode
   private int type;

   /**
    * Name of the field
    */
   @UsedByJNICode
   private String name;

   /**
    * If this field represents a Java array then the instance will be captured here
    */
   @UsedByJNICode
   private Object javaArray;

   /**
    * If this is an array or a buffer then the size (in bytes) is held here
    */
   @UsedByJNICode
   private int sizeInBytes;

   /**
    * If this is an array buffer then the number of elements is stored here
    */
   @UsedByJNICode
   private int numElements;

   /**
    * If this is an array buffer then the number of elements is stored here.
    * 
    * At present only set for AparapiLocalBuffer objs, JNI multiplies this by localSize
    */
   //  @Annotations.Unused @UsedByJNICode public int bytesPerLocalWidth;

   /**
    * Only set for array objs, not used on JNI
    */
   @UsedByJNICode
   private Object array;

   /**
    * Field in Kernel class corresponding to this arg
    */
   @UsedByJNICode
   private Field field;

   /**
    * The byte array for obj conversion passed to opencl
    */
   private byte[] objArrayBuffer;

   /**
    * The ByteBuffer fronting the byte array
    */
   private ByteBuffer objArrayByteBuffer;

   /**
    * ClassModel of the array elements (not used on JNI side)
    * 
    */
   private ClassModel objArrayElementModel;

   /**
    * Only set for AparapiBuffer objs,
    */
   private Object primitiveBuf;

   /**
    * Size of this primitive
    */
   private int primitiveSize;

   /**
    * Default constructor
    */
   public KernelArgJNI() {

   }

   /**
    * @return the type
    */
   public int getType() {
      return type;
   }

   /**
    * @param type the type to set
    */
   public void setType(int type) {
      this.type = type;
   }

   /**
    * @return the name
    */
   public String getName() {
      return name;
   }

   /**
    * @param name the name to set
    */
   public void setName(String name) {
      this.name = name;
   }

   /**
    * @return the javaArray
    */
   public Object getJavaArray() {
      return javaArray;
   }

   /**
    * @param javaArray the javaArray to set
    */
   public void setJavaArray(Object javaArray) {
      this.javaArray = javaArray;
   }

   /**
    * @return the sizeInBytes
    */
   public int getSizeInBytes() {
      return sizeInBytes;
   }

   /**
    * @param sizeInBytes the sizeInBytes to set
    */
   public void setSizeInBytes(int sizeInBytes) {
      this.sizeInBytes = sizeInBytes;
   }

   /**
    * @return the numElements
    */
   public int getNumElements() {
      return numElements;
   }

   /**
    * @param numElements the numElements to set
    */
   public void setNumElements(int numElements) {
      this.numElements = numElements;
   }

   /**
    * @return the array
    */
   public Object getArray() {
      return array;
   }

   /**
    * @param array the array to set
    */
   public void setArray(Object array) {
      this.array = array;
   }

   /**
    * @return the field
    */
   public Field getField() {
      return field;
   }

   /**
    * @param field the field to set
    */
   public void setField(Field field) {
      this.field = field;
   }

   /**
    * @return the objArrayBuffer
    */
   public byte[] getObjArrayBuffer() {
      return objArrayBuffer;
   }

   /**
    * @param objArrayBuffer the objArrayBuffer to set
    */
   public void setObjArrayBuffer(byte[] objArrayBuffer) {
      this.objArrayBuffer = objArrayBuffer;
   }

   /**
    * @return the objArrayByteBuffer
    */
   public ByteBuffer getObjArrayByteBuffer() {
      return objArrayByteBuffer;
   }

   /**
    * @param objArrayByteBuffer the objArrayByteBuffer to set
    */
   public void setObjArrayByteBuffer(ByteBuffer objArrayByteBuffer) {
      this.objArrayByteBuffer = objArrayByteBuffer;
   }

   /**
    * @return the objArrayElementModel
    */
   public ClassModel getObjArrayElementModel() {
      return objArrayElementModel;
   }

   /**
    * @param objArrayElementModel the objArrayElementModel to set
    */
   public void setObjArrayElementModel(ClassModel objArrayElementModel) {
      this.objArrayElementModel = objArrayElementModel;
   }

   /**
    * @return the primitiveBuf
    */
   public Object getPrimitiveBuf() {
      return primitiveBuf;
   }

   /**
    * @param primitiveBuf the primitiveBuf to set
    */
   public void setPrimitiveBuf(Object primitiveBuf) {
      this.primitiveBuf = primitiveBuf;
   }

   /**
    * @return the primitiveSize
    */
   public int getPrimitiveSize() {
      return primitiveSize;
   }

   /**
    * @param primitiveSize the primitiveSize to set
    */
   public void setPrimitiveSize(int primitiveSize) {
      this.primitiveSize = primitiveSize;
   }
}
