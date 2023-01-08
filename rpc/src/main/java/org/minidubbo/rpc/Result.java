package org.minidubbo.rpc;

import java.io.Serializable;

public interface Result extends Serializable {

    Object getValue();

    void setValue(Object value);

    Throwable getException();

    void setException(Throwable t);

    boolean hasException();

    Object recreate() throws Throwable;
}
