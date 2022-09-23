// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.ccache;

import java.io.InputStream;
import java.util.Collection;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.io.IOException;
import org.apache.kerby.kerberos.kerb.type.ticket.Ticket;
import java.util.Iterator;
import java.io.File;
import org.apache.kerby.kerberos.kerb.type.ticket.KrbTicket;
import org.apache.kerby.kerberos.kerb.type.ticket.SgtTicket;
import org.apache.kerby.kerberos.kerb.type.ticket.TgtTicket;
import java.util.ArrayList;
import org.apache.kerby.kerberos.kerb.type.base.PrincipalName;
import java.util.List;

public class CredentialCache implements KrbCredentialCache
{
    public static final int FCC_FVNO_1 = 1281;
    public static final int FCC_FVNO_2 = 1282;
    public static final int FCC_FVNO_3 = 1283;
    public static final int FCC_FVNO_4 = 1284;
    public static final int FCC_TAG_DELTATIME = 1;
    private final List<Credential> credentials;
    private int version;
    private List<Tag> tags;
    private PrincipalName primaryPrincipal;
    
    public CredentialCache() {
        this.version = 1284;
        this.credentials = new ArrayList<Credential>();
    }
    
    public CredentialCache(final TgtTicket tgt) {
        this();
        this.addCredential(new Credential(tgt));
        this.setPrimaryPrincipal(tgt.getClientPrincipal());
    }
    
    public CredentialCache(final SgtTicket sgt) {
        this();
        this.addCredential(new Credential(sgt, sgt.getClientPrincipal()));
        this.setPrimaryPrincipal(sgt.getClientPrincipal());
    }
    
    public CredentialCache(final Credential credential) {
        this();
        this.addCredential(credential);
        this.setPrimaryPrincipal(credential.getClientName());
    }
    
    public static void main(final String[] args) throws IOException {
        if (args.length != 2) {
            System.err.println("Dump credential cache file");
            System.err.println("Usage: CredentialCache <ccache-file>");
            System.exit(1);
        }
        final String cacheFile = args[1];
        final CredentialCache cc = new CredentialCache();
        cc.load(new File(cacheFile));
        for (final Credential cred : cc.getCredentials()) {
            final Ticket tkt = cred.getTicket();
            System.out.println("Tkt server name: " + tkt.getSname().getName());
            System.out.println("Tkt client name: " + cred.getClientName().getName());
            System.out.println("Tkt encrypt type: " + tkt.getEncryptedEncPart().getEType().getName());
        }
    }
    
    @Override
    public void store(final File ccacheFile) throws IOException {
        try (final OutputStream outputStream = Files.newOutputStream(ccacheFile.toPath(), new OpenOption[0])) {
            this.store(outputStream);
        }
    }
    
    @Override
    public void store(final OutputStream outputStream) throws IOException {
        if (outputStream == null) {
            throw new IllegalArgumentException("Invalid and null output stream");
        }
        final CredCacheOutputStream ccos = new CredCacheOutputStream(outputStream);
        this.doStore(ccos);
        ccos.close();
    }
    
    private void doStore(final CredCacheOutputStream ccos) throws IOException {
        this.version = 1283;
        this.writeVersion(ccos);
        if (this.version == 1284) {
            this.writeTags(ccos);
        }
        ccos.writePrincipal(this.primaryPrincipal, this.version);
        for (final Credential cred : this.credentials) {
            cred.store(ccos, this.version);
        }
    }
    
    @Override
    public PrincipalName getPrimaryPrincipal() {
        return this.primaryPrincipal;
    }
    
    @Override
    public void setPrimaryPrincipal(final PrincipalName principal) {
        this.primaryPrincipal = principal;
    }
    
    @Override
    public int getVersion() {
        return this.version;
    }
    
    @Override
    public void setVersion(final int version) {
        this.version = version;
    }
    
    public List<Tag> getTags() {
        return this.tags;
    }
    
    public void setTags(final List<Tag> tags) {
        this.tags = tags;
    }
    
    @Override
    public List<Credential> getCredentials() {
        return this.credentials;
    }
    
    @Override
    public void addCredential(final Credential credential) {
        if (credential != null) {
            this.credentials.add(credential);
        }
    }
    
    @Override
    public void addCredentials(final List<Credential> credentials) {
        if (credentials != null) {
            this.credentials.addAll(credentials);
        }
    }
    
    @Override
    public void removeCredentials(final List<Credential> credentials) {
        if (credentials != null) {
            for (final Credential cred : credentials) {
                this.removeCredential(cred);
            }
        }
    }
    
    @Override
    public void removeCredential(final Credential credential) {
        if (credential != null) {
            for (final Credential cred : this.credentials) {
                if (cred.equals(credential)) {
                    this.credentials.remove(cred);
                    break;
                }
            }
        }
    }
    
    @Override
    public void load(final File ccacheFile) throws IOException {
        if (!ccacheFile.exists() || !ccacheFile.canRead()) {
            throw new IllegalArgumentException("Invalid ccache file: " + ccacheFile.getAbsolutePath());
        }
        try (final InputStream inputStream = Files.newInputStream(ccacheFile.toPath(), new OpenOption[0])) {
            this.load(inputStream);
        }
    }
    
    @Override
    public void load(final InputStream inputStream) throws IOException {
        if (inputStream == null) {
            throw new IllegalArgumentException("Invalid and null input stream");
        }
        final CredCacheInputStream ccis = new CredCacheInputStream(inputStream);
        this.doLoad(ccis);
        ccis.close();
    }
    
    private void doLoad(final CredCacheInputStream ccis) throws IOException {
        this.version = this.readVersion(ccis);
        if (this.version == 1284) {
            this.tags = this.readTags(ccis);
        }
        this.primaryPrincipal = ccis.readPrincipal(this.version);
        this.credentials.addAll(this.readCredentials(ccis));
    }
    
    private List<Credential> readCredentials(final CredCacheInputStream ccis) throws IOException {
        final List<Credential> results = new ArrayList<Credential>(2);
        while (ccis.available() > 0) {
            final Credential cred = new Credential();
            cred.load(ccis, this.version);
            results.add(cred);
        }
        return results;
    }
    
    private int readVersion(final CredCacheInputStream ccis) throws IOException {
        final int result = ccis.readShort();
        return result;
    }
    
    private List<Tag> readTags(final CredCacheInputStream ccis) throws IOException {
        int len = ccis.readShort();
        final List<Tag> tags = new ArrayList<Tag>();
        while (len > 0) {
            final int tag = ccis.readShort();
            final int tagLen = ccis.readShort();
            switch (tag) {
                case 1: {
                    final int time = ccis.readInt();
                    final int usec = ccis.readInt();
                    tags.add(new Tag(tag, time, usec));
                    break;
                }
                default: {
                    if (ccis.read(new byte[tagLen], 0, tagLen) == -1) {
                        throw new IOException();
                    }
                    break;
                }
            }
            len -= 4 + tagLen;
        }
        return tags;
    }
    
    private void writeVersion(final CredCacheOutputStream ccos) throws IOException {
        ccos.writeShort(this.version);
    }
    
    private void writeTags(final CredCacheOutputStream ccos) throws IOException {
        if (this.tags == null) {
            ccos.writeShort(0);
            return;
        }
        int length = 0;
        for (final Tag tag : this.tags) {
            if (tag.tag != 1) {
                continue;
            }
            length += tag.length;
        }
        ccos.writeShort(length);
        for (final Tag tag : this.tags) {
            if (tag.tag != 1) {
                continue;
            }
            this.writeTag(ccos, tag);
        }
    }
    
    private void writeTag(final CredCacheOutputStream ccos, final Tag tag) throws IOException {
        ccos.writeShort(tag.tag);
        ccos.writeShort(tag.length);
        ccos.writeInt(tag.time);
        ccos.writeInt(tag.usec);
    }
}
