// 
// Decompiled by Procyon v0.5.36
// 

package com.ctc.wstx.compat;

import java.util.logging.Logger;
import javax.xml.namespace.QName;

public final class QNameCreator
{
    private static final Helper _helper;
    
    public static QName create(final String uri, final String localName, final String prefix) {
        if (QNameCreator._helper == null) {
            return new QName(uri, localName);
        }
        return QNameCreator._helper.create(uri, localName, prefix);
    }
    
    static {
        Helper h = null;
        try {
            final Helper h2 = new Helper();
            h2.create("elem", "http://dummy", "ns");
            h = h2;
        }
        catch (Throwable t) {
            final String msg = "Could not construct QNameCreator.Helper; assume 3-arg QName constructor not available and use 2-arg method instead. Problem: " + t.getMessage();
            try {
                Logger.getLogger("com.ctc.wstx.compat.QNameCreator").warning(msg);
            }
            catch (Throwable t2) {
                System.err.println("ERROR: failed to log error using Logger (problem " + t.getMessage() + "), original problem: " + msg);
            }
        }
        _helper = h;
    }
    
    private static final class Helper
    {
        public Helper() {
        }
        
        public QName create(final String localName, final String nsURI, final String prefix) {
            return new QName(localName, nsURI, prefix);
        }
    }
}
