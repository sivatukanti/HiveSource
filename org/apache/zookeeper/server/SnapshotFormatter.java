// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper.server;

import java.util.Date;
import org.apache.zookeeper.data.StatPersisted;
import java.util.Iterator;
import java.util.Set;
import java.io.IOException;
import java.util.Map;
import org.apache.jute.InputArchive;
import java.util.HashMap;
import java.io.File;
import org.apache.zookeeper.server.persistence.FileSnap;
import org.apache.jute.BinaryInputArchive;
import java.util.zip.Checksum;
import java.util.zip.CheckedInputStream;
import java.util.zip.Adler32;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import org.apache.yetus.audience.InterfaceAudience;

@InterfaceAudience.Public
public class SnapshotFormatter
{
    public static void main(final String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("USAGE: SnapshotFormatter snapshot_file");
            System.exit(2);
        }
        new SnapshotFormatter().run(args[0]);
    }
    
    public void run(final String snapshotFileName) throws IOException {
        final InputStream is = new CheckedInputStream(new BufferedInputStream(new FileInputStream(snapshotFileName)), new Adler32());
        final InputArchive ia = BinaryInputArchive.getArchive(is);
        final FileSnap fileSnap = new FileSnap(null);
        final DataTree dataTree = new DataTree();
        final Map<Long, Integer> sessions = new HashMap<Long, Integer>();
        fileSnap.deserialize(dataTree, sessions, ia);
        this.printDetails(dataTree, sessions);
    }
    
    private void printDetails(final DataTree dataTree, final Map<Long, Integer> sessions) {
        this.printZnodeDetails(dataTree);
        this.printSessionDetails(dataTree, sessions);
    }
    
    private void printZnodeDetails(final DataTree dataTree) {
        System.out.println(String.format("ZNode Details (count=%d):", dataTree.getNodeCount()));
        this.printZnode(dataTree, "/");
        System.out.println("----");
    }
    
    private void printZnode(final DataTree dataTree, final String name) {
        System.out.println("----");
        final DataNode n = dataTree.getNode(name);
        final Set<String> children;
        synchronized (n) {
            System.out.println(name);
            this.printStat(n.stat);
            if (n.data != null) {
                System.out.println("  dataLength = " + n.data.length);
            }
            else {
                System.out.println("  no data");
            }
            children = n.getChildren();
        }
        for (final String child : children) {
            this.printZnode(dataTree, name + (name.equals("/") ? "" : "/") + child);
        }
    }
    
    private void printSessionDetails(final DataTree dataTree, final Map<Long, Integer> sessions) {
        System.out.println("Session Details (sid, timeout, ephemeralCount):");
        for (final Map.Entry<Long, Integer> e : sessions.entrySet()) {
            final long sid = e.getKey();
            System.out.println(String.format("%#016x, %d, %d", sid, e.getValue(), dataTree.getEphemerals(sid).size()));
        }
    }
    
    private void printStat(final StatPersisted stat) {
        this.printHex("cZxid", stat.getCzxid());
        System.out.println("  ctime = " + new Date(stat.getCtime()).toString());
        this.printHex("mZxid", stat.getMzxid());
        System.out.println("  mtime = " + new Date(stat.getMtime()).toString());
        this.printHex("pZxid", stat.getPzxid());
        System.out.println("  cversion = " + stat.getCversion());
        System.out.println("  dataVersion = " + stat.getVersion());
        System.out.println("  aclVersion = " + stat.getAversion());
        this.printHex("ephemeralOwner", stat.getEphemeralOwner());
    }
    
    private void printHex(final String prefix, final long value) {
        System.out.println(String.format("  %s = %#016x", prefix, value));
    }
}
