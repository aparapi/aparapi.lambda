package com.amd.aparapi;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;


public class TypeHelper{

   static final char VOID = 'V';

   static final char BOOLEAN = 'Z';

   static final char STRING = 's'; // Annotation constantpool entries use this

   static final char ENUM = 'e'; // Annotation constantpool entries use this

   static final char CLASS = 'c'; // Annotation constantpool entries use this

   static final char ANNOTATION = '@'; // Annotation constantpool entries use this

   static final char ARRAY = 'a'; // Annotation constantpool entries use this

   static final char BYTE = 'B';

   static final char CHAR = 'C';

   static final char SHORT = 'S';

   static final char INT = 'I';

   static final char LONG = 'J';

   static final char FLOAT = 'F';

   static final char DOUBLE = 'D';

   static final char ARRAY_DIM = '[';

   static final char CLASS_START = 'L';

   static final char ARG_START = '(';

   static final char CLASS_END = ';';

   static final char ARG_END = ')';

   static final char SLASH = '/';

   static final char DOT = '.';

   static final char UNDERSCORE = '_';


   /**
    * Convert a given JNI character signature (say 'I') to its signature name ('int').
    *
    * @param _typeChar
    * @return either a mapped signature name or null if no mapping exists.
    */
   static String typeName(char _typeChar){
      String returnName = null;
      switch(_typeChar){
         case VOID:
            returnName = "void";
            break;
         case INT:
            returnName = "int";
            break;
         case DOUBLE:
            returnName = "double";
            break;
         case FLOAT:
            returnName = "float";
            break;
         case SHORT:
            returnName = "short";
            break;
         case CHAR:
            returnName = "char";
            break;
         case BYTE:
            returnName = "byte";
            break;
         case LONG:
            returnName = "long";
            break;
         case BOOLEAN:
            returnName = "boolean";
            break;
      }
      return (returnName);
   }

   static String convert(String _string){
      return (convert(_string, "", false));
   }

   static String convert(String _string, String _insert){
      return (convert(_string, _insert, false));
   }

   static String convert(String _string, String _insert, boolean _showFullClassName){
      Stack<String> stringStack = new Stack<String>();
      Stack<String> methodStack = null;
      int length = _string.length();
      char[] chars = _string.toCharArray();
      int i = 0;
      boolean inArray = false;
      boolean inMethod = false;
      boolean inArgs = false;
      int args = 0;
      while(i < length){
         switch(chars[i]){
            case CLASS_START:{
               StringBuilder classNameBuffer = new StringBuilder();
               i++;
               while((i < length) && chars[i] != CLASS_END){
                  if(chars[i] == SLASH){
                     classNameBuffer.append(DOT);
                  }else{
                     classNameBuffer.append(chars[i]);
                  }
                  i++;
               }
               i++; // step over CLASS_END
               String className = classNameBuffer.toString();
               if(_showFullClassName){
                  if(className.startsWith("java.lang")){
                     className = className.substring("java.lang.".length());
                  }
               }else{
                  int lastDot = className.lastIndexOf(DOT);
                  if(lastDot > 0){
                     className = className.substring(lastDot + 1);
                  }
               }
               if(inArray){
                  // swap the stack items
                  String popped = stringStack.pop();
                  if(inArgs && args > 0){
                     stringStack.push(", ");
                  }
                  stringStack.push(className);
                  stringStack.push(popped);
                  inArray = false;
               }else{
                  if(inArgs && args > 0){
                     stringStack.push(", ");
                  }
                  stringStack.push(className);
               }
               args++;
            }
            break;
            case ARRAY_DIM:{
               StringBuilder arrayDims = new StringBuilder();
               while((i < length) && chars[i] == ARRAY_DIM){
                  arrayDims.append("[]");
                  i++;
               }
               stringStack.push(arrayDims.toString());
               inArray = true;
            }
            break;
            case VOID:
            case INT:
            case DOUBLE:
            case FLOAT:
            case SHORT:
            case CHAR:
            case BYTE:
            case LONG:
            case BOOLEAN:{
               if(inArray){
                  // swap the stack items
                  String popped = stringStack.pop();
                  if(inArgs && args > 0){
                     stringStack.push(", ");
                  }
                  stringStack.push(typeName(chars[i]));
                  stringStack.push(popped);
                  inArray = false;
               }else{
                  if(inArgs && args > 0){
                     stringStack.push(", ");
                  }
                  stringStack.push(typeName(chars[i]));
               }
               i++; // step over this
            }
            break;
            case ARG_START:{
               stringStack.push("(");
               i++; // step over this
               inArgs = true;
               args = 0;
            }
            break;
            case ARG_END:{
               inMethod = true;
               inArgs = false;
               stringStack.push(")");
               methodStack = stringStack;
               stringStack = new Stack<String>();
               i++; // step over this
            }
            break;
            default:
               throw new IllegalStateException("invalid prefix!");
         }
      }

      StringBuilder returnValue = new StringBuilder();
      for(String s : stringStack){
         returnValue.append(s);
         returnValue.append(" ");

      }
      if(inMethod){
         for(String s : methodStack){
            returnValue.append(s);
            returnValue.append(" ");
         }
      }else{
         returnValue.append(_insert);
      }
      return (returnValue.toString());
   }

   /**
    * Convert a signature form "Lpackage/Name;" or array form to dot class form.
    * <p/>
    * signatureToDotClassName("Lpackage/Outer$Name;", 0) -> "package.Outer.Name"
    * signatureToDotClassName("[Lpackage/Outer$Name;", 1) -> "package.Outer.Name"
    *
    * @param _signature
    * @param _dims
    * @return
    */
   public static String signatureToDotClassName(String _signature, int _dims){
      String dotClassName = slashClassNameToDotClassName(_signature.substring(1 + _dims, _signature.length() - 1));
      return (dotClassName);
   }

   public static String signatureToMangledClassName(String _signature, int _dims){
      String mangledClassName = slashClassNameToMangledClassName(_signature.substring(1 + _dims, _signature.length() - 1));
      return (mangledClassName);
   }

   /**
    * @param _dotClassName
    * @param _dims
    * @return
    */
   public static String dotClassNameToSignature(String _dotClassName, int _dims){
      StringBuilder sb = new StringBuilder();
      for(int i = 0; i < _dims; i++){
         sb.append(ARRAY_DIM);
      }
      sb.append(CLASS_START).append(dotClassNameToSlashClassName(_dotClassName)).append(CLASS_END);
      return (sb.toString());
   }

   public static String dotClassNameToMangledClassName(String _dotClassName){
      return (_dotClassName.replace(DOT, UNDERSCORE));
   }

   /**
    * @param _dotClassName
    * @return
    */
   public static String dotClassNameToSlashClassName(String _dotClassName){
      return (_dotClassName.replace(DOT, SLASH));
   }

   /**
    * @param _slashClassName
    * @return
    */
   public static String slashClassNameToDotClassName(String _slashClassName){
      return (_slashClassName.replace(SLASH, DOT));
   }

   /**
    * @param _slashClassName
    * @return
    */
   public static String slashClassNameToMangledClassName(String _slashClassName){
      return (_slashClassName.replace(SLASH, UNDERSCORE));
   }


   static final Map<String, JavaType> typeMap = new HashMap<String, JavaType>();

   static{
      typeMap.put(PrimitiveType.u1.getJavaSig(), new JavaType(PrimitiveType.u1));// boolean
      typeMap.put(PrimitiveType.s8.getJavaSig(), new JavaType(PrimitiveType.s8));// byte
      typeMap.put(PrimitiveType.s16.getJavaSig(), new JavaType(PrimitiveType.s16));// short
      typeMap.put(PrimitiveType.u16.getJavaSig(), new JavaType(PrimitiveType.u16));// char
      typeMap.put(PrimitiveType.s32.getJavaSig(), new JavaType(PrimitiveType.s32));// int
      typeMap.put(PrimitiveType.f32.getJavaSig(), new JavaType(PrimitiveType.f32));// float
      typeMap.put(PrimitiveType.s64.getJavaSig(), new JavaType(PrimitiveType.s64));// long
      typeMap.put(PrimitiveType.f64.getJavaSig(), new JavaType(PrimitiveType.f64));// double
   }


   static String createSignature(Class _clazz){
      String arrayPrefix = "";
      String signature = null;
      Class componentType = _clazz;
      if(_clazz.isArray()){

         componentType = _clazz.getComponentType();
         int arrayDimensions = _clazz.getName().lastIndexOf('[') + 1;
         arrayPrefix = _clazz.getName().substring(0, arrayDimensions);
      }
      for(PrimitiveType p : PrimitiveType.javaPrimitiveTypes){
         if(p.getClazz().equals(componentType)){
            signature = arrayPrefix + p.getJavaSig();
            break;
         }
      }
      if(signature == null){
         signature = arrayPrefix + CLASS_START + dotClassNameToSlashClassName(_clazz.getName()) + CLASS_END;
      }
      return (signature);
   }

   static String createSignature(PrimitiveType _primitiveType){
      return (_primitiveType.getJavaSig());
   }


   static synchronized JavaType getJavaType(String _signature){
      if(_signature.contains("]")){
         throw new IllegalStateException("whoa!");
      }
      JavaType type = typeMap.get(_signature);
      if(type == null){
         type = new JavaType(_signature);
         typeMap.put(_signature, type);
      }

      return (type);
   }

   static synchronized JavaType getJavaType(Class _clazz){
      String signature = createSignature(_clazz);
      return (getJavaType(signature));
   }

   static class JavaType{
      private int arrayDimensions = 0;
      private String signature;
      private PrimitiveType type; // I if int  OREF if array (or primitive or object or array) or object

      private JavaType(PrimitiveType _primitiveType){
         signature = createSignature(_primitiveType);
         arrayDimensions = 0;
         type = _primitiveType;

      }

      PrimitiveType getPrimitiveType(){
         return ((PrimitiveType) type);
      }

      private JavaType(String _signature){
         arrayDimensions = _signature.startsWith("[") ? _signature.lastIndexOf('[') + 1 : 0;
         signature = _signature;
         type = PrimitiveType.ref;
      }

      String getSignature(){
         return (signature);
      }

      boolean isVoid(){
         return (arrayDimensions == 0 && type instanceof v);
      }

      boolean isInt(){
         return (arrayDimensions == 0 && type instanceof s32);
      }

      boolean isLong(){
         return (arrayDimensions == 0 && type instanceof s64);
      }

      boolean isShort(){
         return (arrayDimensions == 0 && type instanceof s16);
      }

      boolean isBoolean(){
         return (arrayDimensions == 0 && type instanceof u1);
      }

      boolean isChar(){
         return (arrayDimensions == 0 && type instanceof u16);
      }

      boolean isFloat(){
         return (arrayDimensions == 0 && type instanceof f32);
      }

      boolean isDouble(){
         return (arrayDimensions == 0 && type instanceof f64);
      }

      boolean isByte(){
         return (arrayDimensions == 0 && type instanceof s8);
      }

      boolean isObject(){
         return (arrayDimensions == 0 && !isPrimitive());
      }

      String getObjectClassName(){
         return (TypeHelper.signatureToDotClassName(signature, 0));
      }

      String getMangledClassName(){
         return (TypeHelper.signatureToMangledClassName(signature, 0));
      }


      final boolean isArray(){
         return (arrayDimensions > 0);
      }

      final int getArrayDimensions(){
         return (arrayDimensions);
      }

      final boolean isArrayOfObjects(int _dim){
         return (isArray() && getArrayDimensions() == _dim && isObject());
      }

      final boolean isPrimitive(){
         return (isInt() || isFloat() || isDouble() || isChar() || isLong() || isShort() || isByte() || isVoid());
      }

      String primitiveCharToJavaName(char _ch){
         switch(_ch){
            case VOID:
               return ("void");
            case DOUBLE:
               return ("double");
            case FLOAT:
               return ("float");
            case INT:
               return ("int");
            case CHAR:
               return ("char");
            case BYTE:
               return ("byte");
            case SHORT:
               return ("short");
            case LONG:
               return ("long");
            case BOOLEAN:
               return ("boolean");
         }
         return ("?");
      }


      String getOpenCLName(){
         String openCLName = null;
         if(isArray()){
            openCLName = primitiveCharToJavaName(signature.charAt(arrayDimensions)) + "*";
         }else if(isPrimitive() || isVoid()){
            openCLName = primitiveCharToJavaName(signature.charAt(arrayDimensions));
         }else if(isObject()){
            openCLName = getObjectClassName();
         }
         return (openCLName);
      }

      @Override
      public String toString(){

         // StringBuilder sb = new StringBuilder(getJavaNamer());
         // for (int i = 0; i < arrayDimensions; i++) {
         //     sb.append("[]");
         // }
         throw new IllegalStateException("no toString!");
         //return (sb.toString());
      }
   }

   public static class Arg{
      JavaType type;

      Arg(String _signature, int _start, int _pos, int _argc){
         type = TypeHelper.getJavaType(_signature.substring(_start, _pos + 1));
         argc = _argc;
      }

      Arg(Class _clazz, int _argc){
         type = TypeHelper.getJavaType(_clazz);
         argc = _argc;
      }

      private int argc;

      int getArgc(){
         return (argc);
      }

      public JavaType getJavaType(){
         return (type);
      }
   }

   public static class MethodInfo{
      ArgsAndReturnType argsAndReturnType;
      JavaType containingClass;
      String methodName;

      public MethodInfo(Method _method){
         argsAndReturnType = new ArgsAndReturnType(_method);
         containingClass = TypeHelper.getJavaType(_method.getDeclaringClass());
         methodName = _method.getName();
      }

      public MethodInfo(ClassModel.ConstantPool.MethodEntry _methodEntry){
         argsAndReturnType = _methodEntry.getArgsAndReturnType();
         containingClass = _methodEntry.getContainingClass();
         methodName = _methodEntry.getName();

      }

      public boolean equals(MethodInfo _other){
         return (_other.methodName.equals(methodName) && _other.containingClass.equals(containingClass) && _other.argsAndReturnType.equals(argsAndReturnType));
      }

   }

   public static class FieldInfo{
      JavaType type;
      JavaType containingClass;
      String fieldName;

      FieldInfo(Field _field){
         type = TypeHelper.getJavaType(_field.getType());
         containingClass = TypeHelper.getJavaType(_field.getDeclaringClass());
         fieldName = _field.getName();
      }

      FieldInfo(ClassModel.ConstantPool.FieldEntry _fieldEntry){
         type = _fieldEntry.getType();
         containingClass = _fieldEntry.getContainingClass();
         fieldName = _fieldEntry.getName();
      }

      public boolean equals(FieldInfo _other){
         return (_other.fieldName.equals(fieldName) && _other.containingClass.equals(containingClass) && _other.type.equals(type));
      }
   }

   public static class ArgsAndReturnType{
      private static enum SignatureParseState{
         skipping,
         inArgs,
         inClass,
         inArray,
         done;
      }

      ;


      Arg[] args;
      JavaType returnType;

      public Arg[] getArgs(){
         return (args);
      }

      public JavaType getReturnType(){
         return (returnType);
      }


      public ArgsAndReturnType(String _signature){


         SignatureParseState state = SignatureParseState.skipping;
         List<Arg> argList = new ArrayList<Arg>();
         int start = 0;

         for(int pos = 0; state != SignatureParseState.done; pos++){
            char ch = _signature.charAt(pos);
            switch(ch){
               case ARG_START:
                  state = SignatureParseState.inArgs;
                  break;
               case ARG_END:
                  state = SignatureParseState.done;
                  returnType = TypeHelper.getJavaType(_signature.substring(pos + 1));
                  break;
               case ARRAY_DIM:
                  switch(state){
                     case inArgs:
                        state = SignatureParseState.inArray;
                        start = pos;
                        break;

                  }
                  // we don't care about arrays
                  break;
               case CLASS_START:
                  // beginning of Ljava/lang/String; or something
                  switch(state){
                     case inArgs:
                        start = pos;
                        state = SignatureParseState.inClass;
                        break;
                     case inArray:
                        state = SignatureParseState.inClass;
                        break;
                  }
                  break;
               case CLASS_END:
                  argList.add(new Arg(_signature, start, pos, argList.size()));
                  state = SignatureParseState.inArgs;
                  break;
               default:
                  // we have IJBZDF so inc counter if we are still inArgs
                  switch(state){
                     case inArgs:
                        start = pos;
                        argList.add(new Arg(_signature, start, pos, argList.size()));
                        state = SignatureParseState.inArgs;
                        break;
                     case inArray:
                        argList.add(new Arg(_signature, start, pos, argList.size()));
                        state = SignatureParseState.inArgs;
                        break;

                  }
                  break;
            }

         }
         args = argList.toArray(new Arg[0]);
      }

      public ArgsAndReturnType(Method _method){
         args = new Arg[_method.getParameterCount()];
         Class<?> argsAsClasses[] = _method.getParameterTypes();
         for(int i = 0; i < argsAsClasses.length; i++){
            args[i] = new Arg(argsAsClasses[i], i);
         }
         returnType = TypeHelper.getJavaType(_method.getReturnType());
      }

      public boolean matches(Method _method){
         return (returnType == getJavaType(_method.getReturnType()));
      }

   }

}
