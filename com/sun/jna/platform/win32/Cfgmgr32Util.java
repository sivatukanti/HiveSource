// 
// Decompiled by Procyon v0.5.36
// 

package com.sun.jna.platform.win32;

import com.sun.jna.Pointer;
import com.sun.jna.Memory;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.Native;

public abstract class Cfgmgr32Util
{
    public static String CM_Get_Device_ID(final int devInst) throws Cfgmgr32Exception {
        final int charToBytes = Boolean.getBoolean("w32.ascii") ? 1 : Native.WCHAR_SIZE;
        final IntByReference pulLen = new IntByReference();
        int ret = Cfgmgr32.INSTANCE.CM_Get_Device_ID_Size(pulLen, devInst, 0);
        if (ret != 0) {
            throw new Cfgmgr32Exception(ret);
        }
        Memory buffer = new Memory((pulLen.getValue() + 1) * charToBytes);
        buffer.clear();
        ret = Cfgmgr32.INSTANCE.CM_Get_Device_ID(devInst, buffer, pulLen.getValue(), 0);
        if (ret == 26) {
            ret = Cfgmgr32.INSTANCE.CM_Get_Device_ID_Size(pulLen, devInst, 0);
            if (ret != 0) {
                throw new Cfgmgr32Exception(ret);
            }
            buffer = new Memory((pulLen.getValue() + 1) * charToBytes);
            buffer.clear();
            ret = Cfgmgr32.INSTANCE.CM_Get_Device_ID(devInst, buffer, pulLen.getValue(), 0);
        }
        if (ret != 0) {
            throw new Cfgmgr32Exception(ret);
        }
        if (charToBytes == 1) {
            return buffer.getString(0L);
        }
        return buffer.getWideString(0L);
    }
    
    public static class Cfgmgr32Exception extends RuntimeException
    {
        private final int errorCode;
        
        public Cfgmgr32Exception(final int errorCode) {
            this.errorCode = errorCode;
        }
        
        public int getErrorCode() {
            return this.errorCode;
        }
    }
}
