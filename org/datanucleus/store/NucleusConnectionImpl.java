// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store;

import org.datanucleus.ClassConstants;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.util.Localiser;

public class NucleusConnectionImpl implements NucleusConnection
{
    protected static final Localiser LOCALISER;
    private final Object nativeConnection;
    private final Runnable onClose;
    private boolean isAvailable;
    
    public NucleusConnectionImpl(final Object conn, final Runnable onClose) {
        this.isAvailable = true;
        this.nativeConnection = conn;
        this.onClose = onClose;
    }
    
    @Override
    public void close() {
        if (!this.isAvailable) {
            throw new NucleusUserException(NucleusConnectionImpl.LOCALISER.msg("046001"));
        }
        this.isAvailable = false;
        this.onClose.run();
    }
    
    @Override
    public boolean isAvailable() {
        return this.isAvailable;
    }
    
    @Override
    public Object getNativeConnection() {
        return this.nativeConnection;
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
