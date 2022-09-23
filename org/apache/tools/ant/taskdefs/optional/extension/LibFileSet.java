// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.extension;

import org.apache.tools.ant.types.FileSet;

public class LibFileSet extends FileSet
{
    private boolean includeURL;
    private boolean includeImpl;
    private String urlBase;
    
    public void setIncludeUrl(final boolean includeURL) {
        this.includeURL = includeURL;
    }
    
    public void setIncludeImpl(final boolean includeImpl) {
        this.includeImpl = includeImpl;
    }
    
    public void setUrlBase(final String urlBase) {
        this.urlBase = urlBase;
    }
    
    boolean isIncludeURL() {
        return this.includeURL;
    }
    
    boolean isIncludeImpl() {
        return this.includeImpl;
    }
    
    String getUrlBase() {
        return this.urlBase;
    }
}
