// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.metadata;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AbstractConstraintMetaData extends MetaData
{
    protected String name;
    protected String table;
    protected List<String> memberNames;
    protected List<ColumnMetaData> columns;
    
    public AbstractConstraintMetaData() {
        this.memberNames = null;
        this.columns = null;
    }
    
    public AbstractConstraintMetaData(final AbstractConstraintMetaData acmd) {
        super(null, acmd);
        this.memberNames = null;
        this.columns = null;
        this.name = acmd.name;
        this.table = acmd.table;
        if (acmd.memberNames != null) {
            for (final String memberName : acmd.memberNames) {
                this.addMember(memberName);
            }
        }
        if (acmd.columns != null) {
            for (final ColumnMetaData colmd : acmd.columns) {
                this.addColumn(new ColumnMetaData(colmd));
            }
        }
    }
    
    public void addMember(final String memberName) {
        if (this.memberNames == null) {
            this.memberNames = new ArrayList<String>();
        }
        this.memberNames.add(memberName);
    }
    
    public void addColumn(final ColumnMetaData colmd) {
        if (this.columns == null) {
            this.columns = new ArrayList<ColumnMetaData>();
        }
        this.columns.add(colmd);
        colmd.parent = this;
    }
    
    public ColumnMetaData newColumnMetaData() {
        final ColumnMetaData colmd = new ColumnMetaData();
        this.addColumn(colmd);
        return colmd;
    }
    
    public final String[] getMemberNames() {
        if (this.memberNames == null) {
            return null;
        }
        return this.memberNames.toArray(new String[this.memberNames.size()]);
    }
    
    public final ColumnMetaData[] getColumnMetaData() {
        if (this.columns == null) {
            return null;
        }
        return this.columns.toArray(new ColumnMetaData[this.columns.size()]);
    }
    
    public int getNumberOfMembers() {
        return (this.memberNames != null) ? this.memberNames.size() : 0;
    }
    
    public int getNumberOfColumns() {
        return (this.columns != null) ? this.columns.size() : 0;
    }
}
