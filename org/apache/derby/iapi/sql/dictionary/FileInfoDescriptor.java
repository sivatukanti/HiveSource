// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql.dictionary;

import org.apache.derby.catalog.DependableFinder;
import org.apache.derby.catalog.UUID;
import org.apache.derby.iapi.sql.depend.Provider;

public final class FileInfoDescriptor extends TupleDescriptor implements Provider, UniqueSQLObjectDescriptor
{
    public static final int JAR_FILE_TYPE = 0;
    private final UUID id;
    private final SchemaDescriptor sd;
    private final String sqlName;
    private final long generationId;
    
    public FileInfoDescriptor(final DataDictionary dataDictionary, final UUID id, final SchemaDescriptor sd, final String sqlName, final long generationId) {
        super(dataDictionary);
        this.id = id;
        this.sd = sd;
        this.sqlName = sqlName;
        this.generationId = generationId;
    }
    
    public SchemaDescriptor getSchemaDescriptor() {
        return this.sd;
    }
    
    public String getName() {
        return this.sqlName;
    }
    
    public UUID getUUID() {
        return this.id;
    }
    
    public long getGenerationId() {
        return this.generationId;
    }
    
    public DependableFinder getDependableFinder() {
        return this.getDependableFinder(273);
    }
    
    public String getObjectName() {
        return this.sqlName;
    }
    
    public UUID getObjectID() {
        return this.id;
    }
    
    public String getClassType() {
        return "File";
    }
    
    public String getDescriptorType() {
        return "Jar file";
    }
    
    public String getDescriptorName() {
        return this.sqlName;
    }
}
