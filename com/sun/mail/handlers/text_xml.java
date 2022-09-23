// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.mail.handlers;

import javax.activation.ActivationDataFlavor;

public class text_xml extends text_plain
{
    private static ActivationDataFlavor myDF;
    
    protected ActivationDataFlavor getDF() {
        return text_xml.myDF;
    }
    
    static {
        text_xml.myDF = new ActivationDataFlavor(String.class, "text/xml", "XML String");
    }
}
