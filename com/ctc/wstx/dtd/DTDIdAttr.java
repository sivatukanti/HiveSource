// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.dtd;

import com.ctc.wstx.cfg.ErrorConsts;
import com.ctc.wstx.sr.InputProblemReporter;
import javax.xml.stream.XMLStreamException;
import com.ctc.wstx.util.ElementId;
import javax.xml.stream.Location;
import com.ctc.wstx.util.ElementIdMap;
import com.ctc.wstx.io.WstxInputData;
import com.ctc.wstx.util.PrefixedName;

public final class DTDIdAttr extends DTDAttribute
{
    public DTDIdAttr(final PrefixedName name, final DefaultAttrValue defValue, final int specIndex, final boolean nsAware, final boolean xml11) {
        super(name, defValue, specIndex, nsAware, xml11);
    }
    
    @Override
    public DTDAttribute cloneWith(final int specIndex) {
        return new DTDIdAttr(this.mName, this.mDefValue, specIndex, this.mCfgNsAware, this.mCfgXml11);
    }
    
    @Override
    public int getValueType() {
        return 2;
    }
    
    @Override
    public boolean typeIsId() {
        return true;
    }
    
    @Override
    public String validate(final DTDValidatorBase v, final char[] cbuf, int start, int end, final boolean normalize) throws XMLStreamException {
        while (start < end && WstxInputData.isSpaceChar(cbuf[start])) {
            ++start;
        }
        if (start >= end) {
            return this.reportValidationProblem(v, "Empty ID value");
        }
        --end;
        while (end > start && WstxInputData.isSpaceChar(cbuf[end])) {
            --end;
        }
        char c = cbuf[start];
        if (!WstxInputData.isNameStartChar(c, this.mCfgNsAware, this.mCfgXml11)) {
            return this.reportInvalidChar(v, c, "not valid as the first ID character");
        }
        int hash = c;
        for (int i = start + 1; i <= end; ++i) {
            c = cbuf[i];
            if (!WstxInputData.isNameChar(c, this.mCfgNsAware, this.mCfgXml11)) {
                return this.reportInvalidChar(v, c, "not valid as an ID character");
            }
            hash = hash * 31 + c;
        }
        final ElementIdMap m = v.getIdMap();
        final PrefixedName elemName = v.getElemName();
        final Location loc = v.getLocation();
        final ElementId id = m.addDefined(cbuf, start, end - start + 1, hash, loc, elemName, this.mName);
        if (id.getLocation() != loc) {
            return this.reportValidationProblem(v, "Duplicate id '" + id.getId() + "', first declared at " + id.getLocation());
        }
        if (normalize) {
            return id.getId();
        }
        return null;
    }
    
    @Override
    public void validateDefault(final InputProblemReporter rep, final boolean normalize) {
        throw new IllegalStateException(ErrorConsts.ERR_INTERNAL);
    }
}
