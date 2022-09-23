// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.mapping.java;

import org.datanucleus.identity.OIDFactory;
import org.datanucleus.identity.OID;
import org.datanucleus.exceptions.NucleusObjectNotFoundException;
import org.datanucleus.store.rdbms.exceptions.NullValueException;
import org.datanucleus.util.StringUtils;
import org.datanucleus.exceptions.NucleusUserException;
import java.sql.ResultSet;
import org.datanucleus.ExecutionContext;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.store.rdbms.table.Table;
import org.datanucleus.metadata.AbstractMemberMetaData;

public class InterfaceMapping extends ReferenceMapping
{
    private String implementationClasses;
    
    @Override
    public void initialize(final AbstractMemberMetaData mmd, final Table table, final ClassLoaderResolver clr) {
        super.initialize(mmd, table, clr);
        if (mmd.getType().isInterface() && mmd.getFieldTypes() != null && mmd.getFieldTypes().length == 1) {
            final Class fieldTypeCls = clr.classForName(mmd.getFieldTypes()[0]);
            if (fieldTypeCls.isInterface()) {
                this.type = mmd.getFieldTypes()[0];
            }
        }
    }
    
    public void setImplementationClasses(final String implementationClasses) {
        this.implementationClasses = implementationClasses;
    }
    
    @Override
    public Object getObject(final ExecutionContext ec, final ResultSet rs, final int[] pos) {
        if (!ec.getMetaDataManager().isPersistentInterface(this.type)) {
            return super.getObject(ec, rs, pos);
        }
        if (this.mappingStrategy == 1 || this.mappingStrategy == 2) {
            throw new NucleusUserException("DataNucleus does not support use of mapping-strategy=" + this.mappingStrategy + " with a \"persistable interface\"");
        }
        String[] implTypes = null;
        if (this.implementationClasses != null) {
            implTypes = StringUtils.split(this.implementationClasses, ",");
        }
        else {
            implTypes = ec.getMetaDataManager().getClassesImplementingInterface(this.getType(), ec.getClassLoaderResolver());
        }
        int n = 0;
        for (int i = 0; i < implTypes.length; ++i) {
            JavaTypeMapping mapping;
            if (implTypes.length > this.javaTypeMappings.length) {
                final PersistableMapping m = (PersistableMapping)this.javaTypeMappings[0];
                mapping = this.storeMgr.getMappingManager().getMapping(ec.getClassLoaderResolver().classForName(implTypes[i]));
                for (int j = 0; j < m.getDatastoreMappings().length; ++j) {
                    mapping.addDatastoreMapping(m.getDatastoreMappings()[j]);
                }
                for (int j = 0; j < m.getJavaTypeMapping().length; ++j) {
                    ((PersistableMapping)mapping).addJavaTypeMapping(m.getJavaTypeMapping()[j]);
                }
                ((PersistableMapping)mapping).setReferenceMapping(m.getReferenceMapping());
            }
            else {
                mapping = this.javaTypeMappings[i];
            }
            if (n >= pos.length) {
                n = 0;
            }
            int[] posMapping;
            if (mapping.getReferenceMapping() != null) {
                posMapping = new int[mapping.getReferenceMapping().getNumberOfDatastoreMappings()];
            }
            else {
                posMapping = new int[mapping.getNumberOfDatastoreMappings()];
            }
            for (int j = 0; j < posMapping.length; ++j) {
                posMapping[j] = pos[n++];
            }
            Object value = null;
            try {
                value = mapping.getObject(ec, rs, posMapping);
            }
            catch (NullValueException e) {}
            catch (NucleusObjectNotFoundException ex) {}
            if (value != null) {
                if (value instanceof OID) {
                    String className;
                    if (mapping.getReferenceMapping() != null) {
                        className = mapping.getReferenceMapping().getDatastoreMapping(0).getColumn().getStoredJavaType();
                    }
                    else {
                        className = mapping.getDatastoreMapping(0).getColumn().getStoredJavaType();
                    }
                    value = OIDFactory.getInstance(ec.getNucleusContext(), className, ((OID)value).getKeyValue());
                    return ec.findObject(value, false, true, null);
                }
                if (ec.getClassLoaderResolver().classForName(this.getType()).isAssignableFrom(value.getClass())) {
                    return value;
                }
            }
        }
        return null;
    }
}
