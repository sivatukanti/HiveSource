// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.net;

import java.util.EventListener;

public interface ProtocolCommandListener extends EventListener
{
    void protocolCommandSent(final ProtocolCommandEvent p0);
    
    void protocolReplyReceived(final ProtocolCommandEvent p0);
}
