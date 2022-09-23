// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.mail.mbox;

import java.util.StringTokenizer;
import javax.mail.URLName;
import java.io.ByteArrayOutputStream;
import javax.mail.internet.InternetHeaders;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.BufferedInputStream;
import javax.mail.Address;
import java.util.Date;
import javax.mail.internet.InternetAddress;
import java.io.PrintStream;
import java.io.InputStream;
import java.io.FileInputStream;
import javax.mail.internet.MimeMessage;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import javax.mail.Message;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileOutputStream;
import javax.mail.Flags;
import javax.mail.MessagingException;
import java.io.File;
import javax.mail.Store;
import java.util.Vector;
import javax.mail.Folder;

public class MboxFolder extends Folder
{
    private String name;
    private boolean is_inbox;
    private int total;
    private boolean opened;
    private Vector message_cache;
    private MboxStore mstore;
    private MailFile folder;
    private long file_size;
    private long saved_file_size;
    private MboxMessage special_imap_message;
    
    public MboxFolder(final MboxStore store, final String name) {
        super(store);
        this.is_inbox = false;
        this.opened = false;
        this.mstore = store;
        this.name = name;
        if (name != null && name.equalsIgnoreCase("INBOX")) {
            this.is_inbox = true;
        }
        this.folder = this.mstore.getMailFile((name == null) ? "~" : name);
        if (this.folder.exists()) {
            this.saved_file_size = this.folder.length();
        }
        else {
            this.saved_file_size = -1L;
        }
    }
    
    public char getSeparator() {
        return File.separatorChar;
    }
    
    public Folder[] list(final String pattern) throws MessagingException {
        if (!this.folder.isDirectory()) {
            throw new MessagingException("not a directory");
        }
        if (this.name == null) {
            return this.list(null, pattern, true);
        }
        return this.list(this.name + File.separator, pattern, false);
    }
    
    protected Folder[] list(String ref, String pattern, final boolean fromStore) throws MessagingException {
        if (ref != null && ref.length() == 0) {
            ref = null;
        }
        String refdir = null;
        String realdir = null;
        pattern = canonicalize(ref, pattern);
        int i;
        if ((i = indexOfAny(pattern, "%*")) >= 0) {
            refdir = pattern.substring(0, i);
        }
        else {
            refdir = pattern;
        }
        if ((i = refdir.lastIndexOf(File.separatorChar)) >= 0) {
            refdir = refdir.substring(0, i + 1);
            realdir = this.mstore.mb.filename(this.mstore.user, refdir);
        }
        else if (refdir.length() == 0 || refdir.charAt(0) != '~') {
            refdir = null;
            realdir = this.mstore.home;
        }
        else {
            realdir = this.mstore.mb.filename(this.mstore.user, refdir);
        }
        final Vector flist = new Vector();
        this.listWork(realdir, refdir, pattern, fromStore ? 0 : 1, flist);
        if (match.path("INBOX", pattern, '\0')) {
            flist.addElement("INBOX");
        }
        Folder[] fl;
        for (fl = new Folder[flist.size()], i = 0; i < fl.length; ++i) {
            fl[i] = this.createFolder(this.mstore, flist.elementAt(i));
        }
        return fl;
    }
    
    public String getName() {
        if (this.name == null) {
            return "";
        }
        if (this.is_inbox) {
            return "INBOX";
        }
        return this.folder.getName();
    }
    
    public String getFullName() {
        if (this.name == null) {
            return "";
        }
        return this.name;
    }
    
    public Folder getParent() {
        if (this.name == null) {
            return null;
        }
        if (this.is_inbox) {
            return this.createFolder(this.mstore, null);
        }
        return this.createFolder(this.mstore, this.folder.getParent());
    }
    
    public boolean exists() {
        return this.folder.exists();
    }
    
    public int getType() {
        if (this.folder.isDirectory()) {
            return 2;
        }
        return 1;
    }
    
    public Flags getPermanentFlags() {
        final MboxStore mstore = this.mstore;
        return MboxStore.permFlags;
    }
    
    public synchronized boolean hasNewMessages() {
        if (!(this.folder instanceof UNIXFile)) {
            long current_size;
            if (this.folder.exists()) {
                current_size = this.folder.length();
            }
            else {
                current_size = -1L;
            }
            if (this.saved_file_size < 0L) {
                this.saved_file_size = current_size;
            }
            return current_size > this.saved_file_size;
        }
        final UNIXFile f = (UNIXFile)this.folder;
        if (f.length() > 0L) {
            final long atime = f.lastAccessed();
            final long mtime = f.lastModified();
            return atime < mtime;
        }
        return false;
    }
    
    public synchronized Folder getFolder(final String name) throws MessagingException {
        if (this.folder.exists() && !this.folder.isDirectory()) {
            throw new MessagingException("not a directory");
        }
        Folder f;
        if (this.name != null) {
            f = this.createFolder(this.mstore, this.name + File.separator + name);
        }
        else {
            f = this.createFolder(this.mstore, name);
        }
        return f;
    }
    
    public synchronized boolean create(final int type) throws MessagingException {
        Label_0230: {
            switch (type) {
                case 2: {
                    if (!this.folder.mkdirs()) {
                        return false;
                    }
                    break Label_0230;
                }
                case 1: {
                    if (this.folder.exists()) {
                        return false;
                    }
                    try {
                        new FileOutputStream((File)this.folder).close();
                        break Label_0230;
                    }
                    catch (FileNotFoundException fe) {
                        final File parent = new File(this.folder.getParent());
                        if (!parent.mkdirs()) {
                            throw new MessagingException("can't create folder: " + this.name);
                        }
                        try {
                            new FileOutputStream((File)this.folder).close();
                        }
                        catch (IOException ex3) {
                            throw new MessagingException("can't create folder: " + this.name, ex3);
                        }
                        break Label_0230;
                    }
                    catch (IOException e) {
                        throw new MessagingException("can't create folder: " + this.name, e);
                    }
                    break;
                }
            }
            throw new MessagingException("type not supported");
        }
        this.notifyFolderListeners(1);
        return true;
    }
    
    public synchronized boolean delete(final boolean recurse) throws MessagingException {
        this.checkClosed();
        if (this.name == null) {
            throw new MessagingException("can't delete default folder");
        }
        if (this.folder.delete()) {
            this.notifyFolderListeners(2);
            return true;
        }
        return false;
    }
    
    public synchronized boolean renameTo(final Folder f) throws MessagingException {
        this.checkClosed();
        if (this.name == null) {
            throw new MessagingException("can't rename default folder");
        }
        if (!(f instanceof MboxFolder)) {
            throw new MessagingException("can't rename to: " + f.getName());
        }
        final String newname = ((MboxFolder)f).folder.getPath();
        if (this.folder.renameTo(new File(this.folder.getPath(), newname))) {
            this.notifyFolderRenamedListeners(f);
            return true;
        }
        return false;
    }
    
    void checkOpen() throws IllegalStateException {
        if (!this.opened) {
            throw new IllegalStateException("Folder is not Open");
        }
    }
    
    void checkClosed() throws IllegalStateException {
        if (this.opened) {
            throw new IllegalStateException("Folder is Open");
        }
    }
    
    void checkReadable() throws IllegalStateException {
        if (!this.opened || (this.mode != 1 && this.mode != 2)) {
            throw new IllegalStateException("Folder is not Readable");
        }
    }
    
    void checkWritable() throws IllegalStateException {
        if (!this.opened || this.mode != 2) {
            throw new IllegalStateException("Folder is not Writable");
        }
    }
    
    public boolean isOpen() {
        return this.opened;
    }
    
    public synchronized void open(final int mode) throws MessagingException {
        if (this.opened) {
            throw new IllegalStateException("Folder is already Open");
        }
        switch (this.mode = mode) {
            default: {
                if (!this.folder.canWrite()) {
                    throw new MessagingException("Open Failure, can't write");
                }
            }
            case 1: {
                if (!this.folder.canRead()) {
                    throw new MessagingException("Open Failure, can't read");
                }
                if (this.is_inbox && this.folder instanceof InboxFile) {
                    final InboxFile inf = (InboxFile)this.folder;
                    if (!inf.openLock((mode == 2) ? "rw" : "r")) {
                        throw new MessagingException("Failed to lock INBOX");
                    }
                }
                if (!this.folder.lock("r")) {
                    throw new MessagingException("Failed to lock folder: " + this.name);
                }
                this.message_cache = new Vector();
                this.total = 0;
                Message[] msglist = null;
                try {
                    this.saved_file_size = this.folder.length();
                    msglist = this.load(0L, false);
                }
                catch (IOException e) {
                    throw new MessagingException("IOException", e);
                }
                finally {
                    this.folder.unlock();
                }
                this.notifyConnectionListeners(1);
                if (msglist != null) {
                    this.notifyMessageAddedListeners(msglist);
                }
                this.opened = true;
            }
        }
    }
    
    public synchronized void close(final boolean expunge) throws MessagingException {
        this.checkOpen();
        try {
            if (this.mode == 2) {
                try {
                    this.writeFolder(true, expunge);
                }
                catch (IOException e) {
                    throw new MessagingException("I/O Exception", e);
                }
            }
            this.message_cache = null;
        }
        finally {
            this.opened = false;
            if (this.is_inbox && this.folder instanceof InboxFile) {
                final InboxFile inf = (InboxFile)this.folder;
                inf.closeLock();
            }
            this.notifyConnectionListeners(3);
        }
    }
    
    protected int writeFolder(final boolean closing, final boolean expunge) throws IOException, MessagingException {
        int modified = 0;
        int deleted = 0;
        int recent = 0;
        for (int msgno = 1; msgno <= this.total; ++msgno) {
            final MboxMessage msg = this.message_cache.elementAt(msgno - 1);
            final Flags flags = msg.getFlags();
            if (msg.isModified() || !msg.origFlags.equals(flags)) {
                ++modified;
            }
            if (flags.contains(Flags.Flag.DELETED)) {
                ++deleted;
            }
            if (flags.contains(Flags.Flag.RECENT)) {
                ++recent;
            }
        }
        if ((!closing || recent == 0) && (!expunge || deleted == 0) && modified == 0) {
            return 0;
        }
        if (!this.folder.lock("rw")) {
            throw new MessagingException("Failed to lock folder: " + this.name);
        }
        final int oldtotal = this.total;
        Message[] msglist = null;
        if (this.folder.length() != this.file_size) {
            msglist = this.load(this.file_size, !closing);
        }
        final OutputStream os = new BufferedOutputStream(new FileOutputStream((File)this.folder));
        int wr = 0;
        boolean keep = true;
        try {
            if (this.special_imap_message != null) {
                writeMboxMessage(this.special_imap_message, os);
            }
            for (int msgno2 = 1; msgno2 <= this.total; ++msgno2) {
                final MboxMessage msg2 = this.message_cache.elementAt(msgno2 - 1);
                if (!expunge || !msg2.isSet(Flags.Flag.DELETED)) {
                    if (closing && msgno2 <= oldtotal && msg2.isSet(Flags.Flag.RECENT)) {
                        msg2.setFlag(Flags.Flag.RECENT, false);
                    }
                    writeMboxMessage(msg2, os);
                    this.folder.touchlock();
                    ++wr;
                }
            }
            final long length = this.folder.length();
            this.saved_file_size = length;
            this.file_size = length;
            if (wr == 0 && closing) {
                final String skeep = ((MboxStore)this.store).getSession().getProperty("mail.mbox.deleteEmpty");
                if (skeep != null && skeep.equalsIgnoreCase("true")) {
                    keep = false;
                }
            }
        }
        catch (IOException e) {
            throw e;
        }
        catch (MessagingException e2) {
            throw e2;
        }
        catch (Exception e3) {
            e3.printStackTrace();
            throw new MessagingException("unexpected exception " + e3);
        }
        finally {
            try {
                os.close();
                if (!keep) {
                    this.folder.delete();
                    this.file_size = 0L;
                }
            }
            catch (IOException ex) {}
            if (keep) {
                try {
                    Thread.sleep(1000L);
                }
                catch (InterruptedException ex2) {}
                InputStream is = null;
                try {
                    is = new FileInputStream((File)this.folder);
                    is.read();
                }
                catch (IOException ex3) {}
                try {
                    if (is != null) {
                        is.close();
                    }
                    is = null;
                }
                catch (IOException ex4) {}
            }
            this.folder.unlock();
            if (msglist != null) {
                this.notifyMessageAddedListeners(msglist);
            }
        }
        return wr;
    }
    
    public static void writeMboxMessage(final MimeMessage msg, OutputStream os) throws IOException, MessagingException {
        try {
            if (msg instanceof MboxMessage) {
                ((MboxMessage)msg).writeToFile(os);
            }
            else {
                final ContentLengthCounter cos = new ContentLengthCounter();
                final NewlineOutputStream nos = new NewlineOutputStream(cos);
                msg.writeTo(nos);
                nos.flush();
                os = new NewlineOutputStream(os);
                os = new ContentLengthUpdater(os, cos.getSize());
                final PrintStream pos = new PrintStream(os);
                pos.println(getUnixFrom(msg));
                msg.writeTo(pos);
                pos.println();
                pos.flush();
            }
        }
        catch (MessagingException me) {
            throw me;
        }
        catch (IOException ioe) {
            throw ioe;
        }
        catch (Exception ex) {}
    }
    
    protected static String getUnixFrom(final MimeMessage msg) {
        String from;
        Date ddate;
        try {
            final Address[] afrom;
            if ((afrom = msg.getFrom()) == null || !(afrom[0] instanceof InternetAddress) || (from = ((InternetAddress)afrom[0]).getAddress()) == null) {
                from = "UNKNOWN";
            }
            if ((ddate = msg.getReceivedDate()) == null || (ddate = msg.getSentDate()) == null) {
                ddate = new Date();
            }
        }
        catch (MessagingException e) {
            from = "UNKNOWN";
            ddate = new Date();
        }
        final String date = ddate.toString();
        return "From " + from + " " + date.substring(0, 20) + date.substring(24);
    }
    
    public synchronized int getMessageCount() throws MessagingException {
        if (!this.opened) {
            return -1;
        }
        boolean locked = false;
        Message[] msglist = null;
        try {
            if (this.folder.length() != this.file_size) {
                if (!this.folder.lock("r")) {
                    throw new MessagingException("Failed to lock folder: " + this.name);
                }
                locked = true;
                msglist = this.load(this.file_size, true);
            }
        }
        catch (IOException e) {
            throw new MessagingException("I/O Exception", e);
        }
        finally {
            if (locked) {
                this.folder.unlock();
                if (msglist != null) {
                    this.notifyMessageAddedListeners(msglist);
                }
            }
        }
        return this.total;
    }
    
    public synchronized Message getMessage(final int msgno) throws MessagingException {
        this.checkReadable();
        MboxMessage m = null;
        if (msgno <= this.total) {
            m = this.message_cache.elementAt(msgno - 1);
        }
        return m;
    }
    
    public synchronized void appendMessages(final Message[] msgs) throws MessagingException {
        if (!this.folder.lock("rw")) {
            throw new MessagingException("Failed to lock folder: " + this.name);
        }
        OutputStream os = null;
        boolean err = false;
        try {
            os = new BufferedOutputStream(new FileOutputStream(((File)this.folder).getPath(), true));
            for (int i = 0; i < msgs.length; ++i) {
                if (msgs[i] instanceof MimeMessage) {
                    writeMboxMessage((MimeMessage)msgs[i], os);
                    this.folder.touchlock();
                }
                else {
                    err = true;
                }
            }
        }
        catch (IOException e) {
            throw new MessagingException("I/O Exception", e);
        }
        catch (MessagingException e2) {
            throw e2;
        }
        catch (Exception e3) {
            e3.printStackTrace();
            throw new MessagingException("unexpected exception " + e3);
        }
        finally {
            if (os != null) {
                try {
                    os.close();
                }
                catch (IOException ex) {}
            }
            this.folder.unlock();
        }
        if (this.opened) {
            this.getMessageCount();
        }
        if (err) {
            throw new MessagingException("Can't append non-Mime message");
        }
    }
    
    public synchronized Message[] expunge() throws MessagingException {
        this.checkWritable();
        int wr = this.total;
        try {
            wr = this.writeFolder(false, true);
        }
        catch (IOException e) {
            throw new MessagingException("expunge failed", e);
        }
        if (wr == 0) {
            return new Message[0];
        }
        int del = 0;
        final Message[] msglist = new Message[this.total - wr];
        int msgno = 1;
        while (msgno <= this.total) {
            final MboxMessage msg = this.message_cache.elementAt(msgno - 1);
            if (msg.isSet(Flags.Flag.DELETED)) {
                msglist[del] = msg;
                ++del;
                this.message_cache.removeElementAt(msgno - 1);
                --this.total;
            }
            else {
                msg.setMessageNumber(msgno);
                ++msgno;
            }
        }
        if (del != msglist.length) {
            throw new MessagingException("expunge delete count wrong");
        }
        this.notifyMessageRemovedListeners(true, msglist);
        return msglist;
    }
    
    private Message[] load(final long offset, final boolean notify) throws MessagingException, IOException {
        final int oldtotal = this.total;
        try {
            boolean first = offset == 0L;
            final BufferedInputStream in = new BufferedInputStream(new FileInputStream(this.folder.getFD()), 8192);
            this.skipFully(in, offset);
            while (true) {
                final MboxMessage msg = this.loadMessage(in, this.total, this.mode == 2);
                if (first) {
                    first = false;
                    if (msg.getHeader("X-IMAP") != null) {
                        this.special_imap_message = msg;
                        continue;
                    }
                }
                msg.setMessageNumber(++this.total);
                this.message_cache.addElement(msg);
            }
        }
        catch (EOFException e) {
            this.file_size = this.folder.length();
            if (notify) {
                final Message[] msglist = new Message[this.total - oldtotal];
                for (int i = oldtotal, j = 0; i < this.total; ++i, ++j) {
                    msglist[j] = this.message_cache.elementAt(i);
                }
                return msglist;
            }
            return null;
        }
    }
    
    private MboxMessage loadMessage(final BufferedInputStream is, final int msgno, final boolean writable) throws MessagingException, IOException {
        final DataInputStream in = new DataInputStream(is);
        String unix_from = null;
        String line;
        while ((line = in.readLine()) != null) {
            if (line.trim().length() == 0) {
                continue;
            }
            if (!line.startsWith("From ")) {
                throw new MessagingException("Garbage in mailbox: " + line);
            }
            unix_from = line;
            final int i = unix_from.indexOf(32, 5);
            if (i < 0) {
                continue;
            }
            break;
        }
        if (unix_from == null) {
            throw new EOFException("end of mailbox");
        }
        final InternetHeaders hdrs = new InternetHeaders(is);
        byte[] content = null;
        try {
            final int len;
            if ((len = this.contentLength(hdrs)) >= 0) {
                content = new byte[len];
                in.readFully(content);
            }
            else {
                final ByteArrayOutputStream buf = new ByteArrayOutputStream();
                int b;
                while ((b = is.read()) >= 0) {
                    if (b == 13 || b == 10) {
                        is.mark(6);
                        if (b == 13 && is.read() != 10) {
                            is.reset();
                            is.mark(5);
                        }
                        if (is.read() == 70 && is.read() == 114 && is.read() == 111 && is.read() == 109 && is.read() == 32) {
                            is.reset();
                            break;
                        }
                        is.reset();
                    }
                    buf.write(b);
                }
                content = buf.toByteArray();
            }
        }
        catch (EOFException ex) {}
        return new MboxMessage(this, hdrs, content, msgno, unix_from, writable);
    }
    
    private int contentLength(final InternetHeaders hdrs) {
        int len = -1;
        final String[] cl = hdrs.getHeader("Content-Length");
        try {
            if (cl != null && cl[0] != null) {
                len = Integer.parseInt(cl[0]);
            }
        }
        catch (NumberFormatException ex) {}
        return len;
    }
    
    private void skipFully(final InputStream in, long offset) throws IOException {
        while (offset > 0L) {
            final long cur = in.skip(offset);
            if (cur <= 0L) {
                throw new EOFException("can't skip");
            }
            offset -= cur;
        }
    }
    
    protected void notifyMessageChangedListeners(final int type, final Message m) {
        super.notifyMessageChangedListeners(type, m);
    }
    
    public URLName getURLName() {
        final URLName storeURL = this.getStore().getURLName();
        if (this.name == null) {
            return storeURL;
        }
        final char separator = this.getSeparator();
        final String fullname = this.getFullName();
        final StringBuffer encodedName = new StringBuffer();
        final StringTokenizer tok = new StringTokenizer(fullname, Character.toString(separator), true);
        while (tok.hasMoreTokens()) {
            final String s = tok.nextToken();
            if (s.charAt(0) == separator) {
                encodedName.append("/");
            }
            else {
                encodedName.append(s);
            }
        }
        return new URLName(storeURL.getProtocol(), storeURL.getHost(), storeURL.getPort(), encodedName.toString(), storeURL.getUsername(), null);
    }
    
    protected Folder createFolder(final MboxStore store, final String name) {
        return new MboxFolder(store, name);
    }
    
    private static String canonicalize(final String ref, final String pat) {
        if (ref == null) {
            return pat;
        }
        try {
            if (pat.length() == 0) {
                return ref;
            }
            if (pat.charAt(0) == File.separatorChar) {
                return ref.substring(0, ref.indexOf(File.separatorChar)) + pat;
            }
            return ref + pat;
        }
        catch (StringIndexOutOfBoundsException e) {
            return pat;
        }
    }
    
    private static int indexOfAny(final String s, final String any) {
        try {
            for (int len = s.length(), i = 0; i < len; ++i) {
                if (any.indexOf(s.charAt(i)) >= 0) {
                    return i;
                }
            }
            return -1;
        }
        catch (StringIndexOutOfBoundsException e) {
            return -1;
        }
    }
    
    private void listWork(String realdir, final String dir, final String pat, final int level, final Vector flist) {
        final File fdir = new File(realdir);
        String[] sl;
        try {
            sl = fdir.list();
        }
        catch (SecurityException e) {
            return;
        }
        if (level == 0 && dir != null && match.path(dir, pat, File.separatorChar)) {
            flist.addElement(dir);
        }
        if (sl == null) {
            return;
        }
        if (realdir.charAt(realdir.length() - 1) != File.separatorChar) {
            realdir += File.separator;
        }
        for (int i = 0; i < sl.length; ++i) {
            if (sl[i].charAt(0) != '.') {
                final String md = realdir + sl[i];
                final File mf = new File(md);
                if (mf.exists()) {
                    String name;
                    if (dir != null) {
                        name = dir + sl[i];
                    }
                    else {
                        name = sl[i];
                    }
                    if (mf.isDirectory()) {
                        if (match.path(name, pat, File.separatorChar)) {
                            flist.addElement(name);
                            name += File.separator;
                        }
                        else {
                            name += File.separator;
                            if (match.path(name, pat, File.separatorChar)) {
                                flist.addElement(name);
                            }
                        }
                        if (match.dir(name, pat, File.separatorChar)) {
                            this.listWork(md, name, pat, level + 1, flist);
                        }
                    }
                    else if (match.path(name, pat, File.separatorChar)) {
                        flist.addElement(name);
                    }
                }
            }
        }
    }
}
