// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.mapping;

import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.store.rdbms.mapping.java.MultiMapping;
import org.datanucleus.exceptions.NucleusUserException;
import java.util.HashMap;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.metadata.ColumnMetaDataContainer;
import org.datanucleus.metadata.ColumnMetaData;
import org.datanucleus.store.rdbms.identifier.DatastoreIdentifier;
import java.util.Map;
import org.datanucleus.util.Localiser;

public class CorrespondentColumnsMapper
{
    protected static final Localiser LOCALISER;
    private final Map<DatastoreIdentifier, ColumnMetaData> columnMetaDataBySideBIdentifier;
    private final String columnsName;
    
    public CorrespondentColumnsMapper(final ColumnMetaDataContainer columnContainer, final ColumnMetaData[] colmds, final JavaTypeMapping mappingSideB, final boolean updateContainer) {
        this.columnMetaDataBySideBIdentifier = new HashMap<DatastoreIdentifier, ColumnMetaData>();
        if (columnContainer != null && colmds != null) {
            final int noOfUserColumns = colmds.length;
            final StringBuffer str = new StringBuffer("Columns [");
            for (int i = 0; i < noOfUserColumns; ++i) {
                str.append(colmds[i].getName());
                if (i < noOfUserColumns - 1) {
                    str.append(", ");
                }
            }
            str.append("]");
            this.columnsName = str.toString();
            if (noOfUserColumns > mappingSideB.getNumberOfDatastoreMappings()) {
                throw new NucleusUserException(CorrespondentColumnsMapper.LOCALISER.msg("020003", this.columnsName, "" + noOfUserColumns, "" + mappingSideB.getNumberOfDatastoreMappings())).setFatal();
            }
            final DatastoreIdentifier[] sideBidentifiers = new DatastoreIdentifier[mappingSideB.getNumberOfDatastoreMappings()];
            final boolean[] sideButilised = new boolean[mappingSideB.getNumberOfDatastoreMappings()];
            for (int j = 0; j < mappingSideB.getNumberOfDatastoreMappings(); ++j) {
                sideBidentifiers[j] = mappingSideB.getDatastoreMapping(j).getColumn().getIdentifier();
                sideButilised[j] = false;
            }
            final JavaTypeMapping[] sideBidMappings = ((MultiMapping)mappingSideB).getJavaTypeMapping();
            for (int k = 0; k < noOfUserColumns; ++k) {
                String targetColumnName = colmds[k].getTarget();
                if (targetColumnName == null) {
                    final String targetFieldName = colmds[k].getTargetMember();
                    if (targetFieldName != null) {
                        for (int l = 0; l < sideBidMappings.length; ++l) {
                            if (sideBidMappings[l].getMemberMetaData().getName().equals(targetFieldName)) {
                                targetColumnName = sideBidMappings[l].getDatastoreMapping(0).getColumn().getIdentifier().getIdentifierName();
                                break;
                            }
                        }
                    }
                }
                if (targetColumnName != null) {
                    boolean targetExists = false;
                    for (int l = 0; l < sideBidentifiers.length; ++l) {
                        if (sideBidentifiers[l].getIdentifierName().equalsIgnoreCase(targetColumnName) && !sideButilised[l]) {
                            this.putColumn(sideBidentifiers[l], colmds[k]);
                            sideButilised[l] = true;
                            targetExists = true;
                            break;
                        }
                    }
                    if (!targetExists) {
                        throw new NucleusUserException(CorrespondentColumnsMapper.LOCALISER.msg("020004", this.columnsName, colmds[k].getName(), targetColumnName)).setFatal();
                    }
                }
            }
            for (int k = 0; k < colmds.length; ++k) {
                if (colmds[k].getTarget() == null) {
                    for (int m = 0; m < sideBidentifiers.length; ++m) {
                        if (!sideButilised[m]) {
                            this.putColumn(sideBidentifiers[m], colmds[k]);
                            sideButilised[m] = true;
                            break;
                        }
                    }
                }
            }
            for (int k = colmds.length; k < mappingSideB.getNumberOfDatastoreMappings(); ++k) {
                DatastoreIdentifier sideBidentifier = null;
                for (int j2 = 0; j2 < sideBidentifiers.length; ++j2) {
                    if (!sideButilised[j2]) {
                        sideBidentifier = sideBidentifiers[j2];
                        sideButilised[j2] = true;
                        break;
                    }
                }
                if (sideBidentifier == null) {
                    throw new NucleusUserException(CorrespondentColumnsMapper.LOCALISER.msg("020005", this.columnsName, "" + k)).setFatal();
                }
                final ColumnMetaData colmd = new ColumnMetaData();
                if (updateContainer) {
                    columnContainer.addColumn(colmd);
                }
                this.putColumn(sideBidentifier, colmd);
            }
        }
        else {
            this.columnsName = null;
            for (int i2 = 0; i2 < mappingSideB.getNumberOfDatastoreMappings(); ++i2) {
                final DatastoreIdentifier sideBidentifier2 = mappingSideB.getDatastoreMapping(i2).getColumn().getIdentifier();
                final ColumnMetaData colmd2 = new ColumnMetaData();
                this.putColumn(sideBidentifier2, colmd2);
            }
        }
    }
    
    public CorrespondentColumnsMapper(final ColumnMetaDataContainer columnContainer, final JavaTypeMapping mappingSideB, final boolean updateContainer) {
        this.columnMetaDataBySideBIdentifier = new HashMap<DatastoreIdentifier, ColumnMetaData>();
        if (columnContainer != null) {
            final int noOfUserColumns = columnContainer.getColumnMetaData().length;
            final ColumnMetaData[] colmds = columnContainer.getColumnMetaData();
            final StringBuffer str = new StringBuffer("Columns [");
            for (int i = 0; i < noOfUserColumns; ++i) {
                str.append(colmds[i].getName());
                if (i < noOfUserColumns - 1) {
                    str.append(", ");
                }
            }
            str.append("]");
            this.columnsName = str.toString();
            if (noOfUserColumns > mappingSideB.getNumberOfDatastoreMappings()) {
                throw new NucleusUserException(CorrespondentColumnsMapper.LOCALISER.msg("020003", this.columnsName, "" + noOfUserColumns, "" + mappingSideB.getNumberOfDatastoreMappings())).setFatal();
            }
            final DatastoreIdentifier[] sideBidentifiers = new DatastoreIdentifier[mappingSideB.getNumberOfDatastoreMappings()];
            final boolean[] sideButilised = new boolean[mappingSideB.getNumberOfDatastoreMappings()];
            for (int j = 0; j < mappingSideB.getNumberOfDatastoreMappings(); ++j) {
                sideBidentifiers[j] = mappingSideB.getDatastoreMapping(j).getColumn().getIdentifier();
                sideButilised[j] = false;
            }
            final JavaTypeMapping[] sideBidMappings = ((MultiMapping)mappingSideB).getJavaTypeMapping();
            for (int k = 0; k < noOfUserColumns; ++k) {
                String targetColumnName = colmds[k].getTarget();
                if (targetColumnName == null) {
                    final String targetFieldName = colmds[k].getTargetMember();
                    if (targetFieldName != null) {
                        for (int l = 0; l < sideBidMappings.length; ++l) {
                            if (sideBidMappings[l].getMemberMetaData().getName().equals(targetFieldName)) {
                                targetColumnName = sideBidMappings[l].getDatastoreMapping(0).getColumn().getIdentifier().getIdentifierName();
                                break;
                            }
                        }
                    }
                }
                if (targetColumnName != null) {
                    boolean targetExists = false;
                    for (int l = 0; l < sideBidentifiers.length; ++l) {
                        if (sideBidentifiers[l].getIdentifierName().equalsIgnoreCase(targetColumnName) && !sideButilised[l]) {
                            this.putColumn(sideBidentifiers[l], colmds[k]);
                            sideButilised[l] = true;
                            targetExists = true;
                            break;
                        }
                    }
                    if (!targetExists) {
                        throw new NucleusUserException(CorrespondentColumnsMapper.LOCALISER.msg("020004", this.columnsName, colmds[k].getName(), targetColumnName)).setFatal();
                    }
                }
            }
            for (int k = 0; k < colmds.length; ++k) {
                if (colmds[k].getTarget() == null) {
                    for (int m = 0; m < sideBidentifiers.length; ++m) {
                        if (!sideButilised[m]) {
                            this.putColumn(sideBidentifiers[m], colmds[k]);
                            sideButilised[m] = true;
                            break;
                        }
                    }
                }
            }
            for (int k = colmds.length; k < mappingSideB.getNumberOfDatastoreMappings(); ++k) {
                DatastoreIdentifier sideBidentifier = null;
                for (int j2 = 0; j2 < sideBidentifiers.length; ++j2) {
                    if (!sideButilised[j2]) {
                        sideBidentifier = sideBidentifiers[j2];
                        sideButilised[j2] = true;
                        break;
                    }
                }
                if (sideBidentifier == null) {
                    throw new NucleusUserException(CorrespondentColumnsMapper.LOCALISER.msg("020005", this.columnsName, "" + k)).setFatal();
                }
                final ColumnMetaData colmd = new ColumnMetaData();
                if (updateContainer) {
                    columnContainer.addColumn(colmd);
                }
                this.putColumn(sideBidentifier, colmd);
            }
        }
        else {
            this.columnsName = null;
            for (int i2 = 0; i2 < mappingSideB.getNumberOfDatastoreMappings(); ++i2) {
                final DatastoreIdentifier sideBidentifier2 = mappingSideB.getDatastoreMapping(i2).getColumn().getIdentifier();
                final ColumnMetaData colmd2 = new ColumnMetaData();
                this.putColumn(sideBidentifier2, colmd2);
            }
        }
    }
    
    public ColumnMetaData getColumnMetaDataByIdentifier(final DatastoreIdentifier name) {
        return this.columnMetaDataBySideBIdentifier.get(name);
    }
    
    private void putColumn(final DatastoreIdentifier identifier, final ColumnMetaData colmd) {
        if (this.columnMetaDataBySideBIdentifier.put(identifier, colmd) != null) {
            throw new NucleusUserException(CorrespondentColumnsMapper.LOCALISER.msg("020006", identifier, this.columnsName)).setFatal();
        }
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.store.rdbms.Localisation", RDBMSStoreManager.class.getClassLoader());
    }
}
