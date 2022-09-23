// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper;

import java.util.Collection;
import java.util.Collections;
import org.apache.zookeeper.data.ACL;
import java.util.ArrayList;
import org.apache.zookeeper.data.Id;
import org.apache.yetus.audience.InterfaceAudience;

@InterfaceAudience.Public
public class ZooDefs
{
    public static final String[] opNames;
    
    static {
        opNames = new String[] { "notification", "create", "delete", "exists", "getData", "setData", "getACL", "setACL", "getChildren", "getChildren2", "getMaxChildren", "setMaxChildren", "ping" };
    }
    
    @InterfaceAudience.Public
    public interface Ids
    {
        public static final Id ANYONE_ID_UNSAFE = new Id("world", "anyone");
        public static final Id AUTH_IDS = new Id("auth", "");
        public static final ArrayList<ACL> OPEN_ACL_UNSAFE = new ArrayList<ACL>(Collections.singletonList(new ACL(31, Ids.ANYONE_ID_UNSAFE)));
        public static final ArrayList<ACL> CREATOR_ALL_ACL = new ArrayList<ACL>(Collections.singletonList(new ACL(31, Ids.AUTH_IDS)));
        public static final ArrayList<ACL> READ_ACL_UNSAFE = new ArrayList<ACL>(Collections.singletonList(new ACL(1, Ids.ANYONE_ID_UNSAFE)));
    }
    
    @InterfaceAudience.Public
    public interface Perms
    {
        public static final int READ = 1;
        public static final int WRITE = 2;
        public static final int CREATE = 4;
        public static final int DELETE = 8;
        public static final int ADMIN = 16;
        public static final int ALL = 31;
    }
    
    @InterfaceAudience.Public
    public interface OpCode
    {
        public static final int notification = 0;
        public static final int create = 1;
        public static final int delete = 2;
        public static final int exists = 3;
        public static final int getData = 4;
        public static final int setData = 5;
        public static final int getACL = 6;
        public static final int setACL = 7;
        public static final int getChildren = 8;
        public static final int sync = 9;
        public static final int ping = 11;
        public static final int getChildren2 = 12;
        public static final int check = 13;
        public static final int multi = 14;
        public static final int auth = 100;
        public static final int setWatches = 101;
        public static final int sasl = 102;
        public static final int createSession = -10;
        public static final int closeSession = -11;
        public static final int error = -1;
    }
}
