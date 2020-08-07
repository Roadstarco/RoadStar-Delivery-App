
package com.shrinkcom.pojo.walletrecipt;

import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WalletReciept implements Serializable
{

    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("total_amount")
    @Expose
    private String totalAmount;
    @SerializedName("pay_date")
    @Expose
    private String payDate;
    @SerializedName("pay_status")
    @Expose
    private String payStatus;
    @SerializedName("image")
    @Expose
    private String image;
    private final static long serialVersionUID = 5607287561687263939L;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getPayDate() {
        return payDate;
    }

    public void setPayDate(String payDate) {
        this.payDate = payDate;
    }

    public String getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(String payStatus) {
        this.payStatus = payStatus;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

}
