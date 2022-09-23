// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.dtd;

import java.util.StringTokenizer;
import com.ctc.wstx.sr.InputProblemReporter;
import javax.xml.stream.XMLStreamException;
import com.ctc.wstx.ent.EntityDecl;
import com.ctc.wstx.io.WstxInputData;
import com.ctc.wstx.util.PrefixedName;

public final class DTDEntitiesAttr extends DTDAttribute
{
    public DTDEntitiesAttr(final PrefixedName name, final DefaultAttrValue defValue, final int specIndex, final boolean nsAware, final boolean xml11) {
        super(name, defValue, specIndex, nsAware, xml11);
    }
    
    @Override
    public DTDAttribute cloneWith(final int specIndex) {
        return new DTDEntitiesAttr(this.mName, this.mDefValue, specIndex, this.mCfgNsAware, this.mCfgXml11);
    }
    
    @Override
    public int getValueType() {
        return 6;
    }
    
    @Override
    public String validate(final DTDValidatorBase v, final char[] cbuf, int start, int end, final boolean normalize) throws XMLStreamException {
        while (start < end && WstxInputData.isSpaceChar(cbuf[start])) {
            ++start;
        }
        if (start >= end) {
            return this.reportValidationProblem(v, "Empty ENTITIES value");
        }
        --end;
        while (end > start && WstxInputData.isSpaceChar(cbuf[end])) {
            --end;
        }
        String idStr = null;
        StringBuilder sb = null;
        while (start <= end) {
            char c = cbuf[start];
            if (!WstxInputData.isNameStartChar(c, this.mCfgNsAware, this.mCfgXml11)) {
                return this.reportInvalidChar(v, c, "not valid as the first ENTITIES character");
            }
            int i;
            for (i = start + 1; i <= end; ++i) {
                c = cbuf[i];
                if (WstxInputData.isSpaceChar(c)) {
                    break;
                }
                if (!WstxInputData.isNameChar(c, this.mCfgNsAware, this.mCfgXml11)) {
                    return this.reportInvalidChar(v, c, "not valid as an ENTITIES character");
                }
            }
            final EntityDecl ent = this.findEntityDecl(v, cbuf, start, i - start);
            start = i + 1;
            if (normalize) {
                if (idStr == null) {
                    idStr = ent.getName();
                }
                else {
                    if (sb == null) {
                        sb = new StringBuilder(idStr);
                    }
                    idStr = ent.getName();
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
        final String normStr = this.validateDefaultNames(rep, true);
        if (normalize) {
            this.mDefValue.setValue(normStr);
        }
        final StringTokenizer st = new StringTokenizer(normStr);
        final MinimalDTDReader dtdr = (MinimalDTDReader)rep;
        while (st.hasMoreTokens()) {
            final String str = st.nextToken();
            final EntityDecl ent = dtdr.findEntity(str);
            this.checkEntity(rep, normStr, ent);
        }
    }
}
