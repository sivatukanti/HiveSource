// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.schema.naming;

import org.datanucleus.metadata.ColumnMetaData;
import org.datanucleus.metadata.VersionMetaData;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.NucleusContext;

public class JPANamingFactory extends AbstractNamingFactory
{
    public JPANamingFactory(final NucleusContext nucCtx) {
        super(nucCtx);
    }
    
    @Override
    public String getTableName(final AbstractMemberMetaData mmd) {
        String name = null;
        AbstractMemberMetaData[] relatedMmds = null;
        if (mmd.hasContainer()) {
            if (mmd.getTable() != null) {
                name = mmd.getTable();
            }
            else {
                relatedMmds = mmd.getRelatedMemberMetaData(this.clr);
                if (relatedMmds != null && relatedMmds[0].getTable() != null) {
                    name = relatedMmds[0].getTable();
                }
            }
        }
        if (name == null) {
            String ownerClass = mmd.getClassName(false);
            String otherClass = mmd.getTypeName();
            if (mmd.hasCollection()) {
                otherClass = mmd.getCollection().getElementType();
            }
            else if (mmd.hasArray()) {
                otherClass = mmd.getArray().getElementType();
            }
            else if (mmd.hasMap()) {
                otherClass = mmd.getMap().getValueType();
            }
            if (mmd.hasCollection() && relatedMmds != null && relatedMmds[0].hasCollection() && mmd.getMappedBy() != null) {
                ownerClass = relatedMmds[0].getClassName(false);
                otherClass = relatedMmds[0].getCollection().getElementType();
            }
            otherClass = otherClass.substring(otherClass.lastIndexOf(46) + 1);
            name = ownerClass + this.wordSeparator + otherClass;
        }
        final int maxLength = this.getMaximumLengthForComponent(SchemaComponent.TABLE);
        if (maxLength > 0 && name.length() > maxLength) {
            name = AbstractNamingFactory.truncate(name, maxLength);
        }
        name = this.getNameInRequiredCase(name);
        return name;
    }
    
    @Override
    public String getColumnName(final AbstractClassMetaData cmd, final ColumnType type) {
        String name = null;
        if (type == ColumnType.DISCRIMINATOR_COLUMN) {
            name = cmd.getDiscriminatorColumnName();
            if (name == null) {
                name = "DTYPE";
            }
        }
        else if (type == ColumnType.VERSION_COLUMN) {
            final VersionMetaData vermd = cmd.getVersionMetaData();
            if (vermd != null) {
                final ColumnMetaData colmd = vermd.getColumnMetaData();
                if (colmd != null && colmd.getName() != null) {
                    name = colmd.getName();
                }
            }
            if (name == null) {
                name = "VERSION";
            }
        }
        else if (type == ColumnType.DATASTOREID_COLUMN) {
            if (cmd.getIdentityMetaData() != null) {
                final ColumnMetaData idcolmd = cmd.getIdentityMetaData().getColumnMetaData();
                if (idcolmd != null) {
                    name = idcolmd.getName();
                }
            }
            if (name == null) {
                name = cmd.getName() + this.wordSeparator + "ID";
            }
        }
        else {
            if (type != ColumnType.MULTITENANCY_COLUMN) {
                throw new NucleusException("This method does not support columns of type " + type);
            }
            if (cmd.hasExtension("multitenancy-column-name")) {
                name = cmd.getValueForExtension("multitenancy-column-name");
            }
            if (name == null) {
                name = "TENANT" + this.wordSeparator + "ID";
            }
        }
        return this.prepareColumnNameForUse(name);
    }
    
    @Override
    public String getColumnName(final AbstractMemberMetaData mmd, final ColumnType type, final int position) {
        String name = null;
        if (type == ColumnType.COLUMN) {
            final ColumnMetaData[] colmds = mmd.getColumnMetaData();
            if (colmds != null && colmds.length > position) {
                name = colmds[position].getName();
            }
            if (name == null) {
                name = mmd.getName();
            }
        }
        else if (type == ColumnType.INDEX_COLUMN) {
            if (mmd.getOrderMetaData() != null) {
                final ColumnMetaData[] colmds = mmd.getOrderMetaData().getColumnMetaData();
                if (colmds != null && colmds.length > position) {
                    name = colmds[position].getName();
                }
            }
            if (name == null) {
                name = mmd.getName() + this.wordSeparator + "ORDER";
            }
        }
        else {
            if (type != ColumnType.ADAPTER_COLUMN) {
                throw new NucleusException("This method does not support columns of type " + type);
            }
            name = "IDX";
        }
        return this.prepareColumnNameForUse(name);
    }
}
