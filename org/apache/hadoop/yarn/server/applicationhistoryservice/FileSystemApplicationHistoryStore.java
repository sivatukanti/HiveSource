// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.server.applicationhistoryservice;

import org.apache.hadoop.io.Writable;
import java.io.DataOutputStream;
import java.io.DataOutput;
import org.apache.hadoop.fs.FSDataOutputStream;
import java.io.DataInputStream;
import java.io.DataInput;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.io.file.tfile.TFile;
import org.apache.commons.logging.LogFactory;
import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.hadoop.yarn.proto.ApplicationHistoryServerProtos;
import org.apache.hadoop.yarn.server.applicationhistoryservice.records.impl.pb.ContainerFinishDataPBImpl;
import org.apache.hadoop.yarn.server.applicationhistoryservice.records.impl.pb.ContainerStartDataPBImpl;
import org.apache.hadoop.yarn.server.applicationhistoryservice.records.impl.pb.ApplicationAttemptFinishDataPBImpl;
import org.apache.hadoop.yarn.server.applicationhistoryservice.records.impl.pb.ApplicationAttemptStartDataPBImpl;
import org.apache.hadoop.yarn.server.applicationhistoryservice.records.impl.pb.ApplicationFinishDataPBImpl;
import org.apache.hadoop.yarn.server.applicationhistoryservice.records.impl.pb.ApplicationStartDataPBImpl;
import org.apache.hadoop.yarn.server.applicationhistoryservice.records.ContainerFinishData;
import org.apache.hadoop.yarn.server.applicationhistoryservice.records.ContainerStartData;
import org.apache.hadoop.yarn.api.records.ContainerState;
import org.apache.hadoop.yarn.api.records.Priority;
import org.apache.hadoop.yarn.api.records.NodeId;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.server.applicationhistoryservice.records.ContainerHistoryData;
import org.apache.hadoop.yarn.server.applicationhistoryservice.records.ApplicationAttemptFinishData;
import org.apache.hadoop.yarn.server.applicationhistoryservice.records.ApplicationAttemptStartData;
import org.apache.hadoop.yarn.api.records.YarnApplicationAttemptState;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.server.applicationhistoryservice.records.ApplicationAttemptHistoryData;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.yarn.util.ConverterUtils;
import java.util.HashMap;
import org.apache.hadoop.yarn.server.applicationhistoryservice.records.ApplicationFinishData;
import org.apache.hadoop.yarn.server.applicationhistoryservice.records.ApplicationStartData;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.hadoop.yarn.api.records.FinalApplicationStatus;
import org.apache.hadoop.yarn.server.applicationhistoryservice.records.ApplicationHistoryData;
import java.util.Iterator;
import org.apache.hadoop.io.IOUtils;
import java.io.Closeable;
import java.util.Map;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import java.util.concurrent.ConcurrentMap;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.commons.logging.Log;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.service.AbstractService;

@InterfaceAudience.Public
@InterfaceStability.Unstable
public class FileSystemApplicationHistoryStore extends AbstractService implements ApplicationHistoryStore
{
    private static final Log LOG;
    private static final String ROOT_DIR_NAME = "ApplicationHistoryDataRoot";
    private static final int MIN_BLOCK_SIZE = 262144;
    private static final String START_DATA_SUFFIX = "_start";
    private static final String FINISH_DATA_SUFFIX = "_finish";
    private static final FsPermission ROOT_DIR_UMASK;
    private static final FsPermission HISTORY_FILE_UMASK;
    private FileSystem fs;
    private Path rootDirPath;
    private ConcurrentMap<ApplicationId, HistoryFileWriter> outstandingWriters;
    
    public FileSystemApplicationHistoryStore() {
        super(FileSystemApplicationHistoryStore.class.getName());
        this.outstandingWriters = new ConcurrentHashMap<ApplicationId, HistoryFileWriter>();
    }
    
    protected FileSystem getFileSystem(final Path path, final Configuration conf) throws Exception {
        return path.getFileSystem(conf);
    }
    
    public void serviceStart() throws Exception {
        final Configuration conf = this.getConfig();
        final Path fsWorkingPath = new Path(conf.get("yarn.timeline-service.generic-application-history.fs-history-store.uri", conf.get("hadoop.tmp.dir") + "/yarn/timeline/generic-history"));
        this.rootDirPath = new Path(fsWorkingPath, "ApplicationHistoryDataRoot");
        try {
            this.fs = this.getFileSystem(fsWorkingPath, conf);
            if (!this.fs.isDirectory(this.rootDirPath)) {
                this.fs.mkdirs(this.rootDirPath);
                this.fs.setPermission(this.rootDirPath, FileSystemApplicationHistoryStore.ROOT_DIR_UMASK);
            }
        }
        catch (IOException e) {
            FileSystemApplicationHistoryStore.LOG.error("Error when initializing FileSystemHistoryStorage", e);
            throw e;
        }
        super.serviceStart();
    }
    
    public void serviceStop() throws Exception {
        try {
            for (final Map.Entry<ApplicationId, HistoryFileWriter> entry : this.outstandingWriters.entrySet()) {
                entry.getValue().close();
            }
            this.outstandingWriters.clear();
        }
        finally {
            IOUtils.cleanup(FileSystemApplicationHistoryStore.LOG, this.fs);
        }
        super.serviceStop();
    }
    
    @Override
    public ApplicationHistoryData getApplication(final ApplicationId appId) throws IOException {
        final HistoryFileReader hfReader = this.getHistoryFileReader(appId);
        try {
            boolean readStartData = false;
            boolean readFinishData = false;
            final ApplicationHistoryData historyData = ApplicationHistoryData.newInstance(appId, null, null, null, null, Long.MIN_VALUE, Long.MIN_VALUE, Long.MAX_VALUE, null, FinalApplicationStatus.UNDEFINED, null);
            while ((!readStartData || !readFinishData) && hfReader.hasNext()) {
                final HistoryFileReader.Entry entry = hfReader.next();
                if (entry.key.id.equals(appId.toString())) {
                    if (entry.key.suffix.equals("_start")) {
                        final ApplicationStartData startData = parseApplicationStartData(entry.value);
                        mergeApplicationHistoryData(historyData, startData);
                        readStartData = true;
                    }
                    else {
                        if (!entry.key.suffix.equals("_finish")) {
                            continue;
                        }
                        final ApplicationFinishData finishData = parseApplicationFinishData(entry.value);
                        mergeApplicationHistoryData(historyData, finishData);
                        readFinishData = true;
                    }
                }
            }
            if (!readStartData && !readFinishData) {
                return null;
            }
            if (!readStartData) {
                FileSystemApplicationHistoryStore.LOG.warn("Start information is missing for application " + appId);
            }
            if (!readFinishData) {
                FileSystemApplicationHistoryStore.LOG.warn("Finish information is missing for application " + appId);
            }
            FileSystemApplicationHistoryStore.LOG.info("Completed reading history information of application " + appId);
            return historyData;
        }
        catch (IOException e) {
            FileSystemApplicationHistoryStore.LOG.error("Error when reading history file of application " + appId, e);
            throw e;
        }
        finally {
            hfReader.close();
        }
    }
    
    @Override
    public Map<ApplicationId, ApplicationHistoryData> getAllApplications() throws IOException {
        final Map<ApplicationId, ApplicationHistoryData> historyDataMap = new HashMap<ApplicationId, ApplicationHistoryData>();
        final FileStatus[] arr$;
        final FileStatus[] files = arr$ = this.fs.listStatus(this.rootDirPath);
        for (final FileStatus file : arr$) {
            final ApplicationId appId = ConverterUtils.toApplicationId(file.getPath().getName());
            try {
                final ApplicationHistoryData historyData = this.getApplication(appId);
                if (historyData != null) {
                    historyDataMap.put(appId, historyData);
                }
            }
            catch (IOException e) {
                FileSystemApplicationHistoryStore.LOG.error("History information of application " + appId + " is not included into the result due to the exception", e);
            }
        }
        return historyDataMap;
    }
    
    @Override
    public Map<ApplicationAttemptId, ApplicationAttemptHistoryData> getApplicationAttempts(final ApplicationId appId) throws IOException {
        final Map<ApplicationAttemptId, ApplicationAttemptHistoryData> historyDataMap = new HashMap<ApplicationAttemptId, ApplicationAttemptHistoryData>();
        final HistoryFileReader hfReader = this.getHistoryFileReader(appId);
        try {
            while (hfReader.hasNext()) {
                final HistoryFileReader.Entry entry = hfReader.next();
                if (entry.key.id.startsWith("appattempt")) {
                    final ApplicationAttemptId appAttemptId = ConverterUtils.toApplicationAttemptId(entry.key.id);
                    if (!appAttemptId.getApplicationId().equals(appId)) {
                        continue;
                    }
                    ApplicationAttemptHistoryData historyData = historyDataMap.get(appAttemptId);
                    if (historyData == null) {
                        historyData = ApplicationAttemptHistoryData.newInstance(appAttemptId, null, -1, null, null, null, FinalApplicationStatus.UNDEFINED, null);
                        historyDataMap.put(appAttemptId, historyData);
                    }
                    if (entry.key.suffix.equals("_start")) {
                        mergeApplicationAttemptHistoryData(historyData, parseApplicationAttemptStartData(entry.value));
                    }
                    else {
                        if (!entry.key.suffix.equals("_finish")) {
                            continue;
                        }
                        mergeApplicationAttemptHistoryData(historyData, parseApplicationAttemptFinishData(entry.value));
                    }
                }
            }
            FileSystemApplicationHistoryStore.LOG.info("Completed reading history information of all application attempts of application " + appId);
        }
        catch (IOException e) {
            FileSystemApplicationHistoryStore.LOG.info("Error when reading history information of some application attempts of application " + appId);
        }
        finally {
            hfReader.close();
        }
        return historyDataMap;
    }
    
    @Override
    public ApplicationAttemptHistoryData getApplicationAttempt(final ApplicationAttemptId appAttemptId) throws IOException {
        final HistoryFileReader hfReader = this.getHistoryFileReader(appAttemptId.getApplicationId());
        try {
            boolean readStartData = false;
            boolean readFinishData = false;
            final ApplicationAttemptHistoryData historyData = ApplicationAttemptHistoryData.newInstance(appAttemptId, null, -1, null, null, null, FinalApplicationStatus.UNDEFINED, null);
            while ((!readStartData || !readFinishData) && hfReader.hasNext()) {
                final HistoryFileReader.Entry entry = hfReader.next();
                if (entry.key.id.equals(appAttemptId.toString())) {
                    if (entry.key.suffix.equals("_start")) {
                        final ApplicationAttemptStartData startData = parseApplicationAttemptStartData(entry.value);
                        mergeApplicationAttemptHistoryData(historyData, startData);
                        readStartData = true;
                    }
                    else {
                        if (!entry.key.suffix.equals("_finish")) {
                            continue;
                        }
                        final ApplicationAttemptFinishData finishData = parseApplicationAttemptFinishData(entry.value);
                        mergeApplicationAttemptHistoryData(historyData, finishData);
                        readFinishData = true;
                    }
                }
            }
            if (!readStartData && !readFinishData) {
                return null;
            }
            if (!readStartData) {
                FileSystemApplicationHistoryStore.LOG.warn("Start information is missing for application attempt " + appAttemptId);
            }
            if (!readFinishData) {
                FileSystemApplicationHistoryStore.LOG.warn("Finish information is missing for application attempt " + appAttemptId);
            }
            FileSystemApplicationHistoryStore.LOG.info("Completed reading history information of application attempt " + appAttemptId);
            return historyData;
        }
        catch (IOException e) {
            FileSystemApplicationHistoryStore.LOG.error("Error when reading history file of application attempt" + appAttemptId, e);
            throw e;
        }
        finally {
            hfReader.close();
        }
    }
    
    @Override
    public ContainerHistoryData getContainer(final ContainerId containerId) throws IOException {
        final HistoryFileReader hfReader = this.getHistoryFileReader(containerId.getApplicationAttemptId().getApplicationId());
        try {
            boolean readStartData = false;
            boolean readFinishData = false;
            final ContainerHistoryData historyData = ContainerHistoryData.newInstance(containerId, null, null, null, Long.MIN_VALUE, Long.MAX_VALUE, null, Integer.MAX_VALUE, null);
            while ((!readStartData || !readFinishData) && hfReader.hasNext()) {
                final HistoryFileReader.Entry entry = hfReader.next();
                if (entry.key.id.equals(containerId.toString())) {
                    if (entry.key.suffix.equals("_start")) {
                        final ContainerStartData startData = parseContainerStartData(entry.value);
                        mergeContainerHistoryData(historyData, startData);
                        readStartData = true;
                    }
                    else {
                        if (!entry.key.suffix.equals("_finish")) {
                            continue;
                        }
                        final ContainerFinishData finishData = parseContainerFinishData(entry.value);
                        mergeContainerHistoryData(historyData, finishData);
                        readFinishData = true;
                    }
                }
            }
            if (!readStartData && !readFinishData) {
                return null;
            }
            if (!readStartData) {
                FileSystemApplicationHistoryStore.LOG.warn("Start information is missing for container " + containerId);
            }
            if (!readFinishData) {
                FileSystemApplicationHistoryStore.LOG.warn("Finish information is missing for container " + containerId);
            }
            FileSystemApplicationHistoryStore.LOG.info("Completed reading history information of container " + containerId);
            return historyData;
        }
        catch (IOException e) {
            FileSystemApplicationHistoryStore.LOG.error("Error when reading history file of container " + containerId, e);
            throw e;
        }
        finally {
            hfReader.close();
        }
    }
    
    @Override
    public ContainerHistoryData getAMContainer(final ApplicationAttemptId appAttemptId) throws IOException {
        final ApplicationAttemptHistoryData attemptHistoryData = this.getApplicationAttempt(appAttemptId);
        if (attemptHistoryData == null || attemptHistoryData.getMasterContainerId() == null) {
            return null;
        }
        return this.getContainer(attemptHistoryData.getMasterContainerId());
    }
    
    @Override
    public Map<ContainerId, ContainerHistoryData> getContainers(final ApplicationAttemptId appAttemptId) throws IOException {
        final Map<ContainerId, ContainerHistoryData> historyDataMap = new HashMap<ContainerId, ContainerHistoryData>();
        final HistoryFileReader hfReader = this.getHistoryFileReader(appAttemptId.getApplicationId());
        try {
            while (hfReader.hasNext()) {
                final HistoryFileReader.Entry entry = hfReader.next();
                if (entry.key.id.startsWith("container")) {
                    final ContainerId containerId = ConverterUtils.toContainerId(entry.key.id);
                    if (!containerId.getApplicationAttemptId().equals(appAttemptId)) {
                        continue;
                    }
                    ContainerHistoryData historyData = historyDataMap.get(containerId);
                    if (historyData == null) {
                        historyData = ContainerHistoryData.newInstance(containerId, null, null, null, Long.MIN_VALUE, Long.MAX_VALUE, null, Integer.MAX_VALUE, null);
                        historyDataMap.put(containerId, historyData);
                    }
                    if (entry.key.suffix.equals("_start")) {
                        mergeContainerHistoryData(historyData, parseContainerStartData(entry.value));
                    }
                    else {
                        if (!entry.key.suffix.equals("_finish")) {
                            continue;
                        }
                        mergeContainerHistoryData(historyData, parseContainerFinishData(entry.value));
                    }
                }
            }
            FileSystemApplicationHistoryStore.LOG.info("Completed reading history information of all conatiners of application attempt " + appAttemptId);
        }
        catch (IOException e) {
            FileSystemApplicationHistoryStore.LOG.info("Error when reading history information of some containers of application attempt " + appAttemptId);
        }
        finally {
            hfReader.close();
        }
        return historyDataMap;
    }
    
    @Override
    public void applicationStarted(final ApplicationStartData appStart) throws IOException {
        HistoryFileWriter hfWriter = this.outstandingWriters.get(appStart.getApplicationId());
        if (hfWriter != null) {
            throw new IOException("History file of application " + appStart.getApplicationId() + " is already opened");
        }
        final Path applicationHistoryFile = new Path(this.rootDirPath, appStart.getApplicationId().toString());
        try {
            hfWriter = new HistoryFileWriter(applicationHistoryFile);
            FileSystemApplicationHistoryStore.LOG.info("Opened history file of application " + appStart.getApplicationId());
        }
        catch (IOException e) {
            FileSystemApplicationHistoryStore.LOG.error("Error when openning history file of application " + appStart.getApplicationId(), e);
            throw e;
        }
        this.outstandingWriters.put(appStart.getApplicationId(), hfWriter);
        assert appStart instanceof ApplicationStartDataPBImpl;
        try {
            hfWriter.writeHistoryData(new HistoryDataKey(appStart.getApplicationId().toString(), "_start"), ((ApplicationStartDataPBImpl)appStart).getProto().toByteArray());
            FileSystemApplicationHistoryStore.LOG.info("Start information of application " + appStart.getApplicationId() + " is written");
        }
        catch (IOException e2) {
            FileSystemApplicationHistoryStore.LOG.error("Error when writing start information of application " + appStart.getApplicationId(), e2);
            throw e2;
        }
    }
    
    @Override
    public void applicationFinished(final ApplicationFinishData appFinish) throws IOException {
        final HistoryFileWriter hfWriter = this.getHistoryFileWriter(appFinish.getApplicationId());
        assert appFinish instanceof ApplicationFinishDataPBImpl;
        try {
            hfWriter.writeHistoryData(new HistoryDataKey(appFinish.getApplicationId().toString(), "_finish"), ((ApplicationFinishDataPBImpl)appFinish).getProto().toByteArray());
            FileSystemApplicationHistoryStore.LOG.info("Finish information of application " + appFinish.getApplicationId() + " is written");
        }
        catch (IOException e) {
            FileSystemApplicationHistoryStore.LOG.error("Error when writing finish information of application " + appFinish.getApplicationId(), e);
            throw e;
        }
        finally {
            hfWriter.close();
            this.outstandingWriters.remove(appFinish.getApplicationId());
        }
    }
    
    @Override
    public void applicationAttemptStarted(final ApplicationAttemptStartData appAttemptStart) throws IOException {
        final HistoryFileWriter hfWriter = this.getHistoryFileWriter(appAttemptStart.getApplicationAttemptId().getApplicationId());
        assert appAttemptStart instanceof ApplicationAttemptStartDataPBImpl;
        try {
            hfWriter.writeHistoryData(new HistoryDataKey(appAttemptStart.getApplicationAttemptId().toString(), "_start"), ((ApplicationAttemptStartDataPBImpl)appAttemptStart).getProto().toByteArray());
            FileSystemApplicationHistoryStore.LOG.info("Start information of application attempt " + appAttemptStart.getApplicationAttemptId() + " is written");
        }
        catch (IOException e) {
            FileSystemApplicationHistoryStore.LOG.error("Error when writing start information of application attempt " + appAttemptStart.getApplicationAttemptId(), e);
            throw e;
        }
    }
    
    @Override
    public void applicationAttemptFinished(final ApplicationAttemptFinishData appAttemptFinish) throws IOException {
        final HistoryFileWriter hfWriter = this.getHistoryFileWriter(appAttemptFinish.getApplicationAttemptId().getApplicationId());
        assert appAttemptFinish instanceof ApplicationAttemptFinishDataPBImpl;
        try {
            hfWriter.writeHistoryData(new HistoryDataKey(appAttemptFinish.getApplicationAttemptId().toString(), "_finish"), ((ApplicationAttemptFinishDataPBImpl)appAttemptFinish).getProto().toByteArray());
            FileSystemApplicationHistoryStore.LOG.info("Finish information of application attempt " + appAttemptFinish.getApplicationAttemptId() + " is written");
        }
        catch (IOException e) {
            FileSystemApplicationHistoryStore.LOG.error("Error when writing finish information of application attempt " + appAttemptFinish.getApplicationAttemptId(), e);
            throw e;
        }
    }
    
    @Override
    public void containerStarted(final ContainerStartData containerStart) throws IOException {
        final HistoryFileWriter hfWriter = this.getHistoryFileWriter(containerStart.getContainerId().getApplicationAttemptId().getApplicationId());
        assert containerStart instanceof ContainerStartDataPBImpl;
        try {
            hfWriter.writeHistoryData(new HistoryDataKey(containerStart.getContainerId().toString(), "_start"), ((ContainerStartDataPBImpl)containerStart).getProto().toByteArray());
            FileSystemApplicationHistoryStore.LOG.info("Start information of container " + containerStart.getContainerId() + " is written");
        }
        catch (IOException e) {
            FileSystemApplicationHistoryStore.LOG.error("Error when writing start information of container " + containerStart.getContainerId(), e);
            throw e;
        }
    }
    
    @Override
    public void containerFinished(final ContainerFinishData containerFinish) throws IOException {
        final HistoryFileWriter hfWriter = this.getHistoryFileWriter(containerFinish.getContainerId().getApplicationAttemptId().getApplicationId());
        assert containerFinish instanceof ContainerFinishDataPBImpl;
        try {
            hfWriter.writeHistoryData(new HistoryDataKey(containerFinish.getContainerId().toString(), "_finish"), ((ContainerFinishDataPBImpl)containerFinish).getProto().toByteArray());
            FileSystemApplicationHistoryStore.LOG.info("Finish information of container " + containerFinish.getContainerId() + " is written");
        }
        catch (IOException e) {
            FileSystemApplicationHistoryStore.LOG.error("Error when writing finish information of container " + containerFinish.getContainerId(), e);
        }
    }
    
    private static ApplicationStartData parseApplicationStartData(final byte[] value) throws InvalidProtocolBufferException {
        return new ApplicationStartDataPBImpl(ApplicationHistoryServerProtos.ApplicationStartDataProto.parseFrom(value));
    }
    
    private static ApplicationFinishData parseApplicationFinishData(final byte[] value) throws InvalidProtocolBufferException {
        return new ApplicationFinishDataPBImpl(ApplicationHistoryServerProtos.ApplicationFinishDataProto.parseFrom(value));
    }
    
    private static ApplicationAttemptStartData parseApplicationAttemptStartData(final byte[] value) throws InvalidProtocolBufferException {
        return new ApplicationAttemptStartDataPBImpl(ApplicationHistoryServerProtos.ApplicationAttemptStartDataProto.parseFrom(value));
    }
    
    private static ApplicationAttemptFinishData parseApplicationAttemptFinishData(final byte[] value) throws InvalidProtocolBufferException {
        return new ApplicationAttemptFinishDataPBImpl(ApplicationHistoryServerProtos.ApplicationAttemptFinishDataProto.parseFrom(value));
    }
    
    private static ContainerStartData parseContainerStartData(final byte[] value) throws InvalidProtocolBufferException {
        return new ContainerStartDataPBImpl(ApplicationHistoryServerProtos.ContainerStartDataProto.parseFrom(value));
    }
    
    private static ContainerFinishData parseContainerFinishData(final byte[] value) throws InvalidProtocolBufferException {
        return new ContainerFinishDataPBImpl(ApplicationHistoryServerProtos.ContainerFinishDataProto.parseFrom(value));
    }
    
    private static void mergeApplicationHistoryData(final ApplicationHistoryData historyData, final ApplicationStartData startData) {
        historyData.setApplicationName(startData.getApplicationName());
        historyData.setApplicationType(startData.getApplicationType());
        historyData.setQueue(startData.getQueue());
        historyData.setUser(startData.getUser());
        historyData.setSubmitTime(startData.getSubmitTime());
        historyData.setStartTime(startData.getStartTime());
    }
    
    private static void mergeApplicationHistoryData(final ApplicationHistoryData historyData, final ApplicationFinishData finishData) {
        historyData.setFinishTime(finishData.getFinishTime());
        historyData.setDiagnosticsInfo(finishData.getDiagnosticsInfo());
        historyData.setFinalApplicationStatus(finishData.getFinalApplicationStatus());
        historyData.setYarnApplicationState(finishData.getYarnApplicationState());
    }
    
    private static void mergeApplicationAttemptHistoryData(final ApplicationAttemptHistoryData historyData, final ApplicationAttemptStartData startData) {
        historyData.setHost(startData.getHost());
        historyData.setRPCPort(startData.getRPCPort());
        historyData.setMasterContainerId(startData.getMasterContainerId());
    }
    
    private static void mergeApplicationAttemptHistoryData(final ApplicationAttemptHistoryData historyData, final ApplicationAttemptFinishData finishData) {
        historyData.setDiagnosticsInfo(finishData.getDiagnosticsInfo());
        historyData.setTrackingURL(finishData.getTrackingURL());
        historyData.setFinalApplicationStatus(finishData.getFinalApplicationStatus());
        historyData.setYarnApplicationAttemptState(finishData.getYarnApplicationAttemptState());
    }
    
    private static void mergeContainerHistoryData(final ContainerHistoryData historyData, final ContainerStartData startData) {
        historyData.setAllocatedResource(startData.getAllocatedResource());
        historyData.setAssignedNode(startData.getAssignedNode());
        historyData.setPriority(startData.getPriority());
        historyData.setStartTime(startData.getStartTime());
    }
    
    private static void mergeContainerHistoryData(final ContainerHistoryData historyData, final ContainerFinishData finishData) {
        historyData.setFinishTime(finishData.getFinishTime());
        historyData.setDiagnosticsInfo(finishData.getDiagnosticsInfo());
        historyData.setContainerExitStatus(finishData.getContainerExitStatus());
        historyData.setContainerState(finishData.getContainerState());
    }
    
    private HistoryFileWriter getHistoryFileWriter(final ApplicationId appId) throws IOException {
        final HistoryFileWriter hfWriter = this.outstandingWriters.get(appId);
        if (hfWriter == null) {
            throw new IOException("History file of application " + appId + " is not opened");
        }
        return hfWriter;
    }
    
    private HistoryFileReader getHistoryFileReader(final ApplicationId appId) throws IOException {
        final Path applicationHistoryFile = new Path(this.rootDirPath, appId.toString());
        if (!this.fs.exists(applicationHistoryFile)) {
            throw new IOException("History file for application " + appId + " is not found");
        }
        if (this.outstandingWriters.containsKey(appId)) {
            throw new IOException("History file for application " + appId + " is under writing");
        }
        return new HistoryFileReader(applicationHistoryFile);
    }
    
    static {
        LOG = LogFactory.getLog(FileSystemApplicationHistoryStore.class);
        ROOT_DIR_UMASK = FsPermission.createImmutable((short)480);
        HISTORY_FILE_UMASK = FsPermission.createImmutable((short)416);
    }
    
    private class HistoryFileReader
    {
        private TFile.Reader reader;
        private TFile.Reader.Scanner scanner;
        FSDataInputStream fsdis;
        
        public HistoryFileReader(final Path historyFile) throws IOException {
            this.fsdis = FileSystemApplicationHistoryStore.this.fs.open(historyFile);
            this.reader = new TFile.Reader(this.fsdis, FileSystemApplicationHistoryStore.this.fs.getFileStatus(historyFile).getLen(), FileSystemApplicationHistoryStore.this.getConfig());
            this.reset();
        }
        
        public boolean hasNext() {
            return !this.scanner.atEnd();
        }
        
        public Entry next() throws IOException {
            final TFile.Reader.Scanner.Entry entry = this.scanner.entry();
            DataInputStream dis = entry.getKeyStream();
            final HistoryDataKey key = new HistoryDataKey();
            key.readFields(dis);
            dis = entry.getValueStream();
            final byte[] value = new byte[entry.getValueLength()];
            dis.read(value);
            this.scanner.advance();
            return new Entry(key, value);
        }
        
        public void reset() throws IOException {
            IOUtils.cleanup(FileSystemApplicationHistoryStore.LOG, this.scanner);
            this.scanner = this.reader.createScanner();
        }
        
        public void close() {
            IOUtils.cleanup(FileSystemApplicationHistoryStore.LOG, this.scanner, this.reader, this.fsdis);
        }
        
        private class Entry
        {
            private HistoryDataKey key;
            private byte[] value;
            
            public Entry(final HistoryDataKey key, final byte[] value) {
                this.key = key;
                this.value = value;
            }
        }
    }
    
    private class HistoryFileWriter
    {
        private FSDataOutputStream fsdos;
        private TFile.Writer writer;
        
        public HistoryFileWriter(final Path historyFile) throws IOException {
            if (FileSystemApplicationHistoryStore.this.fs.exists(historyFile)) {
                this.fsdos = FileSystemApplicationHistoryStore.this.fs.append(historyFile);
            }
            else {
                this.fsdos = FileSystemApplicationHistoryStore.this.fs.create(historyFile);
            }
            FileSystemApplicationHistoryStore.this.fs.setPermission(historyFile, FileSystemApplicationHistoryStore.HISTORY_FILE_UMASK);
            this.writer = new TFile.Writer(this.fsdos, 262144, FileSystemApplicationHistoryStore.this.getConfig().get("yarn.timeline-service.generic-application-history.fs-history-store.compression-type", "none"), null, FileSystemApplicationHistoryStore.this.getConfig());
        }
        
        public synchronized void close() {
            IOUtils.cleanup(FileSystemApplicationHistoryStore.LOG, this.writer, this.fsdos);
        }
        
        public synchronized void writeHistoryData(final HistoryDataKey key, final byte[] value) throws IOException {
            DataOutputStream dos = null;
            try {
                dos = this.writer.prepareAppendKey(-1);
                key.write(dos);
            }
            finally {
                IOUtils.cleanup(FileSystemApplicationHistoryStore.LOG, dos);
            }
            try {
                dos = this.writer.prepareAppendValue(value.length);
                dos.write(value);
            }
            finally {
                IOUtils.cleanup(FileSystemApplicationHistoryStore.LOG, dos);
            }
        }
    }
    
    private static class HistoryDataKey implements Writable
    {
        private String id;
        private String suffix;
        
        public HistoryDataKey() {
            this(null, null);
        }
        
        public HistoryDataKey(final String id, final String suffix) {
            this.id = id;
            this.suffix = suffix;
        }
        
        @Override
        public void write(final DataOutput out) throws IOException {
            out.writeUTF(this.id);
            out.writeUTF(this.suffix);
        }
        
        @Override
        public void readFields(final DataInput in) throws IOException {
            this.id = in.readUTF();
            this.suffix = in.readUTF();
        }
    }
}
