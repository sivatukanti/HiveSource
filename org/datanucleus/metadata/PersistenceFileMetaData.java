// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.metadata;

import java.util.Iterator;
import java.util.HashSet;

public class PersistenceFileMetaData extends MetaData
{
    protected String filename;
    protected HashSet<PersistenceUnitMetaData> persistenceUnits;
    
    public PersistenceFileMetaData(final String filename) {
        this.filename = null;
        this.persistenceUnits = new HashSet<PersistenceUnitMetaData>();
        this.filename = filename;
    }
    
    public String getFilename() {
        return this.filename;
    }
    
    public int getNoOfPersistenceUnits() {
        return this.persistenceUnits.size();
    }
    
    public PersistenceUnitMetaData getPersistenceUnit(final String name) {
        for (final PersistenceUnitMetaData p : this.persistenceUnits) {
            if (p.name.equals(name)) {
                return p;
            }
        }
        return null;
    }
    
    public PersistenceUnitMetaData[] getPersistenceUnits() {
        if (this.persistenceUnits.size() == 0) {
            return null;
        }
        return this.persistenceUnits.toArray(new PersistenceUnitMetaData[this.persistenceUnits.size()]);
    }
    
    public void setFilename(final String filename) {
        this.filename = filename;
    }
    
    public void addPersistenceUnit(final PersistenceUnitMetaData pumd) {
        if (pumd == null) {
            return;
        }
        pumd.parent = this;
        for (final PersistenceUnitMetaData p : this.persistenceUnits) {
            if (pumd.getName().equals(p.getName())) {
                return;
            }
        }
        this.persistenceUnits.add(pumd);
    }
    
    public String toString(String indent) {
        if (indent == null) {
            indent = "";
        }
        final StringBuffer sb = new StringBuffer();
        sb.append("<persistence>\n");
        final Iterator<PersistenceUnitMetaData> iter = this.persistenceUnits.iterator();
        while (iter.hasNext()) {
            sb.append(iter.next().toString(indent, indent));
        }
        sb.append("</persistence>");
        return sb.toString();
    }
}
