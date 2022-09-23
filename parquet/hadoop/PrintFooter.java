// 
// Decompiled by Procyon v0.5.36
// 

package parquet.hadoop;

import java.util.TreeSet;
import java.util.LinkedHashMap;
import parquet.column.statistics.Statistics;
import parquet.schema.MessageType;
import parquet.column.Encoding;
import java.util.Collection;
import parquet.hadoop.metadata.ColumnChunkMetaData;
import parquet.hadoop.metadata.BlockMetaData;
import java.util.Set;
import java.util.Deque;
import java.util.concurrent.ExecutorService;
import java.util.Iterator;
import java.util.List;
import org.apache.hadoop.fs.FileSystem;
import parquet.io.ParquetDecodingException;
import parquet.format.converter.ParquetMetadataConverter;
import java.util.concurrent.Callable;
import parquet.hadoop.metadata.ParquetMetadata;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.Executors;
import org.apache.hadoop.fs.FileStatus;
import java.util.ArrayList;
import java.util.Arrays;
import org.apache.hadoop.fs.PathFilter;
import parquet.hadoop.util.HiddenFileFilter;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import java.net.URI;
import parquet.column.ColumnDescriptor;
import java.util.Map;

public class PrintFooter
{
    private static Map<ColumnDescriptor, ColStats> stats;
    private static int blockCount;
    private static long recordCount;
    
    public static void main(final String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("usage PrintFooter <path>");
            return;
        }
        final Path path = new Path(new URI(args[0]));
        final Configuration configuration = new Configuration();
        final FileSystem fs = path.getFileSystem(configuration);
        final FileStatus fileStatus = fs.getFileStatus(path);
        final Path summary = new Path(fileStatus.getPath(), "_metadata");
        if (fileStatus.isDir() && fs.exists(summary)) {
            System.out.println("reading summary file");
            final FileStatus summaryStatus = fs.getFileStatus(summary);
            final List<Footer> readSummaryFile = ParquetFileReader.readSummaryFile(configuration, summaryStatus);
            for (final Footer footer : readSummaryFile) {
                add(footer.getParquetMetadata());
            }
        }
        else {
            List<FileStatus> statuses;
            if (fileStatus.isDir()) {
                System.out.println("listing files in " + fileStatus.getPath());
                statuses = Arrays.asList(fs.listStatus(fileStatus.getPath(), HiddenFileFilter.INSTANCE));
            }
            else {
                statuses = new ArrayList<FileStatus>();
                statuses.add(fileStatus);
            }
            System.out.println("opening " + statuses.size() + " files");
            int i = 0;
            final ExecutorService threadPool = Executors.newFixedThreadPool(5);
            try {
                final long t0 = System.currentTimeMillis();
                final Deque<Future<ParquetMetadata>> footers = new LinkedBlockingDeque<Future<ParquetMetadata>>();
                for (final FileStatus currentFile : statuses) {
                    footers.add(threadPool.submit((Callable<ParquetMetadata>)new Callable<ParquetMetadata>() {
                        @Override
                        public ParquetMetadata call() throws Exception {
                            try {
                                final ParquetMetadata footer = ParquetFileReader.readFooter(configuration, currentFile, ParquetMetadataConverter.NO_FILTER);
                                return footer;
                            }
                            catch (Exception e) {
                                throw new ParquetDecodingException("could not read footer", e);
                            }
                        }
                    }));
                }
                int previousPercent = 0;
                final int n = 60;
                System.out.print("0% [");
                for (int j = 0; j < n; ++j) {
                    System.out.print(" ");
                }
                System.out.print("] 100%");
                for (int j = 0; j < n + 6; ++j) {
                    System.out.print('\b');
                }
                while (!footers.isEmpty()) {
                    final Future<ParquetMetadata> futureFooter = footers.removeFirst();
                    if (!futureFooter.isDone()) {
                        footers.addLast(futureFooter);
                    }
                    else {
                        final ParquetMetadata footer2 = futureFooter.get();
                        for (int currentPercent = ++i * n / statuses.size(); currentPercent > previousPercent; ++previousPercent) {
                            System.out.print("*");
                        }
                        add(footer2);
                    }
                }
                System.out.println("");
                final long t2 = System.currentTimeMillis();
                System.out.println("read all footers in " + (t2 - t0) + " ms");
            }
            finally {
                threadPool.shutdownNow();
            }
        }
        final Set<Map.Entry<ColumnDescriptor, ColStats>> entries = PrintFooter.stats.entrySet();
        long total = 0L;
        long totalUnc = 0L;
        for (final Map.Entry<ColumnDescriptor, ColStats> entry : entries) {
            final ColStats colStats = entry.getValue();
            total += colStats.allStats.total;
            totalUnc += colStats.uncStats.total;
        }
        for (final Map.Entry<ColumnDescriptor, ColStats> entry : entries) {
            final ColStats colStats = entry.getValue();
            System.out.println(entry.getKey() + " " + percent(colStats.allStats.total, total) + "% of all space " + colStats);
        }
        System.out.println("number of blocks: " + PrintFooter.blockCount);
        System.out.println("total data size: " + humanReadable(total) + " (raw " + humanReadable(totalUnc) + ")");
        System.out.println("total record: " + humanReadable(PrintFooter.recordCount));
        System.out.println("average block size: " + humanReadable(total / PrintFooter.blockCount) + " (raw " + humanReadable(totalUnc / PrintFooter.blockCount) + ")");
        System.out.println("average record count: " + humanReadable(PrintFooter.recordCount / PrintFooter.blockCount));
    }
    
    private static void add(final ParquetMetadata footer) {
        for (final BlockMetaData blockMetaData : footer.getBlocks()) {
            ++PrintFooter.blockCount;
            final MessageType schema = footer.getFileMetaData().getSchema();
            PrintFooter.recordCount += blockMetaData.getRowCount();
            final List<ColumnChunkMetaData> columns = blockMetaData.getColumns();
            for (final ColumnChunkMetaData columnMetaData : columns) {
                final ColumnDescriptor desc = schema.getColumnDescription(columnMetaData.getPath().toArray());
                add(desc, columnMetaData.getValueCount(), columnMetaData.getTotalSize(), columnMetaData.getTotalUncompressedSize(), columnMetaData.getEncodings(), columnMetaData.getStatistics());
            }
        }
    }
    
    private static void printTotalString(final String message, final long total, final long totalUnc) {
        System.out.println("total " + message + ": " + humanReadable(total) + " (raw " + humanReadable(totalUnc) + " saved " + percentComp(totalUnc, total) + "%)");
    }
    
    private static float percentComp(final long raw, final long compressed) {
        return percent(raw - compressed, raw);
    }
    
    private static float percent(final long numerator, final long denominator) {
        return numerator * 1000L / denominator / 10.0f;
    }
    
    private static String humanReadable(final long size) {
        if (size < 1000L) {
            return String.valueOf(size);
        }
        long currentSize = size;
        long previousSize = size * 1000L;
        int count = 0;
        final String[] unit = { "", "K", "M", "G", "T", "P" };
        while (currentSize >= 1000L) {
            previousSize = currentSize;
            currentSize /= 1000L;
            ++count;
        }
        return previousSize / 1000.0f + unit[count];
    }
    
    private static void add(final ColumnDescriptor desc, final long valueCount, final long size, final long uncSize, final Collection<Encoding> encodings, final Statistics colValuesStats) {
        ColStats colStats = PrintFooter.stats.get(desc);
        if (colStats == null) {
            colStats = new ColStats();
            PrintFooter.stats.put(desc, colStats);
        }
        colStats.add(valueCount, size, uncSize, encodings, colValuesStats);
    }
    
    static {
        PrintFooter.stats = new LinkedHashMap<ColumnDescriptor, ColStats>();
        PrintFooter.blockCount = 0;
        PrintFooter.recordCount = 0L;
    }
    
    private static class Stats
    {
        long min;
        long max;
        long total;
        
        private Stats() {
            this.min = Long.MAX_VALUE;
            this.max = Long.MIN_VALUE;
            this.total = 0L;
        }
        
        public void add(final long length) {
            this.min = Math.min(length, this.min);
            this.max = Math.max(length, this.max);
            this.total += length;
        }
        
        public String toString(final int blocks) {
            return "min: " + humanReadable(this.min) + " max: " + humanReadable(this.max) + " average: " + humanReadable(this.total / blocks) + " total: " + humanReadable(this.total);
        }
    }
    
    private static class ColStats
    {
        Stats valueCountStats;
        Stats allStats;
        Stats uncStats;
        Set<Encoding> encodings;
        Statistics colValuesStats;
        int blocks;
        
        private ColStats() {
            this.valueCountStats = new Stats();
            this.allStats = new Stats();
            this.uncStats = new Stats();
            this.encodings = new TreeSet<Encoding>();
            this.colValuesStats = null;
            this.blocks = 0;
        }
        
        public void add(final long valueCount, final long size, final long uncSize, final Collection<Encoding> encodings, final Statistics colValuesStats) {
            ++this.blocks;
            this.valueCountStats.add(valueCount);
            this.allStats.add(size);
            this.uncStats.add(uncSize);
            this.encodings.addAll(encodings);
            this.colValuesStats = colValuesStats;
        }
        
        @Override
        public String toString() {
            final long raw = this.uncStats.total;
            final long compressed = this.allStats.total;
            return this.encodings + " " + this.allStats.toString(this.blocks) + " (raw data: " + humanReadable(raw) + ((raw == 0L) ? "" : (" saving " + (raw - compressed) * 100L / raw + "%")) + ")\n" + "  values: " + this.valueCountStats.toString(this.blocks) + "\n" + "  uncompressed: " + this.uncStats.toString(this.blocks) + "\n" + "  column values statistics: " + this.colValuesStats.toString();
        }
    }
}
