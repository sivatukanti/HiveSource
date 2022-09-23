// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.keytab;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import org.apache.kerby.kerberos.kerb.type.base.EncryptionKey;
import org.apache.kerby.kerberos.kerb.type.base.EncryptionType;
import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import java.io.InputStream;
import java.io.IOException;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import org.apache.kerby.kerberos.kerb.type.base.PrincipalName;
import java.util.Map;

public final class Keytab implements KrbKeytab
{
    public static final int V501 = 1281;
    public static final int V502 = 1282;
    private int version;
    private Map<PrincipalName, List<KeytabEntry>> principalEntries;
    
    public Keytab() {
        this.version = 1282;
        this.principalEntries = new HashMap<PrincipalName, List<KeytabEntry>>();
    }
    
    public static Keytab loadKeytab(final File keytabFile) throws IOException {
        final Keytab keytab = new Keytab();
        keytab.load(keytabFile);
        return keytab;
    }
    
    public static Keytab loadKeytab(final InputStream inputStream) throws IOException {
        final Keytab keytab = new Keytab();
        keytab.load(inputStream);
        return keytab;
    }
    
    @Override
    public List<PrincipalName> getPrincipals() {
        return new ArrayList<PrincipalName>(this.principalEntries.keySet());
    }
    
    @Override
    public void addKeytabEntries(final List<KeytabEntry> entries) {
        for (final KeytabEntry entry : entries) {
            this.addEntry(entry);
        }
    }
    
    @Override
    public void removeKeytabEntries(final PrincipalName principal) {
        this.principalEntries.remove(principal);
    }
    
    @Override
    public void removeKeytabEntries(final PrincipalName principal, final int kvno) {
        final List<KeytabEntry> entries = this.getKeytabEntries(principal);
        for (final KeytabEntry entry : entries) {
            if (entry.getKvno() == kvno) {
                this.removeKeytabEntry(entry);
            }
        }
    }
    
    @Override
    public void removeKeytabEntry(final KeytabEntry entry) {
        final PrincipalName principal = entry.getPrincipal();
        final List<KeytabEntry> entries = this.principalEntries.get(principal);
        if (entries != null) {
            final Iterator<KeytabEntry> iter = entries.iterator();
            while (iter.hasNext()) {
                final KeytabEntry tmp = iter.next();
                if (entry.equals(tmp)) {
                    iter.remove();
                    break;
                }
            }
        }
    }
    
    @Override
    public List<KeytabEntry> getKeytabEntries(final PrincipalName principal) {
        final List<KeytabEntry> results = new ArrayList<KeytabEntry>();
        final List<KeytabEntry> internal = this.principalEntries.get(principal);
        if (internal == null) {
            return results;
        }
        for (final KeytabEntry entry : internal) {
            results.add(entry);
        }
        return results;
    }
    
    @Override
    public EncryptionKey getKey(final PrincipalName principal, final EncryptionType keyType) {
        final List<KeytabEntry> entries = this.getKeytabEntries(principal);
        for (final KeytabEntry ke : entries) {
            if (ke.getKey().getKeyType() == keyType) {
                return ke.getKey();
            }
        }
        return null;
    }
    
    @Override
    public void load(final File keytabFile) throws IOException {
        if (!keytabFile.exists() || !keytabFile.canRead()) {
            throw new IllegalArgumentException("Invalid keytab file: " + keytabFile.getAbsolutePath());
        }
        try (final InputStream is = Files.newInputStream(keytabFile.toPath(), new OpenOption[0])) {
            this.load(is);
        }
    }
    
    @Override
    public void load(final InputStream inputStream) throws IOException {
        if (inputStream == null) {
            throw new IllegalArgumentException("Invalid and null input stream");
        }
        final KeytabInputStream kis = new KeytabInputStream(inputStream);
        this.doLoad(kis);
    }
    
    private void doLoad(final KeytabInputStream kis) throws IOException {
        this.version = this.readVersion(kis);
        final List<KeytabEntry> entries = this.readEntries(kis);
        this.addKeytabEntries(entries);
    }
    
    @Override
    public void addEntry(final KeytabEntry entry) {
        final PrincipalName principal = entry.getPrincipal();
        List<KeytabEntry> entries = this.principalEntries.get(principal);
        if (entries == null) {
            entries = new ArrayList<KeytabEntry>();
            this.principalEntries.put(principal, entries);
        }
        entries.add(entry);
    }
    
    private int readVersion(final KeytabInputStream kis) throws IOException {
        return kis.readShort();
    }
    
    private List<KeytabEntry> readEntries(final KeytabInputStream kis) throws IOException {
        final List<KeytabEntry> entries = new ArrayList<KeytabEntry>();
        for (int bytesLeft = kis.available(); bytesLeft > 0; bytesLeft = kis.available()) {
            final int entrySize = kis.readInt();
            if (kis.available() < entrySize) {
                throw new IOException("Bad input stream with less data than expected: " + entrySize);
            }
            final KeytabEntry entry = this.readEntry(kis, entrySize);
            entries.add(entry);
            final int bytesReadForEntry = bytesLeft - kis.available();
            if (bytesReadForEntry != entrySize) {
                kis.skipBytes(entrySize - bytesReadForEntry);
            }
        }
        return entries;
    }
    
    private KeytabEntry readEntry(final KeytabInputStream kis, final int entrySize) throws IOException {
        final KeytabEntry entry = new KeytabEntry();
        entry.load(kis, this.version, entrySize);
        return entry;
    }
    
    @Override
    public void store(final File keytabFile) throws IOException {
        try (final OutputStream outputStream = Files.newOutputStream(keytabFile.toPath(), new OpenOption[0])) {
            this.store(outputStream);
        }
    }
    
    @Override
    public void store(final OutputStream outputStream) throws IOException {
        if (outputStream == null) {
            throw new IllegalArgumentException("Invalid and null output stream");
        }
        final KeytabOutputStream kos = new KeytabOutputStream(outputStream);
        this.writeVersion(kos);
        this.writeEntries(kos);
    }
    
    private void writeVersion(final KeytabOutputStream kos) throws IOException {
        final byte[] bytes = { 5, (byte)((this.version == 1282) ? 2 : 1) };
        kos.write(bytes);
    }
    
    private void writeEntries(final KeytabOutputStream kos) throws IOException {
        for (final Map.Entry<PrincipalName, List<KeytabEntry>> entryList : this.principalEntries.entrySet()) {
            for (final KeytabEntry entry : entryList.getValue()) {
                entry.store(kos);
            }
        }
    }
}
