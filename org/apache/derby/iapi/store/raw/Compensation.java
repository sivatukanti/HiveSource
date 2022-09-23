// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.store.raw;

public interface Compensation extends Loggable
{
    void setUndoOp(final Undoable p0);
}
