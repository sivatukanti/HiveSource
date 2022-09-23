// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.dtd;

import com.ctc.wstx.util.ExceptionUtil;
import com.ctc.wstx.util.ElementIdMap;
import java.util.List;
import javax.xml.stream.XMLStreamException;
import com.ctc.wstx.util.DataUtil;
import com.ctc.wstx.ent.EntityDecl;
import com.ctc.wstx.util.PrefixedName;
import java.util.Map;
import org.codehaus.stax2.validation.ValidationContext;
import java.util.BitSet;

public class DTDTypingNonValidator extends DTDValidatorBase
{
    protected boolean mHasAttrDefaults;
    protected BitSet mCurrDefaultAttrs;
    protected boolean mHasNormalizableAttrs;
    BitSet mTmpDefaultAttrs;
    
    public DTDTypingNonValidator(final DTDSubset schema, final ValidationContext ctxt, final boolean hasNsDefaults, final Map<PrefixedName, DTDElement> elemSpecs, final Map<String, EntityDecl> genEntities) {
        super(schema, ctxt, hasNsDefaults, elemSpecs, genEntities);
        this.mHasAttrDefaults = false;
        this.mCurrDefaultAttrs = null;
        this.mHasNormalizableAttrs = false;
    }
    
    @Override
    public final boolean reallyValidating() {
        return false;
    }
    
    @Override
    public void setAttrValueNormalization(final boolean state) {
    }
    
    @Override
    public void validateElementStart(final String localName, final String uri, final String prefix) throws XMLStreamException {
        this.mTmpKey.reset(prefix, localName);
        final DTDElement elem = this.mElemSpecs.get(this.mTmpKey);
        final int elemCount = this.mElemCount++;
        if (elemCount >= this.mElems.length) {
            this.mElems = (DTDElement[])DataUtil.growArrayBy50Pct(this.mElems);
        }
        this.mElems[elemCount] = (this.mCurrElem = elem);
        this.mAttrCount = 0;
        this.mIdAttrIndex = -2;
        if (elem == null) {
            this.mCurrAttrDefs = DTDTypingNonValidator.NO_ATTRS;
            this.mHasAttrDefaults = false;
            this.mCurrDefaultAttrs = null;
            this.mHasNormalizableAttrs = false;
            return;
        }
        this.mCurrAttrDefs = elem.getAttributes();
        if (this.mCurrAttrDefs == null) {
            this.mCurrAttrDefs = DTDTypingNonValidator.NO_ATTRS;
            this.mHasAttrDefaults = false;
            this.mCurrDefaultAttrs = null;
            this.mHasNormalizableAttrs = false;
            return;
        }
        this.mHasNormalizableAttrs = (this.mNormAttrs || elem.attrsNeedValidation());
        this.mHasAttrDefaults = elem.hasAttrDefaultValues();
        if (this.mHasAttrDefaults) {
            final int specCount = elem.getSpecialCount();
            BitSet bs = this.mTmpDefaultAttrs;
            if (bs == null) {
                bs = (this.mTmpDefaultAttrs = new BitSet(specCount));
            }
            else {
                bs.clear();
            }
            this.mCurrDefaultAttrs = bs;
        }
        else {
            this.mCurrDefaultAttrs = null;
        }
    }
    
    @Override
    public String validateAttribute(final String localName, final String uri, final String prefix, final String value) throws XMLStreamException {
        final DTDAttribute attr = this.mCurrAttrDefs.get(this.mTmpKey.reset(prefix, localName));
        final int index = this.mAttrCount++;
        if (index >= this.mAttrSpecs.length) {
            this.mAttrSpecs = (DTDAttribute[])DataUtil.growArrayBy50Pct(this.mAttrSpecs);
        }
        if ((this.mAttrSpecs[index] = attr) != null) {
            if (this.mHasAttrDefaults) {
                final int specIndex = attr.getSpecialIndex();
                if (specIndex >= 0) {
                    this.mCurrDefaultAttrs.set(specIndex);
                }
            }
            if (this.mHasNormalizableAttrs) {}
        }
        return null;
    }
    
    @Override
    public String validateAttribute(final String localName, final String uri, final String prefix, final char[] valueChars, final int valueStart, final int valueEnd) throws XMLStreamException {
        final DTDAttribute attr = this.mCurrAttrDefs.get(this.mTmpKey.reset(prefix, localName));
        final int index = this.mAttrCount++;
        if (index >= this.mAttrSpecs.length) {
            this.mAttrSpecs = (DTDAttribute[])DataUtil.growArrayBy50Pct(this.mAttrSpecs);
        }
        if ((this.mAttrSpecs[index] = attr) != null) {
            if (this.mHasAttrDefaults) {
                final int specIndex = attr.getSpecialIndex();
                if (specIndex >= 0) {
                    this.mCurrDefaultAttrs.set(specIndex);
                }
            }
            if (this.mHasNormalizableAttrs) {
                return attr.normalize(this, valueChars, valueStart, valueEnd);
            }
        }
        return null;
    }
    
    @Override
    public int validateElementAndAttributes() throws XMLStreamException {
        final DTDElement elem = this.mCurrElem;
        if (this.mHasAttrDefaults) {
            final BitSet specBits = this.mCurrDefaultAttrs;
            for (int specCount = elem.getSpecialCount(), ix = specBits.nextClearBit(0); ix < specCount; ix = specBits.nextClearBit(ix + 1)) {
                final List<DTDAttribute> specAttrs = elem.getSpecialAttrs();
                final DTDAttribute attr = specAttrs.get(ix);
                if (attr.hasDefaultValue()) {
                    this.doAddDefaultValue(attr);
                }
            }
        }
        return (elem == null) ? 4 : elem.getAllowedContentIfSpace();
    }
    
    @Override
    public int validateElementEnd(final String localName, final String uri, final String prefix) throws XMLStreamException {
        final int mElemCount = this.mElemCount - 1;
        this.mElemCount = mElemCount;
        final int ix = mElemCount;
        this.mElems[ix] = null;
        if (ix < 1) {
            return 4;
        }
        final DTDElement elem = this.mElems[ix - 1];
        return (elem == null) ? 4 : this.mElems[ix - 1].getAllowedContentIfSpace();
    }
    
    @Override
    public void validationCompleted(final boolean eod) {
    }
    
    @Override
    protected ElementIdMap getIdMap() {
        ExceptionUtil.throwGenericInternal();
        return null;
    }
}
