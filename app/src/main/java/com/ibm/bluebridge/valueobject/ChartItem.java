package com.ibm.bluebridge.valueobject;

/**
 * Created by huangq on 5/11/2015.
 *
 * This the chart item object for Statistics page.
 * Cause statistics page is list view, have to define an adapter.
 */
public class ChartItem extends BlueBridgeVO{
    private String category;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
