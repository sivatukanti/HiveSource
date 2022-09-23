// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.security;

import java.nio.ByteBuffer;
import org.apache.hadoop.yarn.util.Records;
import javax.crypto.SecretKey;
import org.apache.hadoop.yarn.server.api.records.MasterKey;

public class MasterKeyData
{
    private final MasterKey masterKeyRecord;
    private final SecretKey generatedSecretKey;
    
    public MasterKeyData(final int serialNo, final SecretKey secretKey) {
        (this.masterKeyRecord = Records.newRecord(MasterKey.class)).setKeyId(serialNo);
        this.generatedSecretKey = secretKey;
        this.masterKeyRecord.setBytes(ByteBuffer.wrap(this.generatedSecretKey.getEncoded()));
    }
    
    public MasterKeyData(final MasterKey masterKeyRecord, final SecretKey secretKey) {
        this.masterKeyRecord = masterKeyRecord;
        this.generatedSecretKey = secretKey;
    }
    
    public MasterKey getMasterKey() {
        return this.masterKeyRecord;
    }
    
    public SecretKey getSecretKey() {
        return this.generatedSecretKey;
    }
}
