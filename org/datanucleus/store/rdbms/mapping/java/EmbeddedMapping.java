// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.mapping.java;

import java.sql.ResultSet;
import org.datanucleus.api.ApiAdapter;
import org.datanucleus.state.ObjectProvider;
import java.sql.PreparedStatement;
import org.datanucleus.ExecutionContext;
import java.util.Iterator;
import java.util.ArrayList;
import org.datanucleus.store.rdbms.table.Column;
import org.datanucleus.store.rdbms.mapping.datastore.DatastoreMapping;
import org.datanucleus.store.rdbms.mapping.MappingManager;
import org.datanucleus.metadata.MetaDataManager;
import org.datanucleus.metadata.FieldPersistenceModifier;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.metadata.ColumnMetaData;
import org.datanucleus.metadata.DiscriminatorStrategy;
import org.datanucleus.metadata.MetaData;
import org.datanucleus.metadata.InheritanceMetaData;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.store.rdbms.table.Table;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.metadata.EmbeddedMetaData;
import org.datanucleus.ClassLoaderResolver;
import java.util.List;
import org.datanucleus.metadata.DiscriminatorMetaData;

public abstract class EmbeddedMapping extends SingleFieldMapping
{
    protected DiscriminatorMetaData discrimMetaData;
    protected DiscriminatorMapping discrimMapping;
    protected List<JavaTypeMapping> javaTypeMappings;
    protected ClassLoaderResolver clr;
    protected EmbeddedMetaData emd;
    protected String typeName;
    protected short objectType;
    protected AbstractClassMetaData embCmd;
    
    public EmbeddedMapping() {
        this.objectType = -1;
        this.embCmd = null;
    }
    
    @Override
    public void initialize(final AbstractMemberMetaData fmd, final Table table, final ClassLoaderResolver clr) {
        throw new NucleusException("subclass must override this method").setFatal();
    }
    
    public void initialize(final AbstractMemberMetaData fmd, final Table table, final ClassLoaderResolver clr, final EmbeddedMetaData emd, final String typeName, final int objectType) {
        super.initialize(fmd, table, clr);
        this.clr = clr;
        this.emd = emd;
        this.typeName = typeName;
        this.objectType = (short)objectType;
        final MetaDataManager mmgr = table.getStoreManager().getMetaDataManager();
        AbstractClassMetaData rootEmbCmd = mmgr.getMetaDataForClass(typeName, clr);
        if (rootEmbCmd == null) {
            if (fmd != null) {
                final String[] fieldTypes = fmd.getFieldTypes();
                if (fieldTypes != null && fieldTypes.length == 1) {
                    rootEmbCmd = mmgr.getMetaDataForClass(fieldTypes[0], clr);
                }
                else if (fieldTypes != null && fieldTypes.length > 1) {
                    throw new NucleusUserException("Field " + fmd.getFullFieldName() + " is a reference field that is embedded with multiple possible implementations. " + "DataNucleus doesnt support embedded reference fields that have more than 1 implementation");
                }
            }
            if (rootEmbCmd == null) {
                rootEmbCmd = mmgr.getMetaDataForInterface(clr.classForName(typeName), clr);
                if (rootEmbCmd == null && fmd.getFieldTypes() != null && fmd.getFieldTypes().length == 1) {
                    rootEmbCmd = mmgr.getMetaDataForInterface(clr.classForName(fmd.getFieldTypes()[0]), clr);
                }
            }
        }
        this.embCmd = rootEmbCmd;
        AbstractMemberMetaData[] embFmds;
        if (emd == null && rootEmbCmd.isEmbeddedOnly()) {
            embFmds = rootEmbCmd.getManagedMembers();
        }
        else {
            embFmds = emd.getMemberMetaData();
        }
        final String[] subclasses = mmgr.getSubclassesForClass(rootEmbCmd.getFullClassName(), true);
        if (subclasses != null && subclasses.length > 0) {
            if (rootEmbCmd.hasDiscriminatorStrategy()) {
                this.discrimMetaData = new DiscriminatorMetaData();
                final InheritanceMetaData embInhMd = new InheritanceMetaData();
                embInhMd.setParent(rootEmbCmd);
                this.discrimMetaData.setParent(embInhMd);
                final DiscriminatorMetaData dismd = rootEmbCmd.getDiscriminatorMetaDataRoot();
                if (dismd.getStrategy() != null && dismd.getStrategy() != DiscriminatorStrategy.NONE) {
                    this.discrimMetaData.setStrategy(dismd.getStrategy());
                }
                else {
                    this.discrimMetaData.setStrategy(DiscriminatorStrategy.CLASS_NAME);
                }
                final ColumnMetaData disColmd = new ColumnMetaData();
                disColmd.setAllowsNull(Boolean.TRUE);
                final DiscriminatorMetaData embDismd = emd.getDiscriminatorMetaData();
                if (embDismd != null && embDismd.getColumnMetaData() != null) {
                    disColmd.setName(embDismd.getColumnMetaData().getName());
                }
                else {
                    final ColumnMetaData colmd = dismd.getColumnMetaData();
                    if (colmd != null && colmd.getName() != null) {
                        disColmd.setName(colmd.getName());
                    }
                }
                this.discrimMetaData.setColumnMetaData(disColmd);
                this.discrimMapping = DiscriminatorMapping.createDiscriminatorMapping(table, this.discrimMetaData);
                this.addDatastoreMapping(this.discrimMapping.getDatastoreMapping(0));
            }
            else {
                NucleusLogger.PERSISTENCE.info("Member " + this.mmd.getFullFieldName() + " is embedded and the type " + "(" + rootEmbCmd.getFullClassName() + ") has potential subclasses." + " Impossible to detect which is stored embedded. Add a discriminator to the embedded type");
            }
        }
        final int[] pcFieldNumbers = rootEmbCmd.getAllMemberPositions();
        for (int i = 0; i < pcFieldNumbers.length; ++i) {
            final AbstractMemberMetaData rootEmbMmd = rootEmbCmd.getMetaDataForManagedMemberAtAbsolutePosition(pcFieldNumbers[i]);
            if (rootEmbMmd.getPersistenceModifier() == FieldPersistenceModifier.PERSISTENT) {
                this.addMappingForMember(rootEmbCmd, rootEmbMmd, embFmds);
            }
        }
        if (this.discrimMapping != null && subclasses != null && subclasses.length > 0) {
            for (int i = 0; i < subclasses.length; ++i) {
                final AbstractClassMetaData subEmbCmd = this.storeMgr.getMetaDataManager().getMetaDataForClass(subclasses[i], clr);
                final AbstractMemberMetaData[] subEmbMmds = subEmbCmd.getManagedMembers();
                if (subEmbMmds != null) {
                    for (int j = 0; j < subEmbMmds.length; ++j) {
                        if (subEmbMmds[j].getPersistenceModifier() == FieldPersistenceModifier.PERSISTENT) {
                            this.addMappingForMember(subEmbCmd, subEmbMmds[j], embFmds);
                        }
                    }
                }
            }
        }
    }
    
    private void addMappingForMember(final AbstractClassMetaData embCmd, final AbstractMemberMetaData embMmd, final AbstractMemberMetaData[] embMmds) {
        if (this.emd == null || this.emd.getOwnerMember() == null || !this.emd.getOwnerMember().equals(embMmd.getName())) {
            AbstractMemberMetaData embeddedMmd = null;
            for (int j = 0; j < embMmds.length; ++j) {
                if (embMmds[j] == null) {
                    throw new RuntimeException("embMmds[j] is null for class=" + embCmd.toString() + " type=" + this.typeName);
                }
                final AbstractMemberMetaData embMmdForMmds = embCmd.getMetaDataForMember(embMmds[j].getName());
                if (embMmdForMmds != null && embMmdForMmds.getAbsoluteFieldNumber() == embMmd.getAbsoluteFieldNumber()) {
                    embeddedMmd = embMmds[j];
                }
            }
            final MappingManager mapMgr = this.table.getStoreManager().getMappingManager();
            JavaTypeMapping embMmdMapping;
            if (embeddedMmd != null) {
                embMmdMapping = mapMgr.getMapping(this.table, embeddedMmd, this.clr, 2);
                if (embeddedMmd.getAbsoluteFieldNumber() < 0) {
                    embMmdMapping.setAbsFieldNumber(embMmd.getAbsoluteFieldNumber());
                }
            }
            else {
                embMmdMapping = mapMgr.getMapping(this.table, embMmd, this.clr, 2);
            }
            this.addJavaTypeMapping(embMmdMapping);
            for (int i = 0; i < embMmdMapping.getNumberOfDatastoreMappings(); ++i) {
                final DatastoreMapping datastoreMapping = embMmdMapping.getDatastoreMapping(i);
                this.addDatastoreMapping(datastoreMapping);
                if (this.mmd.isPrimaryKey()) {
                    final Column col = datastoreMapping.getColumn();
                    if (col != null) {
                        col.setAsPrimaryKey();
                    }
                }
            }
        }
    }
    
    @Override
    protected void prepareDatastoreMapping() {
    }
    
    public void addJavaTypeMapping(final JavaTypeMapping mapping) {
        if (this.javaTypeMappings == null) {
            this.javaTypeMappings = new ArrayList<JavaTypeMapping>();
        }
        if (mapping == null) {
            throw new NucleusException("mapping argument in EmbeddedMapping.addJavaTypeMapping is null").setFatal();
        }
        this.javaTypeMappings.add(mapping);
    }
    
    public int getNumberOfJavaTypeMappings() {
        return (this.javaTypeMappings != null) ? this.javaTypeMappings.size() : 0;
    }
    
    public JavaTypeMapping getJavaTypeMapping(final int i) {
        if (this.javaTypeMappings == null) {
            return null;
        }
        return this.javaTypeMappings.get(i);
    }
    
    public JavaTypeMapping getJavaTypeMapping(final String fieldName) {
        if (this.javaTypeMappings == null) {
            return null;
        }
        for (final JavaTypeMapping m : this.javaTypeMappings) {
            if (m.getMemberMetaData().getName().equals(fieldName)) {
                return m;
            }
        }
        return null;
    }
    
    public JavaTypeMapping getDiscriminatorMapping() {
        return this.discrimMapping;
    }
    
    @Override
    public void setObject(final ExecutionContext ec, final PreparedStatement ps, final int[] param, final Object value) {
        this.setObject(ec, ps, param, value, null, -1);
    }
    
    @Override
    public void setObject(final ExecutionContext ec, final PreparedStatement ps, final int[] param, final Object value, final ObjectProvider ownerOP, final int ownerFieldNumber) {
        if (value == null) {
            int n = 0;
            String nullColumn = null;
            String nullValue = null;
            if (this.emd != null) {
                nullColumn = this.emd.getNullIndicatorColumn();
                nullValue = this.emd.getNullIndicatorValue();
            }
            if (this.discrimMapping != null) {
                this.discrimMapping.setObject(ec, ps, new int[] { param[n] }, null);
                ++n;
            }
            for (int i = 0; i < this.javaTypeMappings.size(); ++i) {
                final JavaTypeMapping mapping = this.javaTypeMappings.get(i);
                final int[] posMapping = new int[mapping.getNumberOfDatastoreMappings()];
                for (int j = 0; j < posMapping.length; ++j) {
                    posMapping[j] = param[n++];
                }
                if (nullColumn != null && nullValue != null && mapping.getMemberMetaData().getColumnMetaData().length > 0 && mapping.getMemberMetaData().getColumnMetaData()[0].getName().equals(nullColumn)) {
                    if (mapping instanceof IntegerMapping || mapping instanceof BigIntegerMapping || mapping instanceof LongMapping || mapping instanceof ShortMapping) {
                        Object convertedValue = null;
                        try {
                            if (mapping instanceof IntegerMapping || mapping instanceof ShortMapping) {
                                convertedValue = Integer.valueOf(nullValue);
                            }
                            else if (mapping instanceof LongMapping || mapping instanceof BigIntegerMapping) {
                                convertedValue = Long.valueOf(nullValue);
                            }
                        }
                        catch (Exception ex) {}
                        mapping.setObject(ec, ps, posMapping, convertedValue);
                    }
                    else {
                        mapping.setObject(ec, ps, posMapping, nullValue);
                    }
                }
                else if (mapping.getNumberOfDatastoreMappings() > 0) {
                    mapping.setObject(ec, ps, posMapping, null);
                }
            }
        }
        else {
            final ApiAdapter api = ec.getApiAdapter();
            if (!api.isPersistable(value)) {
                throw new NucleusException(EmbeddedMapping.LOCALISER_RDBMS.msg("041016", value.getClass(), value)).setFatal();
            }
            final AbstractClassMetaData embCmd = ec.getMetaDataManager().getMetaDataForClass(value.getClass(), ec.getClassLoaderResolver());
            ObjectProvider embSM = ec.findObjectProvider(value);
            if (embSM == null || api.getExecutionContext(value) == null) {
                embSM = ec.newObjectProviderForEmbedded(value, false, ownerOP, ownerFieldNumber);
                embSM.setPcObjectType(this.objectType);
            }
            int n2 = 0;
            if (this.discrimMapping != null) {
                if (this.discrimMetaData.getStrategy() == DiscriminatorStrategy.CLASS_NAME) {
                    this.discrimMapping.setObject(ec, ps, new int[] { param[n2] }, value.getClass().getName());
                }
                else if (this.discrimMetaData.getStrategy() == DiscriminatorStrategy.VALUE_MAP) {
                    final DiscriminatorMetaData valueDismd = embCmd.getInheritanceMetaData().getDiscriminatorMetaData();
                    this.discrimMapping.setObject(ec, ps, new int[] { param[n2] }, valueDismd.getValue());
                }
                ++n2;
            }
            for (int k = 0; k < this.javaTypeMappings.size(); ++k) {
                final JavaTypeMapping mapping2 = this.javaTypeMappings.get(k);
                final int[] posMapping2 = new int[mapping2.getNumberOfDatastoreMappings()];
                for (int l = 0; l < posMapping2.length; ++l) {
                    posMapping2[l] = param[n2++];
                }
                final int embAbsFieldNum = embCmd.getAbsolutePositionOfMember(mapping2.getMemberMetaData().getName());
                if (embAbsFieldNum >= 0) {
                    final Object fieldValue = embSM.provideField(embAbsFieldNum);
                    if (mapping2 instanceof EmbeddedPCMapping) {
                        mapping2.setObject(ec, ps, posMapping2, fieldValue, embSM, embAbsFieldNum);
                    }
                    else if (mapping2.getNumberOfDatastoreMappings() > 0) {
                        mapping2.setObject(ec, ps, posMapping2, fieldValue);
                    }
                }
                else {
                    mapping2.setObject(ec, ps, posMapping2, null);
                }
            }
        }
    }
    
    @Override
    public Object getObject(final ExecutionContext ec, final ResultSet rs, final int[] param) {
        return this.getObject(ec, rs, param, null, -1);
    }
    
    @Override
    public Object getObject(final ExecutionContext ec, final ResultSet rs, final int[] param, final ObjectProvider ownerOP, final int ownerFieldNumber) {
        Object value = null;
        int n = 0;
        AbstractClassMetaData embCmd = this.embCmd;
        if (this.discrimMapping != null) {
            final Object discrimValue = this.discrimMapping.getObject(ec, rs, new int[] { param[n] });
            final String className = ec.getMetaDataManager().getClassNameFromDiscriminatorValue((String)discrimValue, this.discrimMetaData);
            embCmd = this.storeMgr.getMetaDataManager().getMetaDataForClass(className, this.clr);
            ++n;
        }
        Class embeddedType = this.clr.classForName(embCmd.getFullClassName());
        if (this.mmd.getFieldTypes() != null && this.mmd.getFieldTypes().length > 0) {
            embeddedType = ec.getClassLoaderResolver().classForName(this.mmd.getFieldTypes()[0]);
        }
        final ObjectProvider embOP = ec.newObjectProviderForHollow(embeddedType, null);
        embOP.setPcObjectType(this.objectType);
        value = embOP.getObject();
        String nullColumn = null;
        String nullValue = null;
        if (this.emd != null) {
            nullColumn = this.emd.getNullIndicatorColumn();
            nullValue = this.emd.getNullIndicatorValue();
        }
        for (int i = 0; i < this.javaTypeMappings.size(); ++i) {
            final JavaTypeMapping mapping = this.javaTypeMappings.get(i);
            final int embAbsFieldNum = embCmd.getAbsolutePositionOfMember(mapping.getMemberMetaData().getName());
            if (embAbsFieldNum >= 0) {
                if (mapping instanceof EmbeddedPCMapping) {
                    final int numSubParams = mapping.getNumberOfDatastoreMappings();
                    final int[] subParam = new int[numSubParams];
                    int k = 0;
                    for (int j = n; j < n + numSubParams; ++j) {
                        subParam[k++] = param[j];
                    }
                    n += numSubParams;
                    final Object subValue = mapping.getObject(ec, rs, subParam, embOP, embAbsFieldNum);
                    if (subValue != null) {
                        embOP.replaceField(embAbsFieldNum, subValue);
                    }
                }
                else {
                    final int[] posMapping = new int[mapping.getNumberOfDatastoreMappings()];
                    for (int l = 0; l < posMapping.length; ++l) {
                        posMapping[l] = param[n++];
                    }
                    final Object fieldValue = mapping.getObject(ec, rs, posMapping);
                    if (nullColumn != null && mapping.getMemberMetaData().getColumnMetaData()[0].getName().equals(nullColumn) && ((nullValue == null && fieldValue == null) || (nullValue != null && fieldValue.toString().equals(nullValue)))) {
                        value = null;
                        break;
                    }
                    if (fieldValue != null) {
                        embOP.replaceField(embAbsFieldNum, fieldValue);
                    }
                    else {
                        final AbstractMemberMetaData embFmd = embCmd.getMetaDataForManagedMemberAtAbsolutePosition(embAbsFieldNum);
                        if (!embFmd.getType().isPrimitive()) {
                            embOP.replaceField(embAbsFieldNum, fieldValue);
                        }
                    }
                }
            }
            else {
                final int numSubParams = mapping.getNumberOfDatastoreMappings();
                n += numSubParams;
            }
        }
        if (this.emd != null) {
            final String ownerField = this.emd.getOwnerMember();
            if (ownerField != null) {
                final int ownerFieldNumberInElement = embCmd.getAbsolutePositionOfMember(ownerField);
                if (ownerFieldNumberInElement >= 0) {
                    embOP.replaceField(ownerFieldNumberInElement, ownerOP.getObject());
                }
            }
        }
        if (value != null && ownerOP != null) {
            ec.registerEmbeddedRelation(ownerOP, ownerFieldNumber, embOP);
        }
        return value;
    }
    
    @Override
    public Class getJavaType() {
        return this.clr.classForName(this.typeName);
    }
}
