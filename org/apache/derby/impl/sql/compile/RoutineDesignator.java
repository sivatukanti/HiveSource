// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.impl.sql.compile;

import org.apache.derby.impl.sql.execute.RoutinePrivilegeInfo;
import org.apache.derby.impl.sql.execute.PrivilegeInfo;
import org.apache.derby.iapi.sql.dictionary.AliasDescriptor;
import java.util.List;

class RoutineDesignator
{
    boolean isSpecific;
    TableName name;
    boolean isFunction;
    List paramTypeList;
    AliasDescriptor aliasDescriptor;
    
    RoutineDesignator(final boolean isSpecific, final TableName name, final boolean isFunction, final List paramTypeList) {
        this.isSpecific = isSpecific;
        this.name = name;
        this.isFunction = isFunction;
        this.paramTypeList = paramTypeList;
    }
    
    void setAliasDescriptor(final AliasDescriptor aliasDescriptor) {
        this.aliasDescriptor = aliasDescriptor;
    }
    
    PrivilegeInfo makePrivilegeInfo() {
        return new RoutinePrivilegeInfo(this.aliasDescriptor);
    }
}
