// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.derby.iapi.sql.dictionary;

import org.apache.derby.catalog.DependableFinder;
import org.apache.derby.iapi.error.StandardException;
import org.apache.derby.iapi.sql.conn.LanguageConnectionContext;
import org.apache.derby.catalog.UUID;
import org.apache.derby.iapi.sql.depend.Provider;

public class RoleGrantDescriptor extends TupleDescriptor implements Provider
{
    private final UUID uuid;
    private final String roleName;
    private final String grantee;
    private final String grantor;
    private boolean withAdminOption;
    private final boolean isDef;
    
    public RoleGrantDescriptor(final DataDictionary dataDictionary, final UUID uuid, final String roleName, final String grantee, final String grantor, final boolean withAdminOption, final boolean isDef) {
        super(dataDictionary);
        this.uuid = uuid;
        this.roleName = roleName;
        this.grantee = grantee;
        this.grantor = grantor;
        this.withAdminOption = withAdminOption;
        this.isDef = isDef;
    }
    
    public UUID getUUID() {
        return this.uuid;
    }
    
    public String getGrantee() {
        return this.grantee;
    }
    
    public String getGrantor() {
        return this.grantor;
    }
    
    public boolean isDef() {
        return this.isDef;
    }
    
    public String getRoleName() {
        return this.roleName;
    }
    
    public boolean isWithAdminOption() {
        return this.withAdminOption;
    }
    
    public void setWithAdminOption(final boolean withAdminOption) {
        this.withAdminOption = withAdminOption;
    }
    
    public String toString() {
        return "";
    }
    
    public String getDescriptorType() {
        return "Role";
    }
    
    public String getDescriptorName() {
        return this.roleName + " " + this.grantor + " " + this.grantee;
    }
    
    public void drop(final LanguageConnectionContext languageConnectionContext) throws StandardException {
        this.getDataDictionary().dropRoleGrant(this.roleName, this.grantee, this.grantor, languageConnectionContext.getTransactionExecute());
    }
    
    public UUID getObjectID() {
        return this.uuid;
    }
    
    public boolean isPersistent() {
        return true;
    }
    
    public String getObjectName() {
        return (this.isDef ? "CREATE ROLE: " : "GRANT ROLE: ") + this.roleName + " GRANT TO: " + this.grantee + " GRANTED BY: " + this.grantor + (this.withAdminOption ? " WITH ADMIN OPTION" : "");
    }
    
    public String getClassType() {
        return "RoleGrant";
    }
    
    public DependableFinder getDependableFinder() {
        return this.getDependableFinder(471);
    }
}
