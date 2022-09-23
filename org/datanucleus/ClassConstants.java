// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus;

import org.datanucleus.metadata.MetaDataManager;
import org.datanucleus.identity.OIDImpl;
import org.datanucleus.store.StoreManager;
import java.io.Serializable;
import java.sql.Timestamp;
import java.sql.Time;
import java.sql.Date;
import java.math.BigInteger;
import java.math.BigDecimal;

public class ClassConstants
{
    public static final ClassLoader NUCLEUS_CONTEXT_LOADER;
    public static final Class BOOLEAN;
    public static final Class BYTE;
    public static final Class CHAR;
    public static final Class DOUBLE;
    public static final Class FLOAT;
    public static final Class INT;
    public static final Class LONG;
    public static final Class SHORT;
    public static final Class JAVA_LANG_BOOLEAN;
    public static final Class JAVA_LANG_BYTE;
    public static final Class JAVA_LANG_CHARACTER;
    public static final Class JAVA_LANG_DOUBLE;
    public static final Class JAVA_LANG_FLOAT;
    public static final Class JAVA_LANG_INTEGER;
    public static final Class JAVA_LANG_LONG;
    public static final Class JAVA_LANG_SHORT;
    public static final Class JAVA_LANG_STRING;
    public static final Class JAVA_MATH_BIGDECIMAL;
    public static final Class JAVA_MATH_BIGINTEGER;
    public static final Class JAVA_SQL_DATE;
    public static final Class JAVA_SQL_TIME;
    public static final Class JAVA_SQL_TIMESTAMP;
    public static final Class JAVA_UTIL_DATE;
    public static final Class JAVA_IO_SERIALIZABLE;
    public static final Class NUCLEUS_CONTEXT;
    public static final Class CLASS_LOADER_RESOLVER;
    public static final Class STORE_MANAGER;
    public static final Class OID_IMPL;
    public static final Class METADATA_MANAGER;
    public static final Class EXECUTION_CONTEXT;
    
    static {
        NUCLEUS_CONTEXT_LOADER = NucleusContext.class.getClassLoader();
        BOOLEAN = Boolean.TYPE;
        BYTE = Byte.TYPE;
        CHAR = Character.TYPE;
        DOUBLE = Double.TYPE;
        FLOAT = Float.TYPE;
        INT = Integer.TYPE;
        LONG = Long.TYPE;
        SHORT = Short.TYPE;
        JAVA_LANG_BOOLEAN = Boolean.class;
        JAVA_LANG_BYTE = Byte.class;
        JAVA_LANG_CHARACTER = Character.class;
        JAVA_LANG_DOUBLE = Double.class;
        JAVA_LANG_FLOAT = Float.class;
        JAVA_LANG_INTEGER = Integer.class;
        JAVA_LANG_LONG = Long.class;
        JAVA_LANG_SHORT = Short.class;
        JAVA_LANG_STRING = String.class;
        JAVA_MATH_BIGDECIMAL = BigDecimal.class;
        JAVA_MATH_BIGINTEGER = BigInteger.class;
        JAVA_SQL_DATE = Date.class;
        JAVA_SQL_TIME = Time.class;
        JAVA_SQL_TIMESTAMP = Timestamp.class;
        JAVA_UTIL_DATE = java.util.Date.class;
        JAVA_IO_SERIALIZABLE = Serializable.class;
        NUCLEUS_CONTEXT = NucleusContext.class;
        CLASS_LOADER_RESOLVER = ClassLoaderResolver.class;
        STORE_MANAGER = StoreManager.class;
        OID_IMPL = OIDImpl.class;
        METADATA_MANAGER = MetaDataManager.class;
        EXECUTION_CONTEXT = ExecutionContext.class;
    }
}
