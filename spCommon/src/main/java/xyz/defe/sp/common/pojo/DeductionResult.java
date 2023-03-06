package xyz.defe.sp.common.pojo;

import java.io.Serializable;

public class DeductionResult implements Serializable {
    private String orderId;
    private boolean successful = false;
    private String message;

    public DeductionResult(){}

    public DeductionResult(String orderId, String message) {
        this.orderId = orderId;
        this.message = message;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
