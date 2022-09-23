// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.xml.bind.unmarshaller;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public class Messages
{
    public static final String UNEXPECTED_ENTER_ELEMENT = "ContentHandlerEx.UnexpectedEnterElement";
    public static final String UNEXPECTED_LEAVE_ELEMENT = "ContentHandlerEx.UnexpectedLeaveElement";
    public static final String UNEXPECTED_ENTER_ATTRIBUTE = "ContentHandlerEx.UnexpectedEnterAttribute";
    public static final String UNEXPECTED_LEAVE_ATTRIBUTE = "ContentHandlerEx.UnexpectedLeaveAttribute";
    public static final String UNEXPECTED_TEXT = "ContentHandlerEx.UnexpectedText";
    public static final String UNEXPECTED_LEAVE_CHILD = "ContentHandlerEx.UnexpectedLeaveChild";
    public static final String UNEXPECTED_ROOT_ELEMENT = "SAXUnmarshallerHandlerImpl.UnexpectedRootElement";
    public static final String UNEXPECTED_ROOT_ELEMENT2 = "SAXUnmarshallerHandlerImpl.UnexpectedRootElement2";
    public static final String UNDEFINED_PREFIX = "Util.UndefinedPrefix";
    public static final String NULL_READER = "Unmarshaller.NullReader";
    public static final String ILLEGAL_READER_STATE = "Unmarshaller.IllegalReaderState";
    
    public static String format(final String property) {
        return format(property, null);
    }
    
    public static String format(final String property, final Object arg1) {
        return format(property, new Object[] { arg1 });
    }
    
    public static String format(final String property, final Object arg1, final Object arg2) {
        return format(property, new Object[] { arg1, arg2 });
    }
    
    public static String format(final String property, final Object arg1, final Object arg2, final Object arg3) {
        return format(property, new Object[] { arg1, arg2, arg3 });
    }
    
    public static String format(final String property, final Object[] args) {
        final String text = ResourceBundle.getBundle(Messages.class.getName()).getString(property);
        return MessageFormat.format(text, args);
    }
}
