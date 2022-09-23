// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.services.daemon;

import org.apache.derby.iapi.error.StandardException;

public interface DaemonFactory
{
    DaemonService createNewDaemon(final String p0) throws StandardException;
}
