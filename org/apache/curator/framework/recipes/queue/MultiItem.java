// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.recipes.queue;

public interface MultiItem<T>
{
    T nextItem() throws Exception;
}
