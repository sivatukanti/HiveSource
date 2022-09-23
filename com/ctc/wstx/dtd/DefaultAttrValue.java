// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.dtd;

import org.codehaus.stax2.validation.XMLValidationProblem;
import java.text.MessageFormat;
import com.ctc.wstx.cfg.ErrorConsts;
import javax.xml.stream.XMLStreamException;
import org.codehaus.stax2.validation.XMLValidator;
import org.codehaus.stax2.validation.ValidationContext;
import javax.xml.stream.Location;

public final class DefaultAttrValue
{
    public static final int DEF_DEFAULT = 1;
    public static final int DEF_IMPLIED = 2;
    public static final int DEF_REQUIRED = 3;
    public static final int DEF_FIXED = 4;
    static final DefaultAttrValue sImplied;
    static final DefaultAttrValue sRequired;
    final int mDefValueType;
    private String mValue;
    private UndeclaredEntity mUndeclaredEntity;
    
    private DefaultAttrValue(final int defValueType) {
        this.mValue = null;
        this.mUndeclaredEntity = null;
        this.mDefValueType = defValueType;
    }
    
    public static DefaultAttrValue constructImplied() {
        return DefaultAttrValue.sImplied;
    }
    
    public static DefaultAttrValue constructRequired() {
        return DefaultAttrValue.sRequired;
    }
    
    public static DefaultAttrValue constructFixed() {
        return new DefaultAttrValue(4);
    }
    
    public static DefaultAttrValue constructOptional() {
        return new DefaultAttrValue(1);
    }
    
    public void setValue(final String v) {
        this.mValue = v;
    }
    
    public void addUndeclaredPE(final String name, final Location loc) {
        this.addUndeclaredEntity(name, loc, true);
    }
    
    public void addUndeclaredGE(final String name, final Location loc) {
        this.addUndeclaredEntity(name, loc, false);
    }
    
    public void reportUndeclared(final ValidationContext ctxt, final XMLValidator dtd) throws XMLStreamException {
        this.mUndeclaredEntity.reportUndeclared(ctxt, dtd);
    }
    
    public boolean hasUndeclaredEntities() {
        return this.mUndeclaredEntity != null;
    }
    
    public String getValue() {
        return this.mValue;
    }
    
    public String getValueIfOk() {
        return (this.mUndeclaredEntity == null) ? this.mValue : null;
    }
    
    public boolean isRequired() {
        return this == DefaultAttrValue.sRequired;
    }
    
    public boolean isFixed() {
        return this.mDefValueType == 4;
    }
    
    public boolean hasDefaultValue() {
        return this.mDefValueType == 1 || this.mDefValueType == 4;
    }
    
    public boolean isSpecial() {
        return this != DefaultAttrValue.sImplied;
    }
    
    private void addUndeclaredEntity(final String name, final Location loc, final boolean isPe) {
        if (this.mUndeclaredEntity == null) {
            this.mUndeclaredEntity = new UndeclaredEntity(name, loc, isPe);
        }
    }
    
    static {
        sImplied = new DefaultAttrValue(2);
        sRequired = new DefaultAttrValue(3);
    }
    
    static final class UndeclaredEntity
    {
        final String mName;
        final boolean mIsPe;
        final Location mLocation;
        
        UndeclaredEntity(final String name, final Location loc, final boolean isPe) {
            this.mName = name;
            this.mIsPe = isPe;
            this.mLocation = loc;
        }
        
        public void reportUndeclared(final ValidationContext ctxt, final XMLValidator dtd) throws XMLStreamException {
            final String msg = MessageFormat.format(ErrorConsts.ERR_DTD_UNDECLARED_ENTITY, this.mIsPe ? "parsed" : "general", this.mName);
            final XMLValidationProblem prob = new XMLValidationProblem(this.mLocation, msg, 3);
            prob.setReporter(dtd);
            ctxt.reportProblem(prob);
        }
    }
}
