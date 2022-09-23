// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs.shell;

import java.util.Iterator;
import java.util.Map;
import java.io.IOException;
import org.apache.hadoop.HadoopIllegalArgumentException;
import com.google.common.base.Preconditions;
import java.util.List;
import org.apache.hadoop.util.StringUtils;
import java.util.LinkedList;
import org.apache.hadoop.fs.XAttrCodec;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Evolving
class XAttrCommands extends FsCommand
{
    private static final String GET_FATTR = "getfattr";
    private static final String SET_FATTR = "setfattr";
    
    public static void registerCommands(final CommandFactory factory) {
        factory.addClass(GetfattrCommand.class, "-getfattr");
        factory.addClass(SetfattrCommand.class, "-setfattr");
    }
    
    public static class GetfattrCommand extends FsCommand
    {
        public static final String NAME = "getfattr";
        public static final String USAGE = "[-R] {-n name | -d} [-e en] <path>";
        public static final String DESCRIPTION = "Displays the extended attribute names and values (if any) for a file or directory.\n-R: Recursively list the attributes for all files and directories.\n-n name: Dump the named extended attribute value.\n-d: Dump all extended attribute values associated with pathname.\n-e <encoding>: Encode values after retrieving them.Valid encodings are \"text\", \"hex\", and \"base64\". Values encoded as text strings are enclosed in double quotes (\"), and values encoded as hexadecimal and base64 are prefixed with 0x and 0s, respectively.\n<path>: The file or directory.\n";
        private String name;
        private boolean dump;
        private XAttrCodec encoding;
        
        public GetfattrCommand() {
            this.name = null;
            this.dump = false;
            this.encoding = XAttrCodec.TEXT;
        }
        
        @Override
        protected void processOptions(final LinkedList<String> args) throws IOException {
            this.name = StringUtils.popOptionWithArgument("-n", args);
            final String en = StringUtils.popOptionWithArgument("-e", args);
            if (en != null) {
                try {
                    this.encoding = XAttrCodec.valueOf(StringUtils.toUpperCase(en));
                }
                catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Invalid/unsupported encoding option specified: " + en);
                }
                Preconditions.checkArgument(this.encoding != null, (Object)("Invalid/unsupported encoding option specified: " + en));
            }
            final boolean r = StringUtils.popOption("-R", args);
            this.setRecursive(r);
            this.dump = StringUtils.popOption("-d", args);
            if (!this.dump && this.name == null) {
                throw new HadoopIllegalArgumentException("Must specify '-n name' or '-d' option.");
            }
            if (args.isEmpty()) {
                throw new HadoopIllegalArgumentException("<path> is missing.");
            }
            if (args.size() > 1) {
                throw new HadoopIllegalArgumentException("Too many arguments.");
            }
        }
        
        @Override
        protected void processPath(final PathData item) throws IOException {
            this.out.println("# file: " + item);
            if (this.dump) {
                final Map<String, byte[]> xattrs = item.fs.getXAttrs(item.path);
                if (xattrs != null) {
                    for (final Map.Entry<String, byte[]> entry : xattrs.entrySet()) {
                        this.printXAttr(entry.getKey(), entry.getValue());
                    }
                }
            }
            else {
                final byte[] value = item.fs.getXAttr(item.path, this.name);
                this.printXAttr(this.name, value);
            }
        }
        
        private void printXAttr(final String name, final byte[] value) throws IOException {
            if (value != null) {
                if (value.length != 0) {
                    this.out.println(name + "=" + XAttrCodec.encodeValue(value, this.encoding));
                }
                else {
                    this.out.println(name);
                }
            }
        }
    }
    
    public static class SetfattrCommand extends FsCommand
    {
        public static final String NAME = "setfattr";
        public static final String USAGE = "{-n name [-v value] | -x name} <path>";
        public static final String DESCRIPTION = "Sets an extended attribute name and value for a file or directory.\n-n name: The extended attribute name.\n-v value: The extended attribute value. There are three different encoding methods for the value. If the argument is enclosed in double quotes, then the value is the string inside the quotes. If the argument is prefixed with 0x or 0X, then it is taken as a hexadecimal number. If the argument begins with 0s or 0S, then it is taken as a base64 encoding.\n-x name: Remove the extended attribute.\n<path>: The file or directory.\n";
        private String name;
        private byte[] value;
        private String xname;
        
        public SetfattrCommand() {
            this.name = null;
            this.value = null;
            this.xname = null;
        }
        
        @Override
        protected void processOptions(final LinkedList<String> args) throws IOException {
            this.name = StringUtils.popOptionWithArgument("-n", args);
            final String v = StringUtils.popOptionWithArgument("-v", args);
            if (v != null) {
                this.value = XAttrCodec.decodeValue(v);
            }
            this.xname = StringUtils.popOptionWithArgument("-x", args);
            if (this.name != null && this.xname != null) {
                throw new HadoopIllegalArgumentException("Can not specify both '-n name' and '-x name' option.");
            }
            if (this.name == null && this.xname == null) {
                throw new HadoopIllegalArgumentException("Must specify '-n name' or '-x name' option.");
            }
            if (args.isEmpty()) {
                throw new HadoopIllegalArgumentException("<path> is missing.");
            }
            if (args.size() > 1) {
                throw new HadoopIllegalArgumentException("Too many arguments.");
            }
        }
        
        @Override
        protected void processPath(final PathData item) throws IOException {
            if (this.name != null) {
                item.fs.setXAttr(item.path, this.name, this.value);
            }
            else if (this.xname != null) {
                item.fs.removeXAttr(item.path, this.xname);
            }
        }
    }
}
