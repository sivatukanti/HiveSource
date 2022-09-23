// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.xml.txw2.output;

import com.sun.xml.txw2.TypedXmlWriter;
import javax.xml.transform.Result;

public class TXWResult implements Result
{
    private String systemId;
    private TypedXmlWriter writer;
    
    public TXWResult(final TypedXmlWriter writer) {
        this.writer = writer;
    }
    
    public TypedXmlWriter getWriter() {
        return this.writer;
    }
    
    public void setWriter(final TypedXmlWriter writer) {
        this.writer = writer;
    }
    
    public String getSystemId() {
        return this.systemId;
    }
    
    public void setSystemId(final String systemId) {
        this.systemId = systemId;
    }
}
