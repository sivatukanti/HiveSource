// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.records;

import org.apache.hadoop.yarn.util.Records;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Stable
public abstract class URL
{
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public static URL newInstance(final String scheme, final String host, final int port, final String file) {
        final URL url = Records.newRecord(URL.class);
        url.setScheme(scheme);
        url.setHost(host);
        url.setPort(port);
        url.setFile(file);
        return url;
    }
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract String getScheme();
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract void setScheme(final String p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract String getUserInfo();
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract void setUserInfo(final String p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract String getHost();
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract void setHost(final String p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract int getPort();
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract void setPort(final int p0);
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract String getFile();
    
    @InterfaceAudience.Public
    @InterfaceStability.Stable
    public abstract void setFile(final String p0);
}
