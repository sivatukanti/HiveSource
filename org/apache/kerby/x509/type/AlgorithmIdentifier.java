// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.x509.type;

import org.apache.kerby.asn1.type.Asn1Any;
import org.apache.kerby.asn1.type.Asn1ObjectIdentifier;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.asn1.type.Asn1SequenceType;

public class AlgorithmIdentifier extends Asn1SequenceType
{
    static Asn1FieldInfo[] fieldInfos;
    
    public AlgorithmIdentifier() {
        super(AlgorithmIdentifier.fieldInfos);
    }
    
    public String getAlgorithm() {
        return this.getFieldAsObjId(AlgorithmIdentifierField.ALGORITHM);
    }
    
    public void setAlgorithm(final String algorithm) {
        this.setFieldAsObjId(AlgorithmIdentifierField.ALGORITHM, algorithm);
    }
    
    public <T extends Asn1Type> T getParametersAs(final Class<T> t) {
        return this.getFieldAsAny(AlgorithmIdentifierField.PARAMETERS, t);
    }
    
    public void setParameters(final Asn1Type parameters) {
        this.setFieldAsAny(AlgorithmIdentifierField.PARAMETERS, parameters);
    }
    
    static {
        AlgorithmIdentifier.fieldInfos = new Asn1FieldInfo[] { new Asn1FieldInfo(AlgorithmIdentifierField.ALGORITHM, Asn1ObjectIdentifier.class), new Asn1FieldInfo(AlgorithmIdentifierField.PARAMETERS, Asn1Any.class) };
    }
    
    protected enum AlgorithmIdentifierField implements EnumType
    {
        ALGORITHM, 
        PARAMETERS;
        
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
