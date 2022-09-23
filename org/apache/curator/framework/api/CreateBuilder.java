// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.api;

public interface CreateBuilder extends BackgroundPathAndBytesable<String>, CreateModable<ACLBackgroundPathAndBytesable<String>>, ACLCreateModeBackgroundPathAndBytesable<String>, Compressible<CreateBackgroundModeACLable>
{
    ProtectACLCreateModePathAndBytesable<String> creatingParentsIfNeeded();
    
    ProtectACLCreateModePathAndBytesable<String> creatingParentContainersIfNeeded();
    
    @Deprecated
    ACLPathAndBytesable<String> withProtectedEphemeralSequential();
    
    ACLCreateModeBackgroundPathAndBytesable<String> withProtection();
}
