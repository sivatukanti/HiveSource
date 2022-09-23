// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.store.raw.xact;

import org.apache.derby.iapi.store.raw.log.LogInstant;

class SavePoint
{
    private LogInstant savePoint;
    private final String name;
    private Object kindOfSavepoint;
    
    SavePoint(final String name, final Object kindOfSavepoint) {
        this.name = name;
        this.kindOfSavepoint = kindOfSavepoint;
    }
    
    void setSavePoint(final LogInstant savePoint) {
        this.savePoint = savePoint;
    }
    
    LogInstant getSavePoint() {
        return this.savePoint;
    }
    
    String getName() {
        return this.name;
    }
    
    boolean isThisUserDefinedsavepoint() {
        return this.kindOfSavepoint != null;
    }
    
    Object getKindOfSavepoint() {
        return this.kindOfSavepoint;
    }
}
