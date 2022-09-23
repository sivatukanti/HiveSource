// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms;

import org.datanucleus.properties.PersistencePropertyValidator;

public class RDBMSPropertyValidator implements PersistencePropertyValidator
{
    @Override
    public boolean validate(final String name, final Object value) {
        if (name == null) {
            return false;
        }
        if (name.equalsIgnoreCase("datanucleus.rdbms.query.fetchDirection")) {
            if (value instanceof String) {
                final String strVal = (String)value;
                if (strVal.equalsIgnoreCase("forward") || strVal.equalsIgnoreCase("reverse") || strVal.equalsIgnoreCase("unknown")) {
                    return true;
                }
            }
        }
        else if (name.equalsIgnoreCase("datanucleus.rdbms.query.resultSetType")) {
            if (value instanceof String) {
                final String strVal = (String)value;
                if (strVal.equalsIgnoreCase("forward-only") || strVal.equalsIgnoreCase("scroll-sensitive") || strVal.equalsIgnoreCase("scroll-insensitive")) {
                    return true;
                }
            }
        }
        else if (name.equalsIgnoreCase("datanucleus.rdbms.query.resultSetConcurrency")) {
            if (value instanceof String) {
                final String strVal = (String)value;
                if (strVal.equalsIgnoreCase("read-only") || strVal.equalsIgnoreCase("updateable")) {
                    return true;
                }
            }
        }
        else if (name.equalsIgnoreCase("datanucleus.rdbms.constraintCreateMode")) {
            if (value instanceof String) {
                final String strVal = (String)value;
                if (strVal.equalsIgnoreCase("DataNucleus") || strVal.equalsIgnoreCase("JDO2")) {
                    return true;
                }
            }
        }
        else if (name.equalsIgnoreCase("datanucleus.rdbms.stringLengthExceededAction")) {
            if (value instanceof String) {
                final String strVal = (String)value;
                if (strVal.equalsIgnoreCase("EXCEPTION") || strVal.equalsIgnoreCase("TRUNCATE")) {
                    return true;
                }
            }
        }
        else if (name.equalsIgnoreCase("datanucleus.rdbms.initializeColumnInfo")) {
            if (value instanceof String) {
                final String strVal = (String)value;
                if (strVal.equalsIgnoreCase("ALL") || strVal.equalsIgnoreCase("PK") || strVal.equalsIgnoreCase("NONE")) {
                    return true;
                }
            }
        }
        else if (name.equalsIgnoreCase("datanucleus.rdbms.statementLogging") && value instanceof String) {
            final String strVal = (String)value;
            if (strVal.equalsIgnoreCase("jdbc") || strVal.equalsIgnoreCase("values") || strVal.equalsIgnoreCase("values-in-brackets")) {
                return true;
            }
        }
        return false;
    }
}
