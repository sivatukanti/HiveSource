// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.httpclient.methods;

import org.apache.commons.httpclient.HttpMethodBase;

public class DeleteMethod extends HttpMethodBase
{
    public DeleteMethod() {
    }
    
    public DeleteMethod(final String uri) {
        super(uri);
    }
    
    public String getName() {
        return "DELETE";
    }
}
