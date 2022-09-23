// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.metadata;

import java.util.Iterator;
import org.datanucleus.util.StringUtils;

public class ForeignKeyMetaData extends AbstractConstraintMetaData implements ColumnMetaDataContainer
{
    protected boolean unique;
    protected boolean deferred;
    protected ForeignKeyAction deleteAction;
    protected ForeignKeyAction updateAction;
    protected String fkDefinition;
    protected boolean fkDefinitionApplies;
    
    public ForeignKeyMetaData() {
        this.unique = false;
        this.deferred = false;
        this.fkDefinition = null;
        this.fkDefinitionApplies = false;
    }
    
    public ForeignKeyMetaData(final ForeignKeyMetaData fkmd) {
        super(fkmd);
        this.unique = false;
        this.deferred = false;
        this.fkDefinition = null;
        this.fkDefinitionApplies = false;
        this.unique = fkmd.unique;
        this.deferred = fkmd.deferred;
        this.deleteAction = fkmd.deleteAction;
        this.updateAction = fkmd.updateAction;
    }
    
    public final String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = (StringUtils.isWhitespace(name) ? null : name);
    }
    
    public final String getTable() {
        return this.table;
    }
    
    public void setTable(final String table) {
        this.table = (StringUtils.isWhitespace(table) ? null : table);
    }
    
    public final boolean isDeferred() {
        return this.deferred;
    }
    
    public ForeignKeyMetaData setDeferred(final boolean deferred) {
        this.deferred = deferred;
        return this;
    }
    
    public ForeignKeyMetaData setDeferred(final String deferred) {
        if (!StringUtils.isWhitespace(deferred)) {
            this.deferred = Boolean.parseBoolean(deferred);
        }
        return this;
    }
    
    public final ForeignKeyAction getDeleteAction() {
        return this.deleteAction;
    }
    
    public void setDeleteAction(final ForeignKeyAction deleteAction) {
        this.deleteAction = deleteAction;
    }
    
    public final boolean isUnique() {
        return this.unique;
    }
    
    public ForeignKeyMetaData setUnique(final boolean unique) {
        this.unique = unique;
        return this;
    }
    
    public ForeignKeyMetaData setUnique(final String unique) {
        if (!StringUtils.isWhitespace(unique)) {
            this.deferred = Boolean.parseBoolean(unique);
        }
        return this;
    }
    
    public final ForeignKeyAction getUpdateAction() {
        return this.updateAction;
    }
    
    public ForeignKeyMetaData setUpdateAction(final ForeignKeyAction updateAction) {
        this.updateAction = updateAction;
        return this;
    }
    
    public void setFkDefinition(final String def) {
        if (StringUtils.isWhitespace(def)) {
            return;
        }
        this.fkDefinition = def;
        this.fkDefinitionApplies = true;
        this.updateAction = null;
        this.deleteAction = null;
    }
    
    public String getFkDefinition() {
        return this.fkDefinition;
    }
    
    public void setFkDefinitionApplies(final boolean flag) {
        this.fkDefinitionApplies = flag;
    }
    
    public boolean getFkDefinitionApplies() {
        return this.fkDefinitionApplies;
    }
    
    @Override
    public String toString(final String prefix, final String indent) {
        if (!StringUtils.isWhitespace(this.fkDefinition)) {
            return "<foreign-key name=\"" + this.name + "\" definition=\"" + this.fkDefinition + "\" definition-applies=" + this.fkDefinitionApplies + "/>";
        }
        final StringBuffer sb = new StringBuffer();
        sb.append(prefix).append("<foreign-key deferred=\"" + this.deferred + "\"\n");
        sb.append(prefix).append("       unique=\"" + this.unique + "\"");
        if (this.updateAction != null) {
            sb.append("\n").append(prefix).append("       update-action=\"" + this.updateAction + "\"");
        }
        if (this.deleteAction != null) {
            sb.append("\n").append(prefix).append("       delete-action=\"" + this.deleteAction + "\"");
        }
        if (this.table != null) {
            sb.append("\n").append(prefix).append("       table=\"" + this.table + "\"");
        }
        if (this.name != null) {
            sb.append("\n").append(prefix).append("       name=\"" + this.name + "\"");
        }
        sb.append(">\n");
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
        sb.append(prefix).append("</foreign-key>\n");
        return sb.toString();
    }
}
