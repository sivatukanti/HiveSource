// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.mapping.java;

import org.datanucleus.store.rdbms.table.DatastoreClass;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.store.rdbms.table.ColumnCreator;
import org.datanucleus.metadata.ColumnMetaData;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.metadata.InheritanceStrategy;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.store.rdbms.table.Table;
import org.datanucleus.metadata.AbstractMemberMetaData;

public class SubclassPCMapping extends MultiPersistableMapping
{
    @Override
    public void initialize(final AbstractMemberMetaData fmd, final Table table, final ClassLoaderResolver clr) {
        super.initialize(fmd, table, clr);
        this.prepareDatastoreMapping(clr);
    }
    
    protected void prepareDatastoreMapping(final ClassLoaderResolver clr) {
        if (this.roleForMember != 4) {
            if (this.roleForMember != 3) {
                if (this.roleForMember != 5) {
                    if (this.roleForMember != 6) {
                        final AbstractClassMetaData refCmd = this.storeMgr.getNucleusContext().getMetaDataManager().getMetaDataForClass(this.mmd.getType(), clr);
                        if (refCmd.getInheritanceMetaData().getStrategy() != InheritanceStrategy.SUBCLASS_TABLE) {
                            throw new NucleusUserException(SubclassPCMapping.LOCALISER.msg("020185", this.mmd.getFullFieldName()));
                        }
                        final AbstractClassMetaData[] subclassCmds = this.storeMgr.getClassesManagingTableForClass(refCmd, clr);
                        boolean pk = false;
                        if (subclassCmds.length > 1) {
                            pk = false;
                        }
                        boolean nullable = true;
                        if (subclassCmds.length > 1) {
                            nullable = true;
                        }
                        int colPos = 0;
                        for (int i = 0; i < subclassCmds.length; ++i) {
                            final Class type = clr.classForName(subclassCmds[i].getFullClassName());
                            final DatastoreClass dc = this.storeMgr.getDatastoreClass(subclassCmds[i].getFullClassName(), clr);
                            final JavaTypeMapping m = dc.getIdMapping();
                            ColumnMetaData[] columnMetaDataForType = null;
                            if (this.mmd.getColumnMetaData() != null && this.mmd.getColumnMetaData().length > 0) {
                                if (this.mmd.getColumnMetaData().length < colPos + m.getNumberOfDatastoreMappings()) {
                                    throw new NucleusUserException(SubclassPCMapping.LOCALISER.msg("020186", this.mmd.getFullFieldName(), "" + this.mmd.getColumnMetaData().length, "" + (colPos + m.getNumberOfDatastoreMappings())));
                                }
                                columnMetaDataForType = new ColumnMetaData[m.getNumberOfDatastoreMappings()];
                                System.arraycopy(this.mmd.getColumnMetaData(), colPos, columnMetaDataForType, 0, columnMetaDataForType.length);
                                colPos += columnMetaDataForType.length;
                            }
                            ColumnCreator.createColumnsForField(type, this, this.table, this.storeMgr, this.mmd, pk, nullable, false, false, 2, columnMetaDataForType, clr, true);
                            if (NucleusLogger.DATASTORE.isInfoEnabled()) {
                                NucleusLogger.DATASTORE.info(SubclassPCMapping.LOCALISER.msg("020187", type, this.mmd.getName()));
                            }
                        }
                    }
                }
            }
        }
    }
    
    @Override
    public Class getJavaType() {
        return null;
    }
}
