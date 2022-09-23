// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.metrics2.sink;

import java.io.IOException;
import java.util.Iterator;
import org.apache.hadoop.metrics2.AbstractMetric;
import org.apache.hadoop.metrics2.MetricsTag;
import org.apache.hadoop.metrics2.MetricsRecord;
import org.apache.hadoop.metrics2.MetricsException;
import java.io.FileOutputStream;
import java.io.File;
import java.io.OutputStream;
import org.apache.commons.configuration2.SubsetConfiguration;
import java.io.PrintStream;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import java.io.Closeable;
import org.apache.hadoop.metrics2.MetricsSink;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public class FileSink implements MetricsSink, Closeable
{
    private static final String FILENAME_KEY = "filename";
    private PrintStream writer;
    
    @Override
    public void init(final SubsetConfiguration conf) {
        final String filename = conf.getString("filename");
        try {
            PrintStream out;
            if (filename == null) {
                out = System.out;
            }
            else {
                final FileOutputStream out2;
                out = new PrintStream(out2, true, "UTF-8");
                out2 = new FileOutputStream(new File(filename));
            }
            this.writer = out;
        }
        catch (Exception e) {
            throw new MetricsException("Error creating " + filename, e);
        }
    }
    
    @Override
    public void putMetrics(final MetricsRecord record) {
        this.writer.print(record.timestamp());
        this.writer.print(" ");
        this.writer.print(record.context());
        this.writer.print(".");
        this.writer.print(record.name());
        String separator = ": ";
        for (final MetricsTag tag : record.tags()) {
            this.writer.print(separator);
            separator = ", ";
            this.writer.print(tag.name());
            this.writer.print("=");
            this.writer.print(tag.value());
        }
        for (final AbstractMetric metric : record.metrics()) {
            this.writer.print(separator);
            separator = ", ";
            this.writer.print(metric.name());
            this.writer.print("=");
            this.writer.print(metric.value());
        }
        this.writer.println();
    }
    
    @Override
    public void flush() {
        this.writer.flush();
    }
    
    @Override
    public void close() throws IOException {
        this.writer.close();
    }
}
