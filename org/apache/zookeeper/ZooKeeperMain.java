// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper;

import java.util.regex.Matcher;
import java.util.NoSuchElementException;
import java.util.Arrays;
import java.util.regex.Pattern;
import org.slf4j.LoggerFactory;
import org.apache.zookeeper.data.Id;
import java.util.ArrayList;
import org.apache.zookeeper.data.ACL;
import java.lang.reflect.Method;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Date;
import org.apache.zookeeper.data.Stat;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.apache.yetus.audience.InterfaceAudience;

@InterfaceAudience.Public
public class ZooKeeperMain
{
    private static final Logger LOG;
    static final Map<String, String> commandMap;
    protected MyCommandOptions cl;
    protected HashMap<Integer, String> history;
    protected int commandCount;
    protected boolean printWatches;
    protected ZooKeeper zk;
    protected String host;
    private static AsyncCallback.DataCallback dataCallback;
    
    public boolean getPrintWatches() {
        return this.printWatches;
    }
    
    static void usage() {
        System.err.println("ZooKeeper -server host:port cmd args");
        for (final Map.Entry<String, String> entry : ZooKeeperMain.commandMap.entrySet()) {
            System.err.println("\t" + entry.getKey() + " " + entry.getValue());
        }
    }
    
    private static int getPermFromString(final String permString) {
        int perm = 0;
        for (int i = 0; i < permString.length(); ++i) {
            switch (permString.charAt(i)) {
                case 'r': {
                    perm |= 0x1;
                    break;
                }
                case 'w': {
                    perm |= 0x2;
                    break;
                }
                case 'c': {
                    perm |= 0x4;
                    break;
                }
                case 'd': {
                    perm |= 0x8;
                    break;
                }
                case 'a': {
                    perm |= 0x10;
                    break;
                }
                default: {
                    System.err.println("Unknown perm type: " + permString.charAt(i));
                    break;
                }
            }
        }
        return perm;
    }
    
    private static void printStat(final Stat stat) {
        System.err.println("cZxid = 0x" + Long.toHexString(stat.getCzxid()));
        System.err.println("ctime = " + new Date(stat.getCtime()).toString());
        System.err.println("mZxid = 0x" + Long.toHexString(stat.getMzxid()));
        System.err.println("mtime = " + new Date(stat.getMtime()).toString());
        System.err.println("pZxid = 0x" + Long.toHexString(stat.getPzxid()));
        System.err.println("cversion = " + stat.getCversion());
        System.err.println("dataVersion = " + stat.getVersion());
        System.err.println("aclVersion = " + stat.getAversion());
        System.err.println("ephemeralOwner = 0x" + Long.toHexString(stat.getEphemeralOwner()));
        System.err.println("dataLength = " + stat.getDataLength());
        System.err.println("numChildren = " + stat.getNumChildren());
    }
    
    protected void addToHistory(final int i, final String cmd) {
        this.history.put(i, cmd);
    }
    
    public static List<String> getCommands() {
        return new LinkedList<String>(ZooKeeperMain.commandMap.keySet());
    }
    
    protected String getPrompt() {
        return "[zk: " + this.host + "(" + this.zk.getState() + ") " + this.commandCount + "] ";
    }
    
    public static void printMessage(final String msg) {
        System.out.println("\n" + msg);
    }
    
    protected void connectToZK(final String newHost) throws InterruptedException, IOException {
        if (this.zk != null && this.zk.getState().isAlive()) {
            this.zk.close();
        }
        this.host = newHost;
        final boolean readOnly = this.cl.getOption("readonly") != null;
        this.zk = new ZooKeeper(this.host, Integer.parseInt(this.cl.getOption("timeout")), new MyWatcher(), readOnly);
    }
    
    public static void main(final String[] args) throws KeeperException, IOException, InterruptedException {
        final ZooKeeperMain main = new ZooKeeperMain(args);
        main.run();
    }
    
    public ZooKeeperMain(final String[] args) throws IOException, InterruptedException {
        this.cl = new MyCommandOptions();
        this.history = new HashMap<Integer, String>();
        this.commandCount = 0;
        this.printWatches = true;
        this.host = "";
        this.cl.parseOptions(args);
        System.out.println("Connecting to " + this.cl.getOption("server"));
        this.connectToZK(this.cl.getOption("server"));
    }
    
    public ZooKeeperMain(final ZooKeeper zk) {
        this.cl = new MyCommandOptions();
        this.history = new HashMap<Integer, String>();
        this.commandCount = 0;
        this.printWatches = true;
        this.host = "";
        this.zk = zk;
    }
    
    void run() throws KeeperException, IOException, InterruptedException {
        if (this.cl.getCommand() == null) {
            System.out.println("Welcome to ZooKeeper!");
            boolean jlinemissing = false;
            try {
                final Class<?> consoleC = Class.forName("jline.ConsoleReader");
                final Class<?> completorC = Class.forName("org.apache.zookeeper.JLineZNodeCompletor");
                System.out.println("JLine support is enabled");
                final Object console = consoleC.getConstructor((Class<?>[])new Class[0]).newInstance(new Object[0]);
                final Object completor = completorC.getConstructor(ZooKeeper.class).newInstance(this.zk);
                final Method addCompletor = consoleC.getMethod("addCompletor", Class.forName("jline.Completor"));
                addCompletor.invoke(console, completor);
                final Method readLine = consoleC.getMethod("readLine", String.class);
                String line;
                while ((line = (String)readLine.invoke(console, this.getPrompt())) != null) {
                    this.executeLine(line);
                }
            }
            catch (ClassNotFoundException e) {
                ZooKeeperMain.LOG.debug("Unable to start jline", e);
                jlinemissing = true;
            }
            catch (NoSuchMethodException e2) {
                ZooKeeperMain.LOG.debug("Unable to start jline", e2);
                jlinemissing = true;
            }
            catch (InvocationTargetException e3) {
                ZooKeeperMain.LOG.debug("Unable to start jline", e3);
                jlinemissing = true;
            }
            catch (IllegalAccessException e4) {
                ZooKeeperMain.LOG.debug("Unable to start jline", e4);
                jlinemissing = true;
            }
            catch (InstantiationException e5) {
                ZooKeeperMain.LOG.debug("Unable to start jline", e5);
                jlinemissing = true;
            }
            if (jlinemissing) {
                System.out.println("JLine support is disabled");
                final BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                String line2;
                while ((line2 = br.readLine()) != null) {
                    this.executeLine(line2);
                }
            }
        }
        else {
            this.processCmd(this.cl);
        }
    }
    
    public void executeLine(final String line) throws InterruptedException, IOException, KeeperException {
        if (!line.equals("")) {
            this.cl.parseCommand(line);
            this.addToHistory(this.commandCount, line);
            this.processCmd(this.cl);
            ++this.commandCount;
        }
    }
    
    private static boolean trimProcQuotas(final ZooKeeper zk, final String path) throws KeeperException, IOException, InterruptedException {
        if ("/zookeeper/quota".equals(path)) {
            return true;
        }
        final List<String> children = zk.getChildren(path, false);
        if (children.size() == 0) {
            zk.delete(path, -1);
            final String parent = path.substring(0, path.lastIndexOf(47));
            return trimProcQuotas(zk, parent);
        }
        return true;
    }
    
    public static boolean delQuota(final ZooKeeper zk, final String path, final boolean bytes, final boolean numNodes) throws KeeperException, IOException, InterruptedException {
        final String parentPath = "/zookeeper/quota" + path;
        final String quotaPath = "/zookeeper/quota" + path + "/" + "zookeeper_limits";
        if (zk.exists(quotaPath, false) == null) {
            System.out.println("Quota does not exist for " + path);
            return true;
        }
        byte[] data = null;
        try {
            data = zk.getData(quotaPath, false, new Stat());
        }
        catch (KeeperException.NoNodeException ne) {
            System.err.println("quota does not exist for " + path);
            return true;
        }
        final StatsTrack strack = new StatsTrack(new String(data));
        if (bytes && !numNodes) {
            strack.setBytes(-1L);
            zk.setData(quotaPath, strack.toString().getBytes(), -1);
        }
        else if (!bytes && numNodes) {
            strack.setCount(-1);
            zk.setData(quotaPath, strack.toString().getBytes(), -1);
        }
        else if (bytes && numNodes) {
            final List<String> children = zk.getChildren(parentPath, false);
            for (final String child : children) {
                zk.delete(parentPath + "/" + child, -1);
            }
            trimProcQuotas(zk, parentPath);
        }
        return true;
    }
    
    private static void checkIfParentQuota(final ZooKeeper zk, final String path) throws InterruptedException, KeeperException {
        final String[] splits = path.split("/");
        String quotaPath = "/zookeeper/quota";
        for (final String str : splits) {
            if (str.length() != 0) {
                quotaPath = quotaPath + "/" + str;
                List<String> children = null;
                try {
                    children = zk.getChildren(quotaPath, false);
                }
                catch (KeeperException.NoNodeException ne) {
                    ZooKeeperMain.LOG.debug("child removed during quota check", ne);
                    return;
                }
                if (children.size() == 0) {
                    return;
                }
                for (final String child : children) {
                    if ("zookeeper_limits".equals(child)) {
                        throw new IllegalArgumentException(path + " has a parent " + quotaPath + " which has a quota");
                    }
                }
            }
        }
    }
    
    public static boolean createQuota(final ZooKeeper zk, final String path, final long bytes, final int numNodes) throws KeeperException, IOException, InterruptedException {
        final Stat initStat = zk.exists(path, false);
        if (initStat == null) {
            throw new IllegalArgumentException(path + " does not exist.");
        }
        String quotaPath = "/zookeeper/quota";
        final String realPath = "/zookeeper/quota" + path;
        try {
            final List<String> children = zk.getChildren(realPath, false);
            for (final String child : children) {
                if (!child.startsWith("zookeeper_")) {
                    throw new IllegalArgumentException(path + " has child " + child + " which has a quota");
                }
            }
        }
        catch (KeeperException.NoNodeException ex) {}
        checkIfParentQuota(zk, path);
        if (zk.exists(quotaPath, false) == null) {
            try {
                zk.create("/zookeeper", null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                zk.create("/zookeeper/quota", null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
            catch (KeeperException.NodeExistsException ex2) {}
        }
        final String[] splits = path.split("/");
        final StringBuilder sb = new StringBuilder();
        sb.append(quotaPath);
        for (int i = 1; i < splits.length; ++i) {
            sb.append("/" + splits[i]);
            quotaPath = sb.toString();
            try {
                zk.create(quotaPath, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
            catch (KeeperException.NodeExistsException ex3) {}
        }
        final String statPath = quotaPath + "/" + "zookeeper_stats";
        quotaPath = quotaPath + "/" + "zookeeper_limits";
        final StatsTrack strack = new StatsTrack(null);
        strack.setBytes(bytes);
        strack.setCount(numNodes);
        try {
            zk.create(quotaPath, strack.toString().getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            final StatsTrack stats = new StatsTrack(null);
            stats.setBytes(0L);
            stats.setCount(0);
            zk.create(statPath, stats.toString().getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
        catch (KeeperException.NodeExistsException ne) {
            final byte[] data = zk.getData(quotaPath, false, new Stat());
            final StatsTrack strackC = new StatsTrack(new String(data));
            if (bytes != -1L) {
                strackC.setBytes(bytes);
            }
            if (numNodes != -1) {
                strackC.setCount(numNodes);
            }
            zk.setData(quotaPath, strackC.toString().getBytes(), -1);
        }
        return true;
    }
    
    protected boolean processCmd(final MyCommandOptions co) throws KeeperException, IOException, InterruptedException {
        try {
            return this.processZKCmd(co);
        }
        catch (IllegalArgumentException e) {
            System.err.println("Command failed: " + e);
        }
        catch (KeeperException.NoNodeException e2) {
            System.err.println("Node does not exist: " + e2.getPath());
        }
        catch (KeeperException.NoChildrenForEphemeralsException e3) {
            System.err.println("Ephemerals cannot have children: " + e3.getPath());
        }
        catch (KeeperException.NodeExistsException e4) {
            System.err.println("Node already exists: " + e4.getPath());
        }
        catch (KeeperException.NotEmptyException e5) {
            System.err.println("Node not empty: " + e5.getPath());
        }
        catch (KeeperException.NotReadOnlyException e6) {
            System.err.println("Not a read-only call: " + e6.getPath());
        }
        catch (KeeperException.InvalidACLException e7) {
            System.err.println("Acl is not valid : " + e7.getPath());
        }
        catch (KeeperException.NoAuthException e8) {
            System.err.println("Authentication is not valid : " + e8.getPath());
        }
        catch (KeeperException.BadArgumentsException e9) {
            System.err.println("Arguments are not valid : " + e9.getPath());
        }
        catch (KeeperException.BadVersionException e10) {
            System.err.println("version No is not valid : " + e10.getPath());
        }
        return false;
    }
    
    protected boolean processZKCmd(final MyCommandOptions co) throws KeeperException, IOException, InterruptedException {
        Stat stat = new Stat();
        final String[] args = co.getArgArray();
        final String cmd = co.getCommand();
        if (args.length < 1) {
            usage();
            return false;
        }
        if (!ZooKeeperMain.commandMap.containsKey(cmd)) {
            usage();
            return false;
        }
        final boolean watch = args.length > 2;
        String path = null;
        List<ACL> acl = ZooDefs.Ids.OPEN_ACL_UNSAFE;
        ZooKeeperMain.LOG.debug("Processing " + cmd);
        if (cmd.equals("quit")) {
            System.out.println("Quitting...");
            this.zk.close();
            System.exit(0);
        }
        else if (cmd.equals("redo") && args.length >= 2) {
            final Integer i = Integer.decode(args[1]);
            if (this.commandCount <= i || i < 0) {
                System.out.println("Command index out of range");
                return false;
            }
            this.cl.parseCommand(this.history.get(i));
            if (this.cl.getCommand().equals("redo")) {
                System.out.println("No redoing redos");
                return false;
            }
            this.history.put(this.commandCount, this.history.get(i));
            this.processCmd(this.cl);
        }
        else if (cmd.equals("history")) {
            for (int j = this.commandCount - 10; j <= this.commandCount; ++j) {
                if (j >= 0) {
                    System.out.println(j + " - " + this.history.get(j));
                }
            }
        }
        else if (cmd.equals("printwatches")) {
            if (args.length == 1) {
                System.out.println("printwatches is " + (this.printWatches ? "on" : "off"));
            }
            else {
                this.printWatches = args[1].equals("on");
            }
        }
        else if (cmd.equals("connect")) {
            if (args.length >= 2) {
                this.connectToZK(args[1]);
            }
            else {
                this.connectToZK(this.host);
            }
        }
        if (this.zk == null || !this.zk.getState().isAlive()) {
            System.out.println("Not connected");
            return false;
        }
        if (cmd.equals("create") && args.length >= 3) {
            int first = 0;
            CreateMode flags = CreateMode.PERSISTENT;
            if ((args[1].equals("-e") && args[2].equals("-s")) || (args[1].equals("-s") && args[2].equals("-e"))) {
                first += 2;
                flags = CreateMode.EPHEMERAL_SEQUENTIAL;
            }
            else if (args[1].equals("-e")) {
                ++first;
                flags = CreateMode.EPHEMERAL;
            }
            else if (args[1].equals("-s")) {
                ++first;
                flags = CreateMode.PERSISTENT_SEQUENTIAL;
            }
            if (args.length == first + 4) {
                acl = parseACLs(args[first + 3]);
            }
            path = args[first + 1];
            final String newPath = this.zk.create(path, args[first + 2].getBytes(), acl, flags);
            System.err.println("Created " + newPath);
        }
        else if (cmd.equals("delete") && args.length >= 2) {
            path = args[1];
            this.zk.delete(path, watch ? Integer.parseInt(args[2]) : -1);
        }
        else if (cmd.equals("rmr") && args.length >= 2) {
            path = args[1];
            ZKUtil.deleteRecursive(this.zk, path);
        }
        else if (cmd.equals("set") && args.length >= 3) {
            path = args[1];
            stat = this.zk.setData(path, args[2].getBytes(), (args.length > 3) ? Integer.parseInt(args[3]) : -1);
            printStat(stat);
        }
        else if (cmd.equals("aget") && args.length >= 2) {
            path = args[1];
            this.zk.getData(path, watch, ZooKeeperMain.dataCallback, path);
        }
        else if (cmd.equals("get") && args.length >= 2) {
            path = args[1];
            byte[] data = this.zk.getData(path, watch, stat);
            data = ((data == null) ? "null".getBytes() : data);
            System.out.println(new String(data));
            printStat(stat);
        }
        else if (cmd.equals("ls") && args.length >= 2) {
            path = args[1];
            final List<String> children = this.zk.getChildren(path, watch);
            System.out.println(children);
        }
        else if (cmd.equals("ls2") && args.length >= 2) {
            path = args[1];
            final List<String> children = this.zk.getChildren(path, watch, stat);
            System.out.println(children);
            printStat(stat);
        }
        else if (cmd.equals("getAcl") && args.length >= 2) {
            path = args[1];
            acl = this.zk.getACL(path, stat);
            for (final ACL a : acl) {
                System.out.println(a.getId() + ": " + getPermString(a.getPerms()));
            }
        }
        else if (cmd.equals("setAcl") && args.length >= 3) {
            path = args[1];
            stat = this.zk.setACL(path, parseACLs(args[2]), (args.length > 4) ? Integer.parseInt(args[3]) : -1);
            printStat(stat);
        }
        else if (cmd.equals("stat") && args.length >= 2) {
            path = args[1];
            stat = this.zk.exists(path, watch);
            if (stat == null) {
                throw new KeeperException.NoNodeException(path);
            }
            printStat(stat);
        }
        else if (cmd.equals("listquota") && args.length >= 2) {
            path = args[1];
            final String absolutePath = "/zookeeper/quota" + path + "/" + "zookeeper_limits";
            byte[] data2 = null;
            try {
                System.err.println("absolute path is " + absolutePath);
                data2 = this.zk.getData(absolutePath, false, stat);
                final StatsTrack st = new StatsTrack(new String(data2));
                System.out.println("Output quota for " + path + " " + st.toString());
                data2 = this.zk.getData("/zookeeper/quota" + path + "/" + "zookeeper_stats", false, stat);
                System.out.println("Output stat for " + path + " " + new StatsTrack(new String(data2)).toString());
            }
            catch (KeeperException.NoNodeException ne) {
                System.err.println("quota for " + path + " does not exist.");
            }
        }
        else if (cmd.equals("setquota") && args.length >= 4) {
            final String option = args[1];
            final String val = args[2];
            path = args[3];
            System.err.println("Comment: the parts are option " + option + " val " + val + " path " + path);
            if ("-b".equals(option)) {
                createQuota(this.zk, path, Long.parseLong(val), -1);
            }
            else if ("-n".equals(option)) {
                createQuota(this.zk, path, -1L, Integer.parseInt(val));
            }
            else {
                usage();
            }
        }
        else if (cmd.equals("delquota") && args.length >= 2) {
            if (args.length == 3) {
                final String option = args[1];
                path = args[2];
                if ("-b".equals(option)) {
                    delQuota(this.zk, path, true, false);
                }
                else if ("-n".equals(option)) {
                    delQuota(this.zk, path, false, true);
                }
            }
            else if (args.length == 2) {
                path = args[1];
                delQuota(this.zk, path, true, true);
            }
            else if (cmd.equals("help")) {
                usage();
            }
        }
        else if (cmd.equals("close")) {
            this.zk.close();
        }
        else if (cmd.equals("sync") && args.length >= 2) {
            path = args[1];
            this.zk.sync(path, new AsyncCallback.VoidCallback() {
                @Override
                public void processResult(final int rc, final String path, final Object ctx) {
                    System.out.println("Sync returned " + rc);
                }
            }, null);
        }
        else if (cmd.equals("addauth") && args.length >= 2) {
            byte[] b = null;
            if (args.length >= 3) {
                b = args[2].getBytes();
            }
            this.zk.addAuthInfo(args[1], b);
        }
        else if (!ZooKeeperMain.commandMap.containsKey(cmd)) {
            usage();
        }
        return watch;
    }
    
    private static String getPermString(final int perms) {
        final StringBuilder p = new StringBuilder();
        if ((perms & 0x4) != 0x0) {
            p.append('c');
        }
        if ((perms & 0x8) != 0x0) {
            p.append('d');
        }
        if ((perms & 0x1) != 0x0) {
            p.append('r');
        }
        if ((perms & 0x2) != 0x0) {
            p.append('w');
        }
        if ((perms & 0x10) != 0x0) {
            p.append('a');
        }
        return p.toString();
    }
    
    private static List<ACL> parseACLs(final String aclString) {
        final String[] acls = aclString.split(",");
        final List<ACL> acl = new ArrayList<ACL>();
        for (final String a : acls) {
            final int firstColon = a.indexOf(58);
            final int lastColon = a.lastIndexOf(58);
            if (firstColon == -1 || lastColon == -1 || firstColon == lastColon) {
                System.err.println(a + " does not have the form scheme:id:perm");
            }
            else {
                final ACL newAcl = new ACL();
                newAcl.setId(new Id(a.substring(0, firstColon), a.substring(firstColon + 1, lastColon)));
                newAcl.setPerms(getPermFromString(a.substring(lastColon + 1)));
                acl.add(newAcl);
            }
        }
        return acl;
    }
    
    static {
        LOG = LoggerFactory.getLogger(ZooKeeperMain.class);
        (commandMap = new HashMap<String, String>()).put("connect", "host:port");
        ZooKeeperMain.commandMap.put("close", "");
        ZooKeeperMain.commandMap.put("create", "[-s] [-e] path data acl");
        ZooKeeperMain.commandMap.put("delete", "path [version]");
        ZooKeeperMain.commandMap.put("rmr", "path");
        ZooKeeperMain.commandMap.put("set", "path data [version]");
        ZooKeeperMain.commandMap.put("get", "path [watch]");
        ZooKeeperMain.commandMap.put("ls", "path [watch]");
        ZooKeeperMain.commandMap.put("ls2", "path [watch]");
        ZooKeeperMain.commandMap.put("getAcl", "path");
        ZooKeeperMain.commandMap.put("setAcl", "path acl");
        ZooKeeperMain.commandMap.put("stat", "path [watch]");
        ZooKeeperMain.commandMap.put("sync", "path");
        ZooKeeperMain.commandMap.put("setquota", "-n|-b val path");
        ZooKeeperMain.commandMap.put("listquota", "path");
        ZooKeeperMain.commandMap.put("delquota", "[-n|-b] path");
        ZooKeeperMain.commandMap.put("history", "");
        ZooKeeperMain.commandMap.put("redo", "cmdno");
        ZooKeeperMain.commandMap.put("printwatches", "on|off");
        ZooKeeperMain.commandMap.put("quit", "");
        ZooKeeperMain.commandMap.put("addauth", "scheme auth");
        ZooKeeperMain.dataCallback = new AsyncCallback.DataCallback() {
            @Override
            public void processResult(final int rc, final String path, final Object ctx, final byte[] data, final Stat stat) {
                System.out.println("rc = " + rc + " path = " + path + " data = " + ((data == null) ? "null" : new String(data)) + " stat = ");
                printStat(stat);
            }
        };
    }
    
    private class MyWatcher implements Watcher
    {
        @Override
        public void process(final WatchedEvent event) {
            if (ZooKeeperMain.this.getPrintWatches()) {
                ZooKeeperMain.printMessage("WATCHER::");
                ZooKeeperMain.printMessage(event.toString());
            }
        }
    }
    
    static class MyCommandOptions
    {
        private Map<String, String> options;
        private List<String> cmdArgs;
        private String command;
        public static final Pattern ARGS_PATTERN;
        public static final Pattern QUOTED_PATTERN;
        
        public MyCommandOptions() {
            this.options = new HashMap<String, String>();
            this.cmdArgs = null;
            this.command = null;
            this.options.put("server", "localhost:2181");
            this.options.put("timeout", "30000");
        }
        
        public String getOption(final String opt) {
            return this.options.get(opt);
        }
        
        public String getCommand() {
            return this.command;
        }
        
        public String getCmdArgument(final int index) {
            return this.cmdArgs.get(index);
        }
        
        public int getNumArguments() {
            return this.cmdArgs.size();
        }
        
        public String[] getArgArray() {
            return this.cmdArgs.toArray(new String[0]);
        }
        
        public boolean parseOptions(final String[] args) {
            final List<String> argList = Arrays.asList(args);
            final Iterator<String> it = argList.iterator();
            while (it.hasNext()) {
                final String opt = it.next();
                try {
                    if (opt.equals("-server")) {
                        this.options.put("server", it.next());
                    }
                    else if (opt.equals("-timeout")) {
                        this.options.put("timeout", it.next());
                    }
                    else if (opt.equals("-r")) {
                        this.options.put("readonly", "true");
                    }
                }
                catch (NoSuchElementException e) {
                    System.err.println("Error: no argument found for option " + opt);
                    return false;
                }
                if (!opt.startsWith("-")) {
                    this.command = opt;
                    (this.cmdArgs = new ArrayList<String>()).add(this.command);
                    while (it.hasNext()) {
                        this.cmdArgs.add(it.next());
                    }
                    return true;
                }
            }
            return true;
        }
        
        public boolean parseCommand(final String cmdstring) {
            final Matcher matcher = MyCommandOptions.ARGS_PATTERN.matcher(cmdstring);
            final List<String> args = new LinkedList<String>();
            while (matcher.find()) {
                String value = matcher.group(1);
                if (MyCommandOptions.QUOTED_PATTERN.matcher(value).matches()) {
                    value = value.substring(1, value.length() - 1);
                }
                args.add(value);
            }
            if (args.isEmpty()) {
                return false;
            }
            this.command = args.get(0);
            this.cmdArgs = args;
            return true;
        }
        
        static {
            ARGS_PATTERN = Pattern.compile("\\s*([^\"']\\S*|\"[^\"]*\"|'[^']*')\\s*");
            QUOTED_PATTERN = Pattern.compile("^(['\"])(.*)(\\1)$");
        }
    }
}
