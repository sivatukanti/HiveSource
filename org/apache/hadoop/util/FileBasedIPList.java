// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util;

import org.slf4j.LoggerFactory;
import java.util.List;
import java.io.Reader;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.io.FileInputStream;
import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Arrays;
import java.io.IOException;
import org.slf4j.Logger;

public class FileBasedIPList implements IPList
{
    private static final Logger LOG;
    private final String fileName;
    private final MachineList addressList;
    
    public FileBasedIPList(final String fileName) {
        this.fileName = fileName;
        String[] lines;
        try {
            lines = readLines(fileName);
        }
        catch (IOException e) {
            lines = null;
        }
        if (lines != null) {
            this.addressList = new MachineList(new HashSet<String>(Arrays.asList(lines)));
        }
        else {
            this.addressList = null;
        }
    }
    
    public FileBasedIPList reload() {
        return new FileBasedIPList(this.fileName);
    }
    
    @Override
    public boolean isIn(final String ipAddress) {
        return ipAddress != null && this.addressList != null && this.addressList.includes(ipAddress);
    }
    
    private static String[] readLines(final String fileName) throws IOException {
        try {
            if (fileName != null) {
                final File file = new File(fileName);
                if (file.exists()) {
                    try (final Reader fileReader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
                         final BufferedReader bufferedReader = new BufferedReader(fileReader)) {
                        final List<String> lines = new ArrayList<String>();
                        String line = null;
                        while ((line = bufferedReader.readLine()) != null) {
                            lines.add(line);
                        }
                        if (FileBasedIPList.LOG.isDebugEnabled()) {
                            FileBasedIPList.LOG.debug("Loaded IP list of size = " + lines.size() + " from file = " + fileName);
                        }
                        return lines.toArray(new String[lines.size()]);
                    }
                }
                FileBasedIPList.LOG.debug("Missing ip list file : " + fileName);
            }
        }
        catch (IOException ioe) {
            FileBasedIPList.LOG.error(ioe.toString());
            throw ioe;
        }
        return null;
    }
    
    static {
        LOG = LoggerFactory.getLogger(FileBasedIPList.class);
    }
}
