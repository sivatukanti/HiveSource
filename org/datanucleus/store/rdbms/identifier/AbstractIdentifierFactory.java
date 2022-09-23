// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.identifier;

import org.datanucleus.ClassConstants;
import org.datanucleus.util.StringUtils;
import org.datanucleus.store.rdbms.exceptions.TooManyIndicesException;
import org.datanucleus.store.rdbms.exceptions.TooManyForeignKeysException;
import org.datanucleus.store.rdbms.table.Table;
import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.util.NucleusLogger;
import java.util.WeakHashMap;
import java.util.Map;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.store.rdbms.adapter.DatastoreAdapter;
import org.datanucleus.store.schema.naming.NamingFactory;
import org.datanucleus.util.Localiser;

public abstract class AbstractIdentifierFactory implements IdentifierFactory
{
    protected static final Localiser LOCALISER;
    public static final int CASE_PRESERVE = 1;
    public static final int CASE_UPPER = 2;
    public static final int CASE_LOWER = 3;
    protected NamingFactory namingFactory;
    protected DatastoreAdapter dba;
    protected ClassLoaderResolver clr;
    protected IdentifierCase identifierCase;
    protected String quoteString;
    private static final int HASH_LENGTH = 4;
    private static final int HASH_RANGE;
    protected Map<String, DatastoreIdentifier> tables;
    protected Map<String, DatastoreIdentifier> columns;
    protected Map<String, DatastoreIdentifier> foreignkeys;
    protected Map<String, DatastoreIdentifier> indexes;
    protected Map<String, DatastoreIdentifier> candidates;
    protected Map<String, DatastoreIdentifier> primarykeys;
    protected Map<String, DatastoreIdentifier> sequences;
    protected Map<String, DatastoreIdentifier> references;
    protected String wordSeparator;
    protected String defaultCatalogName;
    protected String defaultSchemaName;
    
    private static final int calculateHashMax() {
        int hm = 1;
        for (int i = 0; i < 4; ++i) {
            hm *= 36;
        }
        return hm;
    }
    
    public AbstractIdentifierFactory(final DatastoreAdapter dba, final ClassLoaderResolver clr, final Map props) {
        this.tables = new WeakHashMap<String, DatastoreIdentifier>();
        this.columns = new WeakHashMap<String, DatastoreIdentifier>();
        this.foreignkeys = new WeakHashMap<String, DatastoreIdentifier>();
        this.indexes = new WeakHashMap<String, DatastoreIdentifier>();
        this.candidates = new WeakHashMap<String, DatastoreIdentifier>();
        this.primarykeys = new WeakHashMap<String, DatastoreIdentifier>();
        this.sequences = new WeakHashMap<String, DatastoreIdentifier>();
        this.references = new WeakHashMap<String, DatastoreIdentifier>();
        this.wordSeparator = "_";
        this.defaultCatalogName = null;
        this.defaultSchemaName = null;
        this.dba = dba;
        this.clr = clr;
        this.quoteString = dba.getIdentifierQuoteString();
        int userIdentifierCase = 2;
        if (props.containsKey("RequiredCase")) {
            final String requiredCase = props.get("RequiredCase");
            if (requiredCase.equalsIgnoreCase("UpperCase")) {
                userIdentifierCase = 2;
            }
            else if (requiredCase.equalsIgnoreCase("LowerCase")) {
                userIdentifierCase = 3;
            }
            else if (requiredCase.equalsIgnoreCase("PreserveCase")) {
                userIdentifierCase = 1;
            }
        }
        if (userIdentifierCase == 2) {
            if (dba.supportsOption("UpperCaseIdentifiers")) {
                this.identifierCase = IdentifierCase.UPPER_CASE;
            }
            else if (dba.supportsOption("UpperCaseQuotedIdentifiers")) {
                this.identifierCase = IdentifierCase.UPPER_CASE_QUOTED;
            }
            else if (dba.supportsOption("MixedCaseIdentifiers") || dba.supportsOption("MixedCaseSensitiveIdentifiers")) {
                this.identifierCase = IdentifierCase.UPPER_CASE;
            }
            else if (dba.supportsOption("MixedCaseQuotedIdentifiers") || dba.supportsOption("MixedCaseQuotedSensitiveIdentifiers")) {
                this.identifierCase = IdentifierCase.UPPER_CASE_QUOTED;
            }
            else if (dba.supportsOption("LowerCaseIdentifiers")) {
                this.identifierCase = IdentifierCase.LOWER_CASE;
                NucleusLogger.PERSISTENCE.warn(AbstractIdentifierFactory.LOCALISER.msg("039001", "UPPERCASE", "LOWERCASE"));
            }
            else {
                if (!dba.supportsOption("LowerCaseQuotedIdentifiers")) {
                    throw new NucleusUserException(AbstractIdentifierFactory.LOCALISER.msg("039002", "UPPERCASE")).setFatal();
                }
                this.identifierCase = IdentifierCase.LOWER_CASE_QUOTED;
                NucleusLogger.PERSISTENCE.warn(AbstractIdentifierFactory.LOCALISER.msg("039001", "UPPERCASE", "LOWERCASEQUOTED"));
            }
        }
        else if (userIdentifierCase == 3) {
            if (dba.supportsOption("LowerCaseIdentifiers")) {
                this.identifierCase = IdentifierCase.LOWER_CASE;
            }
            else if (dba.supportsOption("LowerCaseQuotedIdentifiers")) {
                this.identifierCase = IdentifierCase.LOWER_CASE_QUOTED;
            }
            else if (dba.supportsOption("MixedCaseIdentifiers") || dba.supportsOption("MixedCaseSensitiveIdentifiers")) {
                this.identifierCase = IdentifierCase.LOWER_CASE;
            }
            else if (dba.supportsOption("MixedCaseQuotedIdentifiers") || dba.supportsOption("MixedCaseQuotedSensitiveIdentifiers")) {
                this.identifierCase = IdentifierCase.LOWER_CASE_QUOTED;
            }
            else if (dba.supportsOption("UpperCaseIdentifiers")) {
                this.identifierCase = IdentifierCase.UPPER_CASE;
                NucleusLogger.PERSISTENCE.warn(AbstractIdentifierFactory.LOCALISER.msg("039001", "LOWERCASE", "UPPERCASE"));
            }
            else {
                if (!dba.supportsOption("UpperCaseQuotedIdentifiers")) {
                    throw new NucleusUserException(AbstractIdentifierFactory.LOCALISER.msg("039002", "LOWERCASE")).setFatal();
                }
                this.identifierCase = IdentifierCase.UPPER_CASE_QUOTED;
                NucleusLogger.PERSISTENCE.warn(AbstractIdentifierFactory.LOCALISER.msg("039001", "LOWERCASE", "UPPERCASEQUOTED"));
            }
        }
        else {
            if (userIdentifierCase != 1) {
                throw new NucleusUserException(AbstractIdentifierFactory.LOCALISER.msg("039000", userIdentifierCase)).setFatal();
            }
            if (dba.supportsOption("MixedCaseIdentifiers") || dba.supportsOption("MixedCaseSensitiveIdentifiers")) {
                this.identifierCase = IdentifierCase.MIXED_CASE;
            }
            else if (dba.supportsOption("MixedCaseQuotedIdentifiers") || dba.supportsOption("MixedCaseQuotedSensitiveIdentifiers")) {
                this.identifierCase = IdentifierCase.MIXED_CASE_QUOTED;
            }
            else if (dba.supportsOption("LowerCaseIdentifiers")) {
                this.identifierCase = IdentifierCase.LOWER_CASE;
                NucleusLogger.PERSISTENCE.warn(AbstractIdentifierFactory.LOCALISER.msg("039001", "MIXEDCASE", "LOWERCASE"));
            }
            else if (dba.supportsOption("LowerCaseQuotedIdentifiers")) {
                this.identifierCase = IdentifierCase.LOWER_CASE_QUOTED;
                NucleusLogger.PERSISTENCE.warn(AbstractIdentifierFactory.LOCALISER.msg("039001", "MIXEDCASE", "LOWERCASEQUOTED"));
            }
            else if (dba.supportsOption("UpperCaseIdentifiers")) {
                this.identifierCase = IdentifierCase.UPPER_CASE;
                NucleusLogger.PERSISTENCE.warn(AbstractIdentifierFactory.LOCALISER.msg("039001", "MIXEDCASE", "UPPERCASE"));
            }
            else {
                if (!dba.supportsOption("UpperCaseQuotedIdentifiers")) {
                    throw new NucleusUserException(AbstractIdentifierFactory.LOCALISER.msg("039002", "MIXEDCASE")).setFatal();
                }
                this.identifierCase = IdentifierCase.UPPER_CASE_QUOTED;
                NucleusLogger.PERSISTENCE.warn(AbstractIdentifierFactory.LOCALISER.msg("039001", "MIXEDCASE", "UPPERCASEQUOTED"));
            }
        }
        if (props.containsKey("DefaultCatalog")) {
            this.defaultCatalogName = this.getIdentifierInAdapterCase(props.get("DefaultCatalog"));
        }
        if (props.containsKey("DefaultSchema")) {
            this.defaultSchemaName = this.getIdentifierInAdapterCase(props.get("DefaultSchema"));
        }
        if (props.containsKey("NamingFactory")) {
            this.namingFactory = (NamingFactory)props.get("NamingFactory");
        }
    }
    
    @Override
    public DatastoreAdapter getDatastoreAdapter() {
        return this.dba;
    }
    
    @Override
    public IdentifierCase getIdentifierCase() {
        return this.identifierCase;
    }
    
    public String getWordSeparator() {
        return this.wordSeparator;
    }
    
    protected String truncate(final String identifier, final int length) {
        if (length == 0) {
            return identifier;
        }
        if (identifier.length() <= length) {
            return identifier;
        }
        if (length < 4) {
            throw new IllegalArgumentException("The length argument (=" + length + ") is less than HASH_LENGTH(=" + 4 + ")!");
        }
        final int tailIndex = length - 4;
        int tailHash = identifier.hashCode();
        if (tailHash < 0) {
            tailHash *= -1;
        }
        tailHash %= AbstractIdentifierFactory.HASH_RANGE;
        String suffix = Integer.toString(tailHash, 36);
        if (suffix.length() > 4) {
            throw new IllegalStateException("Calculated hash \"" + suffix + "\" has more characters than defined by HASH_LENGTH (=" + 4 + ")! This should never happen!");
        }
        if (suffix.length() < 4) {
            final StringBuilder sb = new StringBuilder(4);
            sb.append(suffix);
            while (sb.length() < 4) {
                sb.insert(0, '0');
            }
            suffix = sb.toString();
        }
        return identifier.substring(0, tailIndex) + suffix;
    }
    
    @Override
    public String getIdentifierInAdapterCase(final String identifier) {
        if (identifier == null) {
            return null;
        }
        final StringBuilder id = new StringBuilder();
        if ((this.identifierCase == IdentifierCase.LOWER_CASE_QUOTED || this.identifierCase == IdentifierCase.MIXED_CASE_QUOTED || this.identifierCase == IdentifierCase.UPPER_CASE_QUOTED) && !identifier.startsWith(this.quoteString)) {
            id.append(this.quoteString);
        }
        if (this.identifierCase == IdentifierCase.LOWER_CASE || this.identifierCase == IdentifierCase.LOWER_CASE_QUOTED) {
            id.append(identifier.toLowerCase());
        }
        else if (this.identifierCase == IdentifierCase.UPPER_CASE || this.identifierCase == IdentifierCase.UPPER_CASE_QUOTED) {
            id.append(identifier.toUpperCase());
        }
        else {
            id.append(identifier);
        }
        if ((this.identifierCase == IdentifierCase.LOWER_CASE_QUOTED || this.identifierCase == IdentifierCase.MIXED_CASE_QUOTED || this.identifierCase == IdentifierCase.UPPER_CASE_QUOTED) && !identifier.endsWith(this.quoteString)) {
            id.append(this.quoteString);
        }
        return id.toString();
    }
    
    @Override
    public DatastoreIdentifier newIdentifier(final IdentifierType identifierType, final String name) {
        DatastoreIdentifier identifier = null;
        final String key = name.replace(this.quoteString, "");
        if (identifierType == IdentifierType.TABLE) {
            identifier = this.tables.get(key);
            if (identifier == null) {
                String sqlIdentifier = this.generateIdentifierNameForJavaName(key);
                sqlIdentifier = this.truncate(sqlIdentifier, this.dba.getDatastoreIdentifierMaxLength(identifierType));
                identifier = new TableIdentifier(this, sqlIdentifier);
                this.setCatalogSchemaForTable((TableIdentifier)identifier);
                this.tables.put(key, identifier);
            }
        }
        else if (identifierType == IdentifierType.COLUMN) {
            identifier = this.columns.get(key);
            if (identifier == null) {
                String sqlIdentifier = this.generateIdentifierNameForJavaName(key);
                sqlIdentifier = this.truncate(sqlIdentifier, this.dba.getDatastoreIdentifierMaxLength(identifierType));
                identifier = new ColumnIdentifier(this, sqlIdentifier);
                this.columns.put(key, identifier);
            }
        }
        else if (identifierType == IdentifierType.FOREIGN_KEY) {
            identifier = this.foreignkeys.get(key);
            if (identifier == null) {
                String sqlIdentifier = this.generateIdentifierNameForJavaName(key);
                sqlIdentifier = this.truncate(sqlIdentifier, this.dba.getDatastoreIdentifierMaxLength(identifierType));
                identifier = new ForeignKeyIdentifier(this, sqlIdentifier);
                this.foreignkeys.put(key, identifier);
            }
        }
        else if (identifierType == IdentifierType.INDEX) {
            identifier = this.indexes.get(key);
            if (identifier == null) {
                String sqlIdentifier = this.generateIdentifierNameForJavaName(key);
                sqlIdentifier = this.truncate(sqlIdentifier, this.dba.getDatastoreIdentifierMaxLength(identifierType));
                identifier = new IndexIdentifier(this, sqlIdentifier);
                this.indexes.put(key, identifier);
            }
        }
        else if (identifierType == IdentifierType.CANDIDATE_KEY) {
            identifier = this.candidates.get(key);
            if (identifier == null) {
                String sqlIdentifier = this.generateIdentifierNameForJavaName(key);
                sqlIdentifier = this.truncate(sqlIdentifier, this.dba.getDatastoreIdentifierMaxLength(identifierType));
                identifier = new CandidateKeyIdentifier(this, sqlIdentifier);
                this.candidates.put(key, identifier);
            }
        }
        else if (identifierType == IdentifierType.PRIMARY_KEY) {
            identifier = this.primarykeys.get(key);
            if (identifier == null) {
                String sqlIdentifier = this.generateIdentifierNameForJavaName(key);
                sqlIdentifier = this.truncate(sqlIdentifier, this.dba.getDatastoreIdentifierMaxLength(identifierType));
                identifier = new PrimaryKeyIdentifier(this, sqlIdentifier);
                this.primarykeys.put(key, identifier);
            }
        }
        else {
            if (identifierType != IdentifierType.SEQUENCE) {
                throw new NucleusException("identifier type " + identifierType + " not supported by this factory method").setFatal();
            }
            identifier = this.sequences.get(key);
            if (identifier == null) {
                String sqlIdentifier = this.generateIdentifierNameForJavaName(key);
                sqlIdentifier = this.truncate(sqlIdentifier, this.dba.getDatastoreIdentifierMaxLength(identifierType));
                identifier = new SequenceIdentifier(this, sqlIdentifier);
                this.sequences.put(key, identifier);
            }
        }
        return identifier;
    }
    
    @Override
    public DatastoreIdentifier newIdentifier(final DatastoreIdentifier identifier, final String suffix) {
        String newId = identifier.getIdentifierName() + this.getWordSeparator() + suffix;
        if (identifier instanceof TableIdentifier) {
            newId = this.truncate(newId, this.dba.getDatastoreIdentifierMaxLength(IdentifierType.TABLE));
            final TableIdentifier tableIdentifier = new TableIdentifier(this, newId);
            this.setCatalogSchemaForTable(tableIdentifier);
            return tableIdentifier;
        }
        if (identifier instanceof ColumnIdentifier) {
            newId = this.truncate(newId, this.dba.getDatastoreIdentifierMaxLength(IdentifierType.COLUMN));
            return new ColumnIdentifier(this, newId);
        }
        if (identifier instanceof ForeignKeyIdentifier) {
            newId = this.truncate(newId, this.dba.getDatastoreIdentifierMaxLength(IdentifierType.FOREIGN_KEY));
            return new ForeignKeyIdentifier(this, newId);
        }
        if (identifier instanceof IndexIdentifier) {
            newId = this.truncate(newId, this.dba.getDatastoreIdentifierMaxLength(IdentifierType.INDEX));
            return new IndexIdentifier(this, newId);
        }
        if (identifier instanceof CandidateKeyIdentifier) {
            newId = this.truncate(newId, this.dba.getDatastoreIdentifierMaxLength(IdentifierType.CANDIDATE_KEY));
            return new CandidateKeyIdentifier(this, newId);
        }
        if (identifier instanceof PrimaryKeyIdentifier) {
            newId = this.truncate(newId, this.dba.getDatastoreIdentifierMaxLength(IdentifierType.PRIMARY_KEY));
            return new PrimaryKeyIdentifier(this, newId);
        }
        if (identifier instanceof SequenceIdentifier) {
            newId = this.truncate(newId, this.dba.getDatastoreIdentifierMaxLength(IdentifierType.SEQUENCE));
            return new SequenceIdentifier(this, newId);
        }
        return null;
    }
    
    @Override
    public DatastoreIdentifier newTableIdentifier(final String identifierName) {
        final String key = identifierName.replace(this.quoteString, "");
        DatastoreIdentifier identifier = this.tables.get(key);
        if (identifier == null) {
            final String baseID = this.truncate(key, this.dba.getDatastoreIdentifierMaxLength(IdentifierType.TABLE));
            identifier = new TableIdentifier(this, baseID);
            this.setCatalogSchemaForTable((TableIdentifier)identifier);
            this.tables.put(key, identifier);
        }
        return identifier;
    }
    
    @Override
    public DatastoreIdentifier newColumnIdentifier(final String identifierName) {
        final String key = identifierName.replace(this.quoteString, "");
        DatastoreIdentifier identifier = this.columns.get(key);
        if (identifier == null) {
            final String baseID = this.truncate(key, this.dba.getDatastoreIdentifierMaxLength(IdentifierType.COLUMN));
            identifier = new ColumnIdentifier(this, baseID);
            this.columns.put(key, identifier);
        }
        return identifier;
    }
    
    @Override
    public DatastoreIdentifier newColumnIdentifier(final String javaName, final boolean embedded, final int fieldRole) {
        DatastoreIdentifier identifier = null;
        final String key = "[" + ((javaName == null) ? "" : javaName) + "][" + embedded + "][" + fieldRole;
        identifier = this.columns.get(key);
        if (identifier == null) {
            if (fieldRole == -1) {
                final String baseID = this.truncate(javaName, this.dba.getDatastoreIdentifierMaxLength(IdentifierType.COLUMN));
                identifier = new ColumnIdentifier(this, baseID);
            }
            else {
                final String suffix = this.getColumnIdentifierSuffix(fieldRole, embedded);
                final String datastoreID = this.generateIdentifierNameForJavaName(javaName);
                final String baseID2 = this.truncate(datastoreID, this.dba.getDatastoreIdentifierMaxLength(IdentifierType.COLUMN) - suffix.length());
                identifier = new ColumnIdentifier(this, baseID2 + suffix);
            }
            this.columns.put(key, identifier);
        }
        return identifier;
    }
    
    @Override
    public DatastoreIdentifier newSequenceIdentifier(final String sequenceName) {
        final String key = sequenceName;
        DatastoreIdentifier identifier = this.sequences.get(key);
        if (identifier == null) {
            final String baseID = this.truncate(sequenceName, this.dba.getDatastoreIdentifierMaxLength(IdentifierType.SEQUENCE));
            identifier = new ColumnIdentifier(this, baseID);
            this.sequences.put(key, identifier);
        }
        return identifier;
    }
    
    @Override
    public DatastoreIdentifier newPrimaryKeyIdentifier(final Table table) {
        DatastoreIdentifier identifier = null;
        final String key = table.getIdentifier().toString();
        identifier = this.primarykeys.get(key);
        if (identifier == null) {
            final String suffix = this.getWordSeparator() + "PK";
            final int maxLength = this.dba.getDatastoreIdentifierMaxLength(IdentifierType.PRIMARY_KEY);
            final String baseID = this.truncate(table.getIdentifier().getIdentifierName(), maxLength - suffix.length());
            identifier = new PrimaryKeyIdentifier(this, baseID + suffix);
            this.primarykeys.put(key, identifier);
        }
        return identifier;
    }
    
    @Override
    public DatastoreIdentifier newCandidateKeyIdentifier(final Table table, final int seq) {
        DatastoreIdentifier identifier = null;
        final String key = "[" + table.getIdentifier().toString() + "][" + seq + "]";
        identifier = this.candidates.get(key);
        if (identifier == null) {
            final String suffix = this.getWordSeparator() + "U" + seq;
            final int maxLength = this.dba.getDatastoreIdentifierMaxLength(IdentifierType.CANDIDATE_KEY);
            final String baseID = this.truncate(table.getIdentifier().getIdentifierName(), maxLength - suffix.length());
            identifier = new CandidateKeyIdentifier(this, baseID + suffix);
            this.candidates.put(key, identifier);
        }
        return identifier;
    }
    
    @Override
    public DatastoreIdentifier newForeignKeyIdentifier(final Table table, final int seq) {
        DatastoreIdentifier identifier = null;
        final String key = "[" + table.getIdentifier().toString() + "][" + seq + "]";
        identifier = this.foreignkeys.get(key);
        if (identifier == null) {
            String suffix = this.getWordSeparator() + "FK";
            if (seq < 10) {
                suffix = suffix + "" + (char)(48 + seq);
            }
            else {
                if (seq >= this.dba.getMaxForeignKeys()) {
                    throw new TooManyForeignKeysException(this.dba, table.toString());
                }
                suffix += Integer.toHexString(65 + seq);
            }
            final int maxLength = this.dba.getDatastoreIdentifierMaxLength(IdentifierType.FOREIGN_KEY);
            final String baseID = this.truncate(table.getIdentifier().getIdentifierName(), maxLength - suffix.length());
            identifier = new ForeignKeyIdentifier(this, baseID + suffix);
            this.foreignkeys.put(key, identifier);
        }
        return identifier;
    }
    
    @Override
    public DatastoreIdentifier newIndexIdentifier(final Table table, final boolean isUnique, final int seq) {
        DatastoreIdentifier identifier = null;
        final String key = "[" + table.getIdentifier().toString() + "][" + isUnique + "][" + seq + "]";
        identifier = this.indexes.get(key);
        if (identifier == null) {
            String suffix = this.getWordSeparator() + (isUnique ? "U" : "N");
            if (seq >= this.dba.getMaxIndexes()) {
                throw new TooManyIndicesException(this.dba, table.toString());
            }
            suffix += String.valueOf(48 + seq);
            final int maxLength = this.dba.getDatastoreIdentifierMaxLength(IdentifierType.INDEX);
            final String baseID = this.truncate(table.getIdentifier().getIdentifierName(), maxLength - suffix.length());
            identifier = new IndexIdentifier(this, baseID + suffix);
            this.indexes.put(key, identifier);
        }
        return identifier;
    }
    
    protected abstract String getColumnIdentifierSuffix(final int p0, final boolean p1);
    
    protected abstract String generateIdentifierNameForJavaName(final String p0);
    
    protected void setCatalogSchemaForTable(final TableIdentifier identifier) {
        final String catalogName = identifier.getCatalogName();
        final String schemaName = identifier.getSchemaName();
        if (schemaName == null && catalogName == null) {
            if (this.dba.supportsOption("CatalogInTableDefinition")) {
                identifier.setCatalogName(this.defaultCatalogName);
            }
            if (this.dba.supportsOption("SchemaInTableDefinition")) {
                identifier.setSchemaName(this.defaultSchemaName);
            }
        }
    }
    
    protected String[] getIdentifierNamePartsFromName(final String name) {
        if (name != null) {
            final String[] names = new String[3];
            if (name.indexOf(46) < 0) {
                names[1] = (names[0] = null);
                names[2] = name;
            }
            else {
                final String[] specifiedNameParts = StringUtils.split(name, ".");
                int currentPartIndex = specifiedNameParts.length - 1;
                names[2] = specifiedNameParts[currentPartIndex--];
                if (this.dba.supportsOption("SchemaInTableDefinition") && currentPartIndex >= 0) {
                    names[1] = specifiedNameParts[currentPartIndex--];
                }
                if (this.dba.supportsOption("CatalogInTableDefinition") && currentPartIndex >= 0) {
                    names[0] = specifiedNameParts[currentPartIndex--];
                }
            }
            return names;
        }
        return null;
    }
    
    static {
        LOCALISER = Localiser.getInstance("org.datanucleus.Localisation", ClassConstants.NUCLEUS_CONTEXT_LOADER);
        HASH_RANGE = calculateHashMax();
    }
}
