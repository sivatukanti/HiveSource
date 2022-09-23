// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.metadata;

import java.util.Iterator;
import org.datanucleus.util.StringUtils;

public class IndexMetaData extends AbstractConstraintMetaData implements ColumnMetaDataContainer
{
    boolean unique;
    
    public IndexMetaData(final IndexMetaData imd) {
        super(imd);
        this.unique = false;
        this.unique = imd.unique;
    }
    
    public IndexMetaData() {
        this.unique = false;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = (StringUtils.isWhitespace(name) ? null : name);
    }
    
    public String getTable() {
        return this.table;
    }
    
    public void setTable(final String table) {
        this.table = (StringUtils.isWhitespace(table) ? null : table);
    }
    
    public final boolean isUnique() {
        return this.unique;
    }
    
    public IndexMetaData setUnique(final boolean unique) {
        this.unique = unique;
        return this;
    }
    
    @Override
    public String toString(final String prefix, final String indent) {
        final StringBuffer sb = new StringBuffer();
        sb.append(prefix).append("<index unique=\"" + this.unique + "\"");
        if (this.table != null) {
            sb.append(" table=\"" + this.table + "\"");
        }
        sb.append((this.name != null) ? (" name=\"" + this.name + "\">\n") : ">\n");
        if (this.memberNames != null) {
            for (final String memberName : this.memberNames) {
                sb.append(prefix).append(indent).append("<field name=\"" + memberName + "\"/>");
            }
        }
        if (this.columns != null) {
            for (final ColumnMetaData colmd : this.columns) {
                sb.append(colmd.toString(prefix + indent, indent));
            }
        }
        sb.append(super.toString(prefix + indent, indent));
        sb.append(prefix).append("</index>\n");
        return sb.toString();
    }
}
