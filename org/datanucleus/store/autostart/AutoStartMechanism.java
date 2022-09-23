// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.autostart;

import org.datanucleus.store.StoreData;
import org.datanucleus.store.exceptions.DatastoreInitialisationException;
import java.util.Collection;

public interface AutoStartMechanism
{
    Mode getMode();
    
    void setMode(final Mode p0);
    
    Collection getAllClassData() throws DatastoreInitialisationException;
    
    void open();
    
    void close();
    
    boolean isOpen();
    
    void addClass(final StoreData p0);
    
    void deleteClass(final String p0);
    
    void deleteAllClasses();
    
    String getStorageDescription();
    
    public enum Mode
    {
        NONE, 
        QUIET, 
        CHECKED, 
        IGNORED;
    }
}
