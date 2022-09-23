// 
// Decompiled by Procyon v0.5.36
// 

package com.jcraft.jsch;

import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;
import java.io.InterruptedIOException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Hashtable;
import java.io.OutputStream;
import java.io.InputStream;
import java.net.Socket;

public class Session implements Runnable
{
    static final int SSH_MSG_DISCONNECT = 1;
    static final int SSH_MSG_IGNORE = 2;
    static final int SSH_MSG_UNIMPLEMENTED = 3;
    static final int SSH_MSG_DEBUG = 4;
    static final int SSH_MSG_SERVICE_REQUEST = 5;
    static final int SSH_MSG_SERVICE_ACCEPT = 6;
    static final int SSH_MSG_KEXINIT = 20;
    static final int SSH_MSG_NEWKEYS = 21;
    static final int SSH_MSG_KEXDH_INIT = 30;
    static final int SSH_MSG_KEXDH_REPLY = 31;
    static final int SSH_MSG_KEX_DH_GEX_GROUP = 31;
    static final int SSH_MSG_KEX_DH_GEX_INIT = 32;
    static final int SSH_MSG_KEX_DH_GEX_REPLY = 33;
    static final int SSH_MSG_KEX_DH_GEX_REQUEST = 34;
    static final int SSH_MSG_GLOBAL_REQUEST = 80;
    static final int SSH_MSG_REQUEST_SUCCESS = 81;
    static final int SSH_MSG_REQUEST_FAILURE = 82;
    static final int SSH_MSG_CHANNEL_OPEN = 90;
    static final int SSH_MSG_CHANNEL_OPEN_CONFIRMATION = 91;
    static final int SSH_MSG_CHANNEL_OPEN_FAILURE = 92;
    static final int SSH_MSG_CHANNEL_WINDOW_ADJUST = 93;
    static final int SSH_MSG_CHANNEL_DATA = 94;
    static final int SSH_MSG_CHANNEL_EXTENDED_DATA = 95;
    static final int SSH_MSG_CHANNEL_EOF = 96;
    static final int SSH_MSG_CHANNEL_CLOSE = 97;
    static final int SSH_MSG_CHANNEL_REQUEST = 98;
    static final int SSH_MSG_CHANNEL_SUCCESS = 99;
    static final int SSH_MSG_CHANNEL_FAILURE = 100;
    private static final int PACKET_MAX_SIZE = 262144;
    private byte[] V_S;
    private byte[] V_C;
    private byte[] I_C;
    private byte[] I_S;
    private byte[] K_S;
    private byte[] session_id;
    private byte[] IVc2s;
    private byte[] IVs2c;
    private byte[] Ec2s;
    private byte[] Es2c;
    private byte[] MACc2s;
    private byte[] MACs2c;
    private int seqi;
    private int seqo;
    String[] guess;
    private Cipher s2ccipher;
    private Cipher c2scipher;
    private MAC s2cmac;
    private MAC c2smac;
    private byte[] s2cmac_result1;
    private byte[] s2cmac_result2;
    private Compression deflater;
    private Compression inflater;
    private IO io;
    private Socket socket;
    private int timeout;
    private volatile boolean isConnected;
    private boolean isAuthed;
    private Thread connectThread;
    private Object lock;
    boolean x11_forwarding;
    boolean agent_forwarding;
    InputStream in;
    OutputStream out;
    static Random random;
    Buffer buf;
    Packet packet;
    SocketFactory socket_factory;
    static final int buffer_margin = 128;
    private Hashtable config;
    private Proxy proxy;
    private UserInfo userinfo;
    private String hostKeyAlias;
    private int serverAliveInterval;
    private int serverAliveCountMax;
    private IdentityRepository identityRepository;
    private HostKeyRepository hostkeyRepository;
    protected boolean daemon_thread;
    private long kex_start_time;
    int max_auth_tries;
    int auth_failures;
    String host;
    String org_host;
    int port;
    String username;
    byte[] password;
    JSch jsch;
    private volatile boolean in_kex;
    private volatile boolean in_prompt;
    int[] uncompress_len;
    int[] compress_len;
    private int s2ccipher_size;
    private int c2scipher_size;
    Runnable thread;
    private GlobalRequestReply grr;
    private static final byte[] keepalivemsg;
    private static final byte[] nomoresessions;
    private HostKey hostkey;
    
    Session(final JSch jsch, final String username, final String host, final int port) throws JSchException {
        this.V_C = Util.str2byte("SSH-2.0-JSCH-0.1.54");
        this.seqi = 0;
        this.seqo = 0;
        this.guess = null;
        this.timeout = 0;
        this.isConnected = false;
        this.isAuthed = false;
        this.connectThread = null;
        this.lock = new Object();
        this.x11_forwarding = false;
        this.agent_forwarding = false;
        this.in = null;
        this.out = null;
        this.socket_factory = null;
        this.config = null;
        this.proxy = null;
        this.hostKeyAlias = null;
        this.serverAliveInterval = 0;
        this.serverAliveCountMax = 1;
        this.identityRepository = null;
        this.hostkeyRepository = null;
        this.daemon_thread = false;
        this.kex_start_time = 0L;
        this.max_auth_tries = 6;
        this.auth_failures = 0;
        this.host = "127.0.0.1";
        this.org_host = "127.0.0.1";
        this.port = 22;
        this.username = null;
        this.password = null;
        this.in_kex = false;
        this.in_prompt = false;
        this.uncompress_len = new int[1];
        this.compress_len = new int[1];
        this.s2ccipher_size = 8;
        this.c2scipher_size = 8;
        this.grr = new GlobalRequestReply();
        this.hostkey = null;
        this.jsch = jsch;
        this.buf = new Buffer();
        this.packet = new Packet(this.buf);
        this.username = username;
        this.host = host;
        this.org_host = host;
        this.port = port;
        this.applyConfig();
        if (this.username == null) {
            try {
                this.username = (String)System.getProperties().get("user.name");
            }
            catch (SecurityException ex) {}
        }
        if (this.username == null) {
            throw new JSchException("username is not given.");
        }
    }
    
    public void connect() throws JSchException {
        this.connect(this.timeout);
    }
    
    public void connect(final int connectTimeout) throws JSchException {
        if (this.isConnected) {
            throw new JSchException("session is already connected");
        }
        this.io = new IO();
        if (Session.random == null) {
            try {
                final Class c = Class.forName(this.getConfig("random"));
                Session.random = c.newInstance();
            }
            catch (Exception e) {
                throw new JSchException(e.toString(), e);
            }
        }
        Packet.setRandom(Session.random);
        if (JSch.getLogger().isEnabled(1)) {
            JSch.getLogger().log(1, "Connecting to " + this.host + " port " + this.port);
        }
        try {
            if (this.proxy == null) {
                InputStream in;
                OutputStream out;
                if (this.socket_factory == null) {
                    this.socket = Util.createSocket(this.host, this.port, connectTimeout);
                    in = this.socket.getInputStream();
                    out = this.socket.getOutputStream();
                }
                else {
                    this.socket = this.socket_factory.createSocket(this.host, this.port);
                    in = this.socket_factory.getInputStream(this.socket);
                    out = this.socket_factory.getOutputStream(this.socket);
                }
                this.socket.setTcpNoDelay(true);
                this.io.setInputStream(in);
                this.io.setOutputStream(out);
            }
            else {
                synchronized (this.proxy) {
                    this.proxy.connect(this.socket_factory, this.host, this.port, connectTimeout);
                    this.io.setInputStream(this.proxy.getInputStream());
                    this.io.setOutputStream(this.proxy.getOutputStream());
                    this.socket = this.proxy.getSocket();
                }
            }
            if (connectTimeout > 0 && this.socket != null) {
                this.socket.setSoTimeout(connectTimeout);
            }
            this.isConnected = true;
            if (JSch.getLogger().isEnabled(1)) {
                JSch.getLogger().log(1, "Connection established");
            }
            this.jsch.addSession(this);
            final byte[] foo = new byte[this.V_C.length + 1];
            System.arraycopy(this.V_C, 0, foo, 0, this.V_C.length);
            foo[foo.length - 1] = 10;
            this.io.put(foo, 0, foo.length);
            int i;
            while (true) {
                i = 0;
                int j = 0;
                while (i < this.buf.buffer.length) {
                    j = this.io.getByte();
                    if (j < 0) {
                        break;
                    }
                    this.buf.buffer[i] = (byte)j;
                    ++i;
                    if (j == 10) {
                        break;
                    }
                }
                if (j < 0) {
                    throw new JSchException("connection is closed by foreign host");
                }
                if (this.buf.buffer[i - 1] == 10 && --i > 0 && this.buf.buffer[i - 1] == 13) {
                    --i;
                }
                if (i <= 3) {
                    continue;
                }
                if (i == this.buf.buffer.length) {
                    break;
                }
                if (this.buf.buffer[0] != 83 || this.buf.buffer[1] != 83 || this.buf.buffer[2] != 72) {
                    continue;
                }
                if (this.buf.buffer[3] != 45) {
                    continue;
                }
                break;
            }
            if (i == this.buf.buffer.length || i < 7 || (this.buf.buffer[4] == 49 && this.buf.buffer[6] != 57)) {
                throw new JSchException("invalid server's version string");
            }
            this.V_S = new byte[i];
            System.arraycopy(this.buf.buffer, 0, this.V_S, 0, i);
            if (JSch.getLogger().isEnabled(1)) {
                JSch.getLogger().log(1, "Remote version string: " + Util.byte2str(this.V_S));
                JSch.getLogger().log(1, "Local version string: " + Util.byte2str(this.V_C));
            }
            this.send_kexinit();
            this.buf = this.read(this.buf);
            if (this.buf.getCommand() != 20) {
                this.in_kex = false;
                throw new JSchException("invalid protocol: " + this.buf.getCommand());
            }
            if (JSch.getLogger().isEnabled(1)) {
                JSch.getLogger().log(1, "SSH_MSG_KEXINIT received");
            }
            final KeyExchange kex = this.receive_kexinit(this.buf);
            do {
                this.buf = this.read(this.buf);
                if (kex.getState() != this.buf.getCommand()) {
                    this.in_kex = false;
                    throw new JSchException("invalid protocol(kex): " + this.buf.getCommand());
                }
                this.kex_start_time = System.currentTimeMillis();
                final boolean result = kex.next(this.buf);
                if (!result) {
                    this.in_kex = false;
                    throw new JSchException("verify: " + result);
                }
            } while (kex.getState() != 0);
            try {
                final long tmp = System.currentTimeMillis();
                this.in_prompt = true;
                this.checkHost(this.host, this.port, kex);
                this.in_prompt = false;
                this.kex_start_time += System.currentTimeMillis() - tmp;
            }
            catch (JSchException ee) {
                this.in_kex = false;
                this.in_prompt = false;
                throw ee;
            }
            this.send_newkeys();
            this.buf = this.read(this.buf);
            if (this.buf.getCommand() != 21) {
                this.in_kex = false;
                throw new JSchException("invalid protocol(newkyes): " + this.buf.getCommand());
            }
            if (JSch.getLogger().isEnabled(1)) {
                JSch.getLogger().log(1, "SSH_MSG_NEWKEYS received");
            }
            this.receive_newkeys(this.buf, kex);
            try {
                final String s = this.getConfig("MaxAuthTries");
                if (s != null) {
                    this.max_auth_tries = Integer.parseInt(s);
                }
            }
            catch (NumberFormatException e2) {
                throw new JSchException("MaxAuthTries: " + this.getConfig("MaxAuthTries"), e2);
            }
            boolean auth = false;
            boolean auth_cancel = false;
            UserAuth ua = null;
            try {
                final Class c2 = Class.forName(this.getConfig("userauth.none"));
                ua = c2.newInstance();
            }
            catch (Exception e3) {
                throw new JSchException(e3.toString(), e3);
            }
            auth = ua.start(this);
            final String cmethods = this.getConfig("PreferredAuthentications");
            final String[] cmethoda = Util.split(cmethods, ",");
            String smethods = null;
            if (!auth) {
                smethods = ((UserAuthNone)ua).getMethods();
                if (smethods != null) {
                    smethods = smethods.toLowerCase();
                }
                else {
                    smethods = cmethods;
                }
            }
            String[] smethoda = Util.split(smethods, ",");
            int methodi = 0;
            while (!auth && cmethoda != null && methodi < cmethoda.length) {
                final String method = cmethoda[methodi++];
                boolean acceptable = false;
                for (int k = 0; k < smethoda.length; ++k) {
                    if (smethoda[k].equals(method)) {
                        acceptable = true;
                        break;
                    }
                }
                if (!acceptable) {
                    continue;
                }
                if (JSch.getLogger().isEnabled(1)) {
                    String str = "Authentications that can continue: ";
                    for (int l = methodi - 1; l < cmethoda.length; ++l) {
                        str += cmethoda[l];
                        if (l + 1 < cmethoda.length) {
                            str += ",";
                        }
                    }
                    JSch.getLogger().log(1, str);
                    JSch.getLogger().log(1, "Next authentication method: " + method);
                }
                ua = null;
                try {
                    Class c3 = null;
                    if (this.getConfig("userauth." + method) != null) {
                        c3 = Class.forName(this.getConfig("userauth." + method));
                        ua = c3.newInstance();
                    }
                }
                catch (Exception e4) {
                    if (JSch.getLogger().isEnabled(2)) {
                        JSch.getLogger().log(2, "failed to load " + method + " method");
                    }
                }
                if (ua == null) {
                    continue;
                }
                auth_cancel = false;
                try {
                    auth = ua.start(this);
                    if (!auth || !JSch.getLogger().isEnabled(1)) {
                        continue;
                    }
                    JSch.getLogger().log(1, "Authentication succeeded (" + method + ").");
                }
                catch (JSchAuthCancelException ee6) {
                    auth_cancel = true;
                }
                catch (JSchPartialAuthException ee2) {
                    final String tmp2 = smethods;
                    smethods = ee2.getMethods();
                    smethoda = Util.split(smethods, ",");
                    if (!tmp2.equals(smethods)) {
                        methodi = 0;
                    }
                    auth_cancel = false;
                }
                catch (RuntimeException ee3) {
                    throw ee3;
                }
                catch (JSchException ee4) {
                    throw ee4;
                }
                catch (Exception ee5) {
                    if (JSch.getLogger().isEnabled(2)) {
                        JSch.getLogger().log(2, "an exception during authentication\n" + ee5.toString());
                    }
                    break;
                }
            }
            if (!auth) {
                if (this.auth_failures >= this.max_auth_tries && JSch.getLogger().isEnabled(1)) {
                    JSch.getLogger().log(1, "Login trials exceeds " + this.max_auth_tries);
                }
                if (auth_cancel) {
                    throw new JSchException("Auth cancel");
                }
                throw new JSchException("Auth fail");
            }
            else {
                if (this.socket != null && (connectTimeout > 0 || this.timeout > 0)) {
                    this.socket.setSoTimeout(this.timeout);
                }
                this.isAuthed = true;
                synchronized (this.lock) {
                    if (this.isConnected) {
                        (this.connectThread = new Thread(this)).setName("Connect thread " + this.host + " session");
                        if (this.daemon_thread) {
                            this.connectThread.setDaemon(this.daemon_thread);
                        }
                        this.connectThread.start();
                        this.requestPortForwarding();
                    }
                }
            }
        }
        catch (Exception e) {
            this.in_kex = false;
            try {
                if (this.isConnected) {
                    final String message = e.toString();
                    this.packet.reset();
                    this.buf.checkFreeSize(13 + message.length() + 2 + 128);
                    this.buf.putByte((byte)1);
                    this.buf.putInt(3);
                    this.buf.putString(Util.str2byte(message));
                    this.buf.putString(Util.str2byte("en"));
                    this.write(this.packet);
                }
            }
            catch (Exception ex) {}
            try {
                this.disconnect();
            }
            catch (Exception ex2) {}
            this.isConnected = false;
            if (e instanceof RuntimeException) {
                throw (RuntimeException)e;
            }
            if (e instanceof JSchException) {
                throw (JSchException)e;
            }
            throw new JSchException("Session.connect: " + e);
        }
        finally {
            Util.bzero(this.password);
            this.password = null;
        }
    }
    
    private KeyExchange receive_kexinit(final Buffer buf) throws Exception {
        final int j = buf.getInt();
        if (j != buf.getLength()) {
            buf.getByte();
            this.I_S = new byte[buf.index - 5];
        }
        else {
            this.I_S = new byte[j - 1 - buf.getByte()];
        }
        System.arraycopy(buf.buffer, buf.s, this.I_S, 0, this.I_S.length);
        if (!this.in_kex) {
            this.send_kexinit();
        }
        this.guess = KeyExchange.guess(this.I_S, this.I_C);
        if (this.guess == null) {
            throw new JSchException("Algorithm negotiation fail");
        }
        if (!this.isAuthed && (this.guess[2].equals("none") || this.guess[3].equals("none"))) {
            throw new JSchException("NONE Cipher should not be chosen before authentification is successed.");
        }
        KeyExchange kex = null;
        try {
            final Class c = Class.forName(this.getConfig(this.guess[0]));
            kex = c.newInstance();
        }
        catch (Exception e) {
            throw new JSchException(e.toString(), e);
        }
        kex.init(this, this.V_S, this.V_C, this.I_S, this.I_C);
        return kex;
    }
    
    public void rekey() throws Exception {
        this.send_kexinit();
    }
    
    private void send_kexinit() throws Exception {
        if (this.in_kex) {
            return;
        }
        String cipherc2s = this.getConfig("cipher.c2s");
        String ciphers2c = this.getConfig("cipher.s2c");
        final String[] not_available_ciphers = this.checkCiphers(this.getConfig("CheckCiphers"));
        if (not_available_ciphers != null && not_available_ciphers.length > 0) {
            cipherc2s = Util.diffString(cipherc2s, not_available_ciphers);
            ciphers2c = Util.diffString(ciphers2c, not_available_ciphers);
            if (cipherc2s == null || ciphers2c == null) {
                throw new JSchException("There are not any available ciphers.");
            }
        }
        String kex = this.getConfig("kex");
        final String[] not_available_kexes = this.checkKexes(this.getConfig("CheckKexes"));
        if (not_available_kexes != null && not_available_kexes.length > 0) {
            kex = Util.diffString(kex, not_available_kexes);
            if (kex == null) {
                throw new JSchException("There are not any available kexes.");
            }
        }
        String server_host_key = this.getConfig("server_host_key");
        final String[] not_available_shks = this.checkSignatures(this.getConfig("CheckSignatures"));
        if (not_available_shks != null && not_available_shks.length > 0) {
            server_host_key = Util.diffString(server_host_key, not_available_shks);
            if (server_host_key == null) {
                throw new JSchException("There are not any available sig algorithm.");
            }
        }
        this.in_kex = true;
        this.kex_start_time = System.currentTimeMillis();
        final Buffer buf = new Buffer();
        final Packet packet = new Packet(buf);
        packet.reset();
        buf.putByte((byte)20);
        synchronized (Session.random) {
            Session.random.fill(buf.buffer, buf.index, 16);
            buf.skip(16);
        }
        buf.putString(Util.str2byte(kex));
        buf.putString(Util.str2byte(server_host_key));
        buf.putString(Util.str2byte(cipherc2s));
        buf.putString(Util.str2byte(ciphers2c));
        buf.putString(Util.str2byte(this.getConfig("mac.c2s")));
        buf.putString(Util.str2byte(this.getConfig("mac.s2c")));
        buf.putString(Util.str2byte(this.getConfig("compression.c2s")));
        buf.putString(Util.str2byte(this.getConfig("compression.s2c")));
        buf.putString(Util.str2byte(this.getConfig("lang.c2s")));
        buf.putString(Util.str2byte(this.getConfig("lang.s2c")));
        buf.putByte((byte)0);
        buf.putInt(0);
        buf.setOffSet(5);
        buf.getByte(this.I_C = new byte[buf.getLength()]);
        this.write(packet);
        if (JSch.getLogger().isEnabled(1)) {
            JSch.getLogger().log(1, "SSH_MSG_KEXINIT sent");
        }
    }
    
    private void send_newkeys() throws Exception {
        this.packet.reset();
        this.buf.putByte((byte)21);
        this.write(this.packet);
        if (JSch.getLogger().isEnabled(1)) {
            JSch.getLogger().log(1, "SSH_MSG_NEWKEYS sent");
        }
    }
    
    private void checkHost(String chost, final int port, final KeyExchange kex) throws JSchException {
        final String shkc = this.getConfig("StrictHostKeyChecking");
        if (this.hostKeyAlias != null) {
            chost = this.hostKeyAlias;
        }
        final byte[] K_S = kex.getHostKey();
        final String key_type = kex.getKeyType();
        final String key_fprint = kex.getFingerPrint();
        if (this.hostKeyAlias == null && port != 22) {
            chost = "[" + chost + "]:" + port;
        }
        final HostKeyRepository hkr = this.getHostKeyRepository();
        final String hkh = this.getConfig("HashKnownHosts");
        if (hkh.equals("yes") && hkr instanceof KnownHosts) {
            this.hostkey = ((KnownHosts)hkr).createHashedHostKey(chost, K_S);
        }
        else {
            this.hostkey = new HostKey(chost, K_S);
        }
        int i = 0;
        synchronized (hkr) {
            i = hkr.check(chost, K_S);
        }
        boolean insert = false;
        if ((shkc.equals("ask") || shkc.equals("yes")) && i == 2) {
            String file = null;
            synchronized (hkr) {
                file = hkr.getKnownHostsRepositoryID();
            }
            if (file == null) {
                file = "known_hosts";
            }
            boolean b = false;
            if (this.userinfo != null) {
                final String message = "WARNING: REMOTE HOST IDENTIFICATION HAS CHANGED!\nIT IS POSSIBLE THAT SOMEONE IS DOING SOMETHING NASTY!\nSomeone could be eavesdropping on you right now (man-in-the-middle attack)!\nIt is also possible that the " + key_type + " host key has just been changed.\n" + "The fingerprint for the " + key_type + " key sent by the remote host " + chost + " is\n" + key_fprint + ".\n" + "Please contact your system administrator.\n" + "Add correct host key in " + file + " to get rid of this message.";
                if (shkc.equals("ask")) {
                    b = this.userinfo.promptYesNo(message + "\nDo you want to delete the old key and insert the new key?");
                }
                else {
                    this.userinfo.showMessage(message);
                }
            }
            if (!b) {
                throw new JSchException("HostKey has been changed: " + chost);
            }
            synchronized (hkr) {
                hkr.remove(chost, kex.getKeyAlgorithName(), null);
                insert = true;
            }
        }
        if ((shkc.equals("ask") || shkc.equals("yes")) && i != 0 && !insert) {
            if (shkc.equals("yes")) {
                throw new JSchException("reject HostKey: " + this.host);
            }
            if (this.userinfo != null) {
                final boolean foo = this.userinfo.promptYesNo("The authenticity of host '" + this.host + "' can't be established.\n" + key_type + " key fingerprint is " + key_fprint + ".\n" + "Are you sure you want to continue connecting?");
                if (!foo) {
                    throw new JSchException("reject HostKey: " + this.host);
                }
                insert = true;
            }
            else {
                if (i == 1) {
                    throw new JSchException("UnknownHostKey: " + this.host + ". " + key_type + " key fingerprint is " + key_fprint);
                }
                throw new JSchException("HostKey has been changed: " + this.host);
            }
        }
        if (shkc.equals("no") && 1 == i) {
            insert = true;
        }
        if (i == 0) {
            final HostKey[] keys = hkr.getHostKey(chost, kex.getKeyAlgorithName());
            final String _key = Util.byte2str(Util.toBase64(K_S, 0, K_S.length));
            for (int j = 0; j < keys.length; ++j) {
                if (keys[i].getKey().equals(_key) && keys[j].getMarker().equals("@revoked")) {
                    if (this.userinfo != null) {
                        this.userinfo.showMessage("The " + key_type + " host key for " + this.host + " is marked as revoked.\n" + "This could mean that a stolen key is being used to " + "impersonate this host.");
                    }
                    if (JSch.getLogger().isEnabled(1)) {
                        JSch.getLogger().log(1, "Host '" + this.host + "' has provided revoked key.");
                    }
                    throw new JSchException("revoked HostKey: " + this.host);
                }
            }
        }
        if (i == 0 && JSch.getLogger().isEnabled(1)) {
            JSch.getLogger().log(1, "Host '" + this.host + "' is known and matches the " + key_type + " host key");
        }
        if (insert && JSch.getLogger().isEnabled(2)) {
            JSch.getLogger().log(2, "Permanently added '" + this.host + "' (" + key_type + ") to the list of known hosts.");
        }
        if (insert) {
            synchronized (hkr) {
                hkr.add(this.hostkey, this.userinfo);
            }
        }
    }
    
    public Channel openChannel(final String type) throws JSchException {
        if (!this.isConnected) {
            throw new JSchException("session is down");
        }
        try {
            final Channel channel = Channel.getChannel(type);
            this.addChannel(channel);
            channel.init();
            if (channel instanceof ChannelSession) {
                this.applyConfigChannel((ChannelSession)channel);
            }
            return channel;
        }
        catch (Exception e) {
            return null;
        }
    }
    
    public void encode(final Packet packet) throws Exception {
        if (this.deflater != null) {
            this.compress_len[0] = packet.buffer.index;
            packet.buffer.buffer = this.deflater.compress(packet.buffer.buffer, 5, this.compress_len);
            packet.buffer.index = this.compress_len[0];
        }
        if (this.c2scipher != null) {
            packet.padding(this.c2scipher_size);
            final int pad = packet.buffer.buffer[4];
            synchronized (Session.random) {
                Session.random.fill(packet.buffer.buffer, packet.buffer.index - pad, pad);
            }
        }
        else {
            packet.padding(8);
        }
        if (this.c2smac != null) {
            this.c2smac.update(this.seqo);
            this.c2smac.update(packet.buffer.buffer, 0, packet.buffer.index);
            this.c2smac.doFinal(packet.buffer.buffer, packet.buffer.index);
        }
        if (this.c2scipher != null) {
            final byte[] buf = packet.buffer.buffer;
            this.c2scipher.update(buf, 0, packet.buffer.index, buf, 0);
        }
        if (this.c2smac != null) {
            packet.buffer.skip(this.c2smac.getBlockSize());
        }
    }
    
    public Buffer read(final Buffer buf) throws Exception {
        int j = 0;
        while (true) {
            buf.reset();
            this.io.getByte(buf.buffer, buf.index, this.s2ccipher_size);
            buf.index += this.s2ccipher_size;
            if (this.s2ccipher != null) {
                this.s2ccipher.update(buf.buffer, 0, this.s2ccipher_size, buf.buffer, 0);
            }
            j = ((buf.buffer[0] << 24 & 0xFF000000) | (buf.buffer[1] << 16 & 0xFF0000) | (buf.buffer[2] << 8 & 0xFF00) | (buf.buffer[3] & 0xFF));
            if (j < 5 || j > 262144) {
                this.start_discard(buf, this.s2ccipher, this.s2cmac, j, 262144);
            }
            final int need = j + 4 - this.s2ccipher_size;
            if (buf.index + need > buf.buffer.length) {
                final byte[] foo = new byte[buf.index + need];
                System.arraycopy(buf.buffer, 0, foo, 0, buf.index);
                buf.buffer = foo;
            }
            if (need % this.s2ccipher_size != 0) {
                final String message = "Bad packet length " + need;
                if (JSch.getLogger().isEnabled(4)) {
                    JSch.getLogger().log(4, message);
                }
                this.start_discard(buf, this.s2ccipher, this.s2cmac, j, 262144 - this.s2ccipher_size);
            }
            if (need > 0) {
                this.io.getByte(buf.buffer, buf.index, need);
                buf.index += need;
                if (this.s2ccipher != null) {
                    this.s2ccipher.update(buf.buffer, this.s2ccipher_size, need, buf.buffer, this.s2ccipher_size);
                }
            }
            if (this.s2cmac != null) {
                this.s2cmac.update(this.seqi);
                this.s2cmac.update(buf.buffer, 0, buf.index);
                this.s2cmac.doFinal(this.s2cmac_result1, 0);
                this.io.getByte(this.s2cmac_result2, 0, this.s2cmac_result2.length);
                if (!Arrays.equals(this.s2cmac_result1, this.s2cmac_result2)) {
                    if (need > 262144) {
                        throw new IOException("MAC Error");
                    }
                    this.start_discard(buf, this.s2ccipher, this.s2cmac, j, 262144 - need);
                    continue;
                }
            }
            ++this.seqi;
            if (this.inflater != null) {
                final int pad = buf.buffer[4];
                this.uncompress_len[0] = buf.index - 5 - pad;
                final byte[] foo2 = this.inflater.uncompress(buf.buffer, 5, this.uncompress_len);
                if (foo2 == null) {
                    System.err.println("fail in inflater");
                    break;
                }
                buf.buffer = foo2;
                buf.index = 5 + this.uncompress_len[0];
            }
            final int type = buf.getCommand() & 0xFF;
            if (type == 1) {
                buf.rewind();
                buf.getInt();
                buf.getShort();
                final int reason_code = buf.getInt();
                final byte[] description = buf.getString();
                final byte[] language_tag = buf.getString();
                throw new JSchException("SSH_MSG_DISCONNECT: " + reason_code + " " + Util.byte2str(description) + " " + Util.byte2str(language_tag));
            }
            if (type == 2) {
                continue;
            }
            if (type == 3) {
                buf.rewind();
                buf.getInt();
                buf.getShort();
                final int reason_id = buf.getInt();
                if (!JSch.getLogger().isEnabled(1)) {
                    continue;
                }
                JSch.getLogger().log(1, "Received SSH_MSG_UNIMPLEMENTED for " + reason_id);
            }
            else if (type == 4) {
                buf.rewind();
                buf.getInt();
                buf.getShort();
            }
            else if (type == 93) {
                buf.rewind();
                buf.getInt();
                buf.getShort();
                final Channel c = Channel.getChannel(buf.getInt(), this);
                if (c == null) {
                    continue;
                }
                c.addRemoteWindowSize(buf.getUInt());
            }
            else {
                if (type != 52) {
                    break;
                }
                this.isAuthed = true;
                if (this.inflater == null && this.deflater == null) {
                    String method = this.guess[6];
                    this.initDeflater(method);
                    method = this.guess[7];
                    this.initInflater(method);
                    break;
                }
                break;
            }
        }
        buf.rewind();
        return buf;
    }
    
    private void start_discard(final Buffer buf, final Cipher cipher, final MAC mac, final int packet_length, int discard) throws JSchException, IOException {
        MAC discard_mac = null;
        if (!cipher.isCBC()) {
            throw new JSchException("Packet corrupt");
        }
        if (packet_length != 262144 && mac != null) {
            discard_mac = mac;
        }
        int len;
        for (discard -= buf.index; discard > 0; discard -= len) {
            buf.reset();
            len = ((discard > buf.buffer.length) ? buf.buffer.length : discard);
            this.io.getByte(buf.buffer, 0, len);
            if (discard_mac != null) {
                discard_mac.update(buf.buffer, 0, len);
            }
        }
        if (discard_mac != null) {
            discard_mac.doFinal(buf.buffer, 0);
        }
        throw new JSchException("Packet corrupt");
    }
    
    byte[] getSessionId() {
        return this.session_id;
    }
    
    private void receive_newkeys(final Buffer buf, final KeyExchange kex) throws Exception {
        this.updateKeys(kex);
        this.in_kex = false;
    }
    
    private void updateKeys(final KeyExchange kex) throws Exception {
        final byte[] K = kex.getK();
        final byte[] H = kex.getH();
        final HASH hash = kex.getHash();
        if (this.session_id == null) {
            System.arraycopy(H, 0, this.session_id = new byte[H.length], 0, H.length);
        }
        this.buf.reset();
        this.buf.putMPInt(K);
        this.buf.putByte(H);
        this.buf.putByte((byte)65);
        this.buf.putByte(this.session_id);
        hash.update(this.buf.buffer, 0, this.buf.index);
        this.IVc2s = hash.digest();
        final int j = this.buf.index - this.session_id.length - 1;
        final byte[] buffer = this.buf.buffer;
        final int n = j;
        ++buffer[n];
        hash.update(this.buf.buffer, 0, this.buf.index);
        this.IVs2c = hash.digest();
        final byte[] buffer2 = this.buf.buffer;
        final int n2 = j;
        ++buffer2[n2];
        hash.update(this.buf.buffer, 0, this.buf.index);
        this.Ec2s = hash.digest();
        final byte[] buffer3 = this.buf.buffer;
        final int n3 = j;
        ++buffer3[n3];
        hash.update(this.buf.buffer, 0, this.buf.index);
        this.Es2c = hash.digest();
        final byte[] buffer4 = this.buf.buffer;
        final int n4 = j;
        ++buffer4[n4];
        hash.update(this.buf.buffer, 0, this.buf.index);
        this.MACc2s = hash.digest();
        final byte[] buffer5 = this.buf.buffer;
        final int n5 = j;
        ++buffer5[n5];
        hash.update(this.buf.buffer, 0, this.buf.index);
        this.MACs2c = hash.digest();
        try {
            String method = this.guess[3];
            Class c = Class.forName(this.getConfig(method));
            this.s2ccipher = c.newInstance();
            while (this.s2ccipher.getBlockSize() > this.Es2c.length) {
                this.buf.reset();
                this.buf.putMPInt(K);
                this.buf.putByte(H);
                this.buf.putByte(this.Es2c);
                hash.update(this.buf.buffer, 0, this.buf.index);
                final byte[] foo = hash.digest();
                final byte[] bar = new byte[this.Es2c.length + foo.length];
                System.arraycopy(this.Es2c, 0, bar, 0, this.Es2c.length);
                System.arraycopy(foo, 0, bar, this.Es2c.length, foo.length);
                this.Es2c = bar;
            }
            this.s2ccipher.init(1, this.Es2c, this.IVs2c);
            this.s2ccipher_size = this.s2ccipher.getIVSize();
            method = this.guess[5];
            c = Class.forName(this.getConfig(method));
            this.s2cmac = c.newInstance();
            this.MACs2c = this.expandKey(this.buf, K, H, this.MACs2c, hash, this.s2cmac.getBlockSize());
            this.s2cmac.init(this.MACs2c);
            this.s2cmac_result1 = new byte[this.s2cmac.getBlockSize()];
            this.s2cmac_result2 = new byte[this.s2cmac.getBlockSize()];
            method = this.guess[2];
            c = Class.forName(this.getConfig(method));
            this.c2scipher = c.newInstance();
            while (this.c2scipher.getBlockSize() > this.Ec2s.length) {
                this.buf.reset();
                this.buf.putMPInt(K);
                this.buf.putByte(H);
                this.buf.putByte(this.Ec2s);
                hash.update(this.buf.buffer, 0, this.buf.index);
                final byte[] foo = hash.digest();
                final byte[] bar = new byte[this.Ec2s.length + foo.length];
                System.arraycopy(this.Ec2s, 0, bar, 0, this.Ec2s.length);
                System.arraycopy(foo, 0, bar, this.Ec2s.length, foo.length);
                this.Ec2s = bar;
            }
            this.c2scipher.init(0, this.Ec2s, this.IVc2s);
            this.c2scipher_size = this.c2scipher.getIVSize();
            method = this.guess[4];
            c = Class.forName(this.getConfig(method));
            this.c2smac = c.newInstance();
            this.MACc2s = this.expandKey(this.buf, K, H, this.MACc2s, hash, this.c2smac.getBlockSize());
            this.c2smac.init(this.MACc2s);
            method = this.guess[6];
            this.initDeflater(method);
            method = this.guess[7];
            this.initInflater(method);
        }
        catch (Exception e) {
            if (e instanceof JSchException) {
                throw e;
            }
            throw new JSchException(e.toString(), e);
        }
    }
    
    private byte[] expandKey(final Buffer buf, final byte[] K, final byte[] H, final byte[] key, final HASH hash, final int required_length) throws Exception {
        byte[] result = key;
        final int size = hash.getBlockSize();
        while (result.length < required_length) {
            buf.reset();
            buf.putMPInt(K);
            buf.putByte(H);
            buf.putByte(result);
            hash.update(buf.buffer, 0, buf.index);
            final byte[] tmp = new byte[result.length + size];
            System.arraycopy(result, 0, tmp, 0, result.length);
            System.arraycopy(hash.digest(), 0, tmp, result.length, size);
            Util.bzero(result);
            result = tmp;
        }
        return result;
    }
    
    void write(final Packet packet, final Channel c, int length) throws Exception {
        final long t = this.getTimeout();
        while (true) {
            if (this.in_kex) {
                if (t > 0L && System.currentTimeMillis() - this.kex_start_time > t) {
                    throw new JSchException("timeout in waiting for rekeying process.");
                }
                try {
                    Thread.sleep(10L);
                }
                catch (InterruptedException e) {}
            }
            else {
                synchronized (c) {
                    if (c.rwsize < length) {
                        try {
                            ++c.notifyme;
                            c.wait(100L);
                        }
                        catch (InterruptedException e2) {}
                        finally {
                            --c.notifyme;
                        }
                    }
                    if (this.in_kex) {
                        continue;
                    }
                    if (c.rwsize >= length) {
                        c.rwsize -= length;
                        break;
                    }
                }
                if (c.close || !c.isConnected()) {
                    throw new IOException("channel is broken");
                }
                boolean sendit = false;
                int s = 0;
                byte command = 0;
                int recipient = -1;
                synchronized (c) {
                    if (c.rwsize > 0L) {
                        long len = c.rwsize;
                        if (len > length) {
                            len = length;
                        }
                        if (len != length) {
                            s = packet.shift((int)len, (this.c2scipher != null) ? this.c2scipher_size : 8, (this.c2smac != null) ? this.c2smac.getBlockSize() : 0);
                        }
                        command = packet.buffer.getCommand();
                        recipient = c.getRecipient();
                        length -= (int)len;
                        c.rwsize -= len;
                        sendit = true;
                    }
                }
                if (sendit) {
                    this._write(packet);
                    if (length == 0) {
                        return;
                    }
                    packet.unshift(command, recipient, s, length);
                }
                synchronized (c) {
                    if (this.in_kex) {
                        continue;
                    }
                    if (c.rwsize >= length) {
                        c.rwsize -= length;
                        break;
                    }
                    continue;
                }
            }
        }
        this._write(packet);
    }
    
    public void write(final Packet packet) throws Exception {
        final long t = this.getTimeout();
        while (this.in_kex) {
            if (t > 0L && System.currentTimeMillis() - this.kex_start_time > t && !this.in_prompt) {
                throw new JSchException("timeout in waiting for rekeying process.");
            }
            final byte command = packet.buffer.getCommand();
            if (command == 20 || command == 21 || command == 30 || command == 31 || command == 31 || command == 32 || command == 33 || command == 34) {
                break;
            }
            if (command == 1) {
                break;
            }
            try {
                Thread.sleep(10L);
            }
            catch (InterruptedException ex) {}
        }
        this._write(packet);
    }
    
    private void _write(final Packet packet) throws Exception {
        synchronized (this.lock) {
            this.encode(packet);
            if (this.io != null) {
                this.io.put(packet);
                ++this.seqo;
            }
        }
    }
    
    public void run() {
        this.thread = this;
        Buffer buf = new Buffer();
        final Packet packet = new Packet(buf);
        int i = 0;
        final int[] start = { 0 };
        final int[] length = { 0 };
        KeyExchange kex = null;
        int stimeout = 0;
        try {
            while (this.isConnected && this.thread != null) {
                try {
                    buf = this.read(buf);
                    stimeout = 0;
                }
                catch (InterruptedIOException ee) {
                    if (!this.in_kex && stimeout < this.serverAliveCountMax) {
                        this.sendKeepAliveMsg();
                        ++stimeout;
                        continue;
                    }
                    if (this.in_kex && stimeout < this.serverAliveCountMax) {
                        ++stimeout;
                        continue;
                    }
                    throw ee;
                }
                final int msgType = buf.getCommand() & 0xFF;
                if (kex != null && kex.getState() == msgType) {
                    this.kex_start_time = System.currentTimeMillis();
                    final boolean result = kex.next(buf);
                    if (!result) {
                        throw new JSchException("verify: " + result);
                    }
                    continue;
                }
                else {
                    switch (msgType) {
                        case 20: {
                            kex = this.receive_kexinit(buf);
                            continue;
                        }
                        case 21: {
                            this.send_newkeys();
                            this.receive_newkeys(buf, kex);
                            kex = null;
                            continue;
                        }
                        case 94: {
                            buf.getInt();
                            buf.getByte();
                            buf.getByte();
                            i = buf.getInt();
                            final Channel channel = Channel.getChannel(i, this);
                            final byte[] foo = buf.getString(start, length);
                            if (channel == null) {
                                continue;
                            }
                            if (length[0] == 0) {
                                continue;
                            }
                            try {
                                channel.write(foo, start[0], length[0]);
                            }
                            catch (Exception e2) {
                                try {
                                    channel.disconnect();
                                }
                                catch (Exception ex) {}
                                continue;
                            }
                            final int len = length[0];
                            channel.setLocalWindowSize(channel.lwsize - len);
                            if (channel.lwsize < channel.lwsize_max / 2) {
                                packet.reset();
                                buf.putByte((byte)93);
                                buf.putInt(channel.getRecipient());
                                buf.putInt(channel.lwsize_max - channel.lwsize);
                                synchronized (channel) {
                                    if (!channel.close) {
                                        this.write(packet);
                                    }
                                }
                                channel.setLocalWindowSize(channel.lwsize_max);
                                continue;
                            }
                            continue;
                        }
                        case 95: {
                            buf.getInt();
                            buf.getShort();
                            i = buf.getInt();
                            final Channel channel = Channel.getChannel(i, this);
                            buf.getInt();
                            final byte[] foo = buf.getString(start, length);
                            if (channel == null) {
                                continue;
                            }
                            if (length[0] == 0) {
                                continue;
                            }
                            channel.write_ext(foo, start[0], length[0]);
                            final int len = length[0];
                            channel.setLocalWindowSize(channel.lwsize - len);
                            if (channel.lwsize < channel.lwsize_max / 2) {
                                packet.reset();
                                buf.putByte((byte)93);
                                buf.putInt(channel.getRecipient());
                                buf.putInt(channel.lwsize_max - channel.lwsize);
                                synchronized (channel) {
                                    if (!channel.close) {
                                        this.write(packet);
                                    }
                                }
                                channel.setLocalWindowSize(channel.lwsize_max);
                                continue;
                            }
                            continue;
                        }
                        case 93: {
                            buf.getInt();
                            buf.getShort();
                            i = buf.getInt();
                            final Channel channel = Channel.getChannel(i, this);
                            if (channel == null) {
                                continue;
                            }
                            channel.addRemoteWindowSize(buf.getUInt());
                            continue;
                        }
                        case 96: {
                            buf.getInt();
                            buf.getShort();
                            i = buf.getInt();
                            final Channel channel = Channel.getChannel(i, this);
                            if (channel != null) {
                                channel.eof_remote();
                                continue;
                            }
                            continue;
                        }
                        case 97: {
                            buf.getInt();
                            buf.getShort();
                            i = buf.getInt();
                            final Channel channel = Channel.getChannel(i, this);
                            if (channel != null) {
                                channel.disconnect();
                                continue;
                            }
                            continue;
                        }
                        case 91: {
                            buf.getInt();
                            buf.getShort();
                            i = buf.getInt();
                            final Channel channel = Channel.getChannel(i, this);
                            final int r = buf.getInt();
                            final long rws = buf.getUInt();
                            final int rps = buf.getInt();
                            if (channel != null) {
                                channel.setRemoteWindowSize(rws);
                                channel.setRemotePacketSize(rps);
                                channel.open_confirmation = true;
                                channel.setRecipient(r);
                                continue;
                            }
                            continue;
                        }
                        case 92: {
                            buf.getInt();
                            buf.getShort();
                            i = buf.getInt();
                            final Channel channel = Channel.getChannel(i, this);
                            if (channel != null) {
                                final int reason_code = buf.getInt();
                                channel.setExitStatus(reason_code);
                                channel.close = true;
                                channel.eof_remote = true;
                                channel.setRecipient(0);
                                continue;
                            }
                            continue;
                        }
                        case 98: {
                            buf.getInt();
                            buf.getShort();
                            i = buf.getInt();
                            final byte[] foo = buf.getString();
                            final boolean reply = buf.getByte() != 0;
                            final Channel channel = Channel.getChannel(i, this);
                            if (channel != null) {
                                byte reply_type = 100;
                                if (Util.byte2str(foo).equals("exit-status")) {
                                    i = buf.getInt();
                                    channel.setExitStatus(i);
                                    reply_type = 99;
                                }
                                if (!reply) {
                                    continue;
                                }
                                packet.reset();
                                buf.putByte(reply_type);
                                buf.putInt(channel.getRecipient());
                                this.write(packet);
                                continue;
                            }
                            continue;
                        }
                        case 90: {
                            buf.getInt();
                            buf.getShort();
                            final byte[] foo = buf.getString();
                            final String ctyp = Util.byte2str(foo);
                            if (!"forwarded-tcpip".equals(ctyp) && (!"x11".equals(ctyp) || !this.x11_forwarding) && (!"auth-agent@openssh.com".equals(ctyp) || !this.agent_forwarding)) {
                                packet.reset();
                                buf.putByte((byte)92);
                                buf.putInt(buf.getInt());
                                buf.putInt(1);
                                buf.putString(Util.empty);
                                buf.putString(Util.empty);
                                this.write(packet);
                                continue;
                            }
                            final Channel channel = Channel.getChannel(ctyp);
                            this.addChannel(channel);
                            channel.getData(buf);
                            channel.init();
                            final Thread tmp = new Thread(channel);
                            tmp.setName("Channel " + ctyp + " " + this.host);
                            if (this.daemon_thread) {
                                tmp.setDaemon(this.daemon_thread);
                            }
                            tmp.start();
                            continue;
                        }
                        case 99: {
                            buf.getInt();
                            buf.getShort();
                            i = buf.getInt();
                            final Channel channel = Channel.getChannel(i, this);
                            if (channel == null) {
                                continue;
                            }
                            channel.reply = 1;
                            continue;
                        }
                        case 100: {
                            buf.getInt();
                            buf.getShort();
                            i = buf.getInt();
                            final Channel channel = Channel.getChannel(i, this);
                            if (channel == null) {
                                continue;
                            }
                            channel.reply = 0;
                            continue;
                        }
                        case 80: {
                            buf.getInt();
                            buf.getShort();
                            final byte[] foo = buf.getString();
                            final boolean reply = buf.getByte() != 0;
                            if (reply) {
                                packet.reset();
                                buf.putByte((byte)82);
                                this.write(packet);
                                continue;
                            }
                            continue;
                        }
                        case 81:
                        case 82: {
                            final Thread t = this.grr.getThread();
                            if (t != null) {
                                this.grr.setReply((msgType == 81) ? 1 : 0);
                                if (msgType == 81 && this.grr.getPort() == 0) {
                                    buf.getInt();
                                    buf.getShort();
                                    this.grr.setPort(buf.getInt());
                                }
                                t.interrupt();
                                continue;
                            }
                            continue;
                        }
                        default: {
                            throw new IOException("Unknown SSH message type " + msgType);
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            this.in_kex = false;
            if (JSch.getLogger().isEnabled(1)) {
                JSch.getLogger().log(1, "Caught an exception, leaving main loop due to " + e.getMessage());
            }
        }
        try {
            this.disconnect();
        }
        catch (NullPointerException e3) {}
        catch (Exception ex2) {}
        this.isConnected = false;
    }
    
    public void disconnect() {
        if (!this.isConnected) {
            return;
        }
        if (JSch.getLogger().isEnabled(1)) {
            JSch.getLogger().log(1, "Disconnecting from " + this.host + " port " + this.port);
        }
        Channel.disconnect(this);
        this.isConnected = false;
        PortWatcher.delPort(this);
        ChannelForwardedTCPIP.delPort(this);
        ChannelX11.removeFakedCookie(this);
        synchronized (this.lock) {
            if (this.connectThread != null) {
                Thread.yield();
                this.connectThread.interrupt();
                this.connectThread = null;
            }
        }
        this.thread = null;
        try {
            if (this.io != null) {
                if (this.io.in != null) {
                    this.io.in.close();
                }
                if (this.io.out != null) {
                    this.io.out.close();
                }
                if (this.io.out_ext != null) {
                    this.io.out_ext.close();
                }
            }
            if (this.proxy == null) {
                if (this.socket != null) {
                    this.socket.close();
                }
            }
            else {
                synchronized (this.proxy) {
                    this.proxy.close();
                }
                this.proxy = null;
            }
        }
        catch (Exception ex) {}
        this.io = null;
        this.socket = null;
        this.jsch.removeSession(this);
    }
    
    public int setPortForwardingL(final int lport, final String host, final int rport) throws JSchException {
        return this.setPortForwardingL("127.0.0.1", lport, host, rport);
    }
    
    public int setPortForwardingL(final String bind_address, final int lport, final String host, final int rport) throws JSchException {
        return this.setPortForwardingL(bind_address, lport, host, rport, null);
    }
    
    public int setPortForwardingL(final String bind_address, final int lport, final String host, final int rport, final ServerSocketFactory ssf) throws JSchException {
        return this.setPortForwardingL(bind_address, lport, host, rport, ssf, 0);
    }
    
    public int setPortForwardingL(final String bind_address, final int lport, final String host, final int rport, final ServerSocketFactory ssf, final int connectTimeout) throws JSchException {
        final PortWatcher pw = PortWatcher.addPort(this, bind_address, lport, host, rport, ssf);
        pw.setConnectTimeout(connectTimeout);
        final Thread tmp = new Thread(pw);
        tmp.setName("PortWatcher Thread for " + host);
        if (this.daemon_thread) {
            tmp.setDaemon(this.daemon_thread);
        }
        tmp.start();
        return pw.lport;
    }
    
    public void delPortForwardingL(final int lport) throws JSchException {
        this.delPortForwardingL("127.0.0.1", lport);
    }
    
    public void delPortForwardingL(final String bind_address, final int lport) throws JSchException {
        PortWatcher.delPort(this, bind_address, lport);
    }
    
    public String[] getPortForwardingL() throws JSchException {
        return PortWatcher.getPortForwarding(this);
    }
    
    public void setPortForwardingR(final int rport, final String host, final int lport) throws JSchException {
        this.setPortForwardingR(null, rport, host, lport, null);
    }
    
    public void setPortForwardingR(final String bind_address, final int rport, final String host, final int lport) throws JSchException {
        this.setPortForwardingR(bind_address, rport, host, lport, null);
    }
    
    public void setPortForwardingR(final int rport, final String host, final int lport, final SocketFactory sf) throws JSchException {
        this.setPortForwardingR(null, rport, host, lport, sf);
    }
    
    public void setPortForwardingR(final String bind_address, final int rport, final String host, final int lport, final SocketFactory sf) throws JSchException {
        final int allocated = this._setPortForwardingR(bind_address, rport);
        ChannelForwardedTCPIP.addPort(this, bind_address, rport, allocated, host, lport, sf);
    }
    
    public void setPortForwardingR(final int rport, final String daemon) throws JSchException {
        this.setPortForwardingR(null, rport, daemon, null);
    }
    
    public void setPortForwardingR(final int rport, final String daemon, final Object[] arg) throws JSchException {
        this.setPortForwardingR(null, rport, daemon, arg);
    }
    
    public void setPortForwardingR(final String bind_address, final int rport, final String daemon, final Object[] arg) throws JSchException {
        final int allocated = this._setPortForwardingR(bind_address, rport);
        ChannelForwardedTCPIP.addPort(this, bind_address, rport, allocated, daemon, arg);
    }
    
    public String[] getPortForwardingR() throws JSchException {
        return ChannelForwardedTCPIP.getPortForwarding(this);
    }
    
    private Forwarding parseForwarding(String conf) throws JSchException {
        final String[] tmp = conf.split(" ");
        if (tmp.length > 1) {
            final Vector foo = new Vector();
            for (int i = 0; i < tmp.length; ++i) {
                if (tmp[i].length() != 0) {
                    foo.addElement(tmp[i].trim());
                }
            }
            final StringBuffer sb = new StringBuffer();
            for (int j = 0; j < foo.size(); ++j) {
                sb.append(foo.elementAt(j));
                if (j + 1 < foo.size()) {
                    sb.append(":");
                }
            }
            conf = sb.toString();
        }
        final String org = conf;
        final Forwarding f = new Forwarding();
        try {
            if (conf.lastIndexOf(":") == -1) {
                throw new JSchException("parseForwarding: " + org);
            }
            f.hostport = Integer.parseInt(conf.substring(conf.lastIndexOf(":") + 1));
            conf = conf.substring(0, conf.lastIndexOf(":"));
            if (conf.lastIndexOf(":") == -1) {
                throw new JSchException("parseForwarding: " + org);
            }
            f.host = conf.substring(conf.lastIndexOf(":") + 1);
            conf = conf.substring(0, conf.lastIndexOf(":"));
            if (conf.lastIndexOf(":") != -1) {
                f.port = Integer.parseInt(conf.substring(conf.lastIndexOf(":") + 1));
                conf = conf.substring(0, conf.lastIndexOf(":"));
                if (conf.length() == 0 || conf.equals("*")) {
                    conf = "0.0.0.0";
                }
                if (conf.equals("localhost")) {
                    conf = "127.0.0.1";
                }
                f.bind_address = conf;
            }
            else {
                f.port = Integer.parseInt(conf);
                f.bind_address = "127.0.0.1";
            }
        }
        catch (NumberFormatException e) {
            throw new JSchException("parseForwarding: " + e.toString());
        }
        return f;
    }
    
    public int setPortForwardingL(final String conf) throws JSchException {
        final Forwarding f = this.parseForwarding(conf);
        return this.setPortForwardingL(f.bind_address, f.port, f.host, f.hostport);
    }
    
    public int setPortForwardingR(final String conf) throws JSchException {
        final Forwarding f = this.parseForwarding(conf);
        final int allocated = this._setPortForwardingR(f.bind_address, f.port);
        ChannelForwardedTCPIP.addPort(this, f.bind_address, f.port, allocated, f.host, f.hostport, null);
        return allocated;
    }
    
    public Channel getStreamForwarder(final String host, final int port) throws JSchException {
        final ChannelDirectTCPIP channel = new ChannelDirectTCPIP();
        channel.init();
        this.addChannel(channel);
        channel.setHost(host);
        channel.setPort(port);
        return channel;
    }
    
    private int _setPortForwardingR(final String bind_address, int rport) throws JSchException {
        synchronized (this.grr) {
            final Buffer buf = new Buffer(100);
            final Packet packet = new Packet(buf);
            final String address_to_bind = ChannelForwardedTCPIP.normalize(bind_address);
            this.grr.setThread(Thread.currentThread());
            this.grr.setPort(rport);
            try {
                packet.reset();
                buf.putByte((byte)80);
                buf.putString(Util.str2byte("tcpip-forward"));
                buf.putByte((byte)1);
                buf.putString(Util.str2byte(address_to_bind));
                buf.putInt(rport);
                this.write(packet);
            }
            catch (Exception e) {
                this.grr.setThread(null);
                if (e instanceof Throwable) {
                    throw new JSchException(e.toString(), e);
                }
                throw new JSchException(e.toString());
            }
            int count;
            int reply;
            for (count = 0, reply = this.grr.getReply(); count < 10 && reply == -1; ++count, reply = this.grr.getReply()) {
                try {
                    Thread.sleep(1000L);
                }
                catch (Exception ex) {}
            }
            this.grr.setThread(null);
            if (reply != 1) {
                throw new JSchException("remote port forwarding failed for listen port " + rport);
            }
            rport = this.grr.getPort();
        }
        return rport;
    }
    
    public void delPortForwardingR(final int rport) throws JSchException {
        this.delPortForwardingR(null, rport);
    }
    
    public void delPortForwardingR(final String bind_address, final int rport) throws JSchException {
        ChannelForwardedTCPIP.delPort(this, bind_address, rport);
    }
    
    private void initDeflater(final String method) throws JSchException {
        if (method.equals("none")) {
            this.deflater = null;
            return;
        }
        final String foo = this.getConfig(method);
        if (foo != null) {
            if (!method.equals("zlib")) {
                if (!this.isAuthed || !method.equals("zlib@openssh.com")) {
                    return;
                }
            }
            try {
                final Class c = Class.forName(foo);
                this.deflater = c.newInstance();
                int level = 6;
                try {
                    level = Integer.parseInt(this.getConfig("compression_level"));
                }
                catch (Exception ex) {}
                this.deflater.init(1, level);
            }
            catch (NoClassDefFoundError ee) {
                throw new JSchException(ee.toString(), ee);
            }
            catch (Exception ee2) {
                throw new JSchException(ee2.toString(), ee2);
            }
        }
    }
    
    private void initInflater(final String method) throws JSchException {
        if (method.equals("none")) {
            this.inflater = null;
            return;
        }
        final String foo = this.getConfig(method);
        if (foo != null) {
            if (!method.equals("zlib")) {
                if (!this.isAuthed || !method.equals("zlib@openssh.com")) {
                    return;
                }
            }
            try {
                final Class c = Class.forName(foo);
                (this.inflater = c.newInstance()).init(0, 0);
            }
            catch (Exception ee) {
                throw new JSchException(ee.toString(), ee);
            }
        }
    }
    
    void addChannel(final Channel channel) {
        channel.setSession(this);
    }
    
    public void setProxy(final Proxy proxy) {
        this.proxy = proxy;
    }
    
    public void setHost(final String host) {
        this.host = host;
    }
    
    public void setPort(final int port) {
        this.port = port;
    }
    
    void setUserName(final String username) {
        this.username = username;
    }
    
    public void setUserInfo(final UserInfo userinfo) {
        this.userinfo = userinfo;
    }
    
    public UserInfo getUserInfo() {
        return this.userinfo;
    }
    
    public void setInputStream(final InputStream in) {
        this.in = in;
    }
    
    public void setOutputStream(final OutputStream out) {
        this.out = out;
    }
    
    public void setX11Host(final String host) {
        ChannelX11.setHost(host);
    }
    
    public void setX11Port(final int port) {
        ChannelX11.setPort(port);
    }
    
    public void setX11Cookie(final String cookie) {
        ChannelX11.setCookie(cookie);
    }
    
    public void setPassword(final String password) {
        if (password != null) {
            this.password = Util.str2byte(password);
        }
    }
    
    public void setPassword(final byte[] password) {
        if (password != null) {
            System.arraycopy(password, 0, this.password = new byte[password.length], 0, password.length);
        }
    }
    
    public void setConfig(final Properties newconf) {
        this.setConfig((Hashtable)newconf);
    }
    
    public void setConfig(final Hashtable newconf) {
        synchronized (this.lock) {
            if (this.config == null) {
                this.config = new Hashtable();
            }
            final Enumeration e = newconf.keys();
            while (e.hasMoreElements()) {
                final String key = e.nextElement();
                this.config.put(key, newconf.get(key));
            }
        }
    }
    
    public void setConfig(final String key, final String value) {
        synchronized (this.lock) {
            if (this.config == null) {
                this.config = new Hashtable();
            }
            this.config.put(key, value);
        }
    }
    
    public String getConfig(final String key) {
        Object foo = null;
        if (this.config != null) {
            foo = this.config.get(key);
            if (foo instanceof String) {
                return (String)foo;
            }
        }
        final JSch jsch = this.jsch;
        foo = JSch.getConfig(key);
        if (foo instanceof String) {
            return (String)foo;
        }
        return null;
    }
    
    public void setSocketFactory(final SocketFactory sfactory) {
        this.socket_factory = sfactory;
    }
    
    public boolean isConnected() {
        return this.isConnected;
    }
    
    public int getTimeout() {
        return this.timeout;
    }
    
    public void setTimeout(final int timeout) throws JSchException {
        if (this.socket != null) {
            try {
                this.socket.setSoTimeout(timeout);
                this.timeout = timeout;
            }
            catch (Exception e) {
                if (e instanceof Throwable) {
                    throw new JSchException(e.toString(), e);
                }
                throw new JSchException(e.toString());
            }
            return;
        }
        if (timeout < 0) {
            throw new JSchException("invalid timeout value");
        }
        this.timeout = timeout;
    }
    
    public String getServerVersion() {
        return Util.byte2str(this.V_S);
    }
    
    public String getClientVersion() {
        return Util.byte2str(this.V_C);
    }
    
    public void setClientVersion(final String cv) {
        this.V_C = Util.str2byte(cv);
    }
    
    public void sendIgnore() throws Exception {
        final Buffer buf = new Buffer();
        final Packet packet = new Packet(buf);
        packet.reset();
        buf.putByte((byte)2);
        this.write(packet);
    }
    
    public void sendKeepAliveMsg() throws Exception {
        final Buffer buf = new Buffer();
        final Packet packet = new Packet(buf);
        packet.reset();
        buf.putByte((byte)80);
        buf.putString(Session.keepalivemsg);
        buf.putByte((byte)1);
        this.write(packet);
    }
    
    public void noMoreSessionChannels() throws Exception {
        final Buffer buf = new Buffer();
        final Packet packet = new Packet(buf);
        packet.reset();
        buf.putByte((byte)80);
        buf.putString(Session.nomoresessions);
        buf.putByte((byte)0);
        this.write(packet);
    }
    
    public HostKey getHostKey() {
        return this.hostkey;
    }
    
    public String getHost() {
        return this.host;
    }
    
    public String getUserName() {
        return this.username;
    }
    
    public int getPort() {
        return this.port;
    }
    
    public void setHostKeyAlias(final String hostKeyAlias) {
        this.hostKeyAlias = hostKeyAlias;
    }
    
    public String getHostKeyAlias() {
        return this.hostKeyAlias;
    }
    
    public void setServerAliveInterval(final int interval) throws JSchException {
        this.setTimeout(interval);
        this.serverAliveInterval = interval;
    }
    
    public int getServerAliveInterval() {
        return this.serverAliveInterval;
    }
    
    public void setServerAliveCountMax(final int count) {
        this.serverAliveCountMax = count;
    }
    
    public int getServerAliveCountMax() {
        return this.serverAliveCountMax;
    }
    
    public void setDaemonThread(final boolean enable) {
        this.daemon_thread = enable;
    }
    
    private String[] checkCiphers(final String ciphers) {
        if (ciphers == null || ciphers.length() == 0) {
            return null;
        }
        if (JSch.getLogger().isEnabled(1)) {
            JSch.getLogger().log(1, "CheckCiphers: " + ciphers);
        }
        final String cipherc2s = this.getConfig("cipher.c2s");
        final String ciphers2c = this.getConfig("cipher.s2c");
        final Vector result = new Vector();
        final String[] _ciphers = Util.split(ciphers, ",");
        for (int i = 0; i < _ciphers.length; ++i) {
            final String cipher = _ciphers[i];
            if (ciphers2c.indexOf(cipher) != -1 || cipherc2s.indexOf(cipher) != -1) {
                if (!checkCipher(this.getConfig(cipher))) {
                    result.addElement(cipher);
                }
            }
        }
        if (result.size() == 0) {
            return null;
        }
        final String[] foo = new String[result.size()];
        System.arraycopy(result.toArray(), 0, foo, 0, result.size());
        if (JSch.getLogger().isEnabled(1)) {
            for (int j = 0; j < foo.length; ++j) {
                JSch.getLogger().log(1, foo[j] + " is not available.");
            }
        }
        return foo;
    }
    
    static boolean checkCipher(final String cipher) {
        try {
            final Class c = Class.forName(cipher);
            final Cipher _c = c.newInstance();
            _c.init(0, new byte[_c.getBlockSize()], new byte[_c.getIVSize()]);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }
    
    private String[] checkKexes(final String kexes) {
        if (kexes == null || kexes.length() == 0) {
            return null;
        }
        if (JSch.getLogger().isEnabled(1)) {
            JSch.getLogger().log(1, "CheckKexes: " + kexes);
        }
        final Vector result = new Vector();
        final String[] _kexes = Util.split(kexes, ",");
        for (int i = 0; i < _kexes.length; ++i) {
            if (!checkKex(this, this.getConfig(_kexes[i]))) {
                result.addElement(_kexes[i]);
            }
        }
        if (result.size() == 0) {
            return null;
        }
        final String[] foo = new String[result.size()];
        System.arraycopy(result.toArray(), 0, foo, 0, result.size());
        if (JSch.getLogger().isEnabled(1)) {
            for (int j = 0; j < foo.length; ++j) {
                JSch.getLogger().log(1, foo[j] + " is not available.");
            }
        }
        return foo;
    }
    
    static boolean checkKex(final Session s, final String kex) {
        try {
            final Class c = Class.forName(kex);
            final KeyExchange _c = c.newInstance();
            _c.init(s, null, null, null, null);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }
    
    private String[] checkSignatures(final String sigs) {
        if (sigs == null || sigs.length() == 0) {
            return null;
        }
        if (JSch.getLogger().isEnabled(1)) {
            JSch.getLogger().log(1, "CheckSignatures: " + sigs);
        }
        final Vector result = new Vector();
        final String[] _sigs = Util.split(sigs, ",");
        for (int i = 0; i < _sigs.length; ++i) {
            try {
                final JSch jsch = this.jsch;
                final Class c = Class.forName(JSch.getConfig(_sigs[i]));
                final Signature sig = c.newInstance();
                sig.init();
            }
            catch (Exception e) {
                result.addElement(_sigs[i]);
            }
        }
        if (result.size() == 0) {
            return null;
        }
        final String[] foo = new String[result.size()];
        System.arraycopy(result.toArray(), 0, foo, 0, result.size());
        if (JSch.getLogger().isEnabled(1)) {
            for (int j = 0; j < foo.length; ++j) {
                JSch.getLogger().log(1, foo[j] + " is not available.");
            }
        }
        return foo;
    }
    
    public void setIdentityRepository(final IdentityRepository identityRepository) {
        this.identityRepository = identityRepository;
    }
    
    IdentityRepository getIdentityRepository() {
        if (this.identityRepository == null) {
            return this.jsch.getIdentityRepository();
        }
        return this.identityRepository;
    }
    
    public void setHostKeyRepository(final HostKeyRepository hostkeyRepository) {
        this.hostkeyRepository = hostkeyRepository;
    }
    
    public HostKeyRepository getHostKeyRepository() {
        if (this.hostkeyRepository == null) {
            return this.jsch.getHostKeyRepository();
        }
        return this.hostkeyRepository;
    }
    
    private void applyConfig() throws JSchException {
        final ConfigRepository configRepository = this.jsch.getConfigRepository();
        if (configRepository == null) {
            return;
        }
        final ConfigRepository.Config config = configRepository.getConfig(this.org_host);
        String value = null;
        value = config.getUser();
        if (value != null) {
            this.username = value;
        }
        value = config.getHostname();
        if (value != null) {
            this.host = value;
        }
        final int port = config.getPort();
        if (port != -1) {
            this.port = port;
        }
        this.checkConfig(config, "kex");
        this.checkConfig(config, "server_host_key");
        this.checkConfig(config, "cipher.c2s");
        this.checkConfig(config, "cipher.s2c");
        this.checkConfig(config, "mac.c2s");
        this.checkConfig(config, "mac.s2c");
        this.checkConfig(config, "compression.c2s");
        this.checkConfig(config, "compression.s2c");
        this.checkConfig(config, "compression_level");
        this.checkConfig(config, "StrictHostKeyChecking");
        this.checkConfig(config, "HashKnownHosts");
        this.checkConfig(config, "PreferredAuthentications");
        this.checkConfig(config, "MaxAuthTries");
        this.checkConfig(config, "ClearAllForwardings");
        value = config.getValue("HostKeyAlias");
        if (value != null) {
            this.setHostKeyAlias(value);
        }
        value = config.getValue("UserKnownHostsFile");
        if (value != null) {
            final KnownHosts kh = new KnownHosts(this.jsch);
            kh.setKnownHosts(value);
            this.setHostKeyRepository(kh);
        }
        final String[] values = config.getValues("IdentityFile");
        if (values != null) {
            String[] global = configRepository.getConfig("").getValues("IdentityFile");
            if (global != null) {
                for (int i = 0; i < global.length; ++i) {
                    this.jsch.addIdentity(global[i]);
                }
            }
            else {
                global = new String[0];
            }
            if (values.length - global.length > 0) {
                final IdentityRepository.Wrapper ir = new IdentityRepository.Wrapper(this.jsch.getIdentityRepository(), true);
                for (int j = 0; j < values.length; ++j) {
                    String ifile = values[j];
                    for (int k = 0; k < global.length; ++k) {
                        if (ifile.equals(global[k])) {
                            ifile = null;
                            break;
                        }
                    }
                    if (ifile != null) {
                        final Identity identity = IdentityFile.newInstance(ifile, null, this.jsch);
                        ir.add(identity);
                    }
                }
                this.setIdentityRepository(ir);
            }
        }
        value = config.getValue("ServerAliveInterval");
        if (value != null) {
            try {
                this.setServerAliveInterval(Integer.parseInt(value));
            }
            catch (NumberFormatException ex) {}
        }
        value = config.getValue("ConnectTimeout");
        if (value != null) {
            try {
                this.setTimeout(Integer.parseInt(value));
            }
            catch (NumberFormatException ex2) {}
        }
        value = config.getValue("MaxAuthTries");
        if (value != null) {
            this.setConfig("MaxAuthTries", value);
        }
        value = config.getValue("ClearAllForwardings");
        if (value != null) {
            this.setConfig("ClearAllForwardings", value);
        }
    }
    
    private void applyConfigChannel(final ChannelSession channel) throws JSchException {
        final ConfigRepository configRepository = this.jsch.getConfigRepository();
        if (configRepository == null) {
            return;
        }
        final ConfigRepository.Config config = configRepository.getConfig(this.org_host);
        String value = null;
        value = config.getValue("ForwardAgent");
        if (value != null) {
            channel.setAgentForwarding(value.equals("yes"));
        }
        value = config.getValue("RequestTTY");
        if (value != null) {
            channel.setPty(value.equals("yes"));
        }
    }
    
    private void requestPortForwarding() throws JSchException {
        if (this.getConfig("ClearAllForwardings").equals("yes")) {
            return;
        }
        final ConfigRepository configRepository = this.jsch.getConfigRepository();
        if (configRepository == null) {
            return;
        }
        final ConfigRepository.Config config = configRepository.getConfig(this.org_host);
        String[] values = config.getValues("LocalForward");
        if (values != null) {
            for (int i = 0; i < values.length; ++i) {
                this.setPortForwardingL(values[i]);
            }
        }
        values = config.getValues("RemoteForward");
        if (values != null) {
            for (int i = 0; i < values.length; ++i) {
                this.setPortForwardingR(values[i]);
            }
        }
    }
    
    private void checkConfig(final ConfigRepository.Config config, final String key) {
        final String value = config.getValue(key);
        if (value != null) {
            this.setConfig(key, value);
        }
    }
    
    static {
        keepalivemsg = Util.str2byte("keepalive@jcraft.com");
        nomoresessions = Util.str2byte("no-more-sessions@openssh.com");
    }
    
    private class Forwarding
    {
        String bind_address;
        int port;
        String host;
        int hostport;
        
        private Forwarding() {
            this.bind_address = null;
            this.port = -1;
            this.host = null;
            this.hostport = -1;
        }
    }
    
    private class GlobalRequestReply
    {
        private Thread thread;
        private int reply;
        private int port;
        
        private GlobalRequestReply() {
            this.thread = null;
            this.reply = -1;
            this.port = 0;
        }
        
        void setThread(final Thread thread) {
            this.thread = thread;
            this.reply = -1;
        }
        
        Thread getThread() {
            return this.thread;
        }
        
        void setReply(final int reply) {
            this.reply = reply;
        }
        
        int getReply() {
            return this.reply;
        }
        
        int getPort() {
            return this.port;
        }
        
        void setPort(final int port) {
            this.port = port;
        }
    }
}
