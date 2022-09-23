// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore;

public class ProtectMode
{
    public static String PARAMETER_NAME;
    public static String FLAG_OFFLINE;
    public static String FLAG_NO_DROP;
    public static String FLAG_NO_DROP_CASCADE;
    public static String FLAG_READ_ONLY;
    public boolean offline;
    public boolean readOnly;
    public boolean noDrop;
    public boolean noDropCascade;
    
    public static ProtectMode getProtectModeFromString(final String sourceString) {
        return new ProtectMode(sourceString);
    }
    
    private ProtectMode(final String sourceString) {
        this.offline = false;
        this.readOnly = false;
        this.noDrop = false;
        this.noDropCascade = false;
        final String[] split;
        final String[] tokens = split = sourceString.split(",");
        for (final String token : split) {
            if (token.equalsIgnoreCase(ProtectMode.FLAG_OFFLINE)) {
                this.offline = true;
            }
            else if (token.equalsIgnoreCase(ProtectMode.FLAG_NO_DROP)) {
                this.noDrop = true;
            }
            else if (token.equalsIgnoreCase(ProtectMode.FLAG_NO_DROP_CASCADE)) {
                this.noDropCascade = true;
            }
            else if (token.equalsIgnoreCase(ProtectMode.FLAG_READ_ONLY)) {
                this.readOnly = true;
            }
        }
    }
    
    public ProtectMode() {
        this.offline = false;
        this.readOnly = false;
        this.noDrop = false;
        this.noDropCascade = false;
    }
    
    @Override
    public String toString() {
        String retString = null;
        if (this.offline) {
            retString = ProtectMode.FLAG_OFFLINE;
        }
        if (this.noDrop) {
            if (retString != null) {
                retString = retString + "," + ProtectMode.FLAG_NO_DROP;
            }
            else {
                retString = ProtectMode.FLAG_NO_DROP;
            }
        }
        if (this.noDropCascade) {
            if (retString != null) {
                retString = retString + "," + ProtectMode.FLAG_NO_DROP_CASCADE;
            }
            else {
                retString = ProtectMode.FLAG_NO_DROP_CASCADE;
            }
        }
        if (this.readOnly) {
            if (retString != null) {
                retString = retString + "," + ProtectMode.FLAG_READ_ONLY;
            }
            else {
                retString = ProtectMode.FLAG_READ_ONLY;
            }
        }
        return retString;
    }
    
    static {
        ProtectMode.PARAMETER_NAME = "PROTECT_MODE";
        ProtectMode.FLAG_OFFLINE = "OFFLINE";
        ProtectMode.FLAG_NO_DROP = "NO_DROP";
        ProtectMode.FLAG_NO_DROP_CASCADE = "NO_DROP_CASCADE";
        ProtectMode.FLAG_READ_ONLY = "READ_ONLY";
    }
}
