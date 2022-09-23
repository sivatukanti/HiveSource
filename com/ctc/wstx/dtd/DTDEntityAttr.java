// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.dtd;

import com.ctc.wstx.sr.InputProblemReporter;
import javax.xml.stream.XMLStreamException;
import com.ctc.wstx.ent.EntityDecl;
import com.ctc.wstx.io.WstxInputData;
import com.ctc.wstx.util.PrefixedName;

public final class DTDEntityAttr extends DTDAttribute
{
    public DTDEntityAttr(final PrefixedName name, final DefaultAttrValue defValue, final int specIndex, final boolean nsAware, final boolean xml11) {
        super(name, defValue, specIndex, nsAware, xml11);
    }
    
    @Override
    public DTDAttribute cloneWith(final int specIndex) {
        return new DTDEntityAttr(this.mName, this.mDefValue, specIndex, this.mCfgNsAware, this.mCfgXml11);
    }
    
    @Override
    public int getValueType() {
        return 5;
    }
    
    @Override
    public String validate(final DTDValidatorBase v, final char[] cbuf, int start, int end, final boolean normalize) throws XMLStreamException {
        while (start < end && WstxInputData.isSpaceChar(cbuf[start])) {
            ++start;
        }
        if (start >= end) {
            return this.reportValidationProblem(v, "Empty ENTITY value");
        }
        --end;
        while (end > start && WstxInputData.isSpaceChar(cbuf[end])) {
            --end;
        }
        char c = cbuf[start];
        if (!WstxInputData.isNameStartChar(c, this.mCfgNsAware, this.mCfgXml11) && c != ':') {
            return this.reportInvalidChar(v, c, "not valid as the first ID character");
        }
        for (int i = start + 1; i <= end; ++i) {
            c = cbuf[i];
            if (!WstxInputData.isNameChar(c, this.mCfgNsAware, this.mCfgXml11)) {
                return this.reportInvalidChar(v, c, "not valid as an ID character");
            }
        }
        final EntityDecl ent = this.findEntityDecl(v, cbuf, start, end - start + 1);
        return normalize ? ent.getName() : null;
    }
    
    @Override
    public void validateDefault(final InputProblemReporter rep, final boolean normalize) throws XMLStreamException {
        final String normStr = this.validateDefaultName(rep, normalize);
        if (normalize) {
            this.mDefValue.setValue(normStr);
        }
        final EntityDecl ent = ((MinimalDTDReader)rep).findEntity(normStr);
        this.checkEntity(rep, normStr, ent);
    }
}
