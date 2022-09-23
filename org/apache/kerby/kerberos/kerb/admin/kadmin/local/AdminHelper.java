// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.kerby.kerberos.kerb.admin.kadmin.local;

import java.util.regex.PatternSyntaxException;
import java.util.regex.Pattern;
import java.util.Date;
import org.apache.kerby.KOption;
import org.apache.kerby.kerberos.kerb.admin.kadmin.KadminOption;
import org.apache.kerby.KOptions;
import org.apache.kerby.kerberos.kerb.type.base.PrincipalName;
import org.apache.kerby.kerberos.kerb.keytab.KeytabEntry;
import org.apache.kerby.kerberos.kerb.type.base.EncryptionKey;
import org.apache.kerby.kerberos.kerb.type.base.EncryptionType;
import org.apache.kerby.kerberos.kerb.type.KerberosTime;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import org.apache.kerby.kerberos.kerb.KrbException;
import org.apache.kerby.kerberos.kerb.keytab.Keytab;
import org.apache.kerby.kerberos.kerb.identity.KrbIdentity;
import java.io.File;

public final class AdminHelper
{
    private AdminHelper() {
    }
    
    public static void exportKeytab(final File keytabFile, final KrbIdentity identity) throws KrbException {
        final Keytab keytab = createOrLoadKeytab(keytabFile);
        exportToKeytab(keytab, identity);
        storeKeytab(keytab, keytabFile);
    }
    
    public static void exportKeytab(final File keytabFile, final List<KrbIdentity> identities) throws KrbException {
        final Keytab keytab = createOrLoadKeytab(keytabFile);
        for (final KrbIdentity identity : identities) {
            exportToKeytab(keytab, identity);
        }
        storeKeytab(keytab, keytabFile);
    }
    
    public static Keytab loadKeytab(final File keytabFile) throws KrbException {
        Keytab keytab;
        try {
            keytab = Keytab.loadKeytab(keytabFile);
        }
        catch (IOException e) {
            throw new KrbException("Failed to load keytab", e);
        }
        return keytab;
    }
    
    public static Keytab createOrLoadKeytab(final File keytabFile) throws KrbException {
        Keytab keytab;
        try {
            if (!keytabFile.exists()) {
                if (!keytabFile.createNewFile()) {
                    throw new KrbException("Failed to create keytab file " + keytabFile.getAbsolutePath());
                }
                keytab = new Keytab();
            }
            else {
                keytab = Keytab.loadKeytab(keytabFile);
            }
        }
        catch (IOException e) {
            throw new KrbException("Failed to load or create keytab " + keytabFile.getAbsolutePath(), e);
        }
        return keytab;
    }
    
    public static void exportToKeytab(final Keytab keytab, final KrbIdentity identity) throws KrbException {
        final PrincipalName principal = identity.getPrincipal();
        final KerberosTime timestamp = KerberosTime.now();
        for (final EncryptionType encType : identity.getKeys().keySet()) {
            final EncryptionKey ekey = identity.getKeys().get(encType);
            final int keyVersion = ekey.getKvno();
            keytab.addEntry(new KeytabEntry(principal, timestamp, keyVersion, ekey));
        }
    }
    
    public static void storeKeytab(final Keytab keytab, final File keytabFile) throws KrbException {
        try {
            keytab.store(keytabFile);
        }
        catch (IOException e) {
            throw new KrbException("Failed to store keytab", e);
        }
    }
    
    public static void removeKeytabEntriesOf(final File keytabFile, final String principalName) throws KrbException {
        final Keytab keytab = loadKeytab(keytabFile);
        keytab.removeKeytabEntries(new PrincipalName(principalName));
        storeKeytab(keytab, keytabFile);
    }
    
    static void removeKeytabEntriesOf(final File keytabFile, final String principalName, final int kvno) throws KrbException {
        final Keytab keytab = loadKeytab(keytabFile);
        keytab.removeKeytabEntries(new PrincipalName(principalName), kvno);
        storeKeytab(keytab, keytabFile);
    }
    
    public static void removeOldKeytabEntriesOf(final File keytabFile, final String principalName) throws KrbException {
        final Keytab keytab = loadKeytab(keytabFile);
        final List<KeytabEntry> entries = keytab.getKeytabEntries(new PrincipalName(principalName));
        int maxKvno = 0;
        for (final KeytabEntry entry : entries) {
            if (maxKvno < entry.getKvno()) {
                maxKvno = entry.getKvno();
            }
        }
        for (final KeytabEntry entry : entries) {
            if (entry.getKvno() < maxKvno) {
                keytab.removeKeytabEntry(entry);
            }
        }
        storeKeytab(keytab, keytabFile);
    }
    
    public static KrbIdentity createIdentity(final String principal, final KOptions kOptions) throws KrbException {
        final KrbIdentity kid = new KrbIdentity(principal);
        kid.setCreatedTime(KerberosTime.now());
        if (kOptions.contains(KadminOption.EXPIRE)) {
            final Date date = kOptions.getDateOption(KadminOption.EXPIRE);
            kid.setExpireTime(new KerberosTime(date.getTime()));
        }
        else {
            kid.setExpireTime(new KerberosTime(253402300799900L));
        }
        if (kOptions.contains(KadminOption.KVNO)) {
            kid.setKeyVersion(kOptions.getIntegerOption(KadminOption.KVNO));
        }
        else {
            kid.setKeyVersion(1);
        }
        kid.setDisabled(false);
        kid.setLocked(false);
        return kid;
    }
    
    public static void updateIdentity(final KrbIdentity identity, final KOptions kOptions) {
        if (kOptions.contains(KadminOption.EXPIRE)) {
            final Date date = kOptions.getDateOption(KadminOption.EXPIRE);
            identity.setExpireTime(new KerberosTime(date.getTime()));
        }
        if (kOptions.contains(KadminOption.DISABLED)) {
            identity.setDisabled(kOptions.getBooleanOption(KadminOption.DISABLED, false));
        }
        if (kOptions.contains(KadminOption.LOCKED)) {
            identity.setLocked(kOptions.getBooleanOption(KadminOption.LOCKED, false));
        }
    }
    
    public static Pattern getPatternFromGlobPatternString(final String globString) throws KrbException {
        if (globString == null || globString.equals("")) {
            return null;
        }
        if (!Pattern.matches("^[0-9A-Za-z._/@*?\\[\\]\\-]+$", globString)) {
            throw new KrbException("Glob pattern string contains invalid character");
        }
        String patternString = globString;
        patternString = patternString.replaceAll("\\.", "\\\\.");
        patternString = patternString.replaceAll("\\?", ".");
        patternString = patternString.replaceAll("\\*", ".*");
        patternString = "^" + patternString + "$";
        Pattern pt;
        try {
            pt = Pattern.compile(patternString);
        }
        catch (PatternSyntaxException e) {
            throw new KrbException("Invalid glob pattern string");
        }
        return pt;
    }
}
