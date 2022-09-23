// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.httpclient;

public class Header extends NameValuePair
{
    private boolean isAutogenerated;
    
    public Header() {
        this(null, null);
    }
    
    public Header(final String name, final String value) {
        super(name, value);
        this.isAutogenerated = false;
    }
    
    public Header(final String name, final String value, final boolean isAutogenerated) {
        super(name, value);
        this.isAutogenerated = false;
        this.isAutogenerated = isAutogenerated;
    }
    
    public String toExternalForm() {
        return ((null == this.getName()) ? "" : this.getName()) + ": " + ((null == this.getValue()) ? "" : this.getValue()) + "\r\n";
    }
    
    public String toString() {
        return this.toExternalForm();
    }
    
    public HeaderElement[] getValues() throws HttpException {
        return HeaderElement.parse(this.getValue());
    }
    
    public HeaderElement[] getElements() {
        return HeaderElement.parseElements(this.getValue());
    }
    
    public boolean isAutogenerated() {
        return this.isAutogenerated;
    }
}
