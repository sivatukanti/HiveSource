// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.store.raw;

import org.apache.derby.iapi.error.StandardException;

public interface Corruptable
{
    StandardException markCorrupt(final StandardException p0);
}
