// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.http.impl.cookie;

import org.apache.http.annotation.ThreadSafe;

@Deprecated
@ThreadSafe
public class BestMatchSpec extends DefaultCookieSpec
{
    public BestMatchSpec(final String[] datepatterns, final boolean oneHeader) {
        super(datepatterns, oneHeader);
    }
    
    public BestMatchSpec() {
        this(null, false);
    }
    
    @Override
    public String toString() {
        return "best-match";
    }
}
