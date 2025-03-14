package com.ing.be.tia.model;

public class BulkStatus {

    public enum Status {
        NEW, IN_PROGRESS, ERROR, DONE
    }

    private Status status;

    public BulkStatus(Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
