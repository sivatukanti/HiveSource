// 
// Decompiled by Procyon v0.5.36
// 

package javax.jdo;

import java.net.URI;
import java.net.URLClassLoader;
import java.net.MalformedURLException;
import java.net.URL;
import java.io.File;
import java.util.Iterator;
import java.util.Set;
import java.util.Map;
import java.util.ArrayList;
import java.util.Properties;
import java.util.List;
import javax.jdo.spi.I18NHelper;

public class Enhancer
{
    private static final I18NHelper msg;
    private char NL;
    private String JAR_FILE_SUFFIX;
    private String JDO_FILE_SUFFIX;
    private String CLASS_FILE_SUFFIX;
    private boolean error;
    private boolean printAndExit;
    private List<String> persistenceUnitNames;
    private String directoryName;
    private ClassLoader loader;
    private String classPath;
    private boolean checkOnly;
    private boolean verbose;
    private boolean recurse;
    private StringBuilder errorBuffer;
    private StringBuilder verboseBuffer;
    private List<String> fileNames;
    private List<String> classFileNames;
    private List<String> jdoFileNames;
    private List<String> jarFileNames;
    private int numberOfValidatedClasses;
    private int numberOfEnhancedClasses;
    private Properties properties;
    
    public Enhancer() {
        this.NL = '\n';
        this.JAR_FILE_SUFFIX = ".jar";
        this.JDO_FILE_SUFFIX = ".jdo";
        this.CLASS_FILE_SUFFIX = ".class";
        this.error = false;
        this.printAndExit = false;
        this.persistenceUnitNames = new ArrayList<String>();
        this.directoryName = null;
        this.loader = null;
        this.classPath = null;
        this.checkOnly = false;
        this.verbose = false;
        this.recurse = false;
        this.errorBuffer = new StringBuilder();
        this.verboseBuffer = new StringBuilder();
        this.fileNames = new ArrayList<String>();
        this.classFileNames = new ArrayList<String>();
        this.jdoFileNames = new ArrayList<String>();
        this.jarFileNames = new ArrayList<String>();
        this.numberOfValidatedClasses = 0;
        this.numberOfEnhancedClasses = 0;
    }
    
    public static void main(final String[] args) {
        final Enhancer enhancerMain = new Enhancer();
        enhancerMain.run(args);
    }
    
    private void run(final String[] args) {
        this.processArgs(args);
        JDOEnhancer enhancer = null;
        try {
            enhancer = JDOHelper.getEnhancer();
        }
        catch (JDOException jdoex) {
            jdoex.printStackTrace();
            this.exit(2);
        }
        try {
            this.properties = enhancer.getProperties();
            this.addVerboseMessage("MSG_EnhancerClass", enhancer.getClass().getName());
            this.addVerboseMessage("MSG_EnhancerProperty", "VendorName", this.properties.getProperty("VendorName"));
            this.addVerboseMessage("MSG_EnhancerProperty", "VersionNumber", this.properties.getProperty("VersionNumber"));
            final Set<Map.Entry<Object, Object>> props = this.properties.entrySet();
            for (final Map.Entry<Object, Object> entry : props) {
                if (!"VendorName".equals(entry.getKey()) && !"VersionNumber".equals(entry.getKey())) {
                    this.addVerboseMessage("MSG_EnhancerProperty", entry.getKey(), entry.getValue());
                }
            }
            enhancer.setVerbose(this.verbose);
            if (this.loader != null) {
                enhancer.setClassLoader(this.loader);
            }
            final int numberOfClasses = this.classFileNames.size();
            if (numberOfClasses != 0) {
                enhancer.addClasses((String[])this.classFileNames.toArray(new String[numberOfClasses]));
            }
            final int numberOfFiles = this.jdoFileNames.size();
            if (numberOfFiles != 0) {
                enhancer.addFiles((String[])this.jdoFileNames.toArray(new String[numberOfFiles]));
            }
            if (0 < this.jarFileNames.size()) {
                for (final String jarFileName : this.jarFileNames) {
                    enhancer.addJar(jarFileName);
                }
            }
            if (this.persistenceUnitNames != null) {
                for (final String persistenceUnitName : this.persistenceUnitNames) {
                    enhancer.addPersistenceUnit(persistenceUnitName);
                }
            }
            if (this.directoryName != null) {
                enhancer.setOutputDirectory(this.directoryName);
            }
            if (this.checkOnly) {
                this.addVerboseMessage("MSG_EnhancerValidatedClasses", this.numberOfValidatedClasses = enhancer.validate());
            }
            else {
                this.addVerboseMessage("MSG_EnhancerEnhancedClasses", this.numberOfEnhancedClasses = enhancer.enhance());
            }
            this.exit(0);
        }
        catch (Exception ex) {
            ex.printStackTrace();
            this.exit(1);
        }
    }
    
    private void processArgs(final String[] args) {
        this.parseArgs(args);
        this.parseFiles(this.fileNames.toArray(new String[this.fileNames.size()]), true, this.recurse);
        this.loader = this.prepareClassLoader(this.classPath);
        if (this.error) {
            this.addErrorMessage(Enhancer.msg.msg("MSG_EnhancerUsage"));
            this.exit(3);
        }
        if (this.printAndExit) {
            this.addVerboseMessage("MSG_EnhancerUsage");
            this.exit(0);
        }
    }
    
    private void parseArgs(final String[] args) {
        boolean doneWithOptions = false;
        this.fileNames = new ArrayList<String>();
        for (int i = 0; i < args.length; ++i) {
            final String arg = args[i];
            if ("?".equals(arg)) {
                this.printAndExit = true;
                return;
            }
            if (!doneWithOptions) {
                if (arg.startsWith("-")) {
                    final String option = arg.substring(1);
                    if ("help".equals(option)) {
                        this.addVerboseMessage("MSG_EnhancerProcessing", "-help");
                        this.setPrintAndExit();
                    }
                    else if ("h".equals(option)) {
                        this.addVerboseMessage("MSG_EnhancerProcessing", "-h");
                        this.setPrintAndExit();
                    }
                    else if ("v".equals(option)) {
                        this.addVerboseMessage("MSG_EnhancerProcessing", "-v");
                        this.verbose = true;
                    }
                    else if ("verbose".equals(option)) {
                        this.addVerboseMessage("MSG_EnhancerProcessing", "-verbose");
                        this.verbose = true;
                    }
                    else if ("pu".equals(option)) {
                        if (this.hasNextArgument("MSG_EnhancerProcessing", "-pu", i, args.length)) {
                            final String puName = args[++i];
                            this.addVerboseMessage("MSG_EnhancerPersistenceUnitName", puName);
                            this.persistenceUnitNames.add(puName);
                        }
                        else {
                            this.setError();
                        }
                    }
                    else if ("cp".equals(option)) {
                        if (this.hasNextArgument("MSG_EnhancerProcessing", "-cp", i, args.length)) {
                            this.addVerboseMessage("MSG_EnhancerClassPath", this.classPath = args[++i]);
                        }
                        else {
                            this.setError();
                        }
                    }
                    else if ("d".equals(option)) {
                        if (this.hasNextArgument("MSG_EnhancerProcessing", "-d", i, args.length)) {
                            this.addVerboseMessage("MSG_EnhancerOutputDirectory", this.directoryName = args[++i]);
                        }
                        else {
                            this.setError();
                        }
                    }
                    else if ("checkonly".equals(option)) {
                        this.addVerboseMessage("MSG_EnhancerProcessing", "-checkonly");
                        this.checkOnly = true;
                    }
                    else if ("r".equals(option)) {
                        this.addVerboseMessage("MSG_EnhancerProcessing", "-r");
                        this.recurse = true;
                    }
                    else {
                        this.setError();
                        this.addErrorMessage(Enhancer.msg.msg("ERR_EnhancerUnrecognizedOption", option));
                    }
                }
                else {
                    doneWithOptions = true;
                    this.fileNames.add(arg);
                }
            }
            else {
                this.fileNames.add(arg);
            }
        }
    }
    
    private boolean hasNextArgument(final String msgId, final String where, final int i, final int length) {
        if (i + 1 >= length) {
            this.setError();
            this.addErrorMessage(Enhancer.msg.msg(msgId, where));
            this.addErrorMessage(Enhancer.msg.msg("ERR_EnhancerRequiredArgumentMissing"));
            return false;
        }
        return true;
    }
    
    private void parseFiles(final String[] fileNames, final boolean search, final boolean recurse) {
        for (final String fileName : fileNames) {
            if (fileName.endsWith(this.JAR_FILE_SUFFIX)) {
                this.jarFileNames.add(fileName);
                this.addVerboseMessage("MSG_EnhancerJarFileName", fileName);
            }
            else if (fileName.endsWith(this.JDO_FILE_SUFFIX)) {
                this.jdoFileNames.add(fileName);
                this.addVerboseMessage("MSG_EnhancerJDOFileName", fileName);
            }
            else if (fileName.endsWith(this.CLASS_FILE_SUFFIX)) {
                this.classFileNames.add(fileName);
                this.addVerboseMessage("MSG_EnhancerClassFileName", fileName);
            }
            else {
                final File directoryFile = new File(fileName);
                if (directoryFile.isDirectory() && search) {
                    final String directoryPath = directoryFile.getAbsolutePath();
                    final String[] files = directoryFile.list();
                    final String[] pathName = { null };
                    if (files != null) {
                        for (final String file : files) {
                            pathName[0] = directoryPath + '/' + file;
                            this.parseFiles(pathName, recurse, recurse);
                        }
                    }
                }
            }
        }
    }
    
    private ClassLoader prepareClassLoader(final String classPath) {
        if (classPath == null) {
            return null;
        }
        ClassLoader result = null;
        final String separator = System.getProperty("path.separator");
        final String[] paths = classPath.split(separator);
        final List<URL> urls = new ArrayList<URL>();
        for (final String path : paths) {
            final File file = new File(path);
            final URI uri = file.toURI();
            try {
                final URL url = uri.toURL();
                this.addVerboseMessage("MSG_EnhancerClassPath", url.toString());
                urls.add(url);
            }
            catch (MalformedURLException e) {
                this.setError();
                this.addErrorMessage(Enhancer.msg.msg("ERR_EnhancerBadClassPath", file));
            }
        }
        result = new URLClassLoader(urls.toArray(new URL[urls.size()]), (ClassLoader)null);
        return result;
    }
    
    private void addErrorMessage(final String message) {
        this.errorBuffer.append(message);
        this.errorBuffer.append(this.NL);
    }
    
    private void setError() {
        this.error = true;
    }
    
    private void setPrintAndExit() {
        this.printAndExit = true;
    }
    
    private void exit(final int exitValue) {
        System.out.print(this.verboseBuffer.toString());
        System.err.print(this.errorBuffer.toString());
        System.exit(exitValue);
    }
    
    private void addVerboseMessage(final String msgId, final String... where) {
        this.verboseBuffer.append(Enhancer.msg.msg(msgId, where));
        this.verboseBuffer.append(this.NL);
    }
    
    private void addVerboseMessage(final String msgId, final String where) {
        this.verboseBuffer.append(Enhancer.msg.msg(msgId, where));
        this.verboseBuffer.append(this.NL);
    }
    
    private void addVerboseMessage(final String msgId) {
        this.verboseBuffer.append(Enhancer.msg.msg(msgId));
        this.verboseBuffer.append(this.NL);
    }
    
    private void addVerboseMessage(final String msgId, final int where) {
        this.addVerboseMessage(msgId, String.valueOf(where));
    }
    
    static {
        msg = I18NHelper.getInstance("javax.jdo.Bundle");
    }
}
