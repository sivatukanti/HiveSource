// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.api;

public interface CreateBackgroundModeACLable extends BackgroundPathAndBytesable<String>, CreateModable<ACLBackgroundPathAndBytesable<String>>, ACLCreateModeBackgroundPathAndBytesable<String>
{
    ACLCreateModePathAndBytesable<String> creatingParentsIfNeeded();
    
    ACLCreateModePathAndBytesable<String> creatingParentContainersIfNeeded();
    
    ACLPathAndBytesable<String> withProtectedEphemeralSequential();
}
