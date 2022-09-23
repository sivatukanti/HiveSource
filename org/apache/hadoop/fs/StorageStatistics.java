// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

import java.util.Iterator;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
public abstract class StorageStatistics
{
    private final String name;
    
    public StorageStatistics(final String name) {
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getScheme() {
        return null;
    }
    
    public abstract Iterator<LongStatistic> getLongStatistics();
    
    public abstract Long getLong(final String p0);
    
    public abstract boolean isTracked(final String p0);
    
    public abstract void reset();
    
    public static class LongStatistic
    {
        private final String name;
        private final long value;
        
        public LongStatistic(final String name, final long value) {
            this.name = name;
            this.value = value;
        }
        
        public String getName() {
            return this.name;
        }
        
        public long getValue() {
            return this.value;
        }
        
        @Override
        public String toString() {
            return this.name + " = " + this.value;
        }
    }
    
    public interface CommonStatisticNames
    {
        public static final String OP_APPEND = "op_append";
        public static final String OP_COPY_FROM_LOCAL_FILE = "op_copy_from_local_file";
        public static final String OP_CREATE = "op_create";
        public static final String OP_CREATE_NON_RECURSIVE = "op_create_non_recursive";
        public static final String OP_DELETE = "op_delete";
        public static final String OP_EXISTS = "op_exists";
        public static final String OP_GET_CONTENT_SUMMARY = "op_get_content_summary";
        public static final String OP_GET_FILE_CHECKSUM = "op_get_file_checksum";
        public static final String OP_GET_FILE_STATUS = "op_get_file_status";
        public static final String OP_GET_STATUS = "op_get_status";
        public static final String OP_GLOB_STATUS = "op_glob_status";
        public static final String OP_IS_FILE = "op_is_file";
        public static final String OP_IS_DIRECTORY = "op_is_directory";
        public static final String OP_LIST_FILES = "op_list_files";
        public static final String OP_LIST_LOCATED_STATUS = "op_list_located_status";
        public static final String OP_LIST_STATUS = "op_list_status";
        public static final String OP_MKDIRS = "op_mkdirs";
        public static final String OP_MODIFY_ACL_ENTRIES = "op_modify_acl_entries";
        public static final String OP_OPEN = "op_open";
        public static final String OP_REMOVE_ACL = "op_remove_acl";
        public static final String OP_REMOVE_ACL_ENTRIES = "op_remove_acl_entries";
        public static final String OP_REMOVE_DEFAULT_ACL = "op_remove_default_acl";
        public static final String OP_RENAME = "op_rename";
        public static final String OP_SET_ACL = "op_set_acl";
        public static final String OP_SET_OWNER = "op_set_owner";
        public static final String OP_SET_PERMISSION = "op_set_permission";
        public static final String OP_SET_TIMES = "op_set_times";
        public static final String OP_TRUNCATE = "op_truncate";
    }
}
