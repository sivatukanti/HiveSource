// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.jetty.security;

import java.io.IOException;
import org.mortbay.log.Log;

public class Password extends Credential
{
    public static final String __OBFUSCATE = "OBF:";
    private String _pw;
    
    public Password(final String password) {
        this._pw = password;
        while (this._pw != null && this._pw.startsWith("OBF:")) {
            this._pw = deobfuscate(this._pw);
        }
    }
    
    public String toString() {
        return this._pw;
    }
    
    public String toStarString() {
        return "*****************************************************".substring(0, this._pw.length());
    }
    
    public boolean check(final Object credentials) {
        if (this == credentials) {
            return true;
        }
        if (credentials instanceof Password) {
            return credentials.equals(this._pw);
        }
        if (credentials instanceof String) {
            return credentials.equals(this._pw);
        }
        return credentials instanceof Credential && ((Credential)credentials).check(this._pw);
    }
    
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (null == o) {
            return false;
        }
        if (o instanceof Password) {
            final Password p = (Password)o;
            return p._pw == this._pw || (null != this._pw && this._pw.equals(p._pw));
        }
        return o instanceof String && o.equals(this._pw);
    }
    
    public int hashCode() {
        return (null == this._pw) ? super.hashCode() : this._pw.hashCode();
    }
    
    public static String obfuscate(final String s) {
        final StringBuffer buf = new StringBuffer();
        final byte[] b = s.getBytes();
        synchronized (buf) {
            buf.append("OBF:");
            for (int i = 0; i < b.length; ++i) {
                final byte b2 = b[i];
                final byte b3 = b[s.length() - (i + 1)];
                final int i2 = 127 + b2 + b3;
                final int i3 = 127 + b2 - b3;
                final int i4 = i2 * 256 + i3;
                final String x = Integer.toString(i4, 36);
                switch (x.length()) {
                    case 1: {
                        buf.append('0');
                    }
                    case 2: {
                        buf.append('0');
                    }
                    case 3: {
                        buf.append('0');
                        break;
                    }
                }
                buf.append(x);
            }
            return buf.toString();
        }
    }
    
    public static String deobfuscate(String s) {
        if (s.startsWith("OBF:")) {
            s = s.substring(4);
        }
        final byte[] b = new byte[s.length() / 2];
        int l = 0;
        for (int i = 0; i < s.length(); i += 4) {
            final String x = s.substring(i, i + 4);
            final int i2 = Integer.parseInt(x, 36);
            final int i3 = i2 / 256;
            final int i4 = i2 % 256;
            b[l++] = (byte)((i3 + i4 - 254) / 2);
        }
        return new String(b, 0, l);
    }
    
    public static Password getPassword(final String realm, final String dft, final String promptDft) {
        String passwd = System.getProperty(realm, dft);
        if (passwd != null) {
            if (passwd.length() != 0) {
                return new Password(passwd);
            }
        }
        try {
            System.out.print(realm + ((promptDft != null && promptDft.length() > 0) ? " [dft]" : "") + " : ");
            System.out.flush();
            final byte[] buf = new byte[512];
            final int len = System.in.read(buf);
            if (len > 0) {
                passwd = new String(buf, 0, len).trim();
            }
        }
        catch (IOException e) {
            Log.warn("EXCEPTION ", e);
        }
        if (passwd == null || passwd.length() == 0) {
            passwd = promptDft;
        }
        return new Password(passwd);
    }
    
    public static void main(final String[] arg) {
        if (arg.length != 1 && arg.length != 2) {
            System.err.println("Usage - java org.mortbay.jetty.security.Password [<user>] <password>");
            System.err.println("If the password is ?, the user will be prompted for the password");
            System.exit(1);
        }
        final String p = arg[arg.length != 1];
        final Password pw = "?".equals(p) ? new Password(p) : new Password(p);
        System.err.println(pw.toString());
        System.err.println(obfuscate(pw.toString()));
        System.err.println(MD5.digest(p));
        if (arg.length == 2) {
            System.err.println(Crypt.crypt(arg[0], pw.toString()));
        }
    }
}
