// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.store.access;

import java.io.Serializable;

public interface DatabaseInstant extends Serializable
{
    boolean lessThan(final DatabaseInstant p0);
    
    boolean equals(final Object p0);
    
    DatabaseInstant next();
    
    DatabaseInstant prior();
    
    String toString();
}
