// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal.util;

import com.google.inject.internal.asm.$FieldVisitor;
import com.google.inject.internal.asm.$Attribute;
import com.google.inject.internal.asm.$Label;
import com.google.inject.internal.asm.$AnnotationVisitor;
import com.google.inject.internal.asm.$MethodVisitor;
import java.lang.reflect.Constructor;
import com.google.inject.internal.asm.$Type;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.io.IOException;
import java.io.InputStream;
import com.google.inject.internal.asm.$ClassVisitor;
import com.google.inject.internal.asm.$ClassReader;
import java.util.Map;

final class $LineNumbers
{
    private final Class type;
    private final Map<String, Integer> lines;
    private String source;
    private int firstLine;
    
    public $LineNumbers(final Class type) throws IOException {
        this.lines = (Map<String, Integer>)$Maps.newHashMap();
        this.firstLine = Integer.MAX_VALUE;
        this.type = type;
        if (!type.isArray()) {
            final InputStream in = type.getResourceAsStream("/" + type.getName().replace('.', '/') + ".class");
            if (in != null) {
                new $ClassReader(in).accept(new LineNumberReader(), 4);
            }
        }
    }
    
    public String getSource() {
        return this.source;
    }
    
    public Integer getLineNumber(final Member member) {
        $Preconditions.checkArgument(this.type == member.getDeclaringClass(), "Member %s belongs to %s, not %s", member, member.getDeclaringClass(), this.type);
        return this.lines.get(this.memberKey(member));
    }
    
    public int getFirstLine() {
        return (this.firstLine == Integer.MAX_VALUE) ? 1 : this.firstLine;
    }
    
    private String memberKey(final Member member) {
        $Preconditions.checkNotNull(member, (Object)"member");
        if (member instanceof Field) {
            return member.getName();
        }
        if (member instanceof Method) {
            return member.getName() + $Type.getMethodDescriptor((Method)member);
        }
        if (member instanceof Constructor) {
            final StringBuilder sb = new StringBuilder().append("<init>(");
            for (final Class param : ((Constructor)member).getParameterTypes()) {
                sb.append($Type.getDescriptor(param));
            }
            return sb.append(")V").toString();
        }
        throw new IllegalArgumentException("Unsupported implementation class for Member, " + member.getClass());
    }
    
    private class LineNumberReader implements $ClassVisitor, $MethodVisitor, $AnnotationVisitor
    {
        private int line;
        private String pendingMethod;
        private String name;
        
        private LineNumberReader() {
            this.line = -1;
        }
        
        public void visit(final int version, final int access, final String name, final String signature, final String superName, final String[] interfaces) {
            this.name = name;
        }
        
        public $MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature, final String[] exceptions) {
            if ((access & 0x2) != 0x0) {
                return null;
            }
            this.pendingMethod = name + desc;
            this.line = -1;
            return this;
        }
        
        public void visitSource(final String source, final String debug) {
            $LineNumbers.this.source = source;
        }
        
        public void visitLineNumber(final int line, final $Label start) {
            if (line < $LineNumbers.this.firstLine) {
                $LineNumbers.this.firstLine = line;
            }
            this.line = line;
            if (this.pendingMethod != null) {
                $LineNumbers.this.lines.put(this.pendingMethod, line);
                this.pendingMethod = null;
            }
        }
        
        public void visitFieldInsn(final int opcode, final String owner, final String name, final String desc) {
            if (opcode == 181 && this.name.equals(owner) && !$LineNumbers.this.lines.containsKey(name) && this.line != -1) {
                $LineNumbers.this.lines.put(name, this.line);
            }
        }
        
        public void visitEnd() {
        }
        
        public void visitInnerClass(final String name, final String outerName, final String innerName, final int access) {
        }
        
        public void visitOuterClass(final String owner, final String name, final String desc) {
        }
        
        public void visitAttribute(final $Attribute attr) {
        }
        
        public $FieldVisitor visitField(final int access, final String name, final String desc, final String signature, final Object value) {
            return null;
        }
        
        public $AnnotationVisitor visitAnnotation(final String desc, final boolean visible) {
            return this;
        }
        
        public $AnnotationVisitor visitAnnotation(final String name, final String desc) {
            return this;
        }
        
        public $AnnotationVisitor visitAnnotationDefault() {
            return this;
        }
        
        public $AnnotationVisitor visitParameterAnnotation(final int parameter, final String desc, final boolean visible) {
            return this;
        }
        
        public $AnnotationVisitor visitArray(final String name) {
            return this;
        }
        
        public void visitEnum(final String name, final String desc, final String value) {
        }
        
        public void visit(final String name, final Object value) {
        }
        
        public void visitCode() {
        }
        
        public void visitFrame(final int type, final int nLocal, final Object[] local, final int nStack, final Object[] stack) {
        }
        
        public void visitIincInsn(final int var, final int increment) {
        }
        
        public void visitInsn(final int opcode) {
        }
        
        public void visitIntInsn(final int opcode, final int operand) {
        }
        
        public void visitJumpInsn(final int opcode, final $Label label) {
        }
        
        public void visitLabel(final $Label label) {
        }
        
        public void visitLdcInsn(final Object cst) {
        }
        
        public void visitLocalVariable(final String name, final String desc, final String signature, final $Label start, final $Label end, final int index) {
        }
        
        public void visitLookupSwitchInsn(final $Label dflt, final int[] keys, final $Label[] labels) {
        }
        
        public void visitMaxs(final int maxStack, final int maxLocals) {
        }
        
        public void visitMethodInsn(final int opcode, final String owner, final String name, final String desc) {
        }
        
        public void visitMultiANewArrayInsn(final String desc, final int dims) {
        }
        
        public void visitTableSwitchInsn(final int min, final int max, final $Label dflt, final $Label[] labels) {
        }
        
        public void visitTryCatchBlock(final $Label start, final $Label end, final $Label handler, final String type) {
        }
        
        public void visitTypeInsn(final int opcode, final String desc) {
        }
        
        public void visitVarInsn(final int opcode, final int var) {
        }
    }
}
