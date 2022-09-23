// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.util;

import java.nio.file.Path;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.nio.file.OpenOption;
import java.nio.file.Files;
import java.nio.file.attribute.FileAttribute;
import java.io.File;
import java.util.Random;

public class ReadWriteDiskValidator implements DiskValidator
{
    public static final String NAME = "read-write";
    private static final Random RANDOM;
    
    @Override
    public void checkStatus(final File dir) throws DiskChecker.DiskErrorException {
        final ReadWriteDiskValidatorMetrics metric = ReadWriteDiskValidatorMetrics.getMetric(dir.toString());
        Path tmpFile = null;
        try {
            if (!dir.isDirectory()) {
                metric.diskCheckFailed();
                throw new DiskChecker.DiskErrorException(dir + " is not a directory!");
            }
            DiskChecker.checkDir(dir);
            tmpFile = Files.createTempFile(dir.toPath(), "test", "tmp", (FileAttribute<?>[])new FileAttribute[0]);
            final byte[] inputBytes = new byte[16];
            ReadWriteDiskValidator.RANDOM.nextBytes(inputBytes);
            long startTime = System.nanoTime();
            Files.write(tmpFile, inputBytes, new OpenOption[0]);
            final long writeLatency = TimeUnit.MICROSECONDS.convert(System.nanoTime() - startTime, TimeUnit.NANOSECONDS);
            metric.addWriteFileLatency(writeLatency);
            startTime = System.nanoTime();
            final byte[] outputBytes = Files.readAllBytes(tmpFile);
            final long readLatency = TimeUnit.MICROSECONDS.convert(System.nanoTime() - startTime, TimeUnit.NANOSECONDS);
            metric.addReadFileLatency(readLatency);
            if (!Arrays.equals(inputBytes, outputBytes)) {
                metric.diskCheckFailed();
                throw new DiskChecker.DiskErrorException("Data in file has been corrupted.");
            }
        }
        catch (IOException e) {
            metric.diskCheckFailed();
            throw new DiskChecker.DiskErrorException("Disk Check failed!", e);
        }
        finally {
            if (tmpFile != null) {
                try {
                    Files.delete(tmpFile);
                }
                catch (IOException e2) {
                    metric.diskCheckFailed();
                    throw new DiskChecker.DiskErrorException("File deletion failed!", e2);
                }
            }
        }
    }
    
    static {
        RANDOM = new Random();
    }
}
