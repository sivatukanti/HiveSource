// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.commons.net.nntp;

public final class NNTPCommand
{
    public static final int ARTICLE = 0;
    public static final int BODY = 1;
    public static final int GROUP = 2;
    public static final int HEAD = 3;
    public static final int HELP = 4;
    public static final int IHAVE = 5;
    public static final int LAST = 6;
    public static final int LIST = 7;
    public static final int NEWGROUPS = 8;
    public static final int NEWNEWS = 9;
    public static final int NEXT = 10;
    public static final int POST = 11;
    public static final int QUIT = 12;
    public static final int SLAVE = 13;
    public static final int STAT = 14;
    public static final int AUTHINFO = 15;
    public static final int XOVER = 16;
    public static final int XHDR = 17;
    private static final String[] _commands;
    
    private NNTPCommand() {
    }
    
    public static final String getCommand(final int command) {
        return NNTPCommand._commands[command];
    }
    
    static {
        _commands = new String[] { "ARTICLE", "BODY", "GROUP", "HEAD", "HELP", "IHAVE", "LAST", "LIST", "NEWGROUPS", "NEWNEWS", "NEXT", "POST", "QUIT", "SLAVE", "STAT", "AUTHINFO", "XOVER", "XHDR" };
    }
}
