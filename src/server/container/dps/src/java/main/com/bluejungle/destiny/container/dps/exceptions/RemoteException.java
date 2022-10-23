package com.bluejungle.destiny.container.dps.exceptions;

public class RemoteException extends RuntimeException {

    public RemoteException() {
    }

    public RemoteException(String message) {
        super(message);
    }

    public RemoteException(String s, Throwable e) {
        super(s, e);
    }

    public RemoteException(Throwable e) {
        super(e.getMessage());
    }

}
