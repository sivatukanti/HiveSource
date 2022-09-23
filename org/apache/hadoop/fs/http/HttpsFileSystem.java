// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs.http;

import org.apache.hadoop.conf.Configuration;
import java.net.URI;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.util.Progressable;
import org.apache.hadoop.fs.permission.FsPermission;
import java.io.IOException;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;

public class HttpsFileSystem extends AbstractHttpFileSystem
{
    @Override
    public String getScheme() {
        return "https";
    }
}
