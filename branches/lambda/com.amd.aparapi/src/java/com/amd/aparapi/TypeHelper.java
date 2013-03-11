package com.amd.aparapi;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;


public class TypeHelper{

   static final char VOID = 'V';

   static final char BOOLEAN = 'Z';

   static final char BYTE = 'B';

   static final char CHAR = 'C';

   static final char SHORT = 'S';

   static final char INT = 'I';

   static final char LONG = 'J';

   static final char FLOAT = 'F';

   static final char DOUBLE = 'D';

   static final char ARRAY = '[';

   static final char CLASS_START = 'L';

   static final char ARG_START = '(';

   static final char CLASS_END = ';';

   static final char ARG_END = ')';

   static final char SLASH = '/';

   static final char DOT = '.';

   static final char UNDERSCORE = '_';


   /**
    * Convert a given JNI character type (say 'I') to its type name ('int').
    *
    * @param _typeChar
    * @return either a mapped type name or null if no mapping exists.
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
            case ARRAY:{
               StringBuilder arrayDims = new StringBuilder();
               while((i < length) && chars[i] == ARRAY){
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
      return (slashClassNameToDotClassName(_signature.substring(1 + _dims, _signature.length() - _dims - 1)));
   }

   public static String signatureToMangledClassName(String _signature, int _dims){
      return (slashClassNameToMangledClassName(_signature.substring(1 + _dims, _signature.length() - _dims - 1)));
   }

   /**
    * @param _dotClassName
    * @param _dims
    * @return
    */
   public static String dotClassNameToSignature(String _dotClassName, int _dims){
      StringBuilder sb = new StringBuilder();
      for(int i = 0; i < _dims; i++){
         sb.append(ARRAY);
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

   static class Type{
      private int arrayDimensions = 0;

      Type(String _type){
         type = _type;

         while(type.charAt(arrayDimensions) == ARRAY){
            arrayDimensions++;
         }
         type = type.substring(arrayDimensions);
      }

      String getType(){
         return (type);
      }

      boolean isVoid(){
         return (type.charAt(0) == VOID);
      }

      boolean isInt(){
         return (type.charAt(0) == INT);
      }

      boolean isLong(){
         return (type.charAt(0) == LONG);
      }

      boolean isShort(){
         return (type.charAt(0) == SHORT);
      }

      boolean isBoolean(){
         return (type.charAt(0) == BOOLEAN);
      }

      boolean isChar(){
         return (type.charAt(0) == CHAR);
      }

      boolean isFloat(){
         return (type.charAt(0) == FLOAT);
      }

      boolean isDouble(){
         return (type.charAt(0) == DOUBLE);
      }

      boolean isByte(){
         return (type.charAt(0) == BYTE);
      }

      boolean isObject(){
         return (type.charAt(0) == CLASS_START);
      }

      String getObjectClassName(){
         return (TypeHelper.signatureToDotClassName(type, 0));
      }

      String getMangledClassName(){
         return (TypeHelper.signatureToMangledClassName(type, 0));
      }

      private String type;

      final boolean isArray(){
         return (arrayDimensions > 0);
      }

      final int getArrayDimensions(){
         return (arrayDimensions);
      }

      final boolean isArrayOfObjects(int _dim){
         return (isArray() && getArrayDimensions() == _dim && isObject());
      }

      final boolean isArrayOfInts(int _dim){
         return (isArray() && getArrayDimensions() == _dim && isInt());
      }

      final boolean isArrayOfFloats(int _dim){
         return (isArray() && getArrayDimensions() == _dim && isFloat());
      }

      final boolean isArrayOfDoubles(int _dim){
         return (isArray() && getArrayDimensions() == _dim && isDouble());
      }

      final boolean isArrayOfChars(int _dim){
         return (isArray() && getArrayDimensions() == _dim && isChar());
      }

      final boolean isArrayOfLong(int _dim){
         return (isArray() && getArrayDimensions() == _dim && isLong());
      }

      final boolean isArrayOfShorts(int _dim){
         return (isArray() && getArrayDimensions() == _dim && isShort());
      }

      final boolean isArrayOfBytes(int _dim){
         return (isArray() && getArrayDimensions() == _dim && isByte());
      }

      final boolean isPrimitive(){
         return (isInt() || isFloat() || isDouble() || isChar() || isLong() || isShort() || isByte());
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

      String getJavaName(){
         String javaName = "?";
         if(isPrimitive()){
            javaName = primitiveCharToJavaName(type.charAt(0));
         }else if(isObject()){
            javaName = getObjectClassName();
         }
         return (javaName);
      }

      public boolean matches(Class<?> _type){
         String javaName = getJavaName();
         return (_type.getName().equals(javaName));
      }
   }

   static class Arg extends Type{
      Arg(String _signature, int _start, int _pos, int _argc){
         super(_signature.substring(_start, _pos + 1));
         argc = _argc;
      }

      private int argc;

      int getArgc(){
         return (argc);
      }
   }

   static class ArgsAndReturnType{
      private static enum SignatureParseState{
         skipping,
         counting,
         inClass,
         inArray,
         done;
      }

      ;
      Arg[] args;
      Type returnType;

      public Arg[] getArgs(){
         return (args);
      }

      public Type getReturnType(){
         return (returnType);
      }


      ArgsAndReturnType(String _signature){


         SignatureParseState state = SignatureParseState.skipping;
         List<Arg> argList = new ArrayList<Arg>();
         int start = 0;

         for(int pos = 0; state != SignatureParseState.done; pos++){
            char ch = _signature.charAt(pos);
            switch(ch){
               case ARG_START:
                  state = SignatureParseState.counting;
                  break;
               case ARG_END:
                  state = SignatureParseState.done;
                  returnType = new Type(_signature.substring(pos + 1));
                  break;
               case ARRAY:
                  switch(state){
                     case counting:
                        state = SignatureParseState.inArray;
                        start = pos;
                        break;

                  }
                  // we don't care about arrays
                  break;
               case CLASS_START:
                  // beginning of Ljava/lang/String; or something

                  switch(state){
                     case counting:
                        start = pos;
                        // fallthrough intended!!
                     case inArray:
                        state = SignatureParseState.inClass;
                        break;
                  }
                  break;
               case CLASS_END:
                  // note we will only be in 'inclass' if we were previously counting, so this is safe
                  switch(state){
                     case inClass:
                        argList.add(new Arg(_signature, start, pos, argList.size()));
                        state = SignatureParseState.counting;
                        break;
                  }
                  break;

               default:
                  // we have IJBZDF so inc counter if we are still counting
                  switch(state){
                     case counting:
                        start = pos;
                        // fallthrough intended!!
                     case inArray:
                        argList.add(new Arg(_signature, start, pos, argList.size()));
                        break;

                  }
                  break;
            }

         }
         // System.out.println("method "+name+" has signature of "+signature+" which has "+count+" args");

         args = argList.toArray(new Arg[0]);
      }

      public boolean matches(Method _method){
         return (returnType.matches(_method.getReturnType()));

      }
   }
}
