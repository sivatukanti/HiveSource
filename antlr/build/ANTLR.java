// 
// Decompiled by Procyon v0.5.36
// 

package antlr.build;

import java.io.FilenameFilter;
import java.io.File;

public class ANTLR
{
    public static String compiler;
    public static String jarName;
    public static String root;
    public static String[] srcdir;
    
    public ANTLR() {
        ANTLR.compiler = System.getProperty("antlr.build.compiler", ANTLR.compiler);
        ANTLR.root = System.getProperty("antlr.build.root", ANTLR.root);
    }
    
    public String getName() {
        return "ANTLR";
    }
    
    public void build(final Tool tool) {
        if (!this.rootIsValidANTLRDir(tool)) {
            return;
        }
        tool.antlr(ANTLR.root + "/antlr/antlr.g");
        tool.antlr(ANTLR.root + "/antlr/tokdef.g");
        tool.antlr(ANTLR.root + "/antlr/preprocessor/preproc.g");
        tool.antlr(ANTLR.root + "/antlr/actions/java/action.g");
        tool.antlr(ANTLR.root + "/antlr/actions/cpp/action.g");
        tool.antlr(ANTLR.root + "/antlr/actions/csharp/action.g");
        for (int i = 0; i < ANTLR.srcdir.length; ++i) {
            tool.system(ANTLR.compiler + " -d " + ANTLR.root + " " + ANTLR.root + "/" + ANTLR.srcdir[i] + "/*.java");
        }
    }
    
    public void jar(final Tool tool) {
        if (!this.rootIsValidANTLRDir(tool)) {
            return;
        }
        final StringBuffer sb = new StringBuffer(2000);
        sb.append("jar cvf " + ANTLR.root + "/" + ANTLR.jarName);
        for (int i = 0; i < ANTLR.srcdir.length; ++i) {
            sb.append(" " + ANTLR.root + "/" + ANTLR.srcdir[i] + "/*.class");
        }
        tool.system(sb.toString());
    }
    
    protected boolean rootIsValidANTLRDir(final Tool tool) {
        if (ANTLR.root == null) {
            return false;
        }
        final File file = new File(ANTLR.root);
        if (!file.exists()) {
            tool.error("Property antlr.build.root==" + ANTLR.root + " does not exist");
            return false;
        }
        if (!file.isDirectory()) {
            tool.error("Property antlr.build.root==" + ANTLR.root + " is not a directory");
            return false;
        }
        final String[] list = file.list(new FilenameFilter() {
            public boolean accept(final File file, final String s) {
                return file.isDirectory() && s.equals("antlr");
            }
        });
        if (list == null || list.length == 0) {
            tool.error("Property antlr.build.root==" + ANTLR.root + " does not appear to be a valid ANTLR project root (no antlr subdir)");
            return false;
        }
        final String[] list2 = new File(ANTLR.root + "/antlr").list();
        if (list2 == null || list2.length == 0) {
            tool.error("Property antlr.build.root==" + ANTLR.root + " does not appear to be a valid ANTLR project root (no .java files in antlr subdir");
            return false;
        }
        return true;
    }
    
    static {
        ANTLR.compiler = "javac";
        ANTLR.jarName = "antlr.jar";
        ANTLR.root = ".";
        ANTLR.srcdir = new String[] { "antlr", "antlr/actions/cpp", "antlr/actions/java", "antlr/actions/csharp", "antlr/collections", "antlr/collections/impl", "antlr/debug", "antlr/ASdebug", "antlr/debug/misc", "antlr/preprocessor" };
    }
}
