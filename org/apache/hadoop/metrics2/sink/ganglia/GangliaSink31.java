// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.metrics2.sink.ganglia;

import java.io.IOException;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

public class GangliaSink31 extends GangliaSink30
{
    public final Logger LOG;
    
    public GangliaSink31() {
        this.LOG = LoggerFactory.getLogger(this.getClass());
    }
    
    @Override
    protected void emitMetric(final String groupName, final String name, final String type, final String value, final GangliaConf gConf, final GangliaSlope gSlope) throws IOException {
        if (name == null) {
            this.LOG.warn("Metric was emitted with no name.");
            return;
        }
        if (value == null) {
            this.LOG.warn("Metric name " + name + " was emitted with a null value.");
            return;
        }
        if (type == null) {
            this.LOG.warn("Metric name " + name + ", value " + value + " has no type.");
            return;
        }
        if (this.LOG.isDebugEnabled()) {
            this.LOG.debug("Emitting metric " + name + ", type " + type + ", value " + value + ", slope " + gSlope.name() + " from hostname " + this.getHostName());
        }
        this.xdr_int(128);
        this.xdr_string(this.getHostName());
        this.xdr_string(name);
        this.xdr_int(0);
        this.xdr_string(type);
        this.xdr_string(name);
        this.xdr_string(gConf.getUnits());
        this.xdr_int(gSlope.ordinal());
        this.xdr_int(gConf.getTmax());
        this.xdr_int(gConf.getDmax());
        this.xdr_int(1);
        this.xdr_string("GROUP");
        this.xdr_string(groupName);
        this.emitToGangliaHosts();
        this.xdr_int(133);
        this.xdr_string(this.getHostName());
        this.xdr_string(name);
        this.xdr_int(0);
        this.xdr_string("%s");
        this.xdr_string(value);
        this.emitToGangliaHosts();
    }
}
