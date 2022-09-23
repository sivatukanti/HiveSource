// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server.quorum;

import org.apache.zookeeper.server.ServerCnxn;
import org.apache.zookeeper.data.Id;
import java.util.List;
import java.nio.ByteBuffer;
import org.apache.zookeeper.server.Request;

public class LearnerSyncRequest extends Request
{
    LearnerHandler fh;
    
    public LearnerSyncRequest(final LearnerHandler fh, final long sessionId, final int xid, final int type, final ByteBuffer bb, final List<Id> authInfo) {
        super(null, sessionId, xid, type, bb, authInfo);
        this.fh = fh;
    }
}
