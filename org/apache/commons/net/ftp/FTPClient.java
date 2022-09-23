// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.net.ftp;

import java.net.SocketTimeoutException;
import org.apache.commons.net.io.CopyStreamEvent;
import java.net.SocketException;
import org.apache.commons.net.io.CopyStreamAdapter;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import org.apache.commons.net.ftp.parser.MLSxEntryParser;
import java.util.HashSet;
import java.util.Locale;
import org.apache.commons.net.io.SocketInputStream;
import java.io.Closeable;
import org.apache.commons.net.io.FromNetASCIIInputStream;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import org.apache.commons.net.io.CRLFLineReader;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.ArrayList;
import java.io.Reader;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import java.net.Inet6Address;
import org.apache.commons.net.io.SocketOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import org.apache.commons.net.io.Util;
import org.apache.commons.net.io.ToNetASCIIOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.net.UnknownHostException;
import org.apache.commons.net.MalformedServerReplyException;
import org.apache.commons.net.ftp.parser.DefaultFTPFileEntryParserFactory;
import java.util.Properties;
import java.util.Set;
import java.util.HashMap;
import java.util.regex.Pattern;
import org.apache.commons.net.io.CopyStreamListener;
import org.apache.commons.net.ftp.parser.FTPFileEntryParserFactory;
import java.net.InetAddress;
import java.util.Random;

public class FTPClient extends FTP implements Configurable
{
    public static final String FTP_SYSTEM_TYPE = "org.apache.commons.net.ftp.systemType";
    public static final String FTP_SYSTEM_TYPE_DEFAULT = "org.apache.commons.net.ftp.systemType.default";
    public static final String SYSTEM_TYPE_PROPERTIES = "/systemType.properties";
    public static final int ACTIVE_LOCAL_DATA_CONNECTION_MODE = 0;
    public static final int ACTIVE_REMOTE_DATA_CONNECTION_MODE = 1;
    public static final int PASSIVE_LOCAL_DATA_CONNECTION_MODE = 2;
    public static final int PASSIVE_REMOTE_DATA_CONNECTION_MODE = 3;
    private int __dataConnectionMode;
    private int __dataTimeout;
    private int __passivePort;
    private String __passiveHost;
    private final Random __random;
    private int __activeMinPort;
    private int __activeMaxPort;
    private InetAddress __activeExternalHost;
    private InetAddress __reportActiveExternalHost;
    private InetAddress __passiveLocalHost;
    private int __fileType;
    private int __fileFormat;
    private int __fileStructure;
    private int __fileTransferMode;
    private boolean __remoteVerificationEnabled;
    private long __restartOffset;
    private FTPFileEntryParserFactory __parserFactory;
    private int __bufferSize;
    private int __sendDataSocketBufferSize;
    private int __receiveDataSocketBufferSize;
    private boolean __listHiddenFiles;
    private boolean __useEPSVwithIPv4;
    private String __systemName;
    private FTPFileEntryParser __entryParser;
    private String __entryParserKey;
    private FTPClientConfig __configuration;
    private CopyStreamListener __copyStreamListener;
    private long __controlKeepAliveTimeout;
    private int __controlKeepAliveReplyTimeout;
    private HostnameResolver __passiveNatWorkaroundStrategy;
    private static final Pattern __PARMS_PAT;
    private boolean __autodetectEncoding;
    private HashMap<String, Set<String>> __featuresMap;
    
    private static Properties getOverrideProperties() {
        return PropertiesSingleton.PROPERTIES;
    }
    
    public FTPClient() {
        this.__controlKeepAliveReplyTimeout = 1000;
        this.__passiveNatWorkaroundStrategy = new NatServerResolverImpl(this);
        this.__autodetectEncoding = false;
        this.__initDefaults();
        this.__dataTimeout = -1;
        this.__remoteVerificationEnabled = true;
        this.__parserFactory = new DefaultFTPFileEntryParserFactory();
        this.__configuration = null;
        this.__listHiddenFiles = false;
        this.__useEPSVwithIPv4 = false;
        this.__random = new Random();
        this.__passiveLocalHost = null;
    }
    
    private void __initDefaults() {
        this.__dataConnectionMode = 0;
        this.__passiveHost = null;
        this.__passivePort = -1;
        this.__activeExternalHost = null;
        this.__reportActiveExternalHost = null;
        this.__activeMinPort = 0;
        this.__activeMaxPort = 0;
        this.__fileType = 0;
        this.__fileStructure = 7;
        this.__fileFormat = 4;
        this.__fileTransferMode = 10;
        this.__restartOffset = 0L;
        this.__systemName = null;
        this.__entryParser = null;
        this.__entryParserKey = "";
        this.__featuresMap = null;
    }
    
    static String __parsePathname(final String reply) {
        final String param = reply.substring(4);
        if (param.startsWith("\"")) {
            final StringBuilder sb = new StringBuilder();
            boolean quoteSeen = false;
            for (int i = 1; i < param.length(); ++i) {
                final char ch = param.charAt(i);
                if (ch == '\"') {
                    if (quoteSeen) {
                        sb.append(ch);
                        quoteSeen = false;
                    }
                    else {
                        quoteSeen = true;
                    }
                }
                else {
                    if (quoteSeen) {
                        return sb.toString();
                    }
                    sb.append(ch);
                }
            }
            if (quoteSeen) {
                return sb.toString();
            }
        }
        return param;
    }
    
    protected void _parsePassiveModeReply(final String reply) throws MalformedServerReplyException {
        final Matcher m = FTPClient.__PARMS_PAT.matcher(reply);
        if (!m.find()) {
            throw new MalformedServerReplyException("Could not parse passive host information.\nServer Reply: " + reply);
        }
        this.__passiveHost = m.group(1).replace(',', '.');
        try {
            final int oct1 = Integer.parseInt(m.group(2));
            final int oct2 = Integer.parseInt(m.group(3));
            this.__passivePort = (oct1 << 8 | oct2);
        }
        catch (NumberFormatException e) {
            throw new MalformedServerReplyException("Could not parse passive port information.\nServer Reply: " + reply);
        }
        if (this.__passiveNatWorkaroundStrategy != null) {
            try {
                final String passiveHost = this.__passiveNatWorkaroundStrategy.resolve(this.__passiveHost);
                if (!this.__passiveHost.equals(passiveHost)) {
                    this.fireReplyReceived(0, "[Replacing PASV mode reply address " + this.__passiveHost + " with " + passiveHost + "]\n");
                    this.__passiveHost = passiveHost;
                }
            }
            catch (UnknownHostException e2) {
                throw new MalformedServerReplyException("Could not parse passive host information.\nServer Reply: " + reply);
            }
        }
    }
    
    protected void _parseExtendedPassiveModeReply(String reply) throws MalformedServerReplyException {
        reply = reply.substring(reply.indexOf(40) + 1, reply.indexOf(41)).trim();
        final char delim1 = reply.charAt(0);
        final char delim2 = reply.charAt(1);
        final char delim3 = reply.charAt(2);
        final char delim4 = reply.charAt(reply.length() - 1);
        if (delim1 != delim2 || delim2 != delim3 || delim3 != delim4) {
            throw new MalformedServerReplyException("Could not parse extended passive host information.\nServer Reply: " + reply);
        }
        int port;
        try {
            port = Integer.parseInt(reply.substring(3, reply.length() - 1));
        }
        catch (NumberFormatException e) {
            throw new MalformedServerReplyException("Could not parse extended passive host information.\nServer Reply: " + reply);
        }
        this.__passiveHost = this.getRemoteAddress().getHostAddress();
        this.__passivePort = port;
    }
    
    private boolean __storeFile(final FTPCmd command, final String remote, final InputStream local) throws IOException {
        return this._storeFile(command.getCommand(), remote, local);
    }
    
    protected boolean _storeFile(final String command, final String remote, final InputStream local) throws IOException {
        final Socket socket = this._openDataConnection_(command, remote);
        if (socket == null) {
            return false;
        }
        OutputStream output;
        if (this.__fileType == 0) {
            output = new ToNetASCIIOutputStream(this.getBufferedOutputStream(socket.getOutputStream()));
        }
        else {
            output = this.getBufferedOutputStream(socket.getOutputStream());
        }
        CSL csl = null;
        if (this.__controlKeepAliveTimeout > 0L) {
            csl = new CSL(this, this.__controlKeepAliveTimeout, this.__controlKeepAliveReplyTimeout);
        }
        try {
            Util.copyStream(local, output, this.getBufferSize(), -1L, this.__mergeListeners(csl), false);
        }
        catch (IOException e) {
            Util.closeQuietly(socket);
            if (csl != null) {
                csl.cleanUp();
            }
            throw e;
        }
        output.close();
        socket.close();
        if (csl != null) {
            csl.cleanUp();
        }
        final boolean ok = this.completePendingCommand();
        return ok;
    }
    
    private OutputStream __storeFileStream(final FTPCmd command, final String remote) throws IOException {
        return this._storeFileStream(command.getCommand(), remote);
    }
    
    protected OutputStream _storeFileStream(final String command, final String remote) throws IOException {
        final Socket socket = this._openDataConnection_(command, remote);
        if (socket == null) {
            return null;
        }
        OutputStream output;
        if (this.__fileType == 0) {
            output = new ToNetASCIIOutputStream(this.getBufferedOutputStream(socket.getOutputStream()));
        }
        else {
            output = socket.getOutputStream();
        }
        return new SocketOutputStream(socket, output);
    }
    
    @Deprecated
    protected Socket _openDataConnection_(final int command, final String arg) throws IOException {
        return this._openDataConnection_(FTPCommand.getCommand(command), arg);
    }
    
    protected Socket _openDataConnection_(final FTPCmd command, final String arg) throws IOException {
        return this._openDataConnection_(command.getCommand(), arg);
    }
    
    protected Socket _openDataConnection_(final String command, final String arg) throws IOException {
        if (this.__dataConnectionMode != 0 && this.__dataConnectionMode != 2) {
            return null;
        }
        final boolean isInet6Address = this.getRemoteAddress() instanceof Inet6Address;
        Socket socket;
        if (this.__dataConnectionMode == 0) {
            final ServerSocket server = this._serverSocketFactory_.createServerSocket(this.getActivePort(), 1, this.getHostAddress());
            try {
                if (isInet6Address) {
                    if (!FTPReply.isPositiveCompletion(this.eprt(this.getReportHostAddress(), server.getLocalPort()))) {
                        return null;
                    }
                }
                else if (!FTPReply.isPositiveCompletion(this.port(this.getReportHostAddress(), server.getLocalPort()))) {
                    return null;
                }
                if (this.__restartOffset > 0L && !this.restart(this.__restartOffset)) {
                    return null;
                }
                if (!FTPReply.isPositivePreliminary(this.sendCommand(command, arg))) {
                    return null;
                }
                if (this.__dataTimeout >= 0) {
                    server.setSoTimeout(this.__dataTimeout);
                }
                socket = server.accept();
                if (this.__dataTimeout >= 0) {
                    socket.setSoTimeout(this.__dataTimeout);
                }
                if (this.__receiveDataSocketBufferSize > 0) {
                    socket.setReceiveBufferSize(this.__receiveDataSocketBufferSize);
                }
                if (this.__sendDataSocketBufferSize > 0) {
                    socket.setSendBufferSize(this.__sendDataSocketBufferSize);
                }
            }
            finally {
                server.close();
            }
        }
        else {
            final boolean attemptEPSV = this.isUseEPSVwithIPv4() || isInet6Address;
            if (attemptEPSV && this.epsv() == 229) {
                this._parseExtendedPassiveModeReply(this._replyLines.get(0));
            }
            else {
                if (isInet6Address) {
                    return null;
                }
                if (this.pasv() != 227) {
                    return null;
                }
                this._parsePassiveModeReply(this._replyLines.get(0));
            }
            socket = this._socketFactory_.createSocket();
            if (this.__receiveDataSocketBufferSize > 0) {
                socket.setReceiveBufferSize(this.__receiveDataSocketBufferSize);
            }
            if (this.__sendDataSocketBufferSize > 0) {
                socket.setSendBufferSize(this.__sendDataSocketBufferSize);
            }
            if (this.__passiveLocalHost != null) {
                socket.bind(new InetSocketAddress(this.__passiveLocalHost, 0));
            }
            if (this.__dataTimeout >= 0) {
                socket.setSoTimeout(this.__dataTimeout);
            }
            socket.connect(new InetSocketAddress(this.__passiveHost, this.__passivePort), this.connectTimeout);
            if (this.__restartOffset > 0L && !this.restart(this.__restartOffset)) {
                socket.close();
                return null;
            }
            if (!FTPReply.isPositivePreliminary(this.sendCommand(command, arg))) {
                socket.close();
                return null;
            }
        }
        if (this.__remoteVerificationEnabled && !this.verifyRemote(socket)) {
            socket.close();
            throw new IOException("Host attempting data connection " + socket.getInetAddress().getHostAddress() + " is not same as server " + this.getRemoteAddress().getHostAddress());
        }
        return socket;
    }
    
    @Override
    protected void _connectAction_() throws IOException {
        this._connectAction_(null);
    }
    
    @Override
    protected void _connectAction_(final Reader socketIsReader) throws IOException {
        super._connectAction_(socketIsReader);
        this.__initDefaults();
        if (this.__autodetectEncoding) {
            final ArrayList<String> oldReplyLines = new ArrayList<String>(this._replyLines);
            final int oldReplyCode = this._replyCode;
            if (this.hasFeature("UTF8") || this.hasFeature("UTF-8")) {
                this.setControlEncoding("UTF-8");
                this._controlInput_ = new CRLFLineReader(new InputStreamReader(this._input_, this.getControlEncoding()));
                this._controlOutput_ = new BufferedWriter(new OutputStreamWriter(this._output_, this.getControlEncoding()));
            }
            this._replyLines.clear();
            this._replyLines.addAll(oldReplyLines);
            this._replyCode = oldReplyCode;
            this._newReplyString = true;
        }
    }
    
    public void setDataTimeout(final int timeout) {
        this.__dataTimeout = timeout;
    }
    
    public void setParserFactory(final FTPFileEntryParserFactory parserFactory) {
        this.__parserFactory = parserFactory;
    }
    
    @Override
    public void disconnect() throws IOException {
        super.disconnect();
        this.__initDefaults();
    }
    
    public void setRemoteVerificationEnabled(final boolean enable) {
        this.__remoteVerificationEnabled = enable;
    }
    
    public boolean isRemoteVerificationEnabled() {
        return this.__remoteVerificationEnabled;
    }
    
    public boolean login(final String username, final String password) throws IOException {
        this.user(username);
        return FTPReply.isPositiveCompletion(this._replyCode) || (FTPReply.isPositiveIntermediate(this._replyCode) && FTPReply.isPositiveCompletion(this.pass(password)));
    }
    
    public boolean login(final String username, final String password, final String account) throws IOException {
        this.user(username);
        if (FTPReply.isPositiveCompletion(this._replyCode)) {
            return true;
        }
        if (!FTPReply.isPositiveIntermediate(this._replyCode)) {
            return false;
        }
        this.pass(password);
        return FTPReply.isPositiveCompletion(this._replyCode) || (FTPReply.isPositiveIntermediate(this._replyCode) && FTPReply.isPositiveCompletion(this.acct(account)));
    }
    
    public boolean logout() throws IOException {
        return FTPReply.isPositiveCompletion(this.quit());
    }
    
    public boolean changeWorkingDirectory(final String pathname) throws IOException {
        return FTPReply.isPositiveCompletion(this.cwd(pathname));
    }
    
    public boolean changeToParentDirectory() throws IOException {
        return FTPReply.isPositiveCompletion(this.cdup());
    }
    
    public boolean structureMount(final String pathname) throws IOException {
        return FTPReply.isPositiveCompletion(this.smnt(pathname));
    }
    
    public boolean reinitialize() throws IOException {
        this.rein();
        if (FTPReply.isPositiveCompletion(this._replyCode) || (FTPReply.isPositivePreliminary(this._replyCode) && FTPReply.isPositiveCompletion(this.getReply()))) {
            this.__initDefaults();
            return true;
        }
        return false;
    }
    
    public void enterLocalActiveMode() {
        this.__dataConnectionMode = 0;
        this.__passiveHost = null;
        this.__passivePort = -1;
    }
    
    public void enterLocalPassiveMode() {
        this.__dataConnectionMode = 2;
        this.__passiveHost = null;
        this.__passivePort = -1;
    }
    
    public boolean enterRemoteActiveMode(final InetAddress host, final int port) throws IOException {
        if (FTPReply.isPositiveCompletion(this.port(host, port))) {
            this.__dataConnectionMode = 1;
            this.__passiveHost = null;
            this.__passivePort = -1;
            return true;
        }
        return false;
    }
    
    public boolean enterRemotePassiveMode() throws IOException {
        if (this.pasv() != 227) {
            return false;
        }
        this.__dataConnectionMode = 3;
        this._parsePassiveModeReply(this._replyLines.get(0));
        return true;
    }
    
    public String getPassiveHost() {
        return this.__passiveHost;
    }
    
    public int getPassivePort() {
        return this.__passivePort;
    }
    
    public int getDataConnectionMode() {
        return this.__dataConnectionMode;
    }
    
    private int getActivePort() {
        if (this.__activeMinPort <= 0 || this.__activeMaxPort < this.__activeMinPort) {
            return 0;
        }
        if (this.__activeMaxPort == this.__activeMinPort) {
            return this.__activeMaxPort;
        }
        return this.__random.nextInt(this.__activeMaxPort - this.__activeMinPort + 1) + this.__activeMinPort;
    }
    
    private InetAddress getHostAddress() {
        if (this.__activeExternalHost != null) {
            return this.__activeExternalHost;
        }
        return this.getLocalAddress();
    }
    
    private InetAddress getReportHostAddress() {
        if (this.__reportActiveExternalHost != null) {
            return this.__reportActiveExternalHost;
        }
        return this.getHostAddress();
    }
    
    public void setActivePortRange(final int minPort, final int maxPort) {
        this.__activeMinPort = minPort;
        this.__activeMaxPort = maxPort;
    }
    
    public void setActiveExternalIPAddress(final String ipAddress) throws UnknownHostException {
        this.__activeExternalHost = InetAddress.getByName(ipAddress);
    }
    
    public void setPassiveLocalIPAddress(final String ipAddress) throws UnknownHostException {
        this.__passiveLocalHost = InetAddress.getByName(ipAddress);
    }
    
    public void setPassiveLocalIPAddress(final InetAddress inetAddress) {
        this.__passiveLocalHost = inetAddress;
    }
    
    public InetAddress getPassiveLocalIPAddress() {
        return this.__passiveLocalHost;
    }
    
    public void setReportActiveExternalIPAddress(final String ipAddress) throws UnknownHostException {
        this.__reportActiveExternalHost = InetAddress.getByName(ipAddress);
    }
    
    public boolean setFileType(final int fileType) throws IOException {
        if (FTPReply.isPositiveCompletion(this.type(fileType))) {
            this.__fileType = fileType;
            this.__fileFormat = 4;
            return true;
        }
        return false;
    }
    
    public boolean setFileType(final int fileType, final int formatOrByteSize) throws IOException {
        if (FTPReply.isPositiveCompletion(this.type(fileType, formatOrByteSize))) {
            this.__fileType = fileType;
            this.__fileFormat = formatOrByteSize;
            return true;
        }
        return false;
    }
    
    public boolean setFileStructure(final int structure) throws IOException {
        if (FTPReply.isPositiveCompletion(this.stru(structure))) {
            this.__fileStructure = structure;
            return true;
        }
        return false;
    }
    
    public boolean setFileTransferMode(final int mode) throws IOException {
        if (FTPReply.isPositiveCompletion(this.mode(mode))) {
            this.__fileTransferMode = mode;
            return true;
        }
        return false;
    }
    
    public boolean remoteRetrieve(final String filename) throws IOException {
        return (this.__dataConnectionMode == 1 || this.__dataConnectionMode == 3) && FTPReply.isPositivePreliminary(this.retr(filename));
    }
    
    public boolean remoteStore(final String filename) throws IOException {
        return (this.__dataConnectionMode == 1 || this.__dataConnectionMode == 3) && FTPReply.isPositivePreliminary(this.stor(filename));
    }
    
    public boolean remoteStoreUnique(final String filename) throws IOException {
        return (this.__dataConnectionMode == 1 || this.__dataConnectionMode == 3) && FTPReply.isPositivePreliminary(this.stou(filename));
    }
    
    public boolean remoteStoreUnique() throws IOException {
        return (this.__dataConnectionMode == 1 || this.__dataConnectionMode == 3) && FTPReply.isPositivePreliminary(this.stou());
    }
    
    public boolean remoteAppend(final String filename) throws IOException {
        return (this.__dataConnectionMode == 1 || this.__dataConnectionMode == 3) && FTPReply.isPositivePreliminary(this.appe(filename));
    }
    
    public boolean completePendingCommand() throws IOException {
        return FTPReply.isPositiveCompletion(this.getReply());
    }
    
    public boolean retrieveFile(final String remote, final OutputStream local) throws IOException {
        return this._retrieveFile(FTPCmd.RETR.getCommand(), remote, local);
    }
    
    protected boolean _retrieveFile(final String command, final String remote, final OutputStream local) throws IOException {
        final Socket socket = this._openDataConnection_(command, remote);
        if (socket == null) {
            return false;
        }
        InputStream input;
        if (this.__fileType == 0) {
            input = new FromNetASCIIInputStream(this.getBufferedInputStream(socket.getInputStream()));
        }
        else {
            input = this.getBufferedInputStream(socket.getInputStream());
        }
        CSL csl = null;
        if (this.__controlKeepAliveTimeout > 0L) {
            csl = new CSL(this, this.__controlKeepAliveTimeout, this.__controlKeepAliveReplyTimeout);
        }
        try {
            Util.copyStream(input, local, this.getBufferSize(), -1L, this.__mergeListeners(csl), false);
        }
        finally {
            Util.closeQuietly(input);
            Util.closeQuietly(socket);
            if (csl != null) {
                csl.cleanUp();
            }
        }
        final boolean ok = this.completePendingCommand();
        return ok;
    }
    
    public InputStream retrieveFileStream(final String remote) throws IOException {
        return this._retrieveFileStream(FTPCmd.RETR.getCommand(), remote);
    }
    
    protected InputStream _retrieveFileStream(final String command, final String remote) throws IOException {
        final Socket socket = this._openDataConnection_(command, remote);
        if (socket == null) {
            return null;
        }
        InputStream input;
        if (this.__fileType == 0) {
            input = new FromNetASCIIInputStream(this.getBufferedInputStream(socket.getInputStream()));
        }
        else {
            input = socket.getInputStream();
        }
        return new SocketInputStream(socket, input);
    }
    
    public boolean storeFile(final String remote, final InputStream local) throws IOException {
        return this.__storeFile(FTPCmd.STOR, remote, local);
    }
    
    public OutputStream storeFileStream(final String remote) throws IOException {
        return this.__storeFileStream(FTPCmd.STOR, remote);
    }
    
    public boolean appendFile(final String remote, final InputStream local) throws IOException {
        return this.__storeFile(FTPCmd.APPE, remote, local);
    }
    
    public OutputStream appendFileStream(final String remote) throws IOException {
        return this.__storeFileStream(FTPCmd.APPE, remote);
    }
    
    public boolean storeUniqueFile(final String remote, final InputStream local) throws IOException {
        return this.__storeFile(FTPCmd.STOU, remote, local);
    }
    
    public OutputStream storeUniqueFileStream(final String remote) throws IOException {
        return this.__storeFileStream(FTPCmd.STOU, remote);
    }
    
    public boolean storeUniqueFile(final InputStream local) throws IOException {
        return this.__storeFile(FTPCmd.STOU, null, local);
    }
    
    public OutputStream storeUniqueFileStream() throws IOException {
        return this.__storeFileStream(FTPCmd.STOU, null);
    }
    
    public boolean allocate(final int bytes) throws IOException {
        return FTPReply.isPositiveCompletion(this.allo(bytes));
    }
    
    public boolean features() throws IOException {
        return FTPReply.isPositiveCompletion(this.feat());
    }
    
    public String[] featureValues(final String feature) throws IOException {
        if (!this.initFeatureMap()) {
            return null;
        }
        final Set<String> entries = this.__featuresMap.get(feature.toUpperCase(Locale.ENGLISH));
        if (entries != null) {
            return entries.toArray(new String[entries.size()]);
        }
        return null;
    }
    
    public String featureValue(final String feature) throws IOException {
        final String[] values = this.featureValues(feature);
        if (values != null) {
            return values[0];
        }
        return null;
    }
    
    public boolean hasFeature(final String feature) throws IOException {
        return this.initFeatureMap() && this.__featuresMap.containsKey(feature.toUpperCase(Locale.ENGLISH));
    }
    
    public boolean hasFeature(final String feature, final String value) throws IOException {
        if (!this.initFeatureMap()) {
            return false;
        }
        final Set<String> entries = this.__featuresMap.get(feature.toUpperCase(Locale.ENGLISH));
        return entries != null && entries.contains(value);
    }
    
    private boolean initFeatureMap() throws IOException {
        if (this.__featuresMap == null) {
            final int replyCode = this.feat();
            if (replyCode == 530) {
                return false;
            }
            final boolean success = FTPReply.isPositiveCompletion(replyCode);
            this.__featuresMap = new HashMap<String, Set<String>>();
            if (!success) {
                return false;
            }
            for (final String l : this.getReplyStrings()) {
                if (l.startsWith(" ")) {
                    String value = "";
                    final int varsep = l.indexOf(32, 1);
                    String key;
                    if (varsep > 0) {
                        key = l.substring(1, varsep);
                        value = l.substring(varsep + 1);
                    }
                    else {
                        key = l.substring(1);
                    }
                    key = key.toUpperCase(Locale.ENGLISH);
                    Set<String> entries = this.__featuresMap.get(key);
                    if (entries == null) {
                        entries = new HashSet<String>();
                        this.__featuresMap.put(key, entries);
                    }
                    entries.add(value);
                }
            }
        }
        return true;
    }
    
    public boolean allocate(final int bytes, final int recordSize) throws IOException {
        return FTPReply.isPositiveCompletion(this.allo(bytes, recordSize));
    }
    
    public boolean doCommand(final String command, final String params) throws IOException {
        return FTPReply.isPositiveCompletion(this.sendCommand(command, params));
    }
    
    public String[] doCommandAsStrings(final String command, final String params) throws IOException {
        final boolean success = FTPReply.isPositiveCompletion(this.sendCommand(command, params));
        if (success) {
            return this.getReplyStrings();
        }
        return null;
    }
    
    public FTPFile mlistFile(final String pathname) throws IOException {
        final boolean success = FTPReply.isPositiveCompletion(this.sendCommand(FTPCmd.MLST, pathname));
        if (!success) {
            return null;
        }
        final String reply = this.getReplyStrings()[1];
        if (reply.length() < 3 || reply.charAt(0) != ' ') {
            throw new MalformedServerReplyException("Invalid server reply (MLST): '" + reply + "'");
        }
        final String entry = reply.substring(1);
        return MLSxEntryParser.parseEntry(entry);
    }
    
    public FTPFile[] mlistDir() throws IOException {
        return this.mlistDir(null);
    }
    
    public FTPFile[] mlistDir(final String pathname) throws IOException {
        final FTPListParseEngine engine = this.initiateMListParsing(pathname);
        return engine.getFiles();
    }
    
    public FTPFile[] mlistDir(final String pathname, final FTPFileFilter filter) throws IOException {
        final FTPListParseEngine engine = this.initiateMListParsing(pathname);
        return engine.getFiles(filter);
    }
    
    protected boolean restart(final long offset) throws IOException {
        this.__restartOffset = 0L;
        return FTPReply.isPositiveIntermediate(this.rest(Long.toString(offset)));
    }
    
    public void setRestartOffset(final long offset) {
        if (offset >= 0L) {
            this.__restartOffset = offset;
        }
    }
    
    public long getRestartOffset() {
        return this.__restartOffset;
    }
    
    public boolean rename(final String from, final String to) throws IOException {
        return FTPReply.isPositiveIntermediate(this.rnfr(from)) && FTPReply.isPositiveCompletion(this.rnto(to));
    }
    
    public boolean abort() throws IOException {
        return FTPReply.isPositiveCompletion(this.abor());
    }
    
    public boolean deleteFile(final String pathname) throws IOException {
        return FTPReply.isPositiveCompletion(this.dele(pathname));
    }
    
    public boolean removeDirectory(final String pathname) throws IOException {
        return FTPReply.isPositiveCompletion(this.rmd(pathname));
    }
    
    public boolean makeDirectory(final String pathname) throws IOException {
        return FTPReply.isPositiveCompletion(this.mkd(pathname));
    }
    
    public String printWorkingDirectory() throws IOException {
        if (this.pwd() != 257) {
            return null;
        }
        return __parsePathname(this._replyLines.get(this._replyLines.size() - 1));
    }
    
    public boolean sendSiteCommand(final String arguments) throws IOException {
        return FTPReply.isPositiveCompletion(this.site(arguments));
    }
    
    public String getSystemType() throws IOException {
        if (this.__systemName == null) {
            if (FTPReply.isPositiveCompletion(this.syst())) {
                this.__systemName = this._replyLines.get(this._replyLines.size() - 1).substring(4);
            }
            else {
                final String systDefault = System.getProperty("org.apache.commons.net.ftp.systemType.default");
                if (systDefault == null) {
                    throw new IOException("Unable to determine system type - response: " + this.getReplyString());
                }
                this.__systemName = systDefault;
            }
        }
        return this.__systemName;
    }
    
    public String listHelp() throws IOException {
        if (FTPReply.isPositiveCompletion(this.help())) {
            return this.getReplyString();
        }
        return null;
    }
    
    public String listHelp(final String command) throws IOException {
        if (FTPReply.isPositiveCompletion(this.help(command))) {
            return this.getReplyString();
        }
        return null;
    }
    
    public boolean sendNoOp() throws IOException {
        return FTPReply.isPositiveCompletion(this.noop());
    }
    
    public String[] listNames(final String pathname) throws IOException {
        final Socket socket = this._openDataConnection_(FTPCmd.NLST, this.getListArguments(pathname));
        if (socket == null) {
            return null;
        }
        final BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), this.getControlEncoding()));
        final ArrayList<String> results = new ArrayList<String>();
        String line;
        while ((line = reader.readLine()) != null) {
            results.add(line);
        }
        reader.close();
        socket.close();
        if (this.completePendingCommand()) {
            final String[] names = new String[results.size()];
            return results.toArray(names);
        }
        return null;
    }
    
    public String[] listNames() throws IOException {
        return this.listNames(null);
    }
    
    public FTPFile[] listFiles(final String pathname) throws IOException {
        final FTPListParseEngine engine = this.initiateListParsing((String)null, pathname);
        return engine.getFiles();
    }
    
    public FTPFile[] listFiles() throws IOException {
        return this.listFiles(null);
    }
    
    public FTPFile[] listFiles(final String pathname, final FTPFileFilter filter) throws IOException {
        final FTPListParseEngine engine = this.initiateListParsing((String)null, pathname);
        return engine.getFiles(filter);
    }
    
    public FTPFile[] listDirectories() throws IOException {
        return this.listDirectories(null);
    }
    
    public FTPFile[] listDirectories(final String parent) throws IOException {
        return this.listFiles(parent, FTPFileFilters.DIRECTORIES);
    }
    
    public FTPListParseEngine initiateListParsing() throws IOException {
        return this.initiateListParsing(null);
    }
    
    public FTPListParseEngine initiateListParsing(final String pathname) throws IOException {
        return this.initiateListParsing((String)null, pathname);
    }
    
    public FTPListParseEngine initiateListParsing(final String parserKey, final String pathname) throws IOException {
        this.__createParser(parserKey);
        return this.initiateListParsing(this.__entryParser, pathname);
    }
    
    void __createParser(final String parserKey) throws IOException {
        if (this.__entryParser == null || (parserKey != null && !this.__entryParserKey.equals(parserKey))) {
            if (null != parserKey) {
                this.__entryParser = this.__parserFactory.createFileEntryParser(parserKey);
                this.__entryParserKey = parserKey;
            }
            else if (null != this.__configuration && this.__configuration.getServerSystemKey().length() > 0) {
                this.__entryParser = this.__parserFactory.createFileEntryParser(this.__configuration);
                this.__entryParserKey = this.__configuration.getServerSystemKey();
            }
            else {
                String systemType = System.getProperty("org.apache.commons.net.ftp.systemType");
                if (systemType == null) {
                    systemType = this.getSystemType();
                    final Properties override = getOverrideProperties();
                    if (override != null) {
                        final String newType = override.getProperty(systemType);
                        if (newType != null) {
                            systemType = newType;
                        }
                    }
                }
                if (null != this.__configuration) {
                    this.__entryParser = this.__parserFactory.createFileEntryParser(new FTPClientConfig(systemType, this.__configuration));
                }
                else {
                    this.__entryParser = this.__parserFactory.createFileEntryParser(systemType);
                }
                this.__entryParserKey = systemType;
            }
        }
    }
    
    private FTPListParseEngine initiateListParsing(final FTPFileEntryParser parser, final String pathname) throws IOException {
        final Socket socket = this._openDataConnection_(FTPCmd.LIST, this.getListArguments(pathname));
        final FTPListParseEngine engine = new FTPListParseEngine(parser, this.__configuration);
        if (socket == null) {
            return engine;
        }
        try {
            engine.readServerList(socket.getInputStream(), this.getControlEncoding());
        }
        finally {
            Util.closeQuietly(socket);
        }
        this.completePendingCommand();
        return engine;
    }
    
    private FTPListParseEngine initiateMListParsing(final String pathname) throws IOException {
        final Socket socket = this._openDataConnection_(FTPCmd.MLSD, pathname);
        final FTPListParseEngine engine = new FTPListParseEngine(MLSxEntryParser.getInstance(), this.__configuration);
        if (socket == null) {
            return engine;
        }
        try {
            engine.readServerList(socket.getInputStream(), this.getControlEncoding());
        }
        finally {
            Util.closeQuietly(socket);
            this.completePendingCommand();
        }
        return engine;
    }
    
    protected String getListArguments(final String pathname) {
        if (!this.getListHiddenFiles()) {
            return pathname;
        }
        if (pathname != null) {
            final StringBuilder sb = new StringBuilder(pathname.length() + 3);
            sb.append("-a ");
            sb.append(pathname);
            return sb.toString();
        }
        return "-a";
    }
    
    public String getStatus() throws IOException {
        if (FTPReply.isPositiveCompletion(this.stat())) {
            return this.getReplyString();
        }
        return null;
    }
    
    public String getStatus(final String pathname) throws IOException {
        if (FTPReply.isPositiveCompletion(this.stat(pathname))) {
            return this.getReplyString();
        }
        return null;
    }
    
    public String getModificationTime(final String pathname) throws IOException {
        if (FTPReply.isPositiveCompletion(this.mdtm(pathname))) {
            return this.getReplyStrings()[0].substring(4);
        }
        return null;
    }
    
    public FTPFile mdtmFile(final String pathname) throws IOException {
        if (FTPReply.isPositiveCompletion(this.mdtm(pathname))) {
            final String reply = this.getReplyStrings()[0].substring(4);
            final FTPFile file = new FTPFile();
            file.setName(pathname);
            file.setRawListing(reply);
            file.setTimestamp(MLSxEntryParser.parseGMTdateTime(reply));
            return file;
        }
        return null;
    }
    
    public boolean setModificationTime(final String pathname, final String timeval) throws IOException {
        return FTPReply.isPositiveCompletion(this.mfmt(pathname, timeval));
    }
    
    public void setBufferSize(final int bufSize) {
        this.__bufferSize = bufSize;
    }
    
    public int getBufferSize() {
        return this.__bufferSize;
    }
    
    public void setSendDataSocketBufferSize(final int bufSize) {
        this.__sendDataSocketBufferSize = bufSize;
    }
    
    public int getSendDataSocketBufferSize() {
        return this.__sendDataSocketBufferSize;
    }
    
    public void setReceieveDataSocketBufferSize(final int bufSize) {
        this.__receiveDataSocketBufferSize = bufSize;
    }
    
    public int getReceiveDataSocketBufferSize() {
        return this.__receiveDataSocketBufferSize;
    }
    
    @Override
    public void configure(final FTPClientConfig config) {
        this.__configuration = config;
    }
    
    public void setListHiddenFiles(final boolean listHiddenFiles) {
        this.__listHiddenFiles = listHiddenFiles;
    }
    
    public boolean getListHiddenFiles() {
        return this.__listHiddenFiles;
    }
    
    public boolean isUseEPSVwithIPv4() {
        return this.__useEPSVwithIPv4;
    }
    
    public void setUseEPSVwithIPv4(final boolean selected) {
        this.__useEPSVwithIPv4 = selected;
    }
    
    public void setCopyStreamListener(final CopyStreamListener listener) {
        this.__copyStreamListener = listener;
    }
    
    public CopyStreamListener getCopyStreamListener() {
        return this.__copyStreamListener;
    }
    
    public void setControlKeepAliveTimeout(final long controlIdle) {
        this.__controlKeepAliveTimeout = controlIdle * 1000L;
    }
    
    public long getControlKeepAliveTimeout() {
        return this.__controlKeepAliveTimeout / 1000L;
    }
    
    public void setControlKeepAliveReplyTimeout(final int timeout) {
        this.__controlKeepAliveReplyTimeout = timeout;
    }
    
    public int getControlKeepAliveReplyTimeout() {
        return this.__controlKeepAliveReplyTimeout;
    }
    
    @Deprecated
    public void setPassiveNatWorkaround(final boolean enabled) {
        if (enabled) {
            this.__passiveNatWorkaroundStrategy = new NatServerResolverImpl(this);
        }
        else {
            this.__passiveNatWorkaroundStrategy = null;
        }
    }
    
    public void setPassiveNatWorkaroundStrategy(final HostnameResolver resolver) {
        this.__passiveNatWorkaroundStrategy = resolver;
    }
    
    private OutputStream getBufferedOutputStream(final OutputStream outputStream) {
        if (this.__bufferSize > 0) {
            return new BufferedOutputStream(outputStream, this.__bufferSize);
        }
        return new BufferedOutputStream(outputStream);
    }
    
    private InputStream getBufferedInputStream(final InputStream inputStream) {
        if (this.__bufferSize > 0) {
            return new BufferedInputStream(inputStream, this.__bufferSize);
        }
        return new BufferedInputStream(inputStream);
    }
    
    private CopyStreamListener __mergeListeners(final CopyStreamListener local) {
        if (local == null) {
            return this.__copyStreamListener;
        }
        if (this.__copyStreamListener == null) {
            return local;
        }
        final CopyStreamAdapter merged = new CopyStreamAdapter();
        merged.addCopyStreamListener(local);
        merged.addCopyStreamListener(this.__copyStreamListener);
        return merged;
    }
    
    public void setAutodetectUTF8(final boolean autodetect) {
        this.__autodetectEncoding = autodetect;
    }
    
    public boolean getAutodetectUTF8() {
        return this.__autodetectEncoding;
    }
    
    FTPFileEntryParser getEntryParser() {
        return this.__entryParser;
    }
    
    @Deprecated
    public String getSystemName() throws IOException {
        if (this.__systemName == null && FTPReply.isPositiveCompletion(this.syst())) {
            this.__systemName = this._replyLines.get(this._replyLines.size() - 1).substring(4);
        }
        return this.__systemName;
    }
    
    static {
        __PARMS_PAT = Pattern.compile("(\\d{1,3},\\d{1,3},\\d{1,3},\\d{1,3}),(\\d{1,3}),(\\d{1,3})");
    }
    
    private static class PropertiesSingleton
    {
        static final Properties PROPERTIES;
        
        static {
            final InputStream resourceAsStream = FTPClient.class.getResourceAsStream("/systemType.properties");
            Properties p = null;
            if (resourceAsStream != null) {
                p = new Properties();
                try {
                    p.load(resourceAsStream);
                }
                catch (IOException e) {}
                finally {
                    try {
                        resourceAsStream.close();
                    }
                    catch (IOException ex) {}
                }
            }
            PROPERTIES = p;
        }
    }
    
    public static class NatServerResolverImpl implements HostnameResolver
    {
        private FTPClient client;
        
        public NatServerResolverImpl(final FTPClient client) {
            this.client = client;
        }
        
        @Override
        public String resolve(final String hostname) throws UnknownHostException {
            String newHostname = hostname;
            final InetAddress host = InetAddress.getByName(newHostname);
            if (host.isSiteLocalAddress()) {
                final InetAddress remote = this.client.getRemoteAddress();
                if (!remote.isSiteLocalAddress()) {
                    newHostname = remote.getHostAddress();
                }
            }
            return newHostname;
        }
    }
    
    private static class CSL implements CopyStreamListener
    {
        private final FTPClient parent;
        private final long idle;
        private final int currentSoTimeout;
        private long time;
        private int notAcked;
        
        CSL(final FTPClient parent, final long idleTime, final int maxWait) throws SocketException {
            this.time = System.currentTimeMillis();
            this.idle = idleTime;
            this.parent = parent;
            this.currentSoTimeout = parent.getSoTimeout();
            parent.setSoTimeout(maxWait);
        }
        
        @Override
        public void bytesTransferred(final CopyStreamEvent event) {
            this.bytesTransferred(event.getTotalBytesTransferred(), event.getBytesTransferred(), event.getStreamSize());
        }
        
        @Override
        public void bytesTransferred(final long totalBytesTransferred, final int bytesTransferred, final long streamSize) {
            final long now = System.currentTimeMillis();
            if (now - this.time > this.idle) {
                try {
                    this.parent.__noop();
                }
                catch (SocketTimeoutException e) {
                    ++this.notAcked;
                }
                catch (IOException ex) {}
                this.time = now;
            }
        }
        
        void cleanUp() throws IOException {
            try {
                while (this.notAcked-- > 0) {
                    this.parent.__getReplyNoReport();
                }
            }
            finally {
                this.parent.setSoTimeout(this.currentSoTimeout);
            }
        }
    }
    
    public interface HostnameResolver
    {
        String resolve(final String p0) throws UnknownHostException;
    }
}
