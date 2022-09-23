// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.x509.type;

import org.apache.kerby.asn1.type.Asn1BitString;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.asn1.type.Asn1SequenceType;

public class SubjectPublicKeyInfo extends Asn1SequenceType
{
    static Asn1FieldInfo[] fieldInfos;
    
    public SubjectPublicKeyInfo() {
        super(SubjectPublicKeyInfo.fieldInfos);
    }
    
    public AlgorithmIdentifier getAlgorithm() {
        return this.getFieldAs(SubjectPublicKeyInfoField.ALGORITHM, AlgorithmIdentifier.class);
    }
    
    public void setAlgorithm(final AlgorithmIdentifier algorithm) {
        this.setFieldAs(SubjectPublicKeyInfoField.ALGORITHM, algorithm);
    }
    
    public Asn1BitString getSubjectPubKey() {
        return this.getFieldAs(SubjectPublicKeyInfoField.SUBJECT_PUBLIC_KEY, Asn1BitString.class);
    }
    
    public void setSubjectPubKey(final byte[] subjectPubKey) {
        this.setFieldAs(SubjectPublicKeyInfoField.SUBJECT_PUBLIC_KEY, new Asn1BitString(subjectPubKey));
    }
    
    static {
        SubjectPublicKeyInfo.fieldInfos = new Asn1FieldInfo[] { new Asn1FieldInfo(SubjectPublicKeyInfoField.ALGORITHM, AlgorithmIdentifier.class), new Asn1FieldInfo(SubjectPublicKeyInfoField.SUBJECT_PUBLIC_KEY, Asn1BitString.class) };
    }
    
    protected enum SubjectPublicKeyInfoField implements EnumType
    {
        ALGORITHM, 
        SUBJECT_PUBLIC_KEY;
        
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
