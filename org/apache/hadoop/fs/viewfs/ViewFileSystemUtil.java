// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs.viewfs;

import java.io.IOException;
import java.util.HashMap;
import org.apache.hadoop.fs.UnsupportedFileSystemException;
import org.apache.hadoop.fs.FsStatus;
import java.util.Map;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Public
@InterfaceStability.Evolving
public final class ViewFileSystemUtil
{
    private ViewFileSystemUtil() {
    }
    
    public static boolean isViewFileSystem(final FileSystem fileSystem) {
        return fileSystem.getScheme().equals("viewfs");
    }
    
    public static Map<ViewFileSystem.MountPoint, FsStatus> getStatus(final FileSystem fileSystem, final Path path) throws IOException {
        if (!isViewFileSystem(fileSystem)) {
            throw new UnsupportedFileSystemException("FileSystem '" + fileSystem.getUri() + "'is not a ViewFileSystem.");
        }
        final ViewFileSystem viewFileSystem = (ViewFileSystem)fileSystem;
        final String viewFsUriPath = viewFileSystem.getUriPath(path);
        boolean isPathOverMountPoint = false;
        boolean isPathLeadingToMountPoint = false;
        boolean isPathIncludesAllMountPoint = false;
        final Map<ViewFileSystem.MountPoint, FsStatus> mountPointMap = new HashMap<ViewFileSystem.MountPoint, FsStatus>();
        for (final ViewFileSystem.MountPoint mountPoint : viewFileSystem.getMountPoints()) {
            String[] mountPointPathComponents;
            String[] incomingPathComponents;
            int pathCompIndex;
            for (mountPointPathComponents = InodeTree.breakIntoPathComponents(mountPoint.getMountedOnPath().toString()), incomingPathComponents = InodeTree.breakIntoPathComponents(viewFsUriPath), pathCompIndex = 0; pathCompIndex < mountPointPathComponents.length && pathCompIndex < incomingPathComponents.length && mountPointPathComponents[pathCompIndex].equals(incomingPathComponents[pathCompIndex]); ++pathCompIndex) {}
            if (pathCompIndex >= mountPointPathComponents.length) {
                isPathOverMountPoint = true;
                mountPointMap.clear();
                updateMountPointFsStatus(viewFileSystem, mountPointMap, mountPoint, new Path(viewFsUriPath));
                break;
            }
            if (pathCompIndex > 1) {
                isPathLeadingToMountPoint = true;
            }
            else if (incomingPathComponents.length <= 1) {
                isPathIncludesAllMountPoint = true;
            }
            updateMountPointFsStatus(viewFileSystem, mountPointMap, mountPoint, mountPoint.getMountedOnPath());
        }
        if (!isPathOverMountPoint && !isPathLeadingToMountPoint && !isPathIncludesAllMountPoint) {
            throw new NotInMountpointException(path, "getStatus");
        }
        return mountPointMap;
    }
    
    private static void updateMountPointFsStatus(final ViewFileSystem viewFileSystem, final Map<ViewFileSystem.MountPoint, FsStatus> mountPointMap, final ViewFileSystem.MountPoint mountPoint, final Path path) throws IOException {
        final FsStatus fsStatus = viewFileSystem.getStatus(path);
        mountPointMap.put(mountPoint, fsStatus);
    }
}
