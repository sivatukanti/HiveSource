// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.dtd;

import com.ctc.wstx.sr.InputProblemReporter;
import javax.xml.stream.XMLStreamException;
import com.ctc.wstx.util.ElementId;
import javax.xml.stream.Location;
import com.ctc.wstx.util.ElementIdMap;
import com.ctc.wstx.io.WstxInputData;
import com.ctc.wstx.util.PrefixedName;

public final class DTDIdRefAttr extends DTDAttribute
{
    public DTDIdRefAttr(final PrefixedName name, final DefaultAttrValue defValue, final int specIndex, final boolean nsAware, final boolean xml11) {
        super(name, defValue, specIndex, nsAware, xml11);
    }
    
    @Override
    public DTDAttribute cloneWith(final int specIndex) {
        return new DTDIdRefAttr(this.mName, this.mDefValue, specIndex, this.mCfgNsAware, this.mCfgXml11);
    }
    
    @Override
    public int getValueType() {
        return 3;
    }
    
    @Override
    public String validate(final DTDValidatorBase v, final char[] cbuf, int start, int end, final boolean normalize) throws XMLStreamException {
        while (start < end && WstxInputData.isSpaceChar(cbuf[start])) {
            ++start;
        }
        if (start >= end) {
            return this.reportValidationProblem(v, "Empty IDREF value");
        }
        --end;
        while (end > start && WstxInputData.isSpaceChar(cbuf[end])) {
            --end;
        }
        char c = cbuf[start];
        if (!WstxInputData.isNameStartChar(c, this.mCfgNsAware, this.mCfgXml11)) {
            return this.reportInvalidChar(v, c, "not valid as the first IDREF character");
        }
        int hash = c;
        for (int i = start + 1; i <= end; ++i) {
            c = cbuf[i];
            if (!WstxInputData.isNameChar(c, this.mCfgNsAware, this.mCfgXml11)) {
                return this.reportInvalidChar(v, c, "not valid as an IDREF character");
            }
            hash = hash * 31 + c;
        }
        final ElementIdMap m = v.getIdMap();
        final Location loc = v.getLocation();
        final ElementId id = m.addReferenced(cbuf, start, end - start + 1, hash, loc, v.getElemName(), this.mName);
        return normalize ? id.getId() : null;
    }
    
    @Override
    public void validateDefault(final InputProblemReporter rep, final boolean normalize) throws XMLStreamException {
        final String def = this.validateDefaultName(rep, normalize);
        if (normalize) {
            this.mDefValue.setValue(def);
        }
    }
}
