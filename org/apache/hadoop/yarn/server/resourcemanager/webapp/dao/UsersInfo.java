// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.resourcemanager.webapp.dao;

import javax.xml.bind.annotation.XmlElement;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity.UserInfo;
import java.util.ArrayList;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class UsersInfo
{
    @XmlElement(name = "user")
    protected ArrayList<UserInfo> usersList;
    
    public UsersInfo() {
        this.usersList = new ArrayList<UserInfo>();
    }
    
    public UsersInfo(final ArrayList<UserInfo> usersList) {
        this.usersList = new ArrayList<UserInfo>();
        this.usersList = usersList;
    }
    
    public ArrayList<UserInfo> getUsersList() {
        return this.usersList;
    }
}
