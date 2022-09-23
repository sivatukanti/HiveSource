// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.dtd;

import com.ctc.wstx.sr.InputProblemReporter;
import javax.xml.stream.XMLStreamException;
import com.ctc.wstx.io.WstxInputData;
import com.ctc.wstx.util.PrefixedName;

public final class DTDNmTokensAttr extends DTDAttribute
{
    public DTDNmTokensAttr(final PrefixedName name, final DefaultAttrValue defValue, final int specIndex, final boolean nsAware, final boolean xml11) {
        super(name, defValue, specIndex, nsAware, xml11);
    }
    
    @Override
    public DTDAttribute cloneWith(final int specIndex) {
        return new DTDNmTokensAttr(this.mName, this.mDefValue, specIndex, this.mCfgNsAware, this.mCfgXml11);
    }
    
    @Override
    public int getValueType() {
        return 9;
    }
    
    @Override
    public String validate(final DTDValidatorBase v, final char[] cbuf, int start, int end, final boolean normalize) throws XMLStreamException {
        while (start < end && WstxInputData.isSpaceChar(cbuf[start])) {
            ++start;
        }
        if (start >= end) {
            return this.reportValidationProblem(v, "Empty NMTOKENS value");
        }
        if (!normalize) {
            while (start < end) {
                final char c = cbuf[start];
                if (!WstxInputData.isSpaceChar(c) && !WstxInputData.isNameChar(c, this.mCfgNsAware, this.mCfgXml11)) {
                    return this.reportInvalidChar(v, c, "not valid as NMTOKENS character");
                }
                ++start;
            }
            return null;
        }
        --end;
        while (end > start && WstxInputData.isSpaceChar(cbuf[end])) {
            --end;
        }
        StringBuilder sb = null;
        while (start <= end) {
            int i;
            for (i = start; i <= end; ++i) {
                final char c2 = cbuf[i];
                if (WstxInputData.isSpaceChar(c2)) {
                    break;
                }
                if (!WstxInputData.isNameChar(c2, this.mCfgNsAware, this.mCfgXml11)) {
                    return this.reportInvalidChar(v, c2, "not valid as an NMTOKENS character");
                }
            }
            if (sb == null) {
                sb = new StringBuilder(end - start + 1);
            }
            else {
                sb.append(' ');
            }
            sb.append(cbuf, start, i - start);
            for (start = i + 1; start <= end && WstxInputData.isSpaceChar(cbuf[start]); ++start) {}
        }
        return sb.toString();
    }
    
    @Override
    public void validateDefault(final InputProblemReporter rep, final boolean normalize) throws XMLStreamException {
        final String defValue = this.mDefValue.getValue();
        final int len = defValue.length();
        StringBuilder sb = null;
        int count = 0;
        int start = 0;
    Label_0252:
        while (start < len) {
            for (char c = defValue.charAt(start); WstxInputData.isSpaceChar(c); c = defValue.charAt(start)) {
                if (++start >= len) {
                    break Label_0252;
                }
            }
            int i = start + 1;
            while (true) {
                while (++i < len) {
                    final char c = defValue.charAt(i);
                    if (WstxInputData.isSpaceChar(c)) {
                        ++count;
                        final String token = defValue.substring(start, i);
                        final int illegalIx = WstxInputData.findIllegalNmtokenChar(token, this.mCfgNsAware, this.mCfgXml11);
                        if (illegalIx >= 0) {
                            this.reportValidationProblem(rep, "Invalid default value '" + defValue + "'; character #" + illegalIx + " (" + WstxInputData.getCharDesc(defValue.charAt(illegalIx)) + ") not a valid NMTOKENS character");
                        }
                        if (normalize) {
                            if (sb == null) {
                                sb = new StringBuilder(i - start + 32);
                            }
                            else {
                                sb.append(' ');
                            }
                            sb.append(token);
                        }
                        start = i + 1;
                        continue Label_0252;
                    }
                }
                continue;
            }
        }
        if (count == 0) {
            this.reportValidationProblem(rep, "Invalid default value '" + defValue + "'; empty String is not a valid NMTOKENS value");
            return;
        }
        if (normalize) {
            this.mDefValue.setValue(sb.toString());
        }
    }
}
