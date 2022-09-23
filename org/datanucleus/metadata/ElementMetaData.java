// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.metadata;

import org.datanucleus.ClassLoaderResolver;

public class ElementMetaData extends AbstractElementMetaData
{
    public ElementMetaData(final ElementMetaData emd) {
        super(emd);
    }
    
    public ElementMetaData() {
    }
    
    @Override
    public void populate(final ClassLoaderResolver clr, final ClassLoader primary, final MetaDataManager mmgr) {
        final AbstractMemberMetaData fmd = (AbstractMemberMetaData)this.parent;
        if (fmd.hasCollection()) {
            fmd.getCollection().element.populate(fmd.getAbstractClassMetaData().getPackageName(), clr, primary, mmgr);
        }
        else if (fmd.hasArray()) {
            fmd.getArray().element.populate(fmd.getAbstractClassMetaData().getPackageName(), clr, primary, mmgr);
        }
        if (this.embeddedMetaData == null && ((AbstractMemberMetaData)this.parent).hasCollection() && ((AbstractMemberMetaData)this.parent).getCollection().isEmbeddedElement() && ((AbstractMemberMetaData)this.parent).getJoinMetaData() != null && ((AbstractMemberMetaData)this.parent).getCollection().elementIsPersistent()) {
            this.embeddedMetaData = new EmbeddedMetaData();
            this.embeddedMetaData.parent = this;
        }
        super.populate(clr, primary, mmgr);
    }
    
    @Override
    public String toString(final String prefix, final String indent) {
        final StringBuffer sb = new StringBuffer();
        sb.append(prefix).append("<element");
        if (this.mappedBy != null) {
            sb.append(" mapped-by=\"" + this.mappedBy + "\"");
        }
        if (this.columnName != null) {
            sb.append("\n");
            sb.append(prefix).append("          column=\"" + this.columnName + "\"");
        }
        sb.append(">\n");
        for (int i = 0; i < this.columns.size(); ++i) {
            final ColumnMetaData colmd = this.columns.get(i);
            sb.append(colmd.toString(prefix + indent, indent));
        }
        if (this.indexMetaData != null) {
            sb.append(this.indexMetaData.toString(prefix + indent, indent));
        }
        if (this.uniqueMetaData != null) {
            sb.append(this.uniqueMetaData.toString(prefix + indent, indent));
        }
        if (this.embeddedMetaData != null) {
            sb.append(this.embeddedMetaData.toString(prefix + indent, indent));
        }
        if (this.foreignKeyMetaData != null) {
            sb.append(this.foreignKeyMetaData.toString(prefix + indent, indent));
        }
        sb.append(super.toString(prefix + indent, indent));
        sb.append(prefix).append("</element>\n");
        return sb.toString();
    }
}
