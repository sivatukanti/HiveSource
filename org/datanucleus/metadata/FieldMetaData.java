// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.metadata;

import org.datanucleus.util.StringUtils;

public class FieldMetaData extends AbstractMemberMetaData
{
    public FieldMetaData(final MetaData parent, final AbstractMemberMetaData fmd) {
        super(parent, fmd);
    }
    
    public FieldMetaData(final MetaData parent, final String name) {
        super(parent, name);
    }
    
    @Override
    public String toString(final String prefix, final String indent) {
        if (this.isStatic() || this.isFinal()) {
            return "";
        }
        final StringBuffer sb = new StringBuffer();
        sb.append(prefix).append("<field name=\"" + this.name + "\"");
        if (this.persistenceModifier != null && !StringUtils.isWhitespace(this.persistenceModifier.toString())) {
            sb.append("\n").append(prefix).append("       persistence-modifier=\"" + this.persistenceModifier + "\"");
        }
        if (!StringUtils.isWhitespace(this.table)) {
            sb.append("\n").append(prefix).append("       table=\"" + this.table + "\"");
        }
        if (this.primaryKey != null && this.primaryKey) {
            sb.append("\n").append(prefix).append("       primary-key=\"" + this.primaryKey + "\"");
        }
        sb.append("\n").append(prefix).append("       null-value=\"" + this.nullValue + "\"");
        if (this.defaultFetchGroup != null && !StringUtils.isWhitespace(this.defaultFetchGroup.toString())) {
            sb.append("\n").append(prefix).append("       default-fetch-group=\"" + this.defaultFetchGroup + "\"");
        }
        if (this.embedded != null && !StringUtils.isWhitespace(this.embedded.toString())) {
            sb.append("\n").append(prefix).append("       embedded=\"" + this.embedded + "\"");
        }
        if (this.serialized != null && !StringUtils.isWhitespace(this.serialized.toString())) {
            sb.append("\n").append(prefix).append("       serialized=\"" + this.serialized + "\"");
        }
        if (this.dependent != null) {
            sb.append("\n").append(prefix).append("       dependent=\"" + this.dependent + "\"");
        }
        if (this.mappedBy != null) {
            sb.append("\n").append(prefix).append("       mapped-by=\"" + this.mappedBy + "\"");
        }
        if (this.fieldTypes != null) {
            sb.append("\n").append(prefix).append("       field-type=\"");
            for (int i = 0; i < this.fieldTypes.length; ++i) {
                sb.append(this.fieldTypes[i]);
            }
            sb.append("\"");
        }
        if (!StringUtils.isWhitespace(this.loadFetchGroup)) {
            sb.append("\n").append(prefix).append("       load-fetch-group=\"" + this.loadFetchGroup + "\"");
        }
        if (this.recursionDepth != 1 && this.recursionDepth != 0) {
            sb.append("\n").append(prefix).append("       recursion-depth=\"" + this.recursionDepth + "\"");
        }
        if (this.valueStrategy != null) {
            sb.append("\n").append(prefix).append("       value-strategy=\"" + this.valueStrategy + "\"");
        }
        if (this.sequence != null) {
            sb.append("\n").append(prefix).append("       sequence=\"" + this.sequence + "\"");
        }
        if (this.indexMetaData == null && this.indexed != null) {
            sb.append("\n").append(prefix).append("       indexed=\"" + this.indexed.toString() + "\"");
        }
        if (this.uniqueMetaData == null) {
            sb.append("\n").append(prefix).append("       unique=\"" + this.uniqueConstraint + "\"");
        }
        if (this.columnMetaData == null && this.column != null) {
            sb.append("\n").append(prefix).append("       column=\"" + this.column + "\"");
        }
        sb.append(">\n");
        if (this.containerMetaData != null) {
            if (this.containerMetaData instanceof CollectionMetaData) {
                final CollectionMetaData c = (CollectionMetaData)this.containerMetaData;
                sb.append(c.toString(prefix + indent, indent));
            }
            else if (this.containerMetaData instanceof ArrayMetaData) {
                final ArrayMetaData c2 = (ArrayMetaData)this.containerMetaData;
                sb.append(c2.toString(prefix + indent, indent));
            }
            else if (this.containerMetaData instanceof MapMetaData) {
                final MapMetaData c3 = (MapMetaData)this.containerMetaData;
                sb.append(c3.toString(prefix + indent, indent));
            }
        }
        if (this.columnMetaData != null) {
            for (int i = 0; i < this.columnMetaData.length; ++i) {
                sb.append(this.columnMetaData[i].toString(prefix + indent, indent));
            }
        }
        if (this.joinMetaData != null) {
            sb.append(this.joinMetaData.toString(prefix + indent, indent));
        }
        if (this.elementMetaData != null) {
            sb.append(this.elementMetaData.toString(prefix + indent, indent));
        }
        if (this.keyMetaData != null) {
            sb.append(this.keyMetaData.toString(prefix + indent, indent));
        }
        if (this.valueMetaData != null) {
            sb.append(this.valueMetaData.toString(prefix + indent, indent));
        }
        if (this.orderMetaData != null) {
            sb.append(this.orderMetaData.toString(prefix + indent, indent));
        }
        if (this.embeddedMetaData != null) {
            sb.append(this.embeddedMetaData.toString(prefix + indent, indent));
        }
        if (this.indexMetaData != null) {
            sb.append(this.indexMetaData.toString(prefix + indent, indent));
        }
        if (this.uniqueMetaData != null) {
            sb.append(this.uniqueMetaData.toString(prefix + indent, indent));
        }
        if (this.foreignKeyMetaData != null) {
            sb.append(this.foreignKeyMetaData.toString(prefix + indent, indent));
        }
        sb.append(super.toString(prefix + indent, indent));
        sb.append(prefix).append("</field>\n");
        return sb.toString();
    }
}
