// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.util.timeline;

import org.apache.hadoop.yarn.webapp.YarnJacksonJaxbJsonProvider;
import org.apache.hadoop.security.SecurityUtil;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import java.net.InetSocketAddress;
import org.apache.hadoop.conf.Configuration;
import java.io.IOException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.ObjectMapper;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public class TimelineUtils
{
    private static ObjectMapper mapper;
    
    public static String dumpTimelineRecordtoJSON(final Object o) throws JsonGenerationException, JsonMappingException, IOException {
        return dumpTimelineRecordtoJSON(o, false);
    }
    
    public static String dumpTimelineRecordtoJSON(final Object o, final boolean pretty) throws JsonGenerationException, JsonMappingException, IOException {
        if (pretty) {
            return TimelineUtils.mapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
        }
        return TimelineUtils.mapper.writeValueAsString(o);
    }
    
    public static InetSocketAddress getTimelineTokenServiceAddress(final Configuration conf) {
        InetSocketAddress timelineServiceAddr = null;
        if (YarnConfiguration.useHttps(conf)) {
            timelineServiceAddr = conf.getSocketAddr("yarn.timeline-service.webapp.https.address", "0.0.0.0:8190", 8190);
        }
        else {
            timelineServiceAddr = conf.getSocketAddr("yarn.timeline-service.webapp.address", "0.0.0.0:8188", 8188);
        }
        return timelineServiceAddr;
    }
    
    public static Text buildTimelineTokenService(final Configuration conf) {
        final InetSocketAddress timelineServiceAddr = getTimelineTokenServiceAddress(conf);
        return SecurityUtil.buildTokenService(timelineServiceAddr);
    }
    
    static {
        YarnJacksonJaxbJsonProvider.configObjectMapper(TimelineUtils.mapper = new ObjectMapper());
    }
}
