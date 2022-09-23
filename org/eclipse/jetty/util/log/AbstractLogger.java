// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util.log;

import java.util.Properties;

public abstract class AbstractLogger implements Logger
{
    public static final int LEVEL_DEFAULT = -1;
    public static final int LEVEL_ALL = 0;
    public static final int LEVEL_DEBUG = 1;
    public static final int LEVEL_INFO = 2;
    public static final int LEVEL_WARN = 3;
    public static final int LEVEL_OFF = 10;
    
    @Override
    public final Logger getLogger(final String name) {
        if (isBlank(name)) {
            return this;
        }
        final String basename = this.getName();
        final String fullname = (isBlank(basename) || Log.getRootLogger() == this) ? name : (basename + "." + name);
        Logger logger = Log.getLoggers().get(fullname);
        if (logger == null) {
            final Logger newlog = this.newLogger(fullname);
            logger = Log.getMutableLoggers().putIfAbsent(fullname, newlog);
            if (logger == null) {
                logger = newlog;
            }
        }
        return logger;
    }
    
    protected abstract Logger newLogger(final String p0);
    
    private static boolean isBlank(final String name) {
        if (name == null) {
            return true;
        }
        for (int size = name.length(), i = 0; i < size; ++i) {
            final char c = name.charAt(i);
            if (!Character.isWhitespace(c)) {
                return false;
            }
        }
        return true;
    }
    
    public static int lookupLoggingLevel(final Properties props, final String name) {
        if (props == null || props.isEmpty() || name == null) {
            return -1;
        }
        String nameSegment = name;
        while (nameSegment != null && nameSegment.length() > 0) {
            final String levelStr = props.getProperty(nameSegment + ".LEVEL");
            final int level = getLevelId(nameSegment + ".LEVEL", levelStr);
            if (level != -1) {
                return level;
            }
            final int idx = nameSegment.lastIndexOf(46);
            if (idx >= 0) {
                nameSegment = nameSegment.substring(0, idx);
            }
            else {
                nameSegment = null;
            }
        }
        return -1;
    }
    
    public static String getLoggingProperty(final Properties props, final String name, final String property) {
        int idx;
        for (String nameSegment = name; nameSegment != null && nameSegment.length() > 0; nameSegment = ((idx >= 0) ? nameSegment.substring(0, idx) : null)) {
            final String s = props.getProperty(nameSegment + "." + property);
            if (s != null) {
                return s;
            }
            idx = nameSegment.lastIndexOf(46);
        }
        return null;
    }
    
    protected static int getLevelId(final String levelSegment, final String levelName) {
        if (levelName == null) {
            return -1;
        }
        final String levelStr = levelName.trim();
        if ("ALL".equalsIgnoreCase(levelStr)) {
            return 0;
        }
        if ("DEBUG".equalsIgnoreCase(levelStr)) {
            return 1;
        }
        if ("INFO".equalsIgnoreCase(levelStr)) {
            return 2;
        }
        if ("WARN".equalsIgnoreCase(levelStr)) {
            return 3;
        }
        if ("OFF".equalsIgnoreCase(levelStr)) {
            return 10;
        }
        System.err.println("Unknown StdErrLog level [" + levelSegment + "]=[" + levelStr + "], expecting only [ALL, DEBUG, INFO, WARN, OFF] as values.");
        return -1;
    }
    
    protected static String condensePackageString(final String classname) {
        final String[] parts = classname.split("\\.");
        final StringBuilder dense = new StringBuilder();
        for (int i = 0; i < parts.length - 1; ++i) {
            dense.append(parts[i].charAt(0));
        }
        if (dense.length() > 0) {
            dense.append('.');
        }
        dense.append(parts[parts.length - 1]);
        return dense.toString();
    }
    
    @Override
    public void debug(final String msg, final long arg) {
        if (this.isDebugEnabled()) {
            this.debug(msg, new Long(arg));
        }
    }
}
