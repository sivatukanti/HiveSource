// 
// Decompiled by Procyon v0.5.36
// 

package net.sf.cglib.proxy;

import net.sf.cglib.core.Signature;
import net.sf.cglib.core.MethodInfo;
import net.sf.cglib.core.CodeEmitter;
import java.util.List;
import net.sf.cglib.core.ClassEmitter;

interface CallbackGenerator
{
    void generate(final ClassEmitter p0, final Context p1, final List p2) throws Exception;
    
    void generateStatic(final CodeEmitter p0, final Context p1, final List p2) throws Exception;
    
    public interface Context
    {
        ClassLoader getClassLoader();
        
        CodeEmitter beginMethod(final ClassEmitter p0, final MethodInfo p1);
        
        int getOriginalModifiers(final MethodInfo p0);
        
        int getIndex(final MethodInfo p0);
        
        void emitCallback(final CodeEmitter p0, final int p1);
        
        Signature getImplSignature(final MethodInfo p0);
    }
}
