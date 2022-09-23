// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.cms.type;

import org.apache.kerby.x509.type.AlgorithmIdentifier;
import org.apache.kerby.asn1.type.Asn1Type;
import org.apache.kerby.asn1.EnumType;
import org.apache.kerby.asn1.Asn1FieldInfo;
import org.apache.kerby.asn1.type.Asn1SequenceType;

public class CompressedData extends Asn1SequenceType
{
    static Asn1FieldInfo[] fieldInfos;
    
    public CompressedData() {
        super(CompressedData.fieldInfos);
    }
    
    public CmsVersion getVersion() {
        return this.getFieldAs(CompressedDataField.VERSION, CmsVersion.class);
    }
    
    public void setVersion(final CmsVersion version) {
        this.setFieldAs(CompressedDataField.VERSION, version);
    }
    
    public AlgorithmIdentifier getCompressionAlgorithm() {
        return this.getFieldAs(CompressedDataField.COMPRESSION_ALGORITHM, AlgorithmIdentifier.class);
    }
    
    public void setCompressionAlgorithm(final AlgorithmIdentifier compressionAlgorithm) {
        this.setFieldAs(CompressedDataField.COMPRESSION_ALGORITHM, compressionAlgorithm);
    }
    
    public EncapsulatedContentInfo getEncapContentInfo() {
        return this.getFieldAs(CompressedDataField.ENCAP_CONTENT_INFO, EncapsulatedContentInfo.class);
    }
    
    public void setEncapContentInfo(final EncapsulatedContentInfo encapContentInfo) {
        this.setFieldAs(CompressedDataField.ENCAP_CONTENT_INFO, encapContentInfo);
    }
    
    static {
        CompressedData.fieldInfos = new Asn1FieldInfo[] { new Asn1FieldInfo(CompressedDataField.VERSION, CmsVersion.class), new Asn1FieldInfo(CompressedDataField.COMPRESSION_ALGORITHM, AlgorithmIdentifier.class), new Asn1FieldInfo(CompressedDataField.ENCAP_CONTENT_INFO, EncapsulatedContentInfo.class) };
    }
    
    protected enum CompressedDataField implements EnumType
    {
        VERSION, 
        COMPRESSION_ALGORITHM, 
        ENCAP_CONTENT_INFO;
        
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
