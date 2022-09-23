// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.server.handler;

import org.eclipse.jetty.util.log.Log;
import java.util.Iterator;
import java.util.Map;
import javax.servlet.ServletException;
import java.io.IOException;
import java.net.InetSocketAddress;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.server.HttpChannel;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.util.IPAddressMap;
import org.eclipse.jetty.http.PathMap;
import org.eclipse.jetty.util.log.Logger;

public class IPAccessHandler extends HandlerWrapper
{
    private static final Logger LOG;
    PathMap<IPAddressMap<Boolean>> _white;
    PathMap<IPAddressMap<Boolean>> _black;
    boolean _whiteListByPath;
    
    public IPAccessHandler() {
        this._white = new PathMap<IPAddressMap<Boolean>>(true);
        this._black = new PathMap<IPAddressMap<Boolean>>(true);
        this._whiteListByPath = false;
    }
    
    public IPAccessHandler(final String[] white, final String[] black) {
        this._white = new PathMap<IPAddressMap<Boolean>>(true);
        this._black = new PathMap<IPAddressMap<Boolean>>(true);
        this._whiteListByPath = false;
        if (white != null && white.length > 0) {
            this.setWhite(white);
        }
        if (black != null && black.length > 0) {
            this.setBlack(black);
        }
    }
    
    public void addWhite(final String entry) {
        this.add(entry, this._white);
    }
    
    public void addBlack(final String entry) {
        this.add(entry, this._black);
    }
    
    public void setWhite(final String[] entries) {
        this.set(entries, this._white);
    }
    
    public void setBlack(final String[] entries) {
        this.set(entries, this._black);
    }
    
    public void setWhiteListByPath(final boolean whiteListByPath) {
        this._whiteListByPath = whiteListByPath;
    }
    
    @Override
    public void handle(final String target, final Request baseRequest, final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
        final HttpChannel channel = baseRequest.getHttpChannel();
        if (channel != null) {
            final EndPoint endp = channel.getEndPoint();
            if (endp != null) {
                final InetSocketAddress address = endp.getRemoteAddress();
                if (address != null && !this.isAddrUriAllowed(address.getHostString(), baseRequest.getPathInfo())) {
                    response.sendError(403);
                    baseRequest.setHandled(true);
                    return;
                }
            }
        }
        this.getHandler().handle(target, baseRequest, request, response);
    }
    
    protected void add(final String entry, final PathMap<IPAddressMap<Boolean>> patternMap) {
        if (entry != null && entry.length() > 0) {
            boolean deprecated = false;
            int idx;
            if (entry.indexOf(124) > 0) {
                idx = entry.indexOf(124);
            }
            else {
                idx = entry.indexOf(47);
                deprecated = (idx >= 0);
            }
            final String addr = (idx > 0) ? entry.substring(0, idx) : entry;
            String path = (idx > 0) ? entry.substring(idx) : "/*";
            if (addr.endsWith(".")) {
                deprecated = true;
            }
            if (path != null && (path.startsWith("|") || path.startsWith("/*."))) {
                path = path.substring(1);
            }
            IPAddressMap<Boolean> addrMap = patternMap.get(path);
            if (addrMap == null) {
                addrMap = new IPAddressMap<Boolean>();
                patternMap.put(path, addrMap);
            }
            if (addr != null && !"".equals(addr)) {
                addrMap.put(addr, true);
            }
            if (deprecated) {
                IPAccessHandler.LOG.debug(this.toString() + " - deprecated specification syntax: " + entry, new Object[0]);
            }
        }
    }
    
    protected void set(final String[] entries, final PathMap<IPAddressMap<Boolean>> patternMap) {
        patternMap.clear();
        if (entries != null && entries.length > 0) {
            for (final String addrPath : entries) {
                this.add(addrPath, patternMap);
            }
        }
    }
    
    protected boolean isAddrUriAllowed(final String addr, final String path) {
        if (this._white.size() > 0) {
            boolean match = false;
            boolean matchedByPath = false;
            for (final Map.Entry<String, IPAddressMap<Boolean>> entry : this._white.getMatches(path)) {
                matchedByPath = true;
                final IPAddressMap<Boolean> addrMap = entry.getValue();
                if (addrMap != null && (addrMap.size() == 0 || addrMap.match(addr) != null)) {
                    match = true;
                    break;
                }
            }
            if (this._whiteListByPath) {
                if (matchedByPath && !match) {
                    return false;
                }
            }
            else if (!match) {
                return false;
            }
        }
        if (this._black.size() > 0) {
            for (final Map.Entry<String, IPAddressMap<Boolean>> entry2 : this._black.getMatches(path)) {
                final IPAddressMap<Boolean> addrMap2 = entry2.getValue();
                if (addrMap2 != null && (addrMap2.size() == 0 || addrMap2.match(addr) != null)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    @Override
    public String dump() {
        final StringBuilder buf = new StringBuilder();
        buf.append(this.toString());
        buf.append(" WHITELIST:\n");
        this.dump(buf, this._white);
        buf.append(this.toString());
        buf.append(" BLACKLIST:\n");
        this.dump(buf, this._black);
        return buf.toString();
    }
    
    protected void dump(final StringBuilder buf, final PathMap<IPAddressMap<Boolean>> patternMap) {
        for (final String path : patternMap.keySet()) {
            for (final String addr : patternMap.get(path).keySet()) {
                buf.append("# ");
                buf.append(addr);
                buf.append("|");
                buf.append(path);
                buf.append("\n");
            }
        }
    }
    
    static {
        LOG = Log.getLogger(IPAccessHandler.class);
    }
}
