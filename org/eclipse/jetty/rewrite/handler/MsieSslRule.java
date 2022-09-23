// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.rewrite.handler;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.eclipse.jetty.util.StringMap;

public class MsieSslRule extends Rule
{
    private static final int IEv5 = 53;
    private static final int IEv6 = 54;
    private static StringMap __IE6_BadOS;
    
    public MsieSslRule() {
        MsieSslRule.__IE6_BadOS.put("NT 5.01", Boolean.TRUE);
        MsieSslRule.__IE6_BadOS.put("NT 5.0", Boolean.TRUE);
        MsieSslRule.__IE6_BadOS.put("NT 4.0", Boolean.TRUE);
        MsieSslRule.__IE6_BadOS.put("98", Boolean.TRUE);
        MsieSslRule.__IE6_BadOS.put("98; Win 9x 4.90", Boolean.TRUE);
        MsieSslRule.__IE6_BadOS.put("95", Boolean.TRUE);
        MsieSslRule.__IE6_BadOS.put("CE", Boolean.TRUE);
        this._handling = false;
        this._terminating = false;
    }
    
    @Override
    public String matchAndApply(final String target, final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        if (request.isSecure()) {
            final String user_agent = request.getHeader("User-Agent");
            if (user_agent != null) {
                final int msie = user_agent.indexOf("MSIE");
                if (msie > 0 && user_agent.length() - msie > 5) {
                    final int ieVersion = user_agent.charAt(msie + 5);
                    if (ieVersion <= 53) {
                        response.setHeader("Connection", "close");
                        return target;
                    }
                    if (ieVersion == 54) {
                        final int windows = user_agent.indexOf("Windows", msie + 5);
                        if (windows > 0) {
                            final int end = user_agent.indexOf(41, windows + 8);
                            if (end < 0 || MsieSslRule.__IE6_BadOS.getEntry(user_agent, windows + 8, end - windows - 8) != null) {
                                response.setHeader("Connection", "close");
                                return target;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
    
    static {
        MsieSslRule.__IE6_BadOS = new StringMap();
    }
}
