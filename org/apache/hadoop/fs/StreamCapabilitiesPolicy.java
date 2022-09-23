// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

import org.slf4j.LoggerFactory;
import java.io.InputStream;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public class StreamCapabilitiesPolicy
{
    public static final String CAN_UNBUFFER_NOT_IMPLEMENTED_MESSAGE = "claims unbuffer capabilty but does not implement CanUnbuffer";
    static final Logger LOG;
    
    public static void unbuffer(final InputStream in) {
        try {
            if (in instanceof StreamCapabilities && ((StreamCapabilities)in).hasCapability("in:unbuffer")) {
                ((CanUnbuffer)in).unbuffer();
            }
            else {
                StreamCapabilitiesPolicy.LOG.debug(in.getClass().getName() + ": does not implement StreamCapabilities and the unbuffer capability");
            }
        }
        catch (ClassCastException e) {
            throw new UnsupportedOperationException(in.getClass().getName() + ": " + "claims unbuffer capabilty but does not implement CanUnbuffer");
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(StreamCapabilitiesPolicy.class);
    }
}
