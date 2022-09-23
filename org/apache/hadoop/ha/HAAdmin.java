// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.ha;

import com.google.common.collect.ImmutableMap;
import org.slf4j.LoggerFactory;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Options;
import com.google.common.base.Preconditions;
import java.io.IOException;
import org.apache.commons.cli.CommandLine;
import java.util.Iterator;
import org.apache.hadoop.util.ToolRunner;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import org.apache.hadoop.conf.Configuration;
import java.io.PrintStream;
import java.util.Map;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.conf.Configured;

@InterfaceAudience.Private
public abstract class HAAdmin extends Configured implements Tool
{
    private static final String FORCEFENCE = "forcefence";
    private static final String FORCEACTIVE = "forceactive";
    private static final String FORCEMANUAL = "forcemanual";
    private static final Logger LOG;
    private int rpcTimeoutForChecks;
    protected static final Map<String, UsageInfo> USAGE;
    protected PrintStream errOut;
    protected PrintStream out;
    private HAServiceProtocol.RequestSource requestSource;
    
    protected HAAdmin() {
        this.rpcTimeoutForChecks = -1;
        this.errOut = System.err;
        this.out = System.out;
        this.requestSource = HAServiceProtocol.RequestSource.REQUEST_BY_USER;
    }
    
    protected HAAdmin(final Configuration conf) {
        super(conf);
        this.rpcTimeoutForChecks = -1;
        this.errOut = System.err;
        this.out = System.out;
        this.requestSource = HAServiceProtocol.RequestSource.REQUEST_BY_USER;
    }
    
    protected abstract HAServiceTarget resolveTarget(final String p0);
    
    protected Collection<String> getTargetIds(final String targetNodeToActivate) {
        return new ArrayList<String>(Arrays.asList(targetNodeToActivate));
    }
    
    protected String getUsageString() {
        return "Usage: HAAdmin";
    }
    
    protected void printUsage(final PrintStream errOut) {
        errOut.println(this.getUsageString());
        for (final Map.Entry<String, UsageInfo> e : HAAdmin.USAGE.entrySet()) {
            final String cmd = e.getKey();
            final UsageInfo usage = e.getValue();
            if (usage.args == null) {
                errOut.println("    [" + cmd + "]");
            }
            else {
                errOut.println("    [" + cmd + " " + usage.args + "]");
            }
        }
        errOut.println();
        ToolRunner.printGenericCommandUsage(errOut);
    }
    
    private void printUsage(final PrintStream errOut, final String cmd) {
        final UsageInfo usage = HAAdmin.USAGE.get(cmd);
        if (usage == null) {
            throw new RuntimeException("No usage for cmd " + cmd);
        }
        if (usage.args == null) {
            errOut.println(this.getUsageString() + " [" + cmd + "]");
        }
        else {
            errOut.println(this.getUsageString() + " [" + cmd + " " + usage.args + "]");
        }
    }
    
    private int transitionToActive(final CommandLine cmd) throws IOException, ServiceFailedException {
        final String[] argv = cmd.getArgs();
        if (argv.length != 1) {
            this.errOut.println("transitionToActive: incorrect number of arguments");
            this.printUsage(this.errOut, "-transitionToActive");
            return -1;
        }
        if (!cmd.hasOption("forceactive") && this.isOtherTargetNodeActive(argv[0], cmd.hasOption("forceactive"))) {
            return -1;
        }
        final HAServiceTarget target = this.resolveTarget(argv[0]);
        if (!this.checkManualStateManagementOK(target)) {
            return -1;
        }
        final HAServiceProtocol proto = target.getProxy(this.getConf(), 0);
        HAServiceProtocolHelper.transitionToActive(proto, this.createReqInfo());
        return 0;
    }
    
    private boolean isOtherTargetNodeActive(final String targetNodeToActivate, final boolean forceActive) throws IOException {
        final Collection<String> targetIds = this.getTargetIds(targetNodeToActivate);
        targetIds.remove(targetNodeToActivate);
        for (final String targetId : targetIds) {
            final HAServiceTarget target = this.resolveTarget(targetId);
            if (!this.checkManualStateManagementOK(target)) {
                return true;
            }
            try {
                final HAServiceProtocol proto = target.getProxy(this.getConf(), 5000);
                if (proto.getServiceStatus().getState() == HAServiceProtocol.HAServiceState.ACTIVE) {
                    this.errOut.println("transitionToActive: Node " + targetId + " is already active");
                    this.printUsage(this.errOut, "-transitionToActive");
                    return true;
                }
                continue;
            }
            catch (Exception e) {
                if (!forceActive) {
                    this.errOut.println("Unexpected error occurred  " + e.getMessage());
                    this.printUsage(this.errOut, "-transitionToActive");
                    return true;
                }
                continue;
            }
        }
        return false;
    }
    
    private int transitionToStandby(final CommandLine cmd) throws IOException, ServiceFailedException {
        final String[] argv = cmd.getArgs();
        if (argv.length != 1) {
            this.errOut.println("transitionToStandby: incorrect number of arguments");
            this.printUsage(this.errOut, "-transitionToStandby");
            return -1;
        }
        final HAServiceTarget target = this.resolveTarget(argv[0]);
        if (!this.checkManualStateManagementOK(target)) {
            return -1;
        }
        final HAServiceProtocol proto = target.getProxy(this.getConf(), 0);
        HAServiceProtocolHelper.transitionToStandby(proto, this.createReqInfo());
        return 0;
    }
    
    private boolean checkManualStateManagementOK(final HAServiceTarget target) {
        if (!target.isAutoFailoverEnabled()) {
            return true;
        }
        if (this.requestSource != HAServiceProtocol.RequestSource.REQUEST_BY_USER_FORCED) {
            this.errOut.println("Automatic failover is enabled for " + target + "\nRefusing to manually manage HA state, since it may cause\na split-brain scenario or other incorrect state.\nIf you are very sure you know what you are doing, please \nspecify the --" + "forcemanual" + " flag.");
            return false;
        }
        HAAdmin.LOG.warn("Proceeding with manual HA state management even though\nautomatic failover is enabled for " + target);
        return true;
    }
    
    private HAServiceProtocol.StateChangeRequestInfo createReqInfo() {
        return new HAServiceProtocol.StateChangeRequestInfo(this.requestSource);
    }
    
    private int failover(final CommandLine cmd) throws IOException, ServiceFailedException {
        final boolean forceFence = cmd.hasOption("forcefence");
        final boolean forceActive = cmd.hasOption("forceactive");
        final int numOpts = (cmd.getOptions() == null) ? 0 : cmd.getOptions().length;
        final String[] args = cmd.getArgs();
        if (numOpts > 3 || args.length != 2) {
            this.errOut.println("failover: incorrect arguments");
            this.printUsage(this.errOut, "-failover");
            return -1;
        }
        final HAServiceTarget fromNode = this.resolveTarget(args[0]);
        final HAServiceTarget toNode = this.resolveTarget(args[1]);
        Preconditions.checkState(fromNode.isAutoFailoverEnabled() == toNode.isAutoFailoverEnabled(), "Inconsistent auto-failover configs between %s and %s!", fromNode, toNode);
        if (fromNode.isAutoFailoverEnabled()) {
            if (forceFence || forceActive) {
                this.errOut.println("forcefence and forceactive flags not supported with auto-failover enabled.");
                return -1;
            }
            try {
                return this.gracefulFailoverThroughZKFCs(toNode);
            }
            catch (UnsupportedOperationException e) {
                this.errOut.println("Failover command is not supported with auto-failover enabled: " + e.getLocalizedMessage());
                return -1;
            }
        }
        final FailoverController fc = new FailoverController(this.getConf(), this.requestSource);
        try {
            fc.failover(fromNode, toNode, forceFence, forceActive);
            this.out.println("Failover from " + args[0] + " to " + args[1] + " successful");
        }
        catch (FailoverFailedException ffe) {
            this.errOut.println("Failover failed: " + ffe.getLocalizedMessage());
            return -1;
        }
        return 0;
    }
    
    private int gracefulFailoverThroughZKFCs(final HAServiceTarget toNode) throws IOException {
        final int timeout = FailoverController.getRpcTimeoutToNewActive(this.getConf());
        final ZKFCProtocol proxy = toNode.getZKFCProxy(this.getConf(), timeout);
        try {
            proxy.gracefulFailover();
            this.out.println("Failover to " + toNode + " successful");
        }
        catch (ServiceFailedException sfe) {
            this.errOut.println("Failover failed: " + sfe.getLocalizedMessage());
            return -1;
        }
        return 0;
    }
    
    private int checkHealth(final CommandLine cmd) throws IOException, ServiceFailedException {
        final String[] argv = cmd.getArgs();
        if (argv.length != 1) {
            this.errOut.println("checkHealth: incorrect number of arguments");
            this.printUsage(this.errOut, "-checkHealth");
            return -1;
        }
        final HAServiceProtocol proto = this.resolveTarget(argv[0]).getProxy(this.getConf(), this.rpcTimeoutForChecks);
        try {
            HAServiceProtocolHelper.monitorHealth(proto, this.createReqInfo());
        }
        catch (HealthCheckFailedException e) {
            this.errOut.println("Health check failed: " + e.getLocalizedMessage());
            return -1;
        }
        return 0;
    }
    
    private int getServiceState(final CommandLine cmd) throws IOException, ServiceFailedException {
        final String[] argv = cmd.getArgs();
        if (argv.length != 1) {
            this.errOut.println("getServiceState: incorrect number of arguments");
            this.printUsage(this.errOut, "-getServiceState");
            return -1;
        }
        final HAServiceProtocol proto = this.resolveTarget(argv[0]).getProxy(this.getConf(), this.rpcTimeoutForChecks);
        this.out.println(proto.getServiceStatus().getState());
        return 0;
    }
    
    protected String getServiceAddr(final String serviceId) {
        return serviceId;
    }
    
    @Override
    public void setConf(final Configuration conf) {
        super.setConf(conf);
        if (conf != null) {
            this.rpcTimeoutForChecks = conf.getInt("ha.failover-controller.cli-check.rpc-timeout.ms", 20000);
        }
    }
    
    @Override
    public int run(final String[] argv) throws Exception {
        try {
            return this.runCmd(argv);
        }
        catch (IllegalArgumentException iae) {
            this.errOut.println("Illegal argument: " + iae.getLocalizedMessage());
            return -1;
        }
        catch (IOException ioe) {
            this.errOut.println("Operation failed: " + ioe.getLocalizedMessage());
            if (HAAdmin.LOG.isDebugEnabled()) {
                HAAdmin.LOG.debug("Operation failed", ioe);
            }
            return -1;
        }
    }
    
    protected int runCmd(final String[] argv) throws Exception {
        if (argv.length < 1) {
            this.printUsage(this.errOut);
            return -1;
        }
        final String cmd = argv[0];
        if (!cmd.startsWith("-")) {
            this.errOut.println("Bad command '" + cmd + "': expected command starting with '-'");
            this.printUsage(this.errOut);
            return -1;
        }
        if (!HAAdmin.USAGE.containsKey(cmd)) {
            this.errOut.println(cmd.substring(1) + ": Unknown command");
            this.printUsage(this.errOut);
            return -1;
        }
        final Options opts = new Options();
        if ("-failover".equals(cmd)) {
            this.addFailoverCliOpts(opts);
        }
        if ("-transitionToActive".equals(cmd)) {
            this.addTransitionToActiveCliOpts(opts);
        }
        if ("-transitionToActive".equals(cmd) || "-transitionToStandby".equals(cmd) || "-failover".equals(cmd)) {
            opts.addOption("forcemanual", false, "force manual control even if auto-failover is enabled");
        }
        final CommandLine cmdLine = this.parseOpts(cmd, opts, argv);
        if (cmdLine == null) {
            return -1;
        }
        if (cmdLine.hasOption("forcemanual")) {
            if (!this.confirmForceManual()) {
                HAAdmin.LOG.error("Aborted");
                return -1;
            }
            this.requestSource = HAServiceProtocol.RequestSource.REQUEST_BY_USER_FORCED;
        }
        if ("-transitionToActive".equals(cmd)) {
            return this.transitionToActive(cmdLine);
        }
        if ("-transitionToStandby".equals(cmd)) {
            return this.transitionToStandby(cmdLine);
        }
        if ("-failover".equals(cmd)) {
            return this.failover(cmdLine);
        }
        if ("-getServiceState".equals(cmd)) {
            return this.getServiceState(cmdLine);
        }
        if ("-getAllServiceState".equals(cmd)) {
            return this.getAllServiceState();
        }
        if ("-checkHealth".equals(cmd)) {
            return this.checkHealth(cmdLine);
        }
        if ("-help".equals(cmd)) {
            return this.help(argv);
        }
        throw new AssertionError((Object)("Should not get here, command: " + cmd));
    }
    
    protected int getAllServiceState() {
        final Collection<String> targetIds = this.getTargetIds(null);
        if (targetIds.isEmpty()) {
            this.errOut.println("Failed to get service IDs");
            return -1;
        }
        for (final String targetId : targetIds) {
            final HAServiceTarget target = this.resolveTarget(targetId);
            final String address = target.getAddress().getHostName() + ":" + target.getAddress().getPort();
            try {
                final HAServiceProtocol proto = target.getProxy(this.getConf(), this.rpcTimeoutForChecks);
                this.out.println(String.format("%-50s %-10s", address, proto.getServiceStatus().getState()));
            }
            catch (IOException e) {
                this.out.println(String.format("%-50s %-10s", address, "Failed to connect: " + e.getMessage()));
            }
        }
        return 0;
    }
    
    private boolean confirmForceManual() throws IOException {
        return ToolRunner.confirmPrompt("You have specified the --forcemanual flag. This flag is dangerous, as it can induce a split-brain scenario that WILL CORRUPT your HDFS namespace, possibly irrecoverably.\n\nIt is recommended not to use this flag, but instead to shut down the cluster and disable automatic failover if you prefer to manually manage your HA state.\n\nYou may abort safely by answering 'n' or hitting ^C now.\n\nAre you sure you want to continue?");
    }
    
    private void addFailoverCliOpts(final Options failoverOpts) {
        failoverOpts.addOption("forcefence", false, "force fencing");
        failoverOpts.addOption("forceactive", false, "force failover");
    }
    
    private void addTransitionToActiveCliOpts(final Options transitionToActiveCliOpts) {
        transitionToActiveCliOpts.addOption("forceactive", false, "force active");
    }
    
    private CommandLine parseOpts(final String cmdName, final Options opts, String[] argv) {
        try {
            argv = Arrays.copyOfRange(argv, 1, argv.length);
            return new GnuParser().parse(opts, argv);
        }
        catch (ParseException pe) {
            this.errOut.println(cmdName.substring(1) + ": incorrect arguments");
            this.printUsage(this.errOut, cmdName);
            return null;
        }
    }
    
    private int help(final String[] argv) {
        if (argv.length == 1) {
            this.printUsage(this.out);
            return 0;
        }
        if (argv.length != 2) {
            this.printUsage(this.errOut, "-help");
            return -1;
        }
        String cmd = argv[1];
        if (!cmd.startsWith("-")) {
            cmd = "-" + cmd;
        }
        final UsageInfo usageInfo = HAAdmin.USAGE.get(cmd);
        if (usageInfo == null) {
            this.errOut.println(cmd + ": Unknown command");
            this.printUsage(this.errOut);
            return -1;
        }
        if (usageInfo.args == null) {
            this.out.println(cmd + ": " + usageInfo.help);
        }
        else {
            this.out.println(cmd + " [" + usageInfo.args + "]: " + usageInfo.help);
        }
        return 0;
    }
    
    static {
        LOG = LoggerFactory.getLogger(HAAdmin.class);
        USAGE = ImmutableMap.builder().put("-transitionToActive", new UsageInfo("[--forceactive] <serviceId>", "Transitions the service into Active state")).put("-transitionToStandby", new UsageInfo("<serviceId>", "Transitions the service into Standby state")).put("-failover", new UsageInfo("[--forcefence] [--forceactive] <serviceId> <serviceId>", "Failover from the first service to the second.\nUnconditionally fence services if the --forcefence option is used.\nTry to failover to the target service even if it is not ready if the --forceactive option is used.")).put("-getServiceState", new UsageInfo("<serviceId>", "Returns the state of the service")).put("-getAllServiceState", new UsageInfo(null, "Returns the state of all the services")).put("-checkHealth", new UsageInfo("<serviceId>", "Requests that the service perform a health check.\nThe HAAdmin tool will exit with a non-zero exit code\nif the check fails.")).put("-help", new UsageInfo("<command>", "Displays help on the specified command")).build();
    }
    
    public static class UsageInfo
    {
        public final String args;
        public final String help;
        
        public UsageInfo(final String args, final String help) {
            this.args = args;
            this.help = help;
        }
    }
}
