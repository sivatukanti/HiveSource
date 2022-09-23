// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore.events;

import org.apache.hadoop.hive.metastore.api.NoSuchObjectException;
import org.apache.hadoop.hive.metastore.api.MetaException;
import org.apache.hadoop.hive.metastore.api.Table;
import org.apache.hadoop.hive.metastore.api.FieldSchema;
import java.util.LinkedHashMap;
import org.apache.hadoop.hive.metastore.HiveMetaStore;
import java.util.List;
import java.util.Map;

public class InsertEvent extends ListenerEvent
{
    private final String db;
    private final String table;
    private final Map<String, String> keyValues;
    private final List<String> files;
    
    public InsertEvent(final String db, final String table, final List<String> partVals, final List<String> files, final boolean status, final HiveMetaStore.HMSHandler handler) throws MetaException, NoSuchObjectException {
        super(status, handler);
        this.db = db;
        this.table = table;
        this.files = files;
        final Table t = handler.get_table(db, table);
        this.keyValues = new LinkedHashMap<String, String>();
        if (partVals != null) {
            for (int i = 0; i < partVals.size(); ++i) {
                this.keyValues.put(t.getPartitionKeys().get(i).getName(), partVals.get(i));
            }
        }
    }
    
    public String getDb() {
        return this.db;
    }
    
    public String getTable() {
        return this.table;
    }
    
    public Map<String, String> getPartitionKeyValues() {
        return this.keyValues;
    }
    
    public List<String> getFiles() {
        return this.files;
    }
}
