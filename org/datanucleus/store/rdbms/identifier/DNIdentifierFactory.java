// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.identifier;

import org.datanucleus.exceptions.NucleusException;
import java.util.ListIterator;
import java.util.StringTokenizer;
import java.util.ArrayList;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.metadata.AbstractMemberMetaData;
import java.util.Map;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.store.rdbms.adapter.DatastoreAdapter;

public class DNIdentifierFactory extends AbstractIdentifierFactory
{
    protected String tablePrefix;
    protected String tableSuffix;
    
    public DNIdentifierFactory(final DatastoreAdapter dba, final ClassLoaderResolver clr, final Map props) {
        super(dba, clr, props);
        this.tablePrefix = null;
        this.tableSuffix = null;
        if (props.containsKey("WordSeparator")) {
            this.wordSeparator = props.get("WordSeparator");
        }
        this.tablePrefix = props.get("TablePrefix");
        this.tableSuffix = props.get("TableSuffix");
    }
    
    @Override
    public DatastoreIdentifier newTableIdentifier(final AbstractMemberMetaData fmd) {
        String identifierName = null;
        String schemaName = null;
        String catalogName = null;
        AbstractMemberMetaData[] relatedMmds = null;
        if (fmd.getColumnMetaData().length > 0 && fmd.getColumnMetaData()[0].getName() != null) {
            identifierName = fmd.getColumnMetaData()[0].getName();
        }
        else if (fmd.hasContainer()) {
            if (fmd.getTable() != null) {
                final String specifiedName = fmd.getTable();
                final String[] parts = this.getIdentifierNamePartsFromName(specifiedName);
                if (parts != null) {
                    catalogName = parts[0];
                    schemaName = parts[1];
                    identifierName = parts[2];
                }
                if (catalogName == null) {
                    catalogName = fmd.getCatalog();
                }
                if (schemaName == null) {
                    schemaName = fmd.getSchema();
                }
            }
            else {
                relatedMmds = fmd.getRelatedMemberMetaData(this.clr);
                if (relatedMmds != null && relatedMmds[0].getTable() != null) {
                    final String specifiedName = relatedMmds[0].getTable();
                    final String[] parts = this.getIdentifierNamePartsFromName(specifiedName);
                    if (parts != null) {
                        catalogName = parts[0];
                        schemaName = parts[1];
                        identifierName = parts[2];
                    }
                    if (catalogName == null) {
                        catalogName = fmd.getCatalog();
                    }
                    if (schemaName == null) {
                        schemaName = fmd.getSchema();
                    }
                }
            }
        }
        if (schemaName == null && catalogName == null) {
            if (fmd.getParent() instanceof AbstractClassMetaData) {
                final AbstractClassMetaData ownerCmd = (AbstractClassMetaData)fmd.getParent();
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
            String fieldNameBasis = fmd.getFullFieldName();
            if (relatedMmds != null && relatedMmds[0].getMappedBy() != null) {
                fieldNameBasis = relatedMmds[0].getFullFieldName();
            }
            final ArrayList name_parts = new ArrayList();
            final StringTokenizer tokens = new StringTokenizer(fieldNameBasis, ".");
            while (tokens.hasMoreTokens()) {
                name_parts.add(tokens.nextToken());
            }
            final ListIterator li = name_parts.listIterator(name_parts.size());
            final String unique_name = li.previous();
            final String full_name = (li.hasPrevious() ? (li.previous() + this.getWordSeparator()) : "") + unique_name;
            identifierName = "";
            if (this.tablePrefix != null && this.tablePrefix.length() > 0) {
                identifierName = this.tablePrefix;
            }
            identifierName += full_name;
            if (this.tableSuffix != null && this.tableSuffix.length() > 0) {
                identifierName += this.tableSuffix;
            }
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
            final String unique_name = cmd.getFullClassName().substring(cmd.getFullClassName().lastIndexOf(46) + 1);
            identifierName = "";
            if (this.tablePrefix != null && this.tablePrefix.length() > 0) {
                identifierName = this.tablePrefix;
            }
            identifierName += unique_name;
            if (this.tableSuffix != null && this.tableSuffix.length() > 0) {
                identifierName += this.tableSuffix;
            }
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
        DatastoreIdentifier identifier = null;
        final String key = "[" + refMetaData.getFullFieldName() + "][" + implMetaData.getFullClassName() + "][" + implIdentifier.getIdentifierName() + "]";
        identifier = this.references.get(key);
        if (identifier == null) {
            final String referenceName = refMetaData.getName();
            String implementationName = implMetaData.getFullClassName();
            final int dot = implementationName.lastIndexOf(46);
            if (dot > -1) {
                implementationName = implementationName.substring(dot + 1);
            }
            final String name = referenceName + "." + implementationName + "." + implIdentifier.getIdentifierName();
            final String suffix = this.getColumnIdentifierSuffix(fieldRole, embedded);
            final String datastoreID = this.generateIdentifierNameForJavaName(name);
            final String baseID = this.truncate(datastoreID, this.dba.getDatastoreIdentifierMaxLength(IdentifierType.COLUMN) - suffix.length());
            identifier = new ColumnIdentifier(this, baseID + suffix);
            this.references.put(key, identifier);
        }
        return identifier;
    }
    
    @Override
    public DatastoreIdentifier newJoinTableFieldIdentifier(final AbstractMemberMetaData ownerFmd, final AbstractMemberMetaData relatedFmd, final DatastoreIdentifier destinationId, final boolean embedded, final int fieldRole) {
        if (destinationId != null) {
            return this.newColumnIdentifier(destinationId.getIdentifierName(), embedded, fieldRole);
        }
        String baseName = null;
        if (fieldRole == 3) {
            final String elementType = ownerFmd.getCollection().getElementType();
            baseName = elementType.substring(elementType.lastIndexOf(46) + 1);
        }
        else if (fieldRole == 4) {
            final String elementType = ownerFmd.getArray().getElementType();
            baseName = elementType.substring(elementType.lastIndexOf(46) + 1);
        }
        else if (fieldRole == 5) {
            final String keyType = ownerFmd.getMap().getKeyType();
            baseName = keyType.substring(keyType.lastIndexOf(46) + 1);
        }
        else if (fieldRole == 6) {
            final String valueType = ownerFmd.getMap().getValueType();
            baseName = valueType.substring(valueType.lastIndexOf(46) + 1);
        }
        else {
            baseName = "UNKNOWN";
        }
        return this.newColumnIdentifier(baseName, embedded, fieldRole);
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
                return this.newColumnIdentifier(ownerFmd.getName() + "." + "INTEGER", embedded, fieldRole);
            }
            throw new NucleusException("Column role " + fieldRole + " not supported by this method").setFatal();
        }
    }
    
    @Override
    public DatastoreIdentifier newDiscriminatorFieldIdentifier() {
        final String name = "DISCRIMINATOR";
        DatastoreIdentifier identifier = this.columns.get(name);
        if (identifier == null) {
            identifier = new ColumnIdentifier(this, name);
            this.columns.put(name, identifier);
        }
        return identifier;
    }
    
    @Override
    public DatastoreIdentifier newVersionFieldIdentifier() {
        final String name = "OPT_VERSION";
        DatastoreIdentifier identifier = this.columns.get(name);
        if (identifier == null) {
            identifier = new ColumnIdentifier(this, name);
            this.columns.put(name, identifier);
        }
        return identifier;
    }
    
    @Override
    public DatastoreIdentifier newAdapterIndexFieldIdentifier() {
        final String name = "ADPT_PK_IDX";
        DatastoreIdentifier identifier = this.columns.get(name);
        if (identifier == null) {
            identifier = new ColumnIdentifier(this, name);
            this.columns.put(name, identifier);
        }
        return identifier;
    }
    
    @Override
    public DatastoreIdentifier newIndexFieldIdentifier(final AbstractMemberMetaData mmd) {
        final String name = "INTEGER_IDX";
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
        char prev = '\0';
        for (int i = 0; i < javaName.length(); ++i) {
            final char c = javaName.charAt(i);
            if (c >= 'A' && c <= 'Z' && this.identifierCase != IdentifierCase.MIXED_CASE && this.identifierCase != IdentifierCase.MIXED_CASE_QUOTED) {
                if (prev >= 'a' && prev <= 'z') {
                    s.append(this.wordSeparator);
                }
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
                s.append(this.wordSeparator);
            }
            else {
                final String cval = "000" + Integer.toHexString(c);
                s.append(cval.substring(cval.length() - ((c > '\u00ff') ? 4 : 2)));
            }
            prev = c;
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
        String suffix = null;
        switch (role) {
            default: {
                suffix = (embedded ? "" : "_ID");
                break;
            }
            case -1: {
                suffix = "";
                break;
            }
            case 1: {
                suffix = (embedded ? "_OWN" : "_OID");
                break;
            }
            case 2:
            case 3:
            case 4: {
                suffix = (embedded ? "_ELE" : "_EID");
                break;
            }
            case 5: {
                suffix = (embedded ? "_KEY" : "_KID");
                break;
            }
            case 6: {
                suffix = (embedded ? "_VAL" : "_VID");
                break;
            }
            case 7: {
                suffix = (embedded ? "_IDX" : "_XID");
                break;
            }
        }
        return suffix;
    }
}
