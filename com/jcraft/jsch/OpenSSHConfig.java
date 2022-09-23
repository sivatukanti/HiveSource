// 
// Decompiled by Procyon v0.5.36
// 

package com.jcraft.jsch;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Vector;
import java.util.Hashtable;

public class OpenSSHConfig implements ConfigRepository
{
    private final Hashtable config;
    private final Vector hosts;
    private static final Hashtable keymap;
    
    public static OpenSSHConfig parse(final String conf) throws IOException {
        final Reader r = new StringReader(conf);
        try {
            return new OpenSSHConfig(r);
        }
        finally {
            r.close();
        }
    }
    
    public static OpenSSHConfig parseFile(final String file) throws IOException {
        final Reader r = new FileReader(Util.checkTilde(file));
        try {
            return new OpenSSHConfig(r);
        }
        finally {
            r.close();
        }
    }
    
    OpenSSHConfig(final Reader r) throws IOException {
        this.config = new Hashtable();
        this.hosts = new Vector();
        this._parse(r);
    }
    
    private void _parse(final Reader r) throws IOException {
        final BufferedReader br = new BufferedReader(r);
        String host = "";
        Vector kv = new Vector();
        String l = null;
        while ((l = br.readLine()) != null) {
            l = l.trim();
            if (l.length() != 0) {
                if (l.startsWith("#")) {
                    continue;
                }
                final String[] key_value = l.split("[= \t]", 2);
                for (int i = 0; i < key_value.length; ++i) {
                    key_value[i] = key_value[i].trim();
                }
                if (key_value.length <= 1) {
                    continue;
                }
                if (key_value[0].equals("Host")) {
                    this.config.put(host, kv);
                    this.hosts.addElement(host);
                    host = key_value[1];
                    kv = new Vector();
                }
                else {
                    kv.addElement(key_value);
                }
            }
        }
        this.config.put(host, kv);
        this.hosts.addElement(host);
    }
    
    public Config getConfig(final String host) {
        return new MyConfig(host);
    }
    
    static {
        (keymap = new Hashtable()).put("kex", "KexAlgorithms");
        OpenSSHConfig.keymap.put("server_host_key", "HostKeyAlgorithms");
        OpenSSHConfig.keymap.put("cipher.c2s", "Ciphers");
        OpenSSHConfig.keymap.put("cipher.s2c", "Ciphers");
        OpenSSHConfig.keymap.put("mac.c2s", "Macs");
        OpenSSHConfig.keymap.put("mac.s2c", "Macs");
        OpenSSHConfig.keymap.put("compression.s2c", "Compression");
        OpenSSHConfig.keymap.put("compression.c2s", "Compression");
        OpenSSHConfig.keymap.put("compression_level", "CompressionLevel");
        OpenSSHConfig.keymap.put("MaxAuthTries", "NumberOfPasswordPrompts");
    }
    
    class MyConfig implements Config
    {
        private String host;
        private Vector _configs;
        
        MyConfig(final String host) {
            this._configs = new Vector();
            this.host = host;
            this._configs.addElement(OpenSSHConfig.this.config.get(""));
            final byte[] _host = Util.str2byte(host);
            if (OpenSSHConfig.this.hosts.size() > 1) {
                for (int i = 1; i < OpenSSHConfig.this.hosts.size(); ++i) {
                    final String[] patterns = OpenSSHConfig.this.hosts.elementAt(i).split("[ \t]");
                    for (int j = 0; j < patterns.length; ++j) {
                        boolean negate = false;
                        String foo = patterns[j].trim();
                        if (foo.startsWith("!")) {
                            negate = true;
                            foo = foo.substring(1).trim();
                        }
                        if (Util.glob(Util.str2byte(foo), _host)) {
                            if (!negate) {
                                this._configs.addElement(OpenSSHConfig.this.config.get(OpenSSHConfig.this.hosts.elementAt(i)));
                            }
                        }
                        else if (negate) {
                            this._configs.addElement(OpenSSHConfig.this.config.get(OpenSSHConfig.this.hosts.elementAt(i)));
                        }
                    }
                }
            }
        }
        
        private String find(String key) {
            if (OpenSSHConfig.keymap.get(key) != null) {
                key = OpenSSHConfig.keymap.get(key);
            }
            key = key.toUpperCase();
            String value = null;
            for (int i = 0; i < this._configs.size(); ++i) {
                final Vector v = this._configs.elementAt(i);
                for (int j = 0; j < v.size(); ++j) {
                    final String[] kv = v.elementAt(j);
                    if (kv[0].toUpperCase().equals(key)) {
                        value = kv[1];
                        break;
                    }
                }
                if (value != null) {
                    break;
                }
            }
            return value;
        }
        
        private String[] multiFind(String key) {
            key = key.toUpperCase();
            final Vector value = new Vector();
            for (int i = 0; i < this._configs.size(); ++i) {
                final Vector v = this._configs.elementAt(i);
                for (int j = 0; j < v.size(); ++j) {
                    final String[] kv = v.elementAt(j);
                    if (kv[0].toUpperCase().equals(key)) {
                        final String foo = kv[1];
                        if (foo != null) {
                            value.remove(foo);
                            value.addElement(foo);
                        }
                    }
                }
            }
            final String[] result = new String[value.size()];
            value.toArray(result);
            return result;
        }
        
        public String getHostname() {
            return this.find("Hostname");
        }
        
        public String getUser() {
            return this.find("User");
        }
        
        public int getPort() {
            final String foo = this.find("Port");
            int port = -1;
            try {
                port = Integer.parseInt(foo);
            }
            catch (NumberFormatException ex) {}
            return port;
        }
        
        public String getValue(final String key) {
            if (!key.equals("compression.s2c") && !key.equals("compression.c2s")) {
                return this.find(key);
            }
            final String foo = this.find(key);
            if (foo == null || foo.equals("no")) {
                return "none,zlib@openssh.com,zlib";
            }
            return "zlib@openssh.com,zlib,none";
        }
        
        public String[] getValues(final String key) {
            return this.multiFind(key);
        }
    }
}
