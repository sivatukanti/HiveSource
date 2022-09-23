// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.zookeeper;

import org.apache.zookeeper.data.Stat;

public abstract class OpResult
{
    private int type;
    
    private OpResult(final int type) {
        this.type = type;
    }
    
    public int getType() {
        return this.type;
    }
    
    public static class CreateResult extends OpResult
    {
        private String path;
        
        public CreateResult(final String path) {
            super(1, null);
            this.path = path;
        }
        
        public String getPath() {
            return this.path;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof CreateResult)) {
                return false;
            }
            final CreateResult other = (CreateResult)o;
            return this.getType() == other.getType() && this.path.equals(other.getPath());
        }
        
        @Override
        public int hashCode() {
            return this.getType() * 35 + this.path.hashCode();
        }
    }
    
    public static class DeleteResult extends OpResult
    {
        public DeleteResult() {
            super(2, null);
        }
        
        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof DeleteResult)) {
                return false;
            }
            final DeleteResult opResult = (DeleteResult)o;
            return this.getType() == opResult.getType();
        }
        
        @Override
        public int hashCode() {
            return this.getType();
        }
    }
    
    public static class SetDataResult extends OpResult
    {
        private Stat stat;
        
        public SetDataResult(final Stat stat) {
            super(5, null);
            this.stat = stat;
        }
        
        public Stat getStat() {
            return this.stat;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof SetDataResult)) {
                return false;
            }
            final SetDataResult other = (SetDataResult)o;
            return this.getType() == other.getType() && this.stat.getMzxid() == other.stat.getMzxid();
        }
        
        @Override
        public int hashCode() {
            return (int)(this.getType() * 35 + this.stat.getMzxid());
        }
    }
    
    public static class CheckResult extends OpResult
    {
        public CheckResult() {
            super(13, null);
        }
        
        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof CheckResult)) {
                return false;
            }
            final CheckResult other = (CheckResult)o;
            return this.getType() == other.getType();
        }
        
        @Override
        public int hashCode() {
            return this.getType();
        }
    }
    
    public static class ErrorResult extends OpResult
    {
        private int err;
        
        public ErrorResult(final int err) {
            super(-1, null);
            this.err = err;
        }
        
        public int getErr() {
            return this.err;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof ErrorResult)) {
                return false;
            }
            final ErrorResult other = (ErrorResult)o;
            return this.getType() == other.getType() && this.err == other.getErr();
        }
        
        @Override
        public int hashCode() {
            return this.getType() * 35 + this.err;
        }
    }
}
