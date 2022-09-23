// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server.quorum.flexible;

import org.slf4j.LoggerFactory;
import java.util.Set;
import java.util.Iterator;
import java.util.Map;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;
import java.util.Properties;
import org.apache.zookeeper.server.quorum.QuorumPeerConfig;
import java.util.HashMap;
import org.slf4j.Logger;

public class QuorumHierarchical implements QuorumVerifier
{
    private static final Logger LOG;
    HashMap<Long, Long> serverWeight;
    HashMap<Long, Long> serverGroup;
    HashMap<Long, Long> groupWeight;
    int numGroups;
    
    public QuorumHierarchical(final String filename) throws QuorumPeerConfig.ConfigException {
        this.serverWeight = new HashMap<Long, Long>();
        this.serverGroup = new HashMap<Long, Long>();
        this.groupWeight = new HashMap<Long, Long>();
        this.numGroups = 0;
        this.readConfigFile(filename);
    }
    
    public QuorumHierarchical(final Properties qp) throws QuorumPeerConfig.ConfigException {
        this.serverWeight = new HashMap<Long, Long>();
        this.serverGroup = new HashMap<Long, Long>();
        this.groupWeight = new HashMap<Long, Long>();
        this.numGroups = 0;
        this.parse(qp);
        QuorumHierarchical.LOG.info(this.serverWeight.size() + ", " + this.serverGroup.size() + ", " + this.groupWeight.size());
    }
    
    public QuorumHierarchical(final int numGroups, final HashMap<Long, Long> serverWeight, final HashMap<Long, Long> serverGroup) {
        this.serverWeight = serverWeight;
        this.serverGroup = serverGroup;
        this.groupWeight = new HashMap<Long, Long>();
        this.numGroups = numGroups;
        this.computeGroupWeight();
    }
    
    @Override
    public long getWeight(final long id) {
        return this.serverWeight.get(id);
    }
    
    private void readConfigFile(final String filename) throws QuorumPeerConfig.ConfigException {
        final File configFile = new File(filename);
        QuorumHierarchical.LOG.info("Reading configuration from: " + configFile);
        try {
            if (!configFile.exists()) {
                throw new IllegalArgumentException(configFile.toString() + " file is missing");
            }
            final Properties cfg = new Properties();
            final FileInputStream in = new FileInputStream(configFile);
            try {
                cfg.load(in);
            }
            finally {
                in.close();
            }
            this.parse(cfg);
        }
        catch (IOException e) {
            throw new QuorumPeerConfig.ConfigException("Error processing " + filename, e);
        }
        catch (IllegalArgumentException e2) {
            throw new QuorumPeerConfig.ConfigException("Error processing " + filename, e2);
        }
    }
    
    private void parse(final Properties quorumProp) {
        for (final Map.Entry<Object, Object> entry : quorumProp.entrySet()) {
            final String key = entry.getKey().toString();
            final String value = entry.getValue().toString();
            if (key.startsWith("group")) {
                final int dot = key.indexOf(46);
                final long gid = Long.parseLong(key.substring(dot + 1));
                ++this.numGroups;
                final String[] split;
                final String[] parts = split = value.split(":");
                for (final String s : split) {
                    final long sid = Long.parseLong(s);
                    this.serverGroup.put(sid, gid);
                }
            }
            else {
                if (!key.startsWith("weight")) {
                    continue;
                }
                final int dot = key.indexOf(46);
                final long sid2 = Long.parseLong(key.substring(dot + 1));
                this.serverWeight.put(sid2, Long.parseLong(value));
            }
        }
        this.computeGroupWeight();
    }
    
    private void computeGroupWeight() {
        for (final Map.Entry<Long, Long> entry : this.serverGroup.entrySet()) {
            final Long sid = entry.getKey();
            final Long gid = entry.getValue();
            if (!this.groupWeight.containsKey(gid)) {
                this.groupWeight.put(gid, this.serverWeight.get(sid));
            }
            else {
                final long totalWeight = this.serverWeight.get(sid) + this.groupWeight.get(gid);
                this.groupWeight.put(gid, totalWeight);
            }
        }
        for (final long weight : this.groupWeight.values()) {
            QuorumHierarchical.LOG.debug("Group weight: " + weight);
            if (weight == 0L) {
                --this.numGroups;
                QuorumHierarchical.LOG.debug("One zero-weight group: 1, " + this.numGroups);
            }
        }
    }
    
    @Override
    public boolean containsQuorum(final Set<Long> set) {
        final HashMap<Long, Long> expansion = new HashMap<Long, Long>();
        if (set.size() == 0) {
            return false;
        }
        QuorumHierarchical.LOG.debug("Set size: " + set.size());
        for (final long sid : set) {
            final Long gid = this.serverGroup.get(sid);
            if (!expansion.containsKey(gid)) {
                expansion.put(gid, this.serverWeight.get(sid));
            }
            else {
                final long totalWeight = this.serverWeight.get(sid) + expansion.get(gid);
                expansion.put(gid, totalWeight);
            }
        }
        int majGroupCounter = 0;
        for (final Map.Entry<Long, Long> entry : expansion.entrySet()) {
            final Long gid = entry.getKey();
            QuorumHierarchical.LOG.debug("Group info: " + entry.getValue() + ", " + gid + ", " + this.groupWeight.get(gid));
            if (entry.getValue() > this.groupWeight.get(gid) / 2L) {
                ++majGroupCounter;
            }
        }
        QuorumHierarchical.LOG.debug("Majority group counter: " + majGroupCounter + ", " + this.numGroups);
        if (majGroupCounter > this.numGroups / 2) {
            QuorumHierarchical.LOG.debug("Positive set size: " + set.size());
            return true;
        }
        QuorumHierarchical.LOG.debug("Negative set size: " + set.size());
        return false;
    }
    
    static {
        LOG = LoggerFactory.getLogger(QuorumHierarchical.class);
    }
}
