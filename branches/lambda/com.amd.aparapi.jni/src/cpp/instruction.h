#ifndef INSTRUCTION_H
#define INSTRUCTION_H

#include "classtools.h"

enum ByteCodeType{
   I_NOP,
   I_ACONST_NULL,
   I_ICONST_M1,
   I_ICONST_0,
   I_ICONST_1,
   I_ICONST_2,
   I_ICONST_3,
   I_ICONST_4,
   I_ICONST_5,
   I_LCONST_0,
   I_LCONST_1,
   I_FCONST_0,
   I_FCONST_1,
   I_FCONST_2,
   I_DCONST_0,
   I_DCONST_1,
   I_BIPUSH,
   I_SIPUSH,
   I_LDC,
   I_LDC_W,
   I_LDC2_W,
   I_ILOAD,
   I_LLOAD,
   I_FLOAD,
   I_DLOAD,
   I_ALOAD,
   I_ILOAD_0,
   I_ILOAD_1,
   I_ILOAD_2,
   I_ILOAD_3,
   I_LLOAD_0,
   I_LLOAD_1,
   I_LLOAD_2,
   I_LLOAD_3,
   I_FLOAD_0,
   I_FLOAD_1,
   I_FLOAD_2,
   I_FLOAD_3,
   I_DLOAD_0,
   I_DLOAD_1,
   I_DLOAD_2,
   I_DLOAD_3,
   I_ALOAD_0,
   I_ALOAD_1,
   I_ALOAD_2,
   I_ALOAD_3,
   I_IALOAD,
   I_LALOAD,
   I_FALOAD,
   I_DALOAD,
   I_AALOAD,
   I_BALOAD,
   I_CALOAD,
   I_SALOAD,
   I_ISTORE,
   I_LSTORE,
   I_FSTORE,
   I_DSTORE,
   I_ASTORE,
   I_ISTORE_0,
   I_ISTORE_1,
   I_ISTORE_2,
   I_ISTORE_3,
   I_LSTORE_0,
   I_LSTORE_1,
   I_LSTORE_2,
   I_LSTORE_3,
   I_FSTORE_0,
   I_FSTORE_1,
   I_FSTORE_2,
   I_FSTORE_3,
   I_DSTORE_0,
   I_DSTORE_1,
   I_DSTORE_2,
   I_DSTORE_3,
   I_ASTORE_0,
   I_ASTORE_1,
   I_ASTORE_2,
   I_ASTORE_3,
   I_IASTORE,
   I_LASTORE,
   I_FASTORE,
   I_DASTORE,
   I_AASTORE,
   I_BASTORE,
   I_CASTORE,
   I_SASTORE,
   I_POP,
   I_POP2,
   I_DUP,
   I_DUP_X1,
   I_DUP_X2,
   I_DUP2,
   I_DUP2_X1,
   I_DUP2_X2,
   I_SWAP,
   I_IADD,
   I_LADD,
   I_FADD,
   I_DADD,
   I_ISUB,
   I_LSUB,
   I_FSUB,
   I_DSUB,
   I_IMUL,
   I_LMUL,
   I_FMUL,
   I_DMUL,
   I_IDIV,
   I_LDIV,
   I_FDIV,
   I_DDIV,
   I_IREM,
   I_LREM,
   I_FREM,
   I_DREM,
   I_INEG,
   I_LNEG,
   I_FNEG,
   I_DNEG,
   I_ISHL,
   I_LSHL,
   I_ISHR,
   I_LSHR,
   I_IUSHR,
   I_LUSHR,
   I_IAND,
   I_LAND,
   I_IOR,
   I_LOR,
   I_IXOR,
   I_LXOR,
   I_IINC,
   I_I2L,
   I_I2F,
   I_I2D,
   I_L2I,
   I_L2F,
   I_L2D,
   I_F2I,
   I_F2L,
   I_F2D,
   I_D2I,
   I_D2L,
   I_D2F,
   I_I2B,
   I_I2C,
   I_I2S,
   I_LCMP,
   I_FCMPL,
   I_FCMPG,
   I_DCMPL,
   I_DCMPG,
   I_IFEQ,
   I_IFNE,
   I_IFLT,
   I_IFGE,
   I_IFGT,
   I_IFLE,
   I_IF_ICMPEQ,
   I_IF_ICMPNE,
   I_IF_ICMPLT,
   I_IF_ICMPGE,
   I_IF_ICMPGT,
   I_IF_ICMPLE,
   I_IF_ACMPEQ,
   I_IF_ACMPNE,
   I_GOTO,
   I_JSR,
   I_RET,
   I_TABLESWITCH,
   I_LOOKUPSWITCH,
   I_IRETURN,
   I_LRETURN,
   I_FRETURN,
   I_DRETURN,
   I_ARETURN,
   I_RETURN,
   I_GETSTATIC,
   I_PUTSTATIC,
   I_GETFIELD,
   I_PUTFIELD,
   I_INVOKEVIRTUAL,
   I_INVOKESPECIAL,
   I_INVOKESTATIC,
   I_INVOKEINTERFACE,
   I_INVOKEDYNAMIC,
   I_NEW,
   I_NEWARRAY,
   I_ANEWARRAY,
   I_ARRAYLENGTH,
   I_ATHROW,
   I_CHECKCAST,
   I_INSTANCEOF,
   I_MONITORENTER,
   I_MONITOREXIT,
   I_WIDE,
   I_MULTIANEWARRAY,
   I_IFNULL,
   I_IFNONNULL,
   I_GOTO_W,
   I_JSR_W
};


#define BITS_B  0x1
#define BITS_C  0x2
#define BITS_S  0x3
#define BITS_I  0x4
#define BITS_F  0x5
#define BITS_L  0x6
#define BITS_D  0x7
#define BITS_O  0x8
#define BITS_A  0x9
#define BITS_UN 0xA

#define LDSpec_NONE                    0x0
#define LDSpec_I                       BITS_I
#define LDSpec_L                       BITS_L
#define LDSpec_A                       BITS_A
#define LDSpec_F                       BITS_F
#define LDSpec_D                       BITS_D

#define ImmSpec_NONE                   0x00
#define ImmSpec_Blvti                  0x01
#define ImmSpec_Bcpci                  0x02
#define ImmSpec_Scpci                  0x03
#define ImmSpec_Bconst                 0x04
#define ImmSpec_Sconst                 0x05
#define ImmSpec_IorForS                0x06
#define ImmSpec_Spc                    0x07
#define ImmSpec_Scpfi                  0x08
#define ImmSpec_ScpmiBB                0x09
#define ImmSpec_BlvtiBconst            0x0A
#define ImmSpec_Scpmi                  0x0B
#define ImmSpec_ScpciBdim              0x0C
#define ImmSpec_Ipc                    0x0D
#define ImmSpec_UNKNOWN                0x0E

#define STSpec_NONE                    0x00
#define STSpec_L                       BITS_L
#define STSpec_F                       BITS_F
#define STSpec_D                       BITS_D
#define STSpec_I                       BITS_I
#define STSpec_A                       BITS_A

#define PushSpec_NONE                  0x000
#define PushSpec_N                     0x011
#define PushSpec_I                     0x021
#define PushSpec_L                     0x031
#define PushSpec_F                     0x041
#define PushSpec_D                     0x051
#define PushSpec_O                     0x061
#define PushSpec_A                     0x071
#define PushSpec_RA                    0x082
#define PushSpec_IorForS               0x091
#define PushSpec_LorD                  0x0A1
#define PushSpec_II                    0x0B2
#define PushSpec_III                   0x0C3
#define PushSpec_IIII                  0x0D4
#define PushSpec_IIIII                 0x0E5
#define PushSpec_IIIIII                0x0F6
#define PushSpec_UNKNOWN               0x100

#define PopSpec_NONE                   0x000
#define PopSpec_COUNT_MASK             0x007
#define PopSpec_A                      0x011
#define PopSpec_AI                     0x022
#define PopSpec_AII                    0x033
#define PopSpec_AIL                    0x043
#define PopSpec_AIF                    0x053
#define PopSpec_AID                    0x063
#define PopSpec_AIO                    0x073
#define PopSpec_AIB                    0x083
#define PopSpec_AIC                    0x093
#define PopSpec_AIS                    0x0A3
#define PopSpec_II                     0x0B2
#define PopSpec_III                    0x0C3
#define PopSpec_IIII                   0x0D4
#define PopSpec_L                      0x0E1
#define PopSpec_LI                     0x0F2
#define PopSpec_LL                     0x102
#define PopSpec_F                      0x111
#define PopSpec_FF                     0x122
#define PopSpec_OO                     0x132
#define PopSpec_RA                     0x142
#define PopSpec_O                      0x151
#define PopSpec_I                      0x161
#define PopSpec_D                      0x171
#define PopSpec_DD                     0x182
#define PopSpec_OUNKNOWN               0x192
#define PopSpec_UNKNOWN                0x1A1
#define PopSpec_ARGS                   0x1B0
#define PopSpec_OARGS                  0x1C0


#define OpSpec_NONE                    0x000
#define OpSpec_CAST                    0x0040
#define OpSpec_ARITHMETIC              0x0080
#define OpSpec_BINARY                  0x0100
#define OpSpec_UNARY                   0x0200
#define OpSpec_BITWISE                 0x0400
#define OpSpec_COMPARE                 0x0800
#define OpSpec_Add                     0x0001 | OpSpec_ARITHMETIC  | OpSpec_BINARY
#define OpSpec_Sub                     0x0002 | OpSpec_ARITHMETIC | OpSpec_BINARY
#define OpSpec_Mul                     0x0003 | OpSpec_ARITHMETIC | OpSpec_BINARY
#define OpSpec_Div                     0x0004 | OpSpec_ARITHMETIC | OpSpec_BINARY
#define OpSpec_Rem                     0x0005 | OpSpec_ARITHMETIC | OpSpec_BINARY
#define OpSpec_Neg                     0x0006 | OpSpec_ARITHMETIC | OpSpec_UNARY
#define OpSpec_BitwiseOr               0x0007 | OpSpec_BITWISE | OpSpec_BINARY
#define OpSpec_BitwiseAnd              0x0008 | OpSpec_BITWISE | OpSpec_BINARY
#define OpSpec_BitwiseXor              0x0009 | OpSpec_BITWISE | OpSpec_BINARY
#define OpSpec_LeftShift               0x000A | OpSpec_BITWISE | OpSpec_BINARY
#define OpSpec_LogicalRightShift       0x000B | OpSpec_BITWISE | OpSpec_BINARY
#define OpSpec_ArithmeticRightShift    0x000C | OpSpec_BITWISE | OpSpec_BINARY
#define OpSpec_F2ICast                 0x000D | OpSpec_CAST 
#define OpSpec_F2LCast                 0x000E | OpSpec_CAST
#define OpSpec_F2DCast                 0x000F | OpSpec_CAST
#define OpSpec_D2ICast                 0x0010 | OpSpec_CAST
#define OpSpec_D2FCast                 0x0011 | OpSpec_CAST
#define OpSpec_D2LCast                 0x0012 | OpSpec_CAST
#define OpSpec_I2SCast                 0x0013 | OpSpec_CAST
#define OpSpec_I2BCast                 0x0014 | OpSpec_CAST
#define OpSpec_I2CCast                 0x0015 | OpSpec_CAST
#define OpSpec_I2LCast                 0x0016 | OpSpec_CAST
#define OpSpec_I2FCast                 0x0017 | OpSpec_CAST
#define OpSpec_I2DCast                 0x0018 | OpSpec_CAST
#define OpSpec_L2ICast                 0x0019 | OpSpec_CAST
#define OpSpec_L2FCast                 0x001A | OpSpec_CAST
#define OpSpec_L2DCast                 0x001B | OpSpec_CAST
#define OpSpec_GreaterThan             0x001C | OpSpec_COMPARE | OpSpec_BINARY
#define OpSpec_GreaterThanOrEqual      0x001E | OpSpec_COMPARE | OpSpec_BINARY
#define OpSpec_LessThan                0x001F | OpSpec_COMPARE | OpSpec_BINARY
#define OpSpec_LessThanOrEqual         0x0020 | OpSpec_COMPARE | OpSpec_BINARY
#define OpSpec_Equal                   0x0021 | OpSpec_COMPARE | OpSpec_BINARY
#define OpSpec_EqualNULL               0x0022 | OpSpec_COMPARE | OpSpec_UNARY
#define OpSpec_NotEqual                0x0023 | OpSpec_COMPARE | OpSpec_BINARY
#define OpSpec_NotEqualNULL            0x0024 | OpSpec_COMPARE | OpSpec_UNARY

class ByteCode{
   public:
      ByteCodeType bytecode;
      const char *name;
      u2_t ldSpec;
      u2_t stSpec;
      u2_t immSpec;
      u4_t popSpec;
      u4_t pushSpec;
      u4_t opSpec;
};

#ifdef INSTRUCTION_CPP
ByteCode bytecode[] ={
   {I_NOP, "NOP", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_NONE, PushSpec_NONE, OpSpec_NONE}, 
   {I_ACONST_NULL,"I_ACONST_NULL", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_NONE, PushSpec_N, OpSpec_NONE},
   {I_ICONST_M1,"I_ICONST_M1", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_NONE, PushSpec_I, OpSpec_NONE},
   {I_ICONST_0,"I_ICONST_0", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_NONE, PushSpec_I, OpSpec_NONE},
   {I_ICONST_1,"I_ICONST_1", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_NONE, PushSpec_I, OpSpec_NONE},
   {I_ICONST_2,"I_ICONST_2", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_NONE, PushSpec_I, OpSpec_NONE},
   {I_ICONST_3,"I_ICONST_3", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_NONE, PushSpec_I, OpSpec_NONE},
   {I_ICONST_4,"I_ICONST_4", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_NONE, PushSpec_I, OpSpec_NONE},
   {I_ICONST_5,"I_ICONST_5", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_NONE, PushSpec_I, OpSpec_NONE},
   {I_LCONST_0,"I_LCONST_0", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_NONE, PushSpec_L, OpSpec_NONE},
   {I_LCONST_1,"I_LCONST_1", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_NONE, PushSpec_L, OpSpec_NONE},
   {I_FCONST_0,"I_FCONST_0", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_NONE, PushSpec_F, OpSpec_NONE},
   {I_FCONST_1,"I_FCONST_1", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_NONE, PushSpec_F, OpSpec_NONE},
   {I_FCONST_2,"I_FCONST_2", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_NONE, PushSpec_F, OpSpec_NONE},
   {I_DCONST_0,"I_DCONST_0", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_NONE, PushSpec_D, OpSpec_NONE},
   {I_DCONST_1,"I_DCONST_1", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_NONE, PushSpec_D, OpSpec_NONE},
   {I_BIPUSH,"I_BIPUSH", LDSpec_NONE, STSpec_NONE, ImmSpec_Bconst, PopSpec_NONE, PushSpec_I, OpSpec_NONE},
   {I_SIPUSH,"I_SIPUSH", LDSpec_NONE, STSpec_NONE, ImmSpec_Sconst, PopSpec_NONE, PushSpec_I, OpSpec_NONE},
   {I_LDC,"I_LDC", LDSpec_NONE, STSpec_NONE, ImmSpec_Bcpci, PopSpec_NONE, PushSpec_IorForS, OpSpec_NONE},
   {I_LDC_W,"I_LDC_W", LDSpec_NONE, STSpec_NONE, ImmSpec_Scpci, PopSpec_NONE, PushSpec_IorForS, OpSpec_NONE},
   {I_LDC2_W,"I_LDC2_W", LDSpec_NONE, STSpec_NONE, ImmSpec_Scpci, PopSpec_NONE, PushSpec_LorD, OpSpec_NONE},
   {I_ILOAD,"I_ILOAD", LDSpec_I, ImmSpec_Blvti, PopSpec_NONE, PushSpec_I, OpSpec_NONE},
   {I_LLOAD,"I_LLOAD", LDSpec_L, ImmSpec_Blvti, PopSpec_NONE, PushSpec_L, OpSpec_NONE},
   {I_FLOAD,"I_FLOAD", LDSpec_F, ImmSpec_Blvti, PopSpec_NONE, PushSpec_F, OpSpec_NONE},
   {I_DLOAD,"I_DLOAD", LDSpec_F, ImmSpec_Blvti, PopSpec_NONE, PushSpec_D, OpSpec_NONE},
   {I_ALOAD,"I_ALOAD", LDSpec_A, ImmSpec_Blvti, PopSpec_NONE, PushSpec_O, OpSpec_NONE},
   {I_ILOAD_0,"I_ILOAD_0", LDSpec_I, STSpec_NONE, ImmSpec_NONE, PopSpec_NONE, PushSpec_I, OpSpec_NONE},
   {I_ILOAD_1,"I_ILOAD_1", LDSpec_I, STSpec_NONE, ImmSpec_NONE, PopSpec_NONE, PushSpec_I, OpSpec_NONE},
   {I_ILOAD_2,"I_ILOAD_2", LDSpec_I, STSpec_NONE, ImmSpec_NONE, PopSpec_NONE, PushSpec_I, OpSpec_NONE},
   {I_ILOAD_3,"I_ILOAD_3", LDSpec_I, STSpec_NONE, ImmSpec_NONE, PopSpec_NONE, PushSpec_I, OpSpec_NONE},
   {I_LLOAD_0,"I_LLOAD_0", LDSpec_L, STSpec_NONE, ImmSpec_NONE, PopSpec_NONE, PushSpec_L, OpSpec_NONE},
   {I_LLOAD_1,"I_LLOAD_1", LDSpec_L, STSpec_NONE, ImmSpec_NONE, PopSpec_NONE, PushSpec_L, OpSpec_NONE},
   {I_LLOAD_2,"I_LLOAD_2", LDSpec_L, STSpec_NONE, ImmSpec_NONE, PopSpec_NONE, PushSpec_L, OpSpec_NONE},
   {I_LLOAD_3,"I_LLOAD_3", LDSpec_L, STSpec_NONE, ImmSpec_NONE, PopSpec_NONE, PushSpec_L, OpSpec_NONE},
   {I_FLOAD_0,"I_FLOAD_0", LDSpec_F, STSpec_NONE, ImmSpec_NONE, PopSpec_NONE, PushSpec_F, OpSpec_NONE},
   {I_FLOAD_1,"I_FLOAD_1", LDSpec_F, STSpec_NONE, ImmSpec_NONE, PopSpec_NONE, PushSpec_F, OpSpec_NONE},
   {I_FLOAD_2,"I_FLOAD_2", LDSpec_F, STSpec_NONE, ImmSpec_NONE, PopSpec_NONE, PushSpec_F, OpSpec_NONE},
   {I_FLOAD_3,"I_FLOAD_3", LDSpec_F, STSpec_NONE, ImmSpec_NONE, PopSpec_NONE, PushSpec_F, OpSpec_NONE},
   {I_DLOAD_0,"I_DLOAD_0", LDSpec_D, STSpec_NONE, ImmSpec_NONE, PopSpec_NONE, PushSpec_D, OpSpec_NONE},
   {I_DLOAD_1,"I_DLOAD_1", LDSpec_D, STSpec_NONE, ImmSpec_NONE, PopSpec_NONE, PushSpec_D, OpSpec_NONE},
   {I_DLOAD_2,"I_DLOAD_2", LDSpec_D, STSpec_NONE, ImmSpec_NONE, PopSpec_NONE, PushSpec_D, OpSpec_NONE},
   {I_DLOAD_3,"I_DLOAD_3", LDSpec_D, STSpec_NONE, ImmSpec_NONE, PopSpec_NONE, PushSpec_D, OpSpec_NONE},
   {I_ALOAD_0,"I_ALOAD_0", LDSpec_A, STSpec_NONE, ImmSpec_NONE, PopSpec_NONE, PushSpec_O, OpSpec_NONE},
   {I_ALOAD_1,"I_ALOAD_1", LDSpec_A, STSpec_NONE, ImmSpec_NONE, PopSpec_NONE, PushSpec_O, OpSpec_NONE},
   {I_ALOAD_2,"I_ALOAD_2", LDSpec_A, STSpec_NONE, ImmSpec_NONE, PopSpec_NONE, PushSpec_O, OpSpec_NONE},
   {I_ALOAD_3,"I_ALOAD_3", LDSpec_A, STSpec_NONE, ImmSpec_NONE, PopSpec_NONE, PushSpec_O, OpSpec_NONE},
   {I_IALOAD,"I_IALOAD", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_AI, PushSpec_I, OpSpec_NONE},
   {I_LALOAD,"I_LALOAD", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_AI, PushSpec_L, OpSpec_NONE},
   {I_FALOAD,"I_FALOAD", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_AI, PushSpec_F, OpSpec_NONE},
   {I_DALOAD,"I_DALOAD", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_AI, PushSpec_D, OpSpec_NONE},
   {I_AALOAD,"I_AALOAD", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_AI, PushSpec_A, OpSpec_NONE},
   {I_BALOAD,"I_BALOAD", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_AI, PushSpec_I, OpSpec_NONE},
   {I_CALOAD,"I_CALOAD", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_AI, PushSpec_I, OpSpec_NONE},
   {I_SALOAD,"I_SALOAD", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_AI, PushSpec_I, OpSpec_NONE},
   {I_ISTORE,"I_ISTORE", LDSpec_NONE, STSpec_I, ImmSpec_Blvti, PopSpec_I, PushSpec_NONE, OpSpec_NONE},
   {I_LSTORE,"I_LSTORE", LDSpec_NONE, STSpec_L, ImmSpec_Blvti, PopSpec_L, PushSpec_NONE, OpSpec_NONE},
   {I_FSTORE,"I_FSTORE", LDSpec_NONE, STSpec_F, ImmSpec_Blvti, PopSpec_F, PushSpec_NONE, OpSpec_NONE},
   {I_DSTORE,"I_DSTORE", LDSpec_NONE, STSpec_D, ImmSpec_Blvti, PopSpec_D, PushSpec_NONE, OpSpec_NONE},
   {I_ASTORE,"I_ASTORE", LDSpec_NONE, STSpec_A, ImmSpec_Blvti, PopSpec_O, PushSpec_NONE, OpSpec_NONE},
   {I_ISTORE_0,"I_ISTORE_0", LDSpec_NONE, STSpec_I, ImmSpec_NONE, PopSpec_I, PushSpec_NONE, OpSpec_NONE},
   {I_ISTORE_1,"I_ISTORE_1", LDSpec_NONE, STSpec_I, ImmSpec_NONE, PopSpec_I, PushSpec_NONE, OpSpec_NONE},
   {I_ISTORE_2,"I_ISTORE_2", LDSpec_NONE, STSpec_I, ImmSpec_NONE, PopSpec_I, PushSpec_NONE, OpSpec_NONE},
   {I_ISTORE_3,"I_ISTORE_3", LDSpec_NONE, STSpec_I, ImmSpec_NONE, PopSpec_I, PushSpec_NONE, OpSpec_NONE},
   {I_LSTORE_0,"I_LSTORE_0", LDSpec_NONE, STSpec_L, ImmSpec_NONE, PopSpec_L, PushSpec_NONE, OpSpec_NONE},
   {I_LSTORE_1,"I_LSTORE_1", LDSpec_NONE, STSpec_L, ImmSpec_NONE, PopSpec_L, PushSpec_NONE, OpSpec_NONE},
   {I_LSTORE_2,"I_LSTORE_2", LDSpec_NONE, STSpec_L, ImmSpec_NONE, PopSpec_L, PushSpec_NONE, OpSpec_NONE},
   {I_LSTORE_3,"I_LSTORE_3", LDSpec_NONE, STSpec_L, ImmSpec_NONE, PopSpec_L, PushSpec_NONE, OpSpec_NONE},
   {I_FSTORE_0,"I_FSTORE_0", LDSpec_NONE, STSpec_F, ImmSpec_NONE, PopSpec_F, PushSpec_NONE, OpSpec_NONE},
   {I_FSTORE_1,"I_FSTORE_1", LDSpec_NONE, STSpec_F, ImmSpec_NONE, PopSpec_F, PushSpec_NONE, OpSpec_NONE},
   {I_FSTORE_2,"I_FSTORE_2", LDSpec_NONE, STSpec_F, ImmSpec_NONE, PopSpec_F, PushSpec_NONE, OpSpec_NONE},
   {I_FSTORE_3,"I_FSTORE_3", LDSpec_NONE, STSpec_F, ImmSpec_NONE, PopSpec_F, PushSpec_NONE, OpSpec_NONE},
   {I_DSTORE_0,"I_DSTORE_0", LDSpec_NONE, STSpec_D, ImmSpec_NONE, PopSpec_D, PushSpec_NONE, OpSpec_NONE},
   {I_DSTORE_1,"I_DSTORE_1", LDSpec_NONE, STSpec_D, ImmSpec_NONE, PopSpec_D, PushSpec_NONE, OpSpec_NONE},
   {I_DSTORE_2,"I_DSTORE_2", LDSpec_NONE, STSpec_D, ImmSpec_NONE, PopSpec_D, PushSpec_NONE, OpSpec_NONE},
   {I_DSTORE_3,"I_DSTORE_3", LDSpec_NONE, STSpec_D, ImmSpec_NONE, PopSpec_D, PushSpec_NONE, OpSpec_NONE},
   {I_ASTORE_0,"I_ASTORE_0", LDSpec_NONE, STSpec_A, ImmSpec_NONE, PopSpec_O, PushSpec_NONE, OpSpec_NONE},
   {I_ASTORE_1,"I_ASTORE_1", LDSpec_NONE, STSpec_A, ImmSpec_NONE, PopSpec_O, PushSpec_NONE, OpSpec_NONE},
   {I_ASTORE_2,"I_ASTORE_2", LDSpec_NONE, STSpec_A, ImmSpec_NONE, PopSpec_O, PushSpec_NONE, OpSpec_NONE},
   {I_ASTORE_3,"I_ASTORE_3", LDSpec_NONE, STSpec_A, ImmSpec_NONE, PopSpec_O, PushSpec_NONE, OpSpec_NONE},
   {I_IASTORE,"I_IASTORE", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_AII, PushSpec_NONE, OpSpec_NONE},
   {I_LASTORE,"I_LASTORE", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_AIL, PushSpec_NONE, OpSpec_NONE},
   {I_FASTORE,"I_FASTORE", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_AIF, PushSpec_NONE, OpSpec_NONE},
   {I_DASTORE,"I_DASTORE", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_AID, PushSpec_NONE, OpSpec_NONE},
   {I_AASTORE,"I_AASTORE", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_AIO, PushSpec_NONE, OpSpec_NONE},
   {I_BASTORE,"I_BASTORE", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_AIB, PushSpec_NONE, OpSpec_NONE},
   {I_CASTORE,"I_CASTORE", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_AIC, PushSpec_NONE, OpSpec_NONE},
   {I_SASTORE,"I_SASTORE", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_AIS, PushSpec_NONE, OpSpec_NONE},
   {I_POP,"I_POP", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_I, PushSpec_NONE, OpSpec_NONE},
   {I_POP2,"I_POP2", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_II, PushSpec_NONE, OpSpec_NONE},
   {I_DUP,"I_DUP", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_I, PushSpec_II, OpSpec_NONE},
   {I_DUP_X1,"I_DUP_X1", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_II, PushSpec_III, OpSpec_NONE},
   {I_DUP_X2,"I_DUP_X2", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_III, PushSpec_IIII, OpSpec_NONE},
   {I_DUP2,"I_DUP2", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_II, PushSpec_IIII, OpSpec_NONE},
   {I_DUP2_X1,"I_DUP2_X1", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_III, PushSpec_IIIII, OpSpec_NONE},
   {I_DUP2_X2,"I_DUP2_X2", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_IIII, PushSpec_IIIIII, OpSpec_NONE},
   {I_SWAP,"I_SWAP", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_II, PushSpec_II, OpSpec_NONE},
   {I_IADD,"I_IADD", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_II, PushSpec_I, OpSpec_Add},
   {I_LADD,"I_LADD", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_LL, PushSpec_L, OpSpec_Add},
   {I_FADD,"I_FADD", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_FF, PushSpec_F, OpSpec_Add},
   {I_DADD,"I_DADD", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_DD, PushSpec_D, OpSpec_Add},
   {I_ISUB,"I_ISUB", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_II, PushSpec_I, OpSpec_Sub},
   {I_LSUB,"I_LSUB", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_LL, PushSpec_L, OpSpec_Sub},
   {I_FSUB,"I_FSUB", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_FF, PushSpec_F, OpSpec_Sub},
   {I_DSUB,"I_DSUB", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_DD, PushSpec_D, OpSpec_Sub},
   {I_IMUL,"I_IMUL", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_II, PushSpec_I, OpSpec_Mul},
   {I_LMUL,"I_LMUL", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_LL, PushSpec_L, OpSpec_Mul},
   {I_FMUL,"I_FMUL", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_FF, PushSpec_F, OpSpec_Mul},
   {I_DMUL,"I_DMUL", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_DD, PushSpec_D, OpSpec_Mul},
   {I_IDIV,"I_IDIV", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_II, PushSpec_I, OpSpec_Div},
   {I_LDIV,"I_LDIV", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_LL, PushSpec_L, OpSpec_Div},
   {I_FDIV,"I_FDIV", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_FF, PushSpec_F, OpSpec_Div},
   {I_DDIV,"I_DDIV", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_DD, PushSpec_D, OpSpec_Div},
   {I_IREM,"I_IREM", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_II, PushSpec_I, OpSpec_Rem},
   {I_LREM,"I_LREM", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_LL, PushSpec_L, OpSpec_Rem},
   {I_FREM,"I_FREM", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_FF, PushSpec_F, OpSpec_Rem},
   {I_DREM,"I_DREM", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_DD, PushSpec_D, OpSpec_Rem},
   {I_INEG,"I_INEG", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_I, PushSpec_I, OpSpec_Neg},
   {I_LNEG,"I_LNEG", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_L, PushSpec_L, OpSpec_Neg},
   {I_FNEG,"I_FNEG", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_F, PushSpec_F, OpSpec_Neg},
   {I_DNEG,"I_DNEG", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_D, PushSpec_D, OpSpec_Neg},
   {I_ISHL,"I_ISHL", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_II, PushSpec_I, OpSpec_LeftShift},
   {I_LSHL,"I_LSHL", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_LI, PushSpec_L, OpSpec_LeftShift},
   {I_ISHR,"I_ISHR", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_II, PushSpec_I, OpSpec_LogicalRightShift},
   {I_LSHR,"I_LSHR", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_LI, PushSpec_L, OpSpec_LogicalRightShift},
   {I_IUSHR,"I_IUSHR", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_II, PushSpec_I, OpSpec_ArithmeticRightShift},
   {I_LUSHR,"I_LUSHR", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_LI, PushSpec_L, OpSpec_ArithmeticRightShift},
   {I_IAND,"I_IAND", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_II, PushSpec_I, OpSpec_BitwiseAnd},
   {I_LAND,"I_LAND", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_LL, PushSpec_L, OpSpec_BitwiseAnd},
   {I_IOR,"I_IOR", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_II, PushSpec_I, OpSpec_BitwiseOr},
   {I_LOR,"I_LOR", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_LL, PushSpec_L, OpSpec_BitwiseOr},
   {I_IXOR,"I_IXOR", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_II, PushSpec_I, OpSpec_BitwiseXor},
   {I_LXOR,"I_LXOR", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_LL, PushSpec_L, OpSpec_BitwiseXor},
   {I_IINC,"I_IINC", LDSpec_NONE, STSpec_NONE, ImmSpec_BlvtiBconst},
   {I_I2L,"I_I2L", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_I, PushSpec_L, OpSpec_I2LCast},
   {I_I2F,"I_I2F", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_I, PushSpec_F, OpSpec_I2FCast},
   {I_I2D,"I_I2D", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_I, PushSpec_D, OpSpec_I2DCast},
   {I_L2I,"I_L2I", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_L, PushSpec_I, OpSpec_L2ICast},
   {I_L2F,"I_L2F", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_L, PushSpec_F, OpSpec_L2FCast},
   {I_L2D,"I_L2D", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_L, PushSpec_D, OpSpec_L2DCast},
   {I_F2I,"I_F2I", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_F, PushSpec_I, OpSpec_F2ICast},
   {I_F2L,"I_F2L", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_F, PushSpec_L, OpSpec_F2LCast},
   {I_F2D,"I_F2D", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_F, PushSpec_D, OpSpec_F2DCast},
   {I_D2I,"I_D2I", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_D, PushSpec_I, OpSpec_D2ICast},
   {I_D2L,"I_D2L", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_D, PushSpec_L, OpSpec_D2LCast},
   {I_D2F,"I_D2F", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_D, PushSpec_F, OpSpec_D2FCast},
   {I_I2B,"I_I2B", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_I, PushSpec_I, OpSpec_I2BCast},
   {I_I2C,"I_I2C", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_I, PushSpec_I, OpSpec_I2CCast},
   {I_I2S,"I_I2S", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_I, PushSpec_I, OpSpec_I2SCast},
   {I_LCMP,"I_LCMP", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_LL, PushSpec_I, OpSpec_Sub},
   {I_FCMPL,"I_FCMPL", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_FF, PushSpec_I, OpSpec_LessThan},
   {I_FCMPG,"I_FCMPG", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_FF, PushSpec_I, OpSpec_GreaterThan},
   {I_DCMPL,"I_DCMPL", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_DD, PushSpec_I, OpSpec_LessThan},
   {I_DCMPG,"I_DCMPG", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_DD, PushSpec_I, OpSpec_GreaterThan},
   {I_IFEQ,"I_IFEQ", LDSpec_NONE, STSpec_NONE, ImmSpec_Spc, PopSpec_I, OpSpec_Equal},
   {I_IFNE,"I_IFNE", LDSpec_NONE, STSpec_NONE, ImmSpec_Spc, PopSpec_I, OpSpec_NotEqual},
   {I_IFLT,"I_IFLT", LDSpec_NONE, STSpec_NONE, ImmSpec_Spc, PopSpec_I, OpSpec_LessThan},
   {I_IFGE,"I_IFGE", LDSpec_NONE, STSpec_NONE, ImmSpec_Spc, PopSpec_I, OpSpec_GreaterThanOrEqual},
   {I_IFGT,"I_IFGT", LDSpec_NONE, STSpec_NONE, ImmSpec_Spc, PopSpec_I, OpSpec_GreaterThan},
   {I_IFLE,"I_IFLE", LDSpec_NONE, STSpec_NONE, ImmSpec_Spc, PopSpec_I, OpSpec_LessThanOrEqual},
   {I_IF_ICMPEQ,"I_IF_ICMPEQ", LDSpec_NONE, STSpec_NONE, ImmSpec_Sconst, PopSpec_II, OpSpec_Equal},
   {I_IF_ICMPNE,"I_IF_ICMPNE", LDSpec_NONE, STSpec_NONE, ImmSpec_Spc, PopSpec_II, OpSpec_NotEqual},
   {I_IF_ICMPLT,"I_IF_ICMPLT", LDSpec_NONE, STSpec_NONE, ImmSpec_Spc, PopSpec_II, OpSpec_LessThan},
   {I_IF_ICMPGE,"I_IF_ICMPGE", LDSpec_NONE, STSpec_NONE, ImmSpec_Spc, PopSpec_II, OpSpec_GreaterThanOrEqual},
   {I_IF_ICMPGT,"I_IF_ICMPGT", LDSpec_NONE, STSpec_NONE, ImmSpec_Spc, PopSpec_II, OpSpec_GreaterThan},
   {I_IF_ICMPLE,"I_IF_ICMPLE", LDSpec_NONE, STSpec_NONE, ImmSpec_Spc, PopSpec_II, OpSpec_LessThanOrEqual},
   {I_IF_ACMPEQ,"I_IF_ACMPEQ", LDSpec_NONE, STSpec_NONE, ImmSpec_Spc, PopSpec_OO, OpSpec_Equal},
   {I_IF_ACMPNE,"I_IF_ACMPNE", LDSpec_NONE, STSpec_NONE, ImmSpec_Spc, PopSpec_OO, OpSpec_NotEqual},
   {I_GOTO,"I_GOTO", LDSpec_NONE, STSpec_NONE, ImmSpec_Spc},
   {I_JSR,"I_JSR", LDSpec_NONE, STSpec_NONE, ImmSpec_Spc, PopSpec_NONE, PushSpec_RA, OpSpec_NONE},
   {I_RET,"I_RET", LDSpec_NONE, STSpec_NONE, ImmSpec_Bconst},
   {I_TABLESWITCH,"I_TABLESWITCH", LDSpec_NONE, STSpec_NONE, ImmSpec_UNKNOWN, PopSpec_I, PushSpec_NONE, OpSpec_NONE},
   {I_LOOKUPSWITCH,"I_LOOKUPSWITCH", LDSpec_NONE, STSpec_NONE, ImmSpec_UNKNOWN, PopSpec_I, PushSpec_NONE, OpSpec_NONE},
   {I_IRETURN,"I_IRETURN", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_I, PushSpec_NONE, OpSpec_NONE},
   {I_LRETURN,"I_LRETURN", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_L, PushSpec_NONE, OpSpec_NONE},
   {I_FRETURN,"I_FRETURN", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_F, PushSpec_NONE, OpSpec_NONE},
   {I_DRETURN,"I_DRETURN", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_D, PushSpec_NONE, OpSpec_NONE},
   {I_ARETURN,"I_ARETURN", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_O, PushSpec_NONE, OpSpec_NONE},
   {I_RETURN,"I_RETURN", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_NONE, PushSpec_NONE, OpSpec_NONE},
   {I_GETSTATIC,"I_GETSTATIC", LDSpec_NONE, STSpec_NONE, ImmSpec_Scpfi, PopSpec_NONE, PushSpec_UNKNOWN, OpSpec_NONE},
   {I_PUTSTATIC,"I_PUTSTATIC", LDSpec_NONE, STSpec_NONE, ImmSpec_Scpfi, PopSpec_UNKNOWN, PushSpec_NONE, OpSpec_NONE},
   {I_GETFIELD,"I_GETFIELD", LDSpec_NONE, STSpec_NONE, ImmSpec_Scpfi, PopSpec_O, PushSpec_UNKNOWN, OpSpec_NONE},
   {I_PUTFIELD,"I_PUTFIELD", LDSpec_NONE, STSpec_NONE, ImmSpec_Scpfi, PopSpec_OUNKNOWN, PushSpec_NONE, OpSpec_NONE},
   {I_INVOKEVIRTUAL,"I_INVOKEVIRTUAL", LDSpec_NONE, STSpec_NONE, ImmSpec_Scpmi, PopSpec_OARGS, PushSpec_UNKNOWN, OpSpec_NONE},
   {I_INVOKESPECIAL,"I_INVOKESPECIAL", LDSpec_NONE, STSpec_NONE, ImmSpec_Scpmi, PopSpec_OARGS, PushSpec_UNKNOWN, OpSpec_NONE},
   {I_INVOKESTATIC,"I_INVOKESTATIC", LDSpec_NONE, STSpec_NONE, ImmSpec_Scpmi, PopSpec_ARGS, PushSpec_UNKNOWN, OpSpec_NONE},
   {I_INVOKEINTERFACE,"I_INVOKEINTERFACE", LDSpec_NONE, STSpec_NONE, ImmSpec_ScpmiBB, PopSpec_OARGS, PushSpec_UNKNOWN, OpSpec_NONE},
   {I_INVOKEDYNAMIC,"I_INVOKEDYNAMIC", LDSpec_NONE, STSpec_NONE, ImmSpec_ScpmiBB, PopSpec_OARGS, PushSpec_UNKNOWN, OpSpec_NONE},
   {I_NEW,"I_NEW", LDSpec_NONE, STSpec_NONE, ImmSpec_Scpci, PopSpec_NONE, PushSpec_O, OpSpec_NONE},
   {I_NEWARRAY,"I_NEWARRAY", LDSpec_NONE, STSpec_NONE, ImmSpec_Bconst, PopSpec_I, PushSpec_A, OpSpec_NONE},
   {I_ANEWARRAY,"I_ANEWARRAY", LDSpec_NONE, STSpec_NONE, ImmSpec_Sconst, PopSpec_I, PushSpec_A, OpSpec_NONE},
   {I_ARRAYLENGTH,"I_ARRAYLENGTH", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_A, PushSpec_I, OpSpec_NONE},
   {I_ATHROW,"I_ATHROW", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_O, PushSpec_O, OpSpec_NONE},
   {I_CHECKCAST,"I_CHECKCAST", LDSpec_NONE, STSpec_NONE, ImmSpec_Scpci, PopSpec_O, PushSpec_O, OpSpec_NONE},
   {I_INSTANCEOF,"I_INSTANCEOF", LDSpec_NONE, STSpec_NONE, ImmSpec_Scpci, PopSpec_O, PushSpec_I, OpSpec_NONE},
   {I_MONITORENTER,"I_MONITORENTER", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_O, PushSpec_NONE, OpSpec_NONE},
   {I_MONITOREXIT,"I_MONITOREXIT", LDSpec_NONE, STSpec_NONE, ImmSpec_NONE, PopSpec_O, PushSpec_NONE, OpSpec_NONE},
   {I_WIDE,"I_WIDE", LDSpec_NONE, STSpec_NONE, ImmSpec_UNKNOWN, PopSpec_UNKNOWN, PushSpec_UNKNOWN, OpSpec_NONE},
   {I_MULTIANEWARRAY,"I_MULTIANEWARRAY", LDSpec_NONE, STSpec_NONE, ImmSpec_ScpciBdim, PopSpec_UNKNOWN, PushSpec_A, OpSpec_NONE},
   {I_IFNULL,"I_IFNULL", LDSpec_NONE, STSpec_NONE, ImmSpec_Spc, PopSpec_O, OpSpec_EqualNULL},
   {I_IFNONNULL,"I_IFNONNULL", LDSpec_NONE, STSpec_NONE, ImmSpec_Spc, PopSpec_O, OpSpec_NotEqualNULL},
   {I_GOTO_W,"I_GOTO_W", LDSpec_NONE, STSpec_NONE, ImmSpec_Ipc},
   {I_JSR_W,"I_JSR_W", LDSpec_NONE, STSpec_NONE, ImmSpec_Ipc, PopSpec_NONE, PushSpec_RA, OpSpec_NONE},
};
#else
extern ByteCode bytecode[];
#endif


class Decoder{
   static void list(u1_t *buf, u4_t len);
};

class ClassInfo{
   private:
      u4_t magic;
      u2_t minor;
      u2_t major;
      u2_t constantPoolSize;
      ConstantPoolEntry **constantPool;
      u2_t accessFlags;
      u2_t thisClassConstantPoolIndex;
      u2_t superClassConstantPoolIndex;
      u2_t interfaceCount;
      u2_t *interfaces;
      u2_t fieldCount;
      FieldInfo **fields;
      u2_t methodCount;
      MethodInfo **methods;
      u2_t attributeCount;
      AttributeInfo **attributes;
   public:
      ClassInfo(ByteBuffer *_byteBuffer);
      // com/amd/aparapi/Main$Kernel.run()V ==  "run", "()V"
      MethodInfo *getMethodInfo(char *_methodName, char *_methodDescriptor); // com/amd/aparapi/Main$Kernel.run()V
};

#endif



