// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.diag;

class ConglomInfo
{
    private String tableID;
    private long conglomId;
    private String conglomName;
    private boolean isIndex;
    
    public ConglomInfo(final String tableID, final long conglomId, final String conglomName, final boolean isIndex) {
        this.tableID = tableID;
        this.conglomId = conglomId;
        this.conglomName = conglomName;
        this.isIndex = isIndex;
    }
    
    public String getTableID() {
        return this.tableID;
    }
    
    public long getConglomId() {
        return this.conglomId;
    }
    
    public String getConglomName() {
        return this.conglomName;
    }
    
    public boolean getIsIndex() {
        return this.isIndex;
    }
}
