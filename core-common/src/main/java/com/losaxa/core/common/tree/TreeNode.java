package com.losaxa.core.common.tree;

import java.util.Collection;

/**
 * 树形结构接口
 * @param <T>
 * @param <ID>
 */
public interface TreeNode<T extends TreeNode<T, ID>, ID> {

    ID getId();

    String getName();

    ID getParentId();

    String getParentName();

    Collection<T> getChildren();

    void setChildren(Collection<T> children);

    default int getSeq() {
        return 0;
    }

}
