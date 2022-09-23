// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.store.rdbms.identifier;

import org.datanucleus.exceptions.NucleusException;
import org.datanucleus.metadata.AbstractMemberMetaData;
import java.util.Map;
import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.store.rdbms.adapter.DatastoreAdapter;

public class DN2IdentifierFactory extends DNIdentifierFactory
{
    public DN2IdentifierFactory(final DatastoreAdapter dba, final ClassLoaderResolver clr, final Map props) {
        super(dba, clr, props);
    }
    
    @Override
    public DatastoreIdentifier newJoinTableFieldIdentifier(final AbstractMemberMetaData ownerFmd, final AbstractMemberMetaData relatedFmd, final DatastoreIdentifier destinationId, final boolean embedded, final int fieldRole) {
        if (destinationId != null) {
            return this.newColumnIdentifier(destinationId.getIdentifierName(), embedded, fieldRole);
        }
        String baseName = null;
        if (fieldRole == 3) {
            baseName = "ELEMENT";
        }
        else if (fieldRole == 4) {
            baseName = "ELEMENT";
        }
        else if (fieldRole == 5) {
            baseName = "KEY";
        }
        else if (fieldRole == 6) {
            baseName = "VALUE";
        }
        else {
            baseName = "UNKNOWN";
        }
        return this.newColumnIdentifier(baseName);
    }
    
    public DatastoreIdentifier newForeignKeyFieldIdentifier(final AbstractMemberMetaData ownerFmd, final DatastoreIdentifier destinationId, final boolean embedded, final int fieldRole) {
        if (fieldRole == 1) {
            return this.newColumnIdentifier(ownerFmd.getName() + "." + destinationId.getIdentifierName(), embedded, fieldRole);
        }
        if (fieldRole == 7) {
            return this.newColumnIdentifier(ownerFmd.getName(), embedded, fieldRole);
        }
        throw new NucleusException("Column role " + fieldRole + " not supported by this method").setFatal();
    }
    
    @Override
    public DatastoreIdentifier newVersionFieldIdentifier() {
        final String name = "VERSION";
        DatastoreIdentifier identifier = this.columns.get(name);
        if (identifier == null) {
            identifier = new ColumnIdentifier(this, name);
            this.columns.put(name, identifier);
        }
        return identifier;
    }
    
    @Override
    public DatastoreIdentifier newIndexFieldIdentifier(final AbstractMemberMetaData mmd) {
        final String name = "IDX";
        DatastoreIdentifier identifier = this.columns.get(name);
        if (identifier == null) {
            identifier = new ColumnIdentifier(this, name);
            this.columns.put(name, identifier);
        }
        return identifier;
    }
    
    @Override
    public DatastoreIdentifier newAdapterIndexFieldIdentifier() {
        return this.newIndexFieldIdentifier(null);
    }
    
    @Override
    public String generateIdentifierNameForJavaName(final String javaName) {
        if (javaName == null) {
            return null;
        }
        final StringBuffer s = new StringBuffer();
        for (int i = 0; i < javaName.length(); ++i) {
            final char c = javaName.charAt(i);
            if (c >= 'A' && c <= 'Z' && this.identifierCase != IdentifierCase.MIXED_CASE && this.identifierCase != IdentifierCase.MIXED_CASE_QUOTED) {
                s.append(c);
            }
            else if (c >= 'A' && c <= 'Z' && (this.identifierCase == IdentifierCase.MIXED_CASE || this.identifierCase == IdentifierCase.MIXED_CASE_QUOTED)) {
                s.append(c);
            }
            else if (c >= 'a' && c <= 'z' && (this.identifierCase == IdentifierCase.MIXED_CASE || this.identifierCase == IdentifierCase.MIXED_CASE_QUOTED)) {
                s.append(c);
            }
            else if (c >= 'a' && c <= 'z' && this.identifierCase != IdentifierCase.MIXED_CASE && this.identifierCase != IdentifierCase.MIXED_CASE_QUOTED) {
                s.append((char)(c - ' '));
            }
            else if ((c >= '0' && c <= '9') || c == '_') {
                s.append(c);
            }
            else if (c == '.') {
                s.append(this.wordSeparator);
            }
            else {
                final String cval = "000" + Integer.toHexString(c);
                s.append(cval.substring(cval.length() - ((c > '\u00ff') ? 4 : 2)));
            }
        }
        while (s.length() > 0 && s.charAt(0) == '_') {
            s.deleteCharAt(0);
        }
        if (s.length() == 0) {
            throw new IllegalArgumentException("Illegal Java identifier: " + javaName);
        }
        return s.toString();
    }
}
