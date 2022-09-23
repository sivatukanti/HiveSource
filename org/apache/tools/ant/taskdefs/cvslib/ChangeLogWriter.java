// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.cvslib;

import java.util.TimeZone;
import java.util.Enumeration;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import java.io.IOException;
import org.apache.tools.ant.BuildException;
import java.io.Writer;
import org.apache.tools.ant.util.DOMUtils;
import java.io.PrintWriter;
import org.apache.tools.ant.util.DOMElementWriter;
import java.text.SimpleDateFormat;

public class ChangeLogWriter
{
    private static final SimpleDateFormat OUTPUT_DATE;
    private static final SimpleDateFormat OUTPUT_TIME;
    private static final DOMElementWriter DOM_WRITER;
    
    public void printChangeLog(final PrintWriter output, final CVSEntry[] entries) {
        try {
            output.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            final Document doc = DOMUtils.newDocument();
            final Element root = doc.createElement("changelog");
            ChangeLogWriter.DOM_WRITER.openElement(root, output, 0, "\t");
            output.println();
            for (int i = 0; i < entries.length; ++i) {
                final CVSEntry entry = entries[i];
                this.printEntry(doc, output, entry);
            }
            ChangeLogWriter.DOM_WRITER.closeElement(root, output, 0, "\t", true);
            output.flush();
            output.close();
        }
        catch (IOException e) {
            throw new BuildException(e);
        }
    }
    
    private void printEntry(final Document doc, final PrintWriter output, final CVSEntry entry) throws IOException {
        final Element ent = doc.createElement("entry");
        DOMUtils.appendTextElement(ent, "date", ChangeLogWriter.OUTPUT_DATE.format(entry.getDate()));
        DOMUtils.appendTextElement(ent, "time", ChangeLogWriter.OUTPUT_TIME.format(entry.getDate()));
        DOMUtils.appendCDATAElement(ent, "author", entry.getAuthor());
        final Enumeration enumeration = entry.getFiles().elements();
        while (enumeration.hasMoreElements()) {
            final RCSFile file = enumeration.nextElement();
            final Element f = DOMUtils.createChildElement(ent, "file");
            DOMUtils.appendCDATAElement(f, "name", file.getName());
            DOMUtils.appendTextElement(f, "revision", file.getRevision());
            final String previousRevision = file.getPreviousRevision();
            if (previousRevision != null) {
                DOMUtils.appendTextElement(f, "prevrevision", previousRevision);
            }
        }
        DOMUtils.appendCDATAElement(ent, "msg", entry.getComment());
        ChangeLogWriter.DOM_WRITER.write(ent, output, 1, "\t");
    }
    
    static {
        OUTPUT_DATE = new SimpleDateFormat("yyyy-MM-dd");
        OUTPUT_TIME = new SimpleDateFormat("HH:mm");
        DOM_WRITER = new DOMElementWriter();
        final TimeZone utc = TimeZone.getTimeZone("UTC");
        ChangeLogWriter.OUTPUT_DATE.setTimeZone(utc);
        ChangeLogWriter.OUTPUT_TIME.setTimeZone(utc);
    }
}
