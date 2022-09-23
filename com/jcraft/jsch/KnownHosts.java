// 
// Decompiled by Procyon v0.5.36
// 

package com.jcraft.jsch;

import java.io.OutputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.Vector;

public class KnownHosts implements HostKeyRepository
{
    private static final String _known_hosts = "known_hosts";
    private JSch jsch;
    private String known_hosts;
    private Vector pool;
    private MAC hmacsha1;
    private static final byte[] space;
    private static final byte[] cr;
    
    KnownHosts(final JSch jsch) {
        this.jsch = null;
        this.known_hosts = null;
        this.pool = null;
        this.hmacsha1 = null;
        this.jsch = jsch;
        this.hmacsha1 = this.getHMACSHA1();
        this.pool = new Vector();
    }
    
    void setKnownHosts(final String filename) throws JSchException {
        try {
            this.known_hosts = filename;
            final FileInputStream fis = new FileInputStream(Util.checkTilde(filename));
            this.setKnownHosts(fis);
        }
        catch (FileNotFoundException ex) {}
    }
    
    void setKnownHosts(final InputStream input) throws JSchException {
        this.pool.removeAllElements();
        final StringBuffer sb = new StringBuffer();
        final boolean error = false;
        try {
            final InputStream fis = input;
            String key = null;
            byte[] buf = new byte[1024];
            int bufl = 0;
        Block_4:
            while (true) {
                bufl = 0;
                while (true) {
                    final int j = fis.read();
                    if (j == -1) {
                        if (bufl == 0) {
                            break Block_4;
                        }
                        break;
                    }
                    else {
                        if (j == 13) {
                            continue;
                        }
                        if (j == 10) {
                            break;
                        }
                        if (buf.length <= bufl) {
                            if (bufl > 10240) {
                                break;
                            }
                            final byte[] newbuf = new byte[buf.length * 2];
                            System.arraycopy(buf, 0, newbuf, 0, buf.length);
                            buf = newbuf;
                        }
                        buf[bufl++] = (byte)j;
                    }
                }
                int j = 0;
                while (j < bufl) {
                    final byte i = buf[j];
                    if (i == 32 || i == 9) {
                        ++j;
                    }
                    else {
                        if (i == 35) {
                            this.addInvalidLine(Util.byte2str(buf, 0, bufl));
                            continue Block_4;
                        }
                        break;
                    }
                }
                if (j >= bufl) {
                    this.addInvalidLine(Util.byte2str(buf, 0, bufl));
                }
                else {
                    sb.setLength(0);
                    while (j < bufl) {
                        final byte i = buf[j++];
                        if (i == 32) {
                            break;
                        }
                        if (i == 9) {
                            break;
                        }
                        sb.append((char)i);
                    }
                    String host = sb.toString();
                    if (j >= bufl || host.length() == 0) {
                        this.addInvalidLine(Util.byte2str(buf, 0, bufl));
                    }
                    else {
                        while (j < bufl) {
                            final byte i = buf[j];
                            if (i != 32 && i != 9) {
                                break;
                            }
                            ++j;
                        }
                        String marker = "";
                        if (host.charAt(0) == '@') {
                            marker = host;
                            sb.setLength(0);
                            while (j < bufl) {
                                final byte i = buf[j++];
                                if (i == 32) {
                                    break;
                                }
                                if (i == 9) {
                                    break;
                                }
                                sb.append((char)i);
                            }
                            host = sb.toString();
                            if (j >= bufl || host.length() == 0) {
                                this.addInvalidLine(Util.byte2str(buf, 0, bufl));
                                continue;
                            }
                            while (j < bufl) {
                                final byte i = buf[j];
                                if (i != 32 && i != 9) {
                                    break;
                                }
                                ++j;
                            }
                        }
                        sb.setLength(0);
                        int type = -1;
                        while (j < bufl) {
                            final byte i = buf[j++];
                            if (i == 32) {
                                break;
                            }
                            if (i == 9) {
                                break;
                            }
                            sb.append((char)i);
                        }
                        final String tmp = sb.toString();
                        if (HostKey.name2type(tmp) != 6) {
                            type = HostKey.name2type(tmp);
                        }
                        else {
                            j = bufl;
                        }
                        if (j >= bufl) {
                            this.addInvalidLine(Util.byte2str(buf, 0, bufl));
                        }
                        else {
                            while (j < bufl) {
                                final byte i = buf[j];
                                if (i != 32 && i != 9) {
                                    break;
                                }
                                ++j;
                            }
                            sb.setLength(0);
                            while (j < bufl) {
                                final byte i = buf[j++];
                                if (i == 13) {
                                    continue;
                                }
                                if (i == 10) {
                                    break;
                                }
                                if (i == 32) {
                                    break;
                                }
                                if (i == 9) {
                                    break;
                                }
                                sb.append((char)i);
                            }
                            key = sb.toString();
                            if (key.length() == 0) {
                                this.addInvalidLine(Util.byte2str(buf, 0, bufl));
                            }
                            else {
                                while (j < bufl) {
                                    final byte i = buf[j];
                                    if (i != 32 && i != 9) {
                                        break;
                                    }
                                    ++j;
                                }
                                String comment = null;
                                if (j < bufl) {
                                    sb.setLength(0);
                                    while (j < bufl) {
                                        final byte i = buf[j++];
                                        if (i == 13) {
                                            continue;
                                        }
                                        if (i == 10) {
                                            break;
                                        }
                                        sb.append((char)i);
                                    }
                                    comment = sb.toString();
                                }
                                HostKey hk = null;
                                hk = new HashedHostKey(marker, host, type, Util.fromBase64(Util.str2byte(key), 0, key.length()), comment);
                                this.pool.addElement(hk);
                            }
                        }
                    }
                }
            }
            if (error) {
                throw new JSchException("KnownHosts: invalid format");
            }
        }
        catch (Exception e) {
            if (e instanceof JSchException) {
                throw (JSchException)e;
            }
            if (e instanceof Throwable) {
                throw new JSchException(e.toString(), e);
            }
            throw new JSchException(e.toString());
        }
        finally {
            try {
                input.close();
            }
            catch (IOException e2) {
                throw new JSchException(e2.toString(), e2);
            }
        }
    }
    
    private void addInvalidLine(final String line) throws JSchException {
        final HostKey hk = new HostKey(line, 6, null);
        this.pool.addElement(hk);
    }
    
    String getKnownHostsFile() {
        return this.known_hosts;
    }
    
    public String getKnownHostsRepositoryID() {
        return this.known_hosts;
    }
    
    public int check(final String host, final byte[] key) {
        int result = 1;
        if (host == null) {
            return result;
        }
        HostKey hk = null;
        try {
            hk = new HostKey(host, 0, key);
        }
        catch (JSchException e) {
            return result;
        }
        synchronized (this.pool) {
            for (int i = 0; i < this.pool.size(); ++i) {
                final HostKey _hk = this.pool.elementAt(i);
                if (_hk.isMatched(host) && _hk.type == hk.type) {
                    if (Util.array_equals(_hk.key, key)) {
                        return 0;
                    }
                    result = 2;
                }
            }
        }
        if (result == 1 && host.startsWith("[") && host.indexOf("]:") > 1) {
            return this.check(host.substring(1, host.indexOf("]:")), key);
        }
        return result;
    }
    
    public void add(final HostKey hostkey, final UserInfo userinfo) {
        final int type = hostkey.type;
        final String host = hostkey.getHost();
        final byte[] key = hostkey.key;
        HostKey hk = null;
        synchronized (this.pool) {
            for (int i = 0; i < this.pool.size(); ++i) {
                hk = this.pool.elementAt(i);
                if (!hk.isMatched(host) || hk.type == type) {}
            }
        }
        hk = hostkey;
        this.pool.addElement(hk);
        final String bar = this.getKnownHostsRepositoryID();
        if (bar != null) {
            boolean foo = true;
            File goo = new File(Util.checkTilde(bar));
            if (!goo.exists()) {
                foo = false;
                if (userinfo != null) {
                    foo = userinfo.promptYesNo(bar + " does not exist.\n" + "Are you sure you want to create it?");
                    goo = goo.getParentFile();
                    if (foo && goo != null && !goo.exists()) {
                        foo = userinfo.promptYesNo("The parent directory " + goo + " does not exist.\n" + "Are you sure you want to create it?");
                        if (foo) {
                            if (!goo.mkdirs()) {
                                userinfo.showMessage(goo + " has not been created.");
                                foo = false;
                            }
                            else {
                                userinfo.showMessage(goo + " has been succesfully created.\nPlease check its access permission.");
                            }
                        }
                    }
                    if (goo == null) {
                        foo = false;
                    }
                }
            }
            if (foo) {
                try {
                    this.sync(bar);
                }
                catch (Exception e) {
                    System.err.println("sync known_hosts: " + e);
                }
            }
        }
    }
    
    public HostKey[] getHostKey() {
        return this.getHostKey(null, null);
    }
    
    public HostKey[] getHostKey(final String host, final String type) {
        synchronized (this.pool) {
            final ArrayList v = new ArrayList();
            for (int i = 0; i < this.pool.size(); ++i) {
                final HostKey hk = this.pool.elementAt(i);
                if (hk.type != 6) {
                    if (host == null || (hk.isMatched(host) && (type == null || hk.getType().equals(type)))) {
                        v.add(hk);
                    }
                }
            }
            HostKey[] foo = new HostKey[v.size()];
            for (int j = 0; j < v.size(); ++j) {
                foo[j] = v.get(j);
            }
            if (host != null && host.startsWith("[") && host.indexOf("]:") > 1) {
                final HostKey[] tmp = this.getHostKey(host.substring(1, host.indexOf("]:")), type);
                if (tmp.length > 0) {
                    final HostKey[] bar = new HostKey[foo.length + tmp.length];
                    System.arraycopy(foo, 0, bar, 0, foo.length);
                    System.arraycopy(tmp, 0, bar, foo.length, tmp.length);
                    foo = bar;
                }
            }
            return foo;
        }
    }
    
    public void remove(final String host, final String type) {
        this.remove(host, type, null);
    }
    
    public void remove(final String host, final String type, final byte[] key) {
        boolean sync = false;
        synchronized (this.pool) {
            for (int i = 0; i < this.pool.size(); ++i) {
                final HostKey hk = this.pool.elementAt(i);
                if (host == null || (hk.isMatched(host) && (type == null || (hk.getType().equals(type) && (key == null || Util.array_equals(key, hk.key)))))) {
                    final String hosts = hk.getHost();
                    if (hosts.equals(host) || (hk instanceof HashedHostKey && ((HashedHostKey)hk).isHashed())) {
                        this.pool.removeElement(hk);
                    }
                    else {
                        hk.host = this.deleteSubString(hosts, host);
                    }
                    sync = true;
                }
            }
        }
        if (sync) {
            try {
                this.sync();
            }
            catch (Exception ex) {}
        }
    }
    
    protected void sync() throws IOException {
        if (this.known_hosts != null) {
            this.sync(this.known_hosts);
        }
    }
    
    protected synchronized void sync(final String foo) throws IOException {
        if (foo == null) {
            return;
        }
        final FileOutputStream fos = new FileOutputStream(Util.checkTilde(foo));
        this.dump(fos);
        fos.close();
    }
    
    void dump(final OutputStream out) throws IOException {
        try {
            synchronized (this.pool) {
                for (int i = 0; i < this.pool.size(); ++i) {
                    final HostKey hk = this.pool.elementAt(i);
                    final String marker = hk.getMarker();
                    final String host = hk.getHost();
                    final String type = hk.getType();
                    final String comment = hk.getComment();
                    if (type.equals("UNKNOWN")) {
                        out.write(Util.str2byte(host));
                        out.write(KnownHosts.cr);
                    }
                    else {
                        if (marker.length() != 0) {
                            out.write(Util.str2byte(marker));
                            out.write(KnownHosts.space);
                        }
                        out.write(Util.str2byte(host));
                        out.write(KnownHosts.space);
                        out.write(Util.str2byte(type));
                        out.write(KnownHosts.space);
                        out.write(Util.str2byte(hk.getKey()));
                        if (comment != null) {
                            out.write(KnownHosts.space);
                            out.write(Util.str2byte(comment));
                        }
                        out.write(KnownHosts.cr);
                    }
                }
            }
        }
        catch (Exception e) {
            System.err.println(e);
        }
    }
    
    private String deleteSubString(final String hosts, final String host) {
        int i = 0;
        final int hostlen = host.length();
        int hostslen;
        int j;
        for (hostslen = hosts.length(); i < hostslen; i = j + 1) {
            j = hosts.indexOf(44, i);
            if (j == -1) {
                break;
            }
            if (host.equals(hosts.substring(i, j))) {
                return hosts.substring(0, i) + hosts.substring(j + 1);
            }
        }
        if (hosts.endsWith(host) && hostslen - i == hostlen) {
            return hosts.substring(0, (hostlen == hostslen) ? 0 : (hostslen - hostlen - 1));
        }
        return hosts;
    }
    
    private MAC getHMACSHA1() {
        if (this.hmacsha1 == null) {
            try {
                final JSch jsch = this.jsch;
                final Class c = Class.forName(JSch.getConfig("hmac-sha1"));
                this.hmacsha1 = c.newInstance();
            }
            catch (Exception e) {
                System.err.println("hmacsha1: " + e);
            }
        }
        return this.hmacsha1;
    }
    
    HostKey createHashedHostKey(final String host, final byte[] key) throws JSchException {
        final HashedHostKey hhk = new HashedHostKey(host, key);
        hhk.hash();
        return hhk;
    }
    
    static {
        space = new byte[] { 32 };
        cr = Util.str2byte("\n");
    }
    
    class HashedHostKey extends HostKey
    {
        private static final String HASH_MAGIC = "|1|";
        private static final String HASH_DELIM = "|";
        private boolean hashed;
        byte[] salt;
        byte[] hash;
        
        HashedHostKey(final KnownHosts knownHosts, final String host, final byte[] key) throws JSchException {
            this(knownHosts, host, 0, key);
        }
        
        HashedHostKey(final KnownHosts knownHosts, final String host, final int type, final byte[] key) throws JSchException {
            this(knownHosts, "", host, type, key, null);
        }
        
        HashedHostKey(final String marker, final String host, final int type, final byte[] key, final String comment) throws JSchException {
            super(marker, host, type, key, comment);
            this.hashed = false;
            this.salt = null;
            this.hash = null;
            if (this.host.startsWith("|1|") && this.host.substring("|1|".length()).indexOf("|") > 0) {
                final String data = this.host.substring("|1|".length());
                final String _salt = data.substring(0, data.indexOf("|"));
                final String _hash = data.substring(data.indexOf("|") + 1);
                this.salt = Util.fromBase64(Util.str2byte(_salt), 0, _salt.length());
                this.hash = Util.fromBase64(Util.str2byte(_hash), 0, _hash.length());
                if (this.salt.length != 20 || this.hash.length != 20) {
                    this.salt = null;
                    this.hash = null;
                    return;
                }
                this.hashed = true;
            }
        }
        
        @Override
        boolean isMatched(final String _host) {
            if (!this.hashed) {
                return super.isMatched(_host);
            }
            final MAC macsha1 = KnownHosts.this.getHMACSHA1();
            try {
                synchronized (macsha1) {
                    macsha1.init(this.salt);
                    final byte[] foo = Util.str2byte(_host);
                    macsha1.update(foo, 0, foo.length);
                    final byte[] bar = new byte[macsha1.getBlockSize()];
                    macsha1.doFinal(bar, 0);
                    return Util.array_equals(this.hash, bar);
                }
            }
            catch (Exception e) {
                System.out.println(e);
                return false;
            }
        }
        
        boolean isHashed() {
            return this.hashed;
        }
        
        void hash() {
            if (this.hashed) {
                return;
            }
            final MAC macsha1 = KnownHosts.this.getHMACSHA1();
            if (this.salt == null) {
                final Random random = Session.random;
                synchronized (random) {
                    random.fill(this.salt = new byte[macsha1.getBlockSize()], 0, this.salt.length);
                }
            }
            try {
                synchronized (macsha1) {
                    macsha1.init(this.salt);
                    final byte[] foo = Util.str2byte(this.host);
                    macsha1.update(foo, 0, foo.length);
                    macsha1.doFinal(this.hash = new byte[macsha1.getBlockSize()], 0);
                }
            }
            catch (Exception ex) {}
            this.host = "|1|" + Util.byte2str(Util.toBase64(this.salt, 0, this.salt.length)) + "|" + Util.byte2str(Util.toBase64(this.hash, 0, this.hash.length));
            this.hashed = true;
        }
    }
}
