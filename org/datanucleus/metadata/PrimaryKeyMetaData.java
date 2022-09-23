// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.metadata;

import org.datanucleus.util.StringUtils;
import org.datanucleus.ClassLoaderResolver;
import java.util.ArrayList;
import java.util.List;

public class PrimaryKeyMetaData extends MetaData implements ColumnMetaDataContainer
{
    protected String name;
    protected String columnName;
    protected ColumnMetaData[] columnMetaData;
    protected List columns;
    
    public PrimaryKeyMetaData() {
        this.name = null;
        this.columnName = null;
        this.columnMetaData = null;
        this.columns = new ArrayList();
    }
    
    @Override
    public void initialise(final ClassLoaderResolver clr, final MetaDataManager mmgr) {
        if (this.columns.size() == 0 && this.columnName != null) {
            this.columnMetaData = new ColumnMetaData[1];
            (this.columnMetaData[0] = new ColumnMetaData()).setName(this.columnName);
            this.columnMetaData[0].parent = this;
            this.columnMetaData[0].initialise(clr, mmgr);
        }
        else {
            this.columnMetaData = new ColumnMetaData[this.columns.size()];
            for (int i = 0; i < this.columnMetaData.length; ++i) {
                (this.columnMetaData[i] = this.columns.get(i)).initialise(clr, mmgr);
            }
        }
        this.columns.clear();
        this.columns = null;
        this.setInitialised();
    }
    
    public String getName() {
        return this.name;
    }
    
    public PrimaryKeyMetaData setName(final String name) {
        this.name = (StringUtils.isWhitespace(name) ? null : name);
        return this;
    }
    
    public PrimaryKeyMetaData setColumnName(final String name) {
        this.columnName = (StringUtils.isWhitespace(name) ? null : name);
        return this;
    }
    
    public String getColumnName() {
        return this.columnName;
    }
    
    @Override
    public void addColumn(final ColumnMetaData colmd) {
        this.columns.add(colmd);
        colmd.parent = this;
    }
    
    public ColumnMetaData newColumnMetadata() {
        final ColumnMetaData colmd = new ColumnMetaData();
        this.addColumn(colmd);
        return colmd;
    }
    
    @Override
    public final ColumnMetaData[] getColumnMetaData() {
        return this.columnMetaData;
    }
    
    @Override
    public String toString(final String prefix, final String indent) {
        final StringBuffer sb = new StringBuffer();
        sb.append(prefix).append("<primary-key" + ((this.name != null) ? (" name=\"" + this.name + "\"") : "") + ((this.columnName != null) ? (" column=\"" + this.columnName + "\"") : "") + ">\n");
        if (this.columnMetaData != null) {
            for (int i = 0; i < this.columnMetaData.length; ++i) {
                sb.append(this.columnMetaData[i].toString(prefix + indent, indent));
            }
        }
        sb.append(super.toString(prefix + indent, indent));
        sb.append(prefix).append("</primary-key>\n");
        return sb.toString();
    }
}
