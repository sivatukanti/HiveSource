// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.tools.sysinfo;

import java.security.CodeSource;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.io.FileInputStream;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.Vector;
import java.io.OutputStream;
import java.net.URL;
import org.apache.derby.iapi.services.info.ProductVersionHolder;
import java.io.InputStream;
import java.util.Properties;
import java.util.Arrays;
import java.util.Locale;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.io.File;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.io.IOException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.apache.derby.iapi.tools.i18n.LocalizedOutput;
import java.io.PrintWriter;
import org.apache.derby.iapi.tools.i18n.LocalizedResource;

public final class Main
{
    private static final String MESSAGE_FILE = "org.apache.derby.loc.sysinfoMessages";
    private static final LocalizedResource LOCALIZED_RESOURCE;
    private static boolean setPause;
    private static boolean setLicense;
    private static boolean cptester;
    private static final String sep = "------------------------------------------------------";
    private static final String javaSep;
    private static final String jbmsSep;
    private static final String licSep;
    private static final String locSep;
    private static final String curLoc;
    private static final String EMBEDDED = "embedded";
    private static final String TOOLS = "tools";
    private static final String NET = "server";
    private static final String CLIENT = "client";
    private static final String MAINUSAGESTRING = "java org.apache.derby.tools.sysinfo -cp";
    private static final String USAGESTRINGPARTA = "java org.apache.derby.tools.sysinfo -cp [ [ embedded ][ server ][ client] [ tools ] [";
    private static final String USAGESTRINGPARTB = ".class ] ]";
    private static final String[] infoNames;
    
    public static void main(final String[] array) {
        LocalizedResource.getInstance().init();
        final LocalizedOutput outputWriter = LocalizedResource.OutputWriter();
        parseArgs(array);
        if (Main.cptester) {
            getClasspathInfo(array, outputWriter);
        }
        else {
            getMainInfo(outputWriter, Main.setPause);
        }
    }
    
    public static void getMainInfo(final PrintWriter printWriter, final boolean b) {
        printWriter.println(Main.javaSep);
        reportJavaInfo(printWriter);
        printWriter.println(Main.jbmsSep);
        reportDerby(printWriter);
        printWriter.println("------------------------------------------------------");
        try {
            reportLocales(printWriter);
        }
        catch (Exception ex2) {
            printWriter.println(getTextMessage("SIF01.Q"));
            printWriter.println(getTextMessage("SIF01.B"));
        }
        try {
            reportTesting(printWriter);
        }
        catch (Exception ex) {
            printWriter.println("Exception in reporting version of derbyTesting.jar");
            ex.printStackTrace();
        }
        if (b) {
            pause();
        }
    }
    
    private static void parseArgs(final String[] array) {
        if (array == null) {
            return;
        }
        for (int i = 0; i < array.length; ++i) {
            if (array[i].equals("-pause")) {
                Main.setPause = true;
            }
            if (array[i].equals("-cp")) {
                Main.cptester = true;
            }
        }
    }
    
    private static void pause() {
        try {
            System.out.print(getTextMessage("SIF01.C"));
            new BufferedReader(new InputStreamReader(System.in)).readLine();
        }
        catch (IOException ex) {}
    }
    
    private static void reportDerby(final PrintWriter printWriter) {
        String s;
        try {
            s = AccessController.doPrivileged((PrivilegedAction<String>)new PrivilegedAction() {
                public Object run() {
                    return System.getProperty("java.class.path");
                }
            });
        }
        catch (SecurityException ex) {
            printWriter.println(getTextMessage("SIF01.U", ex.getMessage()));
            s = null;
        }
        final ZipInfoProperties[] allInfo = getAllInfo(s);
        if (allInfo != null) {
            for (int i = 0; i < allInfo.length; ++i) {
                printWriter.println("[" + allInfo[i].getLocation() + "] " + allInfo[i].getVersionBuildInfo());
            }
        }
        else {
            printWriter.println(getTextMessage("SIF01.D"));
        }
    }
    
    private static void reportJavaInfo(final PrintWriter printWriter) {
        printWriter.println(getTextMessage("SIF02.A", getJavaProperty("java.version")));
        printWriter.println(getTextMessage("SIF02.B", getJavaProperty("java.vendor")));
        printWriter.println(getTextMessage("SIF02.C", getJavaProperty("java.home")));
        printWriter.println(getTextMessage("SIF02.D", getJavaProperty("java.class.path")));
        printWriter.println(getTextMessage("SIF02.E", getJavaProperty("os.name")));
        printWriter.println(getTextMessage("SIF02.F", getJavaProperty("os.arch")));
        printWriter.println(getTextMessage("SIF02.G", getJavaProperty("os.version")));
        printWriter.println(getTextMessage("SIF02.H", getJavaProperty("user.name")));
        printWriter.println(getTextMessage("SIF02.I", getJavaProperty("user.home")));
        printWriter.println(getTextMessage("SIF02.J", getJavaProperty("user.dir")));
        printWriter.println("java.specification.name: " + getJavaProperty("java.specification.name"));
        printWriter.println("java.specification.version: " + getJavaProperty("java.specification.version"));
        printPropertyIfNotNull(printWriter, "java.runtime.version");
        printPropertyIfNotNull(printWriter, "java.fullversion");
    }
    
    private static void printPropertyIfNotNull(final PrintWriter printWriter, final String str) {
        final String javaProperty = getJavaProperty(str, true);
        if (javaProperty != null) {
            printWriter.println(str + ": " + javaProperty);
        }
    }
    
    private static String getJavaProperty(final String s) {
        return getJavaProperty(s, false);
    }
    
    private static String getJavaProperty(final String s, final boolean b) {
        final String s2 = b ? null : getTextMessage("SIF01.H");
        try {
            return AccessController.doPrivileged((PrivilegedAction<String>)new PrivilegedAction() {
                public Object run() {
                    return System.getProperty(s, s2);
                }
            });
        }
        catch (SecurityException ex) {
            return getTextMessage("SIF01.I", ex);
        }
    }
    
    private static String getCanonicalPath(final File file) throws IOException {
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<String>)new PrivilegedExceptionAction() {
                public Object run() throws IOException {
                    return file.getCanonicalPath();
                }
            });
        }
        catch (PrivilegedActionException ex) {
            throw (IOException)ex.getCause();
        }
        catch (SecurityException ex2) {
            return getTextMessage("SIF01.I", ex2);
        }
    }
    
    private static void getClasspathInfo(final String[] array, final PrintWriter printWriter) {
        useMe(array, printWriter);
    }
    
    private static void reportLocales(final PrintWriter printWriter) {
        int n = 1;
        printWriter.println(Main.locSep);
        final Locale[] availableLocales = Locale.getAvailableLocales();
        final String[] a = new String[availableLocales.length];
        for (int i = 0; i < availableLocales.length; ++i) {
            a[i] = availableLocales[i].toString();
        }
        Arrays.sort(a);
        final Properties properties = new Properties();
        for (int j = 0; j < a.length; ++j) {
            final String string = "/org/apache/derby/info/locale_" + a[j] + ".properties";
            final Properties properties2 = properties;
            final String s = string;
            try {
                final InputStream inputStream = AccessController.doPrivileged((PrivilegedAction<InputStream>)new PrivilegedAction() {
                    public Object run() {
                        return properties2.getClass().getResourceAsStream(s);
                    }
                });
                if (inputStream != null) {
                    try {
                        properties.clear();
                        properties.load(inputStream);
                        if (n != 0) {
                            final Locale default1 = Locale.getDefault();
                            printWriter.println(getTextMessage("SIF01.T") + "  [" + default1.getDisplayLanguage() + "/" + default1.getDisplayCountry() + " [" + default1 + "]]");
                            n = 0;
                        }
                        final String property = properties.getProperty("derby.locale.external.name");
                        final String substring = property.substring(property.indexOf("[") + 1);
                        printWriter.println(getTextMessage("SIF01.R", substring.substring(0, substring.indexOf("]"))));
                        printWriter.println(getTextMessage("SIF01.S", ProductVersionHolder.fullVersionString(Integer.parseInt(properties.getProperty("derby.locale.version.major")), Integer.parseInt(properties.getProperty("derby.locale.version.minor")), Integer.parseInt(properties.getProperty("derby.locale.version.maint")), false, properties.getProperty("derby.locale.build.number"))));
                    }
                    catch (IOException ex) {
                        printWriter.println("Could not get locale properties from : " + inputStream);
                    }
                }
            }
            catch (Throwable obj) {
                printWriter.println("Could not load resource: " + string);
                printWriter.println("Exception: " + obj);
            }
        }
        printWriter.println("------------------------------------------------------");
    }
    
    private static void reportTesting(final PrintWriter printWriter) {
        final String x = "org.apache.derbyTesting.*:";
        final Properties properties = new Properties();
        final String str = "/org/apache/derby/info/tsting.properties";
        final Properties properties2 = properties;
        final String s = str;
        try {
            final InputStream inputStream = AccessController.doPrivileged((PrivilegedAction<InputStream>)new PrivilegedAction() {
                public Object run() {
                    return properties2.getClass().getResourceAsStream(s);
                }
            });
            if (inputStream != null) {
                try {
                    properties.clear();
                    properties.load(inputStream);
                    final StringBuffer sb = new StringBuffer(getTextMessage(crLf()));
                    tryTstingClasspath(sb, new StringBuffer(crLf() + getTextMessage("SIF08.E") + crLf()));
                    final String string = sb.toString();
                    if (string == null || string.length() <= 2) {
                        return;
                    }
                    printWriter.println(x);
                    printWriter.print("\t ");
                    printWriter.print("[");
                    printWriter.print(formatURL(new URL(string)));
                    printWriter.println("]");
                    printWriter.println(getTextMessage("SIF01.S", ProductVersionHolder.fullVersionString(Integer.parseInt(properties.getProperty("derby.tsting.version.major")), Integer.parseInt(properties.getProperty("derby.tsting.version.minor")), Integer.parseInt(properties.getProperty("derby.tsting.version.maint")), false, properties.getProperty("derby.tsting.build.number"))));
                }
                catch (IOException ex) {
                    printWriter.println("Could not get testing properties from : " + inputStream);
                }
            }
            printWriter.println("------------------------------------------------------");
        }
        catch (Throwable obj) {
            printWriter.println("Could not load resource: " + str);
            printWriter.println("Exception: " + obj);
        }
    }
    
    static void useMe(final String[] array, final PrintWriter printWriter) {
        PrintWriter printWriter2 = printWriter;
        if (printWriter2 == null) {
            printWriter2 = new PrintWriter(System.out);
        }
        if (array.length == 1) {
            try {
                tryAllClasspaths(printWriter2);
            }
            catch (Throwable t) {}
        }
        else {
            try {
                trySomeClasspaths(array, printWriter2);
            }
            catch (Throwable t2) {}
        }
    }
    
    private static void tryAllClasspaths(final PrintWriter printWriter) throws Throwable {
        printWriter.println(getTextMessage("SIF08.B"));
        printWriter.println(getTextMessage("SIF08.C", "java org.apache.derby.tools.sysinfo -cp args"));
        final StringBuffer sb = new StringBuffer(getTextMessage("SIF08.D") + crLf());
        final StringBuffer sb2 = new StringBuffer(crLf() + getTextMessage("SIF08.E") + crLf());
        tryCoreClasspath(sb, sb2);
        tryNetClasspath(sb, sb2);
        tryClientClasspath(sb, sb2);
        tryUtilsClasspath(sb, sb2);
        printWriter.println(sb.toString());
        if (!sb2.toString().equals(crLf() + getTextMessage("SIF08.E") + crLf())) {
            printWriter.println(sb2.toString());
        }
        else {
            printWriter.println(getTextMessage("SIF08.F"));
        }
        printWriter.flush();
    }
    
    private static void trySomeClasspaths(final String[] array, final PrintWriter printWriter) throws Throwable {
        boolean b = false;
        final StringBuffer sb = new StringBuffer(getTextMessage("SIF08.D") + crLf());
        final StringBuffer sb2 = new StringBuffer(crLf() + getTextMessage("SIF08.E") + crLf());
        if (argumentsContain(array, "embedded")) {
            tryCoreClasspath(sb, sb2);
            b = true;
        }
        if (argumentsContain(array, "server")) {
            tryNetClasspath(sb, sb2);
            b = true;
        }
        if (argumentsContain(array, "client")) {
            tryClientClasspath(sb, sb2);
            b = true;
        }
        if (argumentsContain(array, "tools") || argumentsContain(array, "utils")) {
            tryUtilsClasspath(sb, sb2);
            b = true;
        }
        final String argumentMatches = argumentMatches(array, ".class");
        if (!argumentMatches.equals("")) {
            tryMyClasspath(argumentMatches(array, ".class"), getTextMessage("SIF08.H", argumentMatches), sb, sb2);
            b = true;
        }
        if (b) {
            printWriter.println(sb.toString());
            if (!sb2.toString().equals(crLf() + getTextMessage("SIF08.E") + crLf())) {
                printWriter.println(sb2.toString());
            }
            else {
                printWriter.println(getTextMessage("SIF08.F"));
            }
        }
        else {
            printWriter.println(getTextMessage("SIF08.A", "java org.apache.derby.tools.sysinfo -cp [ [ embedded ][ server ][ client] [ tools ] [", ".class ] ]"));
        }
        printWriter.flush();
    }
    
    private static void tryCoreClasspath(final StringBuffer sb, final StringBuffer sb2) {
        tryMyClasspath("org.apache.derby.database.Database", getTextMessage("SIF08.J", "derby.jar"), sb, sb2);
    }
    
    private static void tryNetClasspath(final StringBuffer sb, final StringBuffer sb2) {
        tryMyClasspath("org.apache.derby.database.Database", getTextMessage("SIF08.J", "derby.jar"), sb, sb2);
        tryMyClasspath("org.apache.derby.drda.NetworkServerControl", getTextMessage("SIF08.I", "derbynet.jar"), sb, sb2);
    }
    
    private static void tryClientClasspath(final StringBuffer sb, final StringBuffer sb2) {
        tryMyClasspath("org.apache.derby.jdbc.ClientDriver", getTextMessage("SIF08.L", "derbyclient.jar"), sb, sb2);
    }
    
    private static void tryUtilsClasspath(final StringBuffer sb, final StringBuffer sb2) {
        tryMyClasspath("org.apache.derby.tools.ij", getTextMessage("SIF08.Q", "derbytools.jar"), sb, sb2);
    }
    
    private static void tryTstingClasspath(final StringBuffer sb, final StringBuffer sb2) {
        tryMyClasspath("org.apache.derbyTesting.junit.BaseTestCase", "", sb, sb2);
    }
    
    private static void tryMyClasspath(final String className, final String s, final StringBuffer sb, final StringBuffer sb2) {
        try {
            sb.append(found(className, s, getFileWhichLoadedClass(Class.forName(className))));
        }
        catch (Throwable t) {
            sb2.append(notFound(className, s));
        }
    }
    
    private static void tryAsResource(final String name, final String s, final StringBuffer sb, final StringBuffer sb2) {
        try {
            name.getClass().getResourceAsStream(name).close();
            sb.append(found(name, s, getFileWhichLoadedClass(name.getClass())));
        }
        catch (Throwable t) {
            sb2.append(notFound(name, s));
        }
    }
    
    private static String found(final String s, final String str, final String str2) {
        final StringBuffer sb = new StringBuffer(crLf());
        sb.append("   " + str);
        sb.append(crLf());
        if (str2 != null) {
            sb.append("   ").append(str2).append(crLf());
        }
        sb.append(crLf());
        return sb.toString();
    }
    
    private static String notFound(final String s, final String str) {
        final StringBuffer sb = new StringBuffer(crLf());
        sb.append("   " + str);
        sb.append(crLf());
        sb.append("    " + getTextMessage("SIF08.U", s));
        sb.append(crLf());
        sb.append(crLf());
        return sb.toString();
    }
    
    private static String crLf() {
        return System.getProperty("line.separator");
    }
    
    private static String lookForMainArg(final String[] array, final PrintWriter printWriter) {
        final int length = array.length;
        final String[] array2 = { "embedded" };
        int n = 0;
        String s = "";
        for (int i = 0; i < length; ++i) {
            for (int j = 0; j < array2.length; ++j) {
                if (array[i].toUpperCase(Locale.ENGLISH).equals(array2[j].toUpperCase(Locale.ENGLISH))) {
                    ++n;
                    s = array2[j];
                }
            }
        }
        if (n > 1 || n < 1) {
            printWriter.println(getTextMessage("SIF08.A", "java org.apache.derby.tools.sysinfo -cp [ [ embedded ][ server ][ client] [ tools ] [", ".class ] ]"));
            return "";
        }
        return s;
    }
    
    private static boolean argumentsContain(final String[] array, final String anotherString) {
        for (int i = 0; i < array.length; ++i) {
            if (array[i].equalsIgnoreCase(anotherString)) {
                return true;
            }
        }
        return false;
    }
    
    private static String argumentMatches(final String[] array, final String suffix) {
        String substring = "";
        for (int length = array.length, i = 0; i < length; ++i) {
            if (array[i].endsWith(suffix)) {
                substring = array[i].substring(0, array[i].length() - 6);
            }
        }
        return substring;
    }
    
    public static ZipInfoProperties[] getAllInfo(final String s) {
        ZipInfoProperties[] loadZipFromResource = loadZipFromResource();
        if (loadZipFromResource == null) {
            loadZipFromResource = new ZipInfoProperties[] { null };
            final ZipInfoProperties zipInfoProperties = new ZipInfoProperties(ProductVersionHolder.getProductVersionHolderFromMyEnv("tools"));
            zipInfoProperties.setLocation(getFileWhichLoadedClass(new Main().getClass()));
            loadZipFromResource[0] = zipInfoProperties;
        }
        try {
            if (s != null) {
                final String[] classpath = parseClasspath(s);
                final Vector vector = new Vector<ZipInfoProperties>();
                for (int i = 0; i < classpath.length; ++i) {
                    ZipInfoProperties checkForInfo;
                    try {
                        checkForInfo = checkForInfo(classpath[i]);
                    }
                    catch (SecurityException ex) {
                        checkForInfo = new ZipInfoProperties(null);
                        checkForInfo.setLocation(getTextMessage("SIF03.C", ex.getMessage()));
                    }
                    if (checkForInfo != null) {
                        vector.addElement(checkForInfo);
                    }
                }
                if (vector.size() > 0) {
                    final ZipInfoProperties[] anArray = new ZipInfoProperties[vector.size()];
                    vector.copyInto(anArray);
                    return mergeZips(loadZipFromResource, anArray);
                }
            }
            return mergeZips(loadZipFromResource, null);
        }
        catch (SecurityException ex2) {
            final ZipInfoProperties[] array = { new ZipInfoProperties(null) };
            array[0].setLocation(getTextMessage("SIF03.C", ex2.getMessage()));
            return array;
        }
    }
    
    private static ZipInfoProperties[] loadZipFromResource() {
        final ArrayList list = new ArrayList<ZipInfoProperties>();
        for (int i = 0; i < Main.infoNames.length; ++i) {
            final String concat = "/".concat(Main.infoNames[i]);
            final InputStream inputStream = AccessController.doPrivileged((PrivilegedAction<InputStream>)new PrivilegedAction() {
                public Object run() {
                    return new Main().getClass().getResourceAsStream(concat);
                }
            });
            if (inputStream != null) {
                final ZipInfoProperties e = new ZipInfoProperties(ProductVersionHolder.getProductVersionHolderFromMyEnv(inputStream));
                e.setLocation(formatURL(AccessController.doPrivileged((PrivilegedAction<URL>)new PrivilegedAction() {
                    public Object run() {
                        return new Main().getClass().getResource(concat);
                    }
                })));
                list.add(e);
            }
        }
        if (list.size() == 0) {
            return null;
        }
        final ZipInfoProperties[] a = new ZipInfoProperties[list.size()];
        list.toArray(a);
        return a;
    }
    
    private static String[] parseClasspath(final String str) {
        final StringTokenizer stringTokenizer = new StringTokenizer(str, File.pathSeparator);
        final int countTokens = stringTokenizer.countTokens();
        if (countTokens == 0) {
            return null;
        }
        final String[] array = new String[countTokens];
        for (int i = 0; i < countTokens; ++i) {
            array[i] = stringTokenizer.nextToken();
        }
        return array;
    }
    
    private static ZipInfoProperties checkForInfo(final String s) {
        return AccessController.doPrivileged((PrivilegedAction<ZipInfoProperties>)new PrivilegedAction() {
            public Object run() {
                final File file = new File(s);
                if (!file.exists()) {
                    return null;
                }
                if (file.isDirectory()) {
                    return checkDirectory(s);
                }
                if (file.isFile()) {
                    return checkFile(s);
                }
                return null;
            }
        });
    }
    
    private static ZipInfoProperties checkDirectory(final String s) {
        boolean b = false;
        File file = null;
        for (int i = 0; i < Main.infoNames.length; ++i) {
            file = new File(s, Main.infoNames[i].replace('/', File.separatorChar));
            if (file.exists()) {
                b = true;
                break;
            }
        }
        if (!b || file == null) {
            return null;
        }
        try {
            final ZipInfoProperties zipInfoProperties = new ZipInfoProperties(ProductVersionHolder.getProductVersionHolderFromMyEnv(new FileInputStream(file)));
            zipInfoProperties.setLocation(getCanonicalPath(new File(s)).replace('/', File.separatorChar));
            return zipInfoProperties;
        }
        catch (IOException ex) {
            return null;
        }
    }
    
    private static ZipInfoProperties checkFile(final String s) {
        try {
            final ZipFile zipFile = new ZipFile(s);
            ZipEntry entry = null;
            for (int i = 0; i < Main.infoNames.length; ++i) {
                entry = zipFile.getEntry(Main.infoNames[i]);
                if (entry != null) {
                    break;
                }
            }
            if (entry == null) {
                return null;
            }
            final InputStream inputStream = zipFile.getInputStream(entry);
            if (inputStream == null) {
                return null;
            }
            final ZipInfoProperties zipInfoProperties = new ZipInfoProperties(ProductVersionHolder.getProductVersionHolderFromMyEnv(inputStream));
            zipInfoProperties.setLocation(getCanonicalPath(new File(s)).replace('/', File.separatorChar));
            return zipInfoProperties;
        }
        catch (IOException ex) {
            return null;
        }
    }
    
    public static String getTextMessage(final String s) {
        return getCompleteMessage(s, new Object[0]);
    }
    
    public static String getTextMessage(final String s, final Object o) {
        return getCompleteMessage(s, new Object[] { o });
    }
    
    public static String getTextMessage(final String s, final Object o, final Object o2) {
        return getCompleteMessage(s, new Object[] { o, o2 });
    }
    
    public static String getTextMessage(final String s, final Object o, final Object o2, final Object o3) {
        return getCompleteMessage(s, new Object[] { o, o2, o3 });
    }
    
    public static String getTextMessage(final String s, final Object o, final Object o2, final Object o3, final Object o4) {
        return getCompleteMessage(s, new Object[] { o, o2, o3, o4 });
    }
    
    public static String getCompleteMessage(final String s, final Object[] array) {
        return Main.LOCALIZED_RESOURCE.getTextMessage(s, array);
    }
    
    private static String getFileWhichLoadedClass(final Class clazz) {
        return AccessController.doPrivileged((PrivilegedAction<String>)new PrivilegedAction() {
            public Object run() {
                CodeSource codeSource;
                try {
                    codeSource = clazz.getProtectionDomain().getCodeSource();
                }
                catch (SecurityException ex) {
                    return Main.getTextMessage("SIF01.V", clazz.getName(), ex.getMessage());
                }
                if (codeSource == null) {
                    return null;
                }
                final URL location = codeSource.getLocation();
                try {
                    return URLDecoder.decode(location.toString(), "UTF-8");
                }
                catch (UnsupportedEncodingException ex2) {
                    return ex2.getMessage();
                }
            }
        });
    }
    
    private static ZipInfoProperties[] mergeZips(final ZipInfoProperties[] array, final ZipInfoProperties[] array2) {
        final Vector vector = new Vector<ZipInfoProperties>();
        int n = 0;
        for (int i = 0; i < array.length; ++i) {
            if (array[i] != null && array.length > 1) {
                for (int j = i + 1; j < array.length; ++j) {
                    if (array[i].getLocation().equals(array[j].getLocation())) {
                        array[j] = null;
                    }
                }
            }
            if (array[i] != null) {
                vector.addElement(array[i]);
            }
        }
        if (array2 != null) {
            for (int k = 0; k < array2.length; ++k) {
                for (int l = 0; l < vector.size(); ++l) {
                    if (array2[k].getLocation().equals(vector.get(l).getLocation())) {
                        n = 1;
                    }
                }
                if (n == 0) {
                    vector.addElement(array2[k]);
                }
                n = 0;
            }
        }
        final ZipInfoProperties[] anArray = new ZipInfoProperties[vector.size()];
        vector.copyInto(anArray);
        return anArray;
    }
    
    private static String formatURL(final URL url) {
        String pathname;
        try {
            pathname = URLDecoder.decode(url.toString(), "UTF-8");
        }
        catch (UnsupportedEncodingException ex2) {
            return null;
        }
        if (pathname.startsWith("jar:")) {
            pathname = pathname.substring(4);
        }
        if (pathname.startsWith("file:")) {
            pathname = pathname.substring(5);
        }
        if (pathname.indexOf("!") > -1) {
            pathname = pathname.substring(0, pathname.indexOf("!"));
        }
        if (pathname.indexOf("/org/apache/derby") > -1) {
            pathname = pathname.substring(0, pathname.indexOf("/org/apache/derby"));
        }
        if (pathname.charAt(0) == '/' && Character.isLetter(pathname.charAt(1)) && pathname.charAt(2) == ':' && pathname.charAt(2) == '/') {
            pathname = pathname.substring(1);
        }
        String s;
        try {
            s = getCanonicalPath(new File(pathname)).replace('/', File.separatorChar);
        }
        catch (IOException ex) {
            s = ex.getMessage();
        }
        return s;
    }
    
    static {
        LOCALIZED_RESOURCE = new LocalizedResource(null, null, "org.apache.derby.loc.sysinfoMessages");
        Main.setPause = false;
        Main.setLicense = false;
        Main.cptester = false;
        javaSep = getTextMessage("SIF01.L");
        jbmsSep = getTextMessage("SIF01.M");
        licSep = getTextMessage("SIF01.N");
        locSep = getTextMessage("SIF01.P");
        curLoc = getTextMessage("SIF01.T");
        infoNames = new String[] { "org/apache/derby/info/DBMS.properties", "org/apache/derby/info/tools.properties", "org/apache/derby/info/net.properties", "org/apache/derby/info/dnc.properties" };
    }
}
