// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.identifier;

import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.metadata.AbstractMemberMetaData;
import java.util.Map;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.store.rdbms.adapter.DatastoreAdapter;

public class JPAIdentifierFactory extends AbstractIdentifierFactory
{
    public JPAIdentifierFactory(final DatastoreAdapter dba, final ClassLoaderResolver clr, final Map props) {
        super(dba, clr, props);
    }
    
    @Override
    public DatastoreIdentifier newTableIdentifier(final AbstractMemberMetaData mmd) {
        String identifierName = null;
        String schemaName = null;
        String catalogName = null;
        AbstractMemberMetaData[] relatedMmds = null;
        if (mmd.getColumnMetaData().length > 0 && mmd.getColumnMetaData()[0].getName() != null) {
            identifierName = mmd.getColumnMetaData()[0].getName();
        }
        else if (mmd.hasContainer()) {
            if (mmd.getTable() != null) {
                final String specifiedName = mmd.getTable();
                final String[] parts = this.getIdentifierNamePartsFromName(specifiedName);
                if (parts != null) {
                    catalogName = parts[0];
                    schemaName = parts[1];
                    identifierName = parts[2];
                }
                if (catalogName == null) {
                    catalogName = mmd.getCatalog();
                }
                if (schemaName == null) {
                    schemaName = mmd.getSchema();
                }
            }
            else {
                relatedMmds = mmd.getRelatedMemberMetaData(this.clr);
                if (relatedMmds != null && relatedMmds[0].getTable() != null) {
                    final String specifiedName = relatedMmds[0].getTable();
                    final String[] parts = this.getIdentifierNamePartsFromName(specifiedName);
                    if (parts != null) {
                        catalogName = parts[0];
                        schemaName = parts[1];
                        identifierName = parts[2];
                    }
                    if (catalogName == null) {
                        catalogName = relatedMmds[0].getCatalog();
                    }
                    if (schemaName == null) {
                        schemaName = relatedMmds[0].getSchema();
                    }
                }
            }
        }
        if (schemaName == null && catalogName == null) {
            if (mmd.getParent() instanceof AbstractClassMetaData) {
                final AbstractClassMetaData ownerCmd = (AbstractClassMetaData)mmd.getParent();
                if (this.dba.supportsOption("CatalogInTableDefinition")) {
                    catalogName = ownerCmd.getCatalog();
                }
                if (this.dba.supportsOption("SchemaInTableDefinition")) {
                    schemaName = ownerCmd.getSchema();
                }
            }
            if (schemaName == null && catalogName == null) {
                if (this.dba.supportsOption("CatalogInTableDefinition")) {
                    catalogName = this.defaultCatalogName;
                }
                if (this.dba.supportsOption("SchemaInTableDefinition")) {
                    schemaName = this.defaultSchemaName;
                }
            }
        }
        if (identifierName == null) {
            String ownerClass = mmd.getClassName(false);
            String otherClass = mmd.getTypeName();
            if (mmd.hasCollection()) {
                otherClass = mmd.getCollection().getElementType();
            }
            else if (mmd.hasArray()) {
                otherClass = mmd.getArray().getElementType();
            }
            else if (mmd.hasMap()) {
                otherClass = mmd.getMap().getValueType();
            }
            if (mmd.hasCollection() && relatedMmds != null && relatedMmds[0].hasCollection() && mmd.getMappedBy() != null) {
                ownerClass = relatedMmds[0].getClassName(false);
                otherClass = relatedMmds[0].getCollection().getElementType();
            }
            otherClass = otherClass.substring(otherClass.lastIndexOf(46) + 1);
            final String unique_name = identifierName = ownerClass + this.getWordSeparator() + otherClass;
        }
        final DatastoreIdentifier identifier = this.newTableIdentifier(identifierName);
        if (schemaName != null) {
            identifier.setSchemaName(schemaName);
        }
        if (catalogName != null) {
            identifier.setCatalogName(catalogName);
        }
        return identifier;
    }
    
    @Override
    public DatastoreIdentifier newTableIdentifier(final AbstractClassMetaData cmd) {
        String identifierName = null;
        String schemaName = null;
        String catalogName = null;
        final String specifiedName = cmd.getTable();
        final String[] parts = this.getIdentifierNamePartsFromName(specifiedName);
        if (parts != null) {
            catalogName = parts[0];
            schemaName = parts[1];
            identifierName = parts[2];
        }
        if (schemaName == null && catalogName == null) {
            if (this.dba.supportsOption("CatalogInTableDefinition")) {
                catalogName = cmd.getCatalog();
            }
            if (this.dba.supportsOption("SchemaInTableDefinition")) {
                schemaName = cmd.getSchema();
            }
            if (schemaName == null && catalogName == null) {
                if (this.dba.supportsOption("CatalogInTableDefinition")) {
                    catalogName = this.defaultCatalogName;
                }
                if (this.dba.supportsOption("SchemaInTableDefinition")) {
                    schemaName = this.defaultSchemaName;
                }
            }
        }
        if (identifierName == null) {
            final String unique_name = identifierName = cmd.getFullClassName().substring(cmd.getFullClassName().lastIndexOf(46) + 1);
        }
        final DatastoreIdentifier identifier = this.newTableIdentifier(identifierName);
        if (schemaName != null) {
            identifier.setSchemaName(schemaName);
        }
        if (catalogName != null) {
            identifier.setCatalogName(catalogName);
        }
        return identifier;
    }
    
    @Override
    public DatastoreIdentifier newReferenceFieldIdentifier(final AbstractMemberMetaData refMetaData, final AbstractClassMetaData implMetaData, final DatastoreIdentifier implIdentifier, final boolean embedded, final int fieldRole) {
        final String key = "[" + refMetaData.getFullFieldName() + "][" + implMetaData.getFullClassName() + "][" + implIdentifier.getIdentifierName() + "]";
        DatastoreIdentifier identifier = this.references.get(key);
        if (identifier == null) {
            final String referenceName = refMetaData.getName();
            String implementationName = implMetaData.getFullClassName();
            final int dot = implementationName.lastIndexOf(46);
            if (dot > -1) {
                implementationName = implementationName.substring(dot + 1);
            }
            final String name = referenceName + "." + implementationName + "." + implIdentifier.getIdentifierName();
            final String datastoreID = this.generateIdentifierNameForJavaName(name);
            final String baseID = this.truncate(datastoreID, this.dba.getDatastoreIdentifierMaxLength(IdentifierType.COLUMN));
            identifier = new ColumnIdentifier(this, baseID);
            this.references.put(key, identifier);
        }
        return identifier;
    }
    
    @Override
    public DatastoreIdentifier newJoinTableFieldIdentifier(final AbstractMemberMetaData ownerFmd, final AbstractMemberMetaData relatedFmd, final DatastoreIdentifier destinationId, final boolean embedded, final int fieldRole) {
        DatastoreIdentifier identifier = null;
        if (relatedFmd != null) {
            if (fieldRole == 1) {
                identifier = this.newColumnIdentifier(relatedFmd.getName() + this.getWordSeparator() + destinationId.getIdentifierName());
            }
            else if (fieldRole == 3 || fieldRole == 4 || fieldRole == 5 || fieldRole == 6) {
                if (destinationId != null) {
                    identifier = this.newColumnIdentifier(ownerFmd.getName() + this.getWordSeparator() + destinationId.getIdentifierName());
                }
                else if (fieldRole == 4 || fieldRole == 3) {
                    identifier = this.newColumnIdentifier(ownerFmd.getName() + this.getWordSeparator() + "ELEMENT");
                }
                else if (fieldRole == 5) {
                    identifier = this.newColumnIdentifier(ownerFmd.getName() + this.getWordSeparator() + "KEY");
                }
                else if (fieldRole == 6) {
                    identifier = this.newColumnIdentifier(ownerFmd.getName() + this.getWordSeparator() + "VALUE");
                }
            }
            else {
                identifier = this.newColumnIdentifier(destinationId.getIdentifierName(), embedded, fieldRole);
            }
        }
        else if (fieldRole == 1) {
            identifier = this.newColumnIdentifier(ownerFmd.getClassName(false) + this.getWordSeparator() + destinationId.getIdentifierName());
        }
        else if (fieldRole == 3 || fieldRole == 4 || fieldRole == 5 || fieldRole == 6) {
            if (destinationId != null) {
                identifier = this.newColumnIdentifier(ownerFmd.getName() + this.getWordSeparator() + destinationId.getIdentifierName());
            }
            else if (fieldRole == 4 || fieldRole == 3) {
                identifier = this.newColumnIdentifier(ownerFmd.getName() + this.getWordSeparator() + "ELEMENT");
            }
            else if (fieldRole == 5) {
                identifier = this.newColumnIdentifier(ownerFmd.getName() + this.getWordSeparator() + "KEY");
            }
            else if (fieldRole == 6) {
                identifier = this.newColumnIdentifier(ownerFmd.getName() + this.getWordSeparator() + "VALUE");
            }
        }
        else {
            identifier = this.newColumnIdentifier(destinationId.getIdentifierName(), embedded, fieldRole);
        }
        return identifier;
    }
    
    @Override
    public DatastoreIdentifier newForeignKeyFieldIdentifier(final AbstractMemberMetaData ownerFmd, final AbstractMemberMetaData relatedFmd, final DatastoreIdentifier destinationId, final boolean embedded, final int fieldRole) {
        if (relatedFmd != null) {
            if (fieldRole == 1) {
                return this.newColumnIdentifier(relatedFmd.getName() + "." + destinationId.getIdentifierName(), embedded, fieldRole);
            }
            if (fieldRole == 7) {
                return this.newColumnIdentifier(relatedFmd.getName() + "." + destinationId.getIdentifierName(), embedded, fieldRole);
            }
            throw new NucleusException("Column role " + fieldRole + " not supported by this method").setFatal();
        }
        else {
            if (fieldRole == 1) {
                return this.newColumnIdentifier(ownerFmd.getName() + "." + destinationId.getIdentifierName(), embedded, fieldRole);
            }
            if (fieldRole == 7) {
                return this.newColumnIdentifier(ownerFmd.getName() + ".IDX", embedded, fieldRole);
            }
            throw new NucleusException("Column role " + fieldRole + " not supported by this method").setFatal();
        }
    }
    
    @Override
    public DatastoreIdentifier newDiscriminatorFieldIdentifier() {
        final String name = "DTYPE";
        DatastoreIdentifier identifier = this.columns.get(name);
        if (identifier == null) {
            identifier = new ColumnIdentifier(this, name);
            this.columns.put(name, identifier);
        }
        return identifier;
    }
    
    @Override
    public DatastoreIdentifier newVersionFieldIdentifier() {
        final String name = "VERSION";
        DatastoreIdentifier identifier = this.columns.get(name);
        if (identifier == null) {
            identifier = new ColumnIdentifier(this, name);
            this.columns.put(name, identifier);
        }
        return identifier;
    }
    
    @Override
    public DatastoreIdentifier newIndexFieldIdentifier(final AbstractMemberMetaData mmd) {
        final String name = mmd.getName() + this.getWordSeparator() + "ORDER";
        DatastoreIdentifier identifier = this.columns.get(name);
        if (identifier == null) {
            identifier = new ColumnIdentifier(this, name);
            this.columns.put(name, identifier);
        }
        return identifier;
    }
    
    @Override
    public DatastoreIdentifier newAdapterIndexFieldIdentifier() {
        final String name = "IDX";
        DatastoreIdentifier identifier = this.columns.get(name);
        if (identifier == null) {
            identifier = new ColumnIdentifier(this, name);
            this.columns.put(name, identifier);
        }
        return identifier;
    }
    
    public String generateIdentifierNameForJavaName(final String javaName) {
        if (javaName == null) {
            return null;
        }
        final StringBuffer s = new StringBuffer();
        for (int i = 0; i < javaName.length(); ++i) {
            final char c = javaName.charAt(i);
            if (c >= 'A' && c <= 'Z' && this.identifierCase != IdentifierCase.MIXED_CASE && this.identifierCase != IdentifierCase.MIXED_CASE_QUOTED) {
                s.append(c);
            }
            else if (c >= 'A' && c <= 'Z' && (this.identifierCase == IdentifierCase.MIXED_CASE || this.identifierCase == IdentifierCase.MIXED_CASE_QUOTED)) {
                s.append(c);
            }
            else if (c >= 'a' && c <= 'z' && (this.identifierCase == IdentifierCase.MIXED_CASE || this.identifierCase == IdentifierCase.MIXED_CASE_QUOTED)) {
                s.append(c);
            }
            else if (c >= 'a' && c <= 'z' && this.identifierCase != IdentifierCase.MIXED_CASE && this.identifierCase != IdentifierCase.MIXED_CASE_QUOTED) {
                s.append((char)(c - ' '));
            }
            else if ((c >= '0' && c <= '9') || c == '_') {
                s.append(c);
            }
            else if (c == '.') {
                s.append(this.getWordSeparator());
            }
            else {
                final String cval = "000" + Integer.toHexString(c);
                s.append(cval.substring(cval.length() - ((c > '\u00ff') ? 4 : 2)));
            }
        }
        while (s.length() > 0 && s.charAt(0) == '_') {
            s.deleteCharAt(0);
        }
        if (s.length() == 0) {
            throw new IllegalArgumentException("Illegal Java identifier: " + javaName);
        }
        return s.toString();
    }
    
    @Override
    protected String getColumnIdentifierSuffix(final int role, final boolean embedded) {
        String suffix = "";
        if (role == 0) {
            suffix = (embedded ? "" : "_ID");
        }
        return suffix;
    }
}
