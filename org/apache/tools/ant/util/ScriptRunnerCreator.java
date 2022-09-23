// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.util;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

public class ScriptRunnerCreator
{
    private static final String AUTO = "auto";
    private static final String OATAU = "org.apache.tools.ant.util";
    private static final String UTIL_OPT = "org.apache.tools.ant.util.optional";
    private static final String BSF = "bsf";
    private static final String BSF_PACK = "org.apache.bsf";
    private static final String BSF_MANAGER = "org.apache.bsf.BSFManager";
    private static final String BSF_RUNNER = "org.apache.tools.ant.util.optional.ScriptRunner";
    private static final String JAVAX = "javax";
    private static final String JAVAX_MANAGER = "javax.script.ScriptEngineManager";
    private static final String JAVAX_RUNNER = "org.apache.tools.ant.util.optional.JavaxScriptRunner";
    private Project project;
    private String manager;
    private String language;
    private ClassLoader scriptLoader;
    
    public ScriptRunnerCreator(final Project project) {
        this.scriptLoader = null;
        this.project = project;
    }
    
    public synchronized ScriptRunnerBase createRunner(final String manager, final String language, final ClassLoader classLoader) {
        this.manager = manager;
        this.language = language;
        this.scriptLoader = classLoader;
        if (language == null) {
            throw new BuildException("script language must be specified");
        }
        if (!manager.equals("auto") && !manager.equals("javax") && !manager.equals("bsf")) {
            throw new BuildException("Unsupported language prefix " + manager);
        }
        ScriptRunnerBase ret = null;
        ret = this.createRunner("bsf", "org.apache.bsf.BSFManager", "org.apache.tools.ant.util.optional.ScriptRunner");
        if (ret == null) {
            ret = this.createRunner("javax", "javax.script.ScriptEngineManager", "org.apache.tools.ant.util.optional.JavaxScriptRunner");
        }
        if (ret != null) {
            return ret;
        }
        if ("javax".equals(manager)) {
            throw new BuildException("Unable to load the script engine manager (javax.script.ScriptEngineManager)");
        }
        if ("bsf".equals(manager)) {
            throw new BuildException("Unable to load the BSF script engine manager (org.apache.bsf.BSFManager)");
        }
        throw new BuildException("Unable to load a script engine manager (org.apache.bsf.BSFManager or javax.script.ScriptEngineManager)");
    }
    
    private ScriptRunnerBase createRunner(final String checkManager, final String managerClass, final String runnerClass) {
        ScriptRunnerBase runner = null;
        if (!this.manager.equals("auto") && !this.manager.equals(checkManager)) {
            return null;
        }
        if (this.scriptLoader.getResource(LoaderUtils.classNameToResource(managerClass)) == null) {
            return null;
        }
        if (managerClass.equals("org.apache.bsf.BSFManager")) {
            new ScriptFixBSFPath().fixClassLoader(this.scriptLoader, this.language);
        }
        try {
            runner = (ScriptRunnerBase)Class.forName(runnerClass, true, this.scriptLoader).newInstance();
            runner.setProject(this.project);
        }
        catch (Exception ex) {
            throw ReflectUtil.toBuildException(ex);
        }
        runner.setLanguage(this.language);
        runner.setScriptClassLoader(this.scriptLoader);
        return runner;
    }
}
