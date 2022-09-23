// 
// Decompiled by Procyon v0.5.36
// 

package org.datanucleus.metadata;

import org.datanucleus.util.Localiser;

public class InvalidMemberMetaDataException extends InvalidMetaDataException
{
    String className;
    String memberName;
    
    public InvalidMemberMetaDataException(final Localiser localiser, final String key, final Object... params) {
        super(localiser, key, params);
        this.className = (String)params[0];
        this.memberName = (String)params[1];
    }
    
    public InvalidMemberMetaDataException(final Localiser localiser, final String key, final String className, final String memberName) {
        super(localiser, key, className, memberName);
        this.className = className;
        this.memberName = memberName;
    }
    
    public InvalidMemberMetaDataException(final Localiser localiser, final String key, final String className, final String memberName, final Object param1) {
        super(localiser, key, className, memberName, param1);
        this.className = className;
        this.memberName = memberName;
    }
    
    public InvalidMemberMetaDataException(final Localiser localiser, final String key, final String className, final String memberName, final Object param1, final Object param2) {
        super(localiser, key, className, memberName, param1, param2);
        this.className = className;
        this.memberName = memberName;
    }
    
    public InvalidMemberMetaDataException(final Localiser localiser, final String key, final String className, final String memberName, final Object param1, final Object param2, final Object param3) {
        super(localiser, key, className, memberName, param1, param2, param3);
        this.className = className;
        this.memberName = memberName;
    }
    
    public String getClassName() {
        return this.className;
    }
    
    public String getMemberName() {
        return this.memberName;
    }
}
