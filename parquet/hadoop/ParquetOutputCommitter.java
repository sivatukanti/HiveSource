// 
// Decompiled by Procyon v0.5.36
// 

package parquet.hadoop;

import java.util.List;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.conf.Configuration;
import parquet.hadoop.util.ContextUtil;
import org.apache.hadoop.mapreduce.JobContext;
import java.io.IOException;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.fs.Path;
import parquet.Log;
import org.apache.hadoop.mapreduce.lib.output.FileOutputCommitter;

public class ParquetOutputCommitter extends FileOutputCommitter
{
    private static final Log LOG;
    private final Path outputPath;
    
    public ParquetOutputCommitter(final Path outputPath, final TaskAttemptContext context) throws IOException {
        super(outputPath, context);
        this.outputPath = outputPath;
    }
    
    public void commitJob(final JobContext jobContext) throws IOException {
        super.commitJob(jobContext);
        final Configuration configuration = ContextUtil.getConfiguration(jobContext);
        writeMetaDataFile(configuration, this.outputPath);
    }
    
    public static void writeMetaDataFile(final Configuration configuration, final Path outputPath) {
        if (configuration.getBoolean("parquet.enable.summary-metadata", true)) {
            try {
                final FileSystem fileSystem = outputPath.getFileSystem(configuration);
                final FileStatus outputStatus = fileSystem.getFileStatus(outputPath);
                final List<Footer> footers = ParquetFileReader.readAllFootersInParallel(configuration, outputStatus);
                try {
                    ParquetFileWriter.writeMetadataFile(configuration, outputPath, footers);
                }
                catch (Exception e) {
                    ParquetOutputCommitter.LOG.warn("could not write summary file for " + outputPath, e);
                    final Path metadataPath = new Path(outputPath, "_metadata");
                    if (fileSystem.exists(metadataPath)) {
                        fileSystem.delete(metadataPath, true);
                    }
                }
            }
            catch (Exception e2) {
                ParquetOutputCommitter.LOG.warn("could not write summary file for " + outputPath, e2);
            }
        }
    }
    
    static {
        LOG = Log.getLog(ParquetOutputCommitter.class);
    }
}
