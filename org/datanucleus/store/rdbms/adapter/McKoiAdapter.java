// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.adapter;

import org.datanucleus.exceptions.NucleusUserException;
import org.datanucleus.store.rdbms.schema.McKoiTypeInfo;
import org.datanucleus.store.rdbms.schema.SQLTypeInfo;
import java.sql.ResultSet;
import org.datanucleus.store.rdbms.table.Column;
import org.datanucleus.store.rdbms.table.Table;
import org.datanucleus.store.rdbms.identifier.IdentifierType;
import java.util.Collection;
import java.sql.DatabaseMetaData;

public class McKoiAdapter extends BaseDatastoreAdapter
{
    private static final String MCKOI_RESERVED_WORDS = "ACCOUNT,ACTION,ADD,AFTER,ALL,ALTER,AND,ANY,AS,ASC,AUTO,BEFORE,BETWEEN,BIGINT,BINARY,BIT,BLOB,BOOLEAN,BOTH,BY,CACHE,CALL,CALLBACK,CANONICAL_DECOMPOSITION,CASCADE,CAST,CHAR,CHARACTER,CHECK,CLOB,COLLATE,COLUMN,COMMIT,COMMITTED,COMPACT,CONSTRAINT,COUNT,CREATE,CROSS,CURRENT_DATE,CURRENT_TIME,CURRENT_TIMESTAMP,CYCLE,DATE,DECIMAL,DEFAULT,DEFERRABLE,DEFERRED,DELETE,DESC,DESCRIBE,DISTINCT,DOUBLE,DROP,EACH,EXCEPT,EXECUTE,EXISTS,EXPLAIN,FLOAT,FOR,FOREIGN,FROM,FULL_DECOMPOSITION,FUNCTION,GRANT,GROUP,GROUPS,HAVING,IDENTICAL_STRENGTH,IF,IGNORE,IMMEDIATE,IN,INCREMENT,INDEX,INDEX_BLIST,INDEX_NONE,INITIALLY,INNER,INSERT,INT,INTEGER,INTERSECT,INTO,IS,ISOLATION,JAVA,JAVA_OBJECT,JOIN,KEY,LANGUAGE,LEADING,LEFT,LEVEL,LIKE,LIMIT,LOCK,LONG,LONGVARBINARY,LONGVARCHAR,MAX,MAXVALUE,MINVALUE,NAME,NATURAL,NEW,NO,NO_DECOMPOSITION,NOT,NUMERIC,OLD,ON,OPTIMIZE,OPTION,OR,ORDER,OUTER,PASSWORD,PRIMARY,PRIMARY_STRENGTH,PRIVILEGES,PROCEDURE,PUBLIC,READ,REAL,REFERENCES,REGEX,REPEATABLE,RESTRICT,RETURN,RETURNS,REVOKE,RIGHT,ROLLBACK,ROW,SCHEMA,SECONDARY_STRENGTH,SELECT,SEQUENCE,SERIALIZABLE,SET,SHOW,SHUTDOWN,SMALLINT,SOME,START,STRING,TABLE,TEMPORARY,TERTIARY_STRENGTH,TEXT,TIME,TIMESTAMP,TINYINT,TO,TRAILING,TRANSACTION,TRIGGER,TRIM,UNCOMMITTED,UNION,UNIQUE,UNLOCK,UPDATE,USAGE,USE,USER,USING,VALUES,VARBINARY,VARCHAR,VARYING,VIEW,WHERE,WITH";
    
    public McKoiAdapter(final DatabaseMetaData metadata) {
        super(metadata);
        this.reservedKeywords.addAll((Collection<?>)this.parseKeywordList("ACCOUNT,ACTION,ADD,AFTER,ALL,ALTER,AND,ANY,AS,ASC,AUTO,BEFORE,BETWEEN,BIGINT,BINARY,BIT,BLOB,BOOLEAN,BOTH,BY,CACHE,CALL,CALLBACK,CANONICAL_DECOMPOSITION,CASCADE,CAST,CHAR,CHARACTER,CHECK,CLOB,COLLATE,COLUMN,COMMIT,COMMITTED,COMPACT,CONSTRAINT,COUNT,CREATE,CROSS,CURRENT_DATE,CURRENT_TIME,CURRENT_TIMESTAMP,CYCLE,DATE,DECIMAL,DEFAULT,DEFERRABLE,DEFERRED,DELETE,DESC,DESCRIBE,DISTINCT,DOUBLE,DROP,EACH,EXCEPT,EXECUTE,EXISTS,EXPLAIN,FLOAT,FOR,FOREIGN,FROM,FULL_DECOMPOSITION,FUNCTION,GRANT,GROUP,GROUPS,HAVING,IDENTICAL_STRENGTH,IF,IGNORE,IMMEDIATE,IN,INCREMENT,INDEX,INDEX_BLIST,INDEX_NONE,INITIALLY,INNER,INSERT,INT,INTEGER,INTERSECT,INTO,IS,ISOLATION,JAVA,JAVA_OBJECT,JOIN,KEY,LANGUAGE,LEADING,LEFT,LEVEL,LIKE,LIMIT,LOCK,LONG,LONGVARBINARY,LONGVARCHAR,MAX,MAXVALUE,MINVALUE,NAME,NATURAL,NEW,NO,NO_DECOMPOSITION,NOT,NUMERIC,OLD,ON,OPTIMIZE,OPTION,OR,ORDER,OUTER,PASSWORD,PRIMARY,PRIMARY_STRENGTH,PRIVILEGES,PROCEDURE,PUBLIC,READ,REAL,REFERENCES,REGEX,REPEATABLE,RESTRICT,RETURN,RETURNS,REVOKE,RIGHT,ROLLBACK,ROW,SCHEMA,SECONDARY_STRENGTH,SELECT,SEQUENCE,SERIALIZABLE,SET,SHOW,SHUTDOWN,SMALLINT,SOME,START,STRING,TABLE,TEMPORARY,TERTIARY_STRENGTH,TEXT,TIME,TIMESTAMP,TINYINT,TO,TRAILING,TRANSACTION,TRIGGER,TRIM,UNCOMMITTED,UNION,UNIQUE,UNLOCK,UPDATE,USAGE,USE,USER,USING,VALUES,VARBINARY,VARCHAR,VARYING,VIEW,WHERE,WITH"));
        this.supportedOptions.add("Sequences");
        this.supportedOptions.add("UseUnionAll");
        this.supportedOptions.remove("EscapeExpressionInLikePredicate");
        this.supportedOptions.remove("TxIsolationReadCommitted");
        this.supportedOptions.remove("TxIsolationReadUncommitted");
        this.supportedOptions.remove("TxIsolationReadRepeatableRead");
        this.supportedOptions.remove("TxIsolationNone");
    }
    
    @Override
    public String getVendorID() {
        return "mckoi";
    }
    
    @Override
    public boolean isKeyword(final String word) {
        return true;
    }
    
    @Override
    public int getDatastoreIdentifierMaxLength(final IdentifierType identifierType) {
        if (identifierType == IdentifierType.TABLE) {
            return 128;
        }
        if (identifierType == IdentifierType.COLUMN) {
            return 128;
        }
        if (identifierType == IdentifierType.CANDIDATE_KEY) {
            return 128;
        }
        if (identifierType == IdentifierType.FOREIGN_KEY) {
            return 128;
        }
        if (identifierType == IdentifierType.INDEX) {
            return 128;
        }
        if (identifierType == IdentifierType.PRIMARY_KEY) {
            return 128;
        }
        if (identifierType == IdentifierType.SEQUENCE) {
            return 128;
        }
        return super.getDatastoreIdentifierMaxLength(identifierType);
    }
    
    @Override
    public String getAddColumnStatement(final Table table, final Column col) {
        return "ALTER TABLE " + table.toString() + " ADD COLUMN " + col.getSQLDefinition();
    }
    
    @Override
    public SQLTypeInfo newSQLTypeInfo(final ResultSet rs) {
        return new McKoiTypeInfo(rs);
    }
    
    @Override
    public int getRequiredTransactionIsolationLevel() {
        return 8;
    }
    
    @Override
    public String getDropTableStatement(final Table table) {
        return "DROP TABLE " + table.toString();
    }
    
    @Override
    public String getSequenceCreateStmt(final String sequence_name, final Integer min, final Integer max, final Integer start, final Integer increment, final Integer cache_size) {
        if (sequence_name == null) {
            throw new NucleusUserException(McKoiAdapter.LOCALISER.msg("051028"));
        }
        final StringBuffer stmt = new StringBuffer("CREATE SEQUENCE ");
        stmt.append(sequence_name);
        if (increment != null) {
            stmt.append(" INCREMENT " + increment);
        }
        if (min != null) {
            stmt.append(" MINVALUE " + min);
        }
        if (max != null) {
            stmt.append(" MAXVALUE " + max);
        }
        if (start != null) {
            stmt.append(" START " + start);
        }
        if (cache_size != null) {
            stmt.append(" CACHE " + cache_size);
        }
        return stmt.toString();
    }
    
    @Override
    public String getSequenceNextStmt(final String sequence_name) {
        if (sequence_name == null) {
            throw new NucleusUserException(McKoiAdapter.LOCALISER.msg("051028"));
        }
        final StringBuffer stmt = new StringBuffer("SELECT ");
        stmt.append(" NEXTVAL('" + sequence_name + "') ");
        return stmt.toString();
    }
}
