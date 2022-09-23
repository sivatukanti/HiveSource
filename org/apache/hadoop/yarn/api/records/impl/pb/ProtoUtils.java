// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.yarn.api.records.impl.pb;

import org.apache.hadoop.yarn.api.records.ReservationRequestInterpreter;
import org.apache.hadoop.yarn.api.records.ApplicationAccessType;
import org.apache.hadoop.yarn.api.records.QueueACL;
import org.apache.hadoop.yarn.api.records.QueueState;
import java.nio.ByteBuffer;
import com.google.protobuf.ByteString;
import org.apache.hadoop.yarn.api.records.AMCommand;
import org.apache.hadoop.yarn.api.records.LocalResourceVisibility;
import org.apache.hadoop.yarn.api.records.LocalResourceType;
import org.apache.hadoop.yarn.api.records.FinalApplicationStatus;
import org.apache.hadoop.yarn.api.records.ApplicationResourceUsageReport;
import org.apache.hadoop.yarn.proto.YarnServiceProtos;
import org.apache.hadoop.yarn.api.protocolrecords.ApplicationsRequestScope;
import org.apache.hadoop.yarn.api.records.YarnApplicationAttemptState;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.hadoop.yarn.api.records.NodeId;
import org.apache.hadoop.yarn.api.records.NodeState;
import org.apache.hadoop.yarn.proto.YarnProtos;
import org.apache.hadoop.yarn.api.records.ContainerState;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Unstable
public class ProtoUtils
{
    private static String CONTAINER_STATE_PREFIX;
    private static String NODE_STATE_PREFIX;
    private static String YARN_APPLICATION_ATTEMPT_STATE_PREFIX;
    private static String FINAL_APPLICATION_STATUS_PREFIX;
    private static String QUEUE_STATE_PREFIX;
    private static String QUEUE_ACL_PREFIX;
    private static String APP_ACCESS_TYPE_PREFIX;
    
    public static YarnProtos.ContainerStateProto convertToProtoFormat(final ContainerState e) {
        return YarnProtos.ContainerStateProto.valueOf(ProtoUtils.CONTAINER_STATE_PREFIX + e.name());
    }
    
    public static ContainerState convertFromProtoFormat(final YarnProtos.ContainerStateProto e) {
        return ContainerState.valueOf(e.name().replace(ProtoUtils.CONTAINER_STATE_PREFIX, ""));
    }
    
    public static YarnProtos.NodeStateProto convertToProtoFormat(final NodeState e) {
        return YarnProtos.NodeStateProto.valueOf(ProtoUtils.NODE_STATE_PREFIX + e.name());
    }
    
    public static NodeState convertFromProtoFormat(final YarnProtos.NodeStateProto e) {
        return NodeState.valueOf(e.name().replace(ProtoUtils.NODE_STATE_PREFIX, ""));
    }
    
    public static YarnProtos.NodeIdProto convertToProtoFormat(final NodeId e) {
        return ((NodeIdPBImpl)e).getProto();
    }
    
    public static NodeId convertFromProtoFormat(final YarnProtos.NodeIdProto e) {
        return new NodeIdPBImpl(e);
    }
    
    public static YarnProtos.YarnApplicationStateProto convertToProtoFormat(final YarnApplicationState e) {
        return YarnProtos.YarnApplicationStateProto.valueOf(e.name());
    }
    
    public static YarnApplicationState convertFromProtoFormat(final YarnProtos.YarnApplicationStateProto e) {
        return YarnApplicationState.valueOf(e.name());
    }
    
    public static YarnProtos.YarnApplicationAttemptStateProto convertToProtoFormat(final YarnApplicationAttemptState e) {
        return YarnProtos.YarnApplicationAttemptStateProto.valueOf(ProtoUtils.YARN_APPLICATION_ATTEMPT_STATE_PREFIX + e.name());
    }
    
    public static YarnApplicationAttemptState convertFromProtoFormat(final YarnProtos.YarnApplicationAttemptStateProto e) {
        return YarnApplicationAttemptState.valueOf(e.name().replace(ProtoUtils.YARN_APPLICATION_ATTEMPT_STATE_PREFIX, ""));
    }
    
    public static YarnServiceProtos.ApplicationsRequestScopeProto convertToProtoFormat(final ApplicationsRequestScope e) {
        return YarnServiceProtos.ApplicationsRequestScopeProto.valueOf(e.name());
    }
    
    public static ApplicationsRequestScope convertFromProtoFormat(final YarnServiceProtos.ApplicationsRequestScopeProto e) {
        return ApplicationsRequestScope.valueOf(e.name());
    }
    
    public static YarnProtos.ApplicationResourceUsageReportProto convertToProtoFormat(final ApplicationResourceUsageReport e) {
        return ((ApplicationResourceUsageReportPBImpl)e).getProto();
    }
    
    public static ApplicationResourceUsageReport convertFromProtoFormat(final YarnProtos.ApplicationResourceUsageReportProto e) {
        return new ApplicationResourceUsageReportPBImpl(e);
    }
    
    public static YarnProtos.FinalApplicationStatusProto convertToProtoFormat(final FinalApplicationStatus e) {
        return YarnProtos.FinalApplicationStatusProto.valueOf(ProtoUtils.FINAL_APPLICATION_STATUS_PREFIX + e.name());
    }
    
    public static FinalApplicationStatus convertFromProtoFormat(final YarnProtos.FinalApplicationStatusProto e) {
        return FinalApplicationStatus.valueOf(e.name().replace(ProtoUtils.FINAL_APPLICATION_STATUS_PREFIX, ""));
    }
    
    public static YarnProtos.LocalResourceTypeProto convertToProtoFormat(final LocalResourceType e) {
        return YarnProtos.LocalResourceTypeProto.valueOf(e.name());
    }
    
    public static LocalResourceType convertFromProtoFormat(final YarnProtos.LocalResourceTypeProto e) {
        return LocalResourceType.valueOf(e.name());
    }
    
    public static YarnProtos.LocalResourceVisibilityProto convertToProtoFormat(final LocalResourceVisibility e) {
        return YarnProtos.LocalResourceVisibilityProto.valueOf(e.name());
    }
    
    public static LocalResourceVisibility convertFromProtoFormat(final YarnProtos.LocalResourceVisibilityProto e) {
        return LocalResourceVisibility.valueOf(e.name());
    }
    
    public static YarnProtos.AMCommandProto convertToProtoFormat(final AMCommand e) {
        return YarnProtos.AMCommandProto.valueOf(e.name());
    }
    
    public static AMCommand convertFromProtoFormat(final YarnProtos.AMCommandProto e) {
        return AMCommand.valueOf(e.name());
    }
    
    public static ByteBuffer convertFromProtoFormat(final ByteString byteString) {
        final int capacity = byteString.asReadOnlyByteBuffer().rewind().remaining();
        final byte[] b = new byte[capacity];
        byteString.asReadOnlyByteBuffer().get(b, 0, capacity);
        return ByteBuffer.wrap(b);
    }
    
    public static ByteString convertToProtoFormat(final ByteBuffer byteBuffer) {
        final int oldPos = byteBuffer.position();
        byteBuffer.rewind();
        final ByteString bs = ByteString.copyFrom(byteBuffer);
        byteBuffer.position(oldPos);
        return bs;
    }
    
    public static YarnProtos.QueueStateProto convertToProtoFormat(final QueueState e) {
        return YarnProtos.QueueStateProto.valueOf(ProtoUtils.QUEUE_STATE_PREFIX + e.name());
    }
    
    public static QueueState convertFromProtoFormat(final YarnProtos.QueueStateProto e) {
        return QueueState.valueOf(e.name().replace(ProtoUtils.QUEUE_STATE_PREFIX, ""));
    }
    
    public static YarnProtos.QueueACLProto convertToProtoFormat(final QueueACL e) {
        return YarnProtos.QueueACLProto.valueOf(ProtoUtils.QUEUE_ACL_PREFIX + e.name());
    }
    
    public static QueueACL convertFromProtoFormat(final YarnProtos.QueueACLProto e) {
        return QueueACL.valueOf(e.name().replace(ProtoUtils.QUEUE_ACL_PREFIX, ""));
    }
    
    public static YarnProtos.ApplicationAccessTypeProto convertToProtoFormat(final ApplicationAccessType e) {
        return YarnProtos.ApplicationAccessTypeProto.valueOf(ProtoUtils.APP_ACCESS_TYPE_PREFIX + e.name());
    }
    
    public static ApplicationAccessType convertFromProtoFormat(final YarnProtos.ApplicationAccessTypeProto e) {
        return ApplicationAccessType.valueOf(e.name().replace(ProtoUtils.APP_ACCESS_TYPE_PREFIX, ""));
    }
    
    public static YarnProtos.ReservationRequestInterpreterProto convertToProtoFormat(final ReservationRequestInterpreter e) {
        return YarnProtos.ReservationRequestInterpreterProto.valueOf(e.name());
    }
    
    public static ReservationRequestInterpreter convertFromProtoFormat(final YarnProtos.ReservationRequestInterpreterProto e) {
        return ReservationRequestInterpreter.valueOf(e.name());
    }
    
    static {
        ProtoUtils.CONTAINER_STATE_PREFIX = "C_";
        ProtoUtils.NODE_STATE_PREFIX = "NS_";
        ProtoUtils.YARN_APPLICATION_ATTEMPT_STATE_PREFIX = "APP_ATTEMPT_";
        ProtoUtils.FINAL_APPLICATION_STATUS_PREFIX = "APP_";
        ProtoUtils.QUEUE_STATE_PREFIX = "Q_";
        ProtoUtils.QUEUE_ACL_PREFIX = "QACL_";
        ProtoUtils.APP_ACCESS_TYPE_PREFIX = "APPACCESS_";
    }
}
