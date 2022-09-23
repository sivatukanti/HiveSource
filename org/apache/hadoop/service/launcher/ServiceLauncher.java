// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.service.launcher;

import org.slf4j.LoggerFactory;
import org.apache.hadoop.util.StringUtils;
import org.apache.hadoop.net.NetUtils;
import org.apache.commons.cli.CommandLine;
import org.apache.hadoop.util.GenericOptionsParser;
import java.io.IOException;
import java.io.File;
import org.apache.hadoop.util.ExitCodeProvider;
import com.google.common.base.Preconditions;
import com.google.common.annotations.VisibleForTesting;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import java.util.Iterator;
import java.util.Collection;
import java.util.Arrays;
import java.util.ArrayList;
import org.apache.commons.cli.Options;
import java.net.URL;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.util.ExitUtil;
import org.slf4j.Logger;
import org.apache.hadoop.service.Service;

public class ServiceLauncher<S extends Service> implements LauncherExitCodes, LauncherArguments, Thread.UncaughtExceptionHandler
{
    private static final Logger LOG;
    protected static final int SHUTDOWN_PRIORITY = 30;
    public static final String NAME = "ServiceLauncher";
    protected static final String USAGE_NAME = "Usage: ServiceLauncher";
    protected static final String USAGE_SERVICE_ARGUMENTS = "service-classname <service arguments>";
    public static final String USAGE_MESSAGE = "Usage: ServiceLauncher [--conf <conf file>] [--hadoopconf <configuration classname>] service-classname <service arguments>";
    private static final int SHUTDOWN_TIME_ON_INTERRUPT = 30000;
    private volatile S service;
    private int serviceExitCode;
    private ExitUtil.ExitException serviceException;
    private InterruptEscalator interruptEscalator;
    private Configuration configuration;
    private String serviceName;
    private String serviceClassName;
    protected static final String[] DEFAULT_CONFIGS;
    private List<String> confClassnames;
    private List<URL> confResourceUrls;
    private Options commandOptions;
    
    public ServiceLauncher(final String serviceClassName) {
        this(serviceClassName, serviceClassName);
    }
    
    public ServiceLauncher(final String serviceName, final String serviceClassName) {
        this.serviceClassName = "";
        this.confClassnames = new ArrayList<String>(ServiceLauncher.DEFAULT_CONFIGS.length);
        this.confResourceUrls = new ArrayList<URL>(1);
        this.serviceClassName = serviceClassName;
        this.serviceName = serviceName;
        this.confClassnames.addAll(Arrays.asList(ServiceLauncher.DEFAULT_CONFIGS));
    }
    
    public final S getService() {
        return this.service;
    }
    
    protected void setService(final S s) {
        this.service = s;
    }
    
    public final Configuration getConfiguration() {
        return this.configuration;
    }
    
    public final int getServiceExitCode() {
        return this.serviceExitCode;
    }
    
    public final ExitUtil.ExitException getServiceException() {
        return this.serviceException;
    }
    
    private boolean isClassnameDefined() {
        return this.serviceClassName != null && !this.serviceClassName.isEmpty();
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("\"ServiceLauncher for \"");
        sb.append(this.serviceName);
        if (this.isClassnameDefined()) {
            sb.append(", serviceClassName='").append(this.serviceClassName).append('\'');
        }
        if (this.service != null) {
            sb.append(", service=").append(this.service);
        }
        return sb.toString();
    }
    
    public void launchServiceAndExit(final List<String> args) {
        final StringBuilder builder = new StringBuilder();
        for (final String arg : args) {
            builder.append('\"').append(arg).append("\" ");
        }
        final String argumentString = builder.toString();
        if (ServiceLauncher.LOG.isDebugEnabled()) {
            ServiceLauncher.LOG.debug(startupShutdownMessage(this.serviceName, args));
            ServiceLauncher.LOG.debug(argumentString);
        }
        this.registerFailureHandling();
        this.loadConfigurationClasses();
        final Configuration conf = this.createConfiguration();
        for (final URL resourceUrl : this.confResourceUrls) {
            conf.addResource(resourceUrl);
        }
        this.bindCommandOptions();
        ExitUtil.ExitException exitException;
        try {
            final List<String> processedArgs = this.extractCommandOptions(conf, args);
            exitException = this.launchService(conf, processedArgs, true, true);
        }
        catch (ExitUtil.ExitException e) {
            exitException = e;
            this.noteException(exitException);
        }
        if (exitException.getExitCode() != 0) {
            System.err.println(this.getUsageMessage());
            System.err.println("Command: " + argumentString);
        }
        System.out.flush();
        System.err.flush();
        this.exit(exitException);
    }
    
    protected void bindCommandOptions() {
        this.commandOptions = this.createOptions();
    }
    
    void noteException(final ExitUtil.ExitException exitException) {
        ServiceLauncher.LOG.debug("Exception raised", exitException);
        this.serviceExitCode = exitException.getExitCode();
        this.serviceException = exitException;
    }
    
    protected String getUsageMessage() {
        String message = "Usage: ServiceLauncher [--conf <conf file>] [--hadoopconf <configuration classname>] service-classname <service arguments>";
        if (this.commandOptions != null) {
            message = "Usage: ServiceLauncher " + this.commandOptions.toString() + " " + "service-classname <service arguments>";
        }
        return message;
    }
    
    protected Options createOptions() {
        synchronized (OptionBuilder.class) {
            final Options options = new Options();
            OptionBuilder.withArgName("configuration file");
            OptionBuilder.hasArg();
            OptionBuilder.withDescription("specify an application configuration file");
            OptionBuilder.withLongOpt("conf");
            final Option oconf = OptionBuilder.create("conf");
            OptionBuilder.withArgName("configuration classname");
            OptionBuilder.hasArg();
            OptionBuilder.withDescription("Classname of a Hadoop Configuration subclass to load");
            OptionBuilder.withLongOpt("hadoopconf");
            final Option confclass = OptionBuilder.create("hadoopconf");
            OptionBuilder.withArgName("property=value");
            OptionBuilder.hasArg();
            OptionBuilder.withDescription("use value for given property");
            final Option property = OptionBuilder.create('D');
            options.addOption(oconf);
            options.addOption(property);
            options.addOption(confclass);
            return options;
        }
    }
    
    protected Configuration createConfiguration() {
        return new Configuration();
    }
    
    protected List<String> getConfigurationsToCreate() {
        return this.confClassnames;
    }
    
    @VisibleForTesting
    public int loadConfigurationClasses() {
        final List<String> toCreate = this.getConfigurationsToCreate();
        int loaded = 0;
        for (final String classname : toCreate) {
            try {
                final Class<?> loadClass = this.getClassLoader().loadClass(classname);
                final Object instance = loadClass.getConstructor((Class<?>[])new Class[0]).newInstance(new Object[0]);
                if (!(instance instanceof Configuration)) {
                    throw new ExitUtil.ExitException(56, "Could not create " + classname + " because it is not a Configuration class/subclass");
                }
                ++loaded;
            }
            catch (ClassNotFoundException e3) {
                ServiceLauncher.LOG.debug("Failed to load {} because it is not on the classpath", classname);
            }
            catch (ExitUtil.ExitException e) {
                throw e;
            }
            catch (Exception e2) {
                ServiceLauncher.LOG.info("Failed to create {}", classname, e2);
            }
        }
        return loaded;
    }
    
    @VisibleForTesting
    public ExitUtil.ExitException launchService(final Configuration conf, final List<String> processedArgs, final boolean addShutdownHook, final boolean execute) {
        ExitUtil.ExitException exitException;
        try {
            final int exitCode = this.coreServiceLaunch(conf, processedArgs, addShutdownHook, execute);
            if (this.service != null) {
                final Throwable failure = this.service.getFailureCause();
                if (failure != null) {
                    final Service.STATE failureState = this.service.getFailureState();
                    if (failureState != Service.STATE.STOPPED) {
                        throw failure;
                    }
                    ServiceLauncher.LOG.debug("Failure during shutdown: {} ", failure, failure);
                }
            }
            final String name = this.getServiceName();
            if (exitCode == 0) {
                exitException = new ServiceLaunchException(exitCode, "%s succeeded", new Object[] { name });
            }
            else {
                exitException = new ServiceLaunchException(exitCode, "%s failed ", new Object[] { name });
            }
        }
        catch (ExitUtil.ExitException ee) {
            exitException = ee;
        }
        catch (Throwable thrown) {
            exitException = convertToExitException(thrown);
        }
        this.noteException(exitException);
        return exitException;
    }
    
    protected int coreServiceLaunch(final Configuration conf, final List<String> processedArgs, final boolean addShutdownHook, final boolean execute) throws Exception {
        this.instantiateService(conf);
        ServiceShutdownHook shutdownHook = null;
        if (addShutdownHook) {
            shutdownHook = new ServiceShutdownHook(this.service);
            shutdownHook.register(30);
        }
        final String name = this.getServiceName();
        ServiceLauncher.LOG.debug("Launched service {}", name);
        LaunchableService launchableService = null;
        if (this.service instanceof LaunchableService) {
            ServiceLauncher.LOG.debug("Service {} implements LaunchableService", name);
            launchableService = (LaunchableService)this.service;
            if (launchableService.isInState(Service.STATE.INITED)) {
                ServiceLauncher.LOG.warn("LaunchableService {} initialized in constructor before CLI arguments passed in", name);
            }
            final Configuration newconf = launchableService.bindArgs(this.configuration, processedArgs);
            if (newconf != null) {
                this.configuration = newconf;
            }
        }
        if (!this.service.isInState(Service.STATE.INITED)) {
            this.service.init(this.configuration);
        }
        int exitCode;
        try {
            this.service.start();
            exitCode = 0;
            if (execute && this.service.isInState(Service.STATE.STARTED)) {
                if (launchableService != null) {
                    try {
                        exitCode = launchableService.execute();
                        ServiceLauncher.LOG.debug("Service {} execution returned exit code {}", name, exitCode);
                    }
                    finally {
                        this.service.stop();
                    }
                }
                else {
                    ServiceLauncher.LOG.debug("waiting for service threads to terminate");
                    this.service.waitForServiceToStop(0L);
                }
            }
        }
        finally {
            if (shutdownHook != null) {
                shutdownHook.unregister();
            }
        }
        return exitCode;
    }
    
    public Service instantiateService(final Configuration conf) {
        Preconditions.checkArgument(conf != null, (Object)"null conf");
        Preconditions.checkArgument(this.serviceClassName != null, (Object)"null service classname");
        Preconditions.checkArgument(!this.serviceClassName.isEmpty(), (Object)"undefined service classname");
        this.configuration = conf;
        Object instance;
        try {
            final Class<?> serviceClass = this.getClassLoader().loadClass(this.serviceClassName);
            try {
                instance = serviceClass.getConstructor((Class<?>[])new Class[0]).newInstance(new Object[0]);
            }
            catch (NoSuchMethodException noEmptyConstructor) {
                ServiceLauncher.LOG.debug("No empty constructor {}", noEmptyConstructor, noEmptyConstructor);
                instance = serviceClass.getConstructor(String.class).newInstance(this.serviceClassName);
            }
        }
        catch (Exception e) {
            throw this.serviceCreationFailure(e);
        }
        if (!(instance instanceof Service)) {
            throw new ServiceLaunchException(56, "Not a service class: \"%s\"", new Object[] { this.serviceClassName });
        }
        return this.service = (S)instance;
    }
    
    protected static ExitUtil.ExitException convertToExitException(final Throwable thrown) {
        String message = thrown.toString();
        int exitCode;
        if (thrown instanceof ExitCodeProvider) {
            exitCode = ((ExitCodeProvider)thrown).getExitCode();
            message = thrown.getMessage();
            if (message == null) {
                message = thrown.toString();
            }
        }
        else {
            exitCode = 50;
        }
        final ExitUtil.ExitException exitException = new ServiceLaunchException(exitCode, message);
        exitException.initCause(thrown);
        return exitException;
    }
    
    protected ServiceLaunchException serviceCreationFailure(final Exception exception) {
        return new ServiceLaunchException(56, exception);
    }
    
    protected void registerFailureHandling() {
        try {
            (this.interruptEscalator = new InterruptEscalator(this, 30000)).register("INT");
            this.interruptEscalator.register("TERM");
        }
        catch (IllegalArgumentException e) {
            ServiceLauncher.LOG.warn("{}", e, e);
        }
        Thread.setDefaultUncaughtExceptionHandler(new HadoopUncaughtExceptionHandler(this));
    }
    
    @Override
    public void uncaughtException(final Thread thread, final Throwable exception) {
        ServiceLauncher.LOG.error("Uncaught exception in thread {} -exiting", thread, exception);
        this.exit(convertToExitException(exception));
    }
    
    public String getServiceName() {
        final Service s = this.service;
        String name = null;
        if (s != null) {
            try {
                name = s.getName();
            }
            catch (Exception ex) {}
        }
        if (name != null) {
            return "service " + name;
        }
        return "service " + this.serviceName;
    }
    
    protected void warn(final String text) {
        if (ServiceLauncher.LOG.isWarnEnabled()) {
            ServiceLauncher.LOG.warn(text);
        }
        else {
            System.err.println(text);
        }
    }
    
    protected void error(final String message, final Throwable thrown) {
        final String text = "Exception: " + message;
        if (ServiceLauncher.LOG.isErrorEnabled()) {
            ServiceLauncher.LOG.error(text, thrown);
        }
        else {
            System.err.println(text);
            if (thrown != null) {
                System.err.println(thrown.toString());
            }
        }
    }
    
    protected void exit(final int exitCode, final String message) {
        ExitUtil.terminate(exitCode, message);
    }
    
    protected void exit(final ExitUtil.ExitException ee) {
        ExitUtil.terminate(ee);
    }
    
    protected ClassLoader getClassLoader() {
        return this.getClass().getClassLoader();
    }
    
    public List<String> extractCommandOptions(final Configuration conf, final List<String> args) {
        final int size = args.size();
        if (size <= 1) {
            return new ArrayList<String>(0);
        }
        final List<String> coreArgs = args.subList(1, size);
        return this.parseCommandArgs(conf, coreArgs);
    }
    
    protected List<String> parseCommandArgs(final Configuration conf, final List<String> args) {
        Preconditions.checkNotNull(this.commandOptions, (Object)"Command options have not been created");
        final StringBuilder argString = new StringBuilder(args.size() * 32);
        for (final String arg : args) {
            argString.append("\"").append(arg).append("\" ");
        }
        ServiceLauncher.LOG.debug("Command line: {}", argString);
        try {
            final String[] argArray = args.toArray(new String[args.size()]);
            final GenericOptionsParser parser = this.createGenericOptionsParser(conf, argArray);
            if (!parser.isParseSuccessful()) {
                throw new ServiceLaunchException(40, "Failed to parse:  %s", new Object[] { argString });
            }
            final CommandLine line = parser.getCommandLine();
            final List<String> remainingArgs = Arrays.asList(parser.getRemainingArgs());
            ServiceLauncher.LOG.debug("Remaining arguments {}", remainingArgs);
            if (line.hasOption("conf")) {
                final String[] filenames = line.getOptionValues("conf");
                this.verifyConfigurationFilesExist(filenames);
                for (final String filename : filenames) {
                    final File file = new File(filename);
                    ServiceLauncher.LOG.debug("Configuration files {}", file);
                    this.confResourceUrls.add(file.toURI().toURL());
                }
            }
            if (line.hasOption("hadoopconf")) {
                final List<String> classnameList = Arrays.asList(line.getOptionValues("hadoopconf"));
                ServiceLauncher.LOG.debug("Configuration classes {}", classnameList);
                this.confClassnames.addAll(classnameList);
            }
            return remainingArgs;
        }
        catch (IOException e) {
            throw new ServiceLaunchException(40, e);
        }
        catch (RuntimeException e2) {
            throw new ServiceLaunchException(40, "Failed to parse:  %s : %s", new Object[] { argString, e2 });
        }
    }
    
    protected GenericOptionsParser createGenericOptionsParser(final Configuration conf, final String[] argArray) throws IOException {
        return new MinimalGenericOptionsParser(conf, this.commandOptions, argArray);
    }
    
    protected void verifyConfigurationFilesExist(final String[] filenames) {
        if (filenames == null) {
            return;
        }
        for (final String filename : filenames) {
            final File file = new File(filename);
            ServiceLauncher.LOG.debug("Conf file {}", file.getAbsolutePath());
            if (!file.exists()) {
                throw new ServiceLaunchException(44, "--conf: configuration file not found: %s", new Object[] { file.getAbsolutePath() });
            }
        }
    }
    
    protected static String startupShutdownMessage(final String classname, final List<String> args) {
        final String hostname = NetUtils.getHostname();
        return StringUtils.createStartupShutdownMessage(classname, hostname, args.toArray(new String[args.size()]));
    }
    
    protected static void exitWithMessage(final int status, final String message) {
        ExitUtil.terminate(new ServiceLaunchException(status, message));
    }
    
    protected static void exitWithUsageMessage() {
        exitWithMessage(42, "Usage: ServiceLauncher [--conf <conf file>] [--hadoopconf <configuration classname>] service-classname <service arguments>");
    }
    
    public static void main(final String[] args) {
        serviceMain(Arrays.asList(args));
    }
    
    public static void serviceMain(final String... args) {
        serviceMain(Arrays.asList(args));
    }
    
    public static void serviceMain(final List<String> argsList) {
        if (argsList.isEmpty()) {
            exitWithUsageMessage();
        }
        else {
            final ServiceLauncher<Service> serviceLauncher = new ServiceLauncher<Service>(argsList.get(0));
            serviceLauncher.launchServiceAndExit(argsList);
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(ServiceLauncher.class);
        DEFAULT_CONFIGS = new String[] { "org.apache.hadoop.conf.Configuration", "org.apache.hadoop.hdfs.HdfsConfiguration", "org.apache.hadoop.yarn.conf.YarnConfiguration" };
    }
    
    protected static class MinimalGenericOptionsParser extends GenericOptionsParser
    {
        public MinimalGenericOptionsParser(final Configuration conf, final Options options, final String[] args) throws IOException {
            super(conf, options, args);
        }
        
        @Override
        protected Options buildGeneralOptions(final Options opts) {
            return opts;
        }
    }
}
