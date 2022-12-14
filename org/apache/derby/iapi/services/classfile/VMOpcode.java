// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.services.classfile;

public interface VMOpcode
{
    public static final short BAD = -999;
    public static final short NOP = 0;
    public static final short ACONST_NULL = 1;
    public static final short ICONST_M1 = 2;
    public static final short ICONST_0 = 3;
    public static final short ICONST_1 = 4;
    public static final short ICONST_2 = 5;
    public static final short ICONST_3 = 6;
    public static final short ICONST_4 = 7;
    public static final short ICONST_5 = 8;
    public static final short LCONST_0 = 9;
    public static final short LCONST_1 = 10;
    public static final short FCONST_0 = 11;
    public static final short FCONST_1 = 12;
    public static final short FCONST_2 = 13;
    public static final short DCONST_0 = 14;
    public static final short DCONST_1 = 15;
    public static final short BIPUSH = 16;
    public static final short SIPUSH = 17;
    public static final short LDC = 18;
    public static final short LDC_W = 19;
    public static final short LDC2_W = 20;
    public static final short ILOAD = 21;
    public static final short LLOAD = 22;
    public static final short FLOAD = 23;
    public static final short DLOAD = 24;
    public static final short ALOAD = 25;
    public static final short ILOAD_0 = 26;
    public static final short ILOAD_1 = 27;
    public static final short ILOAD_2 = 28;
    public static final short ILOAD_3 = 29;
    public static final short LLOAD_0 = 30;
    public static final short LLOAD_1 = 31;
    public static final short LLOAD_2 = 32;
    public static final short LLOAD_3 = 33;
    public static final short FLOAD_0 = 34;
    public static final short FLOAD_1 = 35;
    public static final short FLOAD_2 = 36;
    public static final short FLOAD_3 = 37;
    public static final short DLOAD_0 = 38;
    public static final short DLOAD_1 = 39;
    public static final short DLOAD_2 = 40;
    public static final short DLOAD_3 = 41;
    public static final short ALOAD_0 = 42;
    public static final short ALOAD_1 = 43;
    public static final short ALOAD_2 = 44;
    public static final short ALOAD_3 = 45;
    public static final short IALOAD = 46;
    public static final short LALOAD = 47;
    public static final short FALOAD = 48;
    public static final short DALOAD = 49;
    public static final short AALOAD = 50;
    public static final short BALOAD = 51;
    public static final short CALOAD = 52;
    public static final short SALOAD = 53;
    public static final short ISTORE = 54;
    public static final short LSTORE = 55;
    public static final short FSTORE = 56;
    public static final short DSTORE = 57;
    public static final short ASTORE = 58;
    public static final short ISTORE_0 = 59;
    public static final short ISTORE_1 = 60;
    public static final short ISTORE_2 = 61;
    public static final short ISTORE_3 = 62;
    public static final short LSTORE_0 = 63;
    public static final short LSTORE_1 = 64;
    public static final short LSTORE_2 = 65;
    public static final short LSTORE_3 = 66;
    public static final short FSTORE_0 = 67;
    public static final short FSTORE_1 = 68;
    public static final short FSTORE_2 = 69;
    public static final short FSTORE_3 = 70;
    public static final short DSTORE_0 = 71;
    public static final short DSTORE_1 = 72;
    public static final short DSTORE_2 = 73;
    public static final short DSTORE_3 = 74;
    public static final short ASTORE_0 = 75;
    public static final short ASTORE_1 = 76;
    public static final short ASTORE_2 = 77;
    public static final short ASTORE_3 = 78;
    public static final short IASTORE = 79;
    public static final short LASTORE = 80;
    public static final short FASTORE = 81;
    public static final short DASTORE = 82;
    public static final short AASTORE = 83;
    public static final short BASTORE = 84;
    public static final short CASTORE = 85;
    public static final short SASTORE = 86;
    public static final short POP = 87;
    public static final short POP2 = 88;
    public static final short DUP = 89;
    public static final short DUP_X1 = 90;
    public static final short DUP_X2 = 91;
    public static final short DUP2 = 92;
    public static final short DUP2_X1 = 93;
    public static final short DUP2_X2 = 94;
    public static final short SWAP = 95;
    public static final short IADD = 96;
    public static final short LADD = 97;
    public static final short FADD = 98;
    public static final short DADD = 99;
    public static final short ISUB = 100;
    public static final short LSUB = 101;
    public static final short FSUB = 102;
    public static final short DSUB = 103;
    public static final short IMUL = 104;
    public static final short LMUL = 105;
    public static final short FMUL = 106;
    public static final short DMUL = 107;
    public static final short IDIV = 108;
    public static final short LDIV = 109;
    public static final short FDIV = 110;
    public static final short DDIV = 111;
    public static final short IREM = 112;
    public static final short LREM = 113;
    public static final short FREM = 114;
    public static final short DREM = 115;
    public static final short INEG = 116;
    public static final short LNEG = 117;
    public static final short FNEG = 118;
    public static final short DNEG = 119;
    public static final short ISHL = 120;
    public static final short LSHL = 121;
    public static final short ISHR = 122;
    public static final short LSHR = 123;
    public static final short IUSHR = 124;
    public static final short LUSHR = 125;
    public static final short IAND = 126;
    public static final short LAND = 127;
    public static final short IOR = 128;
    public static final short LOR = 129;
    public static final short IXOR = 130;
    public static final short LXOR = 131;
    public static final short IINC = 132;
    public static final short I2L = 133;
    public static final short I2F = 134;
    public static final short I2D = 135;
    public static final short L2I = 136;
    public static final short L2F = 137;
    public static final short L2D = 138;
    public static final short F2I = 139;
    public static final short F2L = 140;
    public static final short F2D = 141;
    public static final short D2I = 142;
    public static final short D2L = 143;
    public static final short D2F = 144;
    public static final short I2B = 145;
    public static final short I2C = 146;
    public static final short I2S = 147;
    public static final short LCMP = 148;
    public static final short FCMPL = 149;
    public static final short FCMPG = 150;
    public static final short DCMPL = 151;
    public static final short DCMPG = 152;
    public static final short IFEQ = 153;
    public static final short IFNE = 154;
    public static final short IFLT = 155;
    public static final short IFGE = 156;
    public static final short IFGT = 157;
    public static final short IFLE = 158;
    public static final short IF_ICMPEQ = 159;
    public static final short IF_ICMPNE = 160;
    public static final short IF_ICMPLT = 161;
    public static final short IF_ICMPGE = 162;
    public static final short IF_ICMPGT = 163;
    public static final short IF_ICMPLE = 164;
    public static final short IF_ACMPEQ = 165;
    public static final short IF_ACMPNE = 166;
    public static final short GOTO = 167;
    public static final short JSR = 168;
    public static final short RET = 169;
    public static final short TABLESWITCH = 170;
    public static final short LOOKUPSWITCH = 171;
    public static final short IRETURN = 172;
    public static final short LRETURN = 173;
    public static final short FRETURN = 174;
    public static final short DRETURN = 175;
    public static final short ARETURN = 176;
    public static final short RETURN = 177;
    public static final short GETSTATIC = 178;
    public static final short PUTSTATIC = 179;
    public static final short GETFIELD = 180;
    public static final short PUTFIELD = 181;
    public static final short INVOKEVIRTUAL = 182;
    public static final short INVOKESPECIAL = 183;
    public static final short INVOKESTATIC = 184;
    public static final short INVOKEINTERFACE = 185;
    public static final short XXXUNUSEDXXX = 186;
    public static final short NEW = 187;
    public static final short NEWARRAY = 188;
    public static final short ANEWARRAY = 189;
    public static final short ARRAYLENGTH = 190;
    public static final short ATHROW = 191;
    public static final short CHECKCAST = 192;
    public static final short INSTANCEOF = 193;
    public static final short MONITORENTER = 194;
    public static final short MONITOREXIT = 195;
    public static final short WIDE = 196;
    public static final short MULTIANEWARRAY = 197;
    public static final short IFNULL = 198;
    public static final short IFNONNULL = 199;
    public static final short GOTO_W = 200;
    public static final short JSR_W = 201;
    public static final short BREAKPOINT = 202;
    public static final int MAX_CODE_LENGTH = 65535;
    public static final int IF_INS_LENGTH = 3;
    public static final int GOTO_INS_LENGTH = 3;
    public static final int GOTO_W_INS_LENGTH = 5;
    public static final int MAX_CONSTANT_POOL_ENTRIES = 65535;
}
