// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.rewrite.handler;

import java.io.IOException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;

public class CookiePatternRule extends PatternRule
{
    private String _name;
    private String _value;
    
    public CookiePatternRule() {
        this._handling = false;
        this._terminating = false;
    }
    
    public void setName(final String name) {
        this._name = name;
    }
    
    public void setValue(final String value) {
        this._value = value;
    }
    
    public String apply(final String target, final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        response.addCookie(new Cookie(this._name, this._value));
        return target;
    }
    
    @Override
    public String toString() {
        return super.toString() + "[" + this._name + "," + this._value + "]";
    }
}
