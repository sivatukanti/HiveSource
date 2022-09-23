// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.util.security;

import org.eclipse.jetty.util.log.Log;
import java.io.IOException;
import java.util.Locale;
import java.nio.charset.StandardCharsets;
import org.eclipse.jetty.util.log.Logger;

public class Password extends Credential
{
    private static final Logger LOG;
    private static final long serialVersionUID = 5062906681431569445L;
    public static final String __OBFUSCATE = "OBF:";
    private String _pw;
    
    public Password(final String password) {
        this._pw = password;
        while (this._pw != null && this._pw.startsWith("OBF:")) {
            this._pw = deobfuscate(this._pw);
        }
    }
    
    @Override
    public String toString() {
        return this._pw;
    }
    
    public String toStarString() {
        return "*****************************************************".substring(0, this._pw.length());
    }
    
    @Override
    public boolean check(final Object credentials) {
        if (this == credentials) {
            return true;
        }
        if (credentials instanceof Password) {
            return credentials.equals(this._pw);
        }
        if (credentials instanceof String) {
            return Credential.stringEquals(this._pw, (String)credentials);
        }
        if (credentials instanceof char[]) {
            return Credential.stringEquals(this._pw, new String((char[])credentials));
        }
        return credentials instanceof Credential && ((Credential)credentials).check(this._pw);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (null == o) {
            return false;
        }
        if (o instanceof Password) {
            return Credential.stringEquals(this._pw, ((Password)o)._pw);
        }
        return o instanceof String && Credential.stringEquals(this._pw, (String)o);
    }
    
    @Override
    public int hashCode() {
        return (null == this._pw) ? super.hashCode() : this._pw.hashCode();
    }
    
    public static String obfuscate(final String s) {
        final StringBuilder buf = new StringBuilder();
        final byte[] b = s.getBytes(StandardCharsets.UTF_8);
        buf.append("OBF:");
        for (int i = 0; i < b.length; ++i) {
            final byte b2 = b[i];
            final byte b3 = b[b.length - (i + 1)];
            if (b2 < 0 || b3 < 0) {
                final int i2 = (0xFF & b2) * 256 + (0xFF & b3);
                final String x = Integer.toString(i2, 36).toLowerCase(Locale.ENGLISH);
                buf.append("U0000", 0, 5 - x.length());
                buf.append(x);
            }
            else {
                final int i3 = 127 + b2 + b3;
                final int i4 = 127 + b2 - b3;
                final int i5 = i3 * 256 + i4;
                final String x2 = Integer.toString(i5, 36).toLowerCase(Locale.ENGLISH);
                final int j0 = Integer.parseInt(x2, 36);
                final int j2 = i5 / 256;
                final int j3 = i5 % 256;
                final byte bx = (byte)((j2 + j3 - 254) / 2);
                buf.append("000", 0, 4 - x2.length());
                buf.append(x2);
            }
        }
        return buf.toString();
    }
    
    public static String deobfuscate(String s) {
        if (s.startsWith("OBF:")) {
            s = s.substring(4);
        }
        final byte[] b = new byte[s.length() / 2];
        int l = 0;
        for (int i = 0; i < s.length(); i += 4) {
            if (s.charAt(i) == 'U') {
                ++i;
                final String x = s.substring(i, i + 4);
                final int i2 = Integer.parseInt(x, 36);
                final byte bx = (byte)(i2 >> 8);
                b[l++] = bx;
            }
            else {
                final String x = s.substring(i, i + 4);
                final int i2 = Integer.parseInt(x, 36);
                final int i3 = i2 / 256;
                final int i4 = i2 % 256;
                final byte bx2 = (byte)((i3 + i4 - 254) / 2);
                b[l++] = bx2;
            }
        }
        return new String(b, 0, l, StandardCharsets.UTF_8);
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
            Password.LOG.warn("EXCEPTION ", e);
        }
        if (passwd == null || passwd.length() == 0) {
            passwd = promptDft;
        }
        return new Password(passwd);
    }
    
    public static void main(final String[] arg) {
        if (arg.length != 1 && arg.length != 2) {
            System.err.println("Usage - java " + Password.class.getName() + " [<user>] <password>");
            System.err.println("If the password is ?, the user will be prompted for the password");
            System.exit(1);
        }
        final String p = arg[arg.length != 1];
        final Password pw = new Password(p);
        System.err.println(pw.toString());
        System.err.println(obfuscate(pw.toString()));
        System.err.println(MD5.digest(p));
        if (arg.length == 2) {
            System.err.println(Crypt.crypt(arg[0], pw.toString()));
        }
    }
    
    static {
        LOG = Log.getLogger(Password.class);
    }
}
