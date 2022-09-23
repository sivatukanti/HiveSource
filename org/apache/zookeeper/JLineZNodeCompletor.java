// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper;

import java.util.Iterator;
import java.util.List;
import jline.Completor;

class JLineZNodeCompletor implements Completor
{
    private ZooKeeper zk;
    
    public JLineZNodeCompletor(final ZooKeeper zk) {
        this.zk = zk;
    }
    
    @Override
    public int complete(String buffer, final int cursor, final List candidates) {
        buffer = buffer.substring(0, cursor);
        String token = "";
        if (!buffer.endsWith(" ")) {
            final String[] tokens = buffer.split(" ");
            if (tokens.length != 0) {
                token = tokens[tokens.length - 1];
            }
        }
        if (token.startsWith("/")) {
            return this.completeZNode(buffer, token, candidates);
        }
        return this.completeCommand(buffer, token, candidates);
    }
    
    private int completeCommand(final String buffer, final String token, final List<String> candidates) {
        for (final String cmd : ZooKeeperMain.getCommands()) {
            if (cmd.startsWith(token)) {
                candidates.add(cmd);
            }
        }
        return buffer.lastIndexOf(" ") + 1;
    }
    
    private int completeZNode(final String buffer, final String token, final List<String> candidates) {
        final String path = token;
        final int idx = path.lastIndexOf("/") + 1;
        final String prefix = path.substring(idx);
        try {
            final String dir = (idx == 1) ? "/" : path.substring(0, idx - 1);
            final List<String> children = this.zk.getChildren(dir, false);
            for (final String child : children) {
                if (child.startsWith(prefix)) {
                    candidates.add(child);
                }
            }
        }
        catch (InterruptedException e) {
            return 0;
        }
        catch (KeeperException e2) {
            return 0;
        }
        return (candidates.size() == 0) ? buffer.length() : (buffer.lastIndexOf("/") + 1);
    }
}
