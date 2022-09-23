// 
// Decompiled by Procyon v0.5.36
// 

package parquet.hadoop.mapred;

import java.io.IOException;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.Configuration;
import parquet.hadoop.ParquetOutputCommitter;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobConf;
import parquet.hadoop.util.ContextUtil;
import org.apache.hadoop.mapred.JobContext;
import org.apache.hadoop.mapred.FileOutputCommitter;

public class MapredParquetOutputCommitter extends FileOutputCommitter
{
    public void commitJob(final JobContext jobContext) throws IOException {
        super.commitJob(jobContext);
        final Configuration conf = ContextUtil.getConfiguration((org.apache.hadoop.mapreduce.JobContext)jobContext);
        final Path outputPath = FileOutputFormat.getOutputPath(new JobConf(conf));
        ParquetOutputCommitter.writeMetaDataFile(conf, outputPath);
    }
}
