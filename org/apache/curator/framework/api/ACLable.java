// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.curator.framework.api;

import org.apache.zookeeper.data.ACL;
import java.util.List;

public interface ACLable<T>
{
    T withACL(final List<ACL> p0);
}
