// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.plus.jaas.callback;

import java.util.List;
import javax.security.auth.callback.Callback;

public class RequestParameterCallback implements Callback
{
    private String _paramName;
    private List<?> _paramValues;
    
    public void setParameterName(final String name) {
        this._paramName = name;
    }
    
    public String getParameterName() {
        return this._paramName;
    }
    
    public void setParameterValues(final List<?> values) {
        this._paramValues = values;
    }
    
    public List<?> getParameterValues() {
        return this._paramValues;
    }
}
