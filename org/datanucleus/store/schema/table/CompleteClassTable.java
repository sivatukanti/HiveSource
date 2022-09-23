// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.schema.table;

import org.datanucleus.metadata.EmbeddedMetaData;
import java.util.Iterator;
import org.datanucleus.metadata.DiscriminatorMetaData;
import org.datanucleus.metadata.VersionMetaData;
import org.datanucleus.ClassLoaderResolver;
import java.util.List;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.store.schema.naming.ColumnType;
import org.datanucleus.metadata.ColumnMetaData;
import org.datanucleus.metadata.IdentityType;
import org.datanucleus.metadata.RelationType;
import java.util.ArrayList;
import java.util.HashMap;
import org.datanucleus.metadata.AbstractMemberMetaData;
import java.util.Map;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.store.StoreManager;

public class CompleteClassTable implements Table
{
    StoreManager storeMgr;
    AbstractClassMetaData cmd;
    String identifier;
    Map<AbstractMemberMetaData, BasicColumn> columnByMember;
    Map<Integer, BasicColumn> columnByPosition;
    
    public CompleteClassTable(final StoreManager storeMgr, final AbstractClassMetaData cmd) {
        this.columnByMember = new HashMap<AbstractMemberMetaData, BasicColumn>();
        this.columnByPosition = new HashMap<Integer, BasicColumn>();
        this.storeMgr = storeMgr;
        this.cmd = cmd;
        if (cmd.getTable() != null) {
            this.identifier = cmd.getTable();
        }
        else {
            this.identifier = storeMgr.getNamingFactory().getTableName(cmd);
        }
        final List<BasicColumn> columns = new ArrayList<BasicColumn>();
        final ClassLoaderResolver clr = storeMgr.getNucleusContext().getClassLoaderResolver(null);
        for (int numMembers = cmd.getAllMemberPositions().length, i = 0; i < numMembers; ++i) {
            final AbstractMemberMetaData mmd = cmd.getMetaDataForManagedMemberAtAbsolutePosition(i);
            final RelationType relationType = mmd.getRelationType(clr);
            if (relationType == RelationType.NONE) {
                this.processBasicMember(columns, mmd);
            }
            else if (mmd.isEmbedded()) {
                this.processEmbeddedMember(columns, mmd, clr);
            }
            else {
                this.processBasicMember(columns, mmd);
            }
        }
        if (cmd.getIdentityType() == IdentityType.DATASTORE) {
            ColumnMetaData colmd = cmd.getIdentityMetaData().getColumnMetaData();
            if (colmd == null || colmd.getName() == null) {
                colmd = ((colmd != null) ? new ColumnMetaData(colmd) : new ColumnMetaData());
                colmd.setName(storeMgr.getNamingFactory().getColumnName(cmd, ColumnType.DATASTOREID_COLUMN));
            }
            final BasicColumn col = new BasicColumn(this, storeMgr, colmd);
            columns.add(col);
        }
        if (cmd.isVersioned()) {
            final VersionMetaData vermd = cmd.getVersionMetaDataForClass();
            ColumnMetaData colmd2 = vermd.getColumnMetaData();
            if (colmd2 == null || colmd2.getName() == null) {
                colmd2 = ((colmd2 != null) ? new ColumnMetaData(colmd2) : new ColumnMetaData());
                colmd2.setName(storeMgr.getNamingFactory().getColumnName(cmd, ColumnType.VERSION_COLUMN));
            }
            final BasicColumn col2 = new BasicColumn(this, storeMgr, colmd2);
            columns.add(col2);
        }
        if (cmd.hasDiscriminatorStrategy()) {
            final DiscriminatorMetaData dismd = cmd.getDiscriminatorMetaDataRoot();
            ColumnMetaData colmd2 = dismd.getColumnMetaData();
            if (colmd2 == null || cmd.getDiscriminatorColumnName() == null) {
                colmd2 = ((colmd2 != null) ? new ColumnMetaData(colmd2) : new ColumnMetaData());
                colmd2.setName(storeMgr.getNamingFactory().getColumnName(cmd, ColumnType.DISCRIMINATOR_COLUMN));
            }
            final BasicColumn col2 = new BasicColumn(this, storeMgr, colmd2);
            columns.add(col2);
        }
        if (storeMgr.getStringProperty("datanucleus.TenantID") != null) {
            if (!"true".equalsIgnoreCase(cmd.getValueForExtension("multitenancy-disable"))) {
                final ColumnMetaData colmd = new ColumnMetaData();
                colmd.setName(storeMgr.getNamingFactory().getColumnName(cmd, ColumnType.MULTITENANCY_COLUMN));
                final BasicColumn col = new BasicColumn(this, storeMgr, colmd);
                columns.add(col);
            }
        }
        final int numCols = columns.size();
        final Iterator<BasicColumn> colIter = columns.iterator();
        while (colIter.hasNext()) {
            final BasicColumn col2 = colIter.next();
            final ColumnMetaData colmd3 = col2.getColumnMetaData();
            final Integer pos = colmd3.getPosition();
            if (pos != null) {
                if (this.columnByPosition.containsKey(pos)) {
                    final BasicColumn col3 = this.columnByPosition.get(pos);
                    throw new NucleusUserException("Table " + this.identifier + " has column " + col2.getIdentifier() + " specified to have column position " + pos + " yet that position is also defined for column " + col3.identifier);
                }
                if (pos >= numCols) {
                    throw new NucleusUserException("Table " + this.identifier + " has column " + col2.getIdentifier() + " specified to have position " + pos + " yet the number of columns is " + numCols + "." + " Column positions should be from 0 and have no gaps");
                }
                this.columnByPosition.put(colmd3.getPosition(), col2);
                colIter.remove();
            }
        }
        if (!columns.isEmpty()) {
            int pos2 = 0;
            for (final BasicColumn col4 : columns) {
                while (this.columnByPosition.containsKey(pos2)) {
                    ++pos2;
                }
                this.columnByPosition.put(pos2, col4);
            }
        }
    }
    
    protected void processBasicMember(final List<BasicColumn> cols, final AbstractMemberMetaData mmd) {
        ColumnMetaData colmd = null;
        final ColumnMetaData[] colmds = mmd.getColumnMetaData();
        if (colmds == null || colmds.length == 0) {
            colmd = new ColumnMetaData();
        }
        else {
            if (colmds.length > 1) {
                throw new NucleusUserException("Dont currently support member having more than 1 column");
            }
            colmd = colmds[0];
        }
        if (colmd.getName() == null) {
            colmd.setName(this.storeMgr.getNamingFactory().getColumnName(mmd, ColumnType.COLUMN, 0));
        }
        final BasicColumn col = new BasicColumn(this, this.storeMgr, colmd);
        col.setMemberMetaData(mmd);
        cols.add(col);
        this.columnByMember.put(mmd, col);
    }
    
    protected void processEmbeddedMember(final List<BasicColumn> cols, final AbstractMemberMetaData ownerMmd, final ClassLoaderResolver clr) {
        final EmbeddedMetaData emd = ownerMmd.getEmbeddedMetaData();
        final AbstractClassMetaData embCmd = this.storeMgr.getNucleusContext().getMetaDataManager().getMetaDataForClass(ownerMmd.getType(), clr);
        final AbstractMemberMetaData[] embMmds = emd.getMemberMetaData();
        for (int i = 0; i < embMmds.length; ++i) {
            final AbstractMemberMetaData mmd = embCmd.getMetaDataForMember(embMmds[i].getName());
            if (embMmds[i].getEmbeddedMetaData() == null) {
                this.processBasicMember(cols, embMmds[i]);
            }
            else {
                final RelationType relationType = mmd.getRelationType(clr);
                if (!RelationType.isRelationSingleValued(relationType)) {
                    throw new NucleusUserException("Dont currently support embedded collections for this datastore");
                }
                this.processEmbeddedMember(cols, mmd, clr);
            }
        }
    }
    
    @Override
    public AbstractClassMetaData getClassMetaData() {
        return this.cmd;
    }
    
    @Override
    public String getIdentifier() {
        return this.identifier;
    }
    
    @Override
    public int getNumberOfColumns() {
        return this.columnByPosition.size();
    }
    
    @Override
    public BasicColumn getColumnForPosition(final int pos) {
        return this.columnByPosition.get(pos);
    }
    
    @Override
    public BasicColumn getColumnForMember(final AbstractMemberMetaData mmd) {
        return this.columnByMember.get(mmd);
    }
}
