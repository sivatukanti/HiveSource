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

public class DN2NamingFactory extends AbstractNamingFactory
{
    public DN2NamingFactory(final NucleusContext nucCtx) {
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
            final String ownerClass = mmd.getClassName(false);
            name = ownerClass + this.wordSeparator + mmd.getName();
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
                name = "DISCRIMINATOR";
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
                final ColumnMetaData idcolmds = cmd.getIdentityMetaData().getColumnMetaData();
                if (idcolmds != null) {
                    name = idcolmds.getName();
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
                name = "IDX";
            }
        }
        else if (type == ColumnType.ADAPTER_COLUMN) {
            name = "IDX";
        }
        else {
            if (type == ColumnType.FK_COLUMN) {
                throw new NucleusException("This method does not support columns of type " + type);
            }
            if (type != ColumnType.JOIN_OWNER_COLUMN) {
                throw new NucleusException("This method does not support columns of type " + type);
            }
            if (mmd.hasContainer() && mmd.getJoinMetaData() != null) {
                final ColumnMetaData[] colmds = mmd.getJoinMetaData().getColumnMetaData();
                if (colmds != null && colmds.length > position) {
                    name = colmds[position].getName();
                }
            }
            if (name == null && mmd.hasContainer()) {
                name = mmd.getName() + this.wordSeparator + "ID_OID";
            }
        }
        return this.prepareColumnNameForUse(name);
    }
}
