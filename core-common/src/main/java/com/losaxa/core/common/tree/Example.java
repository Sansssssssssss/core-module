package com.losaxa.core.common.tree;

import java.util.Collection;

public class Example implements TreeNode<Example,String> {

    @Override
    public String getId() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getParentId() {
        return null;
    }

    @Override
    public String getParentName() {
        return null;
    }

    @Override
    public Collection<Example> getChildren() {
        return null;
    }

    @Override
    public void setChildren(Collection<Example> children) {

    }

    @Override
    public int getSeq() {
        return 0;
    }
}
