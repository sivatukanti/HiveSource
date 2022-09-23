// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional.i18n;

import org.apache.tools.ant.util.LineTokenizer;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import org.apache.tools.ant.DirectoryScanner;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.FileInputStream;
import java.util.Locale;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.util.FileUtils;
import java.util.Hashtable;
import java.util.Vector;
import java.io.File;
import org.apache.tools.ant.taskdefs.MatchingTask;

public class Translate extends MatchingTask
{
    private static final int BUNDLE_SPECIFIED_LANGUAGE_COUNTRY_VARIANT = 0;
    private static final int BUNDLE_SPECIFIED_LANGUAGE_COUNTRY = 1;
    private static final int BUNDLE_SPECIFIED_LANGUAGE = 2;
    private static final int BUNDLE_NOMATCH = 3;
    private static final int BUNDLE_DEFAULT_LANGUAGE_COUNTRY_VARIANT = 4;
    private static final int BUNDLE_DEFAULT_LANGUAGE_COUNTRY = 5;
    private static final int BUNDLE_DEFAULT_LANGUAGE = 6;
    private static final int BUNDLE_MAX_ALTERNATIVES = 7;
    private String bundle;
    private String bundleLanguage;
    private String bundleCountry;
    private String bundleVariant;
    private File toDir;
    private String srcEncoding;
    private String destEncoding;
    private String bundleEncoding;
    private String startToken;
    private String endToken;
    private boolean forceOverwrite;
    private Vector filesets;
    private Hashtable resourceMap;
    private static final FileUtils FILE_UTILS;
    private long[] bundleLastModified;
    private long srcLastModified;
    private long destLastModified;
    private boolean loaded;
    
    public Translate() {
        this.filesets = new Vector();
        this.resourceMap = new Hashtable();
        this.bundleLastModified = new long[7];
        this.loaded = false;
    }
    
    public void setBundle(final String bundle) {
        this.bundle = bundle;
    }
    
    public void setBundleLanguage(final String bundleLanguage) {
        this.bundleLanguage = bundleLanguage;
    }
    
    public void setBundleCountry(final String bundleCountry) {
        this.bundleCountry = bundleCountry;
    }
    
    public void setBundleVariant(final String bundleVariant) {
        this.bundleVariant = bundleVariant;
    }
    
    public void setToDir(final File toDir) {
        this.toDir = toDir;
    }
    
    public void setStartToken(final String startToken) {
        this.startToken = startToken;
    }
    
    public void setEndToken(final String endToken) {
        this.endToken = endToken;
    }
    
    public void setSrcEncoding(final String srcEncoding) {
        this.srcEncoding = srcEncoding;
    }
    
    public void setDestEncoding(final String destEncoding) {
        this.destEncoding = destEncoding;
    }
    
    public void setBundleEncoding(final String bundleEncoding) {
        this.bundleEncoding = bundleEncoding;
    }
    
    public void setForceOverwrite(final boolean forceOverwrite) {
        this.forceOverwrite = forceOverwrite;
    }
    
    public void addFileset(final FileSet set) {
        this.filesets.addElement(set);
    }
    
    @Override
    public void execute() throws BuildException {
        if (this.bundle == null) {
            throw new BuildException("The bundle attribute must be set.", this.getLocation());
        }
        if (this.startToken == null) {
            throw new BuildException("The starttoken attribute must be set.", this.getLocation());
        }
        if (this.endToken == null) {
            throw new BuildException("The endtoken attribute must be set.", this.getLocation());
        }
        if (this.bundleLanguage == null) {
            final Locale l = Locale.getDefault();
            this.bundleLanguage = l.getLanguage();
        }
        if (this.bundleCountry == null) {
            this.bundleCountry = Locale.getDefault().getCountry();
        }
        if (this.bundleVariant == null) {
            final Locale l = new Locale(this.bundleLanguage, this.bundleCountry);
            this.bundleVariant = l.getVariant();
        }
        if (this.toDir == null) {
            throw new BuildException("The todir attribute must be set.", this.getLocation());
        }
        if (!this.toDir.exists()) {
            this.toDir.mkdirs();
        }
        else if (this.toDir.isFile()) {
            throw new BuildException(this.toDir + " is not a directory");
        }
        if (this.srcEncoding == null) {
            this.srcEncoding = System.getProperty("file.encoding");
        }
        if (this.destEncoding == null) {
            this.destEncoding = this.srcEncoding;
        }
        if (this.bundleEncoding == null) {
            this.bundleEncoding = this.srcEncoding;
        }
        this.loadResourceMaps();
        this.translate();
    }
    
    private void loadResourceMaps() throws BuildException {
        Locale locale = new Locale(this.bundleLanguage, this.bundleCountry, this.bundleVariant);
        String language = (locale.getLanguage().length() > 0) ? ("_" + locale.getLanguage()) : "";
        String country = (locale.getCountry().length() > 0) ? ("_" + locale.getCountry()) : "";
        String variant = (locale.getVariant().length() > 0) ? ("_" + locale.getVariant()) : "";
        String bundleFile = this.bundle + language + country + variant;
        this.processBundle(bundleFile, 0, false);
        bundleFile = this.bundle + language + country;
        this.processBundle(bundleFile, 1, false);
        bundleFile = this.bundle + language;
        this.processBundle(bundleFile, 2, false);
        bundleFile = this.bundle;
        this.processBundle(bundleFile, 3, false);
        locale = Locale.getDefault();
        language = ((locale.getLanguage().length() > 0) ? ("_" + locale.getLanguage()) : "");
        country = ((locale.getCountry().length() > 0) ? ("_" + locale.getCountry()) : "");
        variant = ((locale.getVariant().length() > 0) ? ("_" + locale.getVariant()) : "");
        this.bundleEncoding = System.getProperty("file.encoding");
        bundleFile = this.bundle + language + country + variant;
        this.processBundle(bundleFile, 4, false);
        bundleFile = this.bundle + language + country;
        this.processBundle(bundleFile, 5, false);
        bundleFile = this.bundle + language;
        this.processBundle(bundleFile, 6, true);
    }
    
    private void processBundle(final String bundleFile, final int i, final boolean checkLoaded) throws BuildException {
        final File propsFile = this.getProject().resolveFile(bundleFile + ".properties");
        FileInputStream ins = null;
        try {
            ins = new FileInputStream(propsFile);
            this.loaded = true;
            this.bundleLastModified[i] = propsFile.lastModified();
            this.log("Using " + propsFile, 4);
            this.loadResourceMap(ins);
        }
        catch (IOException ioe) {
            this.log(propsFile + " not found.", 4);
            if (!this.loaded && checkLoaded) {
                throw new BuildException(ioe.getMessage(), this.getLocation());
            }
        }
    }
    
    private void loadResourceMap(final FileInputStream ins) throws BuildException {
        try {
            BufferedReader in = null;
            final InputStreamReader isr = new InputStreamReader(ins, this.bundleEncoding);
            in = new BufferedReader(isr);
            String line = null;
            while ((line = in.readLine()) != null) {
                if (line.trim().length() > 1 && '#' != line.charAt(0) && '!' != line.charAt(0)) {
                    int sepIndex = line.indexOf(61);
                    if (-1 == sepIndex) {
                        sepIndex = line.indexOf(58);
                    }
                    if (-1 == sepIndex) {
                        for (int k = 0; k < line.length(); ++k) {
                            if (Character.isSpaceChar(line.charAt(k))) {
                                sepIndex = k;
                                break;
                            }
                        }
                    }
                    if (-1 == sepIndex) {
                        continue;
                    }
                    final String key = line.substring(0, sepIndex).trim();
                    String value;
                    for (value = line.substring(sepIndex + 1).trim(); value.endsWith("\\"); value += line.trim()) {
                        value = value.substring(0, value.length() - 1);
                        line = in.readLine();
                        if (line == null) {
                            break;
                        }
                    }
                    if (key.length() <= 0 || this.resourceMap.get(key) != null) {
                        continue;
                    }
                    this.resourceMap.put(key, value);
                }
            }
            if (in != null) {
                in.close();
            }
        }
        catch (IOException ioe) {
            throw new BuildException(ioe.getMessage(), this.getLocation());
        }
    }
    
    private void translate() throws BuildException {
        int filesProcessed = 0;
        for (int size = this.filesets.size(), i = 0; i < size; ++i) {
            final FileSet fs = this.filesets.elementAt(i);
            final DirectoryScanner ds = fs.getDirectoryScanner(this.getProject());
            final String[] srcFiles = ds.getIncludedFiles();
            for (int j = 0; j < srcFiles.length; ++j) {
                try {
                    final File dest = Translate.FILE_UTILS.resolveFile(this.toDir, srcFiles[j]);
                    try {
                        final File destDir = new File(dest.getParent());
                        if (!destDir.exists()) {
                            destDir.mkdirs();
                        }
                    }
                    catch (Exception e) {
                        this.log("Exception occurred while trying to check/create  parent directory.  " + e.getMessage(), 4);
                    }
                    this.destLastModified = dest.lastModified();
                    final File src = Translate.FILE_UTILS.resolveFile(ds.getBasedir(), srcFiles[j]);
                    this.srcLastModified = src.lastModified();
                    boolean needsWork = this.forceOverwrite || this.destLastModified < this.srcLastModified;
                    if (!needsWork) {
                        for (int icounter = 0; icounter < 7; ++icounter) {
                            needsWork = (this.destLastModified < this.bundleLastModified[icounter]);
                            if (needsWork) {
                                break;
                            }
                        }
                    }
                    if (needsWork) {
                        this.log("Processing " + srcFiles[j], 4);
                        this.translateOneFile(src, dest);
                        ++filesProcessed;
                    }
                    else {
                        this.log("Skipping " + srcFiles[j] + " as destination file is up to date", 3);
                    }
                }
                catch (IOException ioe) {
                    throw new BuildException(ioe.getMessage(), this.getLocation());
                }
            }
        }
        this.log("Translation performed on " + filesProcessed + " file(s).", 4);
    }
    
    private void translateOneFile(final File src, final File dest) throws IOException {
        BufferedWriter out = null;
        BufferedReader in = null;
        try {
            final FileOutputStream fos = new FileOutputStream(dest);
            out = new BufferedWriter(new OutputStreamWriter(fos, this.destEncoding));
            final FileInputStream fis = new FileInputStream(src);
            in = new BufferedReader(new InputStreamReader(fis, this.srcEncoding));
            final LineTokenizer lineTokenizer = new LineTokenizer();
            lineTokenizer.setIncludeDelims(true);
            for (String line = lineTokenizer.getToken(in); line != null; line = lineTokenizer.getToken(in)) {
                for (int startIndex = line.indexOf(this.startToken); startIndex >= 0 && startIndex + this.startToken.length() <= line.length(); startIndex = line.indexOf(this.startToken, startIndex)) {
                    String replace = null;
                    final int endIndex = line.indexOf(this.endToken, startIndex + this.startToken.length());
                    if (endIndex < 0) {
                        ++startIndex;
                    }
                    else {
                        final String token = line.substring(startIndex + this.startToken.length(), endIndex);
                        boolean validToken = true;
                        for (int k = 0; k < token.length() && validToken; ++k) {
                            final char c = token.charAt(k);
                            if (c == ':' || c == '=' || Character.isSpaceChar(c)) {
                                validToken = false;
                            }
                        }
                        if (!validToken) {
                            ++startIndex;
                        }
                        else {
                            if (this.resourceMap.containsKey(token)) {
                                replace = this.resourceMap.get(token);
                            }
                            else {
                                this.log("Replacement string missing for: " + token, 3);
                                replace = this.startToken + token + this.endToken;
                            }
                            line = line.substring(0, startIndex) + replace + line.substring(endIndex + this.endToken.length());
                            startIndex += replace.length();
                        }
                    }
                }
                out.write(line);
            }
        }
        finally {
            FileUtils.close(in);
            FileUtils.close(out);
        }
    }
    
    static {
        FILE_UTILS = FileUtils.getFileUtils();
    }
}
