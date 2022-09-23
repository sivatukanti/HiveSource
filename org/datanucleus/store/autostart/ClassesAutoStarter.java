// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.autostart;

import org.datanucleus.store.exceptions.DatastoreInitialisationException;
import org.datanucleus.metadata.MetaData;
import org.datanucleus.store.StoreData;
import java.util.StringTokenizer;
import java.util.HashSet;
import java.util.Collection;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.store.StoreManager;

public class ClassesAutoStarter extends AbstractAutoStartMechanism
{
    protected String classNames;
    
    public ClassesAutoStarter(final StoreManager storeMgr, final ClassLoaderResolver clr) {
        this.classNames = storeMgr.getStringProperty("datanucleus.autoStartClassNames");
    }
    
    @Override
    public Collection getAllClassData() throws DatastoreInitialisationException {
        final Collection classes = new HashSet();
        if (this.classNames == null) {
            return classes;
        }
        final StringTokenizer tokeniser = new StringTokenizer(this.classNames, ",");
        while (tokeniser.hasMoreTokens()) {
            classes.add(new StoreData(tokeniser.nextToken().trim(), null, 1, null));
        }
        return classes;
    }
    
    @Override
    public void addClass(final StoreData data) {
    }
    
    @Override
    public void deleteClass(final String className) {
    }
    
    @Override
    public void deleteAllClasses() {
    }
    
    @Override
    public String getStorageDescription() {
        return ClassesAutoStarter.LOCALISER.msg("034100");
    }
}
