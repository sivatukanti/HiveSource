// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.metadata;

import java.util.ArrayList;
import java.util.List;

public class StoredProcQueryMetaData extends MetaData
{
    String name;
    String procedureName;
    List<StoredProcQueryParameterMetaData> parameters;
    List<String> resultClasses;
    List<String> resultSetMappings;
    
    public StoredProcQueryMetaData(final String name) {
        this.name = name;
    }
    
    public StoredProcQueryMetaData setName(final String name) {
        this.name = name;
        return this;
    }
    
    public StoredProcQueryMetaData setProcedureName(final String name) {
        this.procedureName = name;
        return this;
    }
    
    public StoredProcQueryMetaData addParameter(final StoredProcQueryParameterMetaData param) {
        if (this.parameters == null) {
            this.parameters = new ArrayList<StoredProcQueryParameterMetaData>(1);
        }
        this.parameters.add(param);
        return this;
    }
    
    public StoredProcQueryMetaData addResultClass(final String resultClass) {
        if (this.resultClasses == null) {
            this.resultClasses = new ArrayList<String>(1);
        }
        this.resultClasses.add(resultClass);
        return this;
    }
    
    public StoredProcQueryMetaData addResultSetMapping(final String mapping) {
        if (this.resultSetMappings == null) {
            this.resultSetMappings = new ArrayList<String>(1);
        }
        this.resultSetMappings.add(mapping);
        return this;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getProcedureName() {
        return this.procedureName;
    }
    
    public List<StoredProcQueryParameterMetaData> getParameters() {
        return this.parameters;
    }
    
    public List<String> getResultClasses() {
        return this.resultClasses;
    }
    
    public List<String> getResultSetMappings() {
        return this.resultSetMappings;
    }
}
