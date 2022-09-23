// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server;

import java.io.IOException;
import org.apache.jute.Record;
import org.apache.zookeeper.proto.ReplyHeader;
import org.apache.zookeeper.KeeperException;

public class UnimplementedRequestProcessor implements RequestProcessor
{
    @Override
    public void processRequest(final Request request) throws RequestProcessorException {
        final KeeperException ke = new KeeperException.UnimplementedException();
        request.setException(ke);
        final ReplyHeader rh = new ReplyHeader(request.cxid, request.zxid, ke.code().intValue());
        try {
            request.cnxn.sendResponse(rh, null, "response");
        }
        catch (IOException e) {
            throw new RequestProcessorException("Can't send the response", e);
        }
        request.cnxn.sendCloseSession();
    }
    
    @Override
    public void shutdown() {
    }
}
