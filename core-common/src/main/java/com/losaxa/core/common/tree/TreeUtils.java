package com.losaxa.core.common.tree;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * 树形结构工具
 */
public class TreeUtils {

    private TreeUtils() {
    }

    /**
     * 列表转树形结构列表
     * @param list
     * @param rootId
     * @param <ID>
     * @param <T>
     * @return
     */
    public static <ID, T extends TreeNode<T, ID>> List<T> listToTreeList(Collection<T> list, ID rootId) {
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        Multimap<ID, T> levelNode = ArrayListMultimap.create();
        List<T>         rootList  = Lists.newArrayList();
        for (T node : list) {
            levelNode.put(node.getParentId(), node);
            if (Objects.equals(node.getParentId(), rootId)) {
                rootList.add(node);
            }
        }
        recursionToTree(rootList, levelNode);
        return rootList;
    }

    /**
     * 递归生成树
     * @param parentNodeList
     * @param levelNode
     * @param <ID>
     * @param <T>
     */
    private static <ID, T extends TreeNode<T, ID>> void recursionToTree(List<T> parentNodeList, Multimap<ID, T> levelNode) {
        Iterator<T>     iterator = parentNodeList.iterator();
        TreeNode<T, ID> parentNode;
        while (iterator.hasNext()) {
            parentNode = iterator.next();
            List<T> child = (List<T>) levelNode.get(parentNode.getId());
            if (!child.isEmpty()) {
                child.sort(Comparator.comparingInt(TreeNode::getSeq));
                parentNode.setChildren(child);
                recursionToTree(child, levelNode);
            }
        }
        parentNodeList.sort(Comparator.comparingInt(TreeNode::getSeq));
    }
}
