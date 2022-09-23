// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.security;

public class IdMappingConstant
{
    public static final String USERGROUPID_UPDATE_MILLIS_KEY = "usergroupid.update.millis";
    public static final long USERGROUPID_UPDATE_MILLIS_DEFAULT = 900000L;
    public static final long USERGROUPID_UPDATE_MILLIS_MIN = 60000L;
    public static final String UNKNOWN_USER = "nobody";
    public static final String UNKNOWN_GROUP = "nobody";
    public static final String STATIC_ID_MAPPING_FILE_KEY = "static.id.mapping.file";
    public static final String STATIC_ID_MAPPING_FILE_DEFAULT = "/etc/nfs.map";
}
