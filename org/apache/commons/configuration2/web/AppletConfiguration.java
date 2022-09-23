// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.configuration2.web;

import java.util.Arrays;
import java.util.Iterator;
import java.applet.Applet;

public class AppletConfiguration extends BaseWebConfiguration
{
    protected Applet applet;
    
    public AppletConfiguration(final Applet applet) {
        this.applet = applet;
    }
    
    @Override
    protected Object getPropertyInternal(final String key) {
        return this.handleDelimiters(this.applet.getParameter(key));
    }
    
    @Override
    protected Iterator<String> getKeysInternal() {
        final String[][] paramsInfo = this.applet.getParameterInfo();
        final String[] keys = new String[(paramsInfo != null) ? paramsInfo.length : 0];
        for (int i = 0; i < keys.length; ++i) {
            keys[i] = paramsInfo[i][0];
        }
        return Arrays.asList(keys).iterator();
    }
}
