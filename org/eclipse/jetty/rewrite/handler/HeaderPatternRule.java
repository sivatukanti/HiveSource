// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.rewrite.handler;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;

public class HeaderPatternRule extends PatternRule
{
    private String _name;
    private String _value;
    private boolean _add;
    
    public HeaderPatternRule() {
        this._add = false;
        this._handling = false;
        this._terminating = false;
    }
    
    public void setName(final String name) {
        this._name = name;
    }
    
    public void setValue(final String value) {
        this._value = value;
    }
    
    public void setAdd(final boolean add) {
        this._add = add;
    }
    
    public String apply(final String target, final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        if (this._add) {
            response.addHeader(this._name, this._value);
        }
        else {
            response.setHeader(this._name, this._value);
        }
        return target;
    }
    
    public String getName() {
        return this._name;
    }
    
    public String getValue() {
        return this._value;
    }
    
    public boolean isAdd() {
        return this._add;
    }
    
    @Override
    public String toString() {
        return super.toString() + "[" + this._name + "," + this._value + "]";
    }
}
