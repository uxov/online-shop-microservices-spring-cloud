package xyz.defe.sp.common.pojo;

import java.io.Serializable;
import java.util.Map;

public class OrderMsg extends Msg implements Serializable {
    private String orderId;
    private Map<String, Integer> counterMap;
    private DeductionResult deductionResult;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Map<String, Integer> getCounterMap() {
        return counterMap;
    }

    public void setCounterMap(Map<String, Integer> counterMap) {
        this.counterMap = counterMap;
    }

    public DeductionResult getDeductionResult() {
        return deductionResult;
    }

    public void setDeductionResult(DeductionResult deductionResult) {
        this.deductionResult = deductionResult;
    }
}
