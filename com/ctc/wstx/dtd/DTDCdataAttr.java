// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.dtd;

import javax.xml.stream.XMLStreamException;
import com.ctc.wstx.sr.InputProblemReporter;
import org.codehaus.stax2.validation.XMLValidationException;
import com.ctc.wstx.util.PrefixedName;

public final class DTDCdataAttr extends DTDAttribute
{
    public DTDCdataAttr(final PrefixedName name, final DefaultAttrValue defValue, final int specIndex, final boolean nsAware, final boolean xml11) {
        super(name, defValue, specIndex, nsAware, xml11);
    }
    
    @Override
    public DTDAttribute cloneWith(final int specIndex) {
        return new DTDCdataAttr(this.mName, this.mDefValue, specIndex, this.mCfgNsAware, this.mCfgXml11);
    }
    
    @Override
    public String validate(final DTDValidatorBase v, final char[] cbuf, final int start, final int end, final boolean normalize) throws XMLValidationException {
        return null;
    }
    
    @Override
    public void validateDefault(final InputProblemReporter rep, final boolean normalize) throws XMLStreamException {
    }
    
    @Override
    public String normalize(final DTDValidatorBase v, final char[] cbuf, final int start, final int end) {
        return null;
    }
    
    @Override
    public void normalizeDefault() {
    }
}
