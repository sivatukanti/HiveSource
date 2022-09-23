// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.optional;

import java.util.Iterator;
import org.apache.tools.ant.types.resources.FileProvider;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.util.regexp.RegexpUtil;
import java.io.Writer;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import org.apache.tools.ant.util.regexp.Regexp;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.types.Substitution;
import org.apache.tools.ant.types.RegularExpression;
import org.apache.tools.ant.types.resources.Union;
import java.io.File;
import org.apache.tools.ant.Task;

public class ReplaceRegExp extends Task
{
    private File file;
    private String flags;
    private boolean byline;
    private Union resources;
    private RegularExpression regex;
    private Substitution subs;
    private static final FileUtils FILE_UTILS;
    private boolean preserveLastModified;
    private String encoding;
    
    public ReplaceRegExp() {
        this.preserveLastModified = false;
        this.encoding = null;
        this.file = null;
        this.flags = "";
        this.byline = false;
        this.regex = null;
        this.subs = null;
    }
    
    public void setFile(final File file) {
        this.file = file;
    }
    
    public void setMatch(final String match) {
        if (this.regex != null) {
            throw new BuildException("Only one regular expression is allowed");
        }
        (this.regex = new RegularExpression()).setPattern(match);
    }
    
    public void setReplace(final String replace) {
        if (this.subs != null) {
            throw new BuildException("Only one substitution expression is allowed");
        }
        (this.subs = new Substitution()).setExpression(replace);
    }
    
    public void setFlags(final String flags) {
        this.flags = flags;
    }
    
    @Deprecated
    public void setByLine(final String byline) {
        Boolean res = Boolean.valueOf(byline);
        if (res == null) {
            res = Boolean.FALSE;
        }
        this.byline = res;
    }
    
    public void setByLine(final boolean byline) {
        this.byline = byline;
    }
    
    public void setEncoding(final String encoding) {
        this.encoding = encoding;
    }
    
    public void addFileset(final FileSet set) {
        this.addConfigured(set);
    }
    
    public void addConfigured(final ResourceCollection rc) {
        if (!rc.isFilesystemOnly()) {
            throw new BuildException("only filesystem resources are supported");
        }
        if (this.resources == null) {
            this.resources = new Union();
        }
        this.resources.add(rc);
    }
    
    public RegularExpression createRegexp() {
        if (this.regex != null) {
            throw new BuildException("Only one regular expression is allowed.");
        }
        return this.regex = new RegularExpression();
    }
    
    public Substitution createSubstitution() {
        if (this.subs != null) {
            throw new BuildException("Only one substitution expression is allowed");
        }
        return this.subs = new Substitution();
    }
    
    public void setPreserveLastModified(final boolean b) {
        this.preserveLastModified = b;
    }
    
    protected String doReplace(final RegularExpression r, final Substitution s, final String input, final int options) {
        String res = input;
        final Regexp regexp = r.getRegexp(this.getProject());
        if (regexp.matches(input, options)) {
            this.log("Found match; substituting", 4);
            res = regexp.substitute(input, s.getExpression(this.getProject()), options);
        }
        return res;
    }
    
    protected void doReplace(final File f, final int options) throws IOException {
        File temp = ReplaceRegExp.FILE_UTILS.createTempFile("replace", ".txt", null, true, true);
        try {
            boolean changes = false;
            final InputStream is = new FileInputStream(f);
            try {
                final Reader r = (this.encoding != null) ? new InputStreamReader(is, this.encoding) : new InputStreamReader(is);
                final OutputStream os = new FileOutputStream(temp);
                try {
                    final Writer w = (this.encoding != null) ? new OutputStreamWriter(os, this.encoding) : new OutputStreamWriter(os);
                    final BufferedReader br = new BufferedReader(r);
                    final BufferedWriter bw = new BufferedWriter(w);
                    this.log("Replacing pattern '" + this.regex.getPattern(this.getProject()) + "' with '" + this.subs.getExpression(this.getProject()) + "' in '" + f.getPath() + "'" + (this.byline ? " by line" : "") + ((this.flags.length() > 0) ? (" with flags: '" + this.flags + "'") : "") + ".", 3);
                    if (this.byline) {
                        StringBuffer linebuf = new StringBuffer();
                        String line = null;
                        String res = null;
                        boolean hasCR = false;
                        int c;
                        do {
                            c = br.read();
                            if (c == 13) {
                                if (hasCR) {
                                    line = linebuf.toString();
                                    res = this.doReplace(this.regex, this.subs, line, options);
                                    if (!res.equals(line)) {
                                        changes = true;
                                    }
                                    bw.write(res);
                                    bw.write(13);
                                    linebuf = new StringBuffer();
                                }
                                else {
                                    hasCR = true;
                                }
                            }
                            else if (c == 10) {
                                line = linebuf.toString();
                                res = this.doReplace(this.regex, this.subs, line, options);
                                if (!res.equals(line)) {
                                    changes = true;
                                }
                                bw.write(res);
                                if (hasCR) {
                                    bw.write(13);
                                    hasCR = false;
                                }
                                bw.write(10);
                                linebuf = new StringBuffer();
                            }
                            else {
                                if (hasCR || c < 0) {
                                    line = linebuf.toString();
                                    res = this.doReplace(this.regex, this.subs, line, options);
                                    if (!res.equals(line)) {
                                        changes = true;
                                    }
                                    bw.write(res);
                                    if (hasCR) {
                                        bw.write(13);
                                        hasCR = false;
                                    }
                                    linebuf = new StringBuffer();
                                }
                                if (c < 0) {
                                    continue;
                                }
                                linebuf.append((char)c);
                            }
                        } while (c >= 0);
                    }
                    else {
                        final String buf = FileUtils.safeReadFully(br);
                        final String res2 = this.doReplace(this.regex, this.subs, buf, options);
                        if (!res2.equals(buf)) {
                            changes = true;
                        }
                        bw.write(res2);
                    }
                    bw.flush();
                }
                finally {
                    os.close();
                }
            }
            finally {
                is.close();
            }
            if (changes) {
                this.log("File has changed; saving the updated file", 3);
                try {
                    final long origLastModified = f.lastModified();
                    ReplaceRegExp.FILE_UTILS.rename(temp, f);
                    if (this.preserveLastModified) {
                        ReplaceRegExp.FILE_UTILS.setFileLastModified(f, origLastModified);
                    }
                    temp = null;
                    return;
                }
                catch (IOException e) {
                    throw new BuildException("Couldn't rename temporary file " + temp, e, this.getLocation());
                }
            }
            this.log("No change made", 4);
        }
        finally {
            if (temp != null) {
                temp.delete();
            }
        }
    }
    
    @Override
    public void execute() throws BuildException {
        if (this.regex == null) {
            throw new BuildException("No expression to match.");
        }
        if (this.subs == null) {
            throw new BuildException("Nothing to replace expression with.");
        }
        if (this.file != null && this.resources != null) {
            throw new BuildException("You cannot supply the 'file' attribute and resource collections at the same time.");
        }
        final int options = RegexpUtil.asOptions(this.flags);
        if (this.file != null && this.file.exists()) {
            try {
                this.doReplace(this.file, options);
            }
            catch (IOException e) {
                this.log("An error occurred processing file: '" + this.file.getAbsolutePath() + "': " + e.toString(), 0);
            }
        }
        else if (this.file != null) {
            this.log("The following file is missing: '" + this.file.getAbsolutePath() + "'", 0);
        }
        if (this.resources != null) {
            for (final Resource r : this.resources) {
                final FileProvider fp = r.as(FileProvider.class);
                final File f = fp.getFile();
                if (f.exists()) {
                    try {
                        this.doReplace(f, options);
                    }
                    catch (Exception e2) {
                        this.log("An error occurred processing file: '" + f.getAbsolutePath() + "': " + e2.toString(), 0);
                    }
                }
                else {
                    this.log("The following file is missing: '" + f.getAbsolutePath() + "'", 0);
                }
            }
        }
    }
    
    static {
        FILE_UTILS = FileUtils.getFileUtils();
    }
}
