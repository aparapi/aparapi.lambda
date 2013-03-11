/*
Copyright (c) 2010-2011, Advanced Micro Devices, Inc.
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following
disclaimer. 

Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
disclaimer in the documentation and/or other materials provided with the distribution. 

Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products
derived from this software without specific prior written permission. 

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE 
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

If you use the software (in whole or in part), you shall adhere to all applicable U.S., European, and other export
laws, including but not limited to the U.S. Export Administration Regulations ("EAR"), (15 C.F.R. Sections 730 through
774), and E.U. Council Regulation (EC) No 1334/2000 of 22 June 2000.  Further, pursuant to Section 740.6 of the EAR,
you hereby certify that, except pursuant to a license granted by the United States Department of Commerce Bureau of 
Industry and Security or as otherwise permitted pursuant to a License Exception under the U.S. Export Administration 
Regulations ("EAR"), you will not (1) export, re-export or release to a national of a country in Country Groups D:1,
E:1 or E:2 any restricted technology, software, or source code you receive hereunder, or (2) export to Country Groups
D:1, E:1 or E:2 the direct product of such technology or software, if such foreign produced direct product is subject
to national security controls as identified on the Commerce Control List (currently found in Supplement 1 to Part 774
of EAR).  For the most current Country Group listings, or for additional information about the EAR or your obligations
under those regulations, please refer to the U.S. Bureau of Industry and Security's website at http://www.bis.doc.gov/. 

 */
package com.amd.aparapi;

import com.amd.aparapi.ClassModel.AttributePool.RuntimeAnnotationsEntry;
import com.amd.aparapi.ClassModel.AttributePool.RuntimeAnnotationsEntry.AnnotationInfo;
import com.amd.aparapi.ClassModel.ClassModelField;
import com.amd.aparapi.ClassModel.ConstantPool.FieldEntry;
import com.amd.aparapi.ClassModel.ConstantPool.MethodEntry;
import com.amd.aparapi.ClassModel.LocalVariableInfo;
import com.amd.aparapi.ClassModel.LocalVariableTableEntry;
import com.amd.aparapi.InstructionSet.AccessArrayElement;
import com.amd.aparapi.InstructionSet.AccessLocalVariable;
import com.amd.aparapi.InstructionSet.AssignToArrayElement;
import com.amd.aparapi.InstructionSet.AssignToField;
import com.amd.aparapi.InstructionSet.AssignToLocalVariable;
import com.amd.aparapi.InstructionSet.BinaryOperator;
import com.amd.aparapi.InstructionSet.FieldReference;
import com.amd.aparapi.InstructionSet.I_ALOAD_0;
import com.amd.aparapi.InstructionSet.I_INVOKESPECIAL;
import com.amd.aparapi.InstructionSet.I_IUSHR;
import com.amd.aparapi.InstructionSet.I_LUSHR;
import com.amd.aparapi.InstructionSet.MethodCall;
import com.amd.aparapi.InstructionSet.VirtualMethodCall;
import com.amd.aparapi.TypeHelper.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

abstract class KernelWriter extends BlockWriter{

   final static String cvtBooleanToChar = "char ";

   final static String cvtBooleanArrayToCharStar = "char* ";

   final static String cvtByteToChar = "char ";

   final static String cvtByteArrayToCharStar = "char* ";

   final static String cvtCharToShort = "unsigned short ";

   final static String cvtCharArrayToShortStar = "unsigned short* ";

   final static String cvtIntArrayToIntStar = "int* ";

   final static String cvtFloatArrayToFloatStar = "float* ";

   final static String cvtDoubleArrayToDoubleStar = "double* ";

   final static String cvtLongArrayToLongStar = "long* ";

   final static String cvtShortArrayToShortStar = "short* ";

   // private static Logger logger = Logger.getLogger(Config.getLoggerName());

   Entrypoint entryPoint = null;

   final static Map<String, String> javaToCLIdentifierMap = new HashMap<String, String>();

   {

      javaToCLIdentifierMap.put("getGlobalId()I", "get_global_id(0)");
      javaToCLIdentifierMap.put("getGlobalId(I)I", "get_global_id"); // no parenthesis if we are conveying args
      javaToCLIdentifierMap.put("getGlobalX()I", "get_global_id(0)");
      javaToCLIdentifierMap.put("getGlobalY()I", "get_global_id(1)");
      javaToCLIdentifierMap.put("getGlobalZ()I", "get_global_id(2)");

      javaToCLIdentifierMap.put("getGlobalSize()I", "get_global_size(0)");
      javaToCLIdentifierMap.put("getGlobalSize(I)I", "get_global_size"); // no parenthesis if we are conveying args
      javaToCLIdentifierMap.put("getGlobalWidth()I", "get_global_size(0)");
      javaToCLIdentifierMap.put("getGlobalHeight()I", "get_global_size(1)");
      javaToCLIdentifierMap.put("getGlobalDepth()I", "get_global_size(2)");

      javaToCLIdentifierMap.put("getLocalId()I", "get_local_id(0)");
      javaToCLIdentifierMap.put("getLocalId(I)I", "get_local_id"); // no parenthesis if we are conveying args
      javaToCLIdentifierMap.put("getLocalX()I", "get_local_id(0)");
      javaToCLIdentifierMap.put("getLocalY()I", "get_local_id(1)");
      javaToCLIdentifierMap.put("getLocalZ()I", "get_local_id(2)");

      javaToCLIdentifierMap.put("getLocalSize()I", "get_local_size(0)");
      javaToCLIdentifierMap.put("getLocalSize(I)I", "get_local_size"); // no parenthesis if we are conveying args
      javaToCLIdentifierMap.put("getLocalWidth()I", "get_local_size(0)");
      javaToCLIdentifierMap.put("getLocalHeight()I", "get_local_size(1)");
      javaToCLIdentifierMap.put("getLocalDepth()I", "get_local_size(2)");

      javaToCLIdentifierMap.put("getNumGroups()I", "get_num_groups(0)");
      javaToCLIdentifierMap.put("getNumGroups(I)I", "get_num_groups"); // no parenthesis if we are conveying args
      javaToCLIdentifierMap.put("getNumGroupsX()I", "get_num_groups(0)");
      javaToCLIdentifierMap.put("getNumGroupsY()I", "get_num_groups(1)");
      javaToCLIdentifierMap.put("getNumGroupsZ()I", "get_num_groups(2)");

      javaToCLIdentifierMap.put("getGroupId()I", "get_group_id(0)");
      javaToCLIdentifierMap.put("getGroupId(I)I", "get_group_id"); // no parenthesis if we are conveying args
      javaToCLIdentifierMap.put("getGroupX()I", "get_group_id(0)");
      javaToCLIdentifierMap.put("getGroupY()I", "get_group_id(1)");
      javaToCLIdentifierMap.put("getGroupZ()I", "get_group_id(2)");

      javaToCLIdentifierMap.put("getPassId()I", "get_pass_id(this)");

      javaToCLIdentifierMap.put("localBarrier()V", "barrier(CLK_LOCAL_MEM_FENCE)");

      javaToCLIdentifierMap.put("globalBarrier()V", "barrier(CLK_GLOBAL_MEM_FENCE)");

   }

   /**
    * These three convert functions are here to perform
    * any type conversion that may be required between
    * Java and OpenCL.
    *
    * @param _typeDesc String in the Java JNI notation, [I, etc
    * @return Suitably converted string, "char*", etc
    */
   @Override
   protected String convertType(String _typeDesc, boolean useClassModel){
      return KernelWriter.convertType0(_typeDesc, useClassModel);
   }

   public static String convertType0(String _typeDesc, boolean useClassModel){
      if(_typeDesc.equals("Z") || _typeDesc.equals("boolean")){
         return (cvtBooleanToChar);
      }else if(_typeDesc.equals("[Z") || _typeDesc.equals("boolean[]")){
         return (cvtBooleanArrayToCharStar);
      }else if(_typeDesc.equals("B") || _typeDesc.equals("byte")){
         return (cvtByteToChar);
      }else if(_typeDesc.equals("[B") || _typeDesc.equals("byte[]")){
         return (cvtByteArrayToCharStar);
      }else if(_typeDesc.equals("C") || _typeDesc.equals("char")){
         return (cvtCharToShort);
      }else if(_typeDesc.equals("[C") || _typeDesc.equals("char[]")){
         return (cvtCharArrayToShortStar);
      }else if(_typeDesc.equals("I")){
         return ("int ");
      }else if(_typeDesc.equals("[I") || _typeDesc.equals("int[]")){
         return (cvtIntArrayToIntStar);
      }else if(_typeDesc.equals("F")){
         return ("float ");
      }else if(_typeDesc.equals("[F") || _typeDesc.equals("float[]")){
         return (cvtFloatArrayToFloatStar);
      }else if(_typeDesc.equals("[D") || _typeDesc.equals("double[]")){
         return (cvtDoubleArrayToDoubleStar);
      }else if(_typeDesc.equals("[J") || _typeDesc.equals("long[]")){
         return (cvtLongArrayToLongStar);
      }else if(_typeDesc.equals("[S") || _typeDesc.equals("short[]")){
         return (cvtShortArrayToShortStar);
      }
      // if we get this far, we haven't matched anything yet
      if(useClassModel){
         return (TypeHelper.convert(_typeDesc, "", true));
      }else{
         return _typeDesc;
      }
   }

   @Override
   protected void writeMethod(MethodCall _methodCall, MethodEntry _methodEntry) throws CodeGenException{

      // System.out.println("_methodEntry = " + _methodEntry);
      // special case for buffers

      int argc = _methodEntry.getStackConsumeCount();

      String methodName = _methodEntry.getNameAndTypeEntry().getNameUTF8Entry().getUTF8();
      String methodSignature = _methodEntry.getNameAndTypeEntry().getDescriptorUTF8Entry().getUTF8();

      String barrierAndGetterMappings = javaToCLIdentifierMap.get(methodName + methodSignature);

      if(barrierAndGetterMappings != null){
         // this is one of the OpenCL barrier or size getter methods
         // write the mapping and exit
         if(argc > 0){
            write(barrierAndGetterMappings);
            write("(");
            for(int arg = 0; arg < argc; arg++){
               if((arg != 0)){
                  write(", ");
               }
               writeInstruction(_methodCall.getArg(arg));
            }
            write(")");
         }else{
            write(barrierAndGetterMappings);
         }
      }else{

         String intrinsicMapping = Kernel.getMappedMethodName(_methodEntry);
         // System.out.println("getMappedMethodName for " + methodName + " returned " + mapping);
         boolean isIntrinsic = false;

         if(intrinsicMapping == null){
            assert entryPoint != null : "entryPoint should not be null";
            boolean isSpecial = _methodCall instanceof I_INVOKESPECIAL;
            boolean isMapped = Kernel.isMappedMethod(_methodEntry);
            MethodModel m = entryPoint.getCallTarget(_methodEntry, isSpecial);

            if(m != null){
               write(m.getMangledName());
            }else{
               // Must be a library call like rsqrt
               assert isMapped : _methodEntry + " should be mapped method!";

               write(methodName);
               isIntrinsic = true;
            }
         }else{
            write(intrinsicMapping);
         }

         write("(");

         if((intrinsicMapping == null) && (_methodCall instanceof VirtualMethodCall) && (!isIntrinsic)){

            Instruction i = ((VirtualMethodCall) _methodCall).getInstanceReference();

            if(i instanceof I_ALOAD_0){
               // For I_ALOAD_0, it must be either a call in the lambda class or
               // a call to the iteration object.
               String className = _methodEntry.getClassEntry().getNameUTF8Entry().getUTF8();
               String classNameInDotForm = TypeHelper.slashClassNameToDotClassName(className);
               if(classNameInDotForm.equals(entryPoint.getClassModel().getClassWeAreModelling().getName())){
                  write("this");
               }else{
                  // It must be the iteration object
                  // Insert the syntax to access the iteration object from the source array
                  write(" &(this->elements[elements_array_index])");
               }
            }else if(i instanceof AccessArrayElement){
               AccessArrayElement arrayAccess = (AccessArrayElement) ((VirtualMethodCall) _methodCall).getInstanceReference();
               Instruction refAccess = arrayAccess.getArrayRef();
               if(refAccess instanceof FieldReference){
                  // Calls to objects in arrays that are fields
                  String fieldName = ((FieldReference) refAccess).getConstantPoolFieldEntry().
                        getNameAndTypeEntry().getNameUTF8Entry().getUTF8();
                  write(" &(this->" + fieldName);
               }else if(refAccess instanceof AccessLocalVariable){
                  // This case is to handle lambda argument object array refs
                  AccessLocalVariable localVariableLoadInstruction = (AccessLocalVariable) refAccess;
                  LocalVariableInfo localVariable = localVariableLoadInstruction.getLocalVariableInfo();
                  write(" &(this->" + localVariable.getVariableName());
               }
               write("[");
               writeInstruction(arrayAccess.getArrayIndex());
               write("])");
            }else{
               // Assume it is a call in an object lambda on the iteration object.
               // Insert the syntax to access the iteration object from the source array
               write(" &(this->elements[elements_array_index])");
            }
         }
         for(int arg = 0; arg < argc; arg++){
            if(((intrinsicMapping == null) && (_methodCall instanceof VirtualMethodCall) && (!isIntrinsic)) || (arg != 0)){
               write(", ");
            }
            writeInstruction(_methodCall.getArg(arg));
         }
         write(")");
      }
   }

   void writePragma(String _name, boolean _enable){
      write("#pragma OPENCL EXTENSION " + _name + " : " + (_enable ? "en" : "dis") + "able");
      newLine();
   }

   public final static String __local = "__local";

   public final static String __global = "__global";

   public final static String __constant = "__constant";

   public final static String LOCAL_ANNOTATION_NAME = TypeHelper.dotClassNameToSignature(Kernel.Local.class.getName(), 0);

   public final static String CONSTANT_ANNOTATION_NAME = TypeHelper.dotClassNameToSignature(Kernel.Constant.class.getName(), 0);

   @Override void write(Entrypoint _entryPoint) throws CodeGenException{
      List<String> thisStruct = new ArrayList<String>();
      List<String> argLines = new ArrayList<String>();
      List<String> assigns = new ArrayList<String>();

      entryPoint = _entryPoint;

      // Add code to collect lambda formal arguments
      // The local variables are the java args to the method
      {
         MethodModel mm = entryPoint.getMethodModel();
         int argsCount = 1;
         Iterator<LocalVariableInfo> lvit = mm.getLocalVariableTableEntry().iterator();
         while(lvit.hasNext()){
            LocalVariableInfo lvi = lvit.next();
            StringBuilder thisStructLine = new StringBuilder();
            StringBuilder argLine = new StringBuilder();
            StringBuilder assignLine = new StringBuilder();
            if((lvi.getStart() == 0) && ((lvi.getVariableIndex() != 0) || mm.getMethod().isStatic())){ // full scope but skip this
               // String descriptor = ;

               // For object stream lambdas, the lvi is the object type, but in
               // the kernel we will need something like:
               //
               // __global com_amd_aparapi_examples_oopnbody_Body *elements,
               // int   elements_array_index;
               //
               // where elements_array_index is the get_global_id index into the elements array


               TypeHelper.Type type = new TypeHelper.Type(lvi.getVariableDescriptor());
               // String classModelType = type.getType();
               String output;
               boolean isObjectLambda = false;
               if(type.isArray()){
                  // This is a local array captured from the caller method and
                  // passed in from the Block/Consumer
                  if(type.isObject()){
                     //classModelType = __global + " " + (classModelType.substring(2, classModelType.length() - 1)).replace("/", "_");
                     output = __global + " " + type.getMangledClassName();
                  }else{
                     // Basic type array
                     output = __global + " " + type.getJavaName();
                  }
               }else if(type.isPrimitive()){
                  output = type.getJavaName();
               }else{
                  // This must be the iteration object
                  // Turn Lcom/amd/javalabs/opencl/demo/DummyOOA; into com_amd_javalabs_opencl_demo_DummyOOA for example
                  output = __global + " " + type.getMangledClassName();
                  isObjectLambda = true;

                  // Insert the source object array and integer index here
                  // in case of object stream lambda
                  //if (isObjectLambda == true && argsCount == 1) {

                  final String sourceArrayName = "elements";
                  String elementsDeclaration = output + " *" + sourceArrayName;

                  // Add array to args
                  argLine.append(elementsDeclaration);
                  argLines.add(argLine.toString());

                  // Add array to this struct
                  thisStructLine.append(elementsDeclaration);
                  thisStruct.add(thisStructLine.toString());

                  // Add index to this struct and args
                  final String objSourceIndex = "elements_array_index";
                  final String objSourceIndexDecl = "int " + objSourceIndex;
                  thisStruct.add(objSourceIndexDecl);
                  argLines.add(objSourceIndexDecl);

                  // Add array to assigns
                  assignLine.append("this->");
                  assignLine.append(sourceArrayName);
                  assignLine.append(" = ");
                  assignLine.append(sourceArrayName);
                  assigns.add(assignLine.toString());

                  // Add get_global_id to assigns
                  StringBuilder assignGid = new StringBuilder();
                  assignGid.append(objSourceIndex);
                  assignGid.append(" = get_global_id(0)");
                  assigns.add(assignGid.toString());

               }

               if(!isObjectLambda){
                  if(lvi.isArray()){
                     // It will be a pointer ref to an array that was a captured arg
                     argLine.append(output);
                     thisStructLine.append(output);
                  }else{
                     argLine.append(convertType(output, false));
                     thisStructLine.append(convertType(output, false));
                  }
                  argLine.append(" ");
                  thisStructLine.append(" ");

                  // Note in the case of int lambdas, the last lambda java method
                  // arg is an int which acts as the opencl gid
                  // Its value is not used and it is assigned with get_global_id(0)
                  if(argsCount == (entryPoint.getLambdaActualParamsCount() + 1) &&
                        (lvi != null) && lvi.getVariableDescriptor().equals("I")){
                     StringBuilder assignGid = new StringBuilder();
                     assignGid.append(lvi.getVariableName());
                     assignGid.append(" = get_global_id(0)");
                     assigns.add(assignGid.toString());
                  }

                  assignLine.append("this->");
                  assignLine.append(lvi.getVariableName());
                  assignLine.append(" = ");
                  assignLine.append(lvi.getVariableName());

                  if(lvi.isArray()){
                     argLine.append("*" + lvi.getVariableName());
                     thisStructLine.append("*" + lvi.getVariableName());
                  }else{
                     argLine.append(lvi.getVariableName());
                     thisStructLine.append(lvi.getVariableName());
                  }

                  assigns.add(assignLine.toString());
                  argLines.add(argLine.toString());
                  thisStruct.add(thisStructLine.toString());
               }

               argsCount++;
            }
         }
      }

      for(ClassModelField field : _entryPoint.getReferencedClassModelFields()){
         // Field field = _entryPoint.getClassModel().getField(f.getName());
         StringBuilder thisStructLine = new StringBuilder();
         StringBuilder argLine = new StringBuilder();
         StringBuilder assignLine = new StringBuilder();

         Type fieldType = field.getType();


         // check the suffix
         String type = field.getName().endsWith(Kernel.LOCAL_SUFFIX) ? __local
               : (field.getName().endsWith(Kernel.CONSTANT_SUFFIX) ? __constant : __global);
         RuntimeAnnotationsEntry visibleAnnotations = field.fieldAttributePool.getRuntimeVisibleAnnotationsEntry();

         if(visibleAnnotations != null){
            for(AnnotationInfo ai : visibleAnnotations){
               String typeDescriptor = ai.getTypeDescriptor();
               if(typeDescriptor.equals(LOCAL_ANNOTATION_NAME)){
                  type = __local;
               }else if(typeDescriptor.equals(CONSTANT_ANNOTATION_NAME)){
                  type = __constant;
               }
            }
         }


         if(fieldType.isArray()){
            argLine.append(type + " ");
            thisStructLine.append(type + " ");
         }

         // If it is a converted array of objects, emit the struct param
         String className = null;
         if(fieldType.isObject()){
            // Turn Lcom/amd/javalabs/opencl/demo/DummyOOA; into com_amd_javalabs_opencl_demo_DummyOOA for example
            className = fieldType.getMangledClassName();
            argLine.append(className);
            thisStructLine.append(className);
         }else{
            //argLine.append(TypeHelper.openCLName(fieldType));
            argLine.append(convertType(TypeHelper.typeName(fieldType.getType().charAt(0)), false));
            thisStructLine.append(convertType(TypeHelper.typeName(fieldType.getType().charAt(0)), false));
         }

         argLine.append(" ");
         thisStructLine.append(" ");

         if(fieldType.isArray()){
            argLine.append("*");
            thisStructLine.append("*");
         }
         assignLine.append("this->");
         assignLine.append(field.getName());
         assignLine.append(" = ");
         assignLine.append(field.getName());
         argLine.append(field.getName());
         thisStructLine.append(field.getName());
         assigns.add(assignLine.toString());
         argLines.add(argLine.toString());
         thisStruct.add(thisStructLine.toString());

         // Add int field into "this" struct for supporting java arraylength op
         // named like foo__javaArrayLength
         if(fieldType.isArray() && _entryPoint.getArrayFieldArrayLengthUsed().contains(field.getName())){
            StringBuilder lenStructLine = new StringBuilder();
            StringBuilder lenArgLine = new StringBuilder();
            StringBuilder lenAssignLine = new StringBuilder();

            lenStructLine.append("int " + field.getName() + BlockWriter.arrayLengthMangleSuffix);

            lenAssignLine.append("this->");
            lenAssignLine.append(field.getName() + BlockWriter.arrayLengthMangleSuffix);
            lenAssignLine.append(" = ");
            lenAssignLine.append(field.getName() + BlockWriter.arrayLengthMangleSuffix);

            lenArgLine.append("int " + field.getName() + BlockWriter.arrayLengthMangleSuffix);

            assigns.add(lenAssignLine.toString());
            argLines.add(lenArgLine.toString());
            thisStruct.add(lenStructLine.toString());
         }
      }

      boolean usesAtomics = false;
      if(Config.enableAtomic32 || _entryPoint.requiresAtomic32Pragma()){
         usesAtomics = true;
         writePragma("cl_khr_global_int32_base_atomics", true);
         writePragma("cl_khr_global_int32_extended_atomics", true);
         writePragma("cl_khr_local_int32_base_atomics", true);
         writePragma("cl_khr_local_int32_extended_atomics", true);
      }
      if(Config.enableAtomic64 || _entryPoint.requiresAtomic64Pragma()){
         usesAtomics = true;
         writePragma("cl_khr_int64_base_atomics", true);
         writePragma("cl_khr_int64_extended_atomics", true);
      }
      if(usesAtomics){
         writeInLn("int atomicAdd(__global int *_arr, int _index, int _delta){");
         {
            writeOutLn("return atomic_add(&_arr[_index], _delta);");
         }
         writeLn("}");
      }

      if(Config.enableDoubles || _entryPoint.requiresDoublePragma()){
         writePragma("cl_khr_fp64", true);
         newLine();
      }

      // Emit structs for oop transformation accessors
      for(ClassModel cm : _entryPoint.getObjectArrayFieldsClasses().values()){
         ArrayList<FieldEntry> fieldSet = cm.getStructMembers();
         if(fieldSet.size() > 0){
            String mangledClassName = cm.getMangledClassName();

            lnWriteInLn("typedef struct " + mangledClassName + "_s{");
            int totalSize = 0;
            int alignTo = 0;

            Iterator<FieldEntry> it = fieldSet.iterator();
            while(it.hasNext()){
               FieldEntry field = it.next();
               String fType = field.getNameAndTypeEntry().getDescriptorUTF8Entry().getUTF8();
               int fSize = InstructionSet.TypeSpec.valueOf(fType.equals("Z") ? "B" : fType).getSize();

               if(fSize > alignTo){
                  alignTo = fSize;
               }
               totalSize += fSize;

               String cType = convertType(field.getNameAndTypeEntry().getDescriptorUTF8Entry().getUTF8(), true);
               assert cType != null : "could not find type for " + field.getNameAndTypeEntry().getDescriptorUTF8Entry().getUTF8();
               writeLn(cType + " " + field.getNameAndTypeEntry().getNameUTF8Entry().getUTF8() + ";");
            }

            // compute total size for OpenCL buffer
            int totalStructSize = 0;
            if(totalSize % alignTo == 0){
               totalStructSize = totalSize;
            }else{
               // Pad up if necessary
               totalStructSize = ((totalSize / alignTo) + 1) * alignTo;
            }
            if(totalStructSize > alignTo){
               while(totalSize < totalStructSize){
                  // structBuffer.put((byte)-1);
                  writeLn("char _pad_" + totalSize + ";");
                  totalSize++;
               }
            }
            out();
            writeLn("} " + mangledClassName + ";");
            newLine();
         }
      }

      write("typedef struct This_s{");
      in();
      newLine();
      for(String line : thisStruct){
         writeLn(line + ";");
      }
      writeOutLn("int passid;");
      writeLn("}This;");

      lnWriteInLn("int get_pass_id(This *this){");
      {
         writeOutLn("return this->passid;");
      }
      writeLn("}");
      newLine();


      for(MethodModel mm : _entryPoint.getCalledMethods()){
         // write declaration :)

         String returnType = mm.getReturnType();
         // Arrays always map to __global arrays
         if(returnType.startsWith("[")){
            write(" __global ");
         }
         write(convertType(returnType, true));

         write(mm.getMangledName() + "(");

         if(!mm.getMethod().isStatic()){
            if((mm.getMethod().getClassModel() == _entryPoint.getClassModel())
                  || mm.getMethod().getClassModel().isSuperClass(_entryPoint.getClassModel().getClassWeAreModelling())){
               write("This *this");
            }else{
               // Call to an object member or superclass of member
               for(ClassModel c : _entryPoint.getObjectArrayFieldsClasses().values()){
                  if(mm.getMethod().getClassModel() == c){
                     write("__global " + mm.getMethod().getClassModel().getMangledClassName()
                           + " *this");
                     break;
                  }else if(mm.getMethod().getClassModel().isSuperClass(c.getClassWeAreModelling())){
                     write("__global " + c.getMangledClassName() + " *this");
                     break;
                  }
               }
            }
         }

         boolean alreadyHasFirstArg = !mm.getMethod().isStatic();

         LocalVariableTableEntry<LocalVariableInfo> lvte = mm.getLocalVariableTableEntry();
         for(LocalVariableInfo lvi : lvte){
            if((lvi.getStart() == 0) && ((lvi.getVariableIndex() != 0) || mm.getMethod().isStatic())){ // full scope but skip this
               String descriptor = lvi.getVariableDescriptor();
               if(alreadyHasFirstArg){
                  write(", ");
               }

               // Arrays always map to __global arrays
               if(descriptor.startsWith("[")){
                  write(" __global ");
               }

               write(convertType(descriptor, true));
               write(lvi.getVariableName());
               alreadyHasFirstArg = true;
            }
         }
         write(")");
         writeMethodBody(mm);
         newLine();
      }
      if(_entryPoint.isKernel()){
         writeIn("__kernel void " + _entryPoint.getMethodModel().getSimpleName() + "(");
      }else{
         writeIn("__kernel void run(");
      }
      boolean first = true;
      for(String line : argLines){

         if(first){
            first = false;
         }else{
            write(", ");
         }

         newLine();
         write(line);
      }

      if(first){
         first = false;
      }else{
         write(", ");
      }

      lnWriteOutLn("int passid");
      writeInLn("){");
      writeLn("This thisStruct;");
      writeLn("This* this=&thisStruct;");
      for(String line : assigns){
         writeLn(line + ";");
      }
      writeLn("this->passid = passid;");

      writeMethodBody(_entryPoint.getMethodModel());
      //  out();
      outWrite("}");

      // out();

   }

   @Override
   protected void writeThisRef(){
      write("this->");
   }

   // Emit the this-> syntax when accessing locals that are lambda arguments
   @Override
   protected void doAccessLocalVariable(Instruction _instruction){
      AccessLocalVariable localVariableLoadInstruction = (AccessLocalVariable) _instruction;
      LocalVariableInfo localVariable = localVariableLoadInstruction.getLocalVariableInfo();
      if((localVariable.getStart() == 0) && (_instruction.getMethod() == entryPoint.getMethodModel())){
         // This is a method parameter captured value into the lambda
         writeThisRef();
      }
      write(localVariable.getVariableName());
   }

   @Override void writeInstruction(Instruction _instruction) throws CodeGenException{
      if((_instruction instanceof I_IUSHR) || (_instruction instanceof I_LUSHR)){
         BinaryOperator binaryInstruction = (BinaryOperator) _instruction;
         Instruction parent = binaryInstruction.getParentExpr();
         boolean needsParenthesis = true;

         if(parent instanceof AssignToLocalVariable){
            needsParenthesis = false;
         }else if(parent instanceof AssignToField){
            needsParenthesis = false;
         }else if(parent instanceof AssignToArrayElement){
            needsParenthesis = false;
         }
         if(needsParenthesis){
            write("(");
         }

         if(binaryInstruction instanceof I_IUSHR){
            write("((unsigned int)");
         }else{
            write("((unsigned long)");
         }
         writeInstruction(binaryInstruction.getLhs());
         write(")");
         write(" >> ");
         writeInstruction(binaryInstruction.getRhs());

         if(needsParenthesis){
            write(")");
         }
      }else{
         super.writeInstruction(_instruction);
      }
   }

   static String writeToString(Entrypoint _entrypoint) throws CodeGenException{
      final StringBuilder openCLStringBuilder = new StringBuilder();
      KernelWriter openCLWriter = new KernelWriter(){
         @Override void write(String _string){
            openCLStringBuilder.append(_string);
         }
      };
      try{
         openCLWriter.write(_entrypoint);
      }catch(CodeGenException codeGenException){
         throw codeGenException;
      }catch(Throwable t){
         throw new CodeGenException(t);
      }
      return (openCLStringBuilder.toString());
   }
}
