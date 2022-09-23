// 
// Decompiled by Procyon v0.5.36
// 

package javax.jdo.metadata;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.NullValue;
import javax.jdo.annotations.PersistenceModifier;
import javax.jdo.annotations.ForeignKeyAction;

public interface MemberMetadata extends Metadata
{
    MemberMetadata setName(final String p0);
    
    String getName();
    
    MemberMetadata setTable(final String p0);
    
    String getTable();
    
    MemberMetadata setColumn(final String p0);
    
    String getColumn();
    
    MemberMetadata setFieldType(final String p0);
    
    String getFieldType();
    
    MemberMetadata setDeleteAction(final ForeignKeyAction p0);
    
    ForeignKeyAction getDeleteAction();
    
    MemberMetadata setPersistenceModifier(final PersistenceModifier p0);
    
    PersistenceModifier getPersistenceModifier();
    
    MemberMetadata setNullValue(final NullValue p0);
    
    NullValue getNullValue();
    
    MemberMetadata setDefaultFetchGroup(final boolean p0);
    
    Boolean getDefaultFetchGroup();
    
    MemberMetadata setDependent(final boolean p0);
    
    Boolean getDependent();
    
    MemberMetadata setEmbedded(final boolean p0);
    
    Boolean getEmbedded();
    
    MemberMetadata setSerialized(final boolean p0);
    
    Boolean getSerialized();
    
    MemberMetadata setPrimaryKey(final boolean p0);
    
    boolean getPrimaryKey();
    
    MemberMetadata setIndexed(final boolean p0);
    
    Boolean getIndexed();
    
    MemberMetadata setUnique(final boolean p0);
    
    Boolean getUnique();
    
    MemberMetadata setCacheable(final boolean p0);
    
    boolean getCacheable();
    
    MemberMetadata setRecursionDepth(final int p0);
    
    int getRecursionDepth();
    
    MemberMetadata setLoadFetchGroup(final String p0);
    
    String getLoadFetchGroup();
    
    MemberMetadata setValueStrategy(final IdGeneratorStrategy p0);
    
    IdGeneratorStrategy getValueStrategy();
    
    MemberMetadata setCustomStrategy(final String p0);
    
    String getCustomStrategy();
    
    MemberMetadata setSequence(final String p0);
    
    String getSequence();
    
    MemberMetadata setMappedBy(final String p0);
    
    String getMappedBy();
    
    ArrayMetadata newArrayMetadata();
    
    ArrayMetadata getArrayMetadata();
    
    CollectionMetadata newCollectionMetadata();
    
    CollectionMetadata getCollectionMetadata();
    
    MapMetadata newMapMetadata();
    
    MapMetadata getMapMetadata();
    
    JoinMetadata newJoinMetadata();
    
    JoinMetadata getJoinMetadata();
    
    EmbeddedMetadata newEmbeddedMetadata();
    
    EmbeddedMetadata getEmbeddedMetadata();
    
    ElementMetadata newElementMetadata();
    
    ElementMetadata getElementMetadata();
    
    KeyMetadata newKeyMetadata();
    
    KeyMetadata getKeyMetadata();
    
    ValueMetadata newValueMetadata();
    
    ValueMetadata getValueMetadata();
    
    IndexMetadata newIndexMetadata();
    
    IndexMetadata getIndexMetadata();
    
    UniqueMetadata newUniqueMetadata();
    
    UniqueMetadata getUniqueMetadata();
    
    ForeignKeyMetadata newForeignKeyMetadata();
    
    ForeignKeyMetadata getForeignKeyMetadata();
    
    OrderMetadata newOrderMetadata();
    
    OrderMetadata getOrderMetadata();
    
    ColumnMetadata[] getColumns();
    
    ColumnMetadata newColumnMetadata();
    
    int getNumberOfColumns();
}
