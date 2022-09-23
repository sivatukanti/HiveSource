// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.io.file.tfile;

import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.Set;
import java.util.Iterator;
import java.util.Collection;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.io.IOUtils;
import java.io.Closeable;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.LinkedHashMap;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.Configuration;
import java.io.PrintStream;
import org.slf4j.Logger;

class TFileDumper
{
    static final Logger LOG;
    
    private TFileDumper() {
    }
    
    public static void dumpInfo(final String file, final PrintStream out, final Configuration conf) throws IOException {
        final int maxKeySampleLen = 16;
        final Path path = new Path(file);
        final FileSystem fs = path.getFileSystem(conf);
        final long length = fs.getFileStatus(path).getLen();
        final FSDataInputStream fsdis = fs.open(path);
        final TFile.Reader reader = new TFile.Reader(fsdis, length, conf);
        try {
            final LinkedHashMap<String, String> properties = new LinkedHashMap<String, String>();
            final int blockCnt = reader.readerBCF.getBlockCount();
            final int metaBlkCnt = reader.readerBCF.metaIndex.index.size();
            properties.put("BCFile Version", reader.readerBCF.version.toString());
            properties.put("TFile Version", reader.tfileMeta.version.toString());
            properties.put("File Length", Long.toString(length));
            properties.put("Data Compression", reader.readerBCF.getDefaultCompressionName());
            properties.put("Record Count", Long.toString(reader.getEntryCount()));
            properties.put("Sorted", Boolean.toString(reader.isSorted()));
            if (reader.isSorted()) {
                properties.put("Comparator", reader.getComparatorName());
            }
            properties.put("Data Block Count", Integer.toString(blockCnt));
            long dataSize = 0L;
            long dataSizeUncompressed = 0L;
            if (blockCnt > 0) {
                for (int i = 0; i < blockCnt; ++i) {
                    final BCFile.BlockRegion region = reader.readerBCF.dataIndex.getBlockRegionList().get(i);
                    dataSize += region.getCompressedSize();
                    dataSizeUncompressed += region.getRawSize();
                }
                properties.put("Data Block Bytes", Long.toString(dataSize));
                if (!reader.readerBCF.getDefaultCompressionName().equals("none")) {
                    properties.put("Data Block Uncompressed Bytes", Long.toString(dataSizeUncompressed));
                    properties.put("Data Block Compression Ratio", String.format("1:%.1f", dataSizeUncompressed / (double)dataSize));
                }
            }
            properties.put("Meta Block Count", Integer.toString(metaBlkCnt));
            long metaSize = 0L;
            long metaSizeUncompressed = 0L;
            if (metaBlkCnt > 0) {
                final Collection<BCFile.MetaIndexEntry> metaBlks = reader.readerBCF.metaIndex.index.values();
                boolean calculateCompression = false;
                for (final BCFile.MetaIndexEntry e : metaBlks) {
                    metaSize += e.getRegion().getCompressedSize();
                    metaSizeUncompressed += e.getRegion().getRawSize();
                    if (e.getCompressionAlgorithm() != Compression.Algorithm.NONE) {
                        calculateCompression = true;
                    }
                }
                properties.put("Meta Block Bytes", Long.toString(metaSize));
                if (calculateCompression) {
                    properties.put("Meta Block Uncompressed Bytes", Long.toString(metaSizeUncompressed));
                    properties.put("Meta Block Compression Ratio", String.format("1:%.1f", metaSizeUncompressed / (double)metaSize));
                }
            }
            properties.put("Meta-Data Size Ratio", String.format("1:%.1f", dataSize / (double)metaSize));
            final long leftOverBytes = length - dataSize - metaSize;
            final long miscSize = BCFile.Magic.size() * 2 + 8 + Utils.Version.size();
            final long metaIndexSize = leftOverBytes - miscSize;
            properties.put("Meta Block Index Bytes", Long.toString(metaIndexSize));
            properties.put("Headers Etc Bytes", Long.toString(miscSize));
            int maxKeyLength = 0;
            final Set<Map.Entry<String, String>> entrySet = properties.entrySet();
            for (final Map.Entry<String, String> e2 : entrySet) {
                if (e2.getKey().length() > maxKeyLength) {
                    maxKeyLength = e2.getKey().length();
                }
            }
            for (final Map.Entry<String, String> e2 : entrySet) {
                out.printf("%s : %s%n", Align.format(e2.getKey(), maxKeyLength, Align.LEFT), e2.getValue());
            }
            out.println();
            reader.checkTFileDataIndex();
            if (blockCnt > 0) {
                final String blkID = "Data-Block";
                final int blkIDWidth = Align.calculateWidth(blkID, blockCnt);
                final int blkIDWidth2 = Align.calculateWidth("", blockCnt);
                final String offset = "Offset";
                final int offsetWidth = Align.calculateWidth(offset, length);
                final String blkLen = "Length";
                final int blkLenWidth = Align.calculateWidth(blkLen, dataSize / blockCnt * 10L);
                final String rawSize = "Raw-Size";
                final int rawSizeWidth = Align.calculateWidth(rawSize, dataSizeUncompressed / blockCnt * 10L);
                final String records = "Records";
                final int recordsWidth = Align.calculateWidth(records, reader.getEntryCount() / blockCnt * 10L);
                final String endKey = "End-Key";
                final int endKeyWidth = Math.max(endKey.length(), 37);
                out.printf("%s %s %s %s %s %s%n", Align.format(blkID, blkIDWidth, Align.CENTER), Align.format(offset, offsetWidth, Align.CENTER), Align.format(blkLen, blkLenWidth, Align.CENTER), Align.format(rawSize, rawSizeWidth, Align.CENTER), Align.format(records, recordsWidth, Align.CENTER), Align.format(endKey, endKeyWidth, Align.LEFT));
                for (int j = 0; j < blockCnt; ++j) {
                    final BCFile.BlockRegion region2 = reader.readerBCF.dataIndex.getBlockRegionList().get(j);
                    final TFile.TFileIndexEntry indexEntry = reader.tfileIndex.getEntry(j);
                    out.printf("%s %s %s %s %s ", Align.format(Align.format(j, blkIDWidth2, Align.ZERO_PADDED), blkIDWidth, Align.LEFT), Align.format(region2.getOffset(), offsetWidth, Align.LEFT), Align.format(region2.getCompressedSize(), blkLenWidth, Align.LEFT), Align.format(region2.getRawSize(), rawSizeWidth, Align.LEFT), Align.format(indexEntry.kvEntries, recordsWidth, Align.LEFT));
                    final byte[] key = indexEntry.key;
                    boolean asAscii = true;
                    final int sampleLen = Math.min(16, key.length);
                    for (final byte b : key) {
                        if ((b < 32 && b != 9) || b == 127) {
                            asAscii = false;
                        }
                    }
                    if (!asAscii) {
                        out.print("0X");
                        for (int k = 0; k < sampleLen; ++k) {
                            final byte b = key[j];
                            out.printf("%X", b);
                        }
                    }
                    else {
                        out.print(new String(key, 0, sampleLen, StandardCharsets.UTF_8));
                    }
                    if (sampleLen < key.length) {
                        out.print("...");
                    }
                    out.println();
                }
            }
            out.println();
            if (metaBlkCnt > 0) {
                final String name = "Meta-Block";
                int maxNameLen = 0;
                final Set<Map.Entry<String, BCFile.MetaIndexEntry>> metaBlkEntrySet = reader.readerBCF.metaIndex.index.entrySet();
                for (final Map.Entry<String, BCFile.MetaIndexEntry> e3 : metaBlkEntrySet) {
                    if (e3.getKey().length() > maxNameLen) {
                        maxNameLen = e3.getKey().length();
                    }
                }
                final int nameWidth = Math.max(name.length(), maxNameLen);
                final String offset2 = "Offset";
                final int offsetWidth2 = Align.calculateWidth(offset2, length);
                final String blkLen2 = "Length";
                final int blkLenWidth2 = Align.calculateWidth(blkLen2, metaSize / metaBlkCnt * 10L);
                final String rawSize2 = "Raw-Size";
                final int rawSizeWidth2 = Align.calculateWidth(rawSize2, metaSizeUncompressed / metaBlkCnt * 10L);
                final String compression = "Compression";
                final int compressionWidth = compression.length();
                out.printf("%s %s %s %s %s%n", Align.format(name, nameWidth, Align.CENTER), Align.format(offset2, offsetWidth2, Align.CENTER), Align.format(blkLen2, blkLenWidth2, Align.CENTER), Align.format(rawSize2, rawSizeWidth2, Align.CENTER), Align.format(compression, compressionWidth, Align.LEFT));
                for (final Map.Entry<String, BCFile.MetaIndexEntry> e4 : metaBlkEntrySet) {
                    final String blkName = e4.getValue().getMetaName();
                    final BCFile.BlockRegion region3 = e4.getValue().getRegion();
                    final String blkCompression = e4.getValue().getCompressionAlgorithm().getName();
                    out.printf("%s %s %s %s %s%n", Align.format(blkName, nameWidth, Align.LEFT), Align.format(region3.getOffset(), offsetWidth2, Align.LEFT), Align.format(region3.getCompressedSize(), blkLenWidth2, Align.LEFT), Align.format(region3.getRawSize(), rawSizeWidth2, Align.LEFT), Align.format(blkCompression, compressionWidth, Align.LEFT));
                }
            }
        }
        finally {
            IOUtils.cleanupWithLogger(TFileDumper.LOG, reader, fsdis);
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger(TFileDumper.class);
    }
    
    private enum Align
    {
        LEFT, 
        CENTER, 
        RIGHT, 
        ZERO_PADDED;
        
        static String format(final String s, final int width, final Align align) {
            if (s.length() >= width) {
                return s;
            }
            final int room = width - s.length();
            Align alignAdjusted = align;
            if (room == 1) {
                alignAdjusted = Align.LEFT;
            }
            if (alignAdjusted == Align.LEFT) {
                return s + String.format("%" + room + "s", "");
            }
            if (alignAdjusted == Align.RIGHT) {
                return String.format("%" + room + "s", "") + s;
            }
            if (alignAdjusted == Align.CENTER) {
                final int half = room / 2;
                return String.format("%" + half + "s", "") + s + String.format("%" + (room - half) + "s", "");
            }
            throw new IllegalArgumentException("Unsupported alignment");
        }
        
        static String format(final long l, final int width, final Align align) {
            if (align == Align.ZERO_PADDED) {
                return String.format("%0" + width + "d", l);
            }
            return format(Long.toString(l), width, align);
        }
        
        static int calculateWidth(final String caption, final long max) {
            return Math.max(caption.length(), Long.toString(max).length());
        }
    }
}
