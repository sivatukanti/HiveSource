// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.api.jdo.metadata;

import java.util.List;
import org.datanucleus.metadata.ColumnMetaData;
import javax.jdo.metadata.ColumnMetadata;
import org.datanucleus.metadata.VersionMetaData;
import javax.jdo.metadata.VersionMetadata;
import javax.jdo.metadata.UniqueMetadata;
import org.datanucleus.metadata.QueryMetaData;
import javax.jdo.metadata.QueryMetadata;
import org.datanucleus.metadata.PrimaryKeyMetaData;
import javax.jdo.metadata.PrimaryKeyMetadata;
import org.datanucleus.metadata.UniqueMetaData;
import org.datanucleus.metadata.JoinMetaData;
import javax.jdo.metadata.JoinMetadata;
import org.datanucleus.metadata.InheritanceMetaData;
import javax.jdo.metadata.InheritanceMetadata;
import org.datanucleus.metadata.IndexMetaData;
import javax.jdo.metadata.IndexMetadata;
import javax.jdo.annotations.IdentityType;
import org.datanucleus.metadata.ForeignKeyMetaData;
import javax.jdo.metadata.ForeignKeyMetadata;
import java.util.Iterator;
import java.util.Set;
import org.datanucleus.metadata.FetchGroupMetaData;
import javax.jdo.metadata.FetchGroupMetadata;
import org.datanucleus.metadata.IdentityMetaData;
import javax.jdo.metadata.DatastoreIdentityMetadata;
import javax.jdo.JDOUserException;
import java.lang.reflect.Method;
import javax.jdo.metadata.PropertyMetadata;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.metadata.PropertyMetaData;
import org.datanucleus.metadata.FieldMetaData;
import javax.jdo.metadata.MemberMetadata;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.metadata.MetaData;
import javax.jdo.metadata.TypeMetadata;

public abstract class TypeMetadataImpl extends AbstractMetadataImpl implements TypeMetadata
{
    public TypeMetadataImpl(final MetaData internal) {
        super(internal);
    }
    
    public AbstractClassMetaData getInternal() {
        return (AbstractClassMetaData)this.internalMD;
    }
    
    public int getNumberOfMembers() {
        return this.getInternal().getNoOfMembers();
    }
    
    public MemberMetadata[] getMembers() {
        final AbstractMemberMetaData[] internalMmds = this.getInternal().getManagedMembers();
        if (internalMmds == null) {
            return null;
        }
        final MemberMetadataImpl[] mmds = new MemberMetadataImpl[internalMmds.length];
        for (int i = 0; i < mmds.length; ++i) {
            if (internalMmds[i] instanceof FieldMetaData) {
                mmds[i] = new FieldMetadataImpl((FieldMetaData)internalMmds[i]);
            }
            else {
                mmds[i] = new PropertyMetadataImpl((PropertyMetaData)internalMmds[i]);
            }
            mmds[i].parent = this;
        }
        return mmds;
    }
    
    public PropertyMetadata newPropertyMetadata(final String name) {
        final PropertyMetaData internalPmd = this.getInternal().newPropertyMetadata(name);
        final PropertyMetadataImpl pmd = new PropertyMetadataImpl(internalPmd);
        pmd.parent = this;
        return pmd;
    }
    
    public PropertyMetadata newPropertyMetadata(final Method method) {
        final String methodName = method.getName();
        String name = null;
        if (methodName.startsWith("set")) {
            name = methodName.substring(3);
        }
        else if (methodName.startsWith("get")) {
            name = methodName.substring(3);
        }
        else {
            if (!methodName.startsWith("is")) {
                throw new JDOUserException("Method " + methodName + " is not a Java-bean method");
            }
            name = methodName.substring(2);
        }
        final String propertyName = name.substring(0, 1).toLowerCase() + name.substring(1);
        final PropertyMetaData internalPmd = this.getInternal().newPropertyMetadata(propertyName);
        final PropertyMetadataImpl pmd = new PropertyMetadataImpl(internalPmd);
        pmd.parent = this;
        return pmd;
    }
    
    public boolean getCacheable() {
        return this.getInternal().isCacheable();
    }
    
    public String getCatalog() {
        return this.getInternal().getCatalog();
    }
    
    public DatastoreIdentityMetadata getDatastoreIdentityMetadata() {
        final IdentityMetaData internalIdmd = this.getInternal().getIdentityMetaData();
        final DatastoreIdentityMetadataImpl idmd = new DatastoreIdentityMetadataImpl(internalIdmd);
        idmd.parent = this;
        return idmd;
    }
    
    public boolean getDetachable() {
        return this.getInternal().isDetachable();
    }
    
    public Boolean getEmbeddedOnly() {
        return this.getInternal().isEmbeddedOnly();
    }
    
    public boolean getSerializeRead() {
        return this.getInternal().isSerializeRead();
    }
    
    public FetchGroupMetadata[] getFetchGroups() {
        final Set<FetchGroupMetaData> internalFgmds = this.getInternal().getFetchGroupMetaData();
        if (internalFgmds == null) {
            return null;
        }
        final FetchGroupMetadataImpl[] fgmds = new FetchGroupMetadataImpl[internalFgmds.size()];
        int i = 0;
        for (final FetchGroupMetaData fgmd : internalFgmds) {
            fgmds[i] = new FetchGroupMetadataImpl(fgmd);
            fgmds[i].parent = this;
            ++i;
        }
        return fgmds;
    }
    
    public ForeignKeyMetadata[] getForeignKeys() {
        final ForeignKeyMetaData[] internalFks = this.getInternal().getForeignKeyMetaData();
        if (internalFks == null) {
            return null;
        }
        final ForeignKeyMetadataImpl[] fkmds = new ForeignKeyMetadataImpl[internalFks.length];
        for (int i = 0; i < fkmds.length; ++i) {
            fkmds[i] = new ForeignKeyMetadataImpl(internalFks[i]);
            fkmds[i].parent = this;
        }
        return fkmds;
    }
    
    public IdentityType getIdentityType() {
        final org.datanucleus.metadata.IdentityType idType = this.getInternal().getIdentityType();
        if (idType == org.datanucleus.metadata.IdentityType.APPLICATION) {
            return IdentityType.APPLICATION;
        }
        if (idType == org.datanucleus.metadata.IdentityType.DATASTORE) {
            return IdentityType.DATASTORE;
        }
        return IdentityType.NONDURABLE;
    }
    
    public IndexMetadata[] getIndices() {
        final IndexMetaData[] internalIdxmds = this.getInternal().getIndexMetaData();
        if (internalIdxmds == null) {
            return null;
        }
        final IndexMetadataImpl[] idxmds = new IndexMetadataImpl[internalIdxmds.length];
        for (int i = 0; i < idxmds.length; ++i) {
            idxmds[i] = new IndexMetadataImpl(internalIdxmds[i]);
            idxmds[i].parent = this;
        }
        return idxmds;
    }
    
    public InheritanceMetadata getInheritanceMetadata() {
        final InheritanceMetaData internalInhmd = this.getInternal().getInheritanceMetaData();
        final InheritanceMetadataImpl inhmd = new InheritanceMetadataImpl(internalInhmd);
        inhmd.parent = this;
        return inhmd;
    }
    
    public JoinMetadata[] getJoins() {
        final JoinMetaData[] internalJoins = this.getInternal().getJoinMetaData();
        if (internalJoins == null) {
            return null;
        }
        final JoinMetadataImpl[] joins = new JoinMetadataImpl[internalJoins.length];
        for (int i = 0; i < joins.length; ++i) {
            joins[i] = new JoinMetadataImpl(internalJoins[i]);
            joins[i].parent = this;
        }
        return joins;
    }
    
    public String getName() {
        return this.getInternal().getName();
    }
    
    public int getNumberOfFetchGroups() {
        final Set<FetchGroupMetaData> fgmds = this.getInternal().getFetchGroupMetaData();
        return (fgmds != null) ? fgmds.size() : 0;
    }
    
    public int getNumberOfForeignKeys() {
        final ForeignKeyMetaData[] fkmds = this.getInternal().getForeignKeyMetaData();
        return (fkmds != null) ? fkmds.length : 0;
    }
    
    public int getNumberOfIndices() {
        final IndexMetaData[] indexmds = this.getInternal().getIndexMetaData();
        return (indexmds != null) ? indexmds.length : 0;
    }
    
    public int getNumberOfJoins() {
        final JoinMetaData[] joinmds = this.getInternal().getJoinMetaData();
        return (joinmds != null) ? joinmds.length : 0;
    }
    
    public int getNumberOfQueries() {
        return this.getInternal().getNoOfQueries();
    }
    
    public int getNumberOfUniques() {
        final UniqueMetaData[] uniquemds = this.getInternal().getUniqueMetaData();
        return (uniquemds != null) ? uniquemds.length : 0;
    }
    
    public String getObjectIdClass() {
        return this.getInternal().getObjectidClass();
    }
    
    public PrimaryKeyMetadata getPrimaryKeyMetadata() {
        final PrimaryKeyMetaData internalPkmd = this.getInternal().getPrimaryKeyMetaData();
        final PrimaryKeyMetadataImpl pkmd = new PrimaryKeyMetadataImpl(internalPkmd);
        pkmd.parent = this;
        return pkmd;
    }
    
    public QueryMetadata[] getQueries() {
        final QueryMetaData[] baseQueries = this.getInternal().getQueries();
        if (baseQueries == null) {
            return null;
        }
        final QueryMetadataImpl[] queries = new QueryMetadataImpl[this.getInternal().getNoOfQueries()];
        for (int i = 0; i < queries.length; ++i) {
            queries[i] = new QueryMetadataImpl(baseQueries[i]);
            queries[i].parent = this;
        }
        return queries;
    }
    
    public boolean getRequiresExtent() {
        return this.getInternal().isRequiresExtent();
    }
    
    public String getSchema() {
        return this.getInternal().getSchema();
    }
    
    public String getTable() {
        return this.getInternal().getTable();
    }
    
    public UniqueMetadata[] getUniques() {
        final UniqueMetaData[] internalUnimds = this.getInternal().getUniqueMetaData();
        if (internalUnimds == null) {
            return null;
        }
        final UniqueMetadataImpl[] unimds = new UniqueMetadataImpl[internalUnimds.length];
        for (int i = 0; i < unimds.length; ++i) {
            unimds[i] = new UniqueMetadataImpl(internalUnimds[i]);
            unimds[i].parent = this;
        }
        return unimds;
    }
    
    public VersionMetadata getVersionMetadata() {
        final VersionMetaData internalVermd = this.getInternal().getVersionMetaData();
        final VersionMetadataImpl vermd = new VersionMetadataImpl(internalVermd);
        vermd.parent = this;
        return vermd;
    }
    
    public DatastoreIdentityMetadata newDatastoreIdentityMetadata() {
        final IdentityMetaData idmd = this.getInternal().newIdentityMetadata();
        final DatastoreIdentityMetadataImpl dimd = new DatastoreIdentityMetadataImpl(idmd);
        dimd.parent = this;
        return dimd;
    }
    
    public FetchGroupMetadata newFetchGroupMetadata(final String name) {
        final FetchGroupMetaData internalFgmd = this.getInternal().newFetchGroupMetaData(name);
        final FetchGroupMetadataImpl fgmd = new FetchGroupMetadataImpl(internalFgmd);
        fgmd.parent = this;
        return fgmd;
    }
    
    public ForeignKeyMetadata newForeignKeyMetadata() {
        final ForeignKeyMetaData internalFkmd = this.getInternal().newForeignKeyMetadata();
        final ForeignKeyMetadataImpl fkmd = new ForeignKeyMetadataImpl(internalFkmd);
        fkmd.parent = this;
        return fkmd;
    }
    
    public IndexMetadata newIndexMetadata() {
        final IndexMetaData internalIdxmd = this.getInternal().newIndexMetadata();
        final IndexMetadataImpl idxmd = new IndexMetadataImpl(internalIdxmd);
        idxmd.parent = this;
        return idxmd;
    }
    
    public InheritanceMetadata newInheritanceMetadata() {
        final InheritanceMetaData internalInhmd = this.getInternal().newInheritanceMetadata();
        final InheritanceMetadataImpl inhmd = new InheritanceMetadataImpl(internalInhmd);
        inhmd.parent = this;
        return inhmd;
    }
    
    public JoinMetadata newJoinMetadata() {
        final JoinMetaData internalJoinmd = this.getInternal().newJoinMetaData();
        final JoinMetadataImpl joinmd = new JoinMetadataImpl(internalJoinmd);
        joinmd.parent = this;
        return joinmd;
    }
    
    public PrimaryKeyMetadata newPrimaryKeyMetadata() {
        final PrimaryKeyMetaData internalPkmd = this.getInternal().newPrimaryKeyMetadata();
        final PrimaryKeyMetadataImpl pkmd = new PrimaryKeyMetadataImpl(internalPkmd);
        pkmd.parent = this;
        return pkmd;
    }
    
    public QueryMetadata newQueryMetadata(final String name) {
        final QueryMetaData internalQmd = this.getInternal().newQueryMetadata(name);
        final QueryMetadataImpl qmd = new QueryMetadataImpl(internalQmd);
        qmd.parent = this;
        return qmd;
    }
    
    public UniqueMetadata newUniqueMetadata() {
        final UniqueMetaData internalUnimd = this.getInternal().newUniqueMetadata();
        final UniqueMetadataImpl unimd = new UniqueMetadataImpl(internalUnimd);
        unimd.parent = this;
        return unimd;
    }
    
    public VersionMetadata newVersionMetadata() {
        final VersionMetaData internalVermd = this.getInternal().newVersionMetadata();
        final VersionMetadataImpl vermd = new VersionMetadataImpl(internalVermd);
        vermd.parent = this;
        return vermd;
    }
    
    public TypeMetadata setCacheable(final boolean cache) {
        this.getInternal().setCacheable(cache);
        return this;
    }
    
    public TypeMetadata setCatalog(final String cat) {
        this.getInternal().setCatalog(cat);
        return this;
    }
    
    public TypeMetadata setDetachable(final boolean flag) {
        this.getInternal().setDetachable(flag);
        return this;
    }
    
    public TypeMetadata setSerializeRead(final boolean flag) {
        this.getInternal().setSerializeRead(flag);
        return this;
    }
    
    public TypeMetadata setEmbeddedOnly(final boolean flag) {
        this.getInternal().setEmbeddedOnly(flag);
        return this;
    }
    
    public TypeMetadata setIdentityType(final IdentityType type) {
        if (type == IdentityType.APPLICATION) {
            this.getInternal().setIdentityType(org.datanucleus.metadata.IdentityType.APPLICATION);
        }
        else if (type == IdentityType.DATASTORE) {
            this.getInternal().setIdentityType(org.datanucleus.metadata.IdentityType.DATASTORE);
        }
        else if (type == IdentityType.NONDURABLE) {
            this.getInternal().setIdentityType(org.datanucleus.metadata.IdentityType.NONDURABLE);
        }
        return this;
    }
    
    public TypeMetadata setObjectIdClass(final String clsName) {
        this.getInternal().setObjectIdClass(clsName);
        return this;
    }
    
    public TypeMetadata setRequiresExtent(final boolean flag) {
        this.getInternal().setRequiresExtent(flag);
        return this;
    }
    
    public TypeMetadata setSchema(final String schema) {
        this.getInternal().setSchema(schema);
        return this;
    }
    
    public TypeMetadata setTable(final String table) {
        this.getInternal().setTable(table);
        return this;
    }
    
    public ColumnMetadata[] getColumns() {
        final List internalColmds = this.getInternal().getUnmappedColumns();
        if (internalColmds == null) {
            return null;
        }
        final ColumnMetadataImpl[] colmds = new ColumnMetadataImpl[internalColmds.size()];
        for (int i = 0; i < colmds.length; ++i) {
            colmds[i] = new ColumnMetadataImpl(internalColmds.get(i));
            colmds[i].parent = this;
        }
        return colmds;
    }
    
    public int getNumberOfColumns() {
        final List colmds = this.getInternal().getUnmappedColumns();
        return (colmds != null) ? colmds.size() : 0;
    }
    
    public ColumnMetadata newColumnMetadata() {
        final ColumnMetaData internalColmd = this.getInternal().newUnmappedColumnMetaData();
        final ColumnMetadataImpl colmd = new ColumnMetadataImpl(internalColmd);
        colmd.parent = this;
        return colmd;
    }
}
