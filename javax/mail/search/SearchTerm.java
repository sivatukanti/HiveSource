// 
// Decompiled by Procyon v0.5.36
// 

package javax.mail.search;

import javax.mail.Message;
import java.io.Serializable;

public abstract class SearchTerm implements Serializable
{
    private static final long serialVersionUID = -6652358452205992789L;
    
    public abstract boolean match(final Message p0);
}
