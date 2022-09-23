// 
// Decompiled by Procyon v0.5.36
// 

package javax.jdo.metadata;

import java.lang.reflect.Method;
import javax.jdo.annotations.IdentityType;

public interface TypeMetadata extends Metadata
{
    String getName();
    
    TypeMetadata setIdentityType(final IdentityType p0);
    
    IdentityType getIdentityType();
    
    TypeMetadata setObjectIdClass(final String p0);
    
    String getObjectIdClass();
    
    TypeMetadata setRequiresExtent(final boolean p0);
    
    boolean getRequiresExtent();
    
    TypeMetadata setDetachable(final boolean p0);
    
    boolean getDetachable();
    
    TypeMetadata setCacheable(final boolean p0);
    
    boolean getCacheable();
    
    TypeMetadata setSerializeRead(final boolean p0);
    
    boolean getSerializeRead();
    
    TypeMetadata setEmbeddedOnly(final boolean p0);
    
    Boolean getEmbeddedOnly();
    
    TypeMetadata setCatalog(final String p0);
    
    String getCatalog();
    
    TypeMetadata setSchema(final String p0);
    
    String getSchema();
    
    TypeMetadata setTable(final String p0);
    
    String getTable();
    
    InheritanceMetadata newInheritanceMetadata();
    
    InheritanceMetadata getInheritanceMetadata();
    
    VersionMetadata newVersionMetadata();
    
    VersionMetadata getVersionMetadata();
    
    DatastoreIdentityMetadata newDatastoreIdentityMetadata();
    
    DatastoreIdentityMetadata getDatastoreIdentityMetadata();
    
    PrimaryKeyMetadata newPrimaryKeyMetadata();
    
    PrimaryKeyMetadata getPrimaryKeyMetadata();
    
    JoinMetadata[] getJoins();
    
    JoinMetadata newJoinMetadata();
    
    int getNumberOfJoins();
    
    ForeignKeyMetadata[] getForeignKeys();
    
    ForeignKeyMetadata newForeignKeyMetadata();
    
    int getNumberOfForeignKeys();
    
    IndexMetadata[] getIndices();
    
    IndexMetadata newIndexMetadata();
    
    int getNumberOfIndices();
    
    UniqueMetadata[] getUniques();
    
    UniqueMetadata newUniqueMetadata();
    
    int getNumberOfUniques();
    
    MemberMetadata[] getMembers();
    
    int getNumberOfMembers();
    
    PropertyMetadata newPropertyMetadata(final String p0);
    
    PropertyMetadata newPropertyMetadata(final Method p0);
    
    QueryMetadata[] getQueries();
    
    QueryMetadata newQueryMetadata(final String p0);
    
    int getNumberOfQueries();
    
    FetchGroupMetadata[] getFetchGroups();
    
    FetchGroupMetadata newFetchGroupMetadata(final String p0);
    
    int getNumberOfFetchGroups();
    
    ColumnMetadata[] getColumns();
    
    ColumnMetadata newColumnMetadata();
    
    int getNumberOfColumns();
}
