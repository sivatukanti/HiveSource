// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.identifier;

import org.datanucleus.store.rdbms.adapter.DatastoreAdapter;

public class DatastoreIdentifierImpl implements DatastoreIdentifier
{
    protected final DatastoreAdapter dba;
    protected final IdentifierFactory factory;
    protected String identifierName;
    protected String catalogName;
    protected String schemaName;
    private String toString;
    
    protected DatastoreIdentifierImpl(final IdentifierFactory factory, final String sqlIdentifier) {
        this.dba = factory.getDatastoreAdapter();
        this.factory = factory;
        this.identifierName = this.toCase(sqlIdentifier);
    }
    
    protected String toCase(final String identifierName) {
        if (this.factory.getIdentifierCase() == IdentifierCase.LOWER_CASE || this.factory.getIdentifierCase() == IdentifierCase.LOWER_CASE_QUOTED) {
            return identifierName.toLowerCase();
        }
        if (this.factory.getIdentifierCase() == IdentifierCase.UPPER_CASE || this.factory.getIdentifierCase() == IdentifierCase.UPPER_CASE_QUOTED) {
            return identifierName.toUpperCase();
        }
        return identifierName;
    }
    
    @Override
    public String getIdentifierName() {
        return this.identifierName;
    }
    
    @Override
    public void setCatalogName(final String catalogName) {
        this.catalogName = catalogName;
    }
    
    @Override
    public void setSchemaName(final String schemaName) {
        this.schemaName = schemaName;
    }
    
    @Override
    public String getCatalogName() {
        return this.catalogName;
    }
    
    @Override
    public String getSchemaName() {
        return this.schemaName;
    }
    
    @Override
    public int hashCode() {
        return this.identifierName.hashCode();
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof DatastoreIdentifierImpl)) {
            return false;
        }
        final DatastoreIdentifierImpl id = (DatastoreIdentifierImpl)obj;
        if (this.identifierName.equals(id.identifierName)) {
            if (this.schemaName != null) {
                if (id.schemaName != null && !this.schemaName.equals(id.schemaName)) {
                    return false;
                }
            }
            if (this.catalogName != null) {
                if (id.catalogName != null && !this.catalogName.equals(id.catalogName)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
    @Override
    public String toString() {
        if (this.toString == null) {
            final String identifierQuoteString = this.dba.getIdentifierQuoteString();
            if (this.dba.isReservedKeyword(this.identifierName)) {
                this.toString = identifierQuoteString + this.identifierName + identifierQuoteString;
            }
            else if (this.factory.getIdentifierCase() == IdentifierCase.LOWER_CASE_QUOTED || this.factory.getIdentifierCase() == IdentifierCase.MIXED_CASE_QUOTED || this.factory.getIdentifierCase() == IdentifierCase.UPPER_CASE_QUOTED) {
                this.toString = identifierQuoteString + this.identifierName + identifierQuoteString;
            }
            else {
                this.toString = this.identifierName;
            }
        }
        return this.toString;
    }
    
    @Override
    public final String getFullyQualifiedName(final boolean adapterCase) {
        final boolean supportsCatalogName = this.dba.supportsOption("CatalogInTableDefinition");
        final boolean supportsSchemaName = this.dba.supportsOption("SchemaInTableDefinition");
        final String separator = this.dba.getCatalogSeparator();
        final StringBuffer name = new StringBuffer();
        if (supportsCatalogName && this.catalogName != null) {
            if (adapterCase) {
                name.append(this.factory.getIdentifierInAdapterCase(this.catalogName));
            }
            else {
                name.append(this.catalogName);
            }
            name.append(separator);
        }
        if (supportsSchemaName && this.schemaName != null) {
            if (adapterCase) {
                name.append(this.factory.getIdentifierInAdapterCase(this.schemaName));
            }
            else {
                name.append(this.schemaName);
            }
            name.append(separator);
        }
        if (adapterCase) {
            name.append(this.factory.getIdentifierInAdapterCase(this.toString()));
        }
        else {
            name.append(this.toString());
        }
        return name.toString();
    }
}
