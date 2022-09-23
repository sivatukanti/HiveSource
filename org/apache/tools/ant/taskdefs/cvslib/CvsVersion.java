// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.tools.ant.taskdefs.cvslib;

import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.util.StringTokenizer;
import org.apache.tools.ant.taskdefs.AbstractCvsTask;

public class CvsVersion extends AbstractCvsTask
{
    static final long VERSION_1_11_2 = 11102L;
    static final long MULTIPLY = 100L;
    private String clientVersion;
    private String serverVersion;
    private String clientVersionProperty;
    private String serverVersionProperty;
    
    public String getClientVersion() {
        return this.clientVersion;
    }
    
    public String getServerVersion() {
        return this.serverVersion;
    }
    
    public void setClientVersionProperty(final String clientVersionProperty) {
        this.clientVersionProperty = clientVersionProperty;
    }
    
    public void setServerVersionProperty(final String serverVersionProperty) {
        this.serverVersionProperty = serverVersionProperty;
    }
    
    public boolean supportsCvsLogWithSOption() {
        if (this.serverVersion == null) {
            return false;
        }
        final StringTokenizer tokenizer = new StringTokenizer(this.serverVersion, ".");
        long counter = 10000L;
        long version = 0L;
        while (tokenizer.hasMoreTokens()) {
            String s;
            int i;
            for (s = tokenizer.nextToken(), i = 0, i = 0; i < s.length() && Character.isDigit(s.charAt(i)); ++i) {}
            final String s2 = s.substring(0, i);
            version += counter * Long.parseLong(s2);
            if (counter == 1L) {
                break;
            }
            counter /= 100L;
        }
        return version >= 11102L;
    }
    
    @Override
    public void execute() {
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        this.setOutputStream(bos);
        final ByteArrayOutputStream berr = new ByteArrayOutputStream();
        this.setErrorStream(berr);
        this.setCommand("version");
        super.execute();
        final String output = bos.toString();
        this.log("Received version response \"" + output + "\"", 4);
        final StringTokenizer st = new StringTokenizer(output);
        boolean client = false;
        boolean server = false;
        String cvs = null;
        String cachedVersion = null;
        boolean haveReadAhead = false;
        while (haveReadAhead || st.hasMoreTokens()) {
            final String currentToken = haveReadAhead ? cachedVersion : st.nextToken();
            haveReadAhead = false;
            if (currentToken.equals("Client:")) {
                client = true;
            }
            else if (currentToken.equals("Server:")) {
                server = true;
            }
            else if (currentToken.startsWith("(CVS") && currentToken.endsWith(")")) {
                cvs = ((currentToken.length() == 5) ? "" : (" " + currentToken));
            }
            if (!client && !server && cvs != null && cachedVersion == null && st.hasMoreTokens()) {
                cachedVersion = st.nextToken();
                haveReadAhead = true;
            }
            else if (client && cvs != null) {
                if (st.hasMoreTokens()) {
                    this.clientVersion = st.nextToken() + cvs;
                }
                client = false;
                cvs = null;
            }
            else if (server && cvs != null) {
                if (st.hasMoreTokens()) {
                    this.serverVersion = st.nextToken() + cvs;
                }
                server = false;
                cvs = null;
            }
            else {
                if (!currentToken.equals("(client/server)") || cvs == null || cachedVersion == null || client || server) {
                    continue;
                }
                server = (client = true);
                final String string = cachedVersion + cvs;
                this.serverVersion = string;
                this.clientVersion = string;
                cvs = (cachedVersion = null);
            }
        }
        if (this.clientVersionProperty != null) {
            this.getProject().setNewProperty(this.clientVersionProperty, this.clientVersion);
        }
        if (this.serverVersionProperty != null) {
            this.getProject().setNewProperty(this.serverVersionProperty, this.serverVersion);
        }
    }
}
