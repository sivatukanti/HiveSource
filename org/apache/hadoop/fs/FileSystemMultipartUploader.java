// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs;

import org.apache.hadoop.conf.Configuration;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.function.Function;
import java.util.Comparator;
import org.apache.commons.lang3.tuple.Pair;
import java.util.Iterator;
import java.util.List;
import java.io.Closeable;
import java.io.OutputStream;
import org.apache.commons.compress.utils.IOUtils;
import java.io.InputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import com.google.common.base.Charsets;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class FileSystemMultipartUploader extends MultipartUploader
{
    private final FileSystem fs;
    
    public FileSystemMultipartUploader(final FileSystem fs) {
        this.fs = fs;
    }
    
    @Override
    public UploadHandle initialize(final Path filePath) throws IOException {
        final Path collectorPath = this.createCollectorPath(filePath);
        this.fs.mkdirs(collectorPath, FsPermission.getDirDefault());
        final ByteBuffer byteBuffer = ByteBuffer.wrap(collectorPath.toString().getBytes(Charsets.UTF_8));
        return BBUploadHandle.from(byteBuffer);
    }
    
    @Override
    public PartHandle putPart(final Path filePath, final InputStream inputStream, final int partNumber, final UploadHandle uploadId, final long lengthInBytes) throws IOException {
        final byte[] uploadIdByteArray = uploadId.toByteArray();
        this.checkUploadId(uploadIdByteArray);
        final Path collectorPath = new Path(new String(uploadIdByteArray, 0, uploadIdByteArray.length, Charsets.UTF_8));
        final Path partPath = Path.mergePaths(collectorPath, Path.mergePaths(new Path("/"), new Path(Integer.toString(partNumber) + ".part")));
        try (final FSDataOutputStream fsDataOutputStream = this.fs.createFile(partPath).build()) {
            IOUtils.copy(inputStream, fsDataOutputStream, 4096);
        }
        finally {
            org.apache.hadoop.io.IOUtils.cleanupWithLogger(FileSystemMultipartUploader.LOG, inputStream);
        }
        return BBPartHandle.from(ByteBuffer.wrap(partPath.toString().getBytes(Charsets.UTF_8)));
    }
    
    private Path createCollectorPath(final Path filePath) {
        return Path.mergePaths(filePath.getParent(), Path.mergePaths(new Path(filePath.getName().split("\\.")[0]), Path.mergePaths(new Path("_multipart"), new Path("/"))));
    }
    
    private PathHandle getPathHandle(final Path filePath) throws IOException {
        final FileStatus status = this.fs.getFileStatus(filePath);
        return this.fs.getPathHandle(status, new Options.HandleOpt[0]);
    }
    
    private long totalPartsLen(final List<Path> partHandles) throws IOException {
        long totalLen = 0L;
        for (final Path p : partHandles) {
            totalLen += this.fs.getFileStatus(p).getLen();
        }
        return totalLen;
    }
    
    @Override
    public PathHandle complete(final Path filePath, final List<Pair<Integer, PartHandle>> handles, final UploadHandle multipartUploadId) throws IOException {
        this.checkUploadId(multipartUploadId.toByteArray());
        if (handles.isEmpty()) {
            throw new IOException("Empty upload");
        }
        if (this.fs.exists(filePath)) {
            return this.getPathHandle(filePath);
        }
        handles.sort(Comparator.comparing((Function<? super Pair<Integer, PartHandle>, ? extends Comparable>)Pair::getKey));
        final byte[] byteArray;
        final Path path;
        final List<Path> partHandles = handles.stream().map(pair -> {
            byteArray = pair.getValue().toByteArray();
            new Path(new String(byteArray, 0, byteArray.length, Charsets.UTF_8));
            return path;
        }).collect((Collector<? super Object, ?, List<Path>>)Collectors.toList());
        final Path collectorPath = this.createCollectorPath(filePath);
        final boolean emptyFile = this.totalPartsLen(partHandles) == 0L;
        if (emptyFile) {
            this.fs.create(filePath).close();
        }
        else {
            final Path filePathInsideCollector = Path.mergePaths(collectorPath, new Path("/" + filePath.getName()));
            this.fs.create(filePathInsideCollector).close();
            this.fs.concat(filePathInsideCollector, partHandles.toArray(new Path[handles.size()]));
            this.fs.rename(filePathInsideCollector, filePath, Options.Rename.OVERWRITE);
        }
        this.fs.delete(collectorPath, true);
        return this.getPathHandle(filePath);
    }
    
    @Override
    public void abort(final Path filePath, final UploadHandle uploadId) throws IOException {
        final byte[] uploadIdByteArray = uploadId.toByteArray();
        this.checkUploadId(uploadIdByteArray);
        final Path collectorPath = new Path(new String(uploadIdByteArray, 0, uploadIdByteArray.length, Charsets.UTF_8));
        this.fs.getFileStatus(collectorPath);
        this.fs.delete(collectorPath, true);
    }
    
    public static class Factory extends MultipartUploaderFactory
    {
        @Override
        protected MultipartUploader createMultipartUploader(final FileSystem fs, final Configuration conf) {
            if (fs.getScheme().equals("file")) {
                return new FileSystemMultipartUploader(fs);
            }
            return null;
        }
    }
}
