// 
// Decompiled by Procyon v0.5.36
// 

package com.google.inject.internal.cglib.core;

import com.google.inject.internal.asm.$ClassWriter;
import com.google.inject.internal.asm.$ClassVisitor;

public class $DefaultGeneratorStrategy implements $GeneratorStrategy
{
    public static final $DefaultGeneratorStrategy INSTANCE;
    
    public byte[] generate(final $ClassGenerator cg) throws Exception {
        final $ClassWriter cw = this.getClassWriter();
        this.transform(cg).generateClass(cw);
        return this.transform(cw.toByteArray());
    }
    
    protected $ClassWriter getClassWriter() throws Exception {
        return new $DebuggingClassWriter(1);
    }
    
    protected byte[] transform(final byte[] b) throws Exception {
        return b;
    }
    
    protected $ClassGenerator transform(final $ClassGenerator cg) throws Exception {
        return cg;
    }
    
    static {
        INSTANCE = new $DefaultGeneratorStrategy();
    }
}
