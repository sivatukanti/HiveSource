// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.api;

public interface ProtectACLCreateModePathAndBytesable<T> extends ACLBackgroundPathAndBytesable<T>, CreateModable<ACLBackgroundPathAndBytesable<T>>
{
    ACLCreateModeBackgroundPathAndBytesable<String> withProtection();
}
