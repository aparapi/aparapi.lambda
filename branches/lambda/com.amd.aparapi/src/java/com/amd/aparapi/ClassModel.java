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

import com.amd.aparapi.ClassModel.AttributePool.CodeEntry;
import com.amd.aparapi.ClassModel.ConstantPool.FieldEntry;
import com.amd.aparapi.ClassModel.ConstantPool.MethodEntry;
import com.amd.aparapi.InstructionSet.Branch;
import com.amd.aparapi.InstructionSet.TypeSpec;
import com.amd.aparapi.TypeHelper.ArgsAndReturnType;
import com.amd.aparapi.TypeHelper.Type;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class represents a ClassFile (MyClass.class).
 * <p/>
 * A ClassModel is constructed from an instance of a <code>java.lang.Class</code>.
 * <p/>
 * If the java class mode changes we may need to modify this to accommodate.
 *
 * @author gfrost
 * @see <a href="http://java.sun.com/docs/books/jvms/second_edition/ClassFileFormat-Java5.pdf">Java 5 Class File Format</a>
 * @see <a href="http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html"> Java 7 Class File Format</a>
 */
public class ClassModel{



    interface LocalVariableInfo{

      int getStart();

      boolean isArray();

      boolean isObject();

      int getEnd();

      String getVariableName();

      String getVariableDescriptor();

      int getSlot();

      int getLength();

   }

   interface LocalVariableTableEntry<T extends LocalVariableTableEntry, I extends LocalVariableInfo> extends Iterable<LocalVariableInfo>{
      I getVariable(int _pc, int _index);

   }


   private static Logger logger = Logger.getLogger(Config.getLoggerName());

   private ClassModel superClazzModel = null;

   private Class<?> clazz;

   static Map<String, ClassModel> map = new LinkedHashMap<String, ClassModel>();

   public static synchronized ClassModel getClassModel(Class<?> _clazz) throws ClassParseException{
      ClassModel classModel = map.get(_clazz.getName());
      if(classModel == null){
         classModel = new ClassModel(_clazz);
         map.put(_clazz.getName(), classModel);
      }
      return (classModel);
   }


   private ClassModel(Class<?> _clazz) throws ClassParseException{
      byte[] _bytes = OpenCLJNI.getJNI().getBytes(_clazz.getName());
      clazz = _clazz;
      parse(new ByteArrayInputStream(_bytes));
   }

   /**
    * Determine if this is the superclass of some other named class.
    *
    * @param otherClassName The name of the class to compare against
    * @return true if 'this' a superclass of another named class
    */
   boolean isSuperClass(String otherClassName){
      if(getDotClassName().equals(otherClassName)){
         return true;
      }else if(superClazzModel != null){
         return superClazzModel.isSuperClass(otherClassName);
      }else{
         return false;
      }
   }

   /**
    * Determine if this is the superclass of some other class.
    *
    * @param _otherClassModel The classModel to compare against
    * @return true if 'this' a superclass of another class
    */
   boolean isSuperClass(ClassModel _otherClassModel){
      ClassModel s = _otherClassModel.getSuperClazzModel();
      while(s != null){
         if(getDotClassName().equals(s.getDotClassName())){
            return true;
         }
         s = s.getSuperClazzModel();
      }
      return false;
   }

   /**
    * Getter for superClazz
    *
    * @return the superClazz ClassModel
    */
   ClassModel getSuperClazzModel(){
      if(superClazzModel == null){
         try{
            superClazzModel = getClassModel(Class.forName(getSuperDotClassName()));
         }catch(ClassNotFoundException cnf){

         }catch(ClassParseException cpe){

         }
      }
      return superClazzModel;
   }

   /**
    * Dont think we need this.
    *
    * @param c
    */
   @Annotations.DocMe void replaceSuperClazz(ClassModel c){
      if(this.superClazzModel != null){
         //  assert c.isSuperClass(this.getClassWeAreModelling()) == true : "not my super";
         if(this.superClazzModel.getDotClassName().equals(c.getDotClassName())){
            this.superClazzModel = c;
         }else{
            this.superClazzModel.replaceSuperClazz(c);
         }
      }
   }


   private int magic;

   private int minorVersion;

   private int majorVersion;

   private ConstantPool constantPool;

   private int accessFlags;

   private int thisClassConstantPoolIndex;

   private int superClassConstantPoolIndex;

   private List<ClassModelInterface> interfaces = new ArrayList<ClassModelInterface>();

   private List<ClassModelField> fields = new ArrayList<ClassModelField>();

   private List<ClassModelMethod> methods = new ArrayList<ClassModelMethod>();

   private AttributePool attributePool;

   enum ConstantPoolType{
      EMPTY(0, 1, "empty"), //0
      UTF8(1, 1, "utf8"), //1
      UNICODE(2, 1, "unicode"), //2
      INTEGER(3, 1, "int"), //3
      FLOAT(4, 1, "float"), //4
      LONG(5, 2, "long"), //5
      DOUBLE(6, 2, "double"), //6
      CLASS(7, 1, "class"), //7
      STRING(8, 1, "string"), //8
      FIELD(9, 1, "field"), //9
      METHOD(10, 1, "method"), //10
      INTERFACEMETHOD(11, 1, "interface_method"), //11
      NAMEANDTYPE(12, 1, "name and type"), //12
      UNUSED13(13, 1, "unused13"),
      UNUSED14(14, 1, "unused14"),
      METHODHANDLE(15, 1, "method_handle"), //15
      METHODTYPE(16, 1, "method_type"), //16
      UNUSED17(17, 1, "unused17"), //17
      INVOKEDYNAMIC(18, 1, "invoke_dynamic");//18

      int index;
      int slots;
      String name;
      ConstantPoolType[] types;

      ConstantPoolType(int _index, int _slots, String _name, ConstantPoolType... _types){
         index = _index;
         slots = _slots;
         name = _name;
         types = _types;
      }
   }

   ;

   enum Access{
      PUBLIC(0x00000001, "public"),
      PRIVATE(0x00000002, "private"),
      PROTECTED(0x00000004, "protected"),
      STATIC(0x00000008, "static"),
      FINAL(0x00000010, "final"),
      ACC_SYNCHRONIZED(0x00000020, "synchronized"),
      ACC_VOLATILE(0x00000040, "volatile"),
      BRIDGE(0x00000040, "bridge"),
      TRANSIENT(0x00000080, "transient"),
      VARARGS(0x00000080, "varargs"),
      NATIVE(0x00000100, "native"),
      INTERFACE(0x00000200, "interface"),
      ABSTRACT(0x00000400, "abstract"),
      SUPER(0x00000020, "super"),
      STRICT(0x00000800, "strict"),
      ANNOTATION(0x00002000, "annotation"),
      ACC_ENUM(0x00004000, "enum");
      int bits;
      String name;

      Access(int _bits, String _name){
         bits = _bits;
         name = _name;
      }

      boolean bitIsSet(int _accessFlags){
         return ((bits & _accessFlags) == bits);
      }

      String convert(int _accessFlags){
         StringBuffer stringBuffer = new StringBuffer();
         for(Access access : Access.values()){
            if(access.bitIsSet(_accessFlags)){
               stringBuffer.append(" " + access.name);
            }
         }
         return (stringBuffer.toString());
      }
   }


   class ConstantPool implements Iterable<ConstantPool.Entry>{

      private List<Entry> entries = new ArrayList<Entry>();

      abstract class Entry{
         private ConstantPoolType constantPoolType;

         private int slot;

         Entry(ByteReader _byteReader, int _slot, ConstantPoolType _constantPoolType){
            constantPoolType = _constantPoolType;
            slot = _slot;
         }

         ConstantPoolType getConstantPoolType(){
            return (constantPoolType);
         }

         int getSlot(){
            return (slot);
         }

      }

      class ClassEntry extends Entry{

         private int nameIndex;

         ClassEntry(ByteReader _byteReader, int _slot){
            super(_byteReader, _slot, ConstantPoolType.CLASS);
            nameIndex = _byteReader.u2();
         }

         int getNameIndex(){
            return (nameIndex);
         }

         UTF8Entry getNameUTF8Entry(){
            return (ConstantPool.this.getUTF8Entry(nameIndex));
         }

         String getClassName(){
            return (getNameUTF8Entry().getUTF8());
         }

         String getDotClassName(){
            return (TypeHelper.slashClassNameToDotClassName(getNameUTF8Entry().getUTF8()));
         }

         String getMangledClassName(){
            return (TypeHelper.slashClassNameToMangledClassName(getNameUTF8Entry().getUTF8()));
         }
      }

      class DoubleEntry extends ConstantEntry<Double>{


         DoubleEntry(ByteReader _byteReader, int _slot){
            super(_byteReader, _slot, ConstantPoolType.DOUBLE);
            value = _byteReader.d8();
         }


      }

      class EmptyEntry extends Entry{
         EmptyEntry(ByteReader _byteReader, int _slot){
            super(_byteReader, _slot, ConstantPoolType.EMPTY);
         }

      }

      class FieldEntry extends ReferenceEntry{

         FieldEntry(ByteReader _byteReader, int _slot){
            super(_byteReader, _slot, ConstantPoolType.FIELD);
         }

         private Type type;

         Type getType(){
            if(type == null){
               NameAndTypeEntry nameAndTypeEntry = getNameAndTypeEntry();


               String signature = nameAndTypeEntry.getDescriptorUTF8Entry().getUTF8();
               type = new Type(signature);

            }
            return (type);

         }
      }

      abstract class ConstantEntry<T> extends Entry{
         protected T value;

         ConstantEntry(ByteReader _byteReader, int _slot, ConstantPoolType _type){
            super(_byteReader, _slot, _type);
         }

         T getValue(){
            return value;
         }

      }

      class FloatEntry extends ConstantEntry<Float>{


         FloatEntry(ByteReader _byteReader, int _slot){
            super(_byteReader, _slot, ConstantPoolType.FLOAT);
            value = _byteReader.f4();
         }


      }

      class IntegerEntry extends ConstantEntry<Integer>{

         IntegerEntry(ByteReader _byteReader, int _slot){
            super(_byteReader, _slot, ConstantPoolType.INTEGER);
            value = _byteReader.u4();
         }


      }

      class InterfaceMethodEntry extends MethodReferenceEntry{
         InterfaceMethodEntry(ByteReader _byteReader, int _slot){
            super(_byteReader, _slot, ConstantPoolType.INTERFACEMETHOD);
         }
      }

      class LongEntry extends ConstantEntry<Long>{


         LongEntry(ByteReader _byteReader, int _slot){
            super(_byteReader, _slot, ConstantPoolType.LONG);
            value = _byteReader.u8();
         }


      }

      class MethodEntry extends MethodReferenceEntry{

         MethodEntry(ByteReader _byteReader, int _slot){
            super(_byteReader, _slot, ConstantPoolType.METHOD);
         }

         @Override
         public String toString(){
            StringBuilder sb = new StringBuilder();
            sb.append(getClassEntry().getNameUTF8Entry().getUTF8());
            sb.append(".");
            sb.append(getNameAndTypeEntry().getNameUTF8Entry().getUTF8());
            sb.append(getNameAndTypeEntry().getDescriptorUTF8Entry().getUTF8());
            return (sb.toString());
         }

      }

      class NameAndTypeEntry extends Entry{
         private int descriptorIndex;

         private int nameIndex;

         NameAndTypeEntry(ByteReader _byteReader, int _slot){
            super(_byteReader, _slot, ConstantPoolType.NAMEANDTYPE);
            nameIndex = _byteReader.u2();
            descriptorIndex = _byteReader.u2();
         }

         int getDescriptorIndex(){
            return (descriptorIndex);
         }

         UTF8Entry getDescriptorUTF8Entry(){
            return (ConstantPool.this.getUTF8Entry(descriptorIndex));
         }

         int getNameIndex(){
            return (nameIndex);
         }

         UTF8Entry getNameUTF8Entry(){
            return (ConstantPool.this.getUTF8Entry(nameIndex));
         }

      }

      class MethodTypeEntry extends Entry{
         private int descriptorIndex;

         MethodTypeEntry(ByteReader _byteReader, int _slot){
            super(_byteReader, _slot, ConstantPoolType.METHODTYPE);
            descriptorIndex = _byteReader.u2();
         }

         int getDescriptorIndex(){
            return (descriptorIndex);
         }

         UTF8Entry getDescriptorUTF8Entry(){
            return (ConstantPool.this.getUTF8Entry(descriptorIndex));
         }

      }

      class MethodHandleEntry extends Entry{
         // http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.4

         private int referenceKind;

         private int referenceIndex;

         MethodHandleEntry(ByteReader _byteReader, int _slot){
            super(_byteReader, _slot, ConstantPoolType.METHODHANDLE);
            referenceKind = _byteReader.u1();
            referenceIndex = _byteReader.u2();
         }

         int getReferenceIndex(){
            return (referenceIndex);
         }

         int getReferenceKind(){
            return (referenceKind);
         }

      }

      class InvokeDynamicEntry extends Entry{
         // http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.4

         private int bootstrapMethodAttrIndex;

         private int nameAndTypeIndex;

         InvokeDynamicEntry(ByteReader _byteReader, int _slot){
            super(_byteReader, _slot, ConstantPoolType.INVOKEDYNAMIC);
            bootstrapMethodAttrIndex = _byteReader.u2();
            nameAndTypeIndex = _byteReader.u2();
         }

         int getBootstrapMethodAttrIndex(){
            return (bootstrapMethodAttrIndex);
         }

         int getNameAndTypeIndex(){
            return (nameAndTypeIndex);
         }

      }

      abstract class MethodReferenceEntry extends ReferenceEntry{


         @Override
         public int hashCode(){
            NameAndTypeEntry nameAndTypeEntry = getNameAndTypeEntry();

            return ((nameAndTypeEntry.getNameIndex() * 31 + nameAndTypeEntry.getDescriptorIndex()) * 31 + getClassIndex());
         }

         @Override
         public boolean equals(Object _other){
            if(_other == null || !(_other instanceof MethodReferenceEntry)){
               return (false);
            }else{
               MethodReferenceEntry otherMethodReferenceEntry = (MethodReferenceEntry) _other;
               return (otherMethodReferenceEntry.getNameAndTypeEntry().getNameIndex() == getNameAndTypeEntry().getNameIndex()
                     && otherMethodReferenceEntry.getNameAndTypeEntry().getDescriptorIndex() == getNameAndTypeEntry()
                     .getDescriptorIndex() && otherMethodReferenceEntry.getClassIndex() == getClassIndex());
            }
         }

         MethodReferenceEntry(ByteReader byteReader, int slot, ConstantPoolType constantPoolType){
            super(byteReader, slot, constantPoolType);


         }

         int getStackProduceCount(){
            return (getArgsAndReturnType().getReturnType().isVoid() ? 0 : 1);
         }

         ArgsAndReturnType argsAndReturnType;

         ArgsAndReturnType getArgsAndReturnType(){
            if(argsAndReturnType == null){
               NameAndTypeEntry nameAndTypeEntry = getNameAndTypeEntry();

               String signature = nameAndTypeEntry.getDescriptorUTF8Entry().getUTF8();// "([[IF)V" for a method that takes an int[][], float and returns void.
               argsAndReturnType = new ArgsAndReturnType(signature);
            }
            return (argsAndReturnType);
         }


         int getStackConsumeCount(){
            return (getArgsAndReturnType().getArgs().length);
         }
      }

      abstract class ReferenceEntry extends Entry{
         protected int referenceClassIndex;

         protected int nameAndTypeIndex;

         protected int argCount = -1;

         ReferenceEntry(ByteReader _byteReader, int _slot, ConstantPoolType _constantPoolType){
            super(_byteReader, _slot, _constantPoolType);
            referenceClassIndex = _byteReader.u2();
            nameAndTypeIndex = _byteReader.u2();
         }

         ClassEntry getClassEntry(){
            return (ConstantPool.this.getClassEntry(referenceClassIndex));
         }

         Type getContainingClass(){
            return (new TypeHelper.Type(getClassEntry().getClassName()));
         }

         String getName(){
            return (getNameAndTypeEntry().getNameUTF8Entry().getUTF8());
         }

         int getClassIndex(){
            return (referenceClassIndex);
         }

         NameAndTypeEntry getNameAndTypeEntry(){
            return (ConstantPool.this.getNameAndTypeEntry(nameAndTypeIndex));
         }

         int getNameAndTypeIndex(){
            return (nameAndTypeIndex);
         }

         boolean same(Entry _entry){
            if(_entry instanceof ReferenceEntry){
               ReferenceEntry entry = (ReferenceEntry) _entry;
               return ((referenceClassIndex == entry.referenceClassIndex) && (nameAndTypeIndex == entry.nameAndTypeIndex));
            }
            return (false);
         }


      }

      class StringEntry extends ConstantEntry<String>{
         private int utf8Index;

         StringEntry(ByteReader _byteReader, int _slot){
            super(_byteReader, _slot, ConstantPoolType.STRING);
            utf8Index = _byteReader.u2();
         }

         int getUTF8Index(){
            return (utf8Index);
         }

         UTF8Entry getStringUTF8Entry(){
            return (ConstantPool.this.getUTF8Entry(utf8Index));
         }

         String getValue(){
            if(value == null){
               value = getStringUTF8Entry().getUTF8();
            }
            return (super.getValue());
         }


      }

      class UTF8Entry extends Entry{
         private String UTF8;

         UTF8Entry(ByteReader _byteReader, int _slot){
            super(_byteReader, _slot, ConstantPoolType.UTF8);
            UTF8 = _byteReader.utf8();
         }

         String getUTF8(){
            return (UTF8);
         }

      }

      ConstantPool(ByteReader _byteReader){
         int size = _byteReader.u2();
         add(new EmptyEntry(_byteReader, 0)); // slot 0

         for(int i = 1; i < size; i++){
            ConstantPoolType constantPoolType = ConstantPoolType.values()[_byteReader.u1()];

            switch(constantPoolType){
               case UTF8:
                  add(new UTF8Entry(_byteReader, i));
                  break;
               case INTEGER:
                  add(new IntegerEntry(_byteReader, i));
                  break;
               case FLOAT:
                  add(new FloatEntry(_byteReader, i));
                  break;
               case LONG:
                  add(new LongEntry(_byteReader, i));
                  i++;// Longs take two slots in the ConstantPool
                  add(new EmptyEntry(_byteReader, i));
                  break;
               case DOUBLE:
                  add(new DoubleEntry(_byteReader, i));
                  i++; // Doubles take two slots in the ConstantPool
                  add(new EmptyEntry(_byteReader, i));
                  break;
               case CLASS:
                  add(new ClassEntry(_byteReader, i));
                  break;
               case STRING:
                  add(new StringEntry(_byteReader, i));
                  break;
               case FIELD:
                  add(new FieldEntry(_byteReader, i));
                  break;
               case METHOD:
                  add(new MethodEntry(_byteReader, i));
                  break;
               case INTERFACEMETHOD:
                  add(new InterfaceMethodEntry(_byteReader, i));
                  break;
               case NAMEANDTYPE:
                  add(new NameAndTypeEntry(_byteReader, i));
                  break;
               case METHODHANDLE:
                  add(new MethodHandleEntry(_byteReader, i));
                  break;
               case METHODTYPE:
                  add(new MethodTypeEntry(_byteReader, i));
                  break;
               case INVOKEDYNAMIC:
                  add(new InvokeDynamicEntry(_byteReader, i));
                  break;
               default:
                  System.out.printf("slot %04x unexpected Constant constantPoolType = %s\n", i, constantPoolType);

            }

         }
      }

      ClassEntry getClassEntry(int _index){
         try{
            return ((ClassEntry) entries.get(_index));
         }catch(ClassCastException e){
            return (null);
         }
      }

      DoubleEntry getDoubleEntry(int _index){
         try{
            return ((DoubleEntry) entries.get(_index));
         }catch(ClassCastException e){
            return (null);
         }
      }

      FieldEntry getFieldEntry(int _index){
         try{
            return ((FieldEntry) entries.get(_index));
         }catch(ClassCastException e){
            return (null);
         }
      }

      FloatEntry getFloatEntry(int _index){
         try{
            return ((FloatEntry) entries.get(_index));
         }catch(ClassCastException e){
            return (null);
         }
      }

      IntegerEntry getIntegerEntry(int _index){
         try{
            return ((IntegerEntry) entries.get(_index));
         }catch(ClassCastException e){
            return (null);
         }
      }

      InterfaceMethodEntry getInterfaceMethodEntry(int _index){
         try{
            return ((InterfaceMethodEntry) entries.get(_index));
         }catch(ClassCastException e){
            return (null);
         }
      }

      LongEntry getLongEntry(int _index){
         try{
            return ((LongEntry) entries.get(_index));
         }catch(ClassCastException e){
            return (null);
         }
      }

      MethodEntry getMethodEntry(int _index){
         try{
            return ((MethodEntry) entries.get(_index));
         }catch(ClassCastException e){
            return (null);
         }
      }

      NameAndTypeEntry getNameAndTypeEntry(int _index){
         try{
            return ((NameAndTypeEntry) entries.get(_index));
         }catch(ClassCastException e){
            return (null);
         }
      }

      StringEntry getStringEntry(int _index){
         try{
            return ((StringEntry) entries.get(_index));
         }catch(ClassCastException e){
            return (null);
         }
      }

      UTF8Entry getUTF8Entry(int _index){
         try{
            return ((UTF8Entry) entries.get(_index));
         }catch(ClassCastException e){
            return (null);
         }
      }

      void add(Entry _entry){
         entries.add(_entry);

      }

      @Override
      public Iterator<Entry> iterator(){
         return (entries.iterator());
      }

      Entry get(int _index){
         return (entries.get(_index));
      }

      String getDescription(ConstantPool.Entry _entry){
         StringBuilder sb = new StringBuilder();
         if(_entry instanceof ConstantPool.EmptyEntry){
            ;
         }else if(_entry instanceof ConstantPool.DoubleEntry){
            ConstantPool.DoubleEntry doubleEntry = (ConstantPool.DoubleEntry) _entry;
            sb.append(doubleEntry.getValue());
         }else if(_entry instanceof ConstantPool.FloatEntry){
            ConstantPool.FloatEntry floatEntry = (ConstantPool.FloatEntry) _entry;
            sb.append(floatEntry.getValue());
         }else if(_entry instanceof ConstantPool.IntegerEntry){
            ConstantPool.IntegerEntry integerEntry = (ConstantPool.IntegerEntry) _entry;
            sb.append(integerEntry.getValue());
         }else if(_entry instanceof ConstantPool.LongEntry){
            ConstantPool.LongEntry longEntry = (ConstantPool.LongEntry) _entry;
            sb.append(longEntry.getValue());
         }else if(_entry instanceof ConstantPool.UTF8Entry){
            ConstantPool.UTF8Entry utf8Entry = (ConstantPool.UTF8Entry) _entry;
            sb.append(utf8Entry.getUTF8());
         }else if(_entry instanceof ConstantPool.StringEntry){
            ConstantPool.StringEntry stringEntry = (ConstantPool.StringEntry) _entry;
            ConstantPool.UTF8Entry utf8Entry = (ConstantPool.UTF8Entry) get(stringEntry.getUTF8Index());
            sb.append(utf8Entry.getUTF8());
         }else if(_entry instanceof ConstantPool.ClassEntry){
            ConstantPool.ClassEntry classEntry = (ConstantPool.ClassEntry) _entry;
            ConstantPool.UTF8Entry utf8Entry = (ConstantPool.UTF8Entry) get(classEntry.getNameIndex());
            sb.append(utf8Entry.getUTF8());
         }else if(_entry instanceof ConstantPool.NameAndTypeEntry){
            ConstantPool.NameAndTypeEntry nameAndTypeEntry = (ConstantPool.NameAndTypeEntry) _entry;
            ConstantPool.UTF8Entry utf8NameEntry = (ConstantPool.UTF8Entry) get(nameAndTypeEntry.getNameIndex());
            ConstantPool.UTF8Entry utf8DescriptorEntry = (ConstantPool.UTF8Entry) get(nameAndTypeEntry.getDescriptorIndex());
            sb.append(utf8NameEntry.getUTF8() + "." + utf8DescriptorEntry.getUTF8());
         }else if(_entry instanceof ConstantPool.MethodEntry){
            ConstantPool.MethodEntry methodEntry = (ConstantPool.MethodEntry) _entry;
            ConstantPool.ClassEntry classEntry = (ConstantPool.ClassEntry) get(methodEntry.getClassIndex());
            ConstantPool.UTF8Entry utf8Entry = (ConstantPool.UTF8Entry) get(classEntry.getNameIndex());
            ConstantPool.NameAndTypeEntry nameAndTypeEntry = (ConstantPool.NameAndTypeEntry) get(methodEntry.getNameAndTypeIndex());
            ConstantPool.UTF8Entry utf8NameEntry = (ConstantPool.UTF8Entry) get(nameAndTypeEntry.getNameIndex());
            ConstantPool.UTF8Entry utf8DescriptorEntry = (ConstantPool.UTF8Entry) get(nameAndTypeEntry.getDescriptorIndex());
            sb.append(TypeHelper.convert(utf8DescriptorEntry.getUTF8(), utf8Entry.getUTF8() + "." + utf8NameEntry.getUTF8()));
         }else if(_entry instanceof ConstantPool.InterfaceMethodEntry){
            ConstantPool.InterfaceMethodEntry interfaceMethodEntry = (ConstantPool.InterfaceMethodEntry) _entry;
            ConstantPool.ClassEntry classEntry = (ConstantPool.ClassEntry) get(interfaceMethodEntry.getClassIndex());
            ConstantPool.UTF8Entry utf8Entry = (ConstantPool.UTF8Entry) get(classEntry.getNameIndex());
            ConstantPool.NameAndTypeEntry nameAndTypeEntry = (ConstantPool.NameAndTypeEntry) get(interfaceMethodEntry
                  .getNameAndTypeIndex());
            ConstantPool.UTF8Entry utf8NameEntry = (ConstantPool.UTF8Entry) get(nameAndTypeEntry.getNameIndex());
            ConstantPool.UTF8Entry utf8DescriptorEntry = (ConstantPool.UTF8Entry) get(nameAndTypeEntry.getDescriptorIndex());
            sb.append(TypeHelper.convert(utf8DescriptorEntry.getUTF8(), utf8Entry.getUTF8() + "." + utf8NameEntry.getUTF8()));
         }else if(_entry instanceof ConstantPool.FieldEntry){
            ConstantPool.FieldEntry fieldEntry = (ConstantPool.FieldEntry) _entry;
            ConstantPool.ClassEntry classEntry = (ConstantPool.ClassEntry) get(fieldEntry.getClassIndex());
            ConstantPool.UTF8Entry utf8Entry = (ConstantPool.UTF8Entry) get(classEntry.getNameIndex());
            ConstantPool.NameAndTypeEntry nameAndTypeEntry = (ConstantPool.NameAndTypeEntry) get(fieldEntry.getNameAndTypeIndex());
            ConstantPool.UTF8Entry utf8NameEntry = (ConstantPool.UTF8Entry) get(nameAndTypeEntry.getNameIndex());
            ConstantPool.UTF8Entry utf8DescriptorEntry = (ConstantPool.UTF8Entry) get(nameAndTypeEntry.getDescriptorIndex());
            sb.append(TypeHelper.convert(utf8DescriptorEntry.getUTF8(), utf8Entry.getUTF8() + "." + utf8NameEntry.getUTF8()));
         }
         return (sb.toString());
      }

      int[] getConstantPoolReferences(ConstantPool.Entry _entry){
         int[] references = new int[0];
         if(_entry instanceof ConstantPool.StringEntry){
            ConstantPool.StringEntry stringEntry = (ConstantPool.StringEntry) _entry;
            references = new int[]{
                  stringEntry.getUTF8Index()
            };
         }else if(_entry instanceof ConstantPool.ClassEntry){
            ConstantPool.ClassEntry classEntry = (ConstantPool.ClassEntry) _entry;
            references = new int[]{
                  classEntry.getNameIndex()
            };
         }else if(_entry instanceof ConstantPool.NameAndTypeEntry){
            ConstantPool.NameAndTypeEntry nameAndTypeEntry = (ConstantPool.NameAndTypeEntry) _entry;
            references = new int[]{
                  nameAndTypeEntry.getNameIndex(),
                  nameAndTypeEntry.getDescriptorIndex()
            };
         }else if(_entry instanceof ConstantPool.MethodEntry){
            ConstantPool.MethodEntry methodEntry = (ConstantPool.MethodEntry) _entry;
            ConstantPool.ClassEntry classEntry = (ConstantPool.ClassEntry) get(methodEntry.getClassIndex());
            @SuppressWarnings("unused") ConstantPool.UTF8Entry utf8Entry = (ConstantPool.UTF8Entry) get(classEntry.getNameIndex());
            ConstantPool.NameAndTypeEntry nameAndTypeEntry = (ConstantPool.NameAndTypeEntry) get(methodEntry.getNameAndTypeIndex());
            @SuppressWarnings("unused") ConstantPool.UTF8Entry utf8NameEntry = (ConstantPool.UTF8Entry) get(nameAndTypeEntry
                  .getNameIndex());
            @SuppressWarnings("unused") ConstantPool.UTF8Entry utf8DescriptorEntry = (ConstantPool.UTF8Entry) get(nameAndTypeEntry
                  .getDescriptorIndex());
            references = new int[]{
                  methodEntry.getClassIndex(),
                  classEntry.getNameIndex(),
                  nameAndTypeEntry.getNameIndex(),
                  nameAndTypeEntry.getDescriptorIndex()
            };
         }else if(_entry instanceof ConstantPool.InterfaceMethodEntry){
            ConstantPool.InterfaceMethodEntry interfaceMethodEntry = (ConstantPool.InterfaceMethodEntry) _entry;
            ConstantPool.ClassEntry classEntry = (ConstantPool.ClassEntry) get(interfaceMethodEntry.getClassIndex());
            @SuppressWarnings("unused") ConstantPool.UTF8Entry utf8Entry = (ConstantPool.UTF8Entry) get(classEntry.getNameIndex());
            ConstantPool.NameAndTypeEntry nameAndTypeEntry = (ConstantPool.NameAndTypeEntry) get(interfaceMethodEntry
                  .getNameAndTypeIndex());
            @SuppressWarnings("unused") ConstantPool.UTF8Entry utf8NameEntry = (ConstantPool.UTF8Entry) get(nameAndTypeEntry
                  .getNameIndex());
            @SuppressWarnings("unused") ConstantPool.UTF8Entry utf8DescriptorEntry = (ConstantPool.UTF8Entry) get(nameAndTypeEntry
                  .getDescriptorIndex());
            references = new int[]{
                  interfaceMethodEntry.getClassIndex(),
                  classEntry.getNameIndex(),
                  nameAndTypeEntry.getNameIndex(),
                  nameAndTypeEntry.getDescriptorIndex()
            };
         }else if(_entry instanceof ConstantPool.FieldEntry){
            ConstantPool.FieldEntry fieldEntry = (ConstantPool.FieldEntry) _entry;
            ConstantPool.ClassEntry classEntry = (ConstantPool.ClassEntry) get(fieldEntry.getClassIndex());
            @SuppressWarnings("unused") ConstantPool.UTF8Entry utf8Entry = (ConstantPool.UTF8Entry) get(classEntry.getNameIndex());
            ConstantPool.NameAndTypeEntry nameAndTypeEntry = (ConstantPool.NameAndTypeEntry) get(fieldEntry.getNameAndTypeIndex());
            @SuppressWarnings("unused") ConstantPool.UTF8Entry utf8NameEntry = (ConstantPool.UTF8Entry) get(nameAndTypeEntry
                  .getNameIndex());
            @SuppressWarnings("unused") ConstantPool.UTF8Entry utf8DescriptorEntry = (ConstantPool.UTF8Entry) get(nameAndTypeEntry
                  .getDescriptorIndex());
            references = new int[]{
                  fieldEntry.getClassIndex(),
                  classEntry.getNameIndex(),
                  nameAndTypeEntry.getNameIndex(),
                  nameAndTypeEntry.getDescriptorIndex()
            };
         }
         return (references);
      }


      Object getConstantEntry(int _constantPoolIndex){
         Entry entry = get(_constantPoolIndex);
         Object object = null;
         switch(entry.getConstantPoolType()){
            case FLOAT:
               object = ((FloatEntry) entry).getValue();
               break;
            case DOUBLE:
               object = ((DoubleEntry) entry).getValue();
               break;
            case INTEGER:
               object = ((IntegerEntry) entry).getValue();
               break;
            case LONG:
               object = ((LongEntry) entry).getValue();
               break;
            case STRING:
               object = ((StringEntry) entry).getStringUTF8Entry().getUTF8();
               break;
         }
         return (object);
      }
   }

   class AttributePool{
      private List<AttributePoolEntry> attributePoolEntries = new ArrayList<AttributePoolEntry>();

      class CodeEntry extends AttributePoolEntry{

         class ExceptionPoolEntry{
            private int exceptionClassIndex;

            private int end;

            private int handler;

            private int start;

            ExceptionPoolEntry(ByteReader _byteReader){
               start = _byteReader.u2();
               end = _byteReader.u2();
               handler = _byteReader.u2();
               exceptionClassIndex = _byteReader.u2();
            }

            ConstantPool.ClassEntry getClassEntry(){
               return (constantPool.getClassEntry(exceptionClassIndex));
            }

            int getClassIndex(){
               return (exceptionClassIndex);
            }

            int getEnd(){
               return (end);
            }

            int getHandler(){
               return (handler);
            }

            int getStart(){
               return (start);
            }

         }

         private List<ExceptionPoolEntry> exceptionPoolEntries = new ArrayList<ExceptionPoolEntry>();

         private AttributePool codeEntryAttributePool;

         private byte[] code;

         private int maxLocals;

         private int maxStack;

         CodeEntry(ByteReader _byteReader, int _nameIndex, int _length){
            super(_byteReader, _nameIndex, _length);
            maxStack = _byteReader.u2();
            maxLocals = _byteReader.u2();
            int codeLength = _byteReader.u4();
            code = _byteReader.bytes(codeLength);
            int exceptionTableLength = _byteReader.u2();
            for(int i = 0; i < exceptionTableLength; i++){
               exceptionPoolEntries.add(new ExceptionPoolEntry(_byteReader));
            }
            codeEntryAttributePool = new AttributePool(_byteReader);
         }

         @Override AttributePool getAttributePool(){
            return (codeEntryAttributePool);
         }

         LineNumberTableEntry getLineNumberTableEntry(){
            return (codeEntryAttributePool.getLineNumberTableEntry());
         }

         int getMaxLocals(){
            return (maxLocals);
         }

         int getMaxStack(){
            return (maxStack);
         }

         byte[] getCode(){
            return code;
         }

         List<ExceptionPoolEntry> getExceptionPoolEntries(){
            return exceptionPoolEntries;
         }
      }

      class ConstantValueEntry extends AttributePoolEntry{
         private int index;

         ConstantValueEntry(ByteReader _byteReader, int _nameIndex, int _length){
            super(_byteReader, _nameIndex, _length);
            index = _byteReader.u2();
         }

         int getIndex(){
            return (index);
         }

      }

      class DeprecatedEntry extends AttributePoolEntry{
         DeprecatedEntry(ByteReader _byteReader, int _nameIndex, int _length){
            super(_byteReader, _nameIndex, _length);
         }

      }

      abstract class AttributePoolEntry{
         protected int length;

         protected int nameIndex;

         AttributePoolEntry(ByteReader _byteReader, int _nameIndex, int _length){
            nameIndex = _nameIndex;
            length = _length;
         }

         AttributePool getAttributePool(){
            return (null);
         }

         int getLength(){
            return (length);
         }

         String getName(){
            return (constantPool.getUTF8Entry(nameIndex).getUTF8());
         }

         int getNameIndex(){
            return (nameIndex);
         }

      }

      abstract class PoolEntry<T> extends AttributePoolEntry implements Iterable<T>{
         private List<T> pool = new ArrayList<T>();

         List<T> getPool(){
            return (pool);
         }

         PoolEntry(ByteReader _byteReader, int _nameIndex, int _length){
            super(_byteReader, _nameIndex, _length);
         }

         @Override
         public Iterator<T> iterator(){
            return (pool.iterator());
         }

      }

      class ExceptionEntry extends PoolEntry<Integer>{

         ExceptionEntry(ByteReader _byteReader, int _nameIndex, int _length){
            super(_byteReader, _nameIndex, _length);
            int exceptionTableLength = _byteReader.u2();
            for(int i = 0; i < exceptionTableLength; i++){
               getPool().add(_byteReader.u2());
            }
         }

      }

      class InnerClassesEntry extends PoolEntry<InnerClassesEntry.InnerClassInfo>{
         class InnerClassInfo{
            private int innerAccess;

            private int innerIndex;

            private int innerNameIndex;

            private int outerIndex;

            InnerClassInfo(ByteReader _byteReader){
               innerIndex = _byteReader.u2();
               outerIndex = _byteReader.u2();
               innerNameIndex = _byteReader.u2();
               innerAccess = _byteReader.u2();
            }

            int getInnerAccess(){
               return (innerAccess);
            }

            int getInnerIndex(){
               return (innerIndex);
            }

            int getInnerNameIndex(){
               return (innerNameIndex);
            }

            int getOuterIndex(){
               return (outerIndex);
            }

         }

         InnerClassesEntry(ByteReader _byteReader, int _nameIndex, int _length){
            super(_byteReader, _nameIndex, _length);
            int innerClassesTableLength = _byteReader.u2();
            for(int i = 0; i < innerClassesTableLength; i++){
               getPool().add(new InnerClassInfo(_byteReader));
            }
         }

      }

      class LineNumberTableEntry extends PoolEntry<LineNumberTableEntry.StartLineNumberPair>{

         class StartLineNumberPair{
            private int lineNumber;

            private int start;

            StartLineNumberPair(ByteReader _byteReader){
               start = _byteReader.u2();
               lineNumber = _byteReader.u2();
            }

            int getLineNumber(){
               return (lineNumber);
            }

            int getStart(){
               return (start);
            }

         }

         LineNumberTableEntry(ByteReader _byteReader, int _nameIndex, int _length){
            super(_byteReader, _nameIndex, _length);
            int lineNumberTableLength = _byteReader.u2();
            for(int i = 0; i < lineNumberTableLength; i++){
               getPool().add(new StartLineNumberPair(_byteReader));
            }
         }

         int getSourceLineNumber(int _start, boolean _exact){
            Iterator<StartLineNumberPair> i = getPool().iterator();
            if(i.hasNext()){
               StartLineNumberPair from = i.next();
               while(i.hasNext()){
                  StartLineNumberPair to = i.next();
                  if(_exact){
                     if(_start == from.getStart()){
                        return (from.getLineNumber());
                     }
                  }else if(_start >= from.getStart() && _start < to.getStart()){
                     return (from.getLineNumber());
                  }
                  from = to;
               }
               if(_exact){
                  if(_start == from.getStart()){
                     return (from.getLineNumber());
                  }
               }else if(_start >= from.getStart()){
                  return (from.getLineNumber());
               }
            }
            return (-1);
         }

      }

      class EnclosingMethodEntry extends AttributePoolEntry{

         EnclosingMethodEntry(ByteReader _byteReader, int _nameIndex, int _length){
            super(_byteReader, _nameIndex, _length);
            enclosingClassIndex = _byteReader.u2();
            enclosingMethodIndex = _byteReader.u2();
         }

         private int enclosingClassIndex;

         int getClassIndex(){
            return (enclosingClassIndex);
         }

         private int enclosingMethodIndex;

         int getMethodIndex(){
            return (enclosingMethodIndex);
         }

      }

      class SignatureEntry extends AttributePoolEntry{

         SignatureEntry(ByteReader _byteReader, int _nameIndex, int _length){
            super(_byteReader, _nameIndex, _length);
            signatureIndex = _byteReader.u2();
         }

         private int signatureIndex;

         int getSignatureIndex(){
            return (signatureIndex);
         }

      }

      public class FakeLocalVariableTableEntry implements LocalVariableTableEntry<FakeLocalVariableTableEntry, FakeLocalVariableTableEntry.Var>, Iterable<LocalVariableInfo>{

         class Var implements LocalVariableInfo{

            int startPc = 0;

            int endPc = 0;

            String name = null;

            boolean arg;

            String descriptor = "";

            int slot;

            Var(InstructionSet.StoreSpec _storeSpec, int _slot, int _startPc, boolean _arg){
               slot = _slot;
               arg = _arg;
               startPc = _startPc;
               if(_storeSpec.equals(InstructionSet.StoreSpec.A)){
                  name = "arr_" + _slot;
                  descriptor = "/* arg */";
               }else{
                  name = _storeSpec.toString().toLowerCase() + "_" + _slot;
                  descriptor = _storeSpec.toString();

               }
            }

            Var(){
               name = "NONE";
            }

            @Override
            public boolean equals(Object object){
               return (object instanceof Var && ((object == this) || ((Var) object).name.equals(name)));
            }

            public String toString(){
               return (name + "[" + startPc + "-" + endPc + "]");
            }

            @Override
            public int getStart(){
               return startPc;
            }

            @Override
            public boolean isArray(){
               return name.startsWith("arr");
            }

            @Override
            public boolean isObject(){
               return name.startsWith("o");
            }

            @Override
            public int getEnd(){
               return endPc;
            }

            @Override
            public int getLength(){
               return endPc - startPc;
            }

            @Override
            public String getVariableName(){
               return (name);
            }

            @Override
            public String getVariableDescriptor(){
               return (descriptor);
            }

            @Override
            public int getSlot(){
               return (slot);
            }
         }

         List<LocalVariableInfo> list = new ArrayList<LocalVariableInfo>();

         public FakeLocalVariableTableEntry(Map<Integer, Instruction> _pcMap, ClassModelMethod _method){
            int numberOfSlots = _method.getCodeEntry().getMaxLocals();

            // MethodDescription description = TypeHelper.getMethodDescription(_method.getDescriptor());

            ArgsAndReturnType argsAndReturnType = _method.getArgsAndReturnType();
            TypeHelper.Arg[] args = argsAndReturnType.getArgs();

            int thisOffset = _method.isStatic() ? 0 : 1;

            Var[] vars = new Var[numberOfSlots + thisOffset];
            InstructionSet.StoreSpec[] argsAsStoreSpecs = new InstructionSet.StoreSpec[args.length + thisOffset];
            if(thisOffset == 1){
               argsAsStoreSpecs[0] = InstructionSet.StoreSpec.O;
               vars[0] = new Var(argsAsStoreSpecs[0], 0, 0, true);
               list.add(vars[0]);

            }

            int currSlotIndex = thisOffset;
            for(int i = 0; i < args.length; i++){
               if(args[i].isArray()){
                  argsAsStoreSpecs[i + thisOffset] = InstructionSet.StoreSpec.A;
               }else if(args[i].isObject()){
                  argsAsStoreSpecs[i + thisOffset] = InstructionSet.StoreSpec.O;
               }else{
                  argsAsStoreSpecs[i + thisOffset] = InstructionSet.StoreSpec.valueOf(args[i].getType().substring(0, 1));
               }
               // Use slot size from TypeSpec to keep vars lined up
               vars[i + thisOffset] = new Var(argsAsStoreSpecs[i + thisOffset], currSlotIndex, 0, true);
               currSlotIndex += argsAsStoreSpecs[i + thisOffset].getTypeSpec().getSlots(); // 1 for most 2 for Long/Double

               // Preserve actual object type
               if(argsAsStoreSpecs[i + thisOffset] == InstructionSet.StoreSpec.O || argsAsStoreSpecs[i + thisOffset] == InstructionSet.StoreSpec.A){
                  vars[i + thisOffset].descriptor = args[i].getType();
               }
               list.add(vars[i + thisOffset]);
            }
            for(int i = args.length + thisOffset; i < numberOfSlots + thisOffset; i++){
               vars[i] = new Var();
            }

            int pc = 0;
            Instruction instruction = null;
            for(Instruction i : _pcMap.values()){
               instruction = i;
               pc = i.getThisPC();
               InstructionSet.StoreSpec storeSpec = i.getByteCode().getStore();
               if(storeSpec != InstructionSet.StoreSpec.NONE){
                  int slotIndex = ((InstructionSet.LocalVariableTableIndexAccessor) i).getLocalVariableTableIndex();
                  Var prevVar = vars[slotIndex];
                  Var var = new Var(storeSpec, slotIndex, pc + i.getLength(), false); // will get collected pretty soon if this is not the same as the previous in this slot
                  if(!prevVar.equals(var)){
                     prevVar.endPc = pc;
                     vars[slotIndex] = var;
                     list.add(vars[slotIndex]);
                  }
               } else if(i.isForwardBranchTarget()){  // Is there an earlier branch branching here   ?
                  // If so we need to descope all vars declared between the brancher and here
                  // this stops
                  // if (){
                  //    int var1=0;
                  // }
                  // int var2=0;
                  // Turning into OpenCL
                  // if (){
                  //    int var=0;
                  // }
                  // var=0; // <- there is no var in scope for this

                  for(Branch b : instruction.getForwardBranches()){
                     for(int slot = 0; slot < numberOfSlots + thisOffset; slot++){
                        if(vars[slot].endPc == 0 && b.getThisPC() < vars[slot].startPc){
                           vars[slot].endPc = pc;
                          // System.out.println("var "+vars[slot].getVariableName()+" is descoped!");
                           vars[slot] = new Var();
                        }
                     }
                  }
               }
            }
            for(int i = 0; i < numberOfSlots + thisOffset; i++){
               vars[i].endPc = pc + instruction.getLength();
            }
            Collections.sort(list, new Comparator<LocalVariableInfo>(){
               @Override
               public int compare(LocalVariableInfo o1, LocalVariableInfo o2){
                  return o1.getStart() - o2.getStart();
               }
            });


         }

         @Override
         public Var getVariable(int _pc, int _slot){
            Var returnValue = null;
            //  System.out.println("pc = " + _pc + " index = " + _index);
            for(LocalVariableInfo localVariableInfo : list){
               // System.out.println("   start=" + localVariableInfo.getStart() + " length=" + localVariableInfo.getLength()
               // + " varidx=" + localVariableInfo.getVariableIndex());
               if(_pc >= localVariableInfo.getStart() - 1 && _pc <= (localVariableInfo.getStart() + localVariableInfo.getLength())
                     && _slot == localVariableInfo.getSlot()){
                  returnValue = (Var) localVariableInfo;
                  break;
               }
            }
            return (returnValue);
         }

         String getVariableName(int _pc, int _index){
            String returnValue = "unknown";
            LocalVariableInfo localVariableInfo = (LocalVariableInfo) getVariable(_pc, _index);
            if(localVariableInfo != null){
               returnValue = ((Var) localVariableInfo).name;
            }
            // System.out.println("returning " + returnValue);
            return (returnValue);
         }

         @Override
         public Iterator<LocalVariableInfo> iterator(){
            return list.iterator();
         }

      }


      class RealLocalVariableTableEntry extends PoolEntry<LocalVariableInfo> implements
            LocalVariableTableEntry<RealLocalVariableTableEntry, RealLocalVariableTableEntry.Var>{

         class Var implements LocalVariableInfo{
            private int descriptorIndex;

            private int usageLength;

            private int variableNameIndex;

            private int startPc;

            private int slot;

            Var(ByteReader _byteReader){
               startPc = _byteReader.u2();
               usageLength = _byteReader.u2();
               variableNameIndex = _byteReader.u2();
               descriptorIndex = _byteReader.u2();
               slot = _byteReader.u2();
            }

            int getDescriptorIndex(){
               return (descriptorIndex);
            }

            public int getLength(){
               return (usageLength);
            }

            int getNameIndex(){
               return (variableNameIndex);
            }

            @Override
            public int getStart(){
               return (startPc);
            }

            @Override
            public int getSlot(){
               return (slot);
            }

            @Override
            public String getVariableName(){
               return (constantPool.getUTF8Entry(variableNameIndex).getUTF8());
            }

            @Override
            public String getVariableDescriptor(){
               return (constantPool.getUTF8Entry(descriptorIndex).getUTF8());
            }

            @Override
            public int getEnd(){
               return (startPc + usageLength);
            }

            @Override
            public boolean isArray(){
               return (getVariableDescriptor().startsWith("["));
            }

            @Override
            public boolean isObject(){
               return (getVariableDescriptor().startsWith("L"));
            }
         }

         RealLocalVariableTableEntry(ByteReader _byteReader, int _nameIndex, int _length){
            super(_byteReader, _nameIndex, _length);
            int localVariableTableLength = _byteReader.u2();
            for(int i = 0; i < localVariableTableLength; i++){
               getPool().add(new Var(_byteReader));
            }


         }

         public Var getVariable(int _pc, int _slot){
            Var returnValue = null;
            // System.out.println("pc = " + _pc + " index = " + _index);
            for(LocalVariableInfo localVariableInfo : getPool()){
               // System.out.println("   start=" + localVariableInfo.getStart() + " length=" + localVariableInfo.getLength()
               // + " varidx=" + localVariableInfo.getVariableIndex());
               if(_pc >= localVariableInfo.getStart() - 1 && _pc <= (localVariableInfo.getStart() + localVariableInfo.getLength())
                     && _slot == localVariableInfo.getSlot()){
                  returnValue = (Var) localVariableInfo;
                  break;
               }
            }
            // System.out.println("returning " + returnValue);
            return (returnValue);
         }

         String getVariableName(int _pc, int _index){
            String returnValue = "unknown";
            Var localVariableInfo = getVariable(_pc, _index);
            if(localVariableInfo != null){
               returnValue = TypeHelper.convert(constantPool.getUTF8Entry(localVariableInfo.getDescriptorIndex()).getUTF8(), constantPool
                     .getUTF8Entry(localVariableInfo.getNameIndex()).getUTF8());
            }
            // System.out.println("returning " + returnValue);
            return (returnValue);
         }

      }

      class BootstrapMethodsEntry extends AttributePoolEntry{
         // http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.7.21
         class BootstrapMethod{
            class BootstrapArgument{
               public BootstrapArgument(ByteReader _byteReader){
                  argument = _byteReader.u2();
               }

               int argument;// u2;
            }

            public BootstrapMethod(ByteReader _byteReader){
               bootstrapMethodRef = _byteReader.u2();
               numBootstrapArguments = _byteReader.u2();
               bootstrapArguments = new BootstrapArgument[numBootstrapArguments];
               for(int i = 0; i < numBootstrapArguments; i++){
                  bootstrapArguments[i] = new BootstrapArgument(_byteReader);
               }
            }

            int bootstrapMethodRef; //u2

            int numBootstrapArguments; //u2

            BootstrapArgument bootstrapArguments[];
         }

         BootstrapMethodsEntry(ByteReader _byteReader, int _nameIndex, int _length){
            super(_byteReader, _nameIndex, _length);
            numBootstrapMethods = _byteReader.u2();
            bootstrapMethods = new BootstrapMethod[numBootstrapMethods];
            for(int i = 0; i < numBootstrapMethods; i++){
               bootstrapMethods[i] = new BootstrapMethod(_byteReader);
            }
         }

         private int numBootstrapMethods;

         BootstrapMethod bootstrapMethods[];

         int getNumBootstrapMethods(){
            return (numBootstrapMethods);
         }

      }

      class OtherEntry extends AttributePoolEntry{
         private byte[] bytes;

         OtherEntry(ByteReader _byteReader, int _nameIndex, int _length){
            super(_byteReader, _nameIndex, _length);
            bytes = _byteReader.bytes(_length);
         }

         byte[] getBytes(){
            return (bytes);
         }

         @Override
         public String toString(){
            return (new String(bytes));
         }

      }

      class StackMapTableEntry extends AttributePoolEntry{
         private byte[] bytes;

         StackMapTableEntry(ByteReader _byteReader, int _nameIndex, int _length){
            super(_byteReader, _nameIndex, _length);
            bytes = _byteReader.bytes(_length);
         }

         byte[] getBytes(){
            return (bytes);
         }

         @Override
         public String toString(){
            return (new String(bytes));
         }
      }

      class LocalVariableTypeTableEntry extends AttributePoolEntry{
         private byte[] bytes;

         LocalVariableTypeTableEntry(ByteReader _byteReader, int _nameIndex, int _length){
            super(_byteReader, _nameIndex, _length);
            bytes = _byteReader.bytes(_length);
         }

         byte[] getBytes(){
            return (bytes);
         }

         @Override
         public String toString(){
            return (new String(bytes));
         }
      }

      class SourceFileEntry extends AttributePoolEntry{
         private int sourceFileIndex;

         SourceFileEntry(ByteReader _byteReader, int _nameIndex, int _length){
            super(_byteReader, _nameIndex, _length);
            sourceFileIndex = _byteReader.u2();
         }

         int getSourceFileIndex(){
            return (sourceFileIndex);
         }

         String getSourceFileName(){
            return (constantPool.getUTF8Entry(sourceFileIndex).getUTF8());
         }

      }

      class SyntheticEntry extends AttributePoolEntry{
         SyntheticEntry(ByteReader _byteReader, int _nameIndex, int _length){
            super(_byteReader, _nameIndex, _length);
         }

      }

      class RuntimeAnnotationsEntry extends PoolEntry<RuntimeAnnotationsEntry.AnnotationInfo>{

         class AnnotationInfo{
            private int typeIndex;

            private int elementValuePairCount;

            class ElementValuePair{
               class Value{
                  Value(int _tag){
                     tag = _tag;
                  }

                  int tag;

               }

               class PrimitiveValue extends Value{
                  private int typeNameIndex;

                  private int constNameIndex;

                  PrimitiveValue(int _tag, ByteReader _byteReader){
                     super(_tag);
                     typeNameIndex = _byteReader.u2();
                     constNameIndex = _byteReader.u2();
                  }

                  int getConstNameIndex(){
                     return (constNameIndex);
                  }

                  int getTypeNameIndex(){
                     return (typeNameIndex);
                  }
               }

               class EnumValue extends Value{
                  EnumValue(int _tag, ByteReader _byteReader){
                     super(_tag);
                  }

               }

               class ArrayValue extends Value{
                  ArrayValue(int _tag, ByteReader _byteReader){
                     super(_tag);
                  }

               }

               class ClassValue extends Value{
                  ClassValue(int _tag, ByteReader _byteReader){
                     super(_tag);
                  }

               }

               class AnnotationValue extends Value{
                  AnnotationValue(int _tag, ByteReader _byteReader){
                     super(_tag);
                  }

               }

               @SuppressWarnings("unused")
               private int elementNameIndex;

               @SuppressWarnings("unused")
               private Value value;

               ElementValuePair(ByteReader _byteReader){
                  elementNameIndex = _byteReader.u2();
                  int tag = _byteReader.u1();

                  switch(tag){
                     case TypeHelper.BYTE:
                     case TypeHelper.CHAR:
                     case TypeHelper.INT:
                     case TypeHelper.LONG:
                     case TypeHelper.DOUBLE:
                     case TypeHelper.FLOAT:
                     case TypeHelper.SHORT:
                     case TypeHelper.BOOLEAN:
                     case TypeHelper.STRING: // special for String
                        value = new PrimitiveValue(tag, _byteReader);
                        break;
                     case TypeHelper.ENUM: // special for Enum
                        value = new EnumValue(tag, _byteReader);
                        break;
                     case TypeHelper.CLASS: // special for class
                        value = new ClassValue(tag, _byteReader);
                        break;
                     case TypeHelper.ANNOTATION: // special for Annotation
                        value = new AnnotationValue(tag, _byteReader);
                        break;
                     case TypeHelper.ARRAY: // special for array
                        value = new ArrayValue(tag, _byteReader);
                        break;
                  }

               }

            }

            ElementValuePair[] elementValuePairs;

            AnnotationInfo(ByteReader _byteReader){
               typeIndex = _byteReader.u2();
               elementValuePairCount = _byteReader.u2();
               elementValuePairs = new ElementValuePair[elementValuePairCount];
               for(int i = 0; i < elementValuePairCount; i++){
                  elementValuePairs[i] = new ElementValuePair(_byteReader);
               }
            }

            int getTypeIndex(){
               return (typeIndex);
            }

            String getTypeDescriptor(){
               return (constantPool.getUTF8Entry(typeIndex).getUTF8());
            }
         }

         RuntimeAnnotationsEntry(ByteReader _byteReader, int _nameIndex, int _length){
            super(_byteReader, _nameIndex, _length);
            int localVariableTableLength = _byteReader.u2();
            for(int i = 0; i < localVariableTableLength; i++){
               getPool().add(new AnnotationInfo(_byteReader));
            }
         }

      }

      private CodeEntry codeEntry = null;

      private EnclosingMethodEntry enclosingMethodEntry = null;

      private DeprecatedEntry deprecatedEntry = null;

      private ExceptionEntry exceptionEntry = null;

      private LineNumberTableEntry lineNumberTableEntry = null;

      private RealLocalVariableTableEntry realLocalVariableTableEntry = null;

      private FakeLocalVariableTableEntry fakeLocalVariableTableEntry = null;

      private RuntimeAnnotationsEntry runtimeVisibleAnnotationsEntry;

      private RuntimeAnnotationsEntry runtimeInvisibleAnnotationsEntry;

      private SourceFileEntry sourceFileEntry = null;

      private SyntheticEntry syntheticEntry = null;

      private BootstrapMethodsEntry bootstrapMethodsEntry = null;

      private final static String LOCALVARIABLETABLE_TAG = "LocalVariableTable";

      private final static String CONSTANTVALUE_TAG = "ConstantValue";

      private final static String LINENUMBERTABLE_TAG = "LineNumberTable";

      private final static String SOURCEFILE_TAG = "SourceFile";

      private final static String SYNTHETIC_TAG = "Synthetic";

      private final static String EXCEPTIONS_TAG = "Exceptions";

      private final static String INNERCLASSES_TAG = "InnerClasses";

      private final static String DEPRECATED_TAG = "Deprecated";

      private final static String CODE_TAG = "Code";

      private final static String ENCLOSINGMETHOD_TAG = "EnclosingMethod";

      private final static String SIGNATURE_TAG = "Signature";

      private final static String RUNTIMEINVISIBLEANNOTATIONS_TAG = "RuntimeInvisibleAnnotations";

      private final static String RUNTIMEVISIBLEANNOTATIONS_TAG = "RuntimeVisibleAnnotations";

      private final static String BOOTSTRAPMETHODS_TAG = "BootstrapMethods";

      private final static String STACKMAPTABLE_TAG = "StackMapTable";

      private final static String LOCALVARIABLETYPETABLE_TAG = "LocalVariableTypeTable";

      AttributePool(ByteReader _byteReader){

         int attributeCount = _byteReader.u2();
         AttributePoolEntry entry = null;
         for(int i = 0; i < attributeCount; i++){
            int attributeNameIndex = _byteReader.u2();
            int length = _byteReader.u4();
            String attributeName = constantPool.getUTF8Entry(attributeNameIndex).getUTF8();
            if(attributeName.equals(LOCALVARIABLETABLE_TAG)){
               realLocalVariableTableEntry = new RealLocalVariableTableEntry(_byteReader, attributeNameIndex, length);
               entry = (RealLocalVariableTableEntry) realLocalVariableTableEntry;
            }else if(attributeName.equals(CONSTANTVALUE_TAG)){
               entry = new ConstantValueEntry(_byteReader, attributeNameIndex, length);
            }else if(attributeName.equals(LINENUMBERTABLE_TAG)){
               lineNumberTableEntry = new LineNumberTableEntry(_byteReader, attributeNameIndex, length);
               entry = lineNumberTableEntry;
            }else if(attributeName.equals(SOURCEFILE_TAG)){
               sourceFileEntry = new SourceFileEntry(_byteReader, attributeNameIndex, length);
               entry = sourceFileEntry;
            }else if(attributeName.equals(SYNTHETIC_TAG)){
               syntheticEntry = new SyntheticEntry(_byteReader, attributeNameIndex, length);
               entry = syntheticEntry;
            }else if(attributeName.equals(EXCEPTIONS_TAG)){
               exceptionEntry = new ExceptionEntry(_byteReader, attributeNameIndex, length);
               entry = exceptionEntry;
            }else if(attributeName.equals(INNERCLASSES_TAG)){
               entry = new InnerClassesEntry(_byteReader, attributeNameIndex, length);
            }else if(attributeName.equals(DEPRECATED_TAG)){
               deprecatedEntry = new DeprecatedEntry(_byteReader, attributeNameIndex, length);
               entry = deprecatedEntry;
            }else if(attributeName.equals(CODE_TAG)){
               codeEntry = new CodeEntry(_byteReader, attributeNameIndex, length);
               entry = codeEntry;
            }else if(attributeName.equals(ENCLOSINGMETHOD_TAG)){
               enclosingMethodEntry = new EnclosingMethodEntry(_byteReader, attributeNameIndex, length);
               entry = enclosingMethodEntry;
            }else if(attributeName.equals(SIGNATURE_TAG)){
               entry = new SignatureEntry(_byteReader, attributeNameIndex, length);
            }else if(attributeName.equals(RUNTIMEINVISIBLEANNOTATIONS_TAG)){
               runtimeInvisibleAnnotationsEntry = new RuntimeAnnotationsEntry(_byteReader, attributeNameIndex, length);
               entry = runtimeInvisibleAnnotationsEntry;
            }else if(attributeName.equals(RUNTIMEVISIBLEANNOTATIONS_TAG)){
               runtimeVisibleAnnotationsEntry = new RuntimeAnnotationsEntry(_byteReader, attributeNameIndex, length);
               entry = runtimeVisibleAnnotationsEntry;
            }else if(attributeName.equals(BOOTSTRAPMETHODS_TAG)){
               bootstrapMethodsEntry = new BootstrapMethodsEntry(_byteReader, attributeNameIndex, length);
               entry = bootstrapMethodsEntry;
               // http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.7.21
            }else if(attributeName.equals(STACKMAPTABLE_TAG)){
               // http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.7.4
               entry = new StackMapTableEntry(_byteReader, attributeNameIndex, length);
            }else if(attributeName.equals(LOCALVARIABLETYPETABLE_TAG)){
               // http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.7.14
               entry = new LocalVariableTypeTableEntry(_byteReader, attributeNameIndex, length);
            }else{
               logger.warning("Found unexpected Attribute (name = " + attributeName + ")");
               entry = new OtherEntry(_byteReader, attributeNameIndex, length);
               attributePoolEntries.add(entry);
            }
         }

      }

      CodeEntry getCodeEntry(){
         return (codeEntry);
      }

      DeprecatedEntry getDeprecatedEntry(){
         return (deprecatedEntry);
      }

      ExceptionEntry getExceptionEntry(){
         return (exceptionEntry);
      }

      LineNumberTableEntry getLineNumberTableEntry(){
         return (lineNumberTableEntry);
      }

      LocalVariableTableEntry getRealLocalVariableTableEntry(){
         return (realLocalVariableTableEntry);
      }
      LocalVariableTableEntry getFakeLocalVariableTableEntry(){
         return (fakeLocalVariableTableEntry);
      }

      SourceFileEntry getSourceFileEntry(){
         return (sourceFileEntry);
      }

      SyntheticEntry getSyntheticEntry(){
         return (syntheticEntry);
      }

      RuntimeAnnotationsEntry getRuntimeInvisibleAnnotationsEntry(){
         return (runtimeInvisibleAnnotationsEntry);
      }

      RuntimeAnnotationsEntry getRuntimeVisibleAnnotationsEntry(){
         return (runtimeVisibleAnnotationsEntry);
      }

      RuntimeAnnotationsEntry getBootstrap(){
         return (runtimeVisibleAnnotationsEntry);
      }

   }

   static ClassLoader classModelLoader = ClassModel.class.getClassLoader();

   class ClassModelField{
      private int fieldAccessFlags;

      AttributePool fieldAttributePool;

      private int descriptorIndex;

      private int index;

      private int nameIndex;

      ClassModelField(ByteReader _byteReader, int _index){
         index = _index;
         fieldAccessFlags = _byteReader.u2();
         nameIndex = _byteReader.u2();
         descriptorIndex = _byteReader.u2();
         fieldAttributePool = new AttributePool(_byteReader);
      }

      int getAccessFlags(){
         return (fieldAccessFlags);
      }

      AttributePool getAttributePool(){
         return (fieldAttributePool);
      }

      String getDescriptor(){
         return (getDescriptorUTF8Entry().getUTF8());
      }

      int getDescriptorIndex(){
         return (descriptorIndex);
      }

      ConstantPool.UTF8Entry getDescriptorUTF8Entry(){
         return (constantPool.getUTF8Entry(descriptorIndex));
      }

      int getIndex(){
         return (index);
      }

      String getName(){
         return (getNameUTF8Entry().getUTF8());
      }

      int getNameIndex(){
         return (nameIndex);
      }

      ConstantPool.UTF8Entry getNameUTF8Entry(){
         return (constantPool.getUTF8Entry(nameIndex));
      }

      Type type;

      Type getType(){
         if(type == null){
            type = new Type(getDescriptor());
         }
         return (type);
      }


   }

   class ClassModelMethod{

      private int methodAccessFlags;

      private AttributePool methodAttributePool;

      private int descriptorIndex;

      private int index;

      private int nameIndex;

      private CodeEntry codeEntry;

      ClassModelMethod(ByteReader _byteReader, int _index){
         index = _index;
         methodAccessFlags = _byteReader.u2();
         nameIndex = _byteReader.u2();
         descriptorIndex = _byteReader.u2();
         methodAttributePool = new AttributePool(_byteReader);
         codeEntry = methodAttributePool.getCodeEntry();
      }

      int getAccessFlags(){
         return (methodAccessFlags);
      }

      public boolean isStatic(){
         return (Access.STATIC.bitIsSet(methodAccessFlags));
      }

      AttributePool getAttributePool(){
         return (methodAttributePool);
      }

      AttributePool.CodeEntry getCodeEntry(){
         return (methodAttributePool.getCodeEntry());
      }

      String getDescriptor(){
         return (getDescriptorUTF8Entry().getUTF8());
      }

      int getDescriptorIndex(){
         return (descriptorIndex);
      }

      ConstantPool.UTF8Entry getDescriptorUTF8Entry(){
         return (constantPool.getUTF8Entry(descriptorIndex));
      }

      ArgsAndReturnType argsAndReturnType;

      ArgsAndReturnType getArgsAndReturnType(){
         if(argsAndReturnType == null){
            argsAndReturnType = new ArgsAndReturnType(getDescriptor());
         }
         return (argsAndReturnType);
      }

      int getIndex(){
         return (index);
      }

      String getName(){
         return (getNameUTF8Entry().getUTF8());
      }

      int getNameIndex(){
         return (nameIndex);
      }

      ConstantPool.UTF8Entry getNameUTF8Entry(){
         return (constantPool.getUTF8Entry(nameIndex));
      }

      ConstantPool getConstantPool(){
         return (constantPool);
      }

      AttributePool.LineNumberTableEntry getLineNumberTableEntry(){
         return (getAttributePool().codeEntry.codeEntryAttributePool.lineNumberTableEntry);
      }
      AttributePool.RealLocalVariableTableEntry getRealLocalVariableTableEntry(){
            return (getAttributePool().codeEntry.codeEntryAttributePool.realLocalVariableTableEntry);

      }
      AttributePool.FakeLocalVariableTableEntry getFakeLocalVariableTableEntry(){
            return (getAttributePool().codeEntry.codeEntryAttributePool.fakeLocalVariableTableEntry);

      }
      LocalVariableTableEntry getPreferredLocalVariableTableEntry(){
         if (Config.enableUseRealLocalVariableTableIfAvailable){
            return ((LocalVariableTableEntry)getRealLocalVariableTableEntry());
         }else{
            return ((LocalVariableTableEntry)getFakeLocalVariableTableEntry());
         }
      }

      void setFakeLocalVariableTableEntry(AttributePool.FakeLocalVariableTableEntry _localVariableTableEntry){
         getAttributePool().codeEntry.codeEntryAttributePool.fakeLocalVariableTableEntry = _localVariableTableEntry;
      }

      LocalVariableInfo getLocalVariable(int _pc, int _index){
         return (getPreferredLocalVariableTableEntry().getVariable(_pc, _index));
      }

      byte[] getCode(){
         return (codeEntry.getCode());
      }

      ClassModel getClassModel(){
         return (ClassModel.this);
      }

      public String toString(){
         return getClassModel().getDotClassName() + "." + getName() + " " + getDescriptor();
      }

      Map<Integer, Instruction> pcMap;
      Set<InstructionSet.Branch> branches;

      Set<InstructionSet.Branch> getBranches(){
         getInstructionMap(); // remember it is lazy
         return (branches);
      }

      Set<Instruction> branchTargets;

      Set<Instruction> getBranchTargets(){
         getInstructionMap(); // remember it is lazy
         return (branchTargets);
      }

      Set<InstructionSet.MethodCall> methodCalls;

      public Set<InstructionSet.MethodCall> getMethodCalls(){
         getInstructionMap(); // remember it is lazy
         return (methodCalls);
      }

      Set<InstructionSet.AccessField> accessedFields;

      public Set<InstructionSet.AccessField> getFieldAccesses(){
         getInstructionMap(); // remember it is lazy

         return (accessedFields);
      }
       Set<InstructionSet.LocalVariableTableIndexAccessor> accessedLocalVariables;
       public Set<InstructionSet.LocalVariableTableIndexAccessor> getLocalVariableAccesses(){
           getInstructionMap(); // remember it is lazy

           return (accessedLocalVariables);
       }

      /**
       * Create a linked list of instructions (from pcHead to pcTail).
       * <p/>
       * Returns a map of int (pc) to Instruction which to allow us to quickly get from a bytecode offset to the appropriate instruction.
       * <p/>
       * Note that not all int values from 0 to code.length values will map to a valid instruction, if pcMap.get(n) == null then this implies
       * that 'n' is not the start of an instruction
       * <p/>
       * So either pcMap.get(i)== null or pcMap.get(i).getThisPC()==i
       *
       * @return Map<Integer, Instruction> the returned pc to Instruction map
       */
      Map<Integer, Instruction> getInstructionMap(){
         // We build this lazily
         if(pcMap == null){
            Instruction pcHead = null;
            Instruction pcTail = null;
            pcMap = new LinkedHashMap<Integer, Instruction>();
            branches = new LinkedHashSet<InstructionSet.Branch>();
            branchTargets = new LinkedHashSet<Instruction>();
            methodCalls = new LinkedHashSet<InstructionSet.MethodCall>();
            accessedFields = new LinkedHashSet<InstructionSet.AccessField>();
             accessedLocalVariables = new LinkedHashSet<InstructionSet.LocalVariableTableIndexAccessor>();
            byte[] code = getCode();

            // We create a byteReader for reading the bytes from the code array
            ByteReader codeReader = new ByteReader(code);
            while(codeReader.hasMore()){
               // Create an instruction from code reader's current position
               int pc = codeReader.getOffset();
               Instruction instruction = InstructionSet.ByteCode.create(this, codeReader);

               if(instruction instanceof InstructionSet.Branch){
                  branches.add(instruction.asBranch());
               }
               if(instruction instanceof InstructionSet.MethodCall){
                  InstructionSet.MethodCall methodCall = (InstructionSet.MethodCall) instruction;
                  methodCalls.add(methodCall);
               }
               if(instruction instanceof InstructionSet.AccessField){
                  InstructionSet.AccessField accessField = (InstructionSet.AccessField) instruction;
                  accessedFields.add(accessField);
               }

                if(instruction instanceof InstructionSet.LocalVariableTableIndexAccessor){
                    InstructionSet.LocalVariableTableIndexAccessor accessLocalVariable = (InstructionSet.LocalVariableTableIndexAccessor) instruction;
                    accessedLocalVariables.add(accessLocalVariable);
                }
               pcMap.put(pc, instruction);

               // list maintenance, make this the pcHead if pcHead is null
               if(pcHead == null){
                  pcHead = instruction;
               }

               // extend the list of instructions here we make the new instruction point to previous tail
               instruction.setPrevPC(pcTail);
               // if tail exists (not the first instruction in the list) link it to the new instruction
               if(pcTail != null){
                  pcTail.setNextPC(instruction);
               }
               // now move the tail along
               pcTail = instruction;

            }

            // Here we connect the branch nodes to the instruction that they branch to.
            //
            // Each branch node contains a 'target' field indended to reference the node that the branch targets. Each instruction also contain four separate lists of branch nodes that reference it.
            // These lists hold forwardConditional, forwardUnconditional, reverseConditional and reverseUnconditional branches that reference it.
            //
            // So assuming that we had a branch node at pc offset 100 which represented 'goto 200'.
            //
            // Following this loop the branch node at pc offset 100 will have a 'target' field which actually references the instruction at pc offset 200, and the instruction at pc offset 200 will
            // have the branch node (at 100) added to it's forwardUnconditional list.
            //
            // @see InstructionSet.Branch#getTarget()

            for(InstructionSet.Branch branch : branches){
               Instruction targetInstruction = pcMap.get(branch.getAbsolute());
               branchTargets.add(targetInstruction);
               branch.setTarget(targetInstruction);
            }

            // We need to remove some javac optimizations
            // Javac optimizes some branches to avoid goto->goto, branch->goto etc.


            for(InstructionSet.Branch branch : branches){
               if(branch.isReverse()){
                  Instruction target = branch.getTarget();
                  LinkedList<InstructionSet.Branch> list = target.getReverseUnconditionalBranches();
                  if((list != null) && (list.size() > 0) && (list.get(list.size() - 1) != branch)){
                     InstructionSet.Branch unconditional = list.get(list.size() - 1).asBranch();
                     branch.retarget(unconditional);

                  }
               }
            }

            AttributePool.RealLocalVariableTableEntry realLocalVariableTableEntry =  getRealLocalVariableTableEntry();

             if(realLocalVariableTableEntry != null && Config.enableShowRealLocalVariableTable){
                 Table table = new Table("|  %3d","|  %3d",  "|   %3d", "|  %2d", "|%4s", "| %8s|");
                 table.header("|Start","|  End", "|Length", "|Slot", "|Name", "|Signature|");
                 AttributePool.RealLocalVariableTableEntry real = realLocalVariableTableEntry;
                 for(LocalVariableInfo var : real){
                     table.data(var.getStart());
                     table.data(var.getEnd());
                     table.data(var.getLength());
                     table.data(var.getSlot());
                     table.data(var.getVariableName());
                     table.data(var.getVariableDescriptor());
                 }
                 System.out.println("REAL!\n"+table);
             }



               AttributePool.FakeLocalVariableTableEntry fakeLocalVariableTableEntry = attributePool.new FakeLocalVariableTableEntry(pcMap, this);

               setFakeLocalVariableTableEntry(fakeLocalVariableTableEntry);
                if(Config.enableShowFakeLocalVariableTable){
                    Table table = new Table("|  %3d","|  %3d",  "|   %3d", "|  %2d", "|%4s", "| %8s|");
                    table.header("|Start","|  End", "|Length", "|Slot", "|Name", "|Signature|");
                    AttributePool.FakeLocalVariableTableEntry fake =  fakeLocalVariableTableEntry;
                    for(LocalVariableInfo var : fake){
                        table.data(var.getStart());
                        table.data(var.getEnd());
                        table.data(var.getLength());
                        table.data(var.getSlot());
                        table.data(var.getVariableName());
                        table.data(var.getVariableDescriptor());
                    }
                    System.out.println("FAKE!\n"+table);
                }


            LocalVariableTableEntry localVariableTableEntry = getPreferredLocalVariableTableEntry();

            for(InstructionSet.LocalVariableTableIndexAccessor instruction : accessedLocalVariables){
                int pc = ((Instruction)instruction).getThisPC();
                int len =  ((Instruction)instruction).getLength();
                int varIndex = instruction.getLocalVariableTableIndex();
                LocalVariableInfo var = localVariableTableEntry.getVariable(pc+len, varIndex);
                if (var == null){
                    System.out.println("Screwed!");
                }
                instruction.setLocalVariableInfo(var);
            }

         }
         return (pcMap);
      }


      public Collection<Instruction> getInstructions(){
         return (getInstructionMap().values());
      }

      public int getInstructionCount(){
         return getInstructionMap().size();
      }


   }

   class ClassModelInterface{
      private int interfaceIndex;

      ClassModelInterface(ByteReader _byteReader){
         interfaceIndex = _byteReader.u2();
      }

      ConstantPool.ClassEntry getClassEntry(){
         return (constantPool.getClassEntry(interfaceIndex));
      }

      int getInterfaceIndex(){
         return (interfaceIndex);
      }

   }

   //private Class<?> clazz;


   void parse(InputStream _inputStream) throws ClassParseException{

      ByteReader byteReader = new ByteReader(_inputStream);
      magic = byteReader.u4();
      minorVersion = byteReader.u2();
      majorVersion = byteReader.u2();
      constantPool = new ConstantPool(byteReader);

      accessFlags = byteReader.u2();
      thisClassConstantPoolIndex = byteReader.u2();
      superClassConstantPoolIndex = byteReader.u2();

      int interfaceCount = byteReader.u2();
      for(int i = 0; i < interfaceCount; i++){
         ClassModelInterface iface = new ClassModelInterface(byteReader);
         interfaces.add(iface);

      }

      int fieldCount = byteReader.u2();
      for(int i = 0; i < fieldCount; i++){
         ClassModelField field = new ClassModelField(byteReader, i);
         fields.add(field);

      }

      int methodPoolLength = byteReader.u2();
      for(int i = 0; i < methodPoolLength; i++){
         ClassModelMethod method = new ClassModelMethod(byteReader, i);
         methods.add(method);

      }

      attributePool = new AttributePool(byteReader);

   }

   int getMagic(){
      return (magic);
   }

   int getMajorVersion(){
      return (majorVersion);
   }

   int getMinorVersion(){
      return (minorVersion);
   }

   int getAccessFlags(){
      return (accessFlags);
   }

   ConstantPool getConstantPool(){
      return (constantPool);
   }

   int getThisClassConstantPoolIndex(){
      return (thisClassConstantPoolIndex);
   }

   int getSuperClassConstantPoolIndex(){
      return (superClassConstantPoolIndex);
   }

   AttributePool getAttributePool(){
      return (attributePool);
   }

   ClassModelField getField(String _name, String _descriptor){
      for(ClassModelField entry : fields){
         if(entry.getName().equals(_name) && entry.getDescriptor().equals(_descriptor)){
            return (entry);
         }
      }
      return superClazzModel.getField(_name, _descriptor);
   }

   ClassModelField getField(String _name){
      for(ClassModelField entry : fields){
         if(entry.getName().equals(_name)){
            return (entry);
         }
      }
      return superClazzModel.getField(_name);
   }

   ClassModelMethod getMethod(String _name, String _descriptor){
      for(ClassModelMethod entry : methods){
         if(entry.getName().equals(_name) && entry.getDescriptor().equals(_descriptor)){
            return (entry);
         }
      }
      return superClazzModel != null ? superClazzModel.getMethod(_name, _descriptor) : (null);
   }

   List<ClassModelField> getFieldPoolEntries(){
      return (fields);
   }

   /**
    * Look up a ConstantPool MethodEntry and return the corresponding Method.
    *
    * @param _methodEntry The ConstantPool MethodEntry we want.
    * @param _isSpecial   True if we wish to delegate to super (to support <code>super.foo()</code>)
    * @return The Method or null if we fail to locate a given method.
    */
   ClassModelMethod getMethod(MethodEntry _methodEntry, boolean _isSpecial){
      String entryClassNameInDotForm = _methodEntry.getClassEntry().getDotClassName();

      // Shortcut direct calls to supers to allow "foo() { super.foo() }" type stuff to work
      if(_isSpecial && (superClazzModel != null) && superClazzModel.isSuperClass(entryClassNameInDotForm)){
         if(logger.isLoggable(Level.FINE)){
            logger.fine("going to look in super:" + superClazzModel.getDotClassName() + " on behalf of "
                  + entryClassNameInDotForm);
         }
         return superClazzModel.getMethod(_methodEntry, false);
      }

      for(ClassModelMethod entry : methods){
         if(entry.getName().equals(_methodEntry.getNameAndTypeEntry().getNameUTF8Entry().getUTF8())
               && entry.getDescriptor().equals(_methodEntry.getNameAndTypeEntry().getDescriptorUTF8Entry().getUTF8())){
            if(logger.isLoggable(Level.FINE)){
               logger.fine("Found " + getDotClassName()
                     + "." + entry.getName() + " " + entry.getDescriptor() + " for "
                     + entryClassNameInDotForm);
            }
            return (entry);
         }
      }

      return superClazzModel != null ? superClazzModel.getMethod(_methodEntry, false) : (null);
   }

   /**
    * Create a MethodModel for a given method name and signature.
    *
    * @param _name
    * @param _signature
    * @return
    * @throws AparapiException
    */

   public MethodModel getMethodModel(String _name, String _signature) throws AparapiException{
      ClassModelMethod method = getMethod(_name, _signature);
      return new MethodModel(method);
   }

   // These fields use for accessor conversion
   private ArrayList<FieldEntry> structMembers = new ArrayList<FieldEntry>();

   private ArrayList<Long> structMemberOffsets = new ArrayList<Long>();

   private ArrayList<TypeSpec> structMemberTypes = new ArrayList<TypeSpec>();

   private int totalStructSize = 0;

   ArrayList<FieldEntry> getStructMembers(){
      return structMembers;
   }

   ArrayList<Long> getStructMemberOffsets(){
      return structMemberOffsets;
   }

   ArrayList<TypeSpec> getStructMemberTypes(){
      return structMemberTypes;
   }

   int getTotalStructSize(){
      return totalStructSize;
   }

   void setTotalStructSize(int x){
      totalStructSize = x;
   }

   Entrypoint getLambdaEntrypoint(String _entrypointName, String _descriptor, Object _k) throws AparapiException{
      MethodModel method = getMethodModel(_entrypointName, _descriptor);
      return (new Entrypoint(this, method, _k, true));
   }

   Entrypoint getKernelEntrypoint(String _entrypointName, String _descriptor, Object _k) throws AparapiException{
      MethodModel method = getMethodModel(_entrypointName, _descriptor);
      return (new Entrypoint(this, method, _k, false));
   }

   Class<?> getClassWeAreModelling(){
      return clazz;
   }

   public Entrypoint getKernelEntrypoint(String _entrypointName, Object _k) throws AparapiException{
      return (getKernelEntrypoint(_entrypointName, "()V", _k));
   }

   public Entrypoint getLambdaEntrypoint(String _entrypointName, Object _k) throws AparapiException{
      return (getLambdaEntrypoint(_entrypointName, "()V", _k));
   }

   public Entrypoint getKernelEntrypoint() throws AparapiException{
      return (getKernelEntrypoint("run", "()V", null));
   }

   public Entrypoint getLambdaEntrypoint() throws AparapiException{
      return (getLambdaEntrypoint("run", "()V", null));
   }

   public String getClassName(){
      ConstantPool.ClassEntry thisClassEntry = constantPool.getClassEntry(getThisClassConstantPoolIndex());
      return (thisClassEntry.getClassName());
   }

   public String getDotClassName(){
      ConstantPool.ClassEntry thisClassEntry = constantPool.getClassEntry(getThisClassConstantPoolIndex());
      return (thisClassEntry.getDotClassName());
   }

   public String getMangledClassName(){

      ConstantPool.ClassEntry thisClassEntry = constantPool.getClassEntry(getThisClassConstantPoolIndex());
      return (thisClassEntry.getMangledClassName());
   }

   public String getSuperClassName(){
      ConstantPool.ClassEntry superClassEntry = constantPool.getClassEntry(getSuperClassConstantPoolIndex());
      return (superClassEntry.getClassName());
   }

   public String getSuperDotClassName(){
      ConstantPool.ClassEntry superClassEntry = constantPool.getClassEntry(getSuperClassConstantPoolIndex());
      return (superClassEntry.getDotClassName());
   }

}
