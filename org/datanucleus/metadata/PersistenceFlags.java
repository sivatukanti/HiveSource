// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.metadata;

public class PersistenceFlags
{
    public static final byte READ_WRITE_OK = 0;
    public static final byte LOAD_REQUIRED = 1;
    public static final byte READ_OK = -1;
    public static final byte CHECK_READ = 1;
    public static final byte MEDIATE_READ = 2;
    public static final byte CHECK_WRITE = 4;
    public static final byte MEDIATE_WRITE = 8;
    public static final byte SERIALIZABLE = 16;
    
    public static String persistenceFlagsToString(final byte flags) {
        switch (flags) {
            case 1: {
                return "LOAD_REQUIRED";
            }
            case -1: {
                return "READ_OK";
            }
            case 0: {
                return "READ_WRITE_OK";
            }
            default: {
                return "???";
            }
        }
    }
}
