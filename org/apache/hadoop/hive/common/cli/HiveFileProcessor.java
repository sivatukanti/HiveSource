// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.hive.common.cli;

import org.apache.commons.lang.StringUtils;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.Closeable;
import org.apache.hadoop.io.IOUtils;

public abstract class HiveFileProcessor implements IHiveFileProcessor
{
    @Override
    public int processFile(final String fileName) throws IOException {
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = this.loadFile(fileName);
            return this.processReader(bufferedReader);
        }
        finally {
            IOUtils.closeStream(bufferedReader);
        }
    }
    
    protected abstract BufferedReader loadFile(final String p0) throws IOException;
    
    protected int processReader(final BufferedReader reader) throws IOException {
        final StringBuilder qsb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            if (!line.startsWith("--")) {
                qsb.append(line);
            }
        }
        return this.processLine(qsb.toString());
    }
    
    protected int processLine(final String line) {
        int lastRet = 0;
        int ret = 0;
        String command = "";
        for (final String oneCmd : line.split(";")) {
            if (StringUtils.indexOf(oneCmd, "\\") != -1) {
                command += StringUtils.join(oneCmd.split("\\\\"));
            }
            else {
                command += oneCmd;
            }
            if (!StringUtils.isBlank(command)) {
                ret = this.processCmd(command);
                command = "";
                if ((lastRet = ret) != 0) {
                    return ret;
                }
            }
        }
        return lastRet;
    }
    
    protected abstract int processCmd(final String p0);
}
