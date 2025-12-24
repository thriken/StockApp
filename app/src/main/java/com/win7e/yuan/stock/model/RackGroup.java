package com.win7e.yuan.stock.model;

import java.util.List;

public class RackGroup {
    private final String rackName;
    private final List<SpecGroup> specGroups;

    public RackGroup(String rackName, List<SpecGroup> specGroups) {
        this.rackName = rackName;
        this.specGroups = specGroups;
    }

    public String getRackName() {
        return rackName;
    }

    public List<SpecGroup> getSpecGroups() {
        return specGroups;
    }
}
