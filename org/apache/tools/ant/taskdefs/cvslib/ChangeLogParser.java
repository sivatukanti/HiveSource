// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.cvslib;

import java.util.TimeZone;
import java.util.Locale;
import java.text.ParseException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import org.apache.tools.ant.taskdefs.AbstractCvsTask;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.List;
import org.apache.tools.ant.util.CollectionUtils;
import java.util.Hashtable;
import java.text.SimpleDateFormat;

class ChangeLogParser
{
    private static final int GET_FILE = 1;
    private static final int GET_DATE = 2;
    private static final int GET_COMMENT = 3;
    private static final int GET_REVISION = 4;
    private static final int GET_PREVIOUS_REV = 5;
    private static final SimpleDateFormat INPUT_DATE;
    private static final SimpleDateFormat CVS1129_INPUT_DATE;
    private String file;
    private String date;
    private String author;
    private String comment;
    private String revision;
    private String previousRevision;
    private int status;
    private final Hashtable entries;
    private final boolean remote;
    private final String[] moduleNames;
    private final int[] moduleNameLengths;
    
    public ChangeLogParser() {
        this(false, "", CollectionUtils.EMPTY_LIST);
    }
    
    public ChangeLogParser(final boolean remote, final String packageName, final List modules) {
        this.status = 1;
        this.entries = new Hashtable();
        this.remote = remote;
        final ArrayList names = new ArrayList();
        if (packageName != null) {
            final StringTokenizer tok = new StringTokenizer(packageName);
            while (tok.hasMoreTokens()) {
                names.add(tok.nextToken());
            }
        }
        for (final AbstractCvsTask.Module m : modules) {
            names.add(m.getName());
        }
        this.moduleNames = names.toArray(new String[names.size()]);
        this.moduleNameLengths = new int[this.moduleNames.length];
        for (int i = 0; i < this.moduleNames.length; ++i) {
            this.moduleNameLengths[i] = this.moduleNames[i].length();
        }
    }
    
    public CVSEntry[] getEntrySetAsArray() {
        final CVSEntry[] array = new CVSEntry[this.entries.size()];
        int i = 0;
        final Enumeration e = this.entries.elements();
        while (e.hasMoreElements()) {
            array[i++] = e.nextElement();
        }
        return array;
    }
    
    public void stdout(final String line) {
        switch (this.status) {
            case 1: {
                this.reset();
                this.processFile(line);
                break;
            }
            case 4: {
                this.processRevision(line);
                break;
            }
            case 2: {
                this.processDate(line);
                break;
            }
            case 3: {
                this.processComment(line);
                break;
            }
            case 5: {
                this.processGetPreviousRevision(line);
                break;
            }
        }
    }
    
    private void processComment(final String line) {
        final String lineSeparator = System.getProperty("line.separator");
        if (line.equals("=============================================================================")) {
            final int end = this.comment.length() - lineSeparator.length();
            this.comment = this.comment.substring(0, end);
            this.saveEntry();
            this.status = 1;
        }
        else if (line.equals("----------------------------")) {
            final int end = this.comment.length() - lineSeparator.length();
            this.comment = this.comment.substring(0, end);
            this.status = 5;
        }
        else {
            this.comment = this.comment + line + lineSeparator;
        }
    }
    
    private void processFile(final String line) {
        if (!this.remote && line.startsWith("Working file:")) {
            this.file = line.substring(14, line.length());
            this.status = 4;
        }
        else if (this.remote && line.startsWith("RCS file:")) {
            int startOfFileName = 0;
            for (int i = 0; i < this.moduleNames.length; ++i) {
                final int index = line.indexOf(this.moduleNames[i]);
                if (index >= 0) {
                    startOfFileName = index + this.moduleNameLengths[i] + 1;
                    break;
                }
            }
            final int endOfFileName = line.indexOf(",v");
            if (endOfFileName == -1) {
                this.file = line.substring(startOfFileName);
            }
            else {
                this.file = line.substring(startOfFileName, endOfFileName);
            }
            this.status = 4;
        }
    }
    
    private void processRevision(final String line) {
        if (line.startsWith("revision")) {
            this.revision = line.substring(9);
            this.status = 2;
        }
        else if (line.startsWith("======")) {
            this.status = 1;
        }
    }
    
    private void processDate(final String line) {
        if (line.startsWith("date:")) {
            final int endOfDateIndex = line.indexOf(59);
            this.date = line.substring("date: ".length(), endOfDateIndex);
            final int startOfAuthorIndex = line.indexOf("author: ", endOfDateIndex + 1);
            final int endOfAuthorIndex = line.indexOf(59, startOfAuthorIndex + 1);
            this.author = line.substring("author: ".length() + startOfAuthorIndex, endOfAuthorIndex);
            this.status = 3;
            this.comment = "";
        }
    }
    
    private void processGetPreviousRevision(final String line) {
        if (!line.startsWith("revision ")) {
            throw new IllegalStateException("Unexpected line from CVS: " + line);
        }
        this.previousRevision = line.substring("revision ".length());
        this.saveEntry();
        this.revision = this.previousRevision;
        this.status = 2;
    }
    
    private void saveEntry() {
        final String entryKey = this.date + this.author + this.comment;
        CVSEntry entry;
        if (!this.entries.containsKey(entryKey)) {
            final Date dateObject = this.parseDate(this.date);
            entry = new CVSEntry(dateObject, this.author, this.comment);
            this.entries.put(entryKey, entry);
        }
        else {
            entry = this.entries.get(entryKey);
        }
        entry.addFile(this.file, this.revision, this.previousRevision);
    }
    
    private Date parseDate(final String date) {
        try {
            return ChangeLogParser.INPUT_DATE.parse(date);
        }
        catch (ParseException e) {
            try {
                return ChangeLogParser.CVS1129_INPUT_DATE.parse(date);
            }
            catch (ParseException e2) {
                throw new IllegalStateException("Invalid date format: " + date);
            }
        }
    }
    
    public void reset() {
        this.file = null;
        this.date = null;
        this.author = null;
        this.comment = null;
        this.revision = null;
        this.previousRevision = null;
    }
    
    static {
        INPUT_DATE = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.US);
        CVS1129_INPUT_DATE = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z", Locale.US);
        final TimeZone utc = TimeZone.getTimeZone("UTC");
        ChangeLogParser.INPUT_DATE.setTimeZone(utc);
        ChangeLogParser.CVS1129_INPUT_DATE.setTimeZone(utc);
    }
}
