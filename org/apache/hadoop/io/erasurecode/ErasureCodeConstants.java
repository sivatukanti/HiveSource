// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.erasurecode;

public final class ErasureCodeConstants
{
    public static final String DUMMY_CODEC_NAME = "dummy";
    public static final String RS_CODEC_NAME = "rs";
    public static final String RS_LEGACY_CODEC_NAME = "rs-legacy";
    public static final String XOR_CODEC_NAME = "xor";
    public static final String HHXOR_CODEC_NAME = "hhxor";
    public static final String REPLICATION_CODEC_NAME = "replication";
    public static final ECSchema RS_6_3_SCHEMA;
    public static final ECSchema RS_3_2_SCHEMA;
    public static final ECSchema RS_6_3_LEGACY_SCHEMA;
    public static final ECSchema XOR_2_1_SCHEMA;
    public static final ECSchema RS_10_4_SCHEMA;
    public static final ECSchema REPLICATION_1_2_SCHEMA;
    public static final byte MAX_POLICY_ID = Byte.MAX_VALUE;
    public static final byte USER_DEFINED_POLICY_START_ID = 64;
    public static final byte REPLICATION_POLICY_ID = 0;
    public static final String REPLICATION_POLICY_NAME = "replication";
    
    private ErasureCodeConstants() {
    }
    
    static {
        RS_6_3_SCHEMA = new ECSchema("rs", 6, 3);
        RS_3_2_SCHEMA = new ECSchema("rs", 3, 2);
        RS_6_3_LEGACY_SCHEMA = new ECSchema("rs-legacy", 6, 3);
        XOR_2_1_SCHEMA = new ECSchema("xor", 2, 1);
        RS_10_4_SCHEMA = new ECSchema("rs", 10, 4);
        REPLICATION_1_2_SCHEMA = new ECSchema("replication", 1, 2);
    }
}
