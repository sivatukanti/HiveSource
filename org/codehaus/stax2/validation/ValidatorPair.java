// 
// Decompiled by Procyon v0.5.36
// 

package org.codehaus.stax2.validation;

import javax.xml.stream.XMLStreamException;

public class ValidatorPair extends XMLValidator
{
    public static final String ATTR_TYPE_DEFAULT = "CDATA";
    protected XMLValidator mFirst;
    protected XMLValidator mSecond;
    
    public ValidatorPair(final XMLValidator mFirst, final XMLValidator mSecond) {
        this.mFirst = mFirst;
        this.mSecond = mSecond;
    }
    
    @Override
    public XMLValidationSchema getSchema() {
        return null;
    }
    
    @Override
    public void validateElementStart(final String s, final String s2, final String s3) throws XMLStreamException {
        this.mFirst.validateElementStart(s, s2, s3);
        this.mSecond.validateElementStart(s, s2, s3);
    }
    
    @Override
    public String validateAttribute(final String s, final String s2, final String s3, String s4) throws XMLStreamException {
        final String validateAttribute = this.mFirst.validateAttribute(s, s2, s3, s4);
        if (validateAttribute != null) {
            s4 = validateAttribute;
        }
        return this.mSecond.validateAttribute(s, s2, s3, s4);
    }
    
    @Override
    public String validateAttribute(final String s, final String s2, final String s3, final char[] array, final int n, final int n2) throws XMLStreamException {
        final String validateAttribute = this.mFirst.validateAttribute(s, s2, s3, array, n, n2);
        if (validateAttribute != null) {
            return this.mSecond.validateAttribute(s, s2, s3, validateAttribute);
        }
        return this.mSecond.validateAttribute(s, s2, s3, array, n, n2);
    }
    
    @Override
    public int validateElementAndAttributes() throws XMLStreamException {
        final int validateElementAndAttributes = this.mFirst.validateElementAndAttributes();
        final int validateElementAndAttributes2 = this.mSecond.validateElementAndAttributes();
        return (validateElementAndAttributes < validateElementAndAttributes2) ? validateElementAndAttributes : validateElementAndAttributes2;
    }
    
    @Override
    public int validateElementEnd(final String s, final String s2, final String s3) throws XMLStreamException {
        final int validateElementEnd = this.mFirst.validateElementEnd(s, s2, s3);
        final int validateElementEnd2 = this.mSecond.validateElementEnd(s, s2, s3);
        return (validateElementEnd < validateElementEnd2) ? validateElementEnd : validateElementEnd2;
    }
    
    @Override
    public void validateText(final String s, final boolean b) throws XMLStreamException {
        this.mFirst.validateText(s, b);
        this.mSecond.validateText(s, b);
    }
    
    @Override
    public void validateText(final char[] array, final int n, final int n2, final boolean b) throws XMLStreamException {
        this.mFirst.validateText(array, n, n2, b);
        this.mSecond.validateText(array, n, n2, b);
    }
    
    @Override
    public void validationCompleted(final boolean b) throws XMLStreamException {
        this.mFirst.validationCompleted(b);
        this.mSecond.validationCompleted(b);
    }
    
    @Override
    public String getAttributeType(final int n) {
        final String attributeType = this.mFirst.getAttributeType(n);
        if (attributeType == null || attributeType.length() == 0 || attributeType.equals("CDATA")) {
            final String attributeType2 = this.mSecond.getAttributeType(n);
            if (attributeType2 != null && attributeType2.length() > 0) {
                return attributeType2;
            }
        }
        return attributeType;
    }
    
    @Override
    public int getIdAttrIndex() {
        final int idAttrIndex = this.mFirst.getIdAttrIndex();
        if (idAttrIndex < 0) {
            return this.mSecond.getIdAttrIndex();
        }
        return idAttrIndex;
    }
    
    @Override
    public int getNotationAttrIndex() {
        final int notationAttrIndex = this.mFirst.getNotationAttrIndex();
        if (notationAttrIndex < 0) {
            return this.mSecond.getNotationAttrIndex();
        }
        return notationAttrIndex;
    }
    
    public static boolean removeValidator(final XMLValidator xmlValidator, final XMLValidationSchema xmlValidationSchema, final XMLValidator[] array) {
        if (xmlValidator instanceof ValidatorPair) {
            return ((ValidatorPair)xmlValidator).doRemoveValidator(xmlValidationSchema, array);
        }
        if (xmlValidator.getSchema() == xmlValidationSchema) {
            array[0] = xmlValidator;
            array[1] = null;
            return true;
        }
        return false;
    }
    
    public static boolean removeValidator(final XMLValidator xmlValidator, final XMLValidator xmlValidator2, final XMLValidator[] array) {
        if (xmlValidator == xmlValidator2) {
            array[0] = xmlValidator;
            array[1] = null;
            return true;
        }
        return xmlValidator instanceof ValidatorPair && ((ValidatorPair)xmlValidator).doRemoveValidator(xmlValidator2, array);
    }
    
    private boolean doRemoveValidator(final XMLValidationSchema xmlValidationSchema, final XMLValidator[] array) {
        if (removeValidator(this.mFirst, xmlValidationSchema, array)) {
            final XMLValidator mFirst = array[1];
            if (mFirst == null) {
                array[1] = this.mSecond;
            }
            else {
                this.mFirst = mFirst;
                array[1] = this;
            }
            return true;
        }
        if (removeValidator(this.mSecond, xmlValidationSchema, array)) {
            final XMLValidator mSecond = array[1];
            if (mSecond == null) {
                array[1] = this.mFirst;
            }
            else {
                this.mSecond = mSecond;
                array[1] = this;
            }
            return true;
        }
        return false;
    }
    
    private boolean doRemoveValidator(final XMLValidator xmlValidator, final XMLValidator[] array) {
        if (removeValidator(this.mFirst, xmlValidator, array)) {
            final XMLValidator mFirst = array[1];
            if (mFirst == null) {
                array[1] = this.mSecond;
            }
            else {
                this.mFirst = mFirst;
                array[1] = this;
            }
            return true;
        }
        if (removeValidator(this.mSecond, xmlValidator, array)) {
            final XMLValidator mSecond = array[1];
            if (mSecond == null) {
                array[1] = this.mFirst;
            }
            else {
                this.mSecond = mSecond;
                array[1] = this;
            }
            return true;
        }
        return false;
    }
}
