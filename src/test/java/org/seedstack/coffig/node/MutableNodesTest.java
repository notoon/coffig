/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.coffig.node;

import org.junit.Test;
import org.seedstack.coffig.MutableTreeNode;
import org.seedstack.coffig.PropertyNotFoundException;
import org.seedstack.coffig.TreeNode;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;

public class MutableNodesTest {

    @Test
    public void testMutableNodes() {
        MutableMapNode mapNode = new MutableMapNode();
        MutableArrayNode arrayNode = new MutableArrayNode();
        arrayNode.add(new ValueNode("foo"));
        arrayNode.add(new ValueNode("bar"));
        arrayNode.add(0, new ValueNode("foo"));
        mapNode.put("custom", arrayNode);

        assertThat(mapNode.get("custom[0]").get().value()).isEqualTo("foo");
        assertThat(mapNode.get("custom[1]").get().value()).isEqualTo("foo");
        assertThat(mapNode.get("custom[2]").get().value()).isEqualTo("bar");
    }

    // Set

    @Test
    public void testSetValue() {
        MutableMapNode mapNode = new MutableMapNode();
        mapNode.set("custom", new ValueNode("foo"));
        assertThat(mapNode.get("custom").get().value()).isEqualTo("foo");
    }

    @Test
    public void testSetUpdateMap() {
        MutableMapNode mapNode = new MutableMapNode();
        mapNode.set("custom.node.old", new ValueNode("foo"));
        assertThat(mapNode.get("custom.node.old").get().value()).isEqualTo("foo");
        mapNode.set("custom.node.new", new ValueNode("bar"));
        assertThat(mapNode.get("custom.node.old").get().value()).isEqualTo("foo");
        assertThat(mapNode.get("custom.node.new").get().value()).isEqualTo("bar");
    }

    @Test
    public void testSetArray() {
        MutableMapNode mapNode = new MutableMapNode();
        mapNode.set("custom.foo[0]", new ValueNode("val1"));
        assertThat(mapNode.get("custom.foo[0]").get().value()).isEqualTo("val1");
        mapNode.set("custom.foo[1]", new ValueNode("val2"));
        assertThat(mapNode.get("custom.foo[1]").get().value()).isEqualTo("val2");
        mapNode.set("custom.foo[1]", new ValueNode("val3"));
        assertThat(mapNode.get("custom.foo[0]").get().value()).isEqualTo("val1");
        assertThat(mapNode.get("custom.foo[1]").get().value()).isEqualTo("val3");
    }

    @Test
    public void testSetArrayOfNode() {
        MutableMapNode mapNode = new MutableMapNode();
        mapNode.set("arr.0.custom", new ValueNode("foo"));
        assertThat(mapNode.get("arr.0.custom").get().value()).isEqualTo("foo");
        mapNode.set("arr.0.custom2", new ValueNode("foo2"));
        assertThat(mapNode.get("arr.0.custom2").get().value()).isEqualTo("foo2");
    }

    @Test
    public void testSetTreeNode() {
        MutableMapNode mapNode = new MutableMapNode();
        mapNode.set("arr.0.custom", new MapNode(new NamedNode("key", new ValueNode("val"))));
        assertThat(mapNode.get("arr.0.custom.key").get().value()).isEqualTo("val");
    }

    // Remove

    @Test
    public void testRemoveMapNode() {
        MutableMapNode mapNode = new MutableMapNode();
        mapNode.set("custom.key", new ValueNode("val"));

        final MutableTreeNode remove = mapNode.remove("custom.key");

        assertRemovedKey(mapNode, "custom");
        assertThat(remove.value()).isEqualTo("val");
    }

    private void assertRemovedKey(TreeNode treeNode, String path) {
        assertThat(treeNode.get(path).isPresent()).isFalse();
    }

    @Test
    public void testRemoveOnlyEmptyMapNodes() {
        MutableMapNode mapNode = new MutableMapNode();
        mapNode.set("custom.property.key1", new ValueNode("val1"));
        mapNode.set("custom.key2", new ValueNode("val2"));

        MutableTreeNode remove = mapNode.remove("custom.property.key1");

        assertRemovedKey(mapNode, "custom.property.key1");
        assertRemovedKey(mapNode, "custom.property");
        assertThat(mapNode.get("custom.key2").get().value()).isEqualTo("val2");
        assertThat(remove.value()).isEqualTo("val1");

    }

    @Test
    public void testRemoveOnlyEmptyArrayNodes() {
        MutableMapNode mapNode = new MutableMapNode();
        mapNode.set("custom.0.0", new ValueNode("val1"));
        mapNode.set("custom.1", new ValueNode("val2"));

        MutableTreeNode remove = mapNode.remove("custom.0.0");

        assertRemovedKey(mapNode, "custom.0.0");
        assertRemovedKey(mapNode, "custom.0");
        assertThat(mapNode.get("custom.1").get().value()).isEqualTo("val2");
        assertThat(remove.value()).isEqualTo("val1");
    }

    @Test
    public void testRemoveNodeNotFound() {
        MutableMapNode mapNode = new MutableMapNode();
        mapNode.set("custom.0.0", new MutableMapNode());

        try {
            mapNode.remove("custom.0.0.fake.test");
            failBecauseExceptionWasNotThrown(PropertyNotFoundException.class);
        } catch (PropertyNotFoundException e) {
            assertThat(e).hasMessage("Sub-property not found: custom.0.0.<fake.test>");
        }
    }

    @Test
    public void testMove() throws Exception {
        MutableMapNode mapNode = new MutableMapNode();
        mapNode.set("custom.0.0", new ValueNode("val"));
        mapNode.move("custom.0.0", "custom.key.test");
        assertThat(mapNode.get("custom.key.test").get().value()).isEqualTo("val");
        assertThat(mapNode.get("custom.0.0").isPresent()).isFalse();
    }
}
