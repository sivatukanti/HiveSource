// 
// Decompiled by Procyon v0.5.36
// 

package net.minidev.asm;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.ClassWriter;
import java.util.Iterator;
import org.objectweb.asm.Type;
import java.lang.reflect.Method;
import java.util.HashMap;

public class BeansAccessBuilder
{
    private static String METHOD_ACCESS_NAME;
    final Class<?> type;
    final Accessor[] accs;
    final DynamicClassLoader loader;
    final String className;
    final String accessClassName;
    final String accessClassNameInternal;
    final String classNameInternal;
    final HashMap<Class<?>, Method> convMtds;
    Class<? extends Exception> exeptionClass;
    
    static {
        BeansAccessBuilder.METHOD_ACCESS_NAME = Type.getInternalName(BeansAccess.class);
    }
    
    public BeansAccessBuilder(final Class<?> type, final Accessor[] accs, final DynamicClassLoader loader) {
        this.convMtds = new HashMap<Class<?>, Method>();
        this.exeptionClass = NoSuchFieldException.class;
        this.type = type;
        this.accs = accs;
        this.loader = loader;
        this.className = type.getName();
        if (this.className.startsWith("java.")) {
            this.accessClassName = "net.minidev.asm." + this.className + "AccAccess";
        }
        else {
            this.accessClassName = this.className.concat("AccAccess");
        }
        this.accessClassNameInternal = this.accessClassName.replace('.', '/');
        this.classNameInternal = this.className.replace('.', '/');
    }
    
    public void addConversion(final Iterable<Class<?>> conv) {
        if (conv == null) {
            return;
        }
        for (final Class<?> c : conv) {
            this.addConversion(c);
        }
    }
    
    public void addConversion(final Class<?> conv) {
        if (conv == null) {
            return;
        }
        Method[] methods;
        for (int length = (methods = conv.getMethods()).length, i = 0; i < length; ++i) {
            final Method mtd = methods[i];
            if ((mtd.getModifiers() & 0x8) != 0x0) {
                final Class[] param = mtd.getParameterTypes();
                if (param.length == 1) {
                    if (param[0].equals(Object.class)) {
                        final Class<?> rType = mtd.getReturnType();
                        if (!rType.equals(Void.TYPE)) {
                            this.convMtds.put(rType, mtd);
                        }
                    }
                }
            }
        }
    }
    
    public Class<?> bulid() {
        final ClassWriter cw = new ClassWriter(1);
        final boolean USE_HASH = this.accs.length > 10;
        final int HASH_LIMIT = 14;
        final String signature = "Lnet/minidev/asm/BeansAccess<L" + this.classNameInternal + ";>;";
        cw.visit(50, 33, this.accessClassNameInternal, signature, BeansAccessBuilder.METHOD_ACCESS_NAME, null);
        MethodVisitor mv = cw.visitMethod(1, "<init>", "()V", null, null);
        mv.visitCode();
        mv.visitVarInsn(25, 0);
        mv.visitMethodInsn(183, BeansAccessBuilder.METHOD_ACCESS_NAME, "<init>", "()V");
        mv.visitInsn(177);
        mv.visitMaxs(1, 1);
        mv.visitEnd();
        mv = cw.visitMethod(1, "set", "(Ljava/lang/Object;ILjava/lang/Object;)V", null, null);
        mv.visitCode();
        if (this.accs.length != 0) {
            if (this.accs.length > HASH_LIMIT) {
                mv.visitVarInsn(21, 2);
                final Label[] labels = ASMUtil.newLabels(this.accs.length);
                final Label defaultLabel = new Label();
                mv.visitTableSwitchInsn(0, labels.length - 1, defaultLabel, labels);
                int i = 0;
                Accessor[] accs;
                for (int length = (accs = this.accs).length, k = 0; k < length; ++k) {
                    final Accessor acc = accs[k];
                    mv.visitLabel(labels[i++]);
                    if (!acc.isWritable()) {
                        mv.visitInsn(177);
                    }
                    else {
                        this.internalSetFiled(mv, acc);
                    }
                }
                mv.visitLabel(defaultLabel);
            }
            else {
                final Label[] labels = ASMUtil.newLabels(this.accs.length);
                int j = 0;
                Accessor[] accs2;
                for (int length2 = (accs2 = this.accs).length, l = 0; l < length2; ++l) {
                    final Accessor acc2 = accs2[l];
                    this.ifNotEqJmp(mv, 2, j, labels[j]);
                    this.internalSetFiled(mv, acc2);
                    mv.visitLabel(labels[j]);
                    mv.visitFrame(3, 0, null, 0, null);
                    ++j;
                }
            }
        }
        if (this.exeptionClass != null) {
            this.throwExIntParam(mv, this.exeptionClass);
        }
        else {
            mv.visitInsn(177);
        }
        mv.visitMaxs(0, 0);
        mv.visitEnd();
        mv = cw.visitMethod(1, "get", "(Ljava/lang/Object;I)Ljava/lang/Object;", null, null);
        mv.visitCode();
        if (this.accs.length == 0) {
            mv.visitFrame(3, 0, null, 0, null);
        }
        else if (this.accs.length > HASH_LIMIT) {
            mv.visitVarInsn(21, 2);
            final Label[] labels = ASMUtil.newLabels(this.accs.length);
            final Label defaultLabel = new Label();
            mv.visitTableSwitchInsn(0, labels.length - 1, defaultLabel, labels);
            int i = 0;
            Accessor[] accs3;
            for (int length3 = (accs3 = this.accs).length, n = 0; n < length3; ++n) {
                final Accessor acc = accs3[n];
                mv.visitLabel(labels[i++]);
                mv.visitFrame(3, 0, null, 0, null);
                if (!acc.isReadable()) {
                    mv.visitInsn(1);
                    mv.visitInsn(176);
                }
                else {
                    mv.visitVarInsn(25, 1);
                    mv.visitTypeInsn(192, this.classNameInternal);
                    final Type fieldType = Type.getType(acc.getType());
                    if (acc.isPublic()) {
                        mv.visitFieldInsn(180, this.classNameInternal, acc.getName(), fieldType.getDescriptor());
                    }
                    else {
                        final String sig = Type.getMethodDescriptor(acc.getter);
                        mv.visitMethodInsn(182, this.classNameInternal, acc.getter.getName(), sig);
                    }
                    ASMUtil.autoBoxing(mv, fieldType);
                    mv.visitInsn(176);
                }
            }
            mv.visitLabel(defaultLabel);
            mv.visitFrame(3, 0, null, 0, null);
        }
        else {
            final Label[] labels = ASMUtil.newLabels(this.accs.length);
            int j = 0;
            Accessor[] accs4;
            for (int length4 = (accs4 = this.accs).length, n2 = 0; n2 < length4; ++n2) {
                final Accessor acc2 = accs4[n2];
                this.ifNotEqJmp(mv, 2, j, labels[j]);
                mv.visitVarInsn(25, 1);
                mv.visitTypeInsn(192, this.classNameInternal);
                final Type fieldType2 = Type.getType(acc2.getType());
                if (acc2.isPublic()) {
                    mv.visitFieldInsn(180, this.classNameInternal, acc2.getName(), fieldType2.getDescriptor());
                }
                else {
                    if (acc2.getter == null) {
                        throw new RuntimeException("no Getter for field " + acc2.getName() + " in class " + this.className);
                    }
                    final String sig2 = Type.getMethodDescriptor(acc2.getter);
                    mv.visitMethodInsn(182, this.classNameInternal, acc2.getter.getName(), sig2);
                }
                ASMUtil.autoBoxing(mv, fieldType2);
                mv.visitInsn(176);
                mv.visitLabel(labels[j]);
                mv.visitFrame(3, 0, null, 0, null);
                ++j;
            }
        }
        if (this.exeptionClass != null) {
            this.throwExIntParam(mv, this.exeptionClass);
        }
        else {
            mv.visitInsn(1);
            mv.visitInsn(176);
        }
        mv.visitMaxs(0, 0);
        mv.visitEnd();
        if (!USE_HASH) {
            mv = cw.visitMethod(1, "set", "(Ljava/lang/Object;Ljava/lang/String;Ljava/lang/Object;)V", null, null);
            mv.visitCode();
            final Label[] labels = ASMUtil.newLabels(this.accs.length);
            int j = 0;
            Accessor[] accs5;
            for (int length5 = (accs5 = this.accs).length, n3 = 0; n3 < length5; ++n3) {
                final Accessor acc2 = accs5[n3];
                mv.visitVarInsn(25, 2);
                mv.visitLdcInsn(acc2.fieldName);
                mv.visitMethodInsn(182, "java/lang/String", "equals", "(Ljava/lang/Object;)Z");
                mv.visitJumpInsn(153, labels[j]);
                this.internalSetFiled(mv, acc2);
                mv.visitLabel(labels[j]);
                mv.visitFrame(3, 0, null, 0, null);
                ++j;
            }
            if (this.exeptionClass != null) {
                this.throwExStrParam(mv, this.exeptionClass);
            }
            else {
                mv.visitInsn(177);
            }
            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }
        if (!USE_HASH) {
            mv = cw.visitMethod(1, "get", "(Ljava/lang/Object;Ljava/lang/String;)Ljava/lang/Object;", null, null);
            mv.visitCode();
            final Label[] labels = ASMUtil.newLabels(this.accs.length);
            int j = 0;
            Accessor[] accs6;
            for (int length6 = (accs6 = this.accs).length, n4 = 0; n4 < length6; ++n4) {
                final Accessor acc2 = accs6[n4];
                mv.visitVarInsn(25, 2);
                mv.visitLdcInsn(acc2.fieldName);
                mv.visitMethodInsn(182, "java/lang/String", "equals", "(Ljava/lang/Object;)Z");
                mv.visitJumpInsn(153, labels[j]);
                mv.visitVarInsn(25, 1);
                mv.visitTypeInsn(192, this.classNameInternal);
                final Type fieldType2 = Type.getType(acc2.getType());
                if (acc2.isPublic()) {
                    mv.visitFieldInsn(180, this.classNameInternal, acc2.getName(), fieldType2.getDescriptor());
                }
                else {
                    final String sig2 = Type.getMethodDescriptor(acc2.getter);
                    mv.visitMethodInsn(182, this.classNameInternal, acc2.getter.getName(), sig2);
                }
                ASMUtil.autoBoxing(mv, fieldType2);
                mv.visitInsn(176);
                mv.visitLabel(labels[j]);
                mv.visitFrame(3, 0, null, 0, null);
                ++j;
            }
            if (this.exeptionClass != null) {
                this.throwExStrParam(mv, this.exeptionClass);
            }
            else {
                mv.visitInsn(1);
                mv.visitInsn(176);
            }
            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }
        mv = cw.visitMethod(1, "newInstance", "()Ljava/lang/Object;", null, null);
        mv.visitCode();
        mv.visitTypeInsn(187, this.classNameInternal);
        mv.visitInsn(89);
        mv.visitMethodInsn(183, this.classNameInternal, "<init>", "()V");
        mv.visitInsn(176);
        mv.visitMaxs(2, 1);
        mv.visitEnd();
        cw.visitEnd();
        final byte[] data = cw.toByteArray();
        return this.loader.defineClass(this.accessClassName, data);
    }
    
    private void dumpDebug(final byte[] data, final String destFile) {
    }
    
    private void internalSetFiled(final MethodVisitor mv, final Accessor acc) {
        mv.visitVarInsn(25, 1);
        mv.visitTypeInsn(192, this.classNameInternal);
        mv.visitVarInsn(25, 3);
        final Type fieldType = Type.getType(acc.getType());
        final Class<?> type = acc.getType();
        final String destClsName = Type.getInternalName(type);
        final Method conMtd = this.convMtds.get(type);
        if (conMtd != null) {
            final String clsSig = Type.getInternalName(conMtd.getDeclaringClass());
            final String mtdName = conMtd.getName();
            final String mtdSig = Type.getMethodDescriptor(conMtd);
            mv.visitMethodInsn(184, clsSig, mtdName, mtdSig);
        }
        else if (acc.isEnum()) {
            final Label isNull = new Label();
            mv.visitJumpInsn(198, isNull);
            mv.visitVarInsn(25, 3);
            mv.visitMethodInsn(182, "java/lang/Object", "toString", "()Ljava/lang/String;");
            mv.visitMethodInsn(184, destClsName, "valueOf", "(Ljava/lang/String;)L" + destClsName + ";");
            mv.visitVarInsn(58, 3);
            mv.visitLabel(isNull);
            mv.visitFrame(3, 0, null, 0, null);
            mv.visitVarInsn(25, 1);
            mv.visitTypeInsn(192, this.classNameInternal);
            mv.visitVarInsn(25, 3);
            mv.visitTypeInsn(192, destClsName);
        }
        else if (type.equals(String.class)) {
            final Label isNull = new Label();
            mv.visitJumpInsn(198, isNull);
            mv.visitVarInsn(25, 3);
            mv.visitMethodInsn(182, "java/lang/Object", "toString", "()Ljava/lang/String;");
            mv.visitVarInsn(58, 3);
            mv.visitLabel(isNull);
            mv.visitFrame(3, 0, null, 0, null);
            mv.visitVarInsn(25, 1);
            mv.visitTypeInsn(192, this.classNameInternal);
            mv.visitVarInsn(25, 3);
            mv.visitTypeInsn(192, destClsName);
        }
        else {
            mv.visitTypeInsn(192, destClsName);
        }
        if (acc.isPublic()) {
            mv.visitFieldInsn(181, this.classNameInternal, acc.getName(), fieldType.getDescriptor());
        }
        else {
            final String sig = Type.getMethodDescriptor(acc.setter);
            mv.visitMethodInsn(182, this.classNameInternal, acc.setter.getName(), sig);
        }
        mv.visitInsn(177);
    }
    
    private void throwExIntParam(final MethodVisitor mv, final Class<?> exCls) {
        final String exSig = Type.getInternalName(exCls);
        mv.visitTypeInsn(187, exSig);
        mv.visitInsn(89);
        mv.visitLdcInsn("mapping " + this.className + " failed to map field:");
        mv.visitVarInsn(21, 2);
        mv.visitMethodInsn(184, "java/lang/Integer", "toString", "(I)Ljava/lang/String;");
        mv.visitMethodInsn(182, "java/lang/String", "concat", "(Ljava/lang/String;)Ljava/lang/String;");
        mv.visitMethodInsn(183, exSig, "<init>", "(Ljava/lang/String;)V");
        mv.visitInsn(191);
    }
    
    private void throwExStrParam(final MethodVisitor mv, final Class<?> exCls) {
        final String exSig = Type.getInternalName(exCls);
        mv.visitTypeInsn(187, exSig);
        mv.visitInsn(89);
        mv.visitLdcInsn("mapping " + this.className + " failed to map field:");
        mv.visitVarInsn(25, 2);
        mv.visitMethodInsn(182, "java/lang/String", "concat", "(Ljava/lang/String;)Ljava/lang/String;");
        mv.visitMethodInsn(183, exSig, "<init>", "(Ljava/lang/String;)V");
        mv.visitInsn(191);
    }
    
    private void ifNotEqJmp(final MethodVisitor mv, final int param, final int value, final Label label) {
        mv.visitVarInsn(21, param);
        if (value == 0) {
            mv.visitJumpInsn(154, label);
        }
        else if (value == 1) {
            mv.visitInsn(4);
            mv.visitJumpInsn(160, label);
        }
        else if (value == 2) {
            mv.visitInsn(5);
            mv.visitJumpInsn(160, label);
        }
        else if (value == 3) {
            mv.visitInsn(6);
            mv.visitJumpInsn(160, label);
        }
        else if (value == 4) {
            mv.visitInsn(7);
            mv.visitJumpInsn(160, label);
        }
        else if (value == 5) {
            mv.visitInsn(8);
            mv.visitJumpInsn(160, label);
        }
        else {
            if (value < 6) {
                throw new RuntimeException("non supported negative values");
            }
            mv.visitIntInsn(16, value);
            mv.visitJumpInsn(160, label);
        }
    }
}
