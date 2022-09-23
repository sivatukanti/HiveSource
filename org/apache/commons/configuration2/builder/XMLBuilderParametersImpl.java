// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.builder;

import org.xml.sax.EntityResolver;
import javax.xml.parsers.DocumentBuilder;
import java.util.Map;

public class XMLBuilderParametersImpl extends HierarchicalBuilderParametersImpl implements XMLBuilderProperties<XMLBuilderParametersImpl>
{
    private static final String PROP_ENTITY_RESOLVER = "entityResolver";
    private static final String PROP_DOCUMENT_BUILDER = "documentBuilder";
    private static final String PROP_PUBLIC_ID = "publicID";
    private static final String PROP_SYSTEM_ID = "systemID";
    private static final String PROP_VALIDATING = "validating";
    private static final String PROP_SCHEMA_VALIDATION = "schemaValidation";
    
    @Override
    public void inheritFrom(final Map<String, ?> source) {
        super.inheritFrom(source);
        this.copyPropertiesFrom(source, "documentBuilder", "entityResolver", "schemaValidation", "validating");
    }
    
    @Override
    public XMLBuilderParametersImpl setDocumentBuilder(final DocumentBuilder docBuilder) {
        this.storeProperty("documentBuilder", docBuilder);
        return this;
    }
    
    @Override
    public XMLBuilderParametersImpl setEntityResolver(final EntityResolver resolver) {
        this.storeProperty("entityResolver", resolver);
        return this;
    }
    
    public EntityResolver getEntityResolver() {
        return (EntityResolver)this.fetchProperty("entityResolver");
    }
    
    @Override
    public XMLBuilderParametersImpl setPublicID(final String pubID) {
        this.storeProperty("publicID", pubID);
        return this;
    }
    
    @Override
    public XMLBuilderParametersImpl setSystemID(final String sysID) {
        this.storeProperty("systemID", sysID);
        return this;
    }
    
    @Override
    public XMLBuilderParametersImpl setValidating(final boolean f) {
        this.storeProperty("validating", f);
        return this;
    }
    
    @Override
    public XMLBuilderParametersImpl setSchemaValidation(final boolean f) {
        this.storeProperty("schemaValidation", f);
        return this;
    }
}
