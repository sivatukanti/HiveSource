// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server;

import org.slf4j.LoggerFactory;
import java.util.concurrent.atomic.AtomicLong;
import java.util.Iterator;
import java.util.Set;
import org.apache.jute.OutputArchive;
import java.io.IOException;
import org.apache.jute.Index;
import java.util.ArrayList;
import org.apache.jute.InputArchive;
import org.apache.zookeeper.ZooDefs;
import java.util.HashMap;
import org.apache.zookeeper.data.ACL;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;

public class ReferenceCountedACLCache
{
    private static final Logger LOG;
    final Map<Long, List<ACL>> longKeyMap;
    final Map<List<ACL>, Long> aclKeyMap;
    final Map<Long, AtomicLongWithEquals> referenceCounter;
    private static final long OPEN_UNSAFE_ACL_ID = -1L;
    long aclIndex;
    
    public ReferenceCountedACLCache() {
        this.longKeyMap = new HashMap<Long, List<ACL>>();
        this.aclKeyMap = new HashMap<List<ACL>, Long>();
        this.referenceCounter = new HashMap<Long, AtomicLongWithEquals>();
        this.aclIndex = 0L;
    }
    
    public synchronized Long convertAcls(final List<ACL> acls) {
        if (acls == null) {
            return -1L;
        }
        Long ret = this.aclKeyMap.get(acls);
        if (ret == null) {
            ret = this.incrementIndex();
            this.longKeyMap.put(ret, acls);
            this.aclKeyMap.put(acls, ret);
        }
        this.addUsage(ret);
        return ret;
    }
    
    public synchronized List<ACL> convertLong(final Long longVal) {
        if (longVal == null) {
            return null;
        }
        if (longVal == -1L) {
            return ZooDefs.Ids.OPEN_ACL_UNSAFE;
        }
        final List<ACL> acls = this.longKeyMap.get(longVal);
        if (acls == null) {
            ReferenceCountedACLCache.LOG.error("ERROR: ACL not available for long " + longVal);
            throw new RuntimeException("Failed to fetch acls for " + longVal);
        }
        return acls;
    }
    
    private long incrementIndex() {
        return ++this.aclIndex;
    }
    
    public synchronized void deserialize(final InputArchive ia) throws IOException {
        this.clear();
        for (int i = ia.readInt("map"); i > 0; --i) {
            final Long val = ia.readLong("long");
            if (this.aclIndex < val) {
                this.aclIndex = val;
            }
            final List<ACL> aclList = new ArrayList<ACL>();
            final Index j = ia.startVector("acls");
            if (j == null) {
                throw new RuntimeException("Incorrent format of InputArchive when deserialize DataTree - missing acls");
            }
            while (!j.done()) {
                final ACL acl = new ACL();
                acl.deserialize(ia, "acl");
                aclList.add(acl);
                j.incr();
            }
            this.longKeyMap.put(val, aclList);
            this.aclKeyMap.put(aclList, val);
            this.referenceCounter.put(val, new AtomicLongWithEquals(0L));
        }
    }
    
    public synchronized void serialize(final OutputArchive oa) throws IOException {
        oa.writeInt(this.longKeyMap.size(), "map");
        final Set<Map.Entry<Long, List<ACL>>> set = this.longKeyMap.entrySet();
        for (final Map.Entry<Long, List<ACL>> val : set) {
            oa.writeLong(val.getKey(), "long");
            final List<ACL> aclList = val.getValue();
            oa.startVector(aclList, "acls");
            for (final ACL acl : aclList) {
                acl.serialize(oa, "acl");
            }
            oa.endVector(aclList, "acls");
        }
    }
    
    public int size() {
        return this.aclKeyMap.size();
    }
    
    private void clear() {
        this.aclKeyMap.clear();
        this.longKeyMap.clear();
        this.referenceCounter.clear();
    }
    
    public synchronized void addUsage(final Long acl) {
        if (acl == -1L) {
            return;
        }
        if (!this.longKeyMap.containsKey(acl)) {
            ReferenceCountedACLCache.LOG.info("Ignoring acl " + acl + " as it does not exist in the cache");
            return;
        }
        final AtomicLong count = this.referenceCounter.get(acl);
        if (count == null) {
            this.referenceCounter.put(acl, new AtomicLongWithEquals(1L));
        }
        else {
            count.incrementAndGet();
        }
    }
    
    public synchronized void removeUsage(final Long acl) {
        if (acl == -1L) {
            return;
        }
        if (!this.longKeyMap.containsKey(acl)) {
            ReferenceCountedACLCache.LOG.info("Ignoring acl " + acl + " as it does not exist in the cache");
            return;
        }
        final long newCount = this.referenceCounter.get(acl).decrementAndGet();
        if (newCount <= 0L) {
            this.referenceCounter.remove(acl);
            this.aclKeyMap.remove(this.longKeyMap.get(acl));
            this.longKeyMap.remove(acl);
        }
    }
    
    public synchronized void purgeUnused() {
        final Iterator<Map.Entry<Long, AtomicLongWithEquals>> refCountIter = this.referenceCounter.entrySet().iterator();
        while (refCountIter.hasNext()) {
            final Map.Entry<Long, AtomicLongWithEquals> entry = refCountIter.next();
            if (entry.getValue().get() <= 0L) {
                final Long acl = entry.getKey();
                this.aclKeyMap.remove(this.longKeyMap.get(acl));
                this.longKeyMap.remove(acl);
                refCountIter.remove();
            }
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(ReferenceCountedACLCache.class);
    }
    
    private static class AtomicLongWithEquals extends AtomicLong
    {
        private static final long serialVersionUID = 3355155896813725462L;
        
        public AtomicLongWithEquals(final long i) {
            super(i);
        }
        
        @Override
        public boolean equals(final Object o) {
            return this == o || (o != null && this.getClass() == o.getClass() && this.equals((AtomicLongWithEquals)o));
        }
        
        public boolean equals(final AtomicLongWithEquals that) {
            return this.get() == that.get();
        }
        
        @Override
        public int hashCode() {
            return 31 * Long.valueOf(this.get()).hashCode();
        }
    }
}
