// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.table;

import org.datanucleus.metadata.VersionMetaData;
import org.datanucleus.metadata.DiscriminatorMetaData;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.datanucleus.store.rdbms.exceptions.NoSuchPersistentFieldException;
import org.datanucleus.store.rdbms.mapping.MappingConsumer;
import java.util.Collection;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.metadata.FieldPersistenceModifier;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.store.rdbms.exceptions.ViewDefinitionException;
import org.datanucleus.store.rdbms.exceptions.PersistentSuperclassNotAllowedException;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.metadata.IdentityType;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.store.rdbms.identifier.DatastoreIdentifier;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.util.MacroString;
import org.datanucleus.metadata.ClassMetaData;

public class ClassView extends ViewImpl implements DatastoreClass
{
    private final ClassMetaData cmd;
    private final MacroString viewDef;
    private String createStatementDDL;
    private JavaTypeMapping[] fieldMappings;
    
    public ClassView(final DatastoreIdentifier tableName, final RDBMSStoreManager storeMgr, final ClassMetaData cmd) {
        super(tableName, storeMgr);
        this.cmd = cmd;
        if (cmd.getIdentityType() == IdentityType.APPLICATION || cmd.getIdentityType() == IdentityType.DATASTORE) {
            throw new NucleusUserException(ClassView.LOCALISER.msg("031005", cmd.getFullClassName(), cmd.getIdentityType()));
        }
        if (cmd.getIdentityType() == IdentityType.NONDURABLE) {}
        if (cmd.getPersistenceCapableSuperclass() != null) {
            throw new PersistentSuperclassNotAllowedException(cmd.getFullClassName());
        }
        final String viewImpStr = cmd.getValueForExtension("view-imports");
        String viewDefStr = null;
        if (this.dba.getVendorID() != null) {
            viewDefStr = cmd.getValueForExtension("view-definition-" + this.dba.getVendorID());
        }
        if (viewDefStr == null) {
            viewDefStr = cmd.getValueForExtension("view-definition");
        }
        if (viewDefStr == null) {
            throw new ViewDefinitionException(cmd.getFullClassName(), (String)null);
        }
        this.viewDef = new MacroString(cmd.getFullClassName(), viewImpStr, viewDefStr);
    }
    
    @Override
    public void initialize(final ClassLoaderResolver clr) {
        this.assertIsUninitialized();
        final int fieldCount = this.cmd.getNoOfManagedMembers();
        this.fieldMappings = new JavaTypeMapping[fieldCount];
        for (int fieldNumber = 0; fieldNumber < fieldCount; ++fieldNumber) {
            final AbstractMemberMetaData fmd = this.cmd.getMetaDataForManagedMemberAtRelativePosition(fieldNumber);
            if (fmd.getPersistenceModifier() == FieldPersistenceModifier.PERSISTENT) {
                this.fieldMappings[fieldNumber] = this.storeMgr.getMappingManager().getMapping(this, fmd, clr, 2);
            }
            else if (fmd.getPersistenceModifier() != FieldPersistenceModifier.TRANSACTIONAL) {
                throw new NucleusException(ClassView.LOCALISER.msg("031006", this.cmd.getFullClassName(), fmd.getName(), fmd.getPersistenceModifier())).setFatal();
            }
        }
        if (NucleusLogger.DATASTORE_SCHEMA.isDebugEnabled()) {
            NucleusLogger.DATASTORE_SCHEMA.debug(ClassView.LOCALISER.msg("057023", this));
        }
        this.storeMgr.registerTableInitialized(this);
        this.state = 2;
    }
    
    @Override
    public void postInitialize(final ClassLoaderResolver clr) {
        this.assertIsInitialized();
        this.createStatementDDL = this.viewDef.substituteMacros(new MacroString.MacroHandler() {
            @Override
            public void onIdentifierMacro(final MacroString.IdentifierMacro im) {
                ClassView.this.storeMgr.resolveIdentifierMacro(im, clr);
            }
            
            @Override
            public void onParameterMacro(final MacroString.ParameterMacro pm) {
                throw new NucleusUserException(AbstractTable.LOCALISER.msg("031009", ClassView.this.cmd.getFullClassName(), pm));
            }
        }, clr);
    }
    
    @Override
    public JavaTypeMapping getIdMapping() {
        for (int i = 0; i < this.fieldMappings.length; ++i) {
            if (this.fieldMappings[i] != null) {
                return this.fieldMappings[i];
            }
        }
        return null;
    }
    
    @Override
    public DatastoreClass getBaseDatastoreClassWithMember(final AbstractMemberMetaData mmd) {
        return null;
    }
    
    @Override
    public DatastoreClass getSuperDatastoreClass() {
        return null;
    }
    
    @Override
    public boolean isSuperDatastoreClass(final DatastoreClass table) {
        return false;
    }
    
    @Override
    public Collection getSecondaryDatastoreClasses() {
        return null;
    }
    
    @Override
    public JavaTypeMapping getDatastoreObjectIdMapping() {
        return null;
    }
    
    @Override
    public boolean managesClass(final String className) {
        return false;
    }
    
    @Override
    public String[] getManagedClasses() {
        return null;
    }
    
    @Override
    public boolean managesMapping(final JavaTypeMapping mapping) {
        return false;
    }
    
    public AbstractMemberMetaData getFieldMetaData(final String fieldName) {
        return this.cmd.getMetaDataForMember(fieldName);
    }
    
    @Override
    public IdentityType getIdentityType() {
        return this.cmd.getIdentityType();
    }
    
    @Override
    public boolean isBaseDatastoreClass() {
        return true;
    }
    
    @Override
    public DatastoreClass getBaseDatastoreClass() {
        return this;
    }
    
    @Override
    public boolean isObjectIdDatastoreAttributed() {
        return false;
    }
    
    @Override
    public void provideDatastoreIdMappings(final MappingConsumer consumer) {
    }
    
    @Override
    public void provideDiscriminatorMappings(final MappingConsumer consumer) {
    }
    
    @Override
    public void provideMultitenancyMapping(final MappingConsumer consumer) {
    }
    
    @Override
    public void provideMappingsForMembers(final MappingConsumer consumer, final AbstractMemberMetaData[] fieldNumbers, final boolean includeSecondaryTables) {
    }
    
    @Override
    public void provideNonPrimaryKeyMappings(final MappingConsumer consumer) {
    }
    
    @Override
    public void providePrimaryKeyMappings(final MappingConsumer consumer) {
    }
    
    @Override
    public void provideVersionMappings(final MappingConsumer consumer) {
    }
    
    @Override
    public void provideExternalMappings(final MappingConsumer consumer, final int mappingType) {
    }
    
    @Override
    public void provideUnmappedColumns(final MappingConsumer consumer) {
    }
    
    @Override
    public String getType() {
        return this.cmd.getFullClassName();
    }
    
    @Override
    public JavaTypeMapping getMemberMapping(final AbstractMemberMetaData mmd) {
        this.assertIsInitialized();
        final JavaTypeMapping m = this.fieldMappings[mmd.getAbsoluteFieldNumber()];
        if (m == null) {
            throw new NoSuchPersistentFieldException(this.cmd.getFullClassName(), mmd.getAbsoluteFieldNumber());
        }
        return m;
    }
    
    @Override
    public JavaTypeMapping getMemberMappingInDatastoreClass(final AbstractMemberMetaData mmd) {
        return this.getMemberMapping(mmd);
    }
    
    @Override
    public JavaTypeMapping getMemberMapping(final String fieldName) {
        this.assertIsInitialized();
        final int rfn = this.cmd.getRelativePositionOfMember(fieldName);
        if (rfn < 0) {
            throw new NoSuchPersistentFieldException(this.cmd.getFullClassName(), fieldName);
        }
        return this.getMemberMapping(this.cmd.getMetaDataForManagedMemberAtRelativePosition(rfn));
    }
    
    @Override
    protected List getSQLCreateStatements(final Properties props) {
        this.assertIsInitialized();
        final ArrayList stmts = new ArrayList();
        final StringTokenizer tokens = new StringTokenizer(this.createStatementDDL, ";");
        while (tokens.hasMoreTokens()) {
            stmts.add(tokens.nextToken());
        }
        return stmts;
    }
    
    @Override
    public final DiscriminatorMetaData getDiscriminatorMetaData() {
        return null;
    }
    
    @Override
    public JavaTypeMapping getDiscriminatorMapping(final boolean allowSuperclasses) {
        return null;
    }
    
    @Override
    public final VersionMetaData getVersionMetaData() {
        return null;
    }
    
    @Override
    public JavaTypeMapping getVersionMapping(final boolean allowSuperclasses) {
        return null;
    }
    
    @Override
    public JavaTypeMapping getExternalMapping(final AbstractMemberMetaData fmd, final int mappingType) {
        throw new NucleusException("N/A").setFatal();
    }
    
    @Override
    public AbstractMemberMetaData getMetaDataForExternalMapping(final JavaTypeMapping mapping, final int mappingType) {
        throw new NucleusException("N/A").setFatal();
    }
}
