// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.webapp;

import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.LimitedPrivate({ "YARN", "MapReduce" })
public interface MimeType
{
    public static final String TEXT = "text/plain; charset=UTF-8";
    public static final String HTML = "text/html; charset=UTF-8";
    public static final String XML = "text/xml; charset=UTF-8";
    public static final String HTTP = "message/http; charset=UTF-8";
    public static final String JSON = "application/json; charset=UTF-8";
}
