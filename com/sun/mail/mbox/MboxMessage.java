// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.mail.mbox;

import java.io.PrintStream;
import java.util.StringTokenizer;
import javax.activation.DataSource;
import javax.mail.internet.MimePart;
import javax.mail.internet.MimePartDataSource;
import javax.activation.DataHandler;
import javax.mail.Message;
import javax.mail.internet.AddressException;
import javax.mail.Address;
import javax.mail.Folder;
import javax.mail.internet.InternetHeaders;
import java.io.IOException;
import javax.mail.MessagingException;
import java.io.DataInputStream;
import java.io.BufferedInputStream;
import java.io.InputStream;
import javax.mail.Session;
import java.io.OutputStream;
import java.util.Date;
import javax.mail.internet.InternetAddress;
import javax.mail.Flags;
import javax.mail.internet.MimeMessage;

public class MboxMessage extends MimeMessage
{
    boolean writable;
    Flags origFlags;
    String unix_from;
    InternetAddress unix_from_user;
    Date rcvDate;
    int lineCount;
    private static OutputStream nullOutputStream;
    
    public MboxMessage(final Session session, final InputStream is) throws MessagingException, IOException {
        super(session);
        this.writable = false;
        this.lineCount = -1;
        BufferedInputStream bis;
        if (is instanceof BufferedInputStream) {
            bis = (BufferedInputStream)is;
        }
        else {
            bis = new BufferedInputStream(is);
        }
        final DataInputStream dis = new DataInputStream(bis);
        bis.mark(1024);
        final String line = dis.readLine();
        if (line != null && line.startsWith("From ")) {
            this.unix_from = line;
        }
        else {
            bis.reset();
        }
        this.parse(bis);
        this.saved = true;
    }
    
    public MboxMessage(final MboxFolder folder, final InternetHeaders hdrs, final byte[] content, final int msgno, final String unix_from, final boolean writable) throws MessagingException {
        super(folder, hdrs, content, msgno);
        this.writable = false;
        this.lineCount = -1;
        this.setFlagsFromHeaders();
        this.origFlags = this.getFlags();
        this.unix_from = unix_from;
        this.writable = writable;
    }
    
    public Address[] getFrom() throws MessagingException {
        Address[] ret = super.getFrom();
        if (ret == null) {
            final InternetAddress ia = this.getUnixFrom();
            if (ia != null) {
                ret = new InternetAddress[] { ia };
            }
        }
        return ret;
    }
    
    public synchronized InternetAddress getUnixFrom() throws MessagingException {
        if (this.unix_from_user == null && this.unix_from != null) {
            final int i = this.unix_from.indexOf(32, 5);
            if (i > 5) {
                try {
                    this.unix_from_user = new InternetAddress(this.unix_from.substring(5, i));
                }
                catch (AddressException ex) {}
            }
        }
        return (this.unix_from_user != null) ? ((InternetAddress)this.unix_from_user.clone()) : null;
    }
    
    public Date getReceivedDate() throws MessagingException {
        if (this.rcvDate == null && this.unix_from != null) {
            final int i = this.unix_from.indexOf(32, 5);
            if (i > 5) {
                try {
                    this.rcvDate = new Date(this.unix_from.substring(i));
                }
                catch (IllegalArgumentException ex) {}
            }
        }
        return (this.rcvDate == null) ? null : new Date(this.rcvDate.getTime());
    }
    
    public int getLineCount() throws MessagingException {
        if (this.lineCount < 0 && this.isMimeType("text/plain")) {
            final LineCounter lc = new LineCounter(MboxMessage.nullOutputStream);
            final boolean seen = this.isSet(Flags.Flag.SEEN);
            try {
                this.getDataHandler().writeTo(lc);
                this.lineCount = lc.getLineCount();
                lc.close();
            }
            catch (IOException ex) {}
            if (!seen) {
                this.setFlag(Flags.Flag.SEEN, false);
            }
        }
        return this.lineCount;
    }
    
    public void setFlags(final Flags newFlags, final boolean set) throws MessagingException {
        final Flags oldFlags = (Flags)this.flags.clone();
        super.setFlags(newFlags, set);
        if (!this.flags.equals(oldFlags)) {
            this.setHeadersFromFlags();
            if (this.folder != null) {
                ((MboxFolder)this.folder).notifyMessageChangedListeners(1, this);
            }
        }
    }
    
    public String getContentType() throws MessagingException {
        String ct = super.getContentType();
        if (ct.indexOf(47) < 0) {
            ct = SunV3BodyPart.MimeV3Map.toMime(ct);
        }
        return ct;
    }
    
    protected InputStream getContentStream() throws MessagingException {
        if (!this.isSet(Flags.Flag.SEEN)) {
            this.setFlag(Flags.Flag.SEEN, true);
        }
        return super.getContentStream();
    }
    
    public synchronized DataHandler getDataHandler() throws MessagingException {
        if (this.dh == null) {
            final String ct = this.getContentType();
            if (!ct.equalsIgnoreCase("multipart/x-sun-attachment")) {
                return super.getDataHandler();
            }
            this.dh = new DataHandler(new SunV3Multipart(new MimePartDataSource(this)), ct);
        }
        return this.dh;
    }
    
    protected void setMessageNumber(final int msgno) {
        super.setMessageNumber(msgno);
    }
    
    private synchronized void setFlagsFromHeaders() {
        this.flags = new Flags(Flags.Flag.RECENT);
        try {
            String s = this.getHeader("Status", null);
            if (s != null) {
                if (s.indexOf(82) >= 0) {
                    this.flags.add(Flags.Flag.SEEN);
                }
                if (s.indexOf(79) >= 0) {
                    this.flags.remove(Flags.Flag.RECENT);
                }
            }
            s = this.getHeader("X-Dt-Delete-Time", null);
            if (s != null) {
                this.flags.add(Flags.Flag.DELETED);
            }
            s = this.getHeader("X-Status", null);
            if (s != null) {
                if (s.indexOf(68) >= 0) {
                    this.flags.add(Flags.Flag.DELETED);
                }
                if (s.indexOf(70) >= 0) {
                    this.flags.add(Flags.Flag.FLAGGED);
                }
                if (s.indexOf(65) >= 0) {
                    this.flags.add(Flags.Flag.ANSWERED);
                }
                if (s.indexOf(84) >= 0) {
                    this.flags.add(Flags.Flag.DRAFT);
                }
            }
            s = this.getHeader("X-Keywords", null);
            if (s != null) {
                final StringTokenizer st = new StringTokenizer(s);
                while (st.hasMoreTokens()) {
                    this.flags.add(st.nextToken());
                }
            }
        }
        catch (MessagingException ex) {}
    }
    
    private synchronized void setHeadersFromFlags() {
        try {
            final StringBuffer status = new StringBuffer();
            if (this.flags.contains(Flags.Flag.SEEN)) {
                status.append('R');
            }
            if (!this.flags.contains(Flags.Flag.RECENT)) {
                status.append('O');
            }
            this.setHeader("Status", status.toString());
            boolean sims = false;
            String s = this.getHeader("X-Status", null);
            sims = (s != null && s.length() == 4 && s.indexOf(36) >= 0);
            status.setLength(0);
            if (this.flags.contains(Flags.Flag.DELETED)) {
                status.append('D');
            }
            else if (sims) {
                status.append('$');
            }
            if (this.flags.contains(Flags.Flag.FLAGGED)) {
                status.append('F');
            }
            else if (sims) {
                status.append('$');
            }
            if (this.flags.contains(Flags.Flag.ANSWERED)) {
                status.append('A');
            }
            else if (sims) {
                status.append('$');
            }
            if (this.flags.contains(Flags.Flag.DRAFT)) {
                status.append('T');
            }
            else if (sims) {
                status.append('$');
            }
            this.setHeader("X-Status", status.toString());
            final String[] userFlags = this.flags.getUserFlags();
            if (userFlags.length > 0) {
                status.setLength(0);
                for (int i = 0; i < userFlags.length; ++i) {
                    status.append(userFlags[i]).append(' ');
                }
                status.setLength(status.length() - 1);
                this.setHeader("X-Keywords", status.toString());
            }
            if (this.flags.contains(Flags.Flag.DELETED)) {
                s = this.getHeader("X-Dt-Delete-Time", null);
                if (s == null) {
                    this.setHeader("X-Dt-Delete-Time", "1");
                }
            }
        }
        catch (MessagingException ex) {}
    }
    
    protected void updateHeaders() throws MessagingException {
        super.updateHeaders();
        this.setHeadersFromFlags();
    }
    
    public void saveChanges() throws MessagingException {
        if (!this.writable) {
            throw new MessagingException("Message is read-only");
        }
        super.saveChanges();
        try {
            final ContentLengthCounter cos = new ContentLengthCounter();
            final OutputStream os = new NewlineOutputStream(cos);
            super.writeTo(os);
            os.flush();
            this.setHeader("Content-Length", String.valueOf(cos.getSize()));
        }
        catch (MessagingException e) {
            throw e;
        }
        catch (Exception e2) {
            throw new MessagingException("unexpected exception " + e2);
        }
    }
    
    boolean isModified() {
        return this.modified;
    }
    
    public void writeToFile(OutputStream os) throws IOException {
        try {
            if (this.getHeader("Content-Length") == null) {
                final ContentLengthCounter cos = new ContentLengthCounter();
                final OutputStream oos = new NewlineOutputStream(cos);
                super.writeTo(oos);
                oos.flush();
                this.setHeader("Content-Length", String.valueOf(cos.getSize()));
            }
            os = new NewlineOutputStream(os);
            final PrintStream pos = new PrintStream(os);
            pos.println(this.unix_from);
            super.writeTo(pos);
            pos.println();
        }
        catch (MessagingException e) {
            throw new IOException("unexpected exception " + e);
        }
    }
    
    static {
        MboxMessage.nullOutputStream = new OutputStream() {
            public void write(final int b) {
            }
            
            public void write(final byte[] b, final int off, final int len) {
            }
        };
    }
}
