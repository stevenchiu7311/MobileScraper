package com.steven.keepscrap.nestedlist.model;

import java.util.List;

public class ParentChild {
    List<Child> child;

    public ParentChild() {}

    public List<Child> getChild() {
        return child;
    }

    public void setChild(List<Child> child) {
        this.child = child;
    }
}
