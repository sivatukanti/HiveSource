// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.metadata;

import java.util.Iterator;
import org.datanucleus.util.StringUtils;

public class UniqueMetaData extends AbstractConstraintMetaData implements ColumnMetaDataContainer
{
    boolean deferred;
    
    public UniqueMetaData(final UniqueMetaData umd) {
        super(umd);
        this.deferred = false;
        this.deferred = umd.deferred;
    }
    
    public UniqueMetaData() {
        this.deferred = false;
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
    
    public final boolean isDeferred() {
        return this.deferred;
    }
    
    public UniqueMetaData setDeferred(final boolean deferred) {
        this.deferred = deferred;
        return this;
    }
    
    @Override
    public String toString(final String prefix, final String indent) {
        final StringBuffer sb = new StringBuffer();
        sb.append(prefix).append("<unique");
        if (this.table != null) {
            sb.append(" table=\"" + this.table + "\"");
        }
        if (this.deferred) {
            sb.append(" deferred=\"true\"");
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
        sb.append(prefix).append("</unique>\n");
        return sb.toString();
    }
}
