package com.earth.libbase.entity;

import android.location.Address;

import java.util.List;

public class LocationEntity {
    private List<Double> doublelist;

    private List<Address> addresseslist;

    public List<Double> getDoublelist() {
        return doublelist;
    }

    public void setDoublelist(List<Double> doublelist) {
        this.doublelist = doublelist;
    }

    public List<Address> getAddresseslist() {
        return addresseslist;
    }

    public void setAddresseslist(List<Address> addresseslist) {
        this.addresseslist = addresseslist;
    }
}
