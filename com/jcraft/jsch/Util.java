// 
// Decompiled by Procyon v0.5.36
// 

package com.jcraft.jsch;

import java.io.IOException;
import java.io.FileInputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.Vector;

class Util
{
    private static final byte[] b64;
    private static String[] chars;
    static final byte[] empty;
    
    private static byte val(final byte foo) {
        if (foo == 61) {
            return 0;
        }
        for (int j = 0; j < Util.b64.length; ++j) {
            if (foo == Util.b64[j]) {
                return (byte)j;
            }
        }
        return 0;
    }
    
    static byte[] fromBase64(final byte[] buf, final int start, final int length) throws JSchException {
        try {
            final byte[] foo = new byte[length];
            int j = 0;
            for (int i = start; i < start + length; i += 4) {
                foo[j] = (byte)(val(buf[i]) << 2 | (val(buf[i + 1]) & 0x30) >>> 4);
                if (buf[i + 2] == 61) {
                    ++j;
                    break;
                }
                foo[j + 1] = (byte)((val(buf[i + 1]) & 0xF) << 4 | (val(buf[i + 2]) & 0x3C) >>> 2);
                if (buf[i + 3] == 61) {
                    j += 2;
                    break;
                }
                foo[j + 2] = (byte)((val(buf[i + 2]) & 0x3) << 6 | (val(buf[i + 3]) & 0x3F));
                j += 3;
            }
            final byte[] bar = new byte[j];
            System.arraycopy(foo, 0, bar, 0, j);
            return bar;
        }
        catch (ArrayIndexOutOfBoundsException e) {
            throw new JSchException("fromBase64: invalid base64 data", e);
        }
    }
    
    static byte[] toBase64(final byte[] buf, final int start, final int length) {
        final byte[] tmp = new byte[length * 2];
        int foo = length / 3 * 3 + start;
        int i = 0;
        int j;
        for (j = start; j < foo; j += 3) {
            int k = buf[j] >>> 2 & 0x3F;
            tmp[i++] = Util.b64[k];
            k = ((buf[j] & 0x3) << 4 | (buf[j + 1] >>> 4 & 0xF));
            tmp[i++] = Util.b64[k];
            k = ((buf[j + 1] & 0xF) << 2 | (buf[j + 2] >>> 6 & 0x3));
            tmp[i++] = Util.b64[k];
            k = (buf[j + 2] & 0x3F);
            tmp[i++] = Util.b64[k];
        }
        foo = start + length - foo;
        if (foo == 1) {
            int k = buf[j] >>> 2 & 0x3F;
            tmp[i++] = Util.b64[k];
            k = ((buf[j] & 0x3) << 4 & 0x3F);
            tmp[i++] = Util.b64[k];
            tmp[i++] = 61;
            tmp[i++] = 61;
        }
        else if (foo == 2) {
            int k = buf[j] >>> 2 & 0x3F;
            tmp[i++] = Util.b64[k];
            k = ((buf[j] & 0x3) << 4 | (buf[j + 1] >>> 4 & 0xF));
            tmp[i++] = Util.b64[k];
            k = ((buf[j + 1] & 0xF) << 2 & 0x3F);
            tmp[i++] = Util.b64[k];
            tmp[i++] = 61;
        }
        final byte[] bar = new byte[i];
        System.arraycopy(tmp, 0, bar, 0, i);
        return bar;
    }
    
    static String[] split(final String foo, final String split) {
        if (foo == null) {
            return null;
        }
        final byte[] buf = str2byte(foo);
        final Vector bar = new Vector();
        int start = 0;
        while (true) {
            final int index = foo.indexOf(split, start);
            if (index < 0) {
                break;
            }
            bar.addElement(byte2str(buf, start, index - start));
            start = index + 1;
        }
        bar.addElement(byte2str(buf, start, buf.length - start));
        final String[] result = new String[bar.size()];
        for (int i = 0; i < result.length; ++i) {
            result[i] = bar.elementAt(i);
        }
        return result;
    }
    
    static boolean glob(final byte[] pattern, final byte[] name) {
        return glob0(pattern, 0, name, 0);
    }
    
    private static boolean glob0(final byte[] pattern, final int pattern_index, final byte[] name, final int name_index) {
        if (name.length > 0 && name[0] == 46) {
            return pattern.length > 0 && pattern[0] == 46 && ((pattern.length == 2 && pattern[1] == 42) || glob(pattern, pattern_index + 1, name, name_index + 1));
        }
        return glob(pattern, pattern_index, name, name_index);
    }
    
    private static boolean glob(final byte[] pattern, final int pattern_index, final byte[] name, final int name_index) {
        final int patternlen = pattern.length;
        if (patternlen == 0) {
            return false;
        }
        final int namelen = name.length;
        int i = pattern_index;
        int j = name_index;
        while (i < patternlen && j < namelen) {
            if (pattern[i] == 92) {
                if (i + 1 == patternlen) {
                    return false;
                }
                ++i;
                if (pattern[i] != name[j]) {
                    return false;
                }
                i += skipUTF8Char(pattern[i]);
                j += skipUTF8Char(name[j]);
            }
            else if (pattern[i] == 42) {
                while (i < patternlen && pattern[i] == 42) {
                    ++i;
                }
                if (patternlen == i) {
                    return true;
                }
                byte foo = pattern[i];
                if (foo == 63) {
                    while (j < namelen) {
                        if (glob(pattern, i, name, j)) {
                            return true;
                        }
                        j += skipUTF8Char(name[j]);
                    }
                    return false;
                }
                if (foo != 92) {
                    while (j < namelen) {
                        if (foo == name[j] && glob(pattern, i, name, j)) {
                            return true;
                        }
                        j += skipUTF8Char(name[j]);
                    }
                    return false;
                }
                if (i + 1 == patternlen) {
                    return false;
                }
                ++i;
                foo = pattern[i];
                while (j < namelen) {
                    if (foo == name[j] && glob(pattern, i + skipUTF8Char(foo), name, j + skipUTF8Char(name[j]))) {
                        return true;
                    }
                    j += skipUTF8Char(name[j]);
                }
                return false;
            }
            else if (pattern[i] == 63) {
                ++i;
                j += skipUTF8Char(name[j]);
            }
            else {
                if (pattern[i] != name[j]) {
                    return false;
                }
                i += skipUTF8Char(pattern[i]);
                j += skipUTF8Char(name[j]);
                if (j < namelen) {
                    continue;
                }
                if (i >= patternlen) {
                    return true;
                }
                if (pattern[i] == 42) {
                    break;
                }
                continue;
            }
        }
        if (i == patternlen && j == namelen) {
            return true;
        }
        if (j >= namelen && pattern[i] == 42) {
            boolean ok = true;
            while (i < patternlen) {
                if (pattern[i++] != 42) {
                    ok = false;
                    break;
                }
            }
            return ok;
        }
        return false;
    }
    
    static String quote(final String path) {
        final byte[] _path = str2byte(path);
        int count = 0;
        for (int i = 0; i < _path.length; ++i) {
            final byte b = _path[i];
            if (b == 92 || b == 63 || b == 42) {
                ++count;
            }
        }
        if (count == 0) {
            return path;
        }
        final byte[] _path2 = new byte[_path.length + count];
        int j = 0;
        int k = 0;
        while (j < _path.length) {
            final byte b2 = _path[j];
            if (b2 == 92 || b2 == 63 || b2 == 42) {
                _path2[k++] = 92;
            }
            _path2[k++] = b2;
            ++j;
        }
        return byte2str(_path2);
    }
    
    static String unquote(final String path) {
        final byte[] foo = str2byte(path);
        final byte[] bar = unquote(foo);
        if (foo.length == bar.length) {
            return path;
        }
        return byte2str(bar);
    }
    
    static byte[] unquote(final byte[] path) {
        int pathlen = path.length;
        for (int i = 0; i < pathlen; ++i) {
            if (path[i] == 92) {
                if (i + 1 == pathlen) {
                    break;
                }
                System.arraycopy(path, i + 1, path, i, path.length - (i + 1));
                --pathlen;
            }
            else {}
        }
        if (pathlen == path.length) {
            return path;
        }
        final byte[] foo = new byte[pathlen];
        System.arraycopy(path, 0, foo, 0, pathlen);
        return foo;
    }
    
    static String getFingerPrint(final HASH hash, final byte[] data) {
        try {
            hash.init();
            hash.update(data, 0, data.length);
            final byte[] foo = hash.digest();
            final StringBuffer sb = new StringBuffer();
            for (int i = 0; i < foo.length; ++i) {
                final int bar = foo[i] & 0xFF;
                sb.append(Util.chars[bar >>> 4 & 0xF]);
                sb.append(Util.chars[bar & 0xF]);
                if (i + 1 < foo.length) {
                    sb.append(":");
                }
            }
            return sb.toString();
        }
        catch (Exception e) {
            return "???";
        }
    }
    
    static boolean array_equals(final byte[] foo, final byte[] bar) {
        final int i = foo.length;
        if (i != bar.length) {
            return false;
        }
        for (int j = 0; j < i; ++j) {
            if (foo[j] != bar[j]) {
                return false;
            }
        }
        return true;
    }
    
    static Socket createSocket(final String host, final int port, final int timeout) throws JSchException {
        Socket socket = null;
        if (timeout == 0) {
            try {
                socket = new Socket(host, port);
                return socket;
            }
            catch (Exception e) {
                final String message = e.toString();
                if (e instanceof Throwable) {
                    throw new JSchException(message, e);
                }
                throw new JSchException(message);
            }
        }
        final String _host = host;
        final int _port = port;
        final Socket[] sockp = { null };
        final Exception[] ee = { null };
        String message2 = "";
        Thread tmp = new Thread(new Runnable() {
            public void run() {
                sockp[0] = null;
                try {
                    sockp[0] = new Socket(_host, _port);
                }
                catch (Exception e) {
                    ee[0] = e;
                    if (sockp[0] != null && sockp[0].isConnected()) {
                        try {
                            sockp[0].close();
                        }
                        catch (Exception ex) {}
                    }
                    sockp[0] = null;
                }
            }
        });
        tmp.setName("Opening Socket " + host);
        tmp.start();
        try {
            tmp.join(timeout);
            message2 = "timeout: ";
        }
        catch (InterruptedException ex) {}
        if (sockp[0] != null && sockp[0].isConnected()) {
            socket = sockp[0];
            return socket;
        }
        message2 += "socket is not established";
        if (ee[0] != null) {
            message2 = ee[0].toString();
        }
        tmp.interrupt();
        tmp = null;
        throw new JSchException(message2, ee[0]);
    }
    
    static byte[] str2byte(final String str, final String encoding) {
        if (str == null) {
            return null;
        }
        try {
            return str.getBytes(encoding);
        }
        catch (UnsupportedEncodingException e) {
            return str.getBytes();
        }
    }
    
    static byte[] str2byte(final String str) {
        return str2byte(str, "UTF-8");
    }
    
    static String byte2str(final byte[] str, final String encoding) {
        return byte2str(str, 0, str.length, encoding);
    }
    
    static String byte2str(final byte[] str, final int s, final int l, final String encoding) {
        try {
            return new String(str, s, l, encoding);
        }
        catch (UnsupportedEncodingException e) {
            return new String(str, s, l);
        }
    }
    
    static String byte2str(final byte[] str) {
        return byte2str(str, 0, str.length, "UTF-8");
    }
    
    static String byte2str(final byte[] str, final int s, final int l) {
        return byte2str(str, s, l, "UTF-8");
    }
    
    static String toHex(final byte[] str) {
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < str.length; ++i) {
            final String foo = Integer.toHexString(str[i] & 0xFF);
            sb.append("0x" + ((foo.length() == 1) ? "0" : "") + foo);
            if (i + 1 < str.length) {
                sb.append(":");
            }
        }
        return sb.toString();
    }
    
    static void bzero(final byte[] foo) {
        if (foo == null) {
            return;
        }
        for (int i = 0; i < foo.length; ++i) {
            foo[i] = 0;
        }
    }
    
    static String diffString(final String str, final String[] not_available) {
        final String[] stra = split(str, ",");
        String result = null;
        int i = 0;
    Label_0012:
        while (i < stra.length) {
            while (true) {
                for (int j = 0; j < not_available.length; ++j) {
                    if (stra[i].equals(not_available[j])) {
                        ++i;
                        continue Label_0012;
                    }
                }
                if (result == null) {
                    result = stra[i];
                    continue;
                }
                result = result + "," + stra[i];
                continue;
            }
        }
        return result;
    }
    
    static String checkTilde(String str) {
        try {
            if (str.startsWith("~")) {
                str = str.replace("~", System.getProperty("user.home"));
            }
        }
        catch (SecurityException ex) {}
        return str;
    }
    
    private static int skipUTF8Char(final byte b) {
        if ((byte)(b & 0x80) == 0) {
            return 1;
        }
        if ((byte)(b & 0xE0) == -64) {
            return 2;
        }
        if ((byte)(b & 0xF0) == -32) {
            return 3;
        }
        return 1;
    }
    
    static byte[] fromFile(String _file) throws IOException {
        _file = checkTilde(_file);
        final File file = new File(_file);
        final FileInputStream fis = new FileInputStream(_file);
        try {
            final byte[] result = new byte[(int)file.length()];
            int len = 0;
            while (true) {
                final int i = fis.read(result, len, result.length - len);
                if (i <= 0) {
                    break;
                }
                len += i;
            }
            fis.close();
            return result;
        }
        finally {
            if (fis != null) {
                fis.close();
            }
        }
    }
    
    static {
        b64 = str2byte("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=");
        Util.chars = new String[] { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };
        empty = str2byte("");
    }
}
