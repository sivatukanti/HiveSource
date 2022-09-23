// 
// Decompiled by Procyon v0.5.36
// 

package org.eclipse.jetty.ajp;

import org.eclipse.jetty.io.Buffer;
import org.eclipse.jetty.io.BufferCache;

public class Ajp13PacketMethods
{
    public static final String OPTIONS = "OPTIONS";
    public static final String GET = "GET";
    public static final String HEAD = "HEAD";
    public static final String POST = "POST";
    public static final String PUT = "PUT";
    public static final String DELETE = "DELETE";
    public static final String TRACE = "TRACE";
    public static final String PROPFIND = "PROPFIND";
    public static final String PROPPATCH = "PROPPATCH";
    public static final String MKCOL = "MKCOL";
    public static final String COPY = "COPY";
    public static final String MOVE = "MOVE";
    public static final String LOCK = "LOCK";
    public static final String UNLOCK = "UNLOCK";
    public static final String ACL = "ACL";
    public static final String REPORT = "REPORT";
    public static final String VERSION_CONTROL = "VERSION-CONTROL";
    public static final String CHECKIN = "CHECKIN";
    public static final String CHECKOUT = "CHECKOUT";
    public static final String UNCHCKOUT = "UNCHECKOUT";
    public static final String SEARCH = "SEARCH";
    public static final String MKWORKSPACE = "MKWORKSPACE";
    public static final String UPDATE = "UPDATE";
    public static final String LABEL = "LABEL";
    public static final String MERGE = "MERGE";
    public static final String BASELINE_CONTROL = "BASELINE-CONTROL";
    public static final String MKACTIVITY = "MKACTIVITY";
    public static final int OPTIONS_ORDINAL = 1;
    public static final int GET_ORDINAL = 2;
    public static final int HEAD_ORDINAL = 3;
    public static final int POST__ORDINAL = 4;
    public static final int PUT_ORDINAL = 5;
    public static final int DELETE_ORDINAL = 6;
    public static final int TRACE_ORDINAL = 7;
    public static final int PROPFIND_ORDINAL = 8;
    public static final int PROPPATCH_ORDINAL = 9;
    public static final int MKCOL_ORDINAL = 10;
    public static final int COPY_ORDINAL = 11;
    public static final int MOVE_ORDINAL = 12;
    public static final int LOCK_ORDINAL = 13;
    public static final int UNLOCK_ORDINAL = 14;
    public static final int ACL_ORDINAL = 15;
    public static final int REPORT_ORDINAL = 16;
    public static final int VERSION_CONTROL_ORDINAL = 17;
    public static final int CHECKIN_ORDINAL = 18;
    public static final int CHECKOUT_ORDINAL = 19;
    public static final int UNCHCKOUT_ORDINAL = 20;
    public static final int SEARCH_ORDINAL = 21;
    public static final int MKWORKSPACE_ORDINAL = 22;
    public static final int UPDATE_ORDINAL = 23;
    public static final int LABEL_ORDINAL = 24;
    public static final int MERGE_ORDINAL = 25;
    public static final int BASELINE_CONTROL_ORDINAL = 26;
    public static final int MKACTIVITY_ORDINAL = 27;
    public static final BufferCache CACHE;
    public static final Buffer OPTIONS_BUFFER;
    public static final Buffer GET_BUFFER;
    public static final Buffer HEAD_BUFFER;
    public static final Buffer POST__BUFFER;
    public static final Buffer PUT_BUFFER;
    public static final Buffer DELETE_BUFFER;
    public static final Buffer TRACE_BUFFER;
    public static final Buffer PROPFIND_BUFFER;
    public static final Buffer PROPPATCH_BUFFER;
    public static final Buffer MKCOL_BUFFER;
    public static final Buffer COPY_BUFFER;
    public static final Buffer MOVE_BUFFER;
    public static final Buffer LOCK_BUFFER;
    public static final Buffer UNLOCK_BUFFER;
    public static final Buffer ACL_BUFFER;
    public static final Buffer REPORT_BUFFER;
    public static final Buffer VERSION_CONTROL_BUFFER;
    public static final Buffer CHECKIN_BUFFER;
    public static final Buffer CHECKOUT_BUFFER;
    public static final Buffer UNCHCKOUT_BUFFER;
    public static final Buffer SEARCH_BUFFER;
    public static final Buffer MKWORKSPACE_BUFFER;
    public static final Buffer UPDATE_BUFFER;
    public static final Buffer LABEL_BUFFER;
    public static final Buffer MERGE_BUFFER;
    public static final Buffer BASELINE_CONTROL_BUFFER;
    public static final Buffer MKACTIVITY_BUFFER;
    
    static {
        CACHE = new BufferCache();
        OPTIONS_BUFFER = Ajp13PacketMethods.CACHE.add("OPTIONS", 1);
        GET_BUFFER = Ajp13PacketMethods.CACHE.add("GET", 2);
        HEAD_BUFFER = Ajp13PacketMethods.CACHE.add("HEAD", 3);
        POST__BUFFER = Ajp13PacketMethods.CACHE.add("POST", 4);
        PUT_BUFFER = Ajp13PacketMethods.CACHE.add("PUT", 5);
        DELETE_BUFFER = Ajp13PacketMethods.CACHE.add("DELETE", 6);
        TRACE_BUFFER = Ajp13PacketMethods.CACHE.add("TRACE", 7);
        PROPFIND_BUFFER = Ajp13PacketMethods.CACHE.add("PROPFIND", 8);
        PROPPATCH_BUFFER = Ajp13PacketMethods.CACHE.add("PROPPATCH", 9);
        MKCOL_BUFFER = Ajp13PacketMethods.CACHE.add("MKCOL", 10);
        COPY_BUFFER = Ajp13PacketMethods.CACHE.add("COPY", 11);
        MOVE_BUFFER = Ajp13PacketMethods.CACHE.add("MOVE", 12);
        LOCK_BUFFER = Ajp13PacketMethods.CACHE.add("LOCK", 13);
        UNLOCK_BUFFER = Ajp13PacketMethods.CACHE.add("UNLOCK", 14);
        ACL_BUFFER = Ajp13PacketMethods.CACHE.add("ACL", 15);
        REPORT_BUFFER = Ajp13PacketMethods.CACHE.add("REPORT", 16);
        VERSION_CONTROL_BUFFER = Ajp13PacketMethods.CACHE.add("VERSION-CONTROL", 17);
        CHECKIN_BUFFER = Ajp13PacketMethods.CACHE.add("CHECKIN", 18);
        CHECKOUT_BUFFER = Ajp13PacketMethods.CACHE.add("CHECKOUT", 19);
        UNCHCKOUT_BUFFER = Ajp13PacketMethods.CACHE.add("UNCHECKOUT", 20);
        SEARCH_BUFFER = Ajp13PacketMethods.CACHE.add("SEARCH", 21);
        MKWORKSPACE_BUFFER = Ajp13PacketMethods.CACHE.add("MKWORKSPACE", 22);
        UPDATE_BUFFER = Ajp13PacketMethods.CACHE.add("UPDATE", 23);
        LABEL_BUFFER = Ajp13PacketMethods.CACHE.add("LABEL", 24);
        MERGE_BUFFER = Ajp13PacketMethods.CACHE.add("MERGE", 25);
        BASELINE_CONTROL_BUFFER = Ajp13PacketMethods.CACHE.add("BASELINE-CONTROL", 26);
        MKACTIVITY_BUFFER = Ajp13PacketMethods.CACHE.add("MKACTIVITY", 27);
    }
}
