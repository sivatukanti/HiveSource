// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jna.platform.win32;

import java.util.Map;
import com.sun.jna.Native;
import com.sun.jna.win32.W32APIOptions;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.Library;

public interface Cfgmgr32 extends Library
{
    public static final Cfgmgr32 INSTANCE = Native.load("Cfgmgr32", Cfgmgr32.class, W32APIOptions.DEFAULT_OPTIONS);
    public static final int CR_SUCCESS = 0;
    public static final int CR_BUFFER_SMALL = 26;
    public static final int CM_LOCATE_DEVNODE_NORMAL = 0;
    public static final int CM_LOCATE_DEVNODE_PHANTOM = 1;
    public static final int CM_LOCATE_DEVNODE_CANCELREMOVE = 2;
    public static final int CM_LOCATE_DEVNODE_NOVALIDATION = 4;
    public static final int CM_LOCATE_DEVNODE_BITS = 7;
    
    int CM_Locate_DevNode(final IntByReference p0, final String p1, final int p2);
    
    int CM_Get_Parent(final IntByReference p0, final int p1, final int p2);
    
    int CM_Get_Child(final IntByReference p0, final int p1, final int p2);
    
    int CM_Get_Sibling(final IntByReference p0, final int p1, final int p2);
    
    int CM_Get_Device_ID(final int p0, final Pointer p1, final int p2, final int p3);
    
    int CM_Get_Device_ID_Size(final IntByReference p0, final int p1, final int p2);
}
