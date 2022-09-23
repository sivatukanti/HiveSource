// 
// Decompiled by Procyon v0.5.36
// 

package antlr;

public class Utils
{
    private static boolean useSystemExit;
    private static boolean useDirectClassLoading;
    
    public static Class loadClass(final String className) throws ClassNotFoundException {
        try {
            final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
            if (!Utils.useDirectClassLoading && contextClassLoader != null) {
                return contextClassLoader.loadClass(className);
            }
            return Class.forName(className);
        }
        catch (Exception ex) {
            return Class.forName(className);
        }
    }
    
    public static Object createInstanceOf(final String s) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        return loadClass(s).newInstance();
    }
    
    public static void error(final String str) {
        if (Utils.useSystemExit) {
            System.exit(1);
        }
        throw new RuntimeException("ANTLR Panic: " + str);
    }
    
    public static void error(final String s, final Throwable cause) {
        if (Utils.useSystemExit) {
            System.exit(1);
        }
        throw new RuntimeException("ANTLR Panic", cause);
    }
    
    static {
        Utils.useSystemExit = true;
        Utils.useDirectClassLoading = false;
        if ("true".equalsIgnoreCase(System.getProperty("ANTLR_DO_NOT_EXIT", "false"))) {
            Utils.useSystemExit = false;
        }
        if ("true".equalsIgnoreCase(System.getProperty("ANTLR_USE_DIRECT_CLASS_LOADING", "false"))) {
            Utils.useDirectClassLoading = true;
        }
    }
}
