// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.api.jdo.metadata;

import org.datanucleus.metadata.ColumnMetaData;
import javax.jdo.metadata.ColumnMetadata;
import javax.jdo.annotations.IdGeneratorStrategy;
import org.datanucleus.metadata.ValueMetaData;
import javax.jdo.metadata.ValueMetadata;
import org.datanucleus.metadata.UniqueMetaData;
import javax.jdo.metadata.UniqueMetadata;
import org.datanucleus.metadata.FieldPersistenceModifier;
import javax.jdo.annotations.PersistenceModifier;
import javax.jdo.annotations.NullValue;
import org.datanucleus.metadata.MapMetaData;
import javax.jdo.metadata.MapMetadata;
import org.datanucleus.metadata.KeyMetaData;
import javax.jdo.metadata.KeyMetadata;
import org.datanucleus.metadata.JoinMetaData;
import javax.jdo.metadata.JoinMetadata;
import org.datanucleus.metadata.IndexedValue;
import org.datanucleus.metadata.IndexMetaData;
import javax.jdo.metadata.IndexMetadata;
import javax.jdo.metadata.ForeignKeyMetadata;
import org.datanucleus.metadata.OrderMetaData;
import javax.jdo.metadata.OrderMetadata;
import org.datanucleus.metadata.EmbeddedMetaData;
import javax.jdo.metadata.EmbeddedMetadata;
import org.datanucleus.metadata.ElementMetaData;
import javax.jdo.metadata.ElementMetadata;
import org.datanucleus.metadata.ForeignKeyMetaData;
import javax.jdo.annotations.ForeignKeyAction;
import org.datanucleus.metadata.IdentityStrategy;
import org.datanucleus.metadata.CollectionMetaData;
import javax.jdo.metadata.CollectionMetadata;
import org.datanucleus.metadata.ArrayMetaData;
import javax.jdo.metadata.ArrayMetadata;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.metadata.MetaData;
import javax.jdo.metadata.MemberMetadata;

public class MemberMetadataImpl extends AbstractMetadataImpl implements MemberMetadata
{
    public MemberMetadataImpl(final MetaData internal) {
        super(internal);
    }
    
    public AbstractMemberMetaData getInternal() {
        return (AbstractMemberMetaData)this.internalMD;
    }
    
    public ArrayMetadata getArrayMetadata() {
        final ArrayMetaData internalArrmd = this.getInternal().getArray();
        if (internalArrmd == null) {
            return null;
        }
        final ArrayMetadataImpl arrmd = new ArrayMetadataImpl(internalArrmd);
        arrmd.parent = this;
        return arrmd;
    }
    
    public boolean getCacheable() {
        return this.getInternal().isCacheable();
    }
    
    public CollectionMetadata getCollectionMetadata() {
        final CollectionMetaData internalCollmd = this.getInternal().getCollection();
        if (internalCollmd == null) {
            return null;
        }
        final CollectionMetadataImpl collmd = new CollectionMetadataImpl(internalCollmd);
        collmd.parent = this;
        return collmd;
    }
    
    public String getColumn() {
        return this.getInternal().getColumn();
    }
    
    public String getCustomStrategy() {
        final IdentityStrategy strategy = this.getInternal().getValueStrategy();
        if (strategy != IdentityStrategy.IDENTITY && strategy != IdentityStrategy.INCREMENT && strategy != IdentityStrategy.NATIVE && strategy != IdentityStrategy.SEQUENCE && strategy != IdentityStrategy.UUIDHEX && strategy != IdentityStrategy.UUIDSTRING && strategy != null) {
            return strategy.toString();
        }
        return null;
    }
    
    public Boolean getDefaultFetchGroup() {
        return this.getInternal().isDefaultFetchGroup();
    }
    
    public ForeignKeyAction getDeleteAction() {
        final ForeignKeyMetaData fkmd = this.getInternal().getForeignKeyMetaData();
        if (fkmd != null) {
            final org.datanucleus.metadata.ForeignKeyAction fk = fkmd.getDeleteAction();
            if (fk == org.datanucleus.metadata.ForeignKeyAction.CASCADE) {
                return ForeignKeyAction.CASCADE;
            }
            if (fk == org.datanucleus.metadata.ForeignKeyAction.DEFAULT) {
                return ForeignKeyAction.DEFAULT;
            }
            if (fk == org.datanucleus.metadata.ForeignKeyAction.NONE) {
                return ForeignKeyAction.NONE;
            }
            if (fk == org.datanucleus.metadata.ForeignKeyAction.NULL) {
                return ForeignKeyAction.NULL;
            }
            if (fk == org.datanucleus.metadata.ForeignKeyAction.RESTRICT) {
                return ForeignKeyAction.RESTRICT;
            }
        }
        return ForeignKeyAction.UNSPECIFIED;
    }
    
    public Boolean getDependent() {
        return this.getInternal().isDependent();
    }
    
    public ElementMetadata getElementMetadata() {
        final ElementMetaData internalElemmd = this.getInternal().getElementMetaData();
        if (internalElemmd == null) {
            return null;
        }
        final ElementMetadataImpl elemmd = new ElementMetadataImpl(internalElemmd);
        elemmd.parent = this;
        return elemmd;
    }
    
    public Boolean getEmbedded() {
        return this.getInternal().isEmbedded();
    }
    
    public EmbeddedMetadata getEmbeddedMetadata() {
        final EmbeddedMetaData internalEmbmd = this.getInternal().getEmbeddedMetaData();
        final EmbeddedMetadataImpl embmd = new EmbeddedMetadataImpl(internalEmbmd);
        embmd.parent = this;
        return embmd;
    }
    
    public OrderMetadata getOrderMetadata() {
        final OrderMetaData internalOrdmd = this.getInternal().getOrderMetaData();
        final OrderMetadataImpl ordmd = new OrderMetadataImpl(internalOrdmd);
        ordmd.parent = this;
        return ordmd;
    }
    
    public String getFieldType() {
        return this.getInternal().getTypeName();
    }
    
    public ForeignKeyMetadata getForeignKeyMetadata() {
        final ForeignKeyMetaData internalFkmd = this.getInternal().getForeignKeyMetaData();
        if (internalFkmd == null) {
            return null;
        }
        final ForeignKeyMetadataImpl fkmd = new ForeignKeyMetadataImpl(internalFkmd);
        fkmd.parent = this;
        return fkmd;
    }
    
    public IndexMetadata getIndexMetadata() {
        final IndexMetaData internalIdxmd = this.getInternal().getIndexMetaData();
        if (internalIdxmd == null) {
            return null;
        }
        final IndexMetadataImpl idxmd = new IndexMetadataImpl(internalIdxmd);
        idxmd.parent = this;
        return idxmd;
    }
    
    public Boolean getIndexed() {
        final IndexedValue val = this.getInternal().getIndexed();
        if (val == IndexedValue.TRUE) {
            return true;
        }
        if (val == IndexedValue.FALSE) {
            return false;
        }
        return null;
    }
    
    public JoinMetadata getJoinMetadata() {
        final JoinMetaData internalJoinmd = this.getInternal().getJoinMetaData();
        if (internalJoinmd == null) {
            return null;
        }
        final JoinMetadataImpl joinmd = new JoinMetadataImpl(internalJoinmd);
        joinmd.parent = this;
        return joinmd;
    }
    
    public KeyMetadata getKeyMetadata() {
        final KeyMetaData internalKeymd = this.getInternal().getKeyMetaData();
        if (internalKeymd == null) {
            return null;
        }
        final KeyMetadataImpl keymd = new KeyMetadataImpl(internalKeymd);
        keymd.parent = this;
        return keymd;
    }
    
    public String getLoadFetchGroup() {
        return this.getInternal().getLoadFetchGroup();
    }
    
    public MapMetadata getMapMetadata() {
        final MapMetaData internalMapmd = this.getInternal().getMap();
        if (internalMapmd == null) {
            return null;
        }
        final MapMetadataImpl mapmd = new MapMetadataImpl(internalMapmd);
        mapmd.parent = this;
        return mapmd;
    }
    
    public String getMappedBy() {
        return this.getInternal().getMappedBy();
    }
    
    public String getName() {
        return this.getInternal().getName();
    }
    
    public NullValue getNullValue() {
        final org.datanucleus.metadata.NullValue val = this.getInternal().getNullValue();
        if (val == null) {
            return null;
        }
        if (val == org.datanucleus.metadata.NullValue.DEFAULT) {
            return NullValue.DEFAULT;
        }
        if (val == org.datanucleus.metadata.NullValue.EXCEPTION) {
            return NullValue.EXCEPTION;
        }
        if (val == org.datanucleus.metadata.NullValue.NONE) {
            return NullValue.NONE;
        }
        return null;
    }
    
    public PersistenceModifier getPersistenceModifier() {
        final FieldPersistenceModifier mod = this.getInternal().getPersistenceModifier();
        if (mod == FieldPersistenceModifier.NONE) {
            return PersistenceModifier.NONE;
        }
        if (mod == FieldPersistenceModifier.TRANSACTIONAL) {
            return PersistenceModifier.TRANSACTIONAL;
        }
        if (mod == FieldPersistenceModifier.PERSISTENT) {
            return PersistenceModifier.PERSISTENT;
        }
        return PersistenceModifier.UNSPECIFIED;
    }
    
    public boolean getPrimaryKey() {
        return this.getInternal().isPrimaryKey();
    }
    
    public int getRecursionDepth() {
        return this.getInternal().getRecursionDepth();
    }
    
    public String getSequence() {
        return this.getInternal().getSequence();
    }
    
    public Boolean getSerialized() {
        return this.getInternal().isSerialized();
    }
    
    public String getTable() {
        return this.getInternal().getTable();
    }
    
    public Boolean getUnique() {
        return this.getInternal().isUnique();
    }
    
    public UniqueMetadata getUniqueMetadata() {
        final UniqueMetaData internalUnimd = this.getInternal().getUniqueMetaData();
        if (internalUnimd == null) {
            return null;
        }
        final UniqueMetadataImpl unimd = new UniqueMetadataImpl(internalUnimd);
        unimd.parent = this;
        return unimd;
    }
    
    public ValueMetadata getValueMetadata() {
        final ValueMetaData internalValmd = this.getInternal().getValueMetaData();
        if (internalValmd == null) {
            return null;
        }
        final ValueMetadataImpl valmd = new ValueMetadataImpl(internalValmd);
        valmd.parent = this;
        return valmd;
    }
    
    public IdGeneratorStrategy getValueStrategy() {
        final IdentityStrategy strategy = this.getInternal().getValueStrategy();
        if (strategy == IdentityStrategy.IDENTITY) {
            return IdGeneratorStrategy.IDENTITY;
        }
        if (strategy == IdentityStrategy.INCREMENT) {
            return IdGeneratorStrategy.INCREMENT;
        }
        if (strategy == IdentityStrategy.NATIVE) {
            return IdGeneratorStrategy.NATIVE;
        }
        if (strategy == IdentityStrategy.SEQUENCE) {
            return IdGeneratorStrategy.SEQUENCE;
        }
        if (strategy == IdentityStrategy.UUIDHEX) {
            return IdGeneratorStrategy.UUIDHEX;
        }
        if (strategy == IdentityStrategy.UUIDSTRING) {
            return IdGeneratorStrategy.UUIDSTRING;
        }
        return IdGeneratorStrategy.UNSPECIFIED;
    }
    
    public ArrayMetadata newArrayMetadata() {
        final ArrayMetaData internalArrmd = this.getInternal().newArrayMetaData();
        final ArrayMetadataImpl arrmd = new ArrayMetadataImpl(internalArrmd);
        arrmd.parent = this;
        return arrmd;
    }
    
    public CollectionMetadata newCollectionMetadata() {
        final CollectionMetaData internalCollmd = this.getInternal().newCollectionMetaData();
        final CollectionMetadataImpl collmd = new CollectionMetadataImpl(internalCollmd);
        collmd.parent = this;
        return collmd;
    }
    
    public ElementMetadata newElementMetadata() {
        final ElementMetaData internalElemmd = this.getInternal().newElementMetaData();
        final ElementMetadataImpl elemmd = new ElementMetadataImpl(internalElemmd);
        elemmd.parent = this;
        return elemmd;
    }
    
    public EmbeddedMetadata newEmbeddedMetadata() {
        final EmbeddedMetaData internalEmbmd = this.getInternal().newEmbeddedMetaData();
        final EmbeddedMetadataImpl embmd = new EmbeddedMetadataImpl(internalEmbmd);
        embmd.parent = this;
        return embmd;
    }
    
    public ForeignKeyMetadata newForeignKeyMetadata() {
        final ForeignKeyMetaData internalFkmd = this.getInternal().newForeignKeyMetaData();
        final ForeignKeyMetadataImpl fkmd = new ForeignKeyMetadataImpl(internalFkmd);
        fkmd.parent = this;
        return fkmd;
    }
    
    public IndexMetadata newIndexMetadata() {
        final IndexMetaData internalIdxmd = this.getInternal().newIndexMetaData();
        final IndexMetadataImpl idxmd = new IndexMetadataImpl(internalIdxmd);
        idxmd.parent = this;
        return idxmd;
    }
    
    public JoinMetadata newJoinMetadata() {
        final JoinMetaData internalJoinmd = this.getInternal().newJoinMetaData();
        final JoinMetadataImpl joinmd = new JoinMetadataImpl(internalJoinmd);
        joinmd.parent = this;
        return joinmd;
    }
    
    public KeyMetadata newKeyMetadata() {
        final KeyMetaData internalKeymd = this.getInternal().newKeyMetaData();
        final KeyMetadataImpl keymd = new KeyMetadataImpl(internalKeymd);
        keymd.parent = this;
        return keymd;
    }
    
    public MapMetadata newMapMetadata() {
        final MapMetaData internalMapmd = this.getInternal().newMapMetaData();
        final MapMetadataImpl mapmd = new MapMetadataImpl(internalMapmd);
        mapmd.parent = this;
        return mapmd;
    }
    
    public OrderMetadata newOrderMetadata() {
        final OrderMetaData internalOrdmd = this.getInternal().newOrderMetaData();
        final OrderMetadataImpl ordmd = new OrderMetadataImpl(internalOrdmd);
        ordmd.parent = this;
        return ordmd;
    }
    
    public UniqueMetadata newUniqueMetadata() {
        final UniqueMetaData internalUnimd = this.getInternal().newUniqueMetaData();
        final UniqueMetadataImpl unimd = new UniqueMetadataImpl(internalUnimd);
        unimd.parent = this;
        return unimd;
    }
    
    public ValueMetadata newValueMetadata() {
        final ValueMetaData internalValmd = this.getInternal().newValueMetaData();
        final ValueMetadataImpl valmd = new ValueMetadataImpl(internalValmd);
        valmd.parent = this;
        return valmd;
    }
    
    public MemberMetadata setCacheable(final boolean cache) {
        this.getInternal().setCacheable(cache);
        return this;
    }
    
    public MemberMetadata setColumn(final String name) {
        this.getInternal().setColumn(name);
        return this;
    }
    
    public MemberMetadata setCustomStrategy(final String strategy) {
        this.getInternal().setValueStrategy(IdentityStrategy.getIdentityStrategy(strategy));
        return this;
    }
    
    public MemberMetadata setDefaultFetchGroup(final boolean dfg) {
        this.getInternal().setDefaultFetchGroup(dfg);
        return this;
    }
    
    public MemberMetadata setDeleteAction(final ForeignKeyAction fk) {
        final ForeignKeyMetaData fkmd = this.getInternal().getForeignKeyMetaData();
        if (fk == ForeignKeyAction.CASCADE) {
            fkmd.setDeleteAction(org.datanucleus.metadata.ForeignKeyAction.CASCADE);
        }
        else if (fk == ForeignKeyAction.DEFAULT) {
            fkmd.setDeleteAction(org.datanucleus.metadata.ForeignKeyAction.DEFAULT);
        }
        else if (fk == ForeignKeyAction.NONE) {
            fkmd.setDeleteAction(org.datanucleus.metadata.ForeignKeyAction.NONE);
        }
        else if (fk == ForeignKeyAction.NULL) {
            fkmd.setDeleteAction(org.datanucleus.metadata.ForeignKeyAction.NULL);
        }
        else if (fk == ForeignKeyAction.RESTRICT) {
            fkmd.setDeleteAction(org.datanucleus.metadata.ForeignKeyAction.RESTRICT);
        }
        return this;
    }
    
    public MemberMetadata setDependent(final boolean flag) {
        this.getInternal().setDependent(flag);
        return this;
    }
    
    public MemberMetadata setEmbedded(final boolean flag) {
        this.getInternal().setEmbedded(flag);
        return this;
    }
    
    public MemberMetadata setFieldType(final String types) {
        this.getInternal().setFieldTypes(types);
        return this;
    }
    
    public MemberMetadata setIndexed(final boolean flag) {
        if (flag) {
            this.getInternal().setIndexed(IndexedValue.TRUE);
        }
        else {
            this.getInternal().setIndexed(IndexedValue.FALSE);
        }
        return this;
    }
    
    public MemberMetadata setLoadFetchGroup(final String load) {
        this.getInternal().setLoadFetchGroup(load);
        return this;
    }
    
    public MemberMetadata setMappedBy(final String mappedBy) {
        this.getInternal().setMappedBy(mappedBy);
        return this;
    }
    
    public MemberMetadata setName(final String name) {
        return this;
    }
    
    public MemberMetadata setNullValue(final NullValue val) {
        if (val == NullValue.DEFAULT) {
            this.getInternal().setNullValue(org.datanucleus.metadata.NullValue.DEFAULT);
        }
        else if (val == NullValue.EXCEPTION) {
            this.getInternal().setNullValue(org.datanucleus.metadata.NullValue.EXCEPTION);
        }
        else if (val == NullValue.NONE) {
            this.getInternal().setNullValue(org.datanucleus.metadata.NullValue.NONE);
        }
        return this;
    }
    
    public MemberMetadata setPersistenceModifier(final PersistenceModifier val) {
        if (val == PersistenceModifier.NONE) {
            this.getInternal().setPersistenceModifier(FieldPersistenceModifier.NONE);
        }
        else if (val == PersistenceModifier.PERSISTENT) {
            this.getInternal().setPersistenceModifier(FieldPersistenceModifier.PERSISTENT);
        }
        else if (val == PersistenceModifier.TRANSACTIONAL) {
            this.getInternal().setPersistenceModifier(FieldPersistenceModifier.TRANSACTIONAL);
        }
        return this;
    }
    
    public MemberMetadata setPrimaryKey(final boolean flag) {
        this.getInternal().setPrimaryKey(flag);
        return this;
    }
    
    public MemberMetadata setRecursionDepth(final int depth) {
        this.getInternal().setRecursionDepth(depth);
        return this;
    }
    
    public MemberMetadata setSequence(final String seq) {
        this.getInternal().setSequence(seq);
        return this;
    }
    
    public MemberMetadata setSerialized(final boolean flag) {
        this.getInternal().setSerialised(flag);
        return this;
    }
    
    public MemberMetadata setTable(final String table) {
        this.getInternal().setTable(table);
        return this;
    }
    
    public MemberMetadata setUnique(final boolean flag) {
        this.getInternal().setUnique(flag);
        return this;
    }
    
    public MemberMetadata setValueStrategy(final IdGeneratorStrategy strategy) {
        if (strategy == IdGeneratorStrategy.IDENTITY) {
            this.getInternal().setValueStrategy(IdentityStrategy.IDENTITY);
        }
        else if (strategy == IdGeneratorStrategy.INCREMENT) {
            this.getInternal().setValueStrategy(IdentityStrategy.INCREMENT);
        }
        else if (strategy == IdGeneratorStrategy.NATIVE) {
            this.getInternal().setValueStrategy(IdentityStrategy.NATIVE);
        }
        else if (strategy == IdGeneratorStrategy.SEQUENCE) {
            this.getInternal().setValueStrategy(IdentityStrategy.SEQUENCE);
        }
        else if (strategy == IdGeneratorStrategy.UUIDHEX) {
            this.getInternal().setValueStrategy(IdentityStrategy.UUIDHEX);
        }
        else if (strategy == IdGeneratorStrategy.UUIDSTRING) {
            this.getInternal().setValueStrategy(IdentityStrategy.UUIDSTRING);
        }
        return this;
    }
    
    public ColumnMetadata[] getColumns() {
        final ColumnMetaData[] internalColmds = this.getInternal().getColumnMetaData();
        if (internalColmds == null) {
            return null;
        }
        final ColumnMetadataImpl[] colmds = new ColumnMetadataImpl[internalColmds.length];
        for (int i = 0; i < colmds.length; ++i) {
            colmds[i] = new ColumnMetadataImpl(internalColmds[i]);
            colmds[i].parent = this;
        }
        return colmds;
    }
    
    public int getNumberOfColumns() {
        final ColumnMetaData[] colmds = this.getInternal().getColumnMetaData();
        return (colmds != null) ? colmds.length : 0;
    }
    
    public ColumnMetadata newColumnMetadata() {
        final ColumnMetaData internalColmd = this.getInternal().newColumnMetaData();
        final ColumnMetadataImpl colmd = new ColumnMetadataImpl(internalColmd);
        colmd.parent = this;
        return colmd;
    }
}
