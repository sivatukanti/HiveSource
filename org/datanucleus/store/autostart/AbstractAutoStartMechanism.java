// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.autostart;

import org.datanucleus.ClassConstants;
import org.datanucleus.util.Localiser;

public abstract class AbstractAutoStartMechanism implements AutoStartMechanism
{
    protected static final Localiser LOCALISER;
    protected Mode mode;
    protected boolean open;
    
    public AbstractAutoStartMechanism() {
        this.open = false;
    }
    
    @Override
    public Mode getMode() {
        return this.mode;
    }
    
    @Override
    public void setMode(final Mode mode) {
        this.mode = mode;
    }
    
    @Override
    public void open() {
        this.open = true;
    }
    
    @Override
    public boolean isOpen() {
        return this.open;
    }
    
    @Override
    public void close() {
        this.open = false;
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
    }
}
