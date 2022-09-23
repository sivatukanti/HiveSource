// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.metrics2.util;

import org.apache.hadoop.net.NetUtils;
import com.google.common.collect.Lists;
import java.net.InetSocketAddress;
import java.util.List;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public class Servers
{
    private Servers() {
    }
    
    public static List<InetSocketAddress> parse(final String specs, final int defaultPort) {
        final List<InetSocketAddress> result = (List<InetSocketAddress>)Lists.newArrayList();
        if (specs == null) {
            result.add(new InetSocketAddress("localhost", defaultPort));
        }
        else {
            final String[] split;
            final String[] specStrings = split = specs.split("[ ,]+");
            for (final String specString : split) {
                result.add(NetUtils.createSocketAddr(specString, defaultPort));
            }
        }
        return result;
    }
}
