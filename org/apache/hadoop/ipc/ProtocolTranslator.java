// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.ipc;

import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
public interface ProtocolTranslator
{
    Object getUnderlyingProxyObject();
}
