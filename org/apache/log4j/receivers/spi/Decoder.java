// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.log4j.receivers.spi;

import java.util.Map;
import java.io.IOException;
import java.net.URL;
import org.apache.log4j.spi.LoggingEvent;
import java.util.Vector;

public interface Decoder
{
    Vector decodeEvents(final String p0);
    
    LoggingEvent decode(final String p0);
    
    Vector decode(final URL p0) throws IOException;
    
    void setAdditionalProperties(final Map p0);
}
