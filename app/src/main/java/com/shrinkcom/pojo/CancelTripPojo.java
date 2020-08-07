package com.shrinkcom.pojo;

import java.io.Serializable;

/**
 * Created by Prem Kumar and Anitha on 11/2/2015.
 */
public class CancelTripPojo implements Serializable
{
    private String reasonId, reason;

    public String getReasonId() {
        return reasonId;
    }

    public void setReasonId(String reasonId) {
        this.reasonId = reasonId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
