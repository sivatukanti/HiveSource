// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util;

import org.slf4j.LoggerFactory;
import java.io.PrintStream;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.GnuParser;
import java.util.Iterator;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileUtil;
import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.net.URI;
import java.io.File;
import java.util.List;
import java.util.ArrayList;
import java.net.URL;
import org.apache.hadoop.security.Credentials;
import org.apache.hadoop.security.UserGroupInformation;
import java.net.URLClassLoader;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.FileSystem;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import java.io.IOException;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.CommandLine;
import org.apache.hadoop.conf.Configuration;
import org.slf4j.Logger;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Evolving
public class GenericOptionsParser
{
    private static final Logger LOG;
    private Configuration conf;
    private CommandLine commandLine;
    private final boolean parseSuccessful;
    
    public GenericOptionsParser(final Options opts, final String[] args) throws IOException {
        this(new Configuration(), opts, args);
    }
    
    public GenericOptionsParser(final String[] args) throws IOException {
        this(new Configuration(), new Options(), args);
    }
    
    public GenericOptionsParser(final Configuration conf, final String[] args) throws IOException {
        this(conf, new Options(), args);
    }
    
    public GenericOptionsParser(final Configuration conf, final Options options, final String[] args) throws IOException {
        this.conf = conf;
        this.parseSuccessful = this.parseGeneralOptions(options, args);
    }
    
    public String[] getRemainingArgs() {
        return (this.commandLine == null) ? new String[0] : this.commandLine.getArgs();
    }
    
    public Configuration getConfiguration() {
        return this.conf;
    }
    
    public CommandLine getCommandLine() {
        return this.commandLine;
    }
    
    public boolean isParseSuccessful() {
        return this.parseSuccessful;
    }
    
    protected Options buildGeneralOptions(final Options opts) {
        synchronized (OptionBuilder.class) {
            OptionBuilder.withArgName("file:///|hdfs://namenode:port");
            OptionBuilder.hasArg();
            OptionBuilder.withDescription("specify default filesystem URL to use, overrides 'fs.defaultFS' property from configurations.");
            final Option fs = OptionBuilder.create("fs");
            OptionBuilder.withArgName("local|resourcemanager:port");
            OptionBuilder.hasArg();
            OptionBuilder.withDescription("specify a ResourceManager");
            final Option jt = OptionBuilder.create("jt");
            OptionBuilder.withArgName("configuration file");
            OptionBuilder.hasArg();
            OptionBuilder.withDescription("specify an application configuration file");
            final Option oconf = OptionBuilder.create("conf");
            OptionBuilder.withArgName("property=value");
            OptionBuilder.hasArg();
            OptionBuilder.withDescription("use value for given property");
            final Option property = OptionBuilder.create('D');
            OptionBuilder.withArgName("paths");
            OptionBuilder.hasArg();
            OptionBuilder.withDescription("comma separated jar files to include in the classpath.");
            final Option libjars = OptionBuilder.create("libjars");
            OptionBuilder.withArgName("paths");
            OptionBuilder.hasArg();
            OptionBuilder.withDescription("comma separated files to be copied to the map reduce cluster");
            final Option files = OptionBuilder.create("files");
            OptionBuilder.withArgName("paths");
            OptionBuilder.hasArg();
            OptionBuilder.withDescription("comma separated archives to be unarchived on the compute machines.");
            final Option archives = OptionBuilder.create("archives");
            OptionBuilder.withArgName("tokensFile");
            OptionBuilder.hasArg();
            OptionBuilder.withDescription("name of the file with the tokens");
            final Option tokensFile = OptionBuilder.create("tokenCacheFile");
            opts.addOption(fs);
            opts.addOption(jt);
            opts.addOption(oconf);
            opts.addOption(property);
            opts.addOption(libjars);
            opts.addOption(files);
            opts.addOption(archives);
            opts.addOption(tokensFile);
            return opts;
        }
    }
    
    private void processGeneralOptions(final CommandLine line) throws IOException {
        if (line.hasOption("fs")) {
            FileSystem.setDefaultUri(this.conf, line.getOptionValue("fs"));
        }
        if (line.hasOption("jt")) {
            final String optionValue = line.getOptionValue("jt");
            if (optionValue.equalsIgnoreCase("local")) {
                this.conf.set("mapreduce.framework.name", optionValue);
            }
            this.conf.set("yarn.resourcemanager.address", optionValue, "from -jt command line option");
        }
        if (line.hasOption("conf")) {
            final String[] optionValues;
            final String[] values = optionValues = line.getOptionValues("conf");
            for (final String value : optionValues) {
                this.conf.addResource(new Path(value));
            }
        }
        if (line.hasOption('D')) {
            final String[] optionValues2;
            final String[] property = optionValues2 = line.getOptionValues('D');
            for (final String prop : optionValues2) {
                final String[] keyval = prop.split("=", 2);
                if (keyval.length == 2) {
                    this.conf.set(keyval[0], keyval[1], "from command line");
                }
            }
        }
        if (line.hasOption("libjars")) {
            this.conf.set("tmpjars", this.validateFiles(line.getOptionValue("libjars"), true), "from -libjars command line option");
            final URL[] libjars = getLibJars(this.conf);
            if (libjars != null && libjars.length > 0) {
                this.conf.setClassLoader(new URLClassLoader(libjars, this.conf.getClassLoader()));
                Thread.currentThread().setContextClassLoader(new URLClassLoader(libjars, Thread.currentThread().getContextClassLoader()));
            }
        }
        if (line.hasOption("files")) {
            this.conf.set("tmpfiles", this.validateFiles(line.getOptionValue("files")), "from -files command line option");
        }
        if (line.hasOption("archives")) {
            this.conf.set("tmparchives", this.validateFiles(line.getOptionValue("archives")), "from -archives command line option");
        }
        this.conf.setBoolean("mapreduce.client.genericoptionsparser.used", true);
        if (line.hasOption("tokenCacheFile")) {
            final String fileName = line.getOptionValue("tokenCacheFile");
            final FileSystem localFs = FileSystem.getLocal(this.conf);
            final Path p = localFs.makeQualified(new Path(fileName));
            localFs.getFileStatus(p);
            if (GenericOptionsParser.LOG.isDebugEnabled()) {
                GenericOptionsParser.LOG.debug("setting conf tokensFile: " + fileName);
            }
            UserGroupInformation.getCurrentUser().addCredentials(Credentials.readTokenStorageFile(p, this.conf));
            this.conf.set("mapreduce.job.credentials.binary", p.toString(), "from -tokenCacheFile command line option");
        }
    }
    
    public static URL[] getLibJars(final Configuration conf) throws IOException {
        final String jars = conf.get("tmpjars");
        if (jars == null || jars.trim().isEmpty()) {
            return null;
        }
        final String[] files = jars.split(",");
        final List<URL> cp = new ArrayList<URL>();
        for (final String file : files) {
            final Path tmp = new Path(file);
            if (tmp.getFileSystem(conf).equals(FileSystem.getLocal(conf))) {
                cp.add(FileSystem.getLocal(conf).pathToFile(tmp).toURI().toURL());
            }
            else {
                GenericOptionsParser.LOG.warn("The libjars file " + tmp + " is not on the local filesystem. It will not be added to the local classpath.");
            }
        }
        return cp.toArray(new URL[0]);
    }
    
    private String validateFiles(final String files) throws IOException {
        return this.validateFiles(files, false);
    }
    
    private String validateFiles(final String files, final boolean expandWildcard) throws IOException {
        if (files == null) {
            return null;
        }
        final String[] fileArr = files.split(",");
        if (fileArr.length == 0) {
            throw new IllegalArgumentException("File name can't be empty string");
        }
        final List<String> finalPaths = new ArrayList<String>(fileArr.length);
        for (int i = 0; i < fileArr.length; ++i) {
            String tmp = fileArr[i];
            if (tmp.isEmpty()) {
                throw new IllegalArgumentException("File name can't be empty string");
            }
            final String wildcard = "*";
            final boolean isWildcard = tmp.endsWith("*") && expandWildcard;
            URI pathURI;
            try {
                if (isWildcard) {
                    tmp = tmp.substring(0, tmp.length() - 1);
                }
                pathURI = (this.matchesCurrentDirectory(tmp) ? new File(".").toURI() : new URI(tmp));
            }
            catch (URISyntaxException e) {
                throw new IllegalArgumentException(e);
            }
            final Path path = new Path(pathURI);
            final FileSystem localFs = FileSystem.getLocal(this.conf);
            if (pathURI.getScheme() == null) {
                localFs.getFileStatus(path);
                if (isWildcard) {
                    this.expandWildcard(finalPaths, path, localFs);
                }
                else {
                    finalPaths.add(path.makeQualified(localFs.getUri(), localFs.getWorkingDirectory()).toString());
                }
            }
            else {
                final FileSystem fs = path.getFileSystem(this.conf);
                fs.getFileStatus(path);
                if (isWildcard) {
                    this.expandWildcard(finalPaths, path, fs);
                }
                else {
                    finalPaths.add(path.makeQualified(fs.getUri(), fs.getWorkingDirectory()).toString());
                }
            }
        }
        if (finalPaths.isEmpty()) {
            throw new IllegalArgumentException("Path " + files + " cannot be empty.");
        }
        return StringUtils.join(",", finalPaths);
    }
    
    private boolean matchesCurrentDirectory(final String path) {
        return path.isEmpty() || path.equals(".") || path.equals("." + File.separator);
    }
    
    private void expandWildcard(final List<String> finalPaths, final Path path, final FileSystem fs) throws IOException {
        final FileStatus status = fs.getFileStatus(path);
        if (!status.isDirectory()) {
            throw new FileNotFoundException(path + " is not a directory.");
        }
        final List<Path> jars = FileUtil.getJarsInDirectory(path.toString(), fs.equals(FileSystem.getLocal(this.conf)));
        if (jars.isEmpty()) {
            GenericOptionsParser.LOG.warn(path + " does not have jars in it. It will be ignored.");
        }
        else {
            for (final Path jar : jars) {
                finalPaths.add(jar.makeQualified(fs.getUri(), fs.getWorkingDirectory()).toString());
            }
        }
    }
    
    private String[] preProcessForWindows(final String[] args) {
        if (!Shell.WINDOWS) {
            return args;
        }
        if (args == null) {
            return null;
        }
        final List<String> newArgs = new ArrayList<String>(args.length);
        for (int i = 0; i < args.length; ++i) {
            if (args[i] != null) {
                String prop = null;
                if (args[i].equals("-D")) {
                    newArgs.add(args[i]);
                    if (i < args.length - 1) {
                        prop = args[++i];
                    }
                }
                else if (args[i].startsWith("-D")) {
                    prop = args[i];
                }
                else {
                    newArgs.add(args[i]);
                }
                if (prop != null) {
                    if (!prop.contains("=")) {
                        if (i < args.length - 1) {
                            prop = prop + "=" + args[++i];
                        }
                    }
                    newArgs.add(prop);
                }
            }
        }
        return newArgs.toArray(new String[newArgs.size()]);
    }
    
    private boolean parseGeneralOptions(Options opts, final String[] args) throws IOException {
        opts = this.buildGeneralOptions(opts);
        final CommandLineParser parser = new GnuParser();
        boolean parsed = false;
        try {
            this.processGeneralOptions(this.commandLine = parser.parse(opts, this.preProcessForWindows(args), true));
            parsed = true;
        }
        catch (ParseException e) {
            GenericOptionsParser.LOG.warn("options parsing failed: " + e.getMessage());
            final HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("general options are: ", opts);
        }
        return parsed;
    }
    
    public static void printGenericCommandUsage(final PrintStream out) {
        out.println("Generic options supported are:");
        out.println("-conf <configuration file>        specify an application configuration file");
        out.println("-D <property=value>               define a value for a given property");
        out.println("-fs <file:///|hdfs://namenode:port> specify default filesystem URL to use, overrides 'fs.defaultFS' property from configurations.");
        out.println("-jt <local|resourcemanager:port>  specify a ResourceManager");
        out.println("-files <file1,...>                specify a comma-separated list of files to be copied to the map reduce cluster");
        out.println("-libjars <jar1,...>               specify a comma-separated list of jar files to be included in the classpath");
        out.println("-archives <archive1,...>          specify a comma-separated list of archives to be unarchived on the compute machines");
        out.println();
        out.println("The general command line syntax is:");
        out.println("command [genericOptions] [commandOptions]");
        out.println();
    }
    
    static {
        LOG = LoggerFactory.getLogger(GenericOptionsParser.class);
    }
}
