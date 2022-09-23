// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.query;

import java.util.HashSet;
import org.datanucleus.metadata.StoredProcQueryParameterMode;
import java.util.Collection;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.store.Extent;
import org.datanucleus.ExecutionContext;
import org.datanucleus.store.StoreManager;
import java.util.Map;
import org.datanucleus.metadata.QueryResultMetaData;
import java.util.Set;

public abstract class AbstractStoredProcedureQuery extends Query
{
    protected String procedureName;
    protected Set<StoredProcedureParameter> storedProcParams;
    protected int resultSetNumber;
    protected QueryResultMetaData[] resultMetaDatas;
    protected Class[] resultClasses;
    protected Map outputParamValues;
    
    public AbstractStoredProcedureQuery(final StoreManager storeMgr, final ExecutionContext ec, final AbstractStoredProcedureQuery query) {
        this(storeMgr, ec, query.procedureName);
    }
    
    public AbstractStoredProcedureQuery(final StoreManager storeMgr, final ExecutionContext ec, final String procName) {
        super(storeMgr, ec);
        this.storedProcParams = null;
        this.resultSetNumber = 0;
        this.resultMetaDatas = null;
        this.resultClasses = null;
        this.outputParamValues = null;
        this.procedureName = procName;
    }
    
    @Override
    public String getLanguage() {
        return "STOREDPROC";
    }
    
    @Override
    public void setCandidates(final Extent pcs) {
        throw new NucleusUserException("Not supported for stored procedures");
    }
    
    @Override
    public void setCandidates(final Collection pcs) {
        throw new NucleusUserException("Not supported for stored procedures");
    }
    
    public void setResultMetaData(final QueryResultMetaData[] qrmds) {
        this.resultMetaDatas = qrmds;
        this.resultClasses = null;
    }
    
    public void setResultClasses(final Class[] resultClasses) {
        this.resultClasses = resultClasses;
        this.resultMetaDatas = null;
    }
    
    public void registerParameter(final int pos, final Class type, final StoredProcQueryParameterMode mode) {
        if (this.storedProcParams == null) {
            this.storedProcParams = new HashSet<StoredProcedureParameter>();
        }
        final StoredProcedureParameter param = new StoredProcedureParameter(mode, pos, type);
        this.storedProcParams.add(param);
    }
    
    public void registerParameter(final String name, final Class type, final StoredProcQueryParameterMode mode) {
        if (this.storedProcParams == null) {
            this.storedProcParams = new HashSet<StoredProcedureParameter>();
        }
        final StoredProcedureParameter param = new StoredProcedureParameter(mode, name, type);
        this.storedProcParams.add(param);
    }
    
    public abstract boolean hasMoreResults();
    
    public abstract Object getNextResults();
    
    public abstract int getUpdateCount();
    
    public Object getOutputParameterValue(final int pos) {
        if (this.outputParamValues != null) {
            return this.outputParamValues.get(pos);
        }
        return null;
    }
    
    public Object getOutputParameterValue(final String name) {
        if (this.outputParamValues != null) {
            return this.outputParamValues.get(name);
        }
        return null;
    }
    
    public static class StoredProcedureParameter
    {
        StoredProcQueryParameterMode mode;
        Integer position;
        String name;
        Class type;
        
        public StoredProcedureParameter(final StoredProcQueryParameterMode mode, final int pos, final Class type) {
            this.mode = mode;
            this.position = pos;
            this.type = type;
        }
        
        public StoredProcedureParameter(final StoredProcQueryParameterMode mode, final String name, final Class type) {
            this.mode = mode;
            this.name = name;
            this.type = type;
        }
        
        public String getName() {
            return this.name;
        }
        
        public Integer getPosition() {
            return this.position;
        }
        
        public StoredProcQueryParameterMode getMode() {
            return this.mode;
        }
        
        public Class getType() {
            return this.type;
        }
    }
}
