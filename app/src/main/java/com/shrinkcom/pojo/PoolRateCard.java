package com.shrinkcom.pojo;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by user88 on 6/6/2017.
 */

public class PoolRateCard implements Serializable {
    private String seat,cost,select;
    private ArrayList<PoolRateCard> poolRateCardList;

    public PoolRateCard(){

    }

    public PoolRateCard(String dummy, ArrayList<PoolRateCard> poolRateCardList) {
        this.poolRateCardList = poolRateCardList;
    }


    public String getSeat() {
        return seat;
    }

    public String getCost() {
        return cost;
    }

    public String getSelect() {
        return select;
    }

    public void setSelect(String select) {
        this.select = select;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public void setSeat(String seat) {
        this.seat = seat;
    }
}
