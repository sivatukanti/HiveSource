// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.hadoop.fs.shell;

import java.util.LinkedList;
import java.io.IOException;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FsShellPermissions;
import org.apache.hadoop.fs.shell.find.Find;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.classification.InterfaceAudience;

@InterfaceAudience.Private
@InterfaceStability.Evolving
public abstract class FsCommand extends Command
{
    public static void registerCommands(final CommandFactory factory) {
        factory.registerCommands(AclCommands.class);
        factory.registerCommands(CopyCommands.class);
        factory.registerCommands(Count.class);
        factory.registerCommands(Delete.class);
        factory.registerCommands(Display.class);
        factory.registerCommands(Find.class);
        factory.registerCommands(FsShellPermissions.class);
        factory.registerCommands(FsUsage.class);
        factory.registerCommands(Ls.class);
        factory.registerCommands(Mkdir.class);
        factory.registerCommands(MoveCommands.class);
        factory.registerCommands(SetReplication.class);
        factory.registerCommands(Stat.class);
        factory.registerCommands(Tail.class);
        factory.registerCommands(Head.class);
        factory.registerCommands(Test.class);
        factory.registerCommands(TouchCommands.class);
        factory.registerCommands(Truncate.class);
        factory.registerCommands(SnapshotCommands.class);
        factory.registerCommands(XAttrCommands.class);
    }
    
    protected FsCommand() {
    }
    
    protected FsCommand(final Configuration conf) {
        super(conf);
    }
    
    @Override
    public String getCommandName() {
        return this.getName();
    }
    
    @Override
    protected void run(final Path path) throws IOException {
        throw new RuntimeException("not supposed to get here");
    }
    
    @Deprecated
    @Override
    public int runAll() {
        return this.run(this.args);
    }
    
    @Override
    protected void processRawArguments(final LinkedList<String> args) throws IOException {
        final LinkedList<PathData> expendedArgs = this.expandArguments(args);
        final boolean displayWarnings = this.getConf().getBoolean("hadoop.shell.missing.defaultFs.warning", false);
        if (displayWarnings) {
            final String defaultFs = this.getConf().get("fs.defaultFS");
            final boolean missingDefaultFs = defaultFs == null || defaultFs.equals("file:///");
            if (missingDefaultFs) {
                this.err.printf("Warning: fs.defaultFS is not set when running \"%s\" command.%n", this.getCommandName());
            }
        }
        this.processArguments(expendedArgs);
    }
}
