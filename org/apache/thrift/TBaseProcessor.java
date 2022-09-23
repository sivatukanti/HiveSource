// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.thrift;

import org.apache.thrift.protocol.TMessage;
import org.apache.thrift.protocol.TProtocolUtil;
import org.apache.thrift.protocol.TProtocol;
import java.util.Collections;
import java.util.Map;

public abstract class TBaseProcessor<I> implements TProcessor
{
    private final I iface;
    private final Map<String, ProcessFunction<I, ? extends TBase>> processMap;
    
    protected TBaseProcessor(final I iface, final Map<String, ProcessFunction<I, ? extends TBase>> processFunctionMap) {
        this.iface = iface;
        this.processMap = processFunctionMap;
    }
    
    public Map<String, ProcessFunction<I, ? extends TBase>> getProcessMapView() {
        return Collections.unmodifiableMap((Map<? extends String, ? extends ProcessFunction<I, ? extends TBase>>)this.processMap);
    }
    
    public boolean process(final TProtocol in, final TProtocol out) throws TException {
        final TMessage msg = in.readMessageBegin();
        final ProcessFunction fn = this.processMap.get(msg.name);
        if (fn == null) {
            TProtocolUtil.skip(in, (byte)12);
            in.readMessageEnd();
            final TApplicationException x = new TApplicationException(1, "Invalid method name: '" + msg.name + "'");
            out.writeMessageBegin(new TMessage(msg.name, (byte)3, msg.seqid));
            x.write(out);
            out.writeMessageEnd();
            out.getTransport().flush();
            return true;
        }
        fn.process(msg.seqid, in, out, this.iface);
        return true;
    }
}
