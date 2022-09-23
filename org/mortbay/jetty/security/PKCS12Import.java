// 
// Decompiled by Procyon v0.5.36
// 

package org.mortbay.jetty.security;

import java.io.IOException;
import java.io.InputStreamReader;
import java.security.cert.X509Certificate;
import java.io.OutputStream;
import java.security.cert.Certificate;
import java.security.Key;
import java.util.Enumeration;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.io.File;

public class PKCS12Import
{
    public static void main(final String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("usage: java PKCS12Import {pkcs12file} [newjksfile]");
            System.exit(1);
        }
        final File fileIn = new File(args[0]);
        File fileOut;
        if (args.length > 1) {
            fileOut = new File(args[1]);
        }
        else {
            fileOut = new File("newstore.jks");
        }
        if (!fileIn.canRead()) {
            System.err.println("Unable to access input keystore: " + fileIn.getPath());
            System.exit(2);
        }
        if (fileOut.exists() && !fileOut.canWrite()) {
            System.err.println("Output file is not writable: " + fileOut.getPath());
            System.exit(2);
        }
        final KeyStore kspkcs12 = KeyStore.getInstance("pkcs12");
        final KeyStore ksjks = KeyStore.getInstance("jks");
        System.out.print("Enter input keystore passphrase: ");
        final char[] inphrase = readPassphrase();
        System.out.print("Enter output keystore passphrase: ");
        final char[] outphrase = readPassphrase();
        kspkcs12.load(new FileInputStream(fileIn), inphrase);
        ksjks.load(fileOut.exists() ? new FileInputStream(fileOut) : null, outphrase);
        final Enumeration eAliases = kspkcs12.aliases();
        int n = 0;
        while (eAliases.hasMoreElements()) {
            final String strAlias = eAliases.nextElement();
            System.err.println("Alias " + n++ + ": " + strAlias);
            if (kspkcs12.isKeyEntry(strAlias)) {
                System.err.println("Adding key for alias " + strAlias);
                final Key key = kspkcs12.getKey(strAlias, inphrase);
                final Certificate[] chain = kspkcs12.getCertificateChain(strAlias);
                ksjks.setKeyEntry(strAlias, key, outphrase, chain);
            }
        }
        final OutputStream out = new FileOutputStream(fileOut);
        ksjks.store(out, outphrase);
        out.close();
    }
    
    static void dumpChain(final Certificate[] chain) {
        for (int i = 0; i < chain.length; ++i) {
            final Certificate cert = chain[i];
            if (cert instanceof X509Certificate) {
                final X509Certificate x509 = (X509Certificate)chain[i];
                System.err.println("subject: " + x509.getSubjectDN());
                System.err.println("issuer: " + x509.getIssuerDN());
            }
        }
    }
    
    static char[] readPassphrase() throws IOException {
        final InputStreamReader in = new InputStreamReader(System.in);
        final char[] cbuf = new char[256];
        int i = 0;
    Label_0076:
        while (i < cbuf.length) {
            final char c = (char)in.read();
            switch (c) {
                case '\r': {
                    break Label_0076;
                }
                case '\n': {
                    break Label_0076;
                }
                default: {
                    cbuf[i++] = c;
                    continue;
                }
            }
        }
        final char[] phrase = new char[i];
        System.arraycopy(cbuf, 0, phrase, 0, i);
        return phrase;
    }
}
