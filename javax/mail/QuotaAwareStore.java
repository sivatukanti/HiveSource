// 
// Decompiled by Procyon v0.5.36
// 

package javax.mail;

public interface QuotaAwareStore
{
    Quota[] getQuota(final String p0) throws MessagingException;
    
    void setQuota(final Quota p0) throws MessagingException;
}
