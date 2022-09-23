// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.nodelabels;

import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.fs.FSDataInputStream;
import java.io.EOFException;
import java.io.InputStream;
import org.apache.hadoop.yarn.proto.YarnServerResourceManagerServiceProtos;
import org.apache.hadoop.yarn.server.api.protocolrecords.RemoveFromClusterNodeLabelsRequest;
import java.util.Iterator;
import com.google.common.collect.Sets;
import org.apache.hadoop.yarn.server.api.protocolrecords.impl.pb.RemoveFromClusterNodeLabelsRequestPBImpl;
import java.util.Collection;
import org.apache.hadoop.yarn.server.api.protocolrecords.AddToClusterNodeLabelsRequest;
import org.apache.hadoop.yarn.server.api.protocolrecords.impl.pb.AddToClusterNodeLabelsRequestPBImpl;
import java.io.OutputStream;
import org.apache.hadoop.yarn.server.api.protocolrecords.ReplaceLabelsOnNodeRequest;
import org.apache.hadoop.yarn.server.api.protocolrecords.impl.pb.ReplaceLabelsOnNodeRequestPBImpl;
import java.util.Set;
import org.apache.hadoop.yarn.api.records.NodeId;
import java.util.Map;
import org.apache.hadoop.fs.LocalFileSystem;
import org.apache.hadoop.conf.Configuration;
import java.io.IOException;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.commons.logging.Log;

public class FileSystemNodeLabelsStore extends NodeLabelsStore
{
    protected static final Log LOG;
    protected static final String DEFAULT_DIR_NAME = "node-labels";
    protected static final String MIRROR_FILENAME = "nodelabel.mirror";
    protected static final String EDITLOG_FILENAME = "nodelabel.editlog";
    Path fsWorkingPath;
    FileSystem fs;
    FSDataOutputStream editlogOs;
    Path editLogPath;
    
    public FileSystemNodeLabelsStore(final CommonNodeLabelsManager mgr) {
        super(mgr);
    }
    
    private String getDefaultFSNodeLabelsRootDir() throws IOException {
        return "file:///tmp/hadoop-yarn-" + UserGroupInformation.getCurrentUser().getShortUserName() + "/" + "node-labels";
    }
    
    @Override
    public void init(final Configuration conf) throws Exception {
        this.fsWorkingPath = new Path(conf.get("yarn.node-labels.fs-store.root-dir", this.getDefaultFSNodeLabelsRootDir()));
        this.setFileSystem(conf);
        this.fs.mkdirs(this.fsWorkingPath);
    }
    
    @Override
    public void close() throws IOException {
        try {
            this.fs.close();
            this.editlogOs.close();
        }
        catch (IOException e) {
            FileSystemNodeLabelsStore.LOG.warn("Exception happened whiling shutting down,", e);
        }
    }
    
    private void setFileSystem(final Configuration conf) throws IOException {
        final Configuration confCopy = new Configuration(conf);
        confCopy.setBoolean("dfs.client.retry.policy.enabled", true);
        final String retryPolicy = confCopy.get("yarn.node-labels.fs-store.retry-policy-spec", "2000, 500");
        confCopy.set("dfs.client.retry.policy.spec", retryPolicy);
        this.fs = this.fsWorkingPath.getFileSystem(confCopy);
        if (this.fs.getScheme().equals("file")) {
            this.fs = ((LocalFileSystem)this.fs).getRaw();
        }
    }
    
    private void ensureAppendEditlogFile() throws IOException {
        this.editlogOs = this.fs.append(this.editLogPath);
    }
    
    private void ensureCloseEditlogFile() throws IOException {
        this.editlogOs.close();
    }
    
    @Override
    public void updateNodeToLabelsMappings(final Map<NodeId, Set<String>> nodeToLabels) throws IOException {
        this.ensureAppendEditlogFile();
        this.editlogOs.writeInt(SerializedLogType.NODE_TO_LABELS.ordinal());
        ((ReplaceLabelsOnNodeRequestPBImpl)ReplaceLabelsOnNodeRequest.newInstance(nodeToLabels)).getProto().writeDelimitedTo(this.editlogOs);
        this.ensureCloseEditlogFile();
    }
    
    @Override
    public void storeNewClusterNodeLabels(final Set<String> labels) throws IOException {
        this.ensureAppendEditlogFile();
        this.editlogOs.writeInt(SerializedLogType.ADD_LABELS.ordinal());
        ((AddToClusterNodeLabelsRequestPBImpl)AddToClusterNodeLabelsRequest.newInstance(labels)).getProto().writeDelimitedTo(this.editlogOs);
        this.ensureCloseEditlogFile();
    }
    
    @Override
    public void removeClusterNodeLabels(final Collection<String> labels) throws IOException {
        this.ensureAppendEditlogFile();
        this.editlogOs.writeInt(SerializedLogType.REMOVE_LABELS.ordinal());
        ((RemoveFromClusterNodeLabelsRequestPBImpl)RemoveFromClusterNodeLabelsRequest.newInstance((Set<String>)Sets.newHashSet((Iterator<?>)labels.iterator()))).getProto().writeDelimitedTo(this.editlogOs);
        this.ensureCloseEditlogFile();
    }
    
    @Override
    public void recover() throws IOException {
        final Path mirrorPath = new Path(this.fsWorkingPath, "nodelabel.mirror");
        final Path oldMirrorPath = new Path(this.fsWorkingPath, "nodelabel.mirror.old");
        FSDataInputStream is = null;
        if (this.fs.exists(mirrorPath)) {
            is = this.fs.open(mirrorPath);
        }
        else if (this.fs.exists(oldMirrorPath)) {
            is = this.fs.open(oldMirrorPath);
        }
        if (null != is) {
            final Set<String> labels = new AddToClusterNodeLabelsRequestPBImpl(YarnServerResourceManagerServiceProtos.AddToClusterNodeLabelsRequestProto.parseDelimitedFrom(is)).getNodeLabels();
            final Map<NodeId, Set<String>> nodeToLabels = new ReplaceLabelsOnNodeRequestPBImpl(YarnServerResourceManagerServiceProtos.ReplaceLabelsOnNodeRequestProto.parseDelimitedFrom(is)).getNodeToLabels();
            this.mgr.addToCluserNodeLabels(labels);
            this.mgr.replaceLabelsOnNode(nodeToLabels);
            is.close();
        }
        this.editLogPath = new Path(this.fsWorkingPath, "nodelabel.editlog");
        if (this.fs.exists(this.editLogPath)) {
            is = this.fs.open(this.editLogPath);
            try {
                while (true) {
                    final SerializedLogType type = SerializedLogType.values()[is.readInt()];
                    switch (type) {
                        case ADD_LABELS: {
                            final Collection<String> labels2 = YarnServerResourceManagerServiceProtos.AddToClusterNodeLabelsRequestProto.parseDelimitedFrom(is).getNodeLabelsList();
                            this.mgr.addToCluserNodeLabels((Set<String>)Sets.newHashSet((Iterator<?>)labels2.iterator()));
                            continue;
                        }
                        case REMOVE_LABELS: {
                            final Collection<String> labels2 = YarnServerResourceManagerServiceProtos.RemoveFromClusterNodeLabelsRequestProto.parseDelimitedFrom(is).getNodeLabelsList();
                            this.mgr.removeFromClusterNodeLabels(labels2);
                            continue;
                        }
                        case NODE_TO_LABELS: {
                            final Map<NodeId, Set<String>> map = new ReplaceLabelsOnNodeRequestPBImpl(YarnServerResourceManagerServiceProtos.ReplaceLabelsOnNodeRequestProto.parseDelimitedFrom(is)).getNodeToLabels();
                            this.mgr.replaceLabelsOnNode(map);
                            continue;
                        }
                    }
                }
            }
            catch (EOFException e) {}
        }
        final Path writingMirrorPath = new Path(this.fsWorkingPath, "nodelabel.mirror.writing");
        final FSDataOutputStream os = this.fs.create(writingMirrorPath, true);
        ((AddToClusterNodeLabelsRequestPBImpl)AddToClusterNodeLabelsRequest.newInstance(this.mgr.getClusterNodeLabels())).getProto().writeDelimitedTo(os);
        ((ReplaceLabelsOnNodeRequestPBImpl)ReplaceLabelsOnNodeRequest.newInstance(this.mgr.getNodeLabels())).getProto().writeDelimitedTo(os);
        os.close();
        if (this.fs.exists(mirrorPath)) {
            this.fs.delete(oldMirrorPath, false);
            this.fs.rename(mirrorPath, oldMirrorPath);
        }
        this.fs.rename(writingMirrorPath, mirrorPath);
        this.fs.delete(writingMirrorPath, false);
        this.fs.delete(oldMirrorPath, false);
        (this.editlogOs = this.fs.create(this.editLogPath, true)).close();
        FileSystemNodeLabelsStore.LOG.info("Finished write mirror at:" + mirrorPath.toString());
        FileSystemNodeLabelsStore.LOG.info("Finished create editlog file at:" + this.editLogPath.toString());
    }
    
    static {
        LOG = LogFactory.getLog(FileSystemNodeLabelsStore.class);
    }
    
    protected enum SerializedLogType
    {
        ADD_LABELS, 
        NODE_TO_LABELS, 
        REMOVE_LABELS;
    }
}
