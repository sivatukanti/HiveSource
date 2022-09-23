// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.mapping;

import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.ClassConstants;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import org.datanucleus.util.ClassUtils;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.store.rdbms.table.DatastoreClass;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.FetchPlan;
import org.datanucleus.state.ObjectProvider;
import org.datanucleus.store.fieldmanager.FieldManager;
import org.datanucleus.store.FieldValues;
import org.datanucleus.identity.IdentityUtils;
import org.datanucleus.metadata.ClassMetaData;
import org.datanucleus.api.ApiAdapter;
import org.datanucleus.util.NucleusLogger;
import org.datanucleus.identity.OIDFactory;
import org.datanucleus.metadata.AbstractClassMetaData;
import java.sql.ResultSet;
import org.datanucleus.ExecutionContext;
import org.datanucleus.store.rdbms.mapping.java.JavaTypeMapping;
import org.datanucleus.util.Localiser;

public class MappingHelper
{
    protected static final Localiser LOCALISER;
    protected static final Localiser LOCALISER_RDBMS;
    
    public static int[] getMappingIndices(final int initialPosition, final JavaTypeMapping mapping) {
        if (mapping.getNumberOfDatastoreMappings() < 1) {
            return new int[] { initialPosition };
        }
        final int[] parameter = new int[mapping.getNumberOfDatastoreMappings()];
        for (int i = 0; i < parameter.length; ++i) {
            parameter[i] = initialPosition + i;
        }
        return parameter;
    }
    
    public static Object getObjectForDatastoreIdentity(final ExecutionContext ec, final JavaTypeMapping mapping, final ResultSet rs, final int[] resultIndexes, final AbstractClassMetaData cmd) {
        Object oid = null;
        if (mapping.getNumberOfDatastoreMappings() > 0) {
            oid = mapping.getDatastoreMapping(0).getObject(rs, resultIndexes[0]);
        }
        else {
            if (mapping.getReferenceMapping() != null) {
                return mapping.getReferenceMapping().getObject(ec, rs, resultIndexes);
            }
            final Class fieldType = mapping.getMemberMetaData().getType();
            final JavaTypeMapping referenceMapping = mapping.getStoreManager().getDatastoreClass(fieldType.getName(), ec.getClassLoaderResolver()).getIdMapping();
            oid = referenceMapping.getDatastoreMapping(0).getObject(rs, resultIndexes[0]);
        }
        if (oid != null) {
            oid = OIDFactory.getInstance(ec.getNucleusContext(), mapping.getType(), oid);
            if (NucleusLogger.PERSISTENCE.isDebugEnabled()) {
                NucleusLogger.PERSISTENCE.debug(MappingHelper.LOCALISER_RDBMS.msg("041034", oid));
            }
        }
        final ApiAdapter api = ec.getApiAdapter();
        if (api.isPersistable(oid)) {
            return oid;
        }
        return (oid == null) ? null : ec.findObject(oid, false, true, null);
    }
    
    public static Object getObjectForApplicationIdentity(final ExecutionContext ec, final JavaTypeMapping mapping, final ResultSet rs, final int[] resultIndexes, final AbstractClassMetaData cmd) {
        final ClassLoaderResolver clr = ec.getClassLoaderResolver();
        if (((ClassMetaData)cmd).isAbstract() && cmd.getObjectidClass() != null) {
            return getObjectForAbstractClass(ec, mapping, rs, resultIndexes, cmd);
        }
        final int totalFieldCount = cmd.getNoOfManagedMembers() + cmd.getNoOfInheritedManagedMembers();
        final StatementMappingIndex[] statementExpressionIndex = new StatementMappingIndex[totalFieldCount];
        int paramIndex = 0;
        final DatastoreClass datastoreClass = mapping.getStoreManager().getDatastoreClass(cmd.getFullClassName(), clr);
        final int[] pkFieldNumbers = cmd.getPKMemberPositions();
        for (int i = 0; i < pkFieldNumbers.length; ++i) {
            final AbstractMemberMetaData fmd = cmd.getMetaDataForManagedMemberAtAbsolutePosition(pkFieldNumbers[i]);
            final JavaTypeMapping m = datastoreClass.getMemberMapping(fmd);
            statementExpressionIndex[fmd.getAbsoluteFieldNumber()] = new StatementMappingIndex(m);
            final int[] expressionsIndex = new int[m.getNumberOfDatastoreMappings()];
            for (int j = 0; j < expressionsIndex.length; ++j) {
                expressionsIndex[j] = resultIndexes[paramIndex++];
            }
            statementExpressionIndex[fmd.getAbsoluteFieldNumber()].setColumnPositions(expressionsIndex);
        }
        final StatementClassMapping resultMappings = new StatementClassMapping();
        for (int k = 0; k < pkFieldNumbers.length; ++k) {
            resultMappings.addMappingForMember(pkFieldNumbers[k], statementExpressionIndex[pkFieldNumbers[k]]);
        }
        final FieldManager resultsFM = mapping.getStoreManager().getFieldManagerForResultProcessing(ec, rs, resultMappings, cmd);
        final Object id = IdentityUtils.getApplicationIdentityForResultSetRow(ec, cmd, null, false, resultsFM);
        final Class type = ec.getClassLoaderResolver().classForName(cmd.getFullClassName());
        return ec.findObject(id, new FieldValues() {
            @Override
            public void fetchFields(final ObjectProvider sm) {
                sm.replaceFields(pkFieldNumbers, resultsFM);
            }
            
            @Override
            public void fetchNonLoadedFields(final ObjectProvider sm) {
                sm.replaceNonLoadedFields(pkFieldNumbers, resultsFM);
            }
            
            @Override
            public FetchPlan getFetchPlanForLoading() {
                return ec.getFetchPlan();
            }
        }, type, false, true);
    }
    
    protected static Object createSingleFieldIdentity(final ExecutionContext ec, final JavaTypeMapping mapping, final ResultSet rs, final int[] param, final AbstractClassMetaData cmd, final Class objectIdClass, final Class pcClass) {
        final int paramNumber = param[0];
        try {
            Object idObj = mapping.getStoreManager().getResultValueAtPosition(rs, mapping, paramNumber);
            if (idObj == null) {
                throw new NucleusException(MappingHelper.LOCALISER.msg("041039")).setFatal();
            }
            final Class keyType = ec.getApiAdapter().getKeyTypeForSingleFieldIdentityType(objectIdClass);
            idObj = ClassUtils.convertValue(idObj, keyType);
            return ec.getApiAdapter().getNewSingleFieldIdentity(objectIdClass, pcClass, idObj);
        }
        catch (Exception e) {
            NucleusLogger.PERSISTENCE.error(MappingHelper.LOCALISER.msg("041036", cmd.getObjectidClass(), e));
            return null;
        }
    }
    
    protected static Object createObjectIdInstanceReflection(final ExecutionContext ec, final JavaTypeMapping mapping, final ResultSet rs, final int[] param, final AbstractClassMetaData cmd, final Class objectIdClass) {
        Object fieldValue = null;
        try {
            final Object id = objectIdClass.newInstance();
            int paramIndex = 0;
            final int[] pkFieldNums = cmd.getPKMemberPositions();
            for (int i = 0; i < pkFieldNums.length; ++i) {
                final AbstractMemberMetaData fmd = cmd.getMetaDataForManagedMemberAtAbsolutePosition(pkFieldNums[i]);
                final Field field = objectIdClass.getField(fmd.getName());
                final JavaTypeMapping m = mapping.getStoreManager().getDatastoreClass(cmd.getFullClassName(), ec.getClassLoaderResolver()).getMemberMapping(fmd);
                for (int j = 0; j < m.getNumberOfDatastoreMappings(); ++j) {
                    Object obj = mapping.getStoreManager().getResultValueAtPosition(rs, mapping, param[paramIndex++]);
                    if (obj instanceof BigDecimal) {
                        final BigDecimal bigDecimal = (BigDecimal)obj;
                        final Class keyType = ec.getApiAdapter().getKeyTypeForSingleFieldIdentityType(field.getType());
                        obj = ClassUtils.convertValue(bigDecimal, keyType);
                        if (!bigDecimal.subtract(new BigDecimal("" + obj)).equals(new BigDecimal("0"))) {
                            throw new NucleusException("Cannot convert retrieved BigInteger value to field of object id class!").setFatal();
                        }
                    }
                    fieldValue = obj;
                }
                field.set(id, fieldValue);
            }
            return id;
        }
        catch (Exception e) {
            final AbstractMemberMetaData mmd = mapping.getMemberMetaData();
            NucleusLogger.PERSISTENCE.error(MappingHelper.LOCALISER.msg("041037", cmd.getObjectidClass(), (mmd == null) ? null : mmd.getName(), fieldValue, e));
            return null;
        }
    }
    
    protected static Object getObjectForAbstractClass(final ExecutionContext ec, final JavaTypeMapping mapping, final ResultSet rs, final int[] resultIndexes, final AbstractClassMetaData cmd) {
        final ClassLoaderResolver clr = ec.getClassLoaderResolver();
        final Class objectIdClass = clr.classForName(cmd.getObjectidClass());
        final Class pcClass = clr.classForName(cmd.getFullClassName());
        Object id;
        if (cmd.usesSingleFieldIdentityClass()) {
            id = createSingleFieldIdentity(ec, mapping, rs, resultIndexes, cmd, objectIdClass, pcClass);
        }
        else {
            id = createObjectIdInstanceReflection(ec, mapping, rs, resultIndexes, cmd, objectIdClass);
        }
        return ec.findObject(id, false, true, null);
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
        LOCALISER_RDBMS = Localiser.getInstance("org.datanucleus.store.rdbms.Localisation", RDBMSStoreManager.class.getClassLoader());
    }
}
