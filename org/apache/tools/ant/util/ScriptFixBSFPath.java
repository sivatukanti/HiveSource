// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.util;

import java.util.HashMap;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.AntClassLoader;
import java.io.File;
import java.util.Map;

public class ScriptFixBSFPath
{
    private static final String UTIL_OPTIONAL_PACKAGE = "org.apache.tools.ant.util.optional";
    private static final String BSF_PACKAGE = "org.apache.bsf";
    private static final String BSF_MANAGER = "org.apache.bsf.BSFManager";
    private static final String BSF_SCRIPT_RUNNER = "org.apache.tools.ant.util.optional.ScriptRunner";
    private static final String[] BSF_LANGUAGES;
    private static final Map BSF_LANGUAGE_MAP;
    
    private File getClassSource(final ClassLoader loader, final String className) {
        return LoaderUtils.getResourceSource(loader, LoaderUtils.classNameToResource(className));
    }
    
    private File getClassSource(final String className) {
        return this.getClassSource(this.getClass().getClassLoader(), className);
    }
    
    public void fixClassLoader(final ClassLoader loader, final String language) {
        if (loader == this.getClass().getClassLoader() || !(loader instanceof AntClassLoader)) {
            return;
        }
        final ClassLoader myLoader = this.getClass().getClassLoader();
        final AntClassLoader fixLoader = (AntClassLoader)loader;
        File bsfSource = this.getClassSource("org.apache.bsf.BSFManager");
        boolean needMoveRunner = bsfSource == null;
        final String languageClassName = ScriptFixBSFPath.BSF_LANGUAGE_MAP.get(language);
        final boolean needMoveBsf = bsfSource != null && languageClassName != null && !LoaderUtils.classExists(myLoader, languageClassName) && LoaderUtils.classExists(loader, languageClassName);
        needMoveRunner = (needMoveRunner || needMoveBsf);
        if (bsfSource == null) {
            bsfSource = this.getClassSource(loader, "org.apache.bsf.BSFManager");
        }
        if (bsfSource == null) {
            throw new BuildException("Unable to find BSF classes for scripting");
        }
        if (needMoveBsf) {
            fixLoader.addPathComponent(bsfSource);
            fixLoader.addLoaderPackageRoot("org.apache.bsf");
        }
        if (needMoveRunner) {
            fixLoader.addPathComponent(LoaderUtils.getResourceSource(fixLoader, LoaderUtils.classNameToResource("org.apache.tools.ant.util.optional.ScriptRunner")));
            fixLoader.addLoaderPackageRoot("org.apache.tools.ant.util.optional");
        }
    }
    
    static {
        BSF_LANGUAGES = new String[] { "js", "org.mozilla.javascript.Scriptable", "javascript", "org.mozilla.javascript.Scriptable", "jacl", "tcl.lang.Interp", "netrexx", "netrexx.lang.Rexx", "nrx", "netrexx.lang.Rexx", "jython", "org.python.core.Py", "py", "org.python.core.Py", "xslt", "org.apache.xpath.objects.XObject" };
        BSF_LANGUAGE_MAP = new HashMap();
        for (int i = 0; i < ScriptFixBSFPath.BSF_LANGUAGES.length; i += 2) {
            ScriptFixBSFPath.BSF_LANGUAGE_MAP.put(ScriptFixBSFPath.BSF_LANGUAGES[i], ScriptFixBSFPath.BSF_LANGUAGES[i + 1]);
        }
    }
}
