// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.api.jdo.metadata;

import javax.jdo.annotations.Embedded;
import javax.jdo.annotations.Order;
import javax.jdo.annotations.Value;
import javax.jdo.annotations.Key;
import javax.jdo.annotations.Element;
import javax.jdo.annotations.Serialized;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.Transactional;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.Extensions;
import javax.jdo.annotations.Columns;
import javax.jdo.annotations.ForeignKey;
import javax.jdo.annotations.ForeignKeys;
import javax.jdo.annotations.Unique;
import javax.jdo.annotations.Uniques;
import javax.jdo.annotations.Index;
import javax.jdo.annotations.Indices;
import javax.jdo.annotations.Sequence;
import javax.jdo.annotations.FetchGroup;
import javax.jdo.annotations.FetchGroups;
import javax.jdo.annotations.FetchPlans;
import javax.jdo.annotations.FetchPlan;
import javax.jdo.annotations.Query;
import javax.jdo.annotations.Queries;
import javax.jdo.annotations.Discriminator;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.Join;
import javax.jdo.annotations.Joins;
import javax.jdo.annotations.PrimaryKey;
import javax.jdo.annotations.DatastoreIdentity;
import javax.jdo.annotations.Version;
import javax.jdo.annotations.EmbeddedOnly;
import javax.jdo.annotations.PersistenceAware;
import javax.jdo.annotations.PersistenceCapable;
import org.datanucleus.metadata.ForeignKeyMetaData;
import org.datanucleus.metadata.UniqueMetaData;
import org.datanucleus.util.StringUtils;
import org.datanucleus.metadata.IndexMetaData;
import java.lang.reflect.Method;
import javax.jdo.annotations.Column;
import org.datanucleus.metadata.MetaData;
import javax.jdo.annotations.Extension;
import org.datanucleus.metadata.ColumnMetaData;
import java.util.HashMap;
import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.VersionStrategy;
import org.datanucleus.metadata.IdentityStrategy;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.SequenceStrategy;
import javax.jdo.annotations.IdentityType;
import org.datanucleus.metadata.FieldPersistenceModifier;
import javax.jdo.annotations.PersistenceModifier;
import javax.jdo.annotations.ForeignKeyAction;
import javax.jdo.annotations.NullValue;
import org.datanucleus.metadata.QueryLanguage;

public class JDOAnnotationUtils
{
    public static final String PERSISTENCE_CAPABLE;
    public static final String PERSISTENCE_AWARE;
    public static final String EMBEDDED_ONLY;
    public static final String VERSION;
    public static final String DATASTORE_IDENTITY;
    public static final String PRIMARY_KEY;
    public static final String JOINS;
    public static final String JOIN;
    public static final String INHERITANCE;
    public static final String DISCRIMINATOR;
    public static final String QUERIES;
    public static final String QUERY;
    public static final String FETCHPLAN;
    public static final String FETCHPLANS;
    public static final String FETCHGROUPS;
    public static final String FETCHGROUP;
    public static final String SEQUENCE;
    public static final String INDICES;
    public static final String INDEX;
    public static final String UNIQUES;
    public static final String UNIQUE;
    public static final String FOREIGNKEYS;
    public static final String FOREIGNKEY;
    public static final String COLUMNS;
    public static final String COLUMN;
    public static final String EXTENSIONS;
    public static final String EXTENSION;
    public static final String PERSISTENT;
    public static final String TRANSACTIONAL;
    public static final String NOTPERSISTENT;
    public static final String SERIALIZED;
    public static final String ELEMENT;
    public static final String KEY;
    public static final String VALUE;
    public static final String ORDER;
    public static final String EMBEDDED;
    public static final String CACHEABLE = "javax.jdo.annotations.Cacheable";
    
    public static String getQueryLanguageName(final String value) {
        if (value == null) {
            return QueryLanguage.JDOQL.toString();
        }
        if (value.equalsIgnoreCase("javax.jdo.query.JDOQL")) {
            return QueryLanguage.JDOQL.toString();
        }
        if (value.equalsIgnoreCase("javax.jdo.query.SQL")) {
            return QueryLanguage.SQL.toString();
        }
        if (value.equalsIgnoreCase("javax.jdo.query.JPQL")) {
            return QueryLanguage.JPQL.toString();
        }
        return value;
    }
    
    public static String getNullValueString(final NullValue value) {
        if (value == NullValue.DEFAULT) {
            return org.datanucleus.metadata.NullValue.DEFAULT.toString();
        }
        if (value == NullValue.EXCEPTION) {
            return org.datanucleus.metadata.NullValue.EXCEPTION.toString();
        }
        if (value == NullValue.NONE) {
            return org.datanucleus.metadata.NullValue.NONE.toString();
        }
        return null;
    }
    
    public static String getForeignKeyActionString(final ForeignKeyAction action) {
        if (action == ForeignKeyAction.CASCADE) {
            return ForeignKeyAction.CASCADE.toString();
        }
        if (action == ForeignKeyAction.DEFAULT) {
            return ForeignKeyAction.DEFAULT.toString();
        }
        if (action == ForeignKeyAction.NONE) {
            return ForeignKeyAction.NONE.toString();
        }
        if (action == ForeignKeyAction.NULL) {
            return ForeignKeyAction.NULL.toString();
        }
        if (action == ForeignKeyAction.RESTRICT) {
            return ForeignKeyAction.RESTRICT.toString();
        }
        return null;
    }
    
    public static FieldPersistenceModifier getFieldPersistenceModifier(final PersistenceModifier modifier) {
        if (modifier == PersistenceModifier.PERSISTENT) {
            return FieldPersistenceModifier.PERSISTENT;
        }
        if (modifier == PersistenceModifier.TRANSACTIONAL) {
            return FieldPersistenceModifier.TRANSACTIONAL;
        }
        if (modifier == PersistenceModifier.NONE) {
            return FieldPersistenceModifier.NONE;
        }
        return null;
    }
    
    public static String getIdentityTypeString(final IdentityType idType) {
        if (idType == IdentityType.APPLICATION) {
            return org.datanucleus.metadata.IdentityType.APPLICATION.toString();
        }
        if (idType == IdentityType.DATASTORE) {
            return org.datanucleus.metadata.IdentityType.DATASTORE.toString();
        }
        if (idType == IdentityType.NONDURABLE) {
            return org.datanucleus.metadata.IdentityType.NONDURABLE.toString();
        }
        return null;
    }
    
    public static String getSequenceStrategyString(final SequenceStrategy strategy) {
        if (strategy == SequenceStrategy.NONTRANSACTIONAL) {
            return org.datanucleus.metadata.SequenceStrategy.NONTRANSACTIONAL.toString();
        }
        if (strategy == SequenceStrategy.CONTIGUOUS) {
            return org.datanucleus.metadata.SequenceStrategy.CONTIGUOUS.toString();
        }
        if (strategy == SequenceStrategy.NONCONTIGUOUS) {
            return org.datanucleus.metadata.SequenceStrategy.NONCONTIGUOUS.toString();
        }
        return null;
    }
    
    public static String getIdentityStrategyString(final IdGeneratorStrategy strategy) {
        if (strategy == IdGeneratorStrategy.NATIVE) {
            return IdentityStrategy.NATIVE.toString();
        }
        if (strategy == IdGeneratorStrategy.IDENTITY) {
            return IdentityStrategy.IDENTITY.toString();
        }
        if (strategy == IdGeneratorStrategy.SEQUENCE) {
            return IdentityStrategy.SEQUENCE.toString();
        }
        if (strategy == IdGeneratorStrategy.UUIDSTRING) {
            return IdentityStrategy.UUIDSTRING.toString();
        }
        if (strategy == IdGeneratorStrategy.UUIDHEX) {
            return IdentityStrategy.UUIDHEX.toString();
        }
        if (strategy == IdGeneratorStrategy.INCREMENT) {
            return IdentityStrategy.INCREMENT.toString();
        }
        return null;
    }
    
    public static String getVersionStrategyString(final VersionStrategy strategy) {
        if (strategy == VersionStrategy.NONE) {
            return org.datanucleus.metadata.VersionStrategy.NONE.toString();
        }
        if (strategy == VersionStrategy.DATE_TIME) {
            return org.datanucleus.metadata.VersionStrategy.DATE_TIME.toString();
        }
        if (strategy == VersionStrategy.VERSION_NUMBER) {
            return org.datanucleus.metadata.VersionStrategy.VERSION_NUMBER.toString();
        }
        if (strategy == VersionStrategy.STATE_IMAGE) {
            return org.datanucleus.metadata.VersionStrategy.STATE_IMAGE.toString();
        }
        return null;
    }
    
    public static String getInheritanceStrategyString(final InheritanceStrategy strategy) {
        if (strategy == InheritanceStrategy.NEW_TABLE) {
            return org.datanucleus.metadata.InheritanceStrategy.NEW_TABLE.toString();
        }
        if (strategy == InheritanceStrategy.SUBCLASS_TABLE) {
            return org.datanucleus.metadata.InheritanceStrategy.SUBCLASS_TABLE.toString();
        }
        if (strategy == InheritanceStrategy.SUPERCLASS_TABLE) {
            return org.datanucleus.metadata.InheritanceStrategy.SUPERCLASS_TABLE.toString();
        }
        try {
            if (strategy == InheritanceStrategy.COMPLETE_TABLE) {
                return org.datanucleus.metadata.InheritanceStrategy.COMPLETE_TABLE.toString();
            }
        }
        catch (Exception e) {}
        catch (Error error) {}
        return null;
    }
    
    public static String getDiscriminatorStrategyString(final DiscriminatorStrategy strategy) {
        if (strategy == DiscriminatorStrategy.NONE) {
            return org.datanucleus.metadata.DiscriminatorStrategy.NONE.toString();
        }
        if (strategy == DiscriminatorStrategy.VALUE_MAP) {
            return org.datanucleus.metadata.DiscriminatorStrategy.VALUE_MAP.toString();
        }
        if (strategy == DiscriminatorStrategy.CLASS_NAME) {
            return org.datanucleus.metadata.DiscriminatorStrategy.CLASS_NAME.toString();
        }
        return null;
    }
    
    public static ColumnMetaData getColumnMetaDataForAnnotations(final HashMap<String, Object> annotationValues) {
        final ColumnMetaData colmd = new ColumnMetaData();
        colmd.setName(annotationValues.get("name"));
        colmd.setTarget(annotationValues.get("target"));
        colmd.setTargetMember(annotationValues.get("targetField"));
        colmd.setJdbcType(annotationValues.get("jdbcType"));
        colmd.setSqlType(annotationValues.get("sqlType"));
        colmd.setLength(annotationValues.get("length"));
        colmd.setScale(annotationValues.get("scale"));
        colmd.setAllowsNull(annotationValues.get("allowsNull"));
        colmd.setDefaultValue(annotationValues.get("defaultValue"));
        colmd.setInsertValue(annotationValues.get("insertValue"));
        if (annotationValues.containsKey("position")) {
            colmd.setPosition(annotationValues.get("position"));
        }
        addExtensionsToMetaData(colmd, annotationValues.get("extensions"));
        return colmd;
    }
    
    public static ColumnMetaData getColumnMetaDataForColumnAnnotation(final Column col) {
        String length = null;
        String scale = null;
        if (col.length() > 0) {
            length = "" + col.length();
        }
        if (col.scale() >= 0) {
            scale = "" + col.scale();
        }
        final ColumnMetaData colmd = new ColumnMetaData();
        colmd.setName(col.name());
        colmd.setTarget(col.target());
        colmd.setTargetMember(col.targetMember());
        colmd.setJdbcType(col.jdbcType());
        colmd.setSqlType(col.sqlType());
        colmd.setLength(length);
        colmd.setScale(scale);
        colmd.setAllowsNull(col.allowsNull());
        colmd.setDefaultValue(col.defaultValue());
        colmd.setInsertValue(col.insertValue());
        try {
            final Method posMethod = col.getClass().getDeclaredMethod("position", null);
            final Integer posValue = (Integer)posMethod.invoke(col, (Object[])null);
            colmd.setPosition(posValue);
        }
        catch (Exception ex) {}
        addExtensionsToMetaData(colmd, col.extensions());
        return colmd;
    }
    
    public static IndexMetaData getIndexMetaData(final String name, final String table, final String unique, final String[] fields, final Column[] columns) {
        final IndexMetaData idxmd = new IndexMetaData();
        idxmd.setName(name);
        idxmd.setTable(table);
        if (!StringUtils.isWhitespace(unique)) {
            idxmd.setUnique(Boolean.valueOf(unique));
        }
        if (fields != null && fields.length > 0) {
            for (int j = 0; j < fields.length; ++j) {
                idxmd.addMember(fields[j]);
            }
        }
        if (idxmd.getNumberOfMembers() == 0 && columns != null && columns.length > 0) {
            for (int j = 0; j < columns.length; ++j) {
                final ColumnMetaData colmd = getColumnMetaDataForColumnAnnotation(columns[j]);
                idxmd.addColumn(colmd);
            }
        }
        return idxmd;
    }
    
    public static UniqueMetaData getUniqueMetaData(final String name, final String table, final String deferred, final String[] fields, final Column[] columns) {
        final UniqueMetaData unimd = new UniqueMetaData();
        unimd.setName(name);
        unimd.setTable(table);
        if (!StringUtils.isWhitespace(deferred)) {
            unimd.setDeferred(Boolean.valueOf(deferred));
        }
        if (fields != null && fields.length > 0) {
            for (int j = 0; j < fields.length; ++j) {
                unimd.addMember(fields[j]);
            }
        }
        if (unimd.getNumberOfMembers() == 0 && columns != null && columns.length > 0) {
            for (int j = 0; j < columns.length; ++j) {
                final ColumnMetaData colmd = getColumnMetaDataForColumnAnnotation(columns[j]);
                unimd.addColumn(colmd);
            }
        }
        return unimd;
    }
    
    public static ForeignKeyMetaData getFKMetaData(final String name, final String table, final String unique, final String deferred, final String deleteAction, final String updateAction, final String[] fields, final Column[] columns) {
        final ForeignKeyMetaData fkmd = new ForeignKeyMetaData();
        fkmd.setName(name);
        fkmd.setTable(table);
        fkmd.setUnique(unique);
        fkmd.setDeferred(deferred);
        fkmd.setDeleteAction(org.datanucleus.metadata.ForeignKeyAction.getForeignKeyAction(deleteAction));
        fkmd.setUpdateAction(org.datanucleus.metadata.ForeignKeyAction.getForeignKeyAction(updateAction));
        if (fields != null && fields.length > 0) {
            for (int j = 0; j < fields.length; ++j) {
                fkmd.addMember(fields[j]);
            }
        }
        if (fkmd.getNumberOfMembers() == 0 && columns != null && columns.length > 0) {
            for (int j = 0; j < columns.length; ++j) {
                final ColumnMetaData colmd = getColumnMetaDataForColumnAnnotation(columns[j]);
                fkmd.addColumn(colmd);
            }
        }
        return fkmd;
    }
    
    public static void addExtensionsToMetaData(final MetaData metadata, final Extension[] extensions) {
        if (extensions == null || extensions.length == 0) {
            return;
        }
        for (int i = 0; i < extensions.length; ++i) {
            metadata.addExtension(extensions[i].vendorName(), extensions[i].key(), extensions[i].value());
        }
    }
    
    static {
        PERSISTENCE_CAPABLE = PersistenceCapable.class.getName();
        PERSISTENCE_AWARE = PersistenceAware.class.getName();
        EMBEDDED_ONLY = EmbeddedOnly.class.getName();
        VERSION = Version.class.getName();
        DATASTORE_IDENTITY = DatastoreIdentity.class.getName();
        PRIMARY_KEY = PrimaryKey.class.getName();
        JOINS = Joins.class.getName();
        JOIN = Join.class.getName();
        INHERITANCE = Inheritance.class.getName();
        DISCRIMINATOR = Discriminator.class.getName();
        QUERIES = Queries.class.getName();
        QUERY = Query.class.getName();
        FETCHPLAN = FetchPlan.class.getName();
        FETCHPLANS = FetchPlans.class.getName();
        FETCHGROUPS = FetchGroups.class.getName();
        FETCHGROUP = FetchGroup.class.getName();
        SEQUENCE = Sequence.class.getName();
        INDICES = Indices.class.getName();
        INDEX = Index.class.getName();
        UNIQUES = Uniques.class.getName();
        UNIQUE = Unique.class.getName();
        FOREIGNKEYS = ForeignKeys.class.getName();
        FOREIGNKEY = ForeignKey.class.getName();
        COLUMNS = Columns.class.getName();
        COLUMN = Column.class.getName();
        EXTENSIONS = Extensions.class.getName();
        EXTENSION = Extension.class.getName();
        PERSISTENT = Persistent.class.getName();
        TRANSACTIONAL = Transactional.class.getName();
        NOTPERSISTENT = NotPersistent.class.getName();
        SERIALIZED = Serialized.class.getName();
        ELEMENT = Element.class.getName();
        KEY = Key.class.getName();
        VALUE = Value.class.getName();
        ORDER = Order.class.getName();
        EMBEDDED = Embedded.class.getName();
    }
}
