// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.store.raw.log;

import org.apache.derby.iapi.store.access.DatabaseInstant;
import org.apache.derby.iapi.services.io.Formatable;

public interface LogInstant extends Formatable, DatabaseInstant
{
    public static final long INVALID_LOG_INSTANT = 0L;
}
