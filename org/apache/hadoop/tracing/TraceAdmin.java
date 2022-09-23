// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.tracing;

import org.slf4j.LoggerFactory;
import org.apache.hadoop.conf.Configuration;
import java.net.InetSocketAddress;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.net.NetUtils;
import org.apache.hadoop.ipc.RPC;
import org.apache.hadoop.ipc.ProtobufRpcEngine;
import java.util.LinkedList;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import org.apache.hadoop.util.StringUtils;
import java.io.IOException;
import org.apache.hadoop.tools.TableListing;
import java.util.List;
import java.io.PrintStream;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.conf.Configured;

@InterfaceAudience.Private
public class TraceAdmin extends Configured implements Tool
{
    private TraceAdminProtocolPB proxy;
    private TraceAdminProtocolTranslatorPB remote;
    private static final Logger LOG;
    private static final String CONFIG_PREFIX = "-C";
    
    private void usage() {
        final PrintStream err = System.err;
        err.print("Hadoop tracing configuration commands:\n  -add [-class classname] [-Ckey=value] [-Ckey2=value2] ...\n    Add a span receiver with the provided class name.  Configuration\n    keys for the span receiver can be specified with the -C options.\n    The span receiver will also inherit whatever configuration keys\n    exist in the daemon's configuration.\n  -help: Print this help message.\n  -host [hostname:port]\n    Specify the hostname and port of the daemon to examine.\n    Required for all commands.\n  -list: List the current span receivers.\n  -remove [id]\n    Remove the span receiver with the specified id.  Use -list to\n    find the id of each receiver.\n  -principal: If the daemon is Kerberized, specify the service\n    principal name.");
    }
    
    private int listSpanReceivers(final List<String> args) throws IOException {
        final SpanReceiverInfo[] infos = this.remote.listSpanReceivers();
        if (infos.length == 0) {
            System.out.println("[no span receivers found]");
            return 0;
        }
        final TableListing listing = new TableListing.Builder().addField("ID").addField("CLASS").showHeaders().build();
        for (final SpanReceiverInfo info : infos) {
            listing.addRow("" + info.getId(), info.getClassName());
        }
        System.out.println(listing.toString());
        return 0;
    }
    
    private int addSpanReceiver(final List<String> args) throws IOException {
        final String className = StringUtils.popOptionWithArgument("-class", args);
        if (className == null) {
            System.err.println("You must specify the classname with -class.");
            return 1;
        }
        final ByteArrayOutputStream configStream = new ByteArrayOutputStream();
        final PrintStream configsOut = new PrintStream(configStream, false, "UTF-8");
        final SpanReceiverInfoBuilder factory = new SpanReceiverInfoBuilder(className);
        String prefix = "";
        for (int i = 0; i < args.size(); ++i) {
            String str = args.get(i);
            if (!str.startsWith("-C")) {
                System.err.println("Can't understand argument: " + str);
                return 1;
            }
            str = str.substring("-C".length());
            final int equalsIndex = str.indexOf("=");
            if (equalsIndex < 0) {
                System.err.println("Can't parse configuration argument " + str);
                System.err.println("Arguments must be in the form key=value");
                return 1;
            }
            final String key = str.substring(0, equalsIndex);
            final String value = str.substring(equalsIndex + 1);
            factory.addConfigurationPair(key, value);
            configsOut.print(prefix + key + " = " + value);
            prefix = ", ";
        }
        final String configStreamStr = configStream.toString("UTF-8");
        try {
            final long id = this.remote.addSpanReceiver(factory.build());
            System.out.println("Added trace span receiver " + id + " with configuration " + configStreamStr);
        }
        catch (IOException e) {
            System.out.println("addSpanReceiver error with configuration " + configStreamStr);
            throw e;
        }
        return 0;
    }
    
    private int removeSpanReceiver(final List<String> args) throws IOException {
        final String indexStr = StringUtils.popFirstNonOption(args);
        long id = -1L;
        try {
            id = Long.parseLong(indexStr);
        }
        catch (NumberFormatException e) {
            System.err.println("Failed to parse ID string " + indexStr + ": " + e.getMessage());
            return 1;
        }
        this.remote.removeSpanReceiver(id);
        System.err.println("Removed trace span receiver " + id);
        return 0;
    }
    
    @Override
    public int run(final String[] argv) throws Exception {
        final LinkedList<String> args = new LinkedList<String>();
        for (final String arg : argv) {
            args.add(arg);
        }
        if (StringUtils.popOption("-h", args) || StringUtils.popOption("-help", args)) {
            this.usage();
            return 0;
        }
        if (args.size() == 0) {
            this.usage();
            return 0;
        }
        final String hostPort = StringUtils.popOptionWithArgument("-host", args);
        if (hostPort == null) {
            System.err.println("You must specify a host with -host.");
            return 1;
        }
        if (args.isEmpty()) {
            System.err.println("You must specify an operation.");
            return 1;
        }
        final String servicePrincipal = StringUtils.popOptionWithArgument("-principal", args);
        if (servicePrincipal != null) {
            TraceAdmin.LOG.debug("Set service principal: {}", servicePrincipal);
            this.getConf().set("hadoop.security.service.user.name.key", servicePrincipal);
        }
        RPC.setProtocolEngine(this.getConf(), TraceAdminProtocolPB.class, ProtobufRpcEngine.class);
        final InetSocketAddress address = NetUtils.createSocketAddr(hostPort);
        final UserGroupInformation ugi = UserGroupInformation.getCurrentUser();
        final Class<?> xface = TraceAdminProtocolPB.class;
        this.proxy = RPC.getProxy(xface, RPC.getProtocolVersion(xface), address, ugi, this.getConf(), NetUtils.getDefaultSocketFactory(this.getConf()), 0);
        this.remote = new TraceAdminProtocolTranslatorPB(this.proxy);
        try {
            if (args.get(0).equals("-list")) {
                return this.listSpanReceivers(args.subList(1, args.size()));
            }
            if (args.get(0).equals("-add")) {
                return this.addSpanReceiver(args.subList(1, args.size()));
            }
            if (args.get(0).equals("-remove")) {
                return this.removeSpanReceiver(args.subList(1, args.size()));
            }
            System.err.println("Unrecognized tracing command: " + args.get(0));
            System.err.println("Use -help for help.");
            return 1;
        }
        finally {
            this.remote.close();
        }
    }
    
    public static void main(final String[] argv) throws Exception {
        final TraceAdmin admin = new TraceAdmin();
        admin.setConf(new Configuration());
        System.exit(admin.run(argv));
    }
    
    static {
        LOG = LoggerFactory.getLogger(TraceAdmin.class);
    }
}
