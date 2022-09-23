// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.metadata;

import org.datanucleus.exceptions.ClassNotResolvedException;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.ClassLoaderResolver;

public class KeyMetaData extends AbstractElementMetaData
{
    public KeyMetaData(final KeyMetaData kmd) {
        super(kmd);
    }
    
    public KeyMetaData() {
    }
    
    @Override
    public void populate(final ClassLoaderResolver clr, final ClassLoader primary, final MetaDataManager mmgr) {
        final AbstractMemberMetaData fmd = (AbstractMemberMetaData)this.parent;
        if (fmd.getMap() == null) {
            throw new NucleusUserException("The field " + fmd.getFullFieldName() + " is defined with <key>, however no <map> definition was found.").setFatal();
        }
        fmd.getMap().key.populate(fmd.getAbstractClassMetaData().getPackageName(), clr, primary, mmgr);
        final String keyType = fmd.getMap().getKeyType();
        Class keyTypeClass = null;
        try {
            keyTypeClass = clr.classForName(keyType, primary);
        }
        catch (ClassNotResolvedException cnre) {
            throw new InvalidMemberMetaDataException(KeyMetaData.LOCALISER, "044147", fmd.getClassName(), fmd.getName(), keyType);
        }
        if (this.embeddedMetaData != null && (keyTypeClass.isInterface() || keyTypeClass.getName().equals("java.lang.Object"))) {
            throw new InvalidMemberMetaDataException(KeyMetaData.LOCALISER, "044152", fmd.getClassName(), fmd.getName(), keyTypeClass.getName());
        }
        if (this.embeddedMetaData == null && ((AbstractMemberMetaData)this.parent).hasMap() && ((AbstractMemberMetaData)this.parent).getMap().isEmbeddedKey() && ((AbstractMemberMetaData)this.parent).getJoinMetaData() != null && ((AbstractMemberMetaData)this.parent).getMap().keyIsPersistent()) {
            this.embeddedMetaData = new EmbeddedMetaData();
            this.embeddedMetaData.parent = this;
        }
        super.populate(clr, primary, mmgr);
    }
    
    @Override
    public String toString(final String prefix, final String indent) {
        final StringBuffer sb = new StringBuffer();
        sb.append(prefix).append("<key");
        if (this.mappedBy != null) {
            sb.append(" mapped-by=\"" + this.mappedBy + "\"");
        }
        if (this.columnName != null) {
            sb.append("\n");
            sb.append(prefix).append("     column=\"" + this.columnName + "\"");
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
        sb.append(prefix).append("</key>\n");
        return sb.toString();
    }
}
