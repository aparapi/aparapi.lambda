

#define CLASSTOOLS_CPP
#include "classtools.h"

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

u1_t ByteBuffer::u1(byte_t *ptr){
   u1_t u1 = (u1_t) (*ptr & 0xff);
   //     fprintf(stderr, "u1 %01x\n", u1);
   return (u1);
}
u2_t ByteBuffer::u2(byte_t *ptr){
   u2_t u2 = (u1(ptr)<<8)|u1(ptr+1);
   //     fprintf(stderr, "u2 %02x\n", u2);
   return (u2);
}
s2_t ByteBuffer::s2(byte_t *ptr){
   u2_t u2 = (u1(ptr)<<8)|u1(ptr+1);
   //     fprintf(stderr, "u2 %02x\n", u2);
   return ((s2_t)u2);
}
u4_t ByteBuffer::u4(byte_t *ptr){
   u4_t u4 = (u2(ptr)<<16)|u2(ptr+2);
   //      fprintf(stderr, "u4 %04x\n", u4);
   return (u4);
}
s4_t ByteBuffer::s4(byte_t *ptr){
   u4s4f4_u u4s4f4;
   u4s4f4.u4 = u4(ptr);
   //      fprintf(stderr, "u4 %04x\n", u4);
   return (u4s4f4.s4);
}
f4_t ByteBuffer::f4(byte_t *ptr){
   u4s4f4_u u4s4f4;
   u4s4f4.u4 = u4(ptr);
   //      fprintf(stderr, "u4 %04x\n", u4);
   return (u4s4f4.f4);
}
u8_t ByteBuffer::u8(byte_t *ptr){
   u8_t u8 = (((u8_t)u4(ptr))<<32)|u4(ptr+4);
   //      fprintf(stderr, "u8 %08lx\n", u8);
   return (u8);
}
s8_t ByteBuffer::s8(byte_t *ptr){
   u8s8f8_u u8s8f8;
   u8s8f8.u8 = u8(ptr);
   //      fprintf(stderr, "u8 %08x\n", u8);
   return (u8s8f8.s8);
}
f8_t ByteBuffer::f8(byte_t *ptr){
   u8s8f8_u u8s8f8;
   u8s8f8.u8 = u8(ptr);
   //      fprintf(stderr, "u8 %08x\n", u8);
   return (u8s8f8.f8);
}
ByteBuffer::ByteBuffer(byte_t *_bytes, size_t _len)
   : len(_len), bytes(_bytes), ptr(_bytes){
   }
ByteBuffer::~ByteBuffer(){
}
byte_t *ByteBuffer::getBytes(){
   return(bytes);
}
size_t ByteBuffer::getLen(){
   return(len);
}
u1_t ByteBuffer::u1(){
   u1_t value = u1(ptr); ptr+=1;
   return (value);
}
u2_t ByteBuffer::u2(){
   u2_t value = u2(ptr); ptr+=2;
   return (value);
}
s2_t ByteBuffer::s2(){
   s2_t value = s2(ptr); ptr+=2;
   return (value);
}
u4_t ByteBuffer::u4(){
   u4_t value = u4(ptr); ptr+=4;
   return (value);
}
f4_t ByteBuffer::f4(){
   f4_t  value = f4(ptr); ptr+=4;
   return (value);
}
s4_t ByteBuffer::s4(){
   s4_t value = s4(ptr); ptr+=4;
   return (value);
}
u8_t ByteBuffer::u8(){
   u8_t value = u8(ptr); ptr+=8;
   return (value);
}
f8_t ByteBuffer::f8(){
   f8_t value = f8(ptr); ptr+=8;
   return (value);
}
s8_t ByteBuffer::s8(){
   s8_t value = s8(ptr); ptr+=8;
   return (value);
}

char *ByteBuffer::createUTF8(int _len){
   char *buf = NULL;
   if (_len > 0){
      buf = new char[_len];
   }
   memcpy((void*)buf, (void*)ptr, _len);
   ptr+=_len;
   return(buf); 
}

ByteBuffer *ByteBuffer::getByteBuffer(int _len){
   byte_t *buf = ptr;
   ptr+=_len;
   return new ByteBuffer(buf, _len);
}
size_t  ByteBuffer::getOffset(){
   return((size_t)(ptr-bytes));
}
bool  ByteBuffer::empty(){
   return(getOffset()>=len);
}

ConstantPoolEntry::ConstantPoolEntry(ByteBuffer *_byteBuffer, ConstantPoolType _constantPoolType)
   : constantPoolType(_constantPoolType) {
   }
ConstantPoolEntry::~ConstantPoolEntry(){
}

ConstantPoolType ConstantPoolEntry::getConstantPoolType() {
   return (constantPoolType);
}

EmptyConstantPoolEntry::EmptyConstantPoolEntry(ByteBuffer *_byteBuffer)
   :  ConstantPoolEntry(_byteBuffer, EMPTY) {
   }
EmptyConstantPoolEntry::~EmptyConstantPoolEntry(){
}

UTF8ConstantPoolEntry::UTF8ConstantPoolEntry(ByteBuffer *_byteBuffer) 
   : ConstantPoolEntry(_byteBuffer, UTF8) {
      len = (size_t)_byteBuffer->u2();
      utf8 = _byteBuffer->createUTF8(len);
   }
UTF8ConstantPoolEntry::~UTF8ConstantPoolEntry(){
   delete utf8;
}
size_t UTF8ConstantPoolEntry::getLen() {
   return(len);
}
char *UTF8ConstantPoolEntry::getUTF8() {
   return(utf8);
}
void UTF8ConstantPoolEntry::write(FILE *file){
   fprintf(file, "len %d \"", (int)len);
   if (len != 0 && utf8 != NULL){
      for (unsigned int i=0; i<len; i++){
         fprintf(file, "%c", utf8[i]);
      }
   }
   fprintf(file, "\"");
}

IntegerConstantPoolEntry::IntegerConstantPoolEntry(ByteBuffer *_byteBuffer)
   :  ConstantPoolEntry(_byteBuffer, INTEGER) {
      value = _byteBuffer->s4();
   }
IntegerConstantPoolEntry::~IntegerConstantPoolEntry(){
}

s4_t IntegerConstantPoolEntry::getValue(){
   return(value);
}

FloatConstantPoolEntry::FloatConstantPoolEntry(ByteBuffer *_byteBuffer)
   :  ConstantPoolEntry(_byteBuffer, FLOAT) {
      value = _byteBuffer->f4();
   }
FloatConstantPoolEntry::~FloatConstantPoolEntry(){
}

f4_t FloatConstantPoolEntry::getValue(){
   return(value);
}

DoubleConstantPoolEntry::DoubleConstantPoolEntry(ByteBuffer *_byteBuffer)
   :  ConstantPoolEntry(_byteBuffer, DOUBLE) {
      value = _byteBuffer->f8();
   }
DoubleConstantPoolEntry::~DoubleConstantPoolEntry(){
}
f8_t DoubleConstantPoolEntry::getValue(){
   return(value);
}

LongConstantPoolEntry::LongConstantPoolEntry(ByteBuffer *_byteBuffer)
   :  ConstantPoolEntry(_byteBuffer, LONG) {
      value = _byteBuffer->s8();
   }
LongConstantPoolEntry::~LongConstantPoolEntry(){
}
s8_t LongConstantPoolEntry::getValue(){
   return(value);
}

ClassConstantPoolEntry::ClassConstantPoolEntry(ByteBuffer *_byteBuffer)
   :  ConstantPoolEntry(_byteBuffer, CLASS) {
      nameIndex = _byteBuffer->u2();
   }
ClassConstantPoolEntry::~ClassConstantPoolEntry(){
}
u2_t ClassConstantPoolEntry::getNameIndex(){
   return(nameIndex);
}


ReferenceConstantPoolEntry::ReferenceConstantPoolEntry(ByteBuffer *_byteBuffer, ConstantPoolType _constantPoolType)
   :  ConstantPoolEntry(_byteBuffer, _constantPoolType) {
      referenceClassIndex = _byteBuffer->u2();
      nameAndTypeIndex = _byteBuffer->u2();
   }
ReferenceConstantPoolEntry::~ReferenceConstantPoolEntry(){
}
u2_t ReferenceConstantPoolEntry::getReferenceClassIndex(){
   return(referenceClassIndex);
}
u2_t ReferenceConstantPoolEntry::getNameAndTypeIndex(){
   return(nameAndTypeIndex);
}

FieldConstantPoolEntry::FieldConstantPoolEntry(ByteBuffer *_byteBuffer)
   :  ReferenceConstantPoolEntry(_byteBuffer, FIELD) {
   }
FieldConstantPoolEntry::~FieldConstantPoolEntry(){
}

MethodReferenceConstantPoolEntry::MethodReferenceConstantPoolEntry(ByteBuffer *_byteBuffer, ConstantPoolType _constantPoolType)
   :  ReferenceConstantPoolEntry(_byteBuffer, _constantPoolType) {
   }

MethodReferenceConstantPoolEntry::~MethodReferenceConstantPoolEntry(){
}

MethodConstantPoolEntry::MethodConstantPoolEntry(ByteBuffer *_byteBuffer)
   :  MethodReferenceConstantPoolEntry(_byteBuffer, METHOD) {
   }
MethodConstantPoolEntry::~MethodConstantPoolEntry(){
}

InterfaceMethodConstantPoolEntry::InterfaceMethodConstantPoolEntry(ByteBuffer *_byteBuffer)
   :  MethodReferenceConstantPoolEntry(_byteBuffer, INTERFACEMETHOD) {
   }

InterfaceMethodConstantPoolEntry::~InterfaceMethodConstantPoolEntry(){
}
NameAndTypeConstantPoolEntry::NameAndTypeConstantPoolEntry(ByteBuffer *_byteBuffer)
   :  ConstantPoolEntry(_byteBuffer, NAMEANDTYPE) {
      descriptorIndex = _byteBuffer->u2();
      nameIndex = _byteBuffer->u2();
   }
NameAndTypeConstantPoolEntry::~NameAndTypeConstantPoolEntry(){
}
u2_t NameAndTypeConstantPoolEntry::getDescriptorIndex(){
   return(descriptorIndex);
}
u2_t NameAndTypeConstantPoolEntry::getNameIndex(){
   return(nameIndex);
}


MethodTypeConstantPoolEntry::MethodTypeConstantPoolEntry(ByteBuffer *_byteBuffer)
   :  ConstantPoolEntry(_byteBuffer, METHODTYPE) {
      descriptorIndex = _byteBuffer->u2();
   }
MethodTypeConstantPoolEntry::~MethodTypeConstantPoolEntry(){
}
u2_t MethodTypeConstantPoolEntry::getDescriptorIndex(){
   return(descriptorIndex);
}

MethodHandleConstantPoolEntry::MethodHandleConstantPoolEntry(ByteBuffer *_byteBuffer)
   :  ConstantPoolEntry(_byteBuffer, METHODHANDLE) {
      referenceKind = _byteBuffer->u1();
      referenceIndex = _byteBuffer->u2();
   }
MethodHandleConstantPoolEntry::~MethodHandleConstantPoolEntry(){
}
u1_t MethodHandleConstantPoolEntry::getReferenceKind(){
   return(referenceKind);
}
u2_t MethodHandleConstantPoolEntry::getReferenceIndex(){
   return(referenceIndex);
}

StringConstantPoolEntry::StringConstantPoolEntry(ByteBuffer *_byteBuffer)
   :  ConstantPoolEntry(_byteBuffer, STRING) {
      utf8Index = _byteBuffer->u2();
   }
StringConstantPoolEntry::~StringConstantPoolEntry(){
}
u2_t StringConstantPoolEntry::getUtf8Index(){
   return(utf8Index);
}

InvokeDynamicConstantPoolEntry::InvokeDynamicConstantPoolEntry(ByteBuffer *_byteBuffer)
   :  ConstantPoolEntry(_byteBuffer, INVOKEDYNAMIC) {
      bootstrapMethodAttrIndex = _byteBuffer->u2();
      nameAndTypeIndex = _byteBuffer->u2();
   }
InvokeDynamicConstantPoolEntry::~InvokeDynamicConstantPoolEntry(){
}
u2_t InvokeDynamicConstantPoolEntry::getBootStrapMethodAttrIndex(){
   return(bootstrapMethodAttrIndex);
}
u2_t InvokeDynamicConstantPoolEntry::getNameAndTypeIndex(){
   return(nameAndTypeIndex);
}

LineNumberTableAttribute::LineNumberTableEntry::LineNumberTableEntry(ByteBuffer *_byteBuffer, ConstantPoolEntry **_constantPool){
   start_pc = _byteBuffer->u2();
   line_number = _byteBuffer->u2();
}
LineNumberTableAttribute::LineNumberTableEntry::~LineNumberTableEntry(){
}

LineNumberTableAttribute::LineNumberTableAttribute(ByteBuffer *_byteBuffer, ConstantPoolEntry **_constantPool){
   line_number_table_length = _byteBuffer->u2();
   lineNumberTable = new LineNumberTableEntry *[line_number_table_length];
#ifdef SHOW
   fprintf(stdout, "%d line numbers", line_number_table_length);
#endif
   for (u2_t i =0; i< line_number_table_length; i++){
      lineNumberTable[i] = new LineNumberTableEntry(_byteBuffer, _constantPool);
   }
}
LineNumberTableAttribute::~LineNumberTableAttribute(){
   for (u2_t i =0; i< line_number_table_length; i++){
      delete lineNumberTable[i];
   }
   delete lineNumberTable;
}

LocalVariableTableAttribute::LocalVariableTableEntry::LocalVariableTableEntry(ByteBuffer *_byteBuffer, ConstantPoolEntry **_constantPool){
   start_pc = _byteBuffer->u2();
   length = _byteBuffer->u2();
   name_index = _byteBuffer->u2();
   descriptor_index = _byteBuffer->u2();
   index = _byteBuffer->u2();
}
LocalVariableTableAttribute::LocalVariableTableEntry::~LocalVariableTableEntry(){
}

LocalVariableTableAttribute::LocalVariableTableAttribute(ByteBuffer *_byteBuffer, ConstantPoolEntry **_constantPool){
   local_variable_table_length = _byteBuffer->u2();
   localVariableTable = new LocalVariableTableEntry *[local_variable_table_length];
#ifdef SHOW
   fprintf(stdout, "%d local variables", local_variable_table_length);
#endif
   for (u2_t i =0; i< local_variable_table_length; i++){
      localVariableTable[i] = new LocalVariableTableEntry(_byteBuffer, _constantPool);
   }
}
LocalVariableTableAttribute::~LocalVariableTableAttribute(){
   for (u2_t i =0; i< local_variable_table_length; i++){
      delete localVariableTable[i];
   }
   delete localVariableTable;
}

CodeAttribute::ExceptionTableEntry::ExceptionTableEntry(ByteBuffer *_byteBuffer, ConstantPoolEntry **_constantPool){
   start_pc = _byteBuffer->u2();
   end_pc = _byteBuffer->u2();
   handler_pc = _byteBuffer->u2();
   catch_type = _byteBuffer->u2();
}
CodeAttribute::ExceptionTableEntry::~ExceptionTableEntry(){
}

CodeAttribute::CodeAttribute(ByteBuffer *_byteBuffer, ConstantPoolEntry **_constantPool){
   max_stack = _byteBuffer->u2();
   max_locals = _byteBuffer->u2();
   u4_t code_length = _byteBuffer->u4();
   codeByteBuffer = _byteBuffer->getByteBuffer(code_length);
#ifdef SHOW
   fprintf(stdout, "MaxStack %d, MaxLocals %d, CodeLength %d", max_stack, max_locals, code_length);
#endif
   exception_table_length = _byteBuffer->u2();
   exceptionTable = new ExceptionTableEntry *[exception_table_length];
   for (u2_t i =0; i< exception_table_length; i++){
      exceptionTable[i] = new ExceptionTableEntry(_byteBuffer, _constantPool);
   }
   attributes_count = _byteBuffer->u2();
   attributes = new AttributeInfo *[attributes_count];
   for (u2_t i=0; i< attributes_count; i++){
      attributes[i] = new AttributeInfo(_byteBuffer, _constantPool);
   }
}
CodeAttribute::~CodeAttribute(){
   for (u2_t i =0; i< exception_table_length; i++){
      delete exceptionTable[i] ;
   }
   delete exceptionTable;
   for (u2_t i=0; i< attributes_count; i++){
      delete attributes[i];
   }
   delete attributes;
}

ByteBuffer *CodeAttribute::getCodeByteBuffer(){
   return(codeByteBuffer);
}


   AttributeInfo::AttributeInfo(ByteBuffer *_byteBuffer, ConstantPoolEntry **_constantPool)
: attribute_type(UNKNOWN), codeAttribute(NULL)
{
   attribute_name_index = _byteBuffer->u2();
   UTF8ConstantPoolEntry *attributeName = (UTF8ConstantPoolEntry*)_constantPool[getAttributeNameIndex()];
   char *attributeNameChars = attributeName->getUTF8();
#ifdef SHOW
   fprintf(stdout, " [ATTR=\"%s\"{", attributeNameChars);
#endif
   u4_t attribute_length = _byteBuffer->u4();
   infoByteBuffer = _byteBuffer->getByteBuffer(attribute_length);
   if (!strcmp(attributeNameChars, "Code")){
      codeAttribute = new CodeAttribute(infoByteBuffer, _constantPool);
      attribute_type  = Code;
   } else if (!strcmp(attributeNameChars, "LineNumberTable")){
      lineNumberTableAttribute = new LineNumberTableAttribute(infoByteBuffer, _constantPool);
      attribute_type  = LineNumberTable;
   } else if (!strcmp(attributeNameChars, "LocalVariableTable")){
      localVariableTableAttribute = new LocalVariableTableAttribute(infoByteBuffer, _constantPool);
      attribute_type  = LocalVariableTable;
   }
#ifdef SHOW
   fprintf(stdout, " }] ", attributeName->getUTF8());
#endif
}
AttributeInfo::~AttributeInfo(){
   delete infoByteBuffer;
   switch (attribute_type){
      case Code:
         delete codeAttribute;
         break;
      case LineNumberTable:
         delete lineNumberTableAttribute;
         break;
      case LocalVariableTable:
         delete localVariableTableAttribute;
         break;
   }

}
u2_t AttributeInfo::getAttributeNameIndex(){
   return(attribute_name_index);
}
AttributeType AttributeInfo::getAttributeType(){
   return(attribute_type);
}

CodeAttribute *AttributeInfo::getCodeAttribute(){
   return(codeAttribute);
}

LineNumberTableAttribute *AttributeInfo::getLineNumberTableAttribute(){
   return(lineNumberTableAttribute);
}

LocalVariableTableAttribute *AttributeInfo::getLocalVariableTableAttribute(){
   return(localVariableTableAttribute);
}


FieldInfo::FieldInfo(ByteBuffer *_byteBuffer, ConstantPoolEntry **_constantPool){
   access_flags = _byteBuffer->u2();
   name_index = _byteBuffer->u2();
   descriptor_index = _byteBuffer->u2();
   attributes_count = _byteBuffer->u2();
   attributes = new AttributeInfo *[attributes_count];
   for (u2_t i=0; i< attributes_count; i++){
      attributes[i] = new AttributeInfo(_byteBuffer, _constantPool);
   }
#ifdef SHOW
   UTF8ConstantPoolEntry *fieldName = (UTF8ConstantPoolEntry*)_constantPool[getNameIndex()];
   fprintf(stdout, " field \"%s\"", fieldName->getUTF8());
   UTF8ConstantPoolEntry *fieldDescriptor = (UTF8ConstantPoolEntry*)_constantPool[getDescriptorIndex()];
   fprintf(stdout, " \"%s\"\n", fieldDescriptor->getUTF8());
#endif
}
FieldInfo::~FieldInfo(){
   for (u2_t i=0; i< attributes_count; i++){
      delete attributes[i];
   }
   delete attributes;
}
u2_t FieldInfo::getNameIndex(){
   return(name_index);
}
u2_t FieldInfo::getDescriptorIndex(){
   return(descriptor_index);
}

MethodInfo::MethodInfo(ByteBuffer *_byteBuffer, ConstantPoolEntry **_constantPool){
   access_flags = _byteBuffer->u2();
   name_index = _byteBuffer->u2();
   descriptor_index = _byteBuffer->u2();
   attributes_count = _byteBuffer->u2();
   attributes = new AttributeInfo *[attributes_count];
   for (u2_t i=0; i< attributes_count; i++){
      attributes[i] = new AttributeInfo(_byteBuffer, _constantPool);
      switch(attributes[i]->getAttributeType()){
         case Code:
            codeAttribute = attributes[i]->getCodeAttribute();
            break;
         case LineNumberTable:
            lineNumberTableAttribute = attributes[i]->getLineNumberTableAttribute();
            break;
         case LocalVariableTable:
            localVariableTableAttribute = attributes[i]->getLocalVariableTableAttribute();
            break;
      }
   }
#ifdef SHOW
   UTF8ConstantPoolEntry *methodName = (UTF8ConstantPoolEntry*)_constantPool[getNameIndex()];
   fprintf(stdout, " method \"%s\"", methodName->getUTF8());
   UTF8ConstantPoolEntry *methodDescriptor = (UTF8ConstantPoolEntry*)_constantPool[getDescriptorIndex()];
   fprintf(stdout, " \"%s\"\n", methodDescriptor->getUTF8());
#endif
}
MethodInfo::~MethodInfo(){
   for (u2_t i=0; i< attributes_count; i++){
      delete attributes[i];
   }
   delete attributes;

}
u2_t MethodInfo::getNameIndex(){
   return(name_index);
}
u2_t MethodInfo::getDescriptorIndex(){
   return(descriptor_index);
}
CodeAttribute* MethodInfo::getCodeAttribute(){
   return(codeAttribute);
}
LineNumberTableAttribute* MethodInfo::getLineNumberTableAttribute(){
   return(lineNumberTableAttribute);
}
LocalVariableTableAttribute* MethodInfo::getLocalVariableTableAttribute(){
   return(localVariableTableAttribute);
}



   ClassInfo::ClassInfo(ByteBuffer *_byteBuffer)
: constantPool(NULL), interfaces(NULL), fields(NULL), methods(NULL), attributes(NULL)
{
   magic= _byteBuffer->u4();
   if (magic == 0xcafebabe){
      //fprintf(stdout, "magic = %04x\n", magic);
      minor= _byteBuffer->u2();
      major= _byteBuffer->u2();
      constantPoolSize = _byteBuffer->u2();
#ifdef SHOW
      fprintf(stdout, "constant pool size = %d\n", constantPoolSize);
#endif
      constantPool=new ConstantPoolEntry *[constantPoolSize];
      u4_t slot = 0;
      constantPool[slot++] = new EmptyConstantPoolEntry(_byteBuffer);

      while (slot < constantPoolSize){
         ConstantPoolType constantPoolType = (ConstantPoolType)_byteBuffer->u1();
         switch (constantPoolType){
            case UTF8: //1
               constantPool[slot++] = new UTF8ConstantPoolEntry(_byteBuffer);
#ifdef SHOW
               fprintf(stdout, "slot %d UTF8 \"%s\"\n", slot-1, ((UTF8ConstantPoolEntry*)constantPool[slot-1])->getUTF8());
#endif
               break;
            case INTEGER: //3
               constantPool[slot++] = new IntegerConstantPoolEntry(_byteBuffer);
#ifdef SHOW
               fprintf(stdout, "slot %d INTEGER %d\n", slot-1, ((IntegerConstantPoolEntry*)constantPool[slot-1])->getValue());
#endif
               break;
            case FLOAT: //4
               constantPool[slot++] = new FloatConstantPoolEntry(_byteBuffer);
#ifdef SHOW
               fprintf(stdout, "slot %d FLOAT %f\n", slot-1, ((FloatConstantPoolEntry*)constantPool[slot-1])->getValue());
#endif
               break;
            case LONG: //5
               constantPool[slot++] = new LongConstantPoolEntry(_byteBuffer);
#ifdef SHOW
               fprintf(stdout, "slot %d LONG %ld\n", slot-1, ((LongConstantPoolEntry*)constantPool[slot-1])->getValue());
#endif
               constantPool[slot++] = new EmptyConstantPoolEntry(_byteBuffer);
               break;
            case DOUBLE: //6
               constantPool[slot++] = new DoubleConstantPoolEntry(_byteBuffer);
#ifdef SHOW
               fprintf(stdout, "slot %d DOUBLE %lf\n", slot-1, ((DoubleConstantPoolEntry*)constantPool[slot-1])->getValue());
#endif
               constantPool[slot++] = new EmptyConstantPoolEntry(_byteBuffer);
               break;
            case CLASS: //7
               constantPool[slot++] = new ClassConstantPoolEntry(_byteBuffer);
#ifdef SHOW
               fprintf(stdout, "slot %d CLASS\n", slot-1);
#endif
               break;
            case STRING: //8
               constantPool[slot++] = new StringConstantPoolEntry(_byteBuffer);
#ifdef SHOW
               fprintf(stdout, "slot %d STRING\n", slot-1);
#endif
               break;
            case FIELD: //9
               constantPool[slot++] = new FieldConstantPoolEntry(_byteBuffer);
#ifdef SHOW
               fprintf(stdout, "slot %d FIELD\n", slot-1);
#endif
               break;
            case METHOD: //10
               constantPool[slot++] = new MethodConstantPoolEntry(_byteBuffer);
#ifdef SHOW
               fprintf(stdout, "slot %d METHOD\n", slot-1);
#endif
               break;
            case INTERFACEMETHOD: //11
               constantPool[slot++] = new InterfaceMethodConstantPoolEntry(_byteBuffer);
#ifdef SHOW
               fprintf(stdout, "slot %d INTERFACEMETHOD\n", slot-1);
#endif
               break;
            case NAMEANDTYPE: //12
               constantPool[slot++] = new NameAndTypeConstantPoolEntry(_byteBuffer);
#ifdef SHOW
               fprintf(stdout, "slot %d NAMEANDTYPE\n", slot-1);
#endif
               break;
            case METHODHANDLE: //15
               constantPool[slot++] = new MethodHandleConstantPoolEntry(_byteBuffer);
#ifdef SHOW
               fprintf(stdout, "slot %d METHODHANDLE\n", slot-1);
#endif
               break;
            case METHODTYPE: //16
               constantPool[slot++] = new MethodTypeConstantPoolEntry(_byteBuffer);
#ifdef SHOW
               fprintf(stdout, "slot %d METHODTYPE", slot-1);
#endif
               break;
            case INVOKEDYNAMIC: //18
               constantPool[slot++] = new InvokeDynamicConstantPoolEntry(_byteBuffer);
#ifdef SHOW
               SHOW fprintf(stdout, "slot %d INVOKEDYNAMIC\n", slot-1);
#endif
               break;
            default: 
               fprintf(stdout, "ERROR found UNKNOWN! %02x/%0d in slot %d\n", constantPoolType, constantPoolType, slot );
               exit (1);
         }
      }

      // we have the constant pool 

      accessFlags = _byteBuffer->u2();
#ifdef SHOW
      fprintf(stdout, "access flags %04x\n", accessFlags);
#endif
      thisClassConstantPoolIndex = _byteBuffer->u2();
#ifdef SHOW
      fprintf(stdout, "this class constant pool index = %04x\n", thisClassConstantPoolIndex);
      ClassConstantPoolEntry *thisClassConstantPoolEntry = (ClassConstantPoolEntry*)constantPool[thisClassConstantPoolIndex];
      fprintf(stdout, "this class name constant pool index = %04x\n", thisClassConstantPoolEntry->getNameIndex());
      UTF8ConstantPoolEntry *thisClassUTF8ConstantPoolEntry = (UTF8ConstantPoolEntry*)constantPool[thisClassConstantPoolEntry->getNameIndex()];
      fprintf(stdout, "UTF8 at this class name index is \"%s\"\n", thisClassUTF8ConstantPoolEntry->getUTF8());
#endif
      superClassConstantPoolIndex = _byteBuffer->u2();
      ClassConstantPoolEntry *superClassConstantPoolEntry = (ClassConstantPoolEntry*)constantPool[superClassConstantPoolIndex];
      UTF8ConstantPoolEntry *superClassUTF8ConstantPoolEntry = (UTF8ConstantPoolEntry*)constantPool[superClassConstantPoolEntry->getNameIndex()];

#ifdef SHOW
      fprintf(stdout, "Class name at super index is \"%s\"\n", superClassUTF8ConstantPoolEntry->getUTF8());
#endif
      interfaceCount = _byteBuffer->u2();
#ifdef SHOW
      fprintf(stdout, "This class implements %d interfaces\n", interfaceCount);
#endif
      interfaces  = new u2_t[interfaceCount];
      for (u2_t i=0; i< interfaceCount; i++){
         interfaces[i] = _byteBuffer->u2();
      }
      fieldCount = _byteBuffer->u2();
#ifdef SHOW
      fprintf(stdout, "This class has  %d fields\n", fieldCount);
#endif
      fields  = new FieldInfo*[fieldCount];
      for (u2_t i=0; i< fieldCount; i++){
         fields[i] = new FieldInfo(_byteBuffer, constantPool);
      }
      methodCount = _byteBuffer->u2();
#ifdef SHOW
      fprintf(stdout, "This class has  %d methods\n", methodCount);
#endif
      methods  = new MethodInfo*[methodCount];
      for (u2_t i=0; i< methodCount; i++){
         methods[i] = new MethodInfo(_byteBuffer, constantPool);
      }
      attributeCount = _byteBuffer->u2();
#ifdef SHOW
      fprintf(stdout, "This class has  %d attributes\n", attributeCount);
#endif
      attributes = new AttributeInfo *[attributeCount];
      for (u2_t i=0; i< attributeCount; i++){
         attributes[i] = new AttributeInfo(_byteBuffer, constantPool);
      }
#ifdef SHOW
      fprintf(stdout, "\n");
#endif
   }
}
ClassInfo::~ClassInfo(){
   //if(false){
   for (u4_t i = 0; i<constantPoolSize; i++){ // <= intentional!
      delete constantPool[i];
   }
   delete constantPool;
   //}
   if (interfaces){
      delete interfaces;
   }
   if (fields){
      for (u2_t i=0; i< fieldCount; i++){
         delete fields[i];
      }
      delete fields;
   }
   if (methods){
      for (u2_t i=0; i< methodCount; i++){
         delete methods[i];
      }
      delete methods;
   }
   if (attributes){
      for (u2_t i=0; i< attributeCount; i++){
         delete attributes[i];
      }
      delete attributes;
   }
}
// com/amd/aparapi/Main$Kernel.run()V == "run", "()V"
MethodInfo *ClassInfo::getMethodInfo(char *_methodName, char *_methodDescriptor){
   MethodInfo *returnMethodInfo = NULL;
   for (u2_t i=0; returnMethodInfo == NULL && i< methodCount; i++){
      MethodInfo* methodInfo = methods[i];
      char * name=(char*)((UTF8ConstantPoolEntry*)constantPool[methodInfo->getNameIndex()])->getUTF8();
      char * descriptor=(char*)((UTF8ConstantPoolEntry*)constantPool[methodInfo->getDescriptorIndex()])->getUTF8();
      if (!strcmp(_methodName, name) && !strcmp(_methodDescriptor, descriptor)){
         returnMethodInfo = methodInfo;
      }
      fprintf(stdout, "found %s%s\n", name, descriptor);
   }
   return(returnMethodInfo);
}

char *ClassInfo::getClassName(){
   //fprintf(stdout, "this class constant pool index = %04x\n", thisClassConstantPoolIndex);
   ClassConstantPoolEntry *thisClassConstantPoolEntry = (ClassConstantPoolEntry*)constantPool[thisClassConstantPoolIndex];
   //fprintf(stdout, "this class name constant pool index = %04x\n", thisClassConstantPoolEntry->getNameIndex());
   UTF8ConstantPoolEntry *thisClassUTF8ConstantPoolEntry = (UTF8ConstantPoolEntry*)constantPool[thisClassConstantPoolEntry->getNameIndex()];
   //fprintf(stdout, "UTF8 at this class name index is \"%s\"\n", thisClassUTF8ConstantPoolEntry->getUTF8());
   return((char*)thisClassUTF8ConstantPoolEntry->getUTF8());
}

char *ClassInfo::getSuperClassName(){
   //fprintf(stdout, "super class constant pool index = %04x\n", superClassConstantPoolIndex);
   ClassConstantPoolEntry *superClassConstantPoolEntry = (ClassConstantPoolEntry*)constantPool[superClassConstantPoolIndex];
   //fprintf(stdout, "super class name constant pool index = %04x\n", superClassConstantPoolEntry->getNameIndex());
   UTF8ConstantPoolEntry *superClassUTF8ConstantPoolEntry = (UTF8ConstantPoolEntry*)constantPool[superClassConstantPoolEntry->getNameIndex()];
   //fprintf(stdout, "UTF8 at super class name index is \"%s\"\n", superClassUTF8ConstantPoolEntry->getUTF8());
   return((char*)superClassUTF8ConstantPoolEntry->getUTF8());
}

ConstantPoolEntry **ClassInfo::getConstantPool(){
   return(constantPool);
}




