// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.security.client;

import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.client.api.TimelineClient;
import org.apache.hadoop.conf.Configuration;
import java.io.IOException;
import org.apache.hadoop.security.token.Token;
import org.apache.hadoop.security.token.TokenRenewer;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Unstable
public class TimelineDelegationTokenIdentifier extends YARNDelegationTokenIdentifier
{
    public static final Text KIND_NAME;
    
    public TimelineDelegationTokenIdentifier() {
    }
    
    public TimelineDelegationTokenIdentifier(final Text owner, final Text renewer, final Text realUser) {
        super(owner, renewer, realUser);
    }
    
    @Override
    public Text getKind() {
        return TimelineDelegationTokenIdentifier.KIND_NAME;
    }
    
    static {
        KIND_NAME = new Text("TIMELINE_DELEGATION_TOKEN");
    }
    
    @InterfaceAudience.Private
    public static class Renewer extends TokenRenewer
    {
        @Override
        public boolean handleKind(final Text kind) {
            return TimelineDelegationTokenIdentifier.KIND_NAME.equals(kind);
        }
        
        @Override
        public boolean isManaged(final Token<?> token) throws IOException {
            return true;
        }
        
        @Override
        public long renew(final Token<?> token, final Configuration conf) throws IOException, InterruptedException {
            final TimelineClient client = TimelineClient.createTimelineClient();
            try {
                client.init(conf);
                client.start();
                return client.renewDelegationToken((Token<TimelineDelegationTokenIdentifier>)token);
            }
            catch (YarnException e) {
                throw new IOException(e);
            }
            finally {
                client.stop();
            }
        }
        
        @Override
        public void cancel(final Token<?> token, final Configuration conf) throws IOException, InterruptedException {
            final TimelineClient client = TimelineClient.createTimelineClient();
            try {
                client.init(conf);
                client.start();
                client.cancelDelegationToken((Token<TimelineDelegationTokenIdentifier>)token);
            }
            catch (YarnException e) {
                throw new IOException(e);
            }
            finally {
                client.stop();
            }
        }
    }
}
