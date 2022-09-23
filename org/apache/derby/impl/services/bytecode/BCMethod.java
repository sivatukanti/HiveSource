// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.services.bytecode;

import org.apache.derby.iapi.services.compiler.LocalField;
import java.io.IOException;
import org.apache.derby.iapi.services.classfile.ClassFormatOutput;
import org.apache.derby.iapi.services.compiler.ClassBuilder;
import org.apache.derby.iapi.services.classfile.ClassMember;
import java.util.Vector;
import org.apache.derby.iapi.services.classfile.ClassHolder;
import org.apache.derby.iapi.services.compiler.MethodBuilder;

class BCMethod implements MethodBuilder
{
    static final int CODE_SPLIT_LENGTH = 65535;
    final BCClass cb;
    protected final ClassHolder modClass;
    final String myReturnType;
    private final String myName;
    BCLocalField[] parameters;
    private final String[] parameterTypes;
    Vector thrownExceptions;
    CodeChunk myCode;
    protected ClassMember myEntry;
    private int currentVarNum;
    private int statementNum;
    private boolean handlingOverflow;
    private int subMethodCount;
    private Type[] stackTypes;
    private int stackTypeOffset;
    int maxStack;
    private int stackDepth;
    private Conditional condition;
    private static final byte[] newArrayElementTypeMap;
    static final byte T_BOOLEAN = 4;
    
    BCMethod(final ClassBuilder classBuilder, final String myReturnType, final String myName, final int n, final String[] parameterTypes, final BCJava bcJava) {
        this.stackTypes = new Type[8];
        this.cb = (BCClass)classBuilder;
        this.modClass = this.cb.modify();
        this.myReturnType = myReturnType;
        this.myName = myName;
        if ((n & 0x8) == 0x0) {
            this.currentVarNum = 1;
        }
        String[] empty;
        if (parameterTypes != null && parameterTypes.length != 0) {
            final int length = parameterTypes.length;
            empty = new String[length];
            this.parameters = new BCLocalField[length];
            for (int i = 0; i < length; ++i) {
                final Type type = bcJava.type(parameterTypes[i]);
                this.parameters[i] = new BCLocalField(type, this.currentVarNum);
                this.currentVarNum += type.width();
                empty[i] = type.vmName();
            }
        }
        else {
            empty = BCMethodDescriptor.EMPTY;
        }
        this.myEntry = this.modClass.addMember(myName, BCMethodDescriptor.get(empty, bcJava.type(myReturnType).vmName(), bcJava), n);
        this.myCode = new CodeChunk(this.cb);
        this.parameterTypes = parameterTypes;
    }
    
    public String getName() {
        return this.myName;
    }
    
    public void getParameter(final int n) {
        final int cpi = this.parameters[n].cpi;
        final short vmType = this.parameters[n].type.vmType();
        if (cpi < 4) {
            this.myCode.addInstr((short)(CodeChunk.LOAD_VARIABLE_FAST[vmType] + cpi));
        }
        else {
            this.myCode.addInstrWide(CodeChunk.LOAD_VARIABLE[vmType], cpi);
        }
        this.growStack(this.parameters[n].type);
    }
    
    public void addThrownException(final String e) {
        if (this.thrownExceptions == null) {
            this.thrownExceptions = new Vector();
        }
        this.thrownExceptions.add(e);
    }
    
    public void complete() {
        if (this.myCode.getPC() > 65535) {
            this.splitMethod();
        }
        this.writeExceptions();
        this.myCode.complete(this, this.modClass, this.myEntry, this.maxStack, this.currentVarNum);
    }
    
    private void splitMethod() {
        int n = 0;
        int n2 = 1;
        for (int n3 = this.myCode.getPC(); this.cb.limitMsg == null && n3 > 65535; n3 = this.myCode.getPC()) {
            final int n4 = n3 - n;
            int n5;
            if (n3 < 131070) {
                n5 = n3 - 65535;
            }
            else {
                n5 = 65534;
            }
            if (n5 > n4) {
                n5 = n4;
            }
            if (n2 != 0) {
                n = this.myCode.splitZeroStack(this, this.modClass, n, n5);
            }
            else {
                n = this.myCode.splitExpressionOut(this, this.modClass, n5, this.maxStack);
            }
            if (n < 0) {
                if (n2 == 0) {
                    break;
                }
                n2 = 0;
                n = 0;
            }
        }
    }
    
    ClassHolder constantPool() {
        return this.modClass;
    }
    
    protected void writeExceptions() {
        if (this.thrownExceptions == null) {
            return;
        }
        final int size = this.thrownExceptions.size();
        if (size != 0) {
            try {
                final ClassFormatOutput classFormatOutput = new ClassFormatOutput(size * 2 + 2);
                classFormatOutput.putU2(size);
                for (int i = 0; i < size; ++i) {
                    classFormatOutput.putU2(this.modClass.addClassReference(this.thrownExceptions.get(i).toString()));
                }
                this.myEntry.addAttribute("Exceptions", classFormatOutput);
            }
            catch (IOException ex) {}
        }
    }
    
    private void growStack(final int n, final Type type) {
        this.stackDepth += n;
        if (this.stackDepth > this.maxStack) {
            this.maxStack = this.stackDepth;
        }
        if (this.stackTypeOffset >= this.stackTypes.length) {
            final Type[] stackTypes = new Type[this.stackTypes.length + 8];
            System.arraycopy(this.stackTypes, 0, stackTypes, 0, this.stackTypes.length);
            this.stackTypes = stackTypes;
        }
        this.stackTypes[this.stackTypeOffset++] = type;
    }
    
    private void growStack(final Type type) {
        this.growStack(type.width(), type);
    }
    
    private Type popStack() {
        --this.stackTypeOffset;
        final Type type = this.stackTypes[this.stackTypeOffset];
        this.stackDepth -= type.width();
        return type;
    }
    
    private Type[] copyStack() {
        final Type[] array = new Type[this.stackTypeOffset];
        System.arraycopy(this.stackTypes, 0, array, 0, this.stackTypeOffset);
        return array;
    }
    
    public void pushThis() {
        this.myCode.addInstr((short)42);
        this.growStack(1, this.cb.classType);
    }
    
    public void push(final byte b) {
        this.push(b, Type.BYTE);
    }
    
    public void push(final boolean b) {
        this.push(b ? 1 : 0, Type.BOOLEAN);
    }
    
    public void push(final short n) {
        this.push(n, Type.SHORT);
    }
    
    public void push(final int n) {
        this.push(n, Type.INT);
    }
    
    public void dup() {
        final Type popStack = this.popStack();
        this.myCode.addInstr((short)((popStack.width() == 2) ? 92 : 89));
        this.growStack(popStack);
        this.growStack(popStack);
    }
    
    public void swap() {
        final Type popStack = this.popStack();
        final Type popStack2 = this.popStack();
        this.growStack(popStack);
        this.growStack(popStack2);
        if (popStack.width() == 1) {
            if (popStack2.width() == 1) {
                this.myCode.addInstr((short)95);
                return;
            }
            this.myCode.addInstr((short)91);
            this.myCode.addInstr((short)87);
        }
        else if (popStack2.width() == 1) {
            this.myCode.addInstr((short)93);
            this.myCode.addInstr((short)88);
        }
        else {
            this.myCode.addInstr((short)94);
            this.myCode.addInstr((short)88);
        }
        this.growStack(popStack);
        this.popStack();
    }
    
    private void push(final int n, final Type type) {
        final CodeChunk myCode = this.myCode;
        if (n >= -1 && n <= 5) {
            myCode.addInstr((short)(3 + n));
        }
        else if (n >= -128 && n <= 127) {
            myCode.addInstrU1((short)16, n);
        }
        else if (n >= -32768 && n <= 32767) {
            myCode.addInstrU2((short)17, n);
        }
        else {
            this.addInstrCPE((short)18, this.modClass.addConstant(n));
        }
        this.growStack(type.width(), type);
    }
    
    public void push(final long n) {
        final CodeChunk myCode = this.myCode;
        if (n == 0L || n == 1L) {
            myCode.addInstr((short)((n == 0L) ? 9 : 10));
        }
        else {
            if (n >= -2147483648L && n <= 2147483647L) {
                this.push((int)n, Type.LONG);
                myCode.addInstr((short)133);
                return;
            }
            myCode.addInstrU2((short)20, this.modClass.addConstant(n));
        }
        this.growStack(2, Type.LONG);
    }
    
    public void push(final float n) {
        final CodeChunk myCode = this.myCode;
        if (n == 0.0) {
            myCode.addInstr((short)11);
        }
        else if (n == 1.0) {
            myCode.addInstr((short)12);
        }
        else if (n == 2.0) {
            myCode.addInstr((short)13);
        }
        else {
            this.addInstrCPE((short)18, this.modClass.addConstant(n));
        }
        this.growStack(1, Type.FLOAT);
    }
    
    public void push(final double n) {
        final CodeChunk myCode = this.myCode;
        if (n == 0.0) {
            myCode.addInstr((short)14);
        }
        else {
            myCode.addInstrU2((short)20, this.modClass.addConstant(n));
        }
        this.growStack(2, Type.DOUBLE);
    }
    
    public void push(final String s) {
        this.addInstrCPE((short)18, this.modClass.addConstant(s));
        this.growStack(1, Type.STRING);
    }
    
    public void methodReturn() {
        short n;
        if (this.stackDepth != 0) {
            n = CodeChunk.RETURN_OPCODE[this.popStack().vmType()];
        }
        else {
            n = 177;
        }
        this.myCode.addInstr(n);
    }
    
    public Object describeMethod(final short n, String javaName, final String s, final String s2) {
        final Type type = this.cb.factory.type(s2);
        final String value = BCMethodDescriptor.get(BCMethodDescriptor.EMPTY, type.vmName(), this.cb.factory);
        if (javaName == null && n != 184) {
            final Type type2 = this.stackTypes[this.stackTypeOffset - 1];
            if (javaName == null) {
                javaName = type2.javaName();
            }
        }
        return new BCMethodCaller(n, type, this.modClass.addMethodReference(javaName, s, value, n == 185));
    }
    
    public int callMethod(final Object o) {
        this.popStack();
        final BCMethodCaller bcMethodCaller = (BCMethodCaller)o;
        final int cpi = bcMethodCaller.cpi;
        final short opcode = bcMethodCaller.opcode;
        if (opcode == 185) {
            this.myCode.addInstrU2U1U1(opcode, cpi, (short)1, (short)0);
        }
        else {
            this.myCode.addInstrU2(opcode, cpi);
        }
        final Type type = bcMethodCaller.type;
        final int width = type.width();
        if (width != 0) {
            this.growStack(width, type);
        }
        else {
            this.overflowMethodCheck();
        }
        return cpi;
    }
    
    public int callMethod(final short n, final String s, final String s2, final String s3, final int n2) {
        final Type type = this.cb.factory.type(s3);
        final int stackDepth = this.stackDepth;
        String[] empty;
        if (n2 == 0) {
            empty = BCMethodDescriptor.EMPTY;
        }
        else {
            empty = new String[n2];
            for (int i = n2 - 1; i >= 0; --i) {
                empty[i] = this.popStack().vmName();
            }
        }
        final String value = BCMethodDescriptor.get(empty, type.vmName(), this.cb.factory);
        Type popStack = null;
        if (n != 184) {
            popStack = this.popStack();
        }
        final Type vmNameDeclaringClass = this.vmNameDeclaringClass(s);
        if (vmNameDeclaringClass != null) {
            popStack = vmNameDeclaringClass;
        }
        final int addMethodReference = this.modClass.addMethodReference(popStack.vmNameSimple, s2, value, n == 185);
        if (n == 185) {
            this.myCode.addInstrU2U1U1(n, addMethodReference, (short)(stackDepth - this.stackDepth), (short)0);
        }
        else {
            this.myCode.addInstrU2(n, addMethodReference);
        }
        final int width = type.width();
        if (width != 0) {
            this.growStack(width, type);
        }
        else {
            this.overflowMethodCheck();
        }
        return addMethodReference;
    }
    
    private Type vmNameDeclaringClass(final String s) {
        if (s == null) {
            return null;
        }
        return this.cb.factory.type(s);
    }
    
    public void callSuper() {
        this.pushThis();
        this.callMethod((short)183, this.cb.getSuperClassName(), "<init>", "void", 0);
    }
    
    public void pushNewStart(final String s) {
        this.myCode.addInstrU2((short)187, this.modClass.addClassReference(s));
        this.myCode.addInstr((short)89);
        final Type type = this.cb.factory.type(s);
        this.growStack(1, type);
        this.growStack(1, type);
    }
    
    public void pushNewComplete(final int n) {
        this.callMethod((short)183, null, "<init>", "void", n);
    }
    
    public void upCast(final String s) {
        this.stackTypes[this.stackTypeOffset - 1] = this.cb.factory.type(s);
    }
    
    public void cast(final String s) {
        final Type type = this.stackTypes[this.stackTypeOffset - 1];
        short vmType = type.vmType();
        if (vmType == 7 && s.equals(type.javaName())) {
            return;
        }
        final Type type2 = this.cb.factory.type(s);
        this.popStack();
        final short vmType2 = type2.vmType();
        if (vmType == 7) {
            this.myCode.addInstrU2((short)192, this.modClass.addClassReference(type2.vmNameSimple));
        }
        else {
            short n = 0;
            while (vmType != vmType2 && n != -999) {
                final short[] array = CodeChunk.CAST_CONVERSION_INFO[vmType][vmType2];
                vmType = array[1];
                n = array[0];
                if (n != 0) {
                    this.myCode.addInstr(n);
                }
            }
        }
        this.growStack(type2);
    }
    
    public void isInstanceOf(final String s) {
        this.myCode.addInstrU2((short)193, this.modClass.addClassReference(s));
        this.popStack();
        this.growStack(1, Type.BOOLEAN);
    }
    
    public void pushNull(final String s) {
        this.myCode.addInstr((short)1);
        this.growStack(1, this.cb.factory.type(s));
    }
    
    public void getField(final LocalField localField) {
        final BCLocalField bcLocalField = (BCLocalField)localField;
        final Type type = bcLocalField.type;
        this.pushThis();
        this.myCode.addInstrU2((short)180, bcLocalField.cpi);
        this.popStack();
        this.growStack(type);
    }
    
    public void getField(final String s, final String s2, final String s3) {
        Type popStack = this.popStack();
        final Type vmNameDeclaringClass = this.vmNameDeclaringClass(s);
        if (vmNameDeclaringClass != null) {
            popStack = vmNameDeclaringClass;
        }
        this.getField((short)180, popStack.vmNameSimple, s2, s3);
    }
    
    public void getStaticField(final String s, final String s2, final String s3) {
        this.getField((short)178, s, s2, s3);
    }
    
    private void getField(final short n, final String s, final String s2, final String s3) {
        final Type type = this.cb.factory.type(s3);
        this.myCode.addInstrU2(n, this.modClass.addFieldReference(this.vmNameDeclaringClass(s).vmNameSimple, s2, type.vmName()));
        this.growStack(type);
    }
    
    public void setField(final LocalField localField) {
        final BCLocalField bcLocalField = (BCLocalField)localField;
        this.putField(bcLocalField.type, bcLocalField.cpi, false);
        this.overflowMethodCheck();
    }
    
    public void putField(final LocalField localField) {
        final BCLocalField bcLocalField = (BCLocalField)localField;
        this.putField(bcLocalField.type, bcLocalField.cpi, true);
    }
    
    public void putField(final String s, final String s2) {
        final Type type = this.cb.factory.type(s2);
        this.putField(type, this.modClass.addFieldReference(this.cb.classType.vmNameSimple, s, type.vmName()), true);
    }
    
    private void putField(final Type type, final int n, final boolean b) {
        if (b) {
            this.myCode.addInstr((short)((type.width() == 2) ? 92 : 89));
            this.growStack(type);
        }
        this.pushThis();
        this.swap();
        this.myCode.addInstrU2((short)181, n);
        this.popStack();
        this.popStack();
    }
    
    public void putField(final String s, final String s2, final String s3) {
        final Type popStack = this.popStack();
        Type popStack2 = this.popStack();
        this.myCode.addInstr((short)((popStack.width() == 2) ? 93 : 90));
        this.growStack(popStack);
        this.growStack(popStack2);
        this.growStack(popStack);
        final Type vmNameDeclaringClass = this.vmNameDeclaringClass(s);
        if (vmNameDeclaringClass != null) {
            popStack2 = vmNameDeclaringClass;
        }
        this.myCode.addInstrU2((short)181, this.modClass.addFieldReference(popStack2.vmNameSimple, s2, this.cb.factory.type(s3).vmName()));
        this.popStack();
        this.popStack();
    }
    
    public void conditionalIfNull() {
        this.conditionalIf((short)199);
    }
    
    public void conditionalIf() {
        this.conditionalIf((short)153);
    }
    
    private void conditionalIf(final short n) {
        this.popStack();
        this.condition = new Conditional(this.condition, this.myCode, n, this.copyStack());
    }
    
    public void startElseCode() {
        final Type[] startElse = this.condition.startElse(this, this.myCode, this.copyStack());
        final int stackDepth = 0;
        this.stackDepth = stackDepth;
        for (int i = stackDepth; i < startElse.length; ++i) {
            final int stackDepth2 = this.stackDepth;
            final Type[] stackTypes = this.stackTypes;
            final int n = i;
            final Type type = startElse[i];
            stackTypes[n] = type;
            this.stackDepth = stackDepth2 + type.width();
        }
        this.stackTypeOffset = startElse.length;
    }
    
    public void completeConditional() {
        this.condition = this.condition.end(this, this.myCode, this.stackTypes, this.stackTypeOffset);
    }
    
    public void pop() {
        this.myCode.addInstr((short)((this.popStack().width() == 2) ? 88 : 87));
        this.overflowMethodCheck();
    }
    
    public void endStatement() {
        if (this.stackDepth != 0) {
            this.pop();
        }
    }
    
    public void getArrayElement(final int n) {
        this.push(n);
        this.popStack();
        final String javaName = this.popStack().javaName();
        final Type type = this.cb.factory.type(javaName.substring(0, javaName.length() - 2));
        int vmType = type.vmType();
        if (vmType == 2 && type.vmName().equals("Z")) {
            vmType = 0;
        }
        this.myCode.addInstr(CodeChunk.ARRAY_ACCESS[vmType]);
        this.growStack(type);
    }
    
    public void setArrayElement(final int n) {
        this.push(n);
        this.swap();
        final Type popStack = this.popStack();
        this.popStack();
        this.popStack();
        int vmType = popStack.vmType();
        if (vmType == 2 && popStack.vmName().equals("Z")) {
            vmType = 0;
        }
        this.myCode.addInstr(CodeChunk.ARRAY_STORE[vmType]);
    }
    
    public void pushNewArray(final String s, final int n) {
        this.push(n);
        this.popStack();
        final Type type = this.cb.factory.type(s);
        if (type.vmType() == 7) {
            this.myCode.addInstrU2((short)189, this.modClass.addClassReference(type.javaName()));
        }
        else {
            int n2;
            if (type.vmType() == 2 && 'Z' == type.vmName().charAt(0)) {
                n2 = 4;
            }
            else {
                n2 = BCMethod.newArrayElementTypeMap[type.vmType()];
            }
            this.myCode.addInstrU1((short)188, n2);
        }
        this.growStack(1, this.cb.factory.type(s.concat("[]")));
    }
    
    private void addInstrCPE(final short n, final int n2) {
        if (n2 >= 65535) {
            this.cb.addLimitExceeded(this, "constant_pool_count", 65535, n2);
        }
        this.myCode.addInstrCPE(n, n2);
    }
    
    public boolean statementNumHitLimit(final int n) {
        if (this.statementNum > 2048) {
            return true;
        }
        this.statementNum += n;
        return false;
    }
    
    private void overflowMethodCheck() {
        if (this.stackDepth != 0) {
            return;
        }
        if (this.handlingOverflow) {
            return;
        }
        if (this.condition != null) {
            return;
        }
        if (this.myCode.getPC() < 55000) {
            return;
        }
        if (this.parameters != null && this.parameters.length != 0) {
            return;
        }
        final BCMethod newSubMethod = this.getNewSubMethod(this.myReturnType, false);
        this.handlingOverflow = true;
        this.callSubMethod(newSubMethod);
        this.methodReturn();
        this.complete();
        this.handlingOverflow = false;
        this.myEntry = newSubMethod.myEntry;
        this.myCode = newSubMethod.myCode;
        this.currentVarNum = newSubMethod.currentVarNum;
        this.statementNum = newSubMethod.statementNum;
        this.stackTypes = newSubMethod.stackTypes;
        this.stackTypeOffset = newSubMethod.stackTypeOffset;
        this.maxStack = newSubMethod.maxStack;
        this.stackDepth = newSubMethod.stackDepth;
    }
    
    final BCMethod getNewSubMethod(final String s, final boolean b) {
        final BCMethod bcMethod = (BCMethod)this.cb.newMethodBuilder((this.myEntry.getModifier() & 0xFFFFFFFA) | 0x2, s, this.myName + "_s" + Integer.toString(this.subMethodCount++), (String[])(b ? this.parameterTypes : null));
        bcMethod.thrownExceptions = this.thrownExceptions;
        return bcMethod;
    }
    
    final void callSubMethod(final BCMethod bcMethod) {
        short n;
        if ((this.myEntry.getModifier() & 0x8) == 0x0) {
            n = 182;
            this.pushThis();
        }
        else {
            n = 184;
        }
        final int n2 = (bcMethod.parameters == null) ? 0 : bcMethod.parameters.length;
        for (int i = 0; i < n2; ++i) {
            this.getParameter(i);
        }
        this.callMethod(n, this.modClass.getName(), bcMethod.getName(), bcMethod.myReturnType, n2);
    }
    
    static {
        newArrayElementTypeMap = new byte[] { 8, 9, 10, 11, 6, 7, 5 };
    }
}
