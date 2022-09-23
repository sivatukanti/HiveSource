// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs.shell;

import java.io.IOException;
import java.util.ArrayList;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.fs.ContentSummary;
import org.apache.hadoop.fs.QuotaUsage;
import java.util.LinkedList;
import java.util.Arrays;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.StorageType;
import java.util.List;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Evolving
public class Count extends FsCommand
{
    private static final String OPTION_QUOTA = "q";
    private static final String OPTION_HUMAN = "h";
    private static final String OPTION_HEADER = "v";
    private static final String OPTION_TYPE = "t";
    private static final String OPTION_EXCLUDE_SNAPSHOT = "x";
    private static final String OPTION_QUOTA_AND_USAGE = "u";
    private static final String OPTION_ECPOLICY = "e";
    public static final String NAME = "count";
    public static final String USAGE = "[-q] [-h] [-v] [-t [<storage type>]] [-u] [-x] [-e] <path> ...";
    public static final String DESCRIPTION;
    private boolean showQuotas;
    private boolean humanReadable;
    private boolean showQuotabyType;
    private List<StorageType> storageTypes;
    private boolean showQuotasAndUsageOnly;
    private boolean excludeSnapshots;
    private boolean displayECPolicy;
    
    public static void registerCommands(final CommandFactory factory) {
        factory.addClass(Count.class, "-count");
    }
    
    public Count() {
        this.storageTypes = null;
    }
    
    @Deprecated
    public Count(final String[] cmd, final int pos, final Configuration conf) {
        super(conf);
        this.storageTypes = null;
        this.args = Arrays.copyOfRange(cmd, pos, cmd.length);
    }
    
    @Override
    protected void processOptions(final LinkedList<String> args) {
        final CommandFormat cf = new CommandFormat(1, Integer.MAX_VALUE, new String[] { "q", "h", "v", "u", "x", "e" });
        cf.addOptionWithValue("t");
        cf.parse(args);
        if (args.isEmpty()) {
            args.add(".");
        }
        this.showQuotas = cf.getOpt("q");
        this.humanReadable = cf.getOpt("h");
        this.showQuotasAndUsageOnly = cf.getOpt("u");
        this.excludeSnapshots = cf.getOpt("x");
        this.displayECPolicy = cf.getOpt("e");
        if (this.showQuotas || this.showQuotasAndUsageOnly) {
            final String types = cf.getOptValue("t");
            if (null != types) {
                this.showQuotabyType = true;
                this.storageTypes = this.getAndCheckStorageTypes(types);
            }
            else {
                this.showQuotabyType = false;
            }
            if (this.excludeSnapshots) {
                this.out.println("q or u option is given, the -x option is ignored.");
                this.excludeSnapshots = false;
            }
        }
        if (cf.getOpt("v")) {
            final StringBuilder headString = new StringBuilder();
            if (this.showQuotabyType) {
                headString.append(QuotaUsage.getStorageTypeHeader(this.storageTypes));
            }
            else if (this.showQuotasAndUsageOnly) {
                headString.append(QuotaUsage.getHeader());
            }
            else {
                headString.append(ContentSummary.getHeader(this.showQuotas));
            }
            if (this.displayECPolicy) {
                headString.append("ERASURECODING_POLICY ");
            }
            headString.append("PATHNAME");
            this.out.println(headString.toString());
        }
    }
    
    private List<StorageType> getAndCheckStorageTypes(final String types) {
        if ("".equals(types) || "all".equalsIgnoreCase(types)) {
            return StorageType.getTypesSupportingQuota();
        }
        final String[] typeArray = StringUtils.split(types, ',');
        final List<StorageType> stTypes = new ArrayList<StorageType>();
        for (final String t : typeArray) {
            stTypes.add(StorageType.parseStorageType(t));
        }
        return stTypes;
    }
    
    @Override
    protected void processPath(final PathData src) throws IOException {
        final StringBuilder outputString = new StringBuilder();
        if (this.showQuotasAndUsageOnly || this.showQuotabyType) {
            final QuotaUsage usage = src.fs.getQuotaUsage(src.path);
            outputString.append(usage.toString(this.isHumanReadable(), this.showQuotabyType, this.storageTypes));
        }
        else {
            final ContentSummary summary = src.fs.getContentSummary(src.path);
            outputString.append(summary.toString(this.showQuotas, this.isHumanReadable(), this.excludeSnapshots));
        }
        if (this.displayECPolicy) {
            final ContentSummary summary = src.fs.getContentSummary(src.path);
            if (!summary.getErasureCodingPolicy().equals("Replicated")) {
                outputString.append("EC:");
            }
            outputString.append(summary.getErasureCodingPolicy());
            outputString.append(" ");
        }
        outputString.append(src);
        this.out.println(outputString.toString());
    }
    
    @InterfaceAudience.Private
    boolean isShowQuotas() {
        return this.showQuotas;
    }
    
    @InterfaceAudience.Private
    boolean isHumanReadable() {
        return this.humanReadable;
    }
    
    @InterfaceAudience.Private
    boolean isShowQuotabyType() {
        return this.showQuotabyType;
    }
    
    @InterfaceAudience.Private
    List<StorageType> getStorageTypes() {
        return this.storageTypes;
    }
    
    static {
        DESCRIPTION = "Count the number of directories, files and bytes under the paths\nthat match the specified file pattern.  The output columns are:\n" + StringUtils.join((Object[])ContentSummary.getHeaderFields(), ' ') + " PATHNAME\nor, with the -" + "q" + " option:\n" + StringUtils.join((Object[])ContentSummary.getQuotaHeaderFields(), ' ') + "\n      " + StringUtils.join((Object[])ContentSummary.getHeaderFields(), ' ') + " PATHNAME\nThe -" + "h" + " option shows file sizes in human readable format.\nThe -" + "v" + " option displays a header line.\nThe -" + "x" + " option excludes snapshots from being calculated. \nThe -" + "t" + " option displays quota by storage types.\nIt should be used with -" + "q" + " or -" + "u" + " option, otherwise it will be ignored.\nIf a comma-separated list of storage types is given after the -" + "t" + " option, \nit displays the quota and usage for the specified types. \nOtherwise, it displays the quota and usage for all the storage \ntypes that support quota. The list of possible storage types(case insensitive):\nram_disk, ssd, disk and archive.\nIt can also pass the value '', 'all' or 'ALL' to specify all the storage types.\nThe -" + "u" + " option shows the quota and \nthe usage against the quota without the detailed content summary.The -" + "e" + " option shows the erasure coding policy.";
    }
}
