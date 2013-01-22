#define INSTRUCTION_CPP
#include "instruction.h"

#include <string.h>

void Decoder::list(u1_t* buf, u4_t len){
   fprintf(stdout, "inside list\n");
}

ClassInfo::ClassInfo(ByteBuffer *_byteBuffer){
   magic= _byteBuffer->u4();
   if (magic == 0xcafebabe){
      //fprintf(stdout, "magic = %04x\n", magic);
      minor= _byteBuffer->u2();
      major= _byteBuffer->u2();
      constantPoolSize = _byteBuffer->u2();
#ifdef SHOW
      fprintf(stdout, "constant pool size = %d\n", constantPoolSize);
#endif
      constantPool=new ConstantPoolEntry *[constantPoolSize+1];
      u4_t slot = 0;
      constantPool[slot] = new EmptyConstantPoolEntry(_byteBuffer, slot);
      slot=1;

      while (slot < constantPoolSize){
         ConstantPoolType constantPoolType = (ConstantPoolType)_byteBuffer->u1();
         switch (constantPoolType){
            case UTF8: //1
                  constantPool[slot] = new UTF8ConstantPoolEntry(_byteBuffer, slot);
#ifdef SHOW
                  fprintf(stdout, "slot %d UTF8 \"%s\"\n", slot, ((UTF8ConstantPoolEntry*)constantPool[slot])->getUTF8Bytes());
#endif
                  slot++;
               break;
            case INTEGER: //3
                  constantPool[slot] = new IntegerConstantPoolEntry(_byteBuffer, slot);
#ifdef SHOW
                  fprintf(stdout, "slot %d INTEGER\n", slot);
#endif
                  slot++;
               break;
            case FLOAT: //4
                  constantPool[slot] = new FloatConstantPoolEntry(_byteBuffer, slot);
#ifdef SHOW
                  fprintf(stdout, "slot %d FLOAT\n", slot);
#endif
                  slot++;
               break;
            case LONG: //5
                  constantPool[slot] = new LongConstantPoolEntry(_byteBuffer, slot);
#ifdef SHOW
                  fprintf(stdout, "slot %d LONG\n", slot);
#endif
                  slot+=2;
               break;
            case DOUBLE: //6
                  constantPool[slot] = new DoubleConstantPoolEntry(_byteBuffer, slot);
#ifdef SHOW
                  fprintf(stdout, "slot %d DOUBLE\n", slot);
#endif
                  slot+=2;
               break;
            case CLASS: //7
                  constantPool[slot] = new ClassConstantPoolEntry(_byteBuffer, slot);
#ifdef SHOW
                  fprintf(stdout, "slot %d CLASS\n", slot);
#endif
                  slot+=1;
               break;
            case STRING: //8
                  constantPool[slot] = new StringConstantPoolEntry(_byteBuffer, slot);
#ifdef SHOW
                  fprintf(stdout, "slot %d STRING\n", slot);
#endif
                  slot+=1;
               break;
            case FIELD: //9
                  constantPool[slot] = new FieldConstantPoolEntry(_byteBuffer, slot);
#ifdef SHOW
                  fprintf(stdout, "slot %d FIELD\n", slot);
#endif
                  slot+=1;
               break;
            case METHOD: //10
                  constantPool[slot] = new MethodConstantPoolEntry(_byteBuffer, slot);
#ifdef SHOW
                  fprintf(stdout, "slot %d METHOD\n", slot);
#endif
                  slot+=1;
               break;
            case INTERFACEMETHOD: //11
                  constantPool[slot] = new InterfaceMethodConstantPoolEntry(_byteBuffer, slot);
#ifdef SHOW
                  fprintf(stdout, "slot %d INTERFACEMETHOD\n", slot);
#endif
                  slot+=1;
               break;
            case NAMEANDTYPE: //12
                  constantPool[slot] = new NameAndTypeConstantPoolEntry(_byteBuffer, slot);
#ifdef SHOW
                  fprintf(stdout, "slot %d NAMEANDTYPE\n", slot);
#endif
                  slot+=1;
               break;
            case METHODHANDLE: //15
                  constantPool[slot] = new MethodHandleConstantPoolEntry(_byteBuffer, slot);
#ifdef SHOW
                  fprintf(stdout, "slot %d METHODHANDLE\n", slot);
#endif
                  slot+=1;
               break;
            case METHODTYPE: //16
                  constantPool[slot] = new MethodTypeConstantPoolEntry(_byteBuffer, slot);
#ifdef SHOW
                  fprintf(stdout, "slot %d METHODTYPE", slot);
#endif
                  slot+=1;
               break;
            case INVOKEDYNAMIC: //18
                  constantPool[slot] = new InvokeDynamicConstantPoolEntry(_byteBuffer, slot);
#ifdef SHOW
                  SHOW fprintf(stdout, "slot %d INVOKEDYNAMIC\n", slot);
#endif
                  slot+=1;
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
      fprintf(stdout, "UTF8 at this class name index is \"%s\"\n", thisClassUTF8ConstantPoolEntry->getUTF8Bytes());
#endif
      superClassConstantPoolIndex = _byteBuffer->u2();
      ClassConstantPoolEntry *superClassConstantPoolEntry = (ClassConstantPoolEntry*)constantPool[superClassConstantPoolIndex];
      UTF8ConstantPoolEntry *superClassUTF8ConstantPoolEntry = (UTF8ConstantPoolEntry*)constantPool[superClassConstantPoolEntry->getNameIndex()];

#ifdef SHOW
      fprintf(stdout, "Class name at super index is \"%s\"\n", superClassUTF8ConstantPoolEntry->getUTF8Bytes());
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
// com/amd/aparapi/Main$Kernel.run()V == "run", "()V"
MethodInfo *ClassInfo::getMethodInfo(char *_methodName, char *_methodDescriptor){
   MethodInfo *returnMethodInfo = NULL;
   for (u2_t i=0; returnMethodInfo == NULL && i< methodCount; i++){
      MethodInfo* methodInfo = methods[i];
      char * name=(char*)((UTF8ConstantPoolEntry*)constantPool[methodInfo->getNameIndex()])->getUTF8Bytes();
      char * descriptor=(char*)((UTF8ConstantPoolEntry*)constantPool[methodInfo->getDescriptorIndex()])->getUTF8Bytes();
      if (!strcmp(_methodName, name) && !strcmp(_methodDescriptor, descriptor)){
         returnMethodInfo = methodInfo;
      }
      fprintf(stdout, "found %s%s\n", name, descriptor);
   }
   return(returnMethodInfo);
}




