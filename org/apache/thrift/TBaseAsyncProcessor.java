// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.thrift;

import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.async.AsyncMethodCallback;
import org.apache.thrift.protocol.TProtocolException;
import org.apache.thrift.protocol.TMessage;
import org.apache.thrift.protocol.TProtocolUtil;
import org.apache.thrift.server.AbstractNonblockingServer;
import java.util.Collections;
import org.slf4j.LoggerFactory;
import java.util.Map;
import org.slf4j.Logger;

public class TBaseAsyncProcessor<I> implements TProcessor
{
    protected final Logger LOGGER;
    final I iface;
    final Map<String, AsyncProcessFunction<I, ? extends TBase, ?>> processMap;
    
    public TBaseAsyncProcessor(final I iface, final Map<String, AsyncProcessFunction<I, ? extends TBase, ?>> processMap) {
        this.LOGGER = LoggerFactory.getLogger(this.getClass().getName());
        this.iface = iface;
        this.processMap = processMap;
    }
    
    public Map<String, AsyncProcessFunction<I, ? extends TBase, ?>> getProcessMapView() {
        return Collections.unmodifiableMap((Map<? extends String, ? extends AsyncProcessFunction<I, ? extends TBase, ?>>)this.processMap);
    }
    
    public boolean process(final AbstractNonblockingServer.AsyncFrameBuffer fb) throws TException {
        final TProtocol in = fb.getInputProtocol();
        final TProtocol out = fb.getOutputProtocol();
        final TMessage msg = in.readMessageBegin();
        final AsyncProcessFunction fn = this.processMap.get(msg.name);
        if (fn == null) {
            TProtocolUtil.skip(in, (byte)12);
            in.readMessageEnd();
            final TApplicationException x = new TApplicationException(1, "Invalid method name: '" + msg.name + "'");
            out.writeMessageBegin(new TMessage(msg.name, (byte)3, msg.seqid));
            x.write(out);
            out.writeMessageEnd();
            out.getTransport().flush();
            fb.responseReady();
            return true;
        }
        final TBase args = fn.getEmptyArgsInstance();
        try {
            args.read(in);
        }
        catch (TProtocolException e) {
            in.readMessageEnd();
            final TApplicationException x2 = new TApplicationException(7, e.getMessage());
            out.writeMessageBegin(new TMessage(msg.name, (byte)3, msg.seqid));
            x2.write(out);
            out.writeMessageEnd();
            out.getTransport().flush();
            fb.responseReady();
            return true;
        }
        in.readMessageEnd();
        fn.start(this.iface, args, fn.getResultHandler(fb, msg.seqid));
        return true;
    }
    
    public boolean process(final TProtocol in, final TProtocol out) throws TException {
        return false;
    }
}
