// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.metastore;

import org.apache.hadoop.hive.metastore.api.LockType;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import org.apache.hadoop.hive.metastore.api.LockComponent;
import java.net.UnknownHostException;
import java.net.InetAddress;
import org.apache.hadoop.hive.metastore.api.LockRequest;

public class LockRequestBuilder
{
    private LockRequest req;
    private LockTrie trie;
    private boolean userSet;
    
    public LockRequestBuilder() {
        this.req = new LockRequest();
        this.trie = new LockTrie();
        this.userSet = false;
    }
    
    public LockRequest build() {
        if (!this.userSet) {
            throw new RuntimeException("Cannot build a lock without giving a user");
        }
        this.trie.addLocksToRequest(this.req);
        try {
            this.req.setHostname(InetAddress.getLocalHost().getHostName());
        }
        catch (UnknownHostException e) {
            throw new RuntimeException("Unable to determine our local host!");
        }
        return this.req;
    }
    
    public LockRequestBuilder setTransactionId(final long txnid) {
        this.req.setTxnid(txnid);
        return this;
    }
    
    public LockRequestBuilder setUser(String user) {
        if (user == null) {
            user = "unknown";
        }
        this.req.setUser(user);
        this.userSet = true;
        return this;
    }
    
    public LockRequestBuilder addLockComponent(final LockComponent component) {
        this.trie.add(component);
        return this;
    }
    
    private static class LockTrie
    {
        Map<String, TableTrie> trie;
        
        LockTrie() {
            this.trie = new HashMap<String, TableTrie>();
        }
        
        public void add(final LockComponent comp) {
            TableTrie tabs = this.trie.get(comp.getDbname());
            if (tabs == null) {
                tabs = new TableTrie();
                this.trie.put(comp.getDbname(), tabs);
            }
            this.setTable(comp, tabs);
        }
        
        public void addLocksToRequest(final LockRequest request) {
            for (final TableTrie tab : this.trie.values()) {
                for (final PartTrie part : ((HashMap<K, PartTrie>)tab).values()) {
                    for (final LockComponent lock : ((HashMap<K, LockComponent>)part).values()) {
                        request.addToComponent(lock);
                    }
                }
            }
        }
        
        private void setTable(final LockComponent comp, final TableTrie tabs) {
            PartTrie parts = ((HashMap<K, PartTrie>)tabs).get(comp.getTablename());
            if (parts == null) {
                parts = new PartTrie();
                tabs.put(comp.getTablename(), parts);
            }
            this.setPart(comp, parts);
        }
        
        private void setPart(final LockComponent comp, final PartTrie parts) {
            final LockComponent existing = ((HashMap<K, LockComponent>)parts).get(comp.getPartitionname());
            if (existing == null) {
                parts.put(comp.getPartitionname(), comp);
            }
            else if (existing.getType() != LockType.EXCLUSIVE && (comp.getType() == LockType.EXCLUSIVE || comp.getType() == LockType.SHARED_WRITE)) {
                parts.put(comp.getPartitionname(), comp);
            }
        }
        
        private static class TableTrie extends HashMap<String, PartTrie>
        {
        }
        
        private static class PartTrie extends HashMap<String, LockComponent>
        {
        }
    }
}
