// 
// Decompiled by Procyon v0.5.36
// 

package com.thoughtworks.paranamer;

import java.net.URLConnection;
import java.util.zip.InflaterInputStream;
import java.util.zip.Inflater;
import java.util.zip.GZIPInputStream;
import java.io.UnsupportedEncodingException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;
import java.lang.reflect.AccessibleObject;
import java.net.URISyntaxException;
import java.net.URL;
import java.io.IOException;
import java.util.SortedMap;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.TreeMap;
import java.util.zip.ZipFile;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.io.File;
import java.util.Set;
import java.net.URI;

public class JavadocParanamer implements Paranamer
{
    private static final String IE = "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 2.0.50727)";
    private static final ParameterNamesNotFoundException CLASS_NOT_SUPPORTED;
    private String base;
    private final boolean isArchive;
    private final boolean isDirectory;
    private final boolean isURI;
    private final URI location;
    private final Set<String> packages;
    public static final String __PARANAMER_DATA = "<init> java.io.File archiveOrDirectory \n<init> java.net.URL url \nlookupParameterNames java.lang.reflect.AccessibleObject methodOrConstructor \nlookupParameterNames java.lang.reflect.AccessibleObject,boolean methodOrConstructor,throwExceptionIfMissing \n";
    
    public JavadocParanamer(final File archiveOrDirectory) throws IOException {
        this.base = null;
        this.packages = new HashSet<String>();
        if (archiveOrDirectory == null) {
            throw new NullPointerException();
        }
        if (!archiveOrDirectory.exists()) {
            throw new FileNotFoundException(archiveOrDirectory.getAbsolutePath());
        }
        this.isURI = false;
        this.location = archiveOrDirectory.toURI();
        if (archiveOrDirectory.isDirectory()) {
            this.isArchive = false;
            this.isDirectory = true;
            final File dir = archiveOrDirectory;
            final File packageList = new File(dir.getAbsolutePath() + "/package-list");
            if (!packageList.isFile()) {
                throw new FileNotFoundException("No package-list found at " + dir.getAbsolutePath() + ". Not a valid Javadoc directory.");
            }
            final FileInputStream input = new FileInputStream(packageList);
            try {
                final String packageListString = this.streamToString(input);
                this.parsePackageList(packageListString);
            }
            finally {
                input.close();
            }
        }
        else {
            if (!archiveOrDirectory.isFile()) {
                throw new IllegalArgumentException(archiveOrDirectory.getAbsolutePath() + " is neither a directory nor a file.");
            }
            this.isArchive = true;
            this.isDirectory = false;
            final File archive = archiveOrDirectory;
            if (!archive.getAbsolutePath().toLowerCase().endsWith(".zip")) {
                throw new IllegalArgumentException(archive.getAbsolutePath() + " is not a zip file.");
            }
            final ZipFile zip = new ZipFile(archive);
            try {
                final Enumeration<? extends ZipEntry> entries = zip.entries();
                final SortedMap<Long, ZipEntry> packageLists = new TreeMap<Long, ZipEntry>();
                while (entries.hasMoreElements()) {
                    final ZipEntry entry = (ZipEntry)entries.nextElement();
                    final String name = entry.getName();
                    if (name.endsWith("package-list")) {
                        final Long size = entry.getSize();
                        packageLists.put(size, entry);
                    }
                }
                if (packageLists.size() == 0) {
                    throw new FileNotFoundException("no package-list found in archive");
                }
                final ZipEntry entry = packageLists.get(packageLists.lastKey());
                final String name = entry.getName();
                this.base = name.substring(0, name.length() - "package-list".length());
                final InputStream input2 = zip.getInputStream(entry);
                try {
                    final String packageListString2 = this.streamToString(input2);
                    this.parsePackageList(packageListString2);
                }
                finally {
                    input2.close();
                }
            }
            finally {
                zip.close();
            }
        }
    }
    
    public JavadocParanamer(final URL url) throws IOException {
        this.base = null;
        this.packages = new HashSet<String>();
        if (url == null) {
            throw new NullPointerException();
        }
        this.isArchive = false;
        this.isDirectory = false;
        this.isURI = true;
        try {
            this.location = new URI(url.toString());
        }
        catch (URISyntaxException e) {
            throw new IOException(e.getMessage());
        }
        final URL packageListURL = new URL(url.toString() + "/package-list");
        final InputStream input = this.urlToInputStream(packageListURL);
        try {
            final String packageList = this.streamToString(input);
            this.parsePackageList(packageList);
        }
        finally {
            input.close();
        }
    }
    
    public String[] lookupParameterNames(final AccessibleObject methodOrConstructor) {
        return this.lookupParameterNames(methodOrConstructor, true);
    }
    
    public String[] lookupParameterNames(final AccessibleObject methodOrConstructor, final boolean throwExceptionIfMissing) {
        if (methodOrConstructor == null) {
            throw new NullPointerException();
        }
        Class<?> klass;
        String name;
        Class<?>[] types;
        if (methodOrConstructor instanceof Constructor) {
            final Constructor<?> constructor = (Constructor<?>)methodOrConstructor;
            klass = constructor.getDeclaringClass();
            name = constructor.getName();
            types = constructor.getParameterTypes();
        }
        else {
            if (!(methodOrConstructor instanceof Method)) {
                throw new IllegalArgumentException();
            }
            final Method method = (Method)methodOrConstructor;
            klass = method.getDeclaringClass();
            name = method.getName();
            types = method.getParameterTypes();
        }
        if (!this.packages.contains(klass.getPackage().getName())) {
            throw JavadocParanamer.CLASS_NOT_SUPPORTED;
        }
        try {
            final String[] names = this.getParameterNames(klass, name, types);
            if (names != null) {
                return names;
            }
            if (throwExceptionIfMissing) {
                throw new ParameterNamesNotFoundException(methodOrConstructor.toString());
            }
            return Paranamer.EMPTY_NAMES;
        }
        catch (IOException e) {
            if (throwExceptionIfMissing) {
                throw new ParameterNamesNotFoundException(methodOrConstructor.toString() + " due to an I/O error: " + e.getMessage());
            }
            return Paranamer.EMPTY_NAMES;
        }
    }
    
    private String[] getParameterNames(final Class<?> klass, final String constructorOrMethodName, final Class<?>[] types) throws IOException {
        if (types != null && types.length == 0) {
            return new String[0];
        }
        final String path = this.getCanonicalName(klass).replace('.', '/');
        if (this.isArchive) {
            final ZipFile archive = new ZipFile(new File(this.location));
            final ZipEntry entry = archive.getEntry(this.base + path + ".html");
            if (entry == null) {
                throw JavadocParanamer.CLASS_NOT_SUPPORTED;
            }
            final InputStream input = archive.getInputStream(entry);
            return this.getParameterNames2(input, constructorOrMethodName, types);
        }
        else {
            if (!this.isDirectory) {
                if (this.isURI) {
                    try {
                        final URL url = new URL(this.location.toString() + "/" + path + ".html");
                        final InputStream input2 = this.urlToInputStream(url);
                        return this.getParameterNames2(input2, constructorOrMethodName, types);
                    }
                    catch (FileNotFoundException e) {
                        throw JavadocParanamer.CLASS_NOT_SUPPORTED;
                    }
                }
                throw new RuntimeException("bug in JavadocParanamer. Should not reach here.");
            }
            final File file = new File(this.location.getPath() + "/" + path + ".html");
            if (!file.isFile()) {
                throw JavadocParanamer.CLASS_NOT_SUPPORTED;
            }
            final FileInputStream input3 = new FileInputStream(file);
            return this.getParameterNames2(input3, constructorOrMethodName, types);
        }
    }
    
    private String[] getParameterNames2(final InputStream input, final String constructorOrMethodName, final Class<?>[] types) throws IOException {
        final String javadoc = this.streamToString(input);
        input.close();
        final StringBuffer regex = new StringBuffer();
        regex.append("NAME=\"");
        regex.append(constructorOrMethodName);
        regex.append("\\(\\Q");
        for (int i = 0; i < types.length; ++i) {
            if (i != 0) {
                regex.append(", ");
            }
            regex.append(this.getCanonicalName(types[i]));
        }
        regex.append("\\E\\)\"");
        final Pattern pattern = Pattern.compile(regex.toString());
        final Matcher matcher = pattern.matcher(javadoc);
        if (!matcher.find()) {
            return Paranamer.EMPTY_NAMES;
        }
        final String[] names = new String[types.length];
        final String regexParams = "<DD><CODE>([^<]*)</CODE>";
        final Pattern patternParams = Pattern.compile(regexParams);
        int start = matcher.end();
        final Matcher matcherParams = patternParams.matcher(javadoc);
        for (int j = 0; j < types.length; ++j) {
            final boolean find = matcherParams.find(start);
            if (!find) {
                return Paranamer.EMPTY_NAMES;
            }
            start = matcherParams.end();
            names[j] = matcherParams.group(1);
        }
        return names;
    }
    
    private String getCanonicalName(final Class<?> klass) {
        if (klass.isArray()) {
            return this.getCanonicalName(klass.getComponentType()) + "[]";
        }
        return klass.getName();
    }
    
    private void parsePackageList(final String packageList) throws IOException {
        final StringReader reader = new StringReader(packageList);
        final BufferedReader breader = new BufferedReader(reader);
        String line;
        while ((line = breader.readLine()) != null) {
            this.packages.add(line);
        }
    }
    
    private String streamToString(final InputStream input) throws IOException {
        InputStreamReader reader;
        try {
            reader = new InputStreamReader(input, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            reader = new InputStreamReader(input);
        }
        final BufferedReader breader = new BufferedReader(reader);
        final StringBuffer builder = new StringBuffer();
        String line;
        while ((line = breader.readLine()) != null) {
            builder.append(line);
            builder.append("\n");
        }
        return builder.toString();
    }
    
    private InputStream urlToInputStream(final URL url) throws IOException {
        final URLConnection conn = url.openConnection();
        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 2.0.50727)");
        conn.setRequestProperty("Accept-Encoding", "gzip, deflate");
        conn.connect();
        final String encoding = conn.getContentEncoding();
        if (encoding != null && encoding.equalsIgnoreCase("gzip")) {
            return new GZIPInputStream(conn.getInputStream());
        }
        if (encoding != null && encoding.equalsIgnoreCase("deflate")) {
            return new InflaterInputStream(conn.getInputStream(), new Inflater(true));
        }
        return conn.getInputStream();
    }
    
    static {
        CLASS_NOT_SUPPORTED = new ParameterNamesNotFoundException("class not supported");
    }
}
