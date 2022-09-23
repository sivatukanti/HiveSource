// 
// Decompiled by Procyon v0.5.36
// 

package com.jcraft.jsch;

import java.io.PipedOutputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.OutputStream;
import java.io.InputStream;
import java.util.Vector;

public abstract class Channel implements Runnable
{
    static final int SSH_MSG_CHANNEL_OPEN_CONFIRMATION = 91;
    static final int SSH_MSG_CHANNEL_OPEN_FAILURE = 92;
    static final int SSH_MSG_CHANNEL_WINDOW_ADJUST = 93;
    static final int SSH_OPEN_ADMINISTRATIVELY_PROHIBITED = 1;
    static final int SSH_OPEN_CONNECT_FAILED = 2;
    static final int SSH_OPEN_UNKNOWN_CHANNEL_TYPE = 3;
    static final int SSH_OPEN_RESOURCE_SHORTAGE = 4;
    static int index;
    private static Vector pool;
    int id;
    volatile int recipient;
    protected byte[] type;
    volatile int lwsize_max;
    volatile int lwsize;
    volatile int lmpsize;
    volatile long rwsize;
    volatile int rmpsize;
    IO io;
    Thread thread;
    volatile boolean eof_local;
    volatile boolean eof_remote;
    volatile boolean close;
    volatile boolean connected;
    volatile boolean open_confirmation;
    volatile int exitstatus;
    volatile int reply;
    volatile int connectTimeout;
    private Session session;
    int notifyme;
    
    static Channel getChannel(final String type) {
        if (type.equals("session")) {
            return new ChannelSession();
        }
        if (type.equals("shell")) {
            return new ChannelShell();
        }
        if (type.equals("exec")) {
            return new ChannelExec();
        }
        if (type.equals("x11")) {
            return new ChannelX11();
        }
        if (type.equals("auth-agent@openssh.com")) {
            return new ChannelAgentForwarding();
        }
        if (type.equals("direct-tcpip")) {
            return new ChannelDirectTCPIP();
        }
        if (type.equals("forwarded-tcpip")) {
            return new ChannelForwardedTCPIP();
        }
        if (type.equals("sftp")) {
            return new ChannelSftp();
        }
        if (type.equals("subsystem")) {
            return new ChannelSubsystem();
        }
        return null;
    }
    
    static Channel getChannel(final int id, final Session session) {
        synchronized (Channel.pool) {
            for (int i = 0; i < Channel.pool.size(); ++i) {
                final Channel c = Channel.pool.elementAt(i);
                if (c.id == id && c.session == session) {
                    return c;
                }
            }
        }
        return null;
    }
    
    static void del(final Channel c) {
        synchronized (Channel.pool) {
            Channel.pool.removeElement(c);
        }
    }
    
    Channel() {
        this.recipient = -1;
        this.type = Util.str2byte("foo");
        this.lwsize_max = 1048576;
        this.lwsize = this.lwsize_max;
        this.lmpsize = 16384;
        this.rwsize = 0L;
        this.rmpsize = 0;
        this.io = null;
        this.thread = null;
        this.eof_local = false;
        this.eof_remote = false;
        this.close = false;
        this.connected = false;
        this.open_confirmation = false;
        this.exitstatus = -1;
        this.reply = 0;
        this.connectTimeout = 0;
        this.notifyme = 0;
        synchronized (Channel.pool) {
            this.id = Channel.index++;
            Channel.pool.addElement(this);
        }
    }
    
    synchronized void setRecipient(final int foo) {
        this.recipient = foo;
        if (this.notifyme > 0) {
            this.notifyAll();
        }
    }
    
    int getRecipient() {
        return this.recipient;
    }
    
    void init() throws JSchException {
    }
    
    public void connect() throws JSchException {
        this.connect(0);
    }
    
    public void connect(final int connectTimeout) throws JSchException {
        this.connectTimeout = connectTimeout;
        try {
            this.sendChannelOpen();
            this.start();
        }
        catch (Exception e) {
            this.connected = false;
            this.disconnect();
            if (e instanceof JSchException) {
                throw (JSchException)e;
            }
            throw new JSchException(e.toString(), e);
        }
    }
    
    public void setXForwarding(final boolean foo) {
    }
    
    public void start() throws JSchException {
    }
    
    public boolean isEOF() {
        return this.eof_remote;
    }
    
    void getData(final Buffer buf) {
        this.setRecipient(buf.getInt());
        this.setRemoteWindowSize(buf.getUInt());
        this.setRemotePacketSize(buf.getInt());
    }
    
    public void setInputStream(final InputStream in) {
        this.io.setInputStream(in, false);
    }
    
    public void setInputStream(final InputStream in, final boolean dontclose) {
        this.io.setInputStream(in, dontclose);
    }
    
    public void setOutputStream(final OutputStream out) {
        this.io.setOutputStream(out, false);
    }
    
    public void setOutputStream(final OutputStream out, final boolean dontclose) {
        this.io.setOutputStream(out, dontclose);
    }
    
    public void setExtOutputStream(final OutputStream out) {
        this.io.setExtOutputStream(out, false);
    }
    
    public void setExtOutputStream(final OutputStream out, final boolean dontclose) {
        this.io.setExtOutputStream(out, dontclose);
    }
    
    public InputStream getInputStream() throws IOException {
        int max_input_buffer_size = 32768;
        try {
            max_input_buffer_size = Integer.parseInt(this.getSession().getConfig("max_input_buffer_size"));
        }
        catch (Exception ex) {}
        final PipedInputStream in = new MyPipedInputStream(32768, max_input_buffer_size);
        final boolean resizable = 32768 < max_input_buffer_size;
        this.io.setOutputStream(new PassiveOutputStream(in, resizable), false);
        return in;
    }
    
    public InputStream getExtInputStream() throws IOException {
        int max_input_buffer_size = 32768;
        try {
            max_input_buffer_size = Integer.parseInt(this.getSession().getConfig("max_input_buffer_size"));
        }
        catch (Exception ex) {}
        final PipedInputStream in = new MyPipedInputStream(32768, max_input_buffer_size);
        final boolean resizable = 32768 < max_input_buffer_size;
        this.io.setExtOutputStream(new PassiveOutputStream(in, resizable), false);
        return in;
    }
    
    public OutputStream getOutputStream() throws IOException {
        final Channel channel = this;
        final OutputStream out = new OutputStream() {
            private int dataLen = 0;
            private Buffer buffer = null;
            private Packet packet = null;
            private boolean closed = false;
            byte[] b = new byte[1];
            
            private synchronized void init() throws IOException {
                this.buffer = new Buffer(Channel.this.rmpsize);
                this.packet = new Packet(this.buffer);
                final byte[] _buf = this.buffer.buffer;
                if (_buf.length - 14 - 128 <= 0) {
                    this.buffer = null;
                    this.packet = null;
                    throw new IOException("failed to initialize the channel.");
                }
            }
            
            @Override
            public void write(final int w) throws IOException {
                this.b[0] = (byte)w;
                this.write(this.b, 0, 1);
            }
            
            @Override
            public void write(final byte[] buf, int s, int l) throws IOException {
                if (this.packet == null) {
                    this.init();
                }
                if (this.closed) {
                    throw new IOException("Already closed");
                }
                final byte[] _buf = this.buffer.buffer;
                final int _bufl = _buf.length;
                while (l > 0) {
                    int _l;
                    if ((_l = l) > _bufl - (14 + this.dataLen) - 128) {
                        _l = _bufl - (14 + this.dataLen) - 128;
                    }
                    if (_l <= 0) {
                        this.flush();
                    }
                    else {
                        System.arraycopy(buf, s, _buf, 14 + this.dataLen, _l);
                        this.dataLen += _l;
                        s += _l;
                        l -= _l;
                    }
                }
            }
            
            @Override
            public void flush() throws IOException {
                if (this.closed) {
                    throw new IOException("Already closed");
                }
                if (this.dataLen == 0) {
                    return;
                }
                this.packet.reset();
                this.buffer.putByte((byte)94);
                this.buffer.putInt(Channel.this.recipient);
                this.buffer.putInt(this.dataLen);
                this.buffer.skip(this.dataLen);
                try {
                    final int foo = this.dataLen;
                    this.dataLen = 0;
                    synchronized (channel) {
                        if (!channel.close) {
                            Channel.this.getSession().write(this.packet, channel, foo);
                        }
                    }
                }
                catch (Exception e) {
                    this.close();
                    throw new IOException(e.toString());
                }
            }
            
            @Override
            public void close() throws IOException {
                if (this.packet == null) {
                    try {
                        this.init();
                    }
                    catch (IOException e) {
                        return;
                    }
                }
                if (this.closed) {
                    return;
                }
                if (this.dataLen > 0) {
                    this.flush();
                }
                channel.eof();
                this.closed = true;
            }
        };
        return out;
    }
    
    void setLocalWindowSizeMax(final int foo) {
        this.lwsize_max = foo;
    }
    
    void setLocalWindowSize(final int foo) {
        this.lwsize = foo;
    }
    
    void setLocalPacketSize(final int foo) {
        this.lmpsize = foo;
    }
    
    synchronized void setRemoteWindowSize(final long foo) {
        this.rwsize = foo;
    }
    
    synchronized void addRemoteWindowSize(final long foo) {
        this.rwsize += foo;
        if (this.notifyme > 0) {
            this.notifyAll();
        }
    }
    
    void setRemotePacketSize(final int foo) {
        this.rmpsize = foo;
    }
    
    public void run() {
    }
    
    void write(final byte[] foo) throws IOException {
        this.write(foo, 0, foo.length);
    }
    
    void write(final byte[] foo, final int s, final int l) throws IOException {
        try {
            this.io.put(foo, s, l);
        }
        catch (NullPointerException ex) {}
    }
    
    void write_ext(final byte[] foo, final int s, final int l) throws IOException {
        try {
            this.io.put_ext(foo, s, l);
        }
        catch (NullPointerException ex) {}
    }
    
    void eof_remote() {
        this.eof_remote = true;
        try {
            this.io.out_close();
        }
        catch (NullPointerException ex) {}
    }
    
    void eof() {
        if (this.eof_local) {
            return;
        }
        this.eof_local = true;
        final int i = this.getRecipient();
        if (i == -1) {
            return;
        }
        try {
            final Buffer buf = new Buffer(100);
            final Packet packet = new Packet(buf);
            packet.reset();
            buf.putByte((byte)96);
            buf.putInt(i);
            synchronized (this) {
                if (!this.close) {
                    this.getSession().write(packet);
                }
            }
        }
        catch (Exception ex) {}
    }
    
    void close() {
        if (this.close) {
            return;
        }
        this.close = true;
        final boolean b = true;
        this.eof_remote = b;
        this.eof_local = b;
        final int i = this.getRecipient();
        if (i == -1) {
            return;
        }
        try {
            final Buffer buf = new Buffer(100);
            final Packet packet = new Packet(buf);
            packet.reset();
            buf.putByte((byte)97);
            buf.putInt(i);
            synchronized (this) {
                this.getSession().write(packet);
            }
        }
        catch (Exception ex) {}
    }
    
    public boolean isClosed() {
        return this.close;
    }
    
    static void disconnect(final Session session) {
        Channel[] channels = null;
        int count = 0;
        synchronized (Channel.pool) {
            channels = new Channel[Channel.pool.size()];
            for (int i = 0; i < Channel.pool.size(); ++i) {
                try {
                    final Channel c = Channel.pool.elementAt(i);
                    if (c.session == session) {
                        channels[count++] = c;
                    }
                }
                catch (Exception ex) {}
            }
        }
        for (int j = 0; j < count; ++j) {
            channels[j].disconnect();
        }
    }
    
    public void disconnect() {
        try {
            synchronized (this) {
                if (!this.connected) {
                    return;
                }
                this.connected = false;
            }
            this.close();
            final boolean b = true;
            this.eof_local = b;
            this.eof_remote = b;
            this.thread = null;
            try {
                if (this.io != null) {
                    this.io.close();
                }
            }
            catch (Exception ex) {}
        }
        finally {
            del(this);
        }
    }
    
    public boolean isConnected() {
        final Session _session = this.session;
        return _session != null && _session.isConnected() && this.connected;
    }
    
    public void sendSignal(final String signal) throws Exception {
        final RequestSignal request = new RequestSignal();
        request.setSignal(signal);
        request.request(this.getSession(), this);
    }
    
    void setExitStatus(final int status) {
        this.exitstatus = status;
    }
    
    public int getExitStatus() {
        return this.exitstatus;
    }
    
    void setSession(final Session session) {
        this.session = session;
    }
    
    public Session getSession() throws JSchException {
        final Session _session = this.session;
        if (_session == null) {
            throw new JSchException("session is not available");
        }
        return _session;
    }
    
    public int getId() {
        return this.id;
    }
    
    protected void sendOpenConfirmation() throws Exception {
        final Buffer buf = new Buffer(100);
        final Packet packet = new Packet(buf);
        packet.reset();
        buf.putByte((byte)91);
        buf.putInt(this.getRecipient());
        buf.putInt(this.id);
        buf.putInt(this.lwsize);
        buf.putInt(this.lmpsize);
        this.getSession().write(packet);
    }
    
    protected void sendOpenFailure(final int reasoncode) {
        try {
            final Buffer buf = new Buffer(100);
            final Packet packet = new Packet(buf);
            packet.reset();
            buf.putByte((byte)92);
            buf.putInt(this.getRecipient());
            buf.putInt(reasoncode);
            buf.putString(Util.str2byte("open failed"));
            buf.putString(Util.empty);
            this.getSession().write(packet);
        }
        catch (Exception ex) {}
    }
    
    protected Packet genChannelOpenPacket() {
        final Buffer buf = new Buffer(100);
        final Packet packet = new Packet(buf);
        packet.reset();
        buf.putByte((byte)90);
        buf.putString(this.type);
        buf.putInt(this.id);
        buf.putInt(this.lwsize);
        buf.putInt(this.lmpsize);
        return packet;
    }
    
    protected void sendChannelOpen() throws Exception {
        final Session _session = this.getSession();
        if (!_session.isConnected()) {
            throw new JSchException("session is down");
        }
        final Packet packet = this.genChannelOpenPacket();
        _session.write(packet);
        int retry = 2000;
        final long start = System.currentTimeMillis();
        final long timeout = this.connectTimeout;
        if (timeout != 0L) {
            retry = 1;
        }
        synchronized (this) {
            while (this.getRecipient() == -1 && _session.isConnected() && retry > 0) {
                if (timeout > 0L && System.currentTimeMillis() - start > timeout) {
                    retry = 0;
                }
                else {
                    try {
                        final long t = (timeout == 0L) ? 10L : timeout;
                        this.notifyme = 1;
                        this.wait(t);
                    }
                    catch (InterruptedException e) {}
                    finally {
                        this.notifyme = 0;
                    }
                    --retry;
                }
            }
        }
        if (!_session.isConnected()) {
            throw new JSchException("session is down");
        }
        if (this.getRecipient() == -1) {
            throw new JSchException("channel is not opened.");
        }
        if (!this.open_confirmation) {
            throw new JSchException("channel is not opened.");
        }
        this.connected = true;
    }
    
    static {
        Channel.index = 0;
        Channel.pool = new Vector();
    }
    
    class MyPipedInputStream extends PipedInputStream
    {
        private int BUFFER_SIZE;
        private int max_buffer_size;
        
        MyPipedInputStream() throws IOException {
            this.BUFFER_SIZE = 1024;
            this.max_buffer_size = this.BUFFER_SIZE;
        }
        
        MyPipedInputStream(final int size) throws IOException {
            this.BUFFER_SIZE = 1024;
            this.max_buffer_size = this.BUFFER_SIZE;
            this.buffer = new byte[size];
            this.BUFFER_SIZE = size;
            this.max_buffer_size = size;
        }
        
        MyPipedInputStream(final Channel channel, final int size, final int max_buffer_size) throws IOException {
            this(channel, size);
            this.max_buffer_size = max_buffer_size;
        }
        
        MyPipedInputStream(final PipedOutputStream out) throws IOException {
            super(out);
            this.BUFFER_SIZE = 1024;
            this.max_buffer_size = this.BUFFER_SIZE;
        }
        
        MyPipedInputStream(final PipedOutputStream out, final int size) throws IOException {
            super(out);
            this.BUFFER_SIZE = 1024;
            this.max_buffer_size = this.BUFFER_SIZE;
            this.buffer = new byte[size];
            this.BUFFER_SIZE = size;
        }
        
        public synchronized void updateReadSide() throws IOException {
            if (this.available() != 0) {
                return;
            }
            this.in = 0;
            this.out = 0;
            this.buffer[this.in++] = 0;
            this.read();
        }
        
        private int freeSpace() {
            int size = 0;
            if (this.out < this.in) {
                size = this.buffer.length - this.in;
            }
            else if (this.in < this.out) {
                if (this.in == -1) {
                    size = this.buffer.length;
                }
                else {
                    size = this.out - this.in;
                }
            }
            return size;
        }
        
        synchronized void checkSpace(final int len) throws IOException {
            final int size = this.freeSpace();
            if (size < len) {
                int datasize;
                int foo;
                for (datasize = this.buffer.length - size, foo = this.buffer.length; foo - datasize < len; foo *= 2) {}
                if (foo > this.max_buffer_size) {
                    foo = this.max_buffer_size;
                }
                if (foo - datasize < len) {
                    return;
                }
                final byte[] tmp = new byte[foo];
                if (this.out < this.in) {
                    System.arraycopy(this.buffer, 0, tmp, 0, this.buffer.length);
                }
                else if (this.in < this.out) {
                    if (this.in != -1) {
                        System.arraycopy(this.buffer, 0, tmp, 0, this.in);
                        System.arraycopy(this.buffer, this.out, tmp, tmp.length - (this.buffer.length - this.out), this.buffer.length - this.out);
                        this.out = tmp.length - (this.buffer.length - this.out);
                    }
                }
                else if (this.in == this.out) {
                    System.arraycopy(this.buffer, 0, tmp, 0, this.buffer.length);
                    this.in = this.buffer.length;
                }
                this.buffer = tmp;
            }
            else if (this.buffer.length == size && size > this.BUFFER_SIZE) {
                int i = size / 2;
                if (i < this.BUFFER_SIZE) {
                    i = this.BUFFER_SIZE;
                }
                final byte[] tmp2 = new byte[i];
                this.buffer = tmp2;
            }
        }
    }
    
    class PassiveInputStream extends MyPipedInputStream
    {
        PipedOutputStream out;
        
        PassiveInputStream(final PipedOutputStream out, final int size) throws IOException {
            super(out, size);
            this.out = out;
        }
        
        PassiveInputStream(final PipedOutputStream out) throws IOException {
            super(out);
            this.out = out;
        }
        
        @Override
        public void close() throws IOException {
            if (this.out != null) {
                this.out.close();
            }
            this.out = null;
        }
    }
    
    class PassiveOutputStream extends PipedOutputStream
    {
        private MyPipedInputStream _sink;
        
        PassiveOutputStream(final PipedInputStream in, final boolean resizable_buffer) throws IOException {
            super(in);
            this._sink = null;
            if (resizable_buffer && in instanceof MyPipedInputStream) {
                this._sink = (MyPipedInputStream)in;
            }
        }
        
        @Override
        public void write(final int b) throws IOException {
            if (this._sink != null) {
                this._sink.checkSpace(1);
            }
            super.write(b);
        }
        
        @Override
        public void write(final byte[] b, final int off, final int len) throws IOException {
            if (this._sink != null) {
                this._sink.checkSpace(len);
            }
            super.write(b, off, len);
        }
    }
}
