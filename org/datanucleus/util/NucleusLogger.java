// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import org.datanucleus.exceptions.NucleusException;
import java.util.Arrays;

public abstract class NucleusLogger
{
    private static Class LOGGER_CLASS;
    public static final NucleusLogger PERSISTENCE;
    public static final NucleusLogger TRANSACTION;
    public static final NucleusLogger CONNECTION;
    public static final NucleusLogger QUERY;
    public static final NucleusLogger METADATA;
    public static final NucleusLogger CACHE;
    public static final NucleusLogger DATASTORE;
    public static final NucleusLogger DATASTORE_PERSIST;
    public static final NucleusLogger DATASTORE_RETRIEVE;
    public static final NucleusLogger DATASTORE_SCHEMA;
    public static final NucleusLogger DATASTORE_NATIVE;
    public static final NucleusLogger LIFECYCLE;
    public static final NucleusLogger GENERAL;
    public static final NucleusLogger VALUEGENERATION;
    
    public static NucleusLogger getLoggerInstance(final String logCategory) {
        final Class[] ctrTypes = { String.class };
        final Object[] ctrArgs = { logCategory };
        Object obj;
        try {
            final Constructor ctor = NucleusLogger.LOGGER_CLASS.getConstructor((Class[])ctrTypes);
            obj = ctor.newInstance(ctrArgs);
        }
        catch (NoSuchMethodException e) {
            throw new NucleusException("Missing constructor in class " + NucleusLogger.LOGGER_CLASS.getName() + ", parameters " + Arrays.asList((Class[])ctrTypes).toString(), new Exception[] { e }).setFatal();
        }
        catch (IllegalAccessException e2) {
            throw new NucleusException("Failed attempting to access class " + NucleusLogger.LOGGER_CLASS.getName(), new Exception[] { e2 }).setFatal();
        }
        catch (InstantiationException e3) {
            throw new NucleusException("Failed instantiating a new object of type " + NucleusLogger.LOGGER_CLASS.getName(), new Exception[] { e3 }).setFatal();
        }
        catch (InvocationTargetException e4) {
            final Throwable t = e4.getTargetException();
            if (t instanceof RuntimeException) {
                throw (RuntimeException)t;
            }
            if (t instanceof Error) {
                throw (Error)t;
            }
            throw new NucleusException("Unexpected exception thrown by constructor for " + NucleusLogger.LOGGER_CLASS.getName() + "," + t).setFatal();
        }
        return (NucleusLogger)obj;
    }
    
    public abstract void debug(final Object p0);
    
    public abstract void debug(final Object p0, final Throwable p1);
    
    public abstract void info(final Object p0);
    
    public abstract void info(final Object p0, final Throwable p1);
    
    public abstract void warn(final Object p0);
    
    public abstract void warn(final Object p0, final Throwable p1);
    
    public abstract void error(final Object p0);
    
    public abstract void error(final Object p0, final Throwable p1);
    
    public abstract void fatal(final Object p0);
    
    public abstract void fatal(final Object p0, final Throwable p1);
    
    public abstract boolean isDebugEnabled();
    
    public abstract boolean isInfoEnabled();
    
    static {
        NucleusLogger.LOGGER_CLASS = null;
        Class loggerClass = null;
        try {
            NucleusLogger.class.getClassLoader().loadClass("org.apache.log4j.Logger");
            loggerClass = Log4JLogger.class;
        }
        catch (Exception e) {
            loggerClass = JDK14Logger.class;
        }
        NucleusLogger.LOGGER_CLASS = loggerClass;
        PERSISTENCE = getLoggerInstance("DataNucleus.Persistence");
        TRANSACTION = getLoggerInstance("DataNucleus.Transaction");
        CONNECTION = getLoggerInstance("DataNucleus.Connection");
        QUERY = getLoggerInstance("DataNucleus.Query");
        METADATA = getLoggerInstance("DataNucleus.MetaData");
        CACHE = getLoggerInstance("DataNucleus.Cache");
        DATASTORE = getLoggerInstance("DataNucleus.Datastore");
        DATASTORE_PERSIST = getLoggerInstance("DataNucleus.Datastore.Persist");
        DATASTORE_RETRIEVE = getLoggerInstance("DataNucleus.Datastore.Retrieve");
        DATASTORE_SCHEMA = getLoggerInstance("DataNucleus.Datastore.Schema");
        DATASTORE_NATIVE = getLoggerInstance("DataNucleus.Datastore.Native");
        LIFECYCLE = getLoggerInstance("DataNucleus.Lifecycle");
        GENERAL = getLoggerInstance("DataNucleus.General");
        VALUEGENERATION = getLoggerInstance("DataNucleus.ValueGeneration");
    }
}
