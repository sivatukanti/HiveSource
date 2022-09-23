// 
// Decompiled by Procyon v0.5.36
// 

package org.objectweb.asm.tree;

import java.util.ArrayList;

final class MethodNode$1 extends ArrayList
{
    private final /* synthetic */ MethodNode this$0;
    
    MethodNode$1(final MethodNode this$0, final int initialCapacity) {
        super(initialCapacity);
        this.this$0 = this$0;
    }
    
    public boolean add(final Object o) {
        this.this$0.annotationDefault = o;
        return super.add(o);
    }
}
