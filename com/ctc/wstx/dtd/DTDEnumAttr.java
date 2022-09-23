// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.dtd;

import com.ctc.wstx.sr.InputProblemReporter;
import javax.xml.stream.XMLStreamException;
import com.ctc.wstx.util.PrefixedName;
import com.ctc.wstx.util.WordResolver;

public final class DTDEnumAttr extends DTDAttribute
{
    final WordResolver mEnumValues;
    
    public DTDEnumAttr(final PrefixedName name, final DefaultAttrValue defValue, final int specIndex, final boolean nsAware, final boolean xml11, final WordResolver enumValues) {
        super(name, defValue, specIndex, nsAware, xml11);
        this.mEnumValues = enumValues;
    }
    
    @Override
    public DTDAttribute cloneWith(final int specIndex) {
        return new DTDEnumAttr(this.mName, this.mDefValue, specIndex, this.mCfgNsAware, this.mCfgXml11, this.mEnumValues);
    }
    
    @Override
    public int getValueType() {
        return 1;
    }
    
    @Override
    public String validate(final DTDValidatorBase v, final char[] cbuf, final int start, final int end, final boolean normalize) throws XMLStreamException {
        final String ok = this.validateEnumValue(cbuf, start, end, normalize, this.mEnumValues);
        if (ok == null) {
            final String val = new String(cbuf, start, end - start);
            return this.reportValidationProblem(v, "Invalid enumerated value '" + val + "': has to be one of (" + this.mEnumValues + ")");
        }
        return ok;
    }
    
    @Override
    public void validateDefault(final InputProblemReporter rep, final boolean normalize) throws XMLStreamException {
        final String def = this.validateDefaultNmToken(rep, normalize);
        final String shared = this.mEnumValues.find(def);
        if (shared == null) {
            this.reportValidationProblem(rep, "Invalid default value '" + def + "': has to be one of (" + this.mEnumValues + ")");
            return;
        }
        if (normalize) {
            this.mDefValue.setValue(shared);
        }
    }
}
