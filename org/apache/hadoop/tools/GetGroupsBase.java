// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.tools;

import org.apache.hadoop.ipc.RPC;
import org.apache.hadoop.net.NetUtils;
import java.io.IOException;
import java.net.InetSocketAddress;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.conf.Configuration;
import java.io.PrintStream;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.conf.Configured;

public abstract class GetGroupsBase extends Configured implements Tool
{
    private PrintStream out;
    
    protected GetGroupsBase(final Configuration conf) {
        this(conf, System.out);
    }
    
    protected GetGroupsBase(final Configuration conf, final PrintStream out) {
        super(conf);
        this.out = out;
    }
    
    @Override
    public int run(String[] args) throws Exception {
        if (args.length == 0) {
            args = new String[] { UserGroupInformation.getCurrentUser().getUserName() };
        }
        for (final String username : args) {
            final StringBuilder sb = new StringBuilder();
            sb.append(username + " :");
            for (final String group : this.getUgmProtocol().getGroupsForUser(username)) {
                sb.append(" ");
                sb.append(group);
            }
            this.out.println(sb);
        }
        return 0;
    }
    
    protected abstract InetSocketAddress getProtocolAddress(final Configuration p0) throws IOException;
    
    protected GetUserMappingsProtocol getUgmProtocol() throws IOException {
        final GetUserMappingsProtocol userGroupMappingProtocol = RPC.getProxy(GetUserMappingsProtocol.class, 1L, this.getProtocolAddress(this.getConf()), UserGroupInformation.getCurrentUser(), this.getConf(), NetUtils.getSocketFactory(this.getConf(), GetUserMappingsProtocol.class));
        return userGroupMappingProtocol;
    }
}
