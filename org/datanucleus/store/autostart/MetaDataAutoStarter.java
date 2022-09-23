// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.autostart;

import org.datanucleus.store.exceptions.DatastoreInitialisationException;
import org.datanucleus.metadata.ClassMetaData;
import org.datanucleus.metadata.PackageMetaData;
import java.util.Iterator;
import org.datanucleus.metadata.MetaData;
import org.datanucleus.store.StoreData;
import org.datanucleus.metadata.FileMetaData;
import java.util.Collections;
import java.util.HashSet;
import java.util.Collection;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.store.StoreManager;

public class MetaDataAutoStarter extends AbstractAutoStartMechanism
{
    protected String metaDataFiles;
    protected StoreManager storeMgr;
    protected ClassLoaderResolver clr;
    protected Collection classes;
    
    public MetaDataAutoStarter(final StoreManager storeMgr, final ClassLoaderResolver clr) {
        this.classes = new HashSet();
        this.metaDataFiles = storeMgr.getStringProperty("datanucleus.autoStartMetaDataFiles");
        this.storeMgr = storeMgr;
        this.clr = clr;
    }
    
    @Override
    public Collection getAllClassData() throws DatastoreInitialisationException {
        if (this.metaDataFiles == null) {
            return Collections.EMPTY_SET;
        }
        final Collection fileMetaData = this.storeMgr.getNucleusContext().getMetaDataManager().loadFiles(this.metaDataFiles.split(","), this.clr);
        for (final FileMetaData filemd : fileMetaData) {
            for (int i = 0; i < filemd.getNoOfPackages(); ++i) {
                final PackageMetaData pmd = filemd.getPackage(i);
                for (int j = 0; j < pmd.getNoOfClasses(); ++j) {
                    final ClassMetaData cmd = pmd.getClass(j);
                    this.classes.add(new StoreData(cmd.getFullClassName().trim(), null, 1, null));
                }
            }
        }
        return this.classes;
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
        return MetaDataAutoStarter.LOCALISER.msg("034150");
    }
}
