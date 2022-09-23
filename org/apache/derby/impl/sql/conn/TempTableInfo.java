// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.conn;

import org.apache.derby.iapi.sql.dictionary.TableDescriptor;

class TempTableInfo
{
    private TableDescriptor td;
    private int declaredInSavepointLevel;
    private int droppededInSavepointLevel;
    private int dataModifiedInSavepointLevel;
    
    TempTableInfo(final TableDescriptor td, final int declaredInSavepointLevel) {
        this.td = td;
        this.declaredInSavepointLevel = declaredInSavepointLevel;
        this.droppededInSavepointLevel = -1;
        this.dataModifiedInSavepointLevel = -1;
    }
    
    TableDescriptor getTableDescriptor() {
        return this.td;
    }
    
    void setTableDescriptor(final TableDescriptor td) {
        this.td = td;
    }
    
    boolean matches(final String anObject) {
        return this.td.getName().equals(anObject) && this.droppededInSavepointLevel == -1;
    }
    
    int getModifiedInSavepointLevel() {
        return this.dataModifiedInSavepointLevel;
    }
    
    void setModifiedInSavepointLevel(final int dataModifiedInSavepointLevel) {
        this.dataModifiedInSavepointLevel = dataModifiedInSavepointLevel;
    }
    
    int getDeclaredInSavepointLevel() {
        return this.declaredInSavepointLevel;
    }
    
    void setDeclaredInSavepointLevel(final int declaredInSavepointLevel) {
        this.declaredInSavepointLevel = declaredInSavepointLevel;
    }
    
    int getDroppedInSavepointLevel() {
        return this.droppededInSavepointLevel;
    }
    
    public void setDroppedInSavepointLevel(final int droppededInSavepointLevel) {
        this.droppededInSavepointLevel = droppededInSavepointLevel;
    }
}
