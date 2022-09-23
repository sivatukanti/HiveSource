// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.services.bytecode;

import org.apache.derby.iapi.services.classfile.CONSTANT_Index_info;
import org.apache.derby.iapi.services.classfile.CONSTANT_Utf8_info;
import java.util.Arrays;
import org.apache.derby.iapi.services.classfile.ClassMember;
import org.apache.derby.iapi.services.classfile.ClassHolder;
import java.io.OutputStream;
import org.apache.derby.iapi.services.io.ArrayOutputStream;
import java.io.IOException;
import org.apache.derby.iapi.services.classfile.ClassFormatOutput;

final class CodeChunk
{
    private static final int CODE_OFFSET = 8;
    static final short[] LOAD_VARIABLE;
    static final short[] LOAD_VARIABLE_FAST;
    static final short[] STORE_VARIABLE;
    static final short[] STORE_VARIABLE_FAST;
    static final short[] ARRAY_ACCESS;
    static final short[] ARRAY_STORE;
    static final short[] RETURN_OPCODE;
    static final short[][][] CAST_CONVERSION_INFO;
    private static final byte[] push1_1i;
    private static final byte[] push2_1i;
    private static final byte[] NS;
    private static final byte VARIABLE_STACK = Byte.MIN_VALUE;
    private static final byte[][] OPCODE_ACTION;
    private final int pcDelta;
    final BCClass cb;
    private final ClassFormatOutput cout;
    
    private void limitHit(final IOException ex) {
        this.cb.addLimitExceeded(ex.toString());
    }
    
    void addInstr(final short n) {
        try {
            this.cout.putU1(n);
        }
        catch (IOException ex) {
            this.limitHit(ex);
        }
    }
    
    void addInstrU2(final short n, final int n2) {
        try {
            this.cout.putU1(n);
            this.cout.putU2(n2);
        }
        catch (IOException ex) {
            this.limitHit(ex);
        }
    }
    
    void addInstrU4(final short n, final int n2) {
        try {
            this.cout.putU1(n);
            this.cout.putU4(n2);
        }
        catch (IOException ex) {
            this.limitHit(ex);
        }
    }
    
    void addInstrU1(final short n, final int n2) {
        try {
            this.cout.putU1(n);
            this.cout.putU1(n2);
        }
        catch (IOException ex) {
            this.limitHit(ex);
        }
    }
    
    void addInstrCPE(final short n, final int n2) {
        if (n2 < 256) {
            this.addInstrU1(n, n2);
        }
        else {
            this.addInstrU2((short)(n + 1), n2);
        }
    }
    
    void addInstrWide(final short n, final int n2) {
        if (n2 < 256) {
            this.addInstrU1(n, n2);
        }
        else {
            this.addInstr((short)196);
            this.addInstrU2(n, n2);
        }
    }
    
    void addInstrU2U1U1(final short n, final int n2, final short n3, final short n4) {
        try {
            this.cout.putU1(n);
            this.cout.putU2(n2);
            this.cout.putU1(n3);
            this.cout.putU1(n4);
        }
        catch (IOException ex) {
            this.limitHit(ex);
        }
    }
    
    int getPC() {
        return this.cout.size() + this.pcDelta;
    }
    
    private static int instructionLength(final short n) {
        return CodeChunk.OPCODE_ACTION[n][1];
    }
    
    CodeChunk(final BCClass cb) {
        this.cb = cb;
        this.cout = new ClassFormatOutput();
        try {
            this.cout.putU2(0);
            this.cout.putU2(0);
            this.cout.putU4(0);
        }
        catch (IOException ex) {
            this.limitHit(ex);
        }
        this.pcDelta = -8;
    }
    
    private CodeChunk(final CodeChunk codeChunk, final int pcDelta, final int limit) {
        this.cb = codeChunk.cb;
        final ArrayOutputStream arrayOutputStream = new ArrayOutputStream(codeChunk.cout.getData());
        try {
            arrayOutputStream.setPosition(8 + pcDelta);
            arrayOutputStream.setLimit(limit);
        }
        catch (IOException ex) {
            this.limitHit(ex);
        }
        this.cout = new ClassFormatOutput(arrayOutputStream);
        this.pcDelta = pcDelta;
    }
    
    private void fixLengths(final BCMethod bcMethod, final int n, final int n2, final int n3) {
        final byte[] data = this.cout.getData();
        if (bcMethod != null && n > 65535) {
            this.cb.addLimitExceeded(bcMethod, "max_stack", 65535, n);
        }
        data[0] = (byte)(n >> 8);
        data[1] = (byte)n;
        if (bcMethod != null && n2 > 65535) {
            this.cb.addLimitExceeded(bcMethod, "max_locals", 65535, n2);
        }
        data[2] = (byte)(n2 >> 8);
        data[3] = (byte)n2;
        if (bcMethod != null && n3 > 65535) {
            this.cb.addLimitExceeded(bcMethod, "code_length", 65535, n3);
        }
        data[4] = (byte)(n3 >> 24);
        data[5] = (byte)(n3 >> 16);
        data[6] = (byte)(n3 >> 8);
        data[7] = (byte)n3;
    }
    
    void complete(final BCMethod bcMethod, final ClassHolder classHolder, final ClassMember classMember, final int n, final int n2) {
        final int pc = this.getPC();
        final ClassFormatOutput cout = this.cout;
        try {
            cout.putU2(0);
            cout.putU2(0);
        }
        catch (IOException ex) {
            this.limitHit(ex);
        }
        this.fixLengths(bcMethod, n, n2, pc);
        classMember.addAttribute("Code", cout);
    }
    
    short getOpcode(final int n) {
        return (short)(this.cout.getData()[8 + n] & 0xFF);
    }
    
    private int getU2(final int n) {
        final byte[] data = this.cout.getData();
        final int n2 = 8 + n + 1;
        return (data[n2] & 0xFF) << 8 | (data[n2 + 1] & 0xFF);
    }
    
    private int getU4(final int n) {
        final byte[] data = this.cout.getData();
        final int n2 = 8 + n + 1;
        return (data[n2] & 0xFF) << 24 | (data[n2 + 1] & 0xFF) << 16 | (data[n2 + 2] & 0xFF) << 8 | (data[n2 + 3] & 0xFF);
    }
    
    CodeChunk insertCodeSpace(final int n, int n2) {
        final int instructionLength = instructionLength(this.getOpcode(n));
        if (n2 > 0) {
            final int n3 = this.getPC() - n - instructionLength;
            for (int i = 0; i < n2; ++i) {
                this.addInstr((short)0);
            }
            final byte[] data = this.cout.getData();
            final int fromIndex = 8 + n + instructionLength;
            System.arraycopy(data, fromIndex, data, fromIndex + n2, n3);
            Arrays.fill(data, fromIndex, fromIndex + n2, (byte)0);
        }
        n2 += instructionLength;
        return new CodeChunk(this, n, n2);
    }
    
    private int findMaxStack(final ClassHolder classHolder, int i, final int n) {
        final int n2 = i + n;
        int n3 = 0;
        int n4 = 0;
        while (i < n2) {
            final short opcode = this.getOpcode(i);
            n3 += this.stackWordDelta(classHolder, i, opcode);
            if (n3 > n4) {
                n4 = n3;
            }
            final int[] conditionalPCs = this.findConditionalPCs(i, opcode);
            if (conditionalPCs != null && conditionalPCs[3] != -1) {
                final int maxStack = this.findMaxStack(classHolder, conditionalPCs[1], conditionalPCs[2]);
                if (n3 + maxStack > n4) {
                    n4 = n3 + maxStack;
                }
                i = conditionalPCs[3];
            }
            else {
                i += instructionLength(opcode);
            }
        }
        return n4;
    }
    
    private int stackWordDelta(final ClassHolder classHolder, final int n, final short n2) {
        int variableStackDelta = CodeChunk.OPCODE_ACTION[n2][0];
        if (variableStackDelta == -128) {
            variableStackDelta = this.getVariableStackDelta(classHolder, n, n2);
        }
        return variableStackDelta;
    }
    
    private String getTypeDescriptor(final ClassHolder classHolder, final int n) {
        return ((CONSTANT_Utf8_info)classHolder.getEntry(((CONSTANT_Index_info)classHolder.getEntry(((CONSTANT_Index_info)classHolder.getEntry(this.getU2(n))).getI2())).getI2())).toString();
    }
    
    private static int getDescriptorWordCount(final String s) {
        int n;
        if ("D".equals(s)) {
            n = 2;
        }
        else if ("J".equals(s)) {
            n = 2;
        }
        else if (s.charAt(0) == '(') {
            switch (s.charAt(s.length() - 1)) {
                case 'D':
                case 'J': {
                    n = 2;
                    break;
                }
                case 'V': {
                    n = 0;
                    break;
                }
                default: {
                    n = 1;
                    break;
                }
            }
        }
        else {
            n = 1;
        }
        return n;
    }
    
    private int getVariableStackDelta(final ClassHolder classHolder, final int n, final int n2) {
        final String typeDescriptor = this.getTypeDescriptor(classHolder, n);
        final int descriptorWordCount = getDescriptorWordCount(typeDescriptor);
        int n3 = 0;
        switch (n2) {
            case 178: {
                n3 = descriptorWordCount;
                break;
            }
            case 180: {
                n3 = descriptorWordCount - 1;
                break;
            }
            case 179: {
                n3 = -descriptorWordCount;
                break;
            }
            case 181: {
                n3 = -descriptorWordCount - 1;
                break;
            }
            case 182:
            case 183: {
                n3 = -1;
            }
            case 184: {
                n3 += descriptorWordCount - parameterWordCount(typeDescriptor);
                break;
            }
            case 185: {
                n3 = descriptorWordCount - this.getOpcode(n + 3);
                break;
            }
            default: {
                System.out.println("WHO IS THIS ");
                break;
            }
        }
        return n3;
    }
    
    private static int parameterWordCount(final String s) {
        int n = 0;
        int n2 = 1;
    Label_0060:
        while (true) {
            Label_0097: {
                switch (s.charAt(n2)) {
                    case ')': {
                        break Label_0060;
                    }
                    case 'D':
                    case 'J': {
                        n += 2;
                        break;
                    }
                    case '[': {
                        do {
                            ++n2;
                        } while (s.charAt(n2) == '[');
                        if (s.charAt(n2) != 'L') {
                            ++n;
                            break;
                        }
                        break Label_0097;
                    }
                    case 'L': {
                        do {
                            ++n2;
                        } while (s.charAt(n2) != ';');
                        ++n;
                        break;
                    }
                    default: {
                        ++n;
                        break;
                    }
                }
            }
            ++n2;
        }
        return n;
    }
    
    private int[] findConditionalPCs(final int n, final short n2) {
        switch (n2) {
            default: {
                return null;
            }
            case 153:
            case 154:
            case 198:
            case 199: {
                final int u2 = this.getU2(n);
                int n3;
                int n4;
                if (u2 == 8 && this.getOpcode(n + 3) == 200) {
                    n3 = n + 3 + 5;
                    n4 = n + 3 + this.getU4(n + 3);
                }
                else {
                    n3 = n + 3;
                    n4 = n + u2;
                }
                int n5 = -1;
                int i = n3;
                while (i < n4) {
                    final short opcode = this.getOpcode(i);
                    final int[] conditionalPCs = this.findConditionalPCs(i, opcode);
                    if (conditionalPCs != null) {
                        i = conditionalPCs[5];
                    }
                    else if (opcode == 167) {
                        if (i != n4 - 3) {
                            continue;
                        }
                        n5 = i + this.getU2(i);
                        break;
                    }
                    else if (opcode == 200) {
                        if (i != n4 - 5) {
                            continue;
                        }
                        n5 = i + this.getU4(i);
                        break;
                    }
                    else {
                        i += instructionLength(opcode);
                    }
                }
                int n6;
                int n7;
                if (n5 == -1) {
                    n5 = n4;
                    n4 = -1;
                    n6 = n5 - n3;
                    n7 = -1;
                }
                else {
                    n6 = n4 - n3;
                    n7 = n5 - n4;
                }
                return new int[] { n, n3, n6, n4, n7, n5 };
            }
        }
    }
    
    final int splitZeroStack(final BCMethod bcMethod, final ClassHolder classHolder, final int n, final int n2) {
        final int splitMinLength = splitMinLength(bcMethod);
        int n3 = 0;
        int n4 = -1;
        int n5 = -1;
        final int pc = this.getPC();
        int i = n;
        while (i < pc) {
            final short opcode = this.getOpcode(i);
            n3 += this.stackWordDelta(classHolder, i, opcode);
            final int[] conditionalPCs = this.findConditionalPCs(i, opcode);
            if (conditionalPCs != null) {
                if (conditionalPCs[3] != -1) {
                    i = conditionalPCs[3];
                    continue;
                }
                if (n5 == -1) {
                    n5 = conditionalPCs[5];
                }
            }
            i += instructionLength(opcode);
            if (n5 != -1) {
                if (i <= n5) {
                    continue;
                }
                n5 = -1;
            }
            else {
                if (n3 != 0) {
                    continue;
                }
                int n6 = i - n;
                if (n6 >= n2) {
                    if (n6 > 65534) {
                        n6 = -1;
                    }
                    else if (isReturn(opcode)) {
                        n6 = -1;
                    }
                    if (n6 == -1) {
                        if (n4 == -1) {
                            return -1;
                        }
                        if (n4 <= splitMinLength) {
                            return -1;
                        }
                        n6 = n4;
                    }
                    return this.splitCodeIntoSubMethod(bcMethod, classHolder, this.startSubMethod(bcMethod, "void", n, n6), n, n6);
                }
                n4 = n6;
            }
        }
        return -1;
    }
    
    private BCMethod startSubMethod(final BCMethod bcMethod, final String s, final int n, final int n2) {
        return bcMethod.getNewSubMethod(s, this.usesParameters(bcMethod, n, n2));
    }
    
    private boolean usesParameters(final BCMethod bcMethod, int i, final int n) {
        if (bcMethod.parameters == null) {
            return false;
        }
        final boolean b = (bcMethod.myEntry.getModifier() & 0x8) != 0x0;
        while (i < i + n) {
            final short opcode = this.getOpcode(i);
            switch (opcode) {
                case 26:
                case 30:
                case 34:
                case 38: {
                    return true;
                }
                case 42: {
                    if (b) {
                        return true;
                    }
                    break;
                }
                case 27:
                case 31:
                case 35:
                case 39:
                case 43: {
                    return true;
                }
                case 28:
                case 32:
                case 36:
                case 40:
                case 44: {
                    return true;
                }
                case 29:
                case 33:
                case 37:
                case 41:
                case 45: {
                    return true;
                }
                case 21:
                case 22:
                case 23:
                case 24:
                case 25: {
                    return true;
                }
            }
            i += instructionLength(opcode);
        }
        return false;
    }
    
    private int splitCodeIntoSubMethod(final BCMethod bcMethod, final ClassHolder classHolder, final BCMethod bcMethod2, final int n, final int len) {
        final CodeChunk myCode = bcMethod2.myCode;
        final byte[] data = this.cout.getData();
        try {
            myCode.cout.write(data, 8 + n, len);
        }
        catch (IOException ex) {
            this.limitHit(ex);
        }
        if (bcMethod2.myReturnType.equals("void")) {
            myCode.addInstr((short)177);
        }
        else {
            myCode.addInstr((short)176);
        }
        if (this.cb.limitMsg != null) {
            return -1;
        }
        bcMethod2.maxStack = myCode.findMaxStack(classHolder, 0, myCode.getPC());
        bcMethod2.complete();
        return this.removePushedCode(bcMethod, classHolder, bcMethod2, n, len);
    }
    
    private int removePushedCode(final BCMethod bcMethod, final ClassHolder classHolder, final BCMethod bcMethod2, final int len, final int n) {
        final int pc = this.getPC();
        final CodeChunk myCode = new CodeChunk(bcMethod.cb);
        bcMethod.myCode = myCode;
        bcMethod.maxStack = 0;
        final byte[] data = this.cout.getData();
        if (len != 0) {
            try {
                myCode.cout.write(data, 8, len);
            }
            catch (IOException ex) {
                this.limitHit(ex);
            }
        }
        bcMethod.callSubMethod(bcMethod2);
        final int pc2 = myCode.getPC();
        final int n2 = len + n;
        final int len2 = pc - n - len;
        try {
            myCode.cout.write(data, 8 + n2, len2);
        }
        catch (IOException ex2) {
            this.limitHit(ex2);
        }
        if (this.cb.limitMsg != null) {
            return -1;
        }
        bcMethod.maxStack = myCode.findMaxStack(classHolder, 0, myCode.getPC());
        return pc2;
    }
    
    final int splitExpressionOut(final BCMethod bcMethod, final ClassHolder classHolder, final int n, final int n2) {
        int n3 = -1;
        int n4 = -1;
        String s = null;
        final int splitMinLength = splitMinLength(bcMethod);
        final int[] a = new int[n2 + 1];
        int n5 = 0;
        int n6 = -1;
        final int pc = this.getPC();
        int i = 0;
        while (i < pc) {
            final short opcode = this.getOpcode(i);
            n5 += this.stackWordDelta(classHolder, i, opcode);
            if (this.findConditionalPCs(i, opcode) != null) {
                return -1;
            }
            i += instructionLength(opcode);
            if (n6 != -1) {
                if (i <= n6) {
                    continue;
                }
                n6 = -1;
            }
            else {
                final int n7 = i - instructionLength(opcode);
                switch (opcode) {
                    default: {
                        Arrays.fill(a, 0, n5 + 1, -1);
                        continue;
                    }
                    case 0:
                    case 119:
                    case 139:
                    case 143:
                    case 190:
                    case 192: {
                        continue;
                    }
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                    case 7:
                    case 8:
                    case 11:
                    case 12:
                    case 13:
                    case 16:
                    case 17:
                    case 18:
                    case 19:
                    case 23:
                    case 25:
                    case 42:
                    case 43:
                    case 44:
                    case 45: {
                        a[n5] = n7;
                        continue;
                    }
                    case 9:
                    case 10:
                    case 14:
                    case 15:
                    case 20:
                    case 22:
                    case 30:
                    case 31:
                    case 32:
                    case 33: {
                        a[n5 - 1] = (a[n5] = n7);
                        continue;
                    }
                    case 87:
                    case 88: {
                        continue;
                    }
                    case 95: {
                        a[n5] = a[n5 - 1];
                        continue;
                    }
                    case 133: {
                        a[n5] = a[n5 - 1];
                        continue;
                    }
                    case 180: {
                        if (getDescriptorWordCount(this.getTypeDescriptor(classHolder, n7)) == 2) {
                            a[n5] = a[n5 - 1];
                            continue;
                        }
                        continue;
                    }
                    case 182:
                    case 185: {
                        final String typeDescriptor = this.getTypeDescriptor(classHolder, n7);
                        final int descriptorWordCount = getDescriptorWordCount(typeDescriptor);
                        int n8;
                        if (descriptorWordCount == 0) {
                            n8 = -1;
                        }
                        else if (descriptorWordCount == 1) {
                            n8 = a[n5];
                        }
                        else {
                            n8 = -1;
                            a[n5] = a[n5 - 1];
                        }
                        if (n8 != -1) {
                            final int n9 = i - n8;
                            if (n9 <= splitMinLength) {
                                continue;
                            }
                            if (n9 > 65534) {
                                continue;
                            }
                            final int lastIndex = typeDescriptor.lastIndexOf(41);
                            if (typeDescriptor.charAt(lastIndex + 1) != 'L') {
                                continue;
                            }
                            final String replace = typeDescriptor.substring(lastIndex + 2, typeDescriptor.length() - 1).replace('/', '.');
                            if (n9 >= n) {
                                return this.splitCodeIntoSubMethod(bcMethod, classHolder, this.startSubMethod(bcMethod, replace, n8, n9), n8, n9);
                            }
                            if (n9 <= n4) {
                                continue;
                            }
                            n3 = n8;
                            n4 = n9;
                            s = replace;
                            continue;
                        }
                        continue;
                    }
                }
            }
        }
        if (n4 != -1) {
            return this.splitCodeIntoSubMethod(bcMethod, classHolder, this.startSubMethod(bcMethod, s, n3, n4), n3, n4);
        }
        return -1;
    }
    
    private static boolean isReturn(final short n) {
        switch (n) {
            case 172:
            case 173:
            case 174:
            case 175:
            case 176:
            case 177: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    private static int splitMinLength(final BCMethod bcMethod) {
        int n = 4;
        if (bcMethod.parameters != null) {
            final int length = bcMethod.parameters.length;
            n += length;
            if (length > 3) {
                n += length - 3;
            }
        }
        return n;
    }
    
    static {
        LOAD_VARIABLE = new short[] { 21, 21, 21, 22, 23, 24, 21, 25 };
        LOAD_VARIABLE_FAST = new short[] { 26, 26, 26, 30, 34, 38, 26, 42 };
        STORE_VARIABLE = new short[] { 54, 54, 54, 55, 56, 57, 54, 58 };
        STORE_VARIABLE_FAST = new short[] { 59, 59, 59, 63, 67, 71, 59, 75 };
        ARRAY_ACCESS = new short[] { 51, 53, 46, 47, 48, 49, 52, 50 };
        ARRAY_STORE = new short[] { 84, 86, 79, 80, 81, 82, 85, 83 };
        RETURN_OPCODE = new short[] { 172, 172, 172, 173, 174, 175, 172, 176 };
        CAST_CONVERSION_INFO = new short[][][] { { { 0, 0 }, { 0, 1 }, { 0, 2 }, { 0, 2 }, { 0, 2 }, { 0, 2 }, { 0, 6 }, { -999, 7 } }, { { 0, 0 }, { 0, 1 }, { 0, 2 }, { 0, 2 }, { 0, 2 }, { 0, 2 }, { 0, 6 }, { -999, 7 } }, { { 145, 0 }, { 147, 1 }, { 0, 2 }, { 133, 3 }, { 134, 4 }, { 135, 5 }, { 145, 6 }, { -999, 7 } }, { { 136, 2 }, { 136, 2 }, { 136, 2 }, { 0, 3 }, { 137, 4 }, { 138, 5 }, { 136, 2 }, { -999, 7 } }, { { 139, 2 }, { 139, 2 }, { 139, 2 }, { 140, 3 }, { 0, 4 }, { 141, 5 }, { 139, 2 }, { -999, 7 } }, { { 142, 2 }, { 142, 2 }, { 142, 2 }, { 143, 3 }, { 144, 4 }, { 0, 5 }, { 142, 2 }, { -999, 7 } }, { { 0, 0 }, { 0, 1 }, { 0, 2 }, { 0, 2 }, { 0, 2 }, { 0, 2 }, { 0, 6 }, { -999, 7 } }, { { -999, 0 }, { -999, 1 }, { -999, 2 }, { -999, 3 }, { -999, 4 }, { -999, 5 }, { -999, 6 }, { 0, 7 } } };
        push1_1i = new byte[] { 1, 1 };
        push2_1i = new byte[] { 2, 1 };
        NS = new byte[] { 0, -1 };
        OPCODE_ACTION = new byte[][] { { 0, 1 }, CodeChunk.push1_1i, CodeChunk.push1_1i, CodeChunk.push1_1i, CodeChunk.push1_1i, CodeChunk.push1_1i, CodeChunk.push1_1i, CodeChunk.push1_1i, CodeChunk.push1_1i, CodeChunk.push2_1i, CodeChunk.push2_1i, CodeChunk.push1_1i, CodeChunk.push1_1i, CodeChunk.push1_1i, CodeChunk.push2_1i, CodeChunk.push2_1i, { 1, 2 }, { 1, 3 }, { 1, 2 }, { 1, 3 }, { 2, 3 }, { 1, 2 }, { 2, 2 }, { 1, 2 }, { 2, 2 }, { 1, 2 }, CodeChunk.push1_1i, CodeChunk.push1_1i, CodeChunk.push1_1i, CodeChunk.push1_1i, CodeChunk.push2_1i, CodeChunk.push2_1i, CodeChunk.push2_1i, CodeChunk.push2_1i, CodeChunk.push1_1i, CodeChunk.push1_1i, CodeChunk.push1_1i, CodeChunk.push1_1i, CodeChunk.push2_1i, CodeChunk.push2_1i, CodeChunk.push2_1i, CodeChunk.push2_1i, CodeChunk.push1_1i, CodeChunk.push1_1i, CodeChunk.push1_1i, CodeChunk.push1_1i, { -1, 1 }, { 0, 1 }, { -1, 1 }, { 0, 1 }, { -1, 1 }, { -1, 1 }, { -1, 1 }, { -1, 1 }, { -1, 2 }, { -2, 2 }, { -1, 2 }, { -2, 2 }, { -1, 2 }, { -1, 1 }, { -1, 1 }, { -1, 1 }, { -1, 1 }, { -2, 1 }, { -2, 1 }, { -2, 1 }, { -2, 1 }, { -1, 1 }, { -1, 1 }, { -1, 1 }, { -1, 1 }, { -2, 1 }, { -2, 1 }, { -2, 1 }, { -2, 1 }, { -1, 1 }, { -1, 1 }, { -1, 1 }, { -1, 1 }, { -3, 1 }, { -4, 1 }, { -3, 1 }, { -4, 1 }, { -3, 1 }, { -3, 1 }, { -3, 1 }, { -3, 1 }, { -1, 1 }, { -2, 1 }, CodeChunk.push1_1i, CodeChunk.push1_1i, CodeChunk.push1_1i, CodeChunk.push2_1i, CodeChunk.push2_1i, CodeChunk.push2_1i, { 0, 1 }, CodeChunk.NS, CodeChunk.NS, { -1, 1 }, { -2, 1 }, CodeChunk.NS, CodeChunk.NS, { -1, 1 }, { -2, 1 }, CodeChunk.NS, CodeChunk.NS, { -1, 1 }, { -2, 1 }, CodeChunk.NS, CodeChunk.NS, { -1, 1 }, { -2, 1 }, { -1, 1 }, { -2, 1 }, { -1, 1 }, { -2, 1 }, { 0, 1 }, { 0, 1 }, { 0, 1 }, { 0, 1 }, { -1, 1 }, CodeChunk.NS, CodeChunk.NS, CodeChunk.NS, CodeChunk.NS, CodeChunk.NS, { -1, 1 }, CodeChunk.NS, { -1, 1 }, CodeChunk.NS, CodeChunk.NS, CodeChunk.NS, CodeChunk.NS, CodeChunk.push1_1i, { 0, 1 }, CodeChunk.push1_1i, { -1, 1 }, { -1, 1 }, { 0, 1 }, { 0, 1 }, CodeChunk.push2_1i, CodeChunk.push1_1i, { -1, 1 }, { 0, 1 }, { -1, 1 }, { 0, 1 }, { 0, 1 }, { 0, 1 }, CodeChunk.NS, { -1, 1 }, { -1, 1 }, { -3, 1 }, { -3, 1 }, { -1, 3 }, { -1, 3 }, { -1, 3 }, { -1, 3 }, { -1, 3 }, { -1, 3 }, CodeChunk.NS, CodeChunk.NS, CodeChunk.NS, CodeChunk.NS, CodeChunk.NS, CodeChunk.NS, CodeChunk.NS, CodeChunk.NS, { 0, 3 }, CodeChunk.NS, CodeChunk.NS, CodeChunk.NS, CodeChunk.NS, { -1, 1 }, { -2, 1 }, { -1, 1 }, { -2, 1 }, { -1, 1 }, { 0, 1 }, { -128, 3 }, { -128, 3 }, { -128, 3 }, { -128, 3 }, { -128, 3 }, { -128, 3 }, { -128, 3 }, { -128, 5 }, CodeChunk.NS, { 1, 3 }, { 0, 2 }, { 0, 3 }, { 0, 1 }, CodeChunk.NS, { 0, 3 }, { 0, 3 }, CodeChunk.NS, CodeChunk.NS, CodeChunk.NS, CodeChunk.NS, { -1, 3 }, { -1, 3 }, { 0, 5 }, CodeChunk.NS, CodeChunk.NS };
    }
}
