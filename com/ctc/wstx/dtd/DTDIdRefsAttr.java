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

public final class DTDIdRefsAttr extends DTDAttribute
{
    public DTDIdRefsAttr(final PrefixedName name, final DefaultAttrValue defValue, final int specIndex, final boolean nsAware, final boolean xml11) {
        super(name, defValue, specIndex, nsAware, xml11);
    }
    
    @Override
    public DTDAttribute cloneWith(final int specIndex) {
        return new DTDIdRefsAttr(this.mName, this.mDefValue, specIndex, this.mCfgNsAware, this.mCfgXml11);
    }
    
    @Override
    public int getValueType() {
        return 4;
    }
    
    @Override
    public String validate(final DTDValidatorBase v, final char[] cbuf, int start, int end, final boolean normalize) throws XMLStreamException {
        while (start < end && WstxInputData.isSpaceChar(cbuf[start])) {
            ++start;
        }
        if (start >= end) {
            return this.reportValidationProblem(v, "Empty IDREFS value");
        }
        --end;
        while (end > start && WstxInputData.isSpaceChar(cbuf[end])) {
            --end;
        }
        final ElementIdMap m = v.getIdMap();
        final Location loc = v.getLocation();
        String idStr = null;
        StringBuilder sb = null;
        while (start <= end) {
            char c = cbuf[start];
            if (!WstxInputData.isNameStartChar(c, this.mCfgNsAware, this.mCfgXml11)) {
                return this.reportInvalidChar(v, c, "not valid as the first IDREFS character");
            }
            int hash = c;
            int i;
            for (i = start + 1; i <= end; ++i) {
                c = cbuf[i];
                if (WstxInputData.isSpaceChar(c)) {
                    break;
                }
                if (!WstxInputData.isNameChar(c, this.mCfgNsAware, this.mCfgXml11)) {
                    return this.reportInvalidChar(v, c, "not valid as an IDREFS character");
                }
                hash = hash * 31 + c;
            }
            final ElementId id = m.addReferenced(cbuf, start, i - start, hash, loc, v.getElemName(), this.mName);
            start = i + 1;
            if (normalize) {
                if (idStr == null) {
                    idStr = id.getId();
                }
                else {
                    if (sb == null) {
                        sb = new StringBuilder(idStr);
                    }
                    idStr = id.getId();
                    sb.append(' ');
                    sb.append(idStr);
                }
            }
            while (start <= end && WstxInputData.isSpaceChar(cbuf[start])) {
                ++start;
            }
        }
        if (normalize) {
            if (sb != null) {
                idStr = sb.toString();
            }
            return idStr;
        }
        return null;
    }
    
    @Override
    public void validateDefault(final InputProblemReporter rep, final boolean normalize) throws XMLStreamException {
        final String def = this.validateDefaultNames(rep, normalize);
        if (normalize) {
            this.mDefValue.setValue(def);
        }
    }
}
