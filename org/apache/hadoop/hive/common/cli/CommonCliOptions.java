// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.common.cli;

import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import java.util.Iterator;
import java.util.Properties;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

public class CommonCliOptions
{
    protected final Options OPTIONS;
    protected CommandLine commandLine;
    protected final String cliname;
    private boolean verbose;
    
    public CommonCliOptions(final String cliname, final boolean includeHiveConf) {
        this.OPTIONS = new Options();
        this.verbose = false;
        this.cliname = cliname;
        this.OPTIONS.addOption(new Option("v", "verbose", false, "Verbose mode"));
        this.OPTIONS.addOption(new Option("h", "help", false, "Print help information"));
        if (includeHiveConf) {
            final Options options = this.OPTIONS;
            OptionBuilder.withValueSeparator();
            OptionBuilder.hasArgs(2);
            OptionBuilder.withArgName("property=value");
            OptionBuilder.withLongOpt("hiveconf");
            OptionBuilder.withDescription("Use value for given property");
            options.addOption(OptionBuilder.create());
        }
    }
    
    public Properties addHiveconfToSystemProperties() {
        final Properties confProps = this.commandLine.getOptionProperties("hiveconf");
        for (final String propKey : confProps.stringPropertyNames()) {
            if (this.verbose) {
                System.err.println("hiveconf: " + propKey + "=" + confProps.getProperty(propKey));
            }
            System.setProperty(propKey, confProps.getProperty(propKey));
        }
        return confProps;
    }
    
    public void printUsage() {
        new HelpFormatter().printHelp(this.cliname, this.OPTIONS);
    }
    
    public void parse(final String[] args) {
        try {
            this.commandLine = new GnuParser().parse(this.OPTIONS, args);
            if (this.commandLine.hasOption('h')) {
                this.printUsage();
                System.exit(1);
            }
            if (this.commandLine.hasOption('v')) {
                this.verbose = true;
            }
        }
        catch (ParseException e) {
            System.err.println(e.getMessage());
            this.printUsage();
            System.exit(1);
        }
    }
    
    public boolean isVerbose() {
        return this.verbose;
    }
}
