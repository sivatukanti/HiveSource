// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.api;

public interface ChildrenDeletable extends BackgroundVersionable
{
    BackgroundVersionable deletingChildrenIfNeeded();
}
