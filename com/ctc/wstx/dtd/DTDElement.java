// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.dtd;

import com.ctc.wstx.cfg.ErrorConsts;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import com.ctc.wstx.util.WordResolver;
import javax.xml.stream.XMLStreamException;
import com.ctc.wstx.sr.InputProblemReporter;
import com.ctc.wstx.util.ExceptionUtil;
import com.ctc.wstx.api.ReaderConfig;
import java.util.ArrayList;
import java.util.HashMap;
import javax.xml.stream.Location;
import com.ctc.wstx.util.PrefixedName;

public final class DTDElement
{
    final PrefixedName mName;
    final Location mLocation;
    StructValidator mValidator;
    int mAllowedContent;
    final boolean mNsAware;
    final boolean mXml11;
    HashMap<PrefixedName, DTDAttribute> mAttrMap;
    ArrayList<DTDAttribute> mSpecAttrList;
    boolean mAnyFixed;
    boolean mAnyDefaults;
    boolean mValidateAttrs;
    DTDAttribute mIdAttr;
    DTDAttribute mNotationAttr;
    HashMap<String, DTDAttribute> mNsDefaults;
    
    private DTDElement(final Location loc, final PrefixedName name, final StructValidator val, final int allowedContent, final boolean nsAware, final boolean xml11) {
        this.mAttrMap = null;
        this.mSpecAttrList = null;
        this.mAnyFixed = false;
        this.mAnyDefaults = false;
        this.mValidateAttrs = false;
        this.mNsDefaults = null;
        this.mName = name;
        this.mLocation = loc;
        this.mValidator = val;
        this.mAllowedContent = allowedContent;
        this.mNsAware = nsAware;
        this.mXml11 = xml11;
    }
    
    public static DTDElement createDefined(final ReaderConfig cfg, final Location loc, final PrefixedName name, final StructValidator val, final int allowedContent) {
        if (allowedContent == 5) {
            ExceptionUtil.throwInternal("trying to use XMLValidator.CONTENT_ALLOW_UNDEFINED via createDefined()");
        }
        return new DTDElement(loc, name, val, allowedContent, cfg.willSupportNamespaces(), cfg.isXml11());
    }
    
    public static DTDElement createPlaceholder(final ReaderConfig cfg, final Location loc, final PrefixedName name) {
        return new DTDElement(loc, name, null, 5, cfg.willSupportNamespaces(), cfg.isXml11());
    }
    
    public DTDElement define(final Location loc, final StructValidator val, final int allowedContent) {
        this.verifyUndefined();
        if (allowedContent == 5) {
            ExceptionUtil.throwInternal("trying to use CONTENT_ALLOW_UNDEFINED via define()");
        }
        final DTDElement elem = new DTDElement(loc, this.mName, val, allowedContent, this.mNsAware, this.mXml11);
        elem.mAttrMap = this.mAttrMap;
        elem.mSpecAttrList = this.mSpecAttrList;
        elem.mAnyFixed = this.mAnyFixed;
        elem.mValidateAttrs = this.mValidateAttrs;
        elem.mAnyDefaults = this.mAnyDefaults;
        elem.mIdAttr = this.mIdAttr;
        elem.mNotationAttr = this.mNotationAttr;
        elem.mNsDefaults = this.mNsDefaults;
        return elem;
    }
    
    public void defineFrom(final InputProblemReporter rep, final DTDElement definedElem, final boolean fullyValidate) throws XMLStreamException {
        if (fullyValidate) {
            this.verifyUndefined();
        }
        this.mValidator = definedElem.mValidator;
        this.mAllowedContent = definedElem.mAllowedContent;
        this.mergeMissingAttributesFrom(rep, definedElem, fullyValidate);
    }
    
    private void verifyUndefined() {
        if (this.mAllowedContent != 5) {
            ExceptionUtil.throwInternal("redefining defined element spec");
        }
    }
    
    public DTDAttribute addAttribute(final InputProblemReporter rep, final PrefixedName attrName, final int valueType, final DefaultAttrValue defValue, final WordResolver enumValues, final boolean fullyValidate) throws XMLStreamException {
        HashMap<PrefixedName, DTDAttribute> m = this.mAttrMap;
        if (m == null) {
            m = (this.mAttrMap = new HashMap<PrefixedName, DTDAttribute>());
        }
        final List<DTDAttribute> specList = defValue.isSpecial() ? this.getSpecialList() : null;
        final int specIndex = (specList == null) ? -1 : specList.size();
        DTDAttribute attr = null;
        switch (valueType) {
            case 0: {
                attr = new DTDCdataAttr(attrName, defValue, specIndex, this.mNsAware, this.mXml11);
                break;
            }
            case 1: {
                attr = new DTDEnumAttr(attrName, defValue, specIndex, this.mNsAware, this.mXml11, enumValues);
                break;
            }
            case 2: {
                attr = new DTDIdAttr(attrName, defValue, specIndex, this.mNsAware, this.mXml11);
                break;
            }
            case 3: {
                attr = new DTDIdRefAttr(attrName, defValue, specIndex, this.mNsAware, this.mXml11);
                break;
            }
            case 4: {
                attr = new DTDIdRefsAttr(attrName, defValue, specIndex, this.mNsAware, this.mXml11);
                break;
            }
            case 5: {
                attr = new DTDEntityAttr(attrName, defValue, specIndex, this.mNsAware, this.mXml11);
                break;
            }
            case 6: {
                attr = new DTDEntitiesAttr(attrName, defValue, specIndex, this.mNsAware, this.mXml11);
                break;
            }
            case 7: {
                attr = new DTDNotationAttr(attrName, defValue, specIndex, this.mNsAware, this.mXml11, enumValues);
                break;
            }
            case 8: {
                attr = new DTDNmTokenAttr(attrName, defValue, specIndex, this.mNsAware, this.mXml11);
                break;
            }
            case 9: {
                attr = new DTDNmTokensAttr(attrName, defValue, specIndex, this.mNsAware, this.mXml11);
                break;
            }
            default: {
                ExceptionUtil.throwGenericInternal();
                attr = null;
                break;
            }
        }
        final DTDAttribute old = this.doAddAttribute(m, rep, attr, specList, fullyValidate);
        return (old == null) ? attr : null;
    }
    
    public DTDAttribute addNsDefault(final InputProblemReporter rep, final PrefixedName attrName, final int valueType, final DefaultAttrValue defValue, final boolean fullyValidate) throws XMLStreamException {
        DTDAttribute nsAttr = null;
        switch (valueType) {
            case 0: {
                nsAttr = new DTDCdataAttr(attrName, defValue, -1, this.mNsAware, this.mXml11);
                break;
            }
            default: {
                nsAttr = new DTDNmTokenAttr(attrName, defValue, -1, this.mNsAware, this.mXml11);
                break;
            }
        }
        String prefix = attrName.getPrefix();
        if (prefix == null || prefix.length() == 0) {
            prefix = "";
        }
        else {
            prefix = attrName.getLocalName();
        }
        if (this.mNsDefaults == null) {
            this.mNsDefaults = new HashMap<String, DTDAttribute>();
        }
        else if (this.mNsDefaults.containsKey(prefix)) {
            return null;
        }
        this.mNsDefaults.put(prefix, nsAttr);
        return nsAttr;
    }
    
    public void mergeMissingAttributesFrom(final InputProblemReporter rep, final DTDElement other, final boolean fullyValidate) throws XMLStreamException {
        final Map<PrefixedName, DTDAttribute> otherMap = other.getAttributes();
        HashMap<PrefixedName, DTDAttribute> m = this.mAttrMap;
        if (m == null) {
            m = (this.mAttrMap = new HashMap<PrefixedName, DTDAttribute>());
        }
        if (otherMap != null && otherMap.size() > 0) {
            for (final Map.Entry<PrefixedName, DTDAttribute> me : otherMap.entrySet()) {
                final PrefixedName key = me.getKey();
                if (!m.containsKey(key)) {
                    DTDAttribute newAttr = me.getValue();
                    List<DTDAttribute> specList;
                    if (newAttr.isSpecial()) {
                        specList = this.getSpecialList();
                        newAttr = newAttr.cloneWith(specList.size());
                    }
                    else {
                        specList = null;
                    }
                    this.doAddAttribute(m, rep, newAttr, specList, fullyValidate);
                }
            }
        }
        final HashMap<String, DTDAttribute> otherNs = other.mNsDefaults;
        if (otherNs != null) {
            if (this.mNsDefaults == null) {
                this.mNsDefaults = new HashMap<String, DTDAttribute>();
            }
            for (final Map.Entry<String, DTDAttribute> en : otherNs.entrySet()) {
                final String prefix = en.getKey();
                if (!this.mNsDefaults.containsKey(prefix)) {
                    this.mNsDefaults.put(prefix, en.getValue());
                }
            }
        }
    }
    
    private DTDAttribute doAddAttribute(final Map<PrefixedName, DTDAttribute> attrMap, final InputProblemReporter rep, final DTDAttribute attr, final List<DTDAttribute> specList, final boolean fullyValidate) throws XMLStreamException {
        final PrefixedName attrName = attr.getName();
        final DTDAttribute old = attrMap.get(attrName);
        if (old != null) {
            rep.reportProblem(null, ErrorConsts.WT_ATTR_DECL, ErrorConsts.W_DTD_DUP_ATTR, attrName, this.mName);
            return old;
        }
        switch (attr.getValueType()) {
            case 2: {
                if (fullyValidate && this.mIdAttr != null) {
                    rep.throwParseError("Invalid id attribute \"{0}\" for element <{1}>: already had id attribute \"" + this.mIdAttr.getName() + "\"", attrName, this.mName);
                }
                this.mIdAttr = attr;
                break;
            }
            case 7: {
                if (fullyValidate && this.mNotationAttr != null) {
                    rep.throwParseError("Invalid notation attribute '" + attrName + "' for element <" + this.mName + ">: already had notation attribute '" + this.mNotationAttr.getName() + "'");
                }
                this.mNotationAttr = attr;
                break;
            }
        }
        attrMap.put(attrName, attr);
        if (specList != null) {
            specList.add(attr);
        }
        if (!this.mAnyFixed) {
            this.mAnyFixed = attr.isFixed();
        }
        if (!this.mValidateAttrs) {
            this.mValidateAttrs = attr.needsValidation();
        }
        if (!this.mAnyDefaults) {
            this.mAnyDefaults = attr.hasDefaultValue();
        }
        return null;
    }
    
    public PrefixedName getName() {
        return this.mName;
    }
    
    @Override
    public String toString() {
        return this.mName.toString();
    }
    
    public String getDisplayName() {
        return this.mName.toString();
    }
    
    public Location getLocation() {
        return this.mLocation;
    }
    
    public boolean isDefined() {
        return this.mAllowedContent != 5;
    }
    
    public int getAllowedContent() {
        return this.mAllowedContent;
    }
    
    public int getAllowedContentIfSpace() {
        final int vld = this.mAllowedContent;
        return (vld <= 1) ? 2 : 4;
    }
    
    public HashMap<PrefixedName, DTDAttribute> getAttributes() {
        return this.mAttrMap;
    }
    
    public int getSpecialCount() {
        return (this.mSpecAttrList == null) ? 0 : this.mSpecAttrList.size();
    }
    
    public List<DTDAttribute> getSpecialAttrs() {
        return this.mSpecAttrList;
    }
    
    public boolean attrsNeedValidation() {
        return this.mValidateAttrs;
    }
    
    public boolean hasFixedAttrs() {
        return this.mAnyFixed;
    }
    
    public boolean hasAttrDefaultValues() {
        return this.mAnyDefaults;
    }
    
    public DTDAttribute getIdAttribute() {
        return this.mIdAttr;
    }
    
    public DTDAttribute getNotationAttribute() {
        return this.mNotationAttr;
    }
    
    public boolean hasNsDefaults() {
        return this.mNsDefaults != null;
    }
    
    public StructValidator getValidator() {
        return (this.mValidator == null) ? null : this.mValidator.newInstance();
    }
    
    protected HashMap<String, DTDAttribute> getNsDefaults() {
        return this.mNsDefaults;
    }
    
    private List<DTDAttribute> getSpecialList() {
        ArrayList<DTDAttribute> l = this.mSpecAttrList;
        if (l == null) {
            l = (this.mSpecAttrList = new ArrayList<DTDAttribute>());
        }
        return l;
    }
}
