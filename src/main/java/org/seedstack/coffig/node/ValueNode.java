/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.coffig.node;

import org.seedstack.coffig.ConfigurationErrorCode;
import org.seedstack.coffig.ConfigurationException;
import org.seedstack.coffig.NamedNode;
import org.seedstack.coffig.TreeNode;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public class ValueNode extends AbstractTreeNode {
    private final String value;

    public ValueNode() {
        this.value = null;
    }

    public ValueNode(ValueNode other) {
        super(other);
        this.value = other.value;
    }

    public ValueNode(String value) {
        this.value = value;
    }

    @Override
    public Type type() {
        return Type.VALUE_NODE;
    }

    @Override
    public String value() {
        return value;
    }

    @Override
    public Stream<TreeNode> nodes() {
        return Stream.of(this);
    }

    @Override
    public Stream<NamedNode> namedNodes() {
        return Stream.of(new NamedNode("value", this));
    }

    @Override
    public TreeNode node(String key) {
        return this;
    }

    @Override
    public Optional<TreeNode> get(String path) {
        return Optional.empty();
    }

    @Override
    public Stream<TreeNode> walk() {
        return Stream.of(this);
    }

    @Override
    public boolean isEmpty() {
        return value == null;
    }

    @Override
    public TreeNode merge(TreeNode otherNode) {
        return otherNode;
    }

    @Override
    public TreeNode set(String path, TreeNode value) {
        throw ConfigurationException.createNew(ConfigurationErrorCode.ILLEGAL_TREE_ACCESS)
                .put("path", path)
                .put("reason", "cannot add children to value node");
    }

    @Override
    public TreeNode remove(String path) {
        throw ConfigurationException.createNew(ConfigurationErrorCode.ILLEGAL_TREE_ACCESS)
                .put("path", path)
                .put("reason", "cannot remove children from value node");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !getClass().isAssignableFrom(o.getClass()) && !o.getClass().isAssignableFrom(getClass()))
            return false;
        ValueNode valueNode = (ValueNode) o;
        return Objects.equals(value, valueNode.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "\"" + (isHidden() ? HIDDEN_PLACEHOLDER : quote(value)) + "\"";
    }
}
