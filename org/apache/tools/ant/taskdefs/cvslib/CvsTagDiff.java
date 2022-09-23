// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.cvslib;

import java.util.Iterator;
import java.util.StringTokenizer;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import org.apache.tools.ant.util.CollectionUtils;
import org.apache.tools.ant.util.DOMUtils;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import org.apache.tools.ant.BuildException;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import org.apache.tools.ant.util.DOMElementWriter;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.taskdefs.AbstractCvsTask;

public class CvsTagDiff extends AbstractCvsTask
{
    private static final FileUtils FILE_UTILS;
    private static final DOMElementWriter DOM_WRITER;
    static final String FILE_STRING = "File ";
    static final int FILE_STRING_LENGTH;
    static final String TO_STRING = " to ";
    static final String FILE_IS_NEW = " is new;";
    static final String REVISION = "revision ";
    static final String FILE_HAS_CHANGED = " changed from revision ";
    static final String FILE_WAS_REMOVED = " is removed";
    private String mypackage;
    private String mystartTag;
    private String myendTag;
    private String mystartDate;
    private String myendDate;
    private File mydestfile;
    private boolean ignoreRemoved;
    private List packageNames;
    private String[] packageNamePrefixes;
    private int[] packageNamePrefixLengths;
    
    public CvsTagDiff() {
        this.ignoreRemoved = false;
        this.packageNames = new ArrayList();
        this.packageNamePrefixes = null;
        this.packageNamePrefixLengths = null;
    }
    
    @Override
    public void setPackage(final String p) {
        this.mypackage = p;
    }
    
    public void setStartTag(final String s) {
        this.mystartTag = s;
    }
    
    public void setStartDate(final String s) {
        this.mystartDate = s;
    }
    
    public void setEndTag(final String s) {
        this.myendTag = s;
    }
    
    public void setEndDate(final String s) {
        this.myendDate = s;
    }
    
    public void setDestFile(final File f) {
        this.mydestfile = f;
    }
    
    public void setIgnoreRemoved(final boolean b) {
        this.ignoreRemoved = b;
    }
    
    @Override
    public void execute() throws BuildException {
        this.validate();
        this.addCommandArgument("rdiff");
        this.addCommandArgument("-s");
        if (this.mystartTag != null) {
            this.addCommandArgument("-r");
            this.addCommandArgument(this.mystartTag);
        }
        else {
            this.addCommandArgument("-D");
            this.addCommandArgument(this.mystartDate);
        }
        if (this.myendTag != null) {
            this.addCommandArgument("-r");
            this.addCommandArgument(this.myendTag);
        }
        else {
            this.addCommandArgument("-D");
            this.addCommandArgument(this.myendDate);
        }
        this.setCommand("");
        File tmpFile = null;
        try {
            this.handlePackageNames();
            tmpFile = CvsTagDiff.FILE_UTILS.createTempFile("cvstagdiff", ".log", null, true, true);
            this.setOutput(tmpFile);
            super.execute();
            final CvsTagEntry[] entries = this.parseRDiff(tmpFile);
            this.writeTagDiff(entries);
        }
        finally {
            this.packageNamePrefixes = null;
            this.packageNamePrefixLengths = null;
            this.packageNames.clear();
            if (tmpFile != null) {
                tmpFile.delete();
            }
        }
    }
    
    private CvsTagEntry[] parseRDiff(final File tmpFile) throws BuildException {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(tmpFile));
            final Vector entries = new Vector();
            for (String line = reader.readLine(); null != line; line = reader.readLine()) {
                line = removePackageName(line, this.packageNamePrefixes, this.packageNamePrefixLengths);
                if (line != null) {
                    final boolean processed = this.doFileIsNew(entries, line) || this.doFileHasChanged(entries, line) || this.doFileWasRemoved(entries, line);
                }
            }
            final CvsTagEntry[] array = new CvsTagEntry[entries.size()];
            entries.copyInto(array);
            return array;
        }
        catch (IOException e) {
            throw new BuildException("Error in parsing", e);
        }
        finally {
            if (reader != null) {
                try {
                    reader.close();
                }
                catch (IOException e2) {
                    this.log(e2.toString(), 0);
                }
            }
        }
    }
    
    private boolean doFileIsNew(final Vector entries, final String line) {
        final int index = line.indexOf(" is new;");
        if (index == -1) {
            return false;
        }
        final String filename = line.substring(0, index);
        String rev = null;
        final int indexrev = line.indexOf("revision ", index);
        if (indexrev != -1) {
            rev = line.substring(indexrev + "revision ".length());
        }
        final CvsTagEntry entry = new CvsTagEntry(filename, rev);
        entries.addElement(entry);
        this.log(entry.toString(), 3);
        return true;
    }
    
    private boolean doFileHasChanged(final Vector entries, final String line) {
        final int index = line.indexOf(" changed from revision ");
        if (index == -1) {
            return false;
        }
        final String filename = line.substring(0, index);
        final int revSeparator = line.indexOf(" to ", index);
        final String prevRevision = line.substring(index + " changed from revision ".length(), revSeparator);
        final String revision = line.substring(revSeparator + " to ".length());
        final CvsTagEntry entry = new CvsTagEntry(filename, revision, prevRevision);
        entries.addElement(entry);
        this.log(entry.toString(), 3);
        return true;
    }
    
    private boolean doFileWasRemoved(final Vector entries, final String line) {
        if (this.ignoreRemoved) {
            return false;
        }
        final int index = line.indexOf(" is removed");
        if (index == -1) {
            return false;
        }
        final String filename = line.substring(0, index);
        String rev = null;
        final int indexrev = line.indexOf("revision ", index);
        if (indexrev != -1) {
            rev = line.substring(indexrev + "revision ".length());
        }
        final CvsTagEntry entry = new CvsTagEntry(filename, null, rev);
        entries.addElement(entry);
        this.log(entry.toString(), 3);
        return true;
    }
    
    private void writeTagDiff(final CvsTagEntry[] entries) throws BuildException {
        FileOutputStream output = null;
        try {
            output = new FileOutputStream(this.mydestfile);
            final PrintWriter writer = new PrintWriter(new OutputStreamWriter(output, "UTF-8"));
            writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            final Document doc = DOMUtils.newDocument();
            final Element root = doc.createElement("tagdiff");
            if (this.mystartTag != null) {
                root.setAttribute("startTag", this.mystartTag);
            }
            else {
                root.setAttribute("startDate", this.mystartDate);
            }
            if (this.myendTag != null) {
                root.setAttribute("endTag", this.myendTag);
            }
            else {
                root.setAttribute("endDate", this.myendDate);
            }
            root.setAttribute("cvsroot", this.getCvsRoot());
            root.setAttribute("package", CollectionUtils.flattenToString(this.packageNames));
            CvsTagDiff.DOM_WRITER.openElement(root, writer, 0, "\t");
            writer.println();
            for (int i = 0, c = entries.length; i < c; ++i) {
                this.writeTagEntry(doc, writer, entries[i]);
            }
            CvsTagDiff.DOM_WRITER.closeElement(root, writer, 0, "\t", true);
            writer.flush();
            if (writer.checkError()) {
                throw new IOException("Encountered an error writing tagdiff");
            }
            writer.close();
        }
        catch (UnsupportedEncodingException uee) {
            this.log(uee.toString(), 0);
        }
        catch (IOException ioe) {
            throw new BuildException(ioe.toString(), ioe);
        }
        finally {
            if (null != output) {
                try {
                    output.close();
                }
                catch (IOException ioe2) {
                    this.log(ioe2.toString(), 0);
                }
            }
        }
    }
    
    private void writeTagEntry(final Document doc, final PrintWriter writer, final CvsTagEntry entry) throws IOException {
        final Element ent = doc.createElement("entry");
        final Element f = DOMUtils.createChildElement(ent, "file");
        DOMUtils.appendCDATAElement(f, "name", entry.getFile());
        if (entry.getRevision() != null) {
            DOMUtils.appendTextElement(f, "revision", entry.getRevision());
        }
        if (entry.getPreviousRevision() != null) {
            DOMUtils.appendTextElement(f, "prevrevision", entry.getPreviousRevision());
        }
        CvsTagDiff.DOM_WRITER.write(ent, writer, 1, "\t");
    }
    
    private void validate() throws BuildException {
        if (null == this.mypackage && this.getModules().size() == 0) {
            throw new BuildException("Package/module must be set.");
        }
        if (null == this.mydestfile) {
            throw new BuildException("Destfile must be set.");
        }
        if (null == this.mystartTag && null == this.mystartDate) {
            throw new BuildException("Start tag or start date must be set.");
        }
        if (null != this.mystartTag && null != this.mystartDate) {
            throw new BuildException("Only one of start tag and start date must be set.");
        }
        if (null == this.myendTag && null == this.myendDate) {
            throw new BuildException("End tag or end date must be set.");
        }
        if (null != this.myendTag && null != this.myendDate) {
            throw new BuildException("Only one of end tag and end date must be set.");
        }
    }
    
    private void handlePackageNames() {
        if (this.mypackage != null) {
            final StringTokenizer myTokenizer = new StringTokenizer(this.mypackage);
            while (myTokenizer.hasMoreTokens()) {
                final String pack = myTokenizer.nextToken();
                this.packageNames.add(pack);
                this.addCommandArgument(pack);
            }
        }
        for (final Module m : this.getModules()) {
            this.packageNames.add(m.getName());
        }
        this.packageNamePrefixes = new String[this.packageNames.size()];
        this.packageNamePrefixLengths = new int[this.packageNames.size()];
        for (int i = 0; i < this.packageNamePrefixes.length; ++i) {
            this.packageNamePrefixes[i] = "File " + this.packageNames.get(i) + "/";
            this.packageNamePrefixLengths[i] = this.packageNamePrefixes[i].length();
        }
    }
    
    private static String removePackageName(String line, final String[] packagePrefixes, final int[] prefixLengths) {
        if (line.length() < CvsTagDiff.FILE_STRING_LENGTH) {
            return null;
        }
        boolean matched = false;
        for (int i = 0; i < packagePrefixes.length; ++i) {
            if (line.startsWith(packagePrefixes[i])) {
                matched = true;
                line = line.substring(prefixLengths[i]);
                break;
            }
        }
        if (!matched) {
            line = line.substring(CvsTagDiff.FILE_STRING_LENGTH);
        }
        return line;
    }
    
    static {
        FILE_UTILS = FileUtils.getFileUtils();
        DOM_WRITER = new DOMElementWriter();
        FILE_STRING_LENGTH = "File ".length();
    }
}
