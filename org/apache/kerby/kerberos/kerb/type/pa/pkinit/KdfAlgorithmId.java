// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.type.pa.pkinit;

import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.ExplicitField;
import org.apache.kerby.asn1.type.Asn1ObjectIdentifier;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.kerberos.kerb.type.KrbSequenceType;

public class KdfAlgorithmId extends KrbSequenceType
{
    static Asn1FieldInfo[] fieldInfos;
    
    public KdfAlgorithmId() {
        super(KdfAlgorithmId.fieldInfos);
    }
    
    public String getKdfId() {
        return this.getFieldAsObjId(KdfAlgorithmIdField.KDF_ID);
    }
    
    public void setKdfId(final String kdfId) {
        this.setFieldAsObjId(KdfAlgorithmIdField.KDF_ID, kdfId);
    }
    
    static {
        KdfAlgorithmId.fieldInfos = new Asn1FieldInfo[] { new ExplicitField(KdfAlgorithmIdField.KDF_ID, Asn1ObjectIdentifier.class) };
    }
    
    protected enum KdfAlgorithmIdField implements EnumType
    {
        KDF_ID;
        
        @Override
        public int getValue() {
            return this.ordinal();
        }
        
        @Override
        public String getName() {
            return this.name();
        }
    }
}
