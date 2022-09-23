// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.metadata;

import org.datanucleus.exceptions.ClassNotResolvedException;
import org.datanucleus.exceptions.NucleusFatalUserException;
import org.datanucleus.ClassLoaderResolver;

public class ValueMetaData extends AbstractElementMetaData
{
    public ValueMetaData(final ValueMetaData vmd) {
        super(vmd);
    }
    
    public ValueMetaData() {
    }
    
    @Override
    public void populate(final ClassLoaderResolver clr, final ClassLoader primary, final MetaDataManager mmgr) {
        final AbstractMemberMetaData fmd = (AbstractMemberMetaData)this.parent;
        if (fmd.getMap() == null) {
            throw new NucleusFatalUserException("The field " + fmd.getFullFieldName() + " is defined with <value>, however no <map> definition was found.");
        }
        fmd.getMap().value.populate(fmd.getAbstractClassMetaData().getPackageName(), clr, primary, mmgr);
        final String valueType = fmd.getMap().getValueType();
        Class valueTypeClass = null;
        try {
            valueTypeClass = clr.classForName(valueType, primary);
        }
        catch (ClassNotResolvedException cnre) {
            throw new InvalidMemberMetaDataException(ValueMetaData.LOCALISER, "044150", fmd.getClassName(), fmd.getName(), valueType);
        }
        if (this.embeddedMetaData != null && (valueTypeClass.isInterface() || valueTypeClass.getName().equals("java.lang.Object"))) {
            throw new InvalidMemberMetaDataException(ValueMetaData.LOCALISER, "044152", fmd.getClassName(), fmd.getName(), valueTypeClass.getName());
        }
        if (this.embeddedMetaData == null && ((AbstractMemberMetaData)this.parent).hasMap() && ((AbstractMemberMetaData)this.parent).getMap().isEmbeddedValue() && ((AbstractMemberMetaData)this.parent).getJoinMetaData() != null && ((AbstractMemberMetaData)this.parent).getMap().valueIsPersistent()) {
            this.embeddedMetaData = new EmbeddedMetaData();
            this.embeddedMetaData.parent = this;
        }
        super.populate(clr, primary, mmgr);
    }
    
    @Override
    public String toString(final String prefix, final String indent) {
        final StringBuffer sb = new StringBuffer();
        sb.append(prefix).append("<value");
        if (this.mappedBy != null) {
            sb.append(" mapped-by=\"" + this.mappedBy + "\"");
        }
        if (this.columnName != null) {
            sb.append("\n");
            sb.append(prefix).append("       column=\"" + this.columnName + "\"");
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
        sb.append(prefix).append("</value>\n");
        return sb.toString();
    }
}
