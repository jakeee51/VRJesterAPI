package com.calicraft.vrjester.utils.vrdata;

import java.util.List;

public interface VRDataAggregator {

    List<VRDataState> getData();

    VRDataState listen();

    void clear();
}
