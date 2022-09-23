// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.javacc;

import java.io.InputStream;
import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.Path;
import java.util.Enumeration;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.util.JavaEnvUtils;
import org.apache.tools.ant.types.CommandlineJava;
import java.io.File;
import java.util.Hashtable;
import org.apache.tools.ant.Task;

public class JavaCC extends Task
{
    private static final String LOOKAHEAD = "LOOKAHEAD";
    private static final String CHOICE_AMBIGUITY_CHECK = "CHOICE_AMBIGUITY_CHECK";
    private static final String OTHER_AMBIGUITY_CHECK = "OTHER_AMBIGUITY_CHECK";
    private static final String STATIC = "STATIC";
    private static final String DEBUG_PARSER = "DEBUG_PARSER";
    private static final String DEBUG_LOOKAHEAD = "DEBUG_LOOKAHEAD";
    private static final String DEBUG_TOKEN_MANAGER = "DEBUG_TOKEN_MANAGER";
    private static final String OPTIMIZE_TOKEN_MANAGER = "OPTIMIZE_TOKEN_MANAGER";
    private static final String ERROR_REPORTING = "ERROR_REPORTING";
    private static final String JAVA_UNICODE_ESCAPE = "JAVA_UNICODE_ESCAPE";
    private static final String UNICODE_INPUT = "UNICODE_INPUT";
    private static final String IGNORE_CASE = "IGNORE_CASE";
    private static final String COMMON_TOKEN_ACTION = "COMMON_TOKEN_ACTION";
    private static final String USER_TOKEN_MANAGER = "USER_TOKEN_MANAGER";
    private static final String USER_CHAR_STREAM = "USER_CHAR_STREAM";
    private static final String BUILD_PARSER = "BUILD_PARSER";
    private static final String BUILD_TOKEN_MANAGER = "BUILD_TOKEN_MANAGER";
    private static final String SANITY_CHECK = "SANITY_CHECK";
    private static final String FORCE_LA_CHECK = "FORCE_LA_CHECK";
    private static final String CACHE_TOKENS = "CACHE_TOKENS";
    private static final String KEEP_LINE_COLUMN = "KEEP_LINE_COLUMN";
    private static final String JDK_VERSION = "JDK_VERSION";
    private final Hashtable optionalAttrs;
    private File outputDirectory;
    private File targetFile;
    private File javaccHome;
    private CommandlineJava cmdl;
    protected static final int TASKDEF_TYPE_JAVACC = 1;
    protected static final int TASKDEF_TYPE_JJTREE = 2;
    protected static final int TASKDEF_TYPE_JJDOC = 3;
    protected static final String[] ARCHIVE_LOCATIONS;
    protected static final int[] ARCHIVE_LOCATIONS_VS_MAJOR_VERSION;
    protected static final String COM_PACKAGE = "COM.sun.labs.";
    protected static final String COM_JAVACC_CLASS = "javacc.Main";
    protected static final String COM_JJTREE_CLASS = "jjtree.Main";
    protected static final String COM_JJDOC_CLASS = "jjdoc.JJDocMain";
    protected static final String ORG_PACKAGE_3_0 = "org.netbeans.javacc.";
    protected static final String ORG_PACKAGE_3_1 = "org.javacc.";
    protected static final String ORG_JAVACC_CLASS = "parser.Main";
    protected static final String ORG_JJTREE_CLASS = "jjtree.Main";
    protected static final String ORG_JJDOC_CLASS = "jjdoc.JJDocMain";
    private String maxMemory;
    
    public void setLookahead(final int lookahead) {
        this.optionalAttrs.put("LOOKAHEAD", new Integer(lookahead));
    }
    
    public void setChoiceambiguitycheck(final int choiceAmbiguityCheck) {
        this.optionalAttrs.put("CHOICE_AMBIGUITY_CHECK", new Integer(choiceAmbiguityCheck));
    }
    
    public void setOtherambiguityCheck(final int otherAmbiguityCheck) {
        this.optionalAttrs.put("OTHER_AMBIGUITY_CHECK", new Integer(otherAmbiguityCheck));
    }
    
    public void setStatic(final boolean staticParser) {
        this.optionalAttrs.put("STATIC", staticParser ? Boolean.TRUE : Boolean.FALSE);
    }
    
    public void setDebugparser(final boolean debugParser) {
        this.optionalAttrs.put("DEBUG_PARSER", debugParser ? Boolean.TRUE : Boolean.FALSE);
    }
    
    public void setDebuglookahead(final boolean debugLookahead) {
        this.optionalAttrs.put("DEBUG_LOOKAHEAD", debugLookahead ? Boolean.TRUE : Boolean.FALSE);
    }
    
    public void setDebugtokenmanager(final boolean debugTokenManager) {
        this.optionalAttrs.put("DEBUG_TOKEN_MANAGER", debugTokenManager ? Boolean.TRUE : Boolean.FALSE);
    }
    
    public void setOptimizetokenmanager(final boolean optimizeTokenManager) {
        this.optionalAttrs.put("OPTIMIZE_TOKEN_MANAGER", optimizeTokenManager ? Boolean.TRUE : Boolean.FALSE);
    }
    
    public void setErrorreporting(final boolean errorReporting) {
        this.optionalAttrs.put("ERROR_REPORTING", errorReporting ? Boolean.TRUE : Boolean.FALSE);
    }
    
    public void setJavaunicodeescape(final boolean javaUnicodeEscape) {
        this.optionalAttrs.put("JAVA_UNICODE_ESCAPE", javaUnicodeEscape ? Boolean.TRUE : Boolean.FALSE);
    }
    
    public void setUnicodeinput(final boolean unicodeInput) {
        this.optionalAttrs.put("UNICODE_INPUT", unicodeInput ? Boolean.TRUE : Boolean.FALSE);
    }
    
    public void setIgnorecase(final boolean ignoreCase) {
        this.optionalAttrs.put("IGNORE_CASE", ignoreCase ? Boolean.TRUE : Boolean.FALSE);
    }
    
    public void setCommontokenaction(final boolean commonTokenAction) {
        this.optionalAttrs.put("COMMON_TOKEN_ACTION", commonTokenAction ? Boolean.TRUE : Boolean.FALSE);
    }
    
    public void setUsertokenmanager(final boolean userTokenManager) {
        this.optionalAttrs.put("USER_TOKEN_MANAGER", userTokenManager ? Boolean.TRUE : Boolean.FALSE);
    }
    
    public void setUsercharstream(final boolean userCharStream) {
        this.optionalAttrs.put("USER_CHAR_STREAM", userCharStream ? Boolean.TRUE : Boolean.FALSE);
    }
    
    public void setBuildparser(final boolean buildParser) {
        this.optionalAttrs.put("BUILD_PARSER", buildParser ? Boolean.TRUE : Boolean.FALSE);
    }
    
    public void setBuildtokenmanager(final boolean buildTokenManager) {
        this.optionalAttrs.put("BUILD_TOKEN_MANAGER", buildTokenManager ? Boolean.TRUE : Boolean.FALSE);
    }
    
    public void setSanitycheck(final boolean sanityCheck) {
        this.optionalAttrs.put("SANITY_CHECK", sanityCheck ? Boolean.TRUE : Boolean.FALSE);
    }
    
    public void setForcelacheck(final boolean forceLACheck) {
        this.optionalAttrs.put("FORCE_LA_CHECK", forceLACheck ? Boolean.TRUE : Boolean.FALSE);
    }
    
    public void setCachetokens(final boolean cacheTokens) {
        this.optionalAttrs.put("CACHE_TOKENS", cacheTokens ? Boolean.TRUE : Boolean.FALSE);
    }
    
    public void setKeeplinecolumn(final boolean keepLineColumn) {
        this.optionalAttrs.put("KEEP_LINE_COLUMN", keepLineColumn ? Boolean.TRUE : Boolean.FALSE);
    }
    
    public void setJDKversion(final String jdkVersion) {
        this.optionalAttrs.put("JDK_VERSION", jdkVersion);
    }
    
    public void setOutputdirectory(final File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }
    
    public void setTarget(final File targetFile) {
        this.targetFile = targetFile;
    }
    
    public void setJavacchome(final File javaccHome) {
        this.javaccHome = javaccHome;
    }
    
    public void setMaxmemory(final String max) {
        this.maxMemory = max;
    }
    
    public JavaCC() {
        this.optionalAttrs = new Hashtable();
        this.outputDirectory = null;
        this.targetFile = null;
        this.javaccHome = null;
        this.cmdl = new CommandlineJava();
        this.maxMemory = null;
        this.cmdl.setVm(JavaEnvUtils.getJreExecutable("java"));
    }
    
    @Override
    public void execute() throws BuildException {
        final Enumeration iter = this.optionalAttrs.keys();
        while (iter.hasMoreElements()) {
            final String name = iter.nextElement();
            final Object value = this.optionalAttrs.get(name);
            this.cmdl.createArgument().setValue("-" + name + ":" + value.toString());
        }
        if (this.targetFile == null || !this.targetFile.isFile()) {
            throw new BuildException("Invalid target: " + this.targetFile);
        }
        if (this.outputDirectory == null) {
            this.outputDirectory = new File(this.targetFile.getParent());
        }
        else if (!this.outputDirectory.isDirectory()) {
            throw new BuildException("Outputdir not a directory.");
        }
        this.cmdl.createArgument().setValue("-OUTPUT_DIRECTORY:" + this.outputDirectory.getAbsolutePath());
        final File javaFile = this.getOutputJavaFile(this.outputDirectory, this.targetFile);
        if (javaFile.exists() && this.targetFile.lastModified() < javaFile.lastModified()) {
            this.log("Target is already built - skipping (" + this.targetFile + ")", 3);
            return;
        }
        this.cmdl.createArgument().setValue(this.targetFile.getAbsolutePath());
        final Path classpath = this.cmdl.createClasspath(this.getProject());
        final File javaccJar = getArchiveFile(this.javaccHome);
        classpath.createPathElement().setPath(javaccJar.getAbsolutePath());
        classpath.addJavaRuntime();
        this.cmdl.setClassname(getMainClass(classpath, 1));
        this.cmdl.setMaxmemory(this.maxMemory);
        final Commandline.Argument arg = this.cmdl.createVmArgument();
        arg.setValue("-Dinstall.root=" + this.javaccHome.getAbsolutePath());
        Execute.runCommand(this, this.cmdl.getCommandline());
    }
    
    protected static File getArchiveFile(final File home) throws BuildException {
        return new File(home, JavaCC.ARCHIVE_LOCATIONS[getArchiveLocationIndex(home)]);
    }
    
    protected static String getMainClass(final File home, final int type) throws BuildException {
        final Path p = new Path(null);
        p.createPathElement().setLocation(getArchiveFile(home));
        p.addJavaRuntime();
        return getMainClass(p, type);
    }
    
    protected static String getMainClass(final Path path, final int type) throws BuildException {
        String packagePrefix = null;
        String mainClass = null;
        AntClassLoader l = null;
        try {
            l = AntClassLoader.newAntClassLoader(null, null, path.concatSystemClasspath("ignore"), true);
            String javaccClass = "COM.sun.labs.javacc.Main";
            InputStream is = l.getResourceAsStream(javaccClass.replace('.', '/') + ".class");
            if (is != null) {
                packagePrefix = "COM.sun.labs.";
                switch (type) {
                    case 0: {
                        mainClass = "javacc.Main";
                        break;
                    }
                    case 1: {
                        mainClass = "jjtree.Main";
                        break;
                    }
                    case 2: {
                        mainClass = "jjdoc.JJDocMain";
                        break;
                    }
                }
            }
            else {
                javaccClass = "org.javacc.parser.Main";
                is = l.getResourceAsStream(javaccClass.replace('.', '/') + ".class");
                if (is != null) {
                    packagePrefix = "org.javacc.";
                }
                else {
                    javaccClass = "org.netbeans.javacc.parser.Main";
                    is = l.getResourceAsStream(javaccClass.replace('.', '/') + ".class");
                    if (is != null) {
                        packagePrefix = "org.netbeans.javacc.";
                    }
                }
                if (is != null) {
                    switch (type) {
                        case 0: {
                            mainClass = "parser.Main";
                            break;
                        }
                        case 1: {
                            mainClass = "jjtree.Main";
                            break;
                        }
                        case 2: {
                            mainClass = "jjdoc.JJDocMain";
                            break;
                        }
                    }
                }
            }
            if (packagePrefix == null) {
                throw new BuildException("failed to load JavaCC");
            }
            if (mainClass == null) {
                throw new BuildException("unknown task type " + type);
            }
            return packagePrefix + mainClass;
        }
        finally {
            if (l != null) {
                l.cleanup();
            }
        }
    }
    
    private static int getArchiveLocationIndex(final File home) throws BuildException {
        if (home == null || !home.isDirectory()) {
            throw new BuildException("JavaCC home must be a valid directory.");
        }
        for (int i = 0; i < JavaCC.ARCHIVE_LOCATIONS.length; ++i) {
            final File f = new File(home, JavaCC.ARCHIVE_LOCATIONS[i]);
            if (f.exists()) {
                return i;
            }
        }
        throw new BuildException("Could not find a path to JavaCC.zip or javacc.jar from '" + home + "'.");
    }
    
    protected static int getMajorVersionNumber(final File home) throws BuildException {
        return JavaCC.ARCHIVE_LOCATIONS_VS_MAJOR_VERSION[getArchiveLocationIndex(home)];
    }
    
    private File getOutputJavaFile(final File outputdir, final File srcfile) {
        String path = srcfile.getPath();
        final int startBasename = path.lastIndexOf(File.separator);
        if (startBasename != -1) {
            path = path.substring(startBasename + 1);
        }
        final int startExtn = path.lastIndexOf(46);
        if (startExtn != -1) {
            path = path.substring(0, startExtn) + ".java";
        }
        else {
            path += ".java";
        }
        if (outputdir != null) {
            path = outputdir + File.separator + path;
        }
        return new File(path);
    }
    
    static {
        ARCHIVE_LOCATIONS = new String[] { "JavaCC.zip", "bin/lib/JavaCC.zip", "bin/lib/javacc.jar", "javacc.jar" };
        ARCHIVE_LOCATIONS_VS_MAJOR_VERSION = new int[] { 1, 2, 3, 3 };
    }
}
