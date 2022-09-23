// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql.dictionary;

import java.util.Arrays;
import java.sql.Timestamp;

public final class UserDescriptor extends TupleDescriptor
{
    private String _userName;
    private String _hashingScheme;
    private char[] _password;
    private Timestamp _lastModified;
    
    public UserDescriptor(final DataDictionary dataDictionary, final String userName, final String hashingScheme, final char[] array, final Timestamp lastModified) {
        super(dataDictionary);
        this._userName = userName;
        this._hashingScheme = hashingScheme;
        if (array == null) {
            this._password = null;
        }
        else {
            System.arraycopy(array, 0, this._password = new char[array.length], 0, array.length);
        }
        this._lastModified = lastModified;
    }
    
    public String getUserName() {
        return this._userName;
    }
    
    public String getHashingScheme() {
        return this._hashingScheme;
    }
    
    public Timestamp getLastModified() {
        return this._lastModified;
    }
    
    public char[] getAndZeroPassword() {
        final int length = this._password.length;
        final char[] array = new char[length];
        System.arraycopy(this._password, 0, array, 0, length);
        Arrays.fill(this._password, '\0');
        return array;
    }
    
    public String getDescriptorType() {
        return "User";
    }
    
    public String getDescriptorName() {
        return this._userName;
    }
}
