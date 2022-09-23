// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.properties;

import java.util.TimeZone;

public class CorePropertyValidator implements PersistencePropertyValidator
{
    @Override
    public boolean validate(final String name, final Object value) {
        if (name == null) {
            return false;
        }
        if (name.equalsIgnoreCase("datanucleus.autoStartMechanismMode")) {
            if (value instanceof String) {
                final String strVal = (String)value;
                if (strVal.equalsIgnoreCase("Quiet") || strVal.equalsIgnoreCase("Ignored") || strVal.equalsIgnoreCase("Checked")) {
                    return true;
                }
            }
        }
        else if (name.equalsIgnoreCase("datanucleus.flush.mode")) {
            if (value instanceof String) {
                final String strVal = (String)value;
                if (strVal.equalsIgnoreCase("Auto") || strVal.equalsIgnoreCase("Manual")) {
                    return true;
                }
            }
        }
        else if (name.equalsIgnoreCase("datanucleus.readOnlyDatastoreAction")) {
            if (value instanceof String) {
                final String strVal = (String)value;
                if (strVal.equalsIgnoreCase("EXCEPTION") || strVal.equalsIgnoreCase("LOG")) {
                    return true;
                }
            }
        }
        else if (name.equalsIgnoreCase("datanucleus.deletionPolicy")) {
            if (value instanceof String) {
                final String strVal = (String)value;
                if (strVal.equalsIgnoreCase("JDO2") || strVal.equalsIgnoreCase("DataNucleus")) {
                    return true;
                }
            }
        }
        else if (name.equalsIgnoreCase("datanucleus.defaultInheritanceStrategy")) {
            if (value instanceof String) {
                final String strVal = (String)value;
                if (strVal.equalsIgnoreCase("JDO2") || strVal.equalsIgnoreCase("TABLE_PER_CLASS")) {
                    return true;
                }
            }
        }
        else if (name.equalsIgnoreCase("datanucleus.identifier.case")) {
            if (value instanceof String) {
                final String strVal = (String)value;
                if (strVal.equalsIgnoreCase("UPPERCASE") || strVal.equalsIgnoreCase("lowercase") || strVal.equalsIgnoreCase("PreserveCase")) {
                    return true;
                }
            }
        }
        else if (name.equalsIgnoreCase("datanucleus.valuegeneration.transactionAttribute")) {
            if (value instanceof String) {
                final String strVal = (String)value;
                if (strVal.equalsIgnoreCase("New") || strVal.equalsIgnoreCase("UsePM")) {
                    return true;
                }
            }
        }
        else if (name.equalsIgnoreCase("datanucleus.valuegeneration.transactionIsolation") || name.equalsIgnoreCase("datanucleus.transactionIsolation")) {
            if (value instanceof String) {
                final String strVal = (String)value;
                if (strVal.equalsIgnoreCase("none") || strVal.equalsIgnoreCase("read-committed") || strVal.equalsIgnoreCase("read-uncommitted") || strVal.equalsIgnoreCase("repeatable-read") || strVal.equalsIgnoreCase("serializable") || strVal.equalsIgnoreCase("snapshot")) {
                    return true;
                }
            }
        }
        else if (name.equalsIgnoreCase("datanucleus.plugin.pluginRegistryBundleCheck")) {
            if (value instanceof String) {
                final String strVal = (String)value;
                if (strVal.equalsIgnoreCase("EXCEPTION") || strVal.equalsIgnoreCase("LOG") || strVal.equalsIgnoreCase("NONE")) {
                    return true;
                }
            }
        }
        else if (name.equalsIgnoreCase("datanucleus.TransactionType")) {
            if (value instanceof String) {
                final String strVal = (String)value;
                if (strVal.equalsIgnoreCase("RESOURCE_LOCAL") || strVal.equalsIgnoreCase("JTA")) {
                    return true;
                }
            }
        }
        else if (name.equalsIgnoreCase("datanucleus.ServerTimeZoneID")) {
            if (value instanceof String) {
                final String strVal = (String)value;
                final String[] availableIDs = TimeZone.getAvailableIDs();
                final boolean validZone = false;
                for (int i = 0; i < availableIDs.length; ++i) {
                    if (availableIDs[i].equals(strVal)) {
                        return true;
                    }
                }
                if (!validZone) {
                    return false;
                }
            }
        }
        else if (name.equalsIgnoreCase("datanucleus.connection.resourceType") || name.equalsIgnoreCase("datanucleus.connection2.resourceType")) {
            if (value instanceof String) {
                final String strVal = (String)value;
                if (strVal.equalsIgnoreCase("RESOURCE_LOCAL") || strVal.equalsIgnoreCase("JTA")) {
                    return true;
                }
            }
        }
        else if (name.equalsIgnoreCase("datanucleus.schemaTool.mode")) {
            if (value instanceof String) {
                final String strVal = (String)value;
                if (strVal.equalsIgnoreCase("create") || strVal.equalsIgnoreCase("delete") || strVal.equalsIgnoreCase("validate") || strVal.equalsIgnoreCase("schemainfo") || strVal.equalsIgnoreCase("dbinfo")) {
                    return true;
                }
            }
        }
        else if (name.equalsIgnoreCase("datanucleus.detachmentFields")) {
            if (value instanceof String) {
                final String strVal = (String)value;
                if (strVal.equalsIgnoreCase("load-fields") || strVal.equalsIgnoreCase("unload-fields") || strVal.equalsIgnoreCase("load-unload-fields")) {
                    return true;
                }
            }
        }
        else if (name.equalsIgnoreCase("datanucleus.detachedState")) {
            if (value instanceof String) {
                final String strVal = (String)value;
                if (strVal.equalsIgnoreCase("all") || strVal.equalsIgnoreCase("fetch-groups") || strVal.equalsIgnoreCase("loaded")) {
                    return true;
                }
            }
        }
        else if (name.equalsIgnoreCase("datanucleus.validation.mode")) {
            if (value instanceof String) {
                final String strVal = (String)value;
                if (strVal.equalsIgnoreCase("auto") || strVal.equalsIgnoreCase("none") || strVal.equalsIgnoreCase("callback")) {
                    return true;
                }
            }
        }
        else if (name.equalsIgnoreCase("datanucleus.cache.level2.mode")) {
            if (value instanceof String) {
                final String strVal = (String)value;
                if (strVal.equalsIgnoreCase("ENABLE_SELECTIVE") || strVal.equalsIgnoreCase("DISABLE_SELECTIVE") || strVal.equalsIgnoreCase("ALL") || strVal.equalsIgnoreCase("NONE") || strVal.equalsIgnoreCase("UNSPECIFIED")) {
                    return true;
                }
            }
        }
        else if (name.equalsIgnoreCase("datanucleus.generateSchema.database.mode") || name.equalsIgnoreCase("datanucleus.generateSchema.scripts.mode")) {
            if (value instanceof String) {
                final String strVal = (String)value;
                if (strVal.equalsIgnoreCase("none") || strVal.equalsIgnoreCase("create") || strVal.equalsIgnoreCase("drop-and-create") || strVal.equalsIgnoreCase("drop")) {
                    return true;
                }
            }
        }
        else if (name.equalsIgnoreCase("datanucleus.cache.level2.retrieveMode")) {
            if (value instanceof String) {
                final String strVal = (String)value;
                if (strVal.equalsIgnoreCase("use") || strVal.equalsIgnoreCase("bypass")) {
                    return true;
                }
            }
        }
        else if (name.equalsIgnoreCase("datanucleus.cache.level2.storeMode")) {
            if (value instanceof String) {
                final String strVal = (String)value;
                if (strVal.equalsIgnoreCase("use") || strVal.equalsIgnoreCase("bypass") || strVal.equalsIgnoreCase("refresh")) {
                    return true;
                }
            }
        }
        else if (name.equalsIgnoreCase("datanucleus.cache.level2.updateMode") && value instanceof String) {
            final String strVal = (String)value;
            if (strVal.equalsIgnoreCase("commit-and-datastore-read") || strVal.equalsIgnoreCase("commit-only")) {
                return true;
            }
        }
        return false;
    }
}
