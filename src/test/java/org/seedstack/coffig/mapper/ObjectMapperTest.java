/*
 * Copyright © 2013-2021, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.coffig.mapper;

import org.junit.Test;
import org.seedstack.coffig.Coffig;
import org.seedstack.coffig.TreeNode;
import org.seedstack.coffig.fixture.AccessorFixture;
import org.seedstack.coffig.fixture.EmptyPrefixFixture;
import org.seedstack.coffig.fixture.MultiTypesFixture;
import org.seedstack.coffig.fixture.PrefixFixture;
import org.seedstack.coffig.fixture.SingleValueFixture;
import org.seedstack.coffig.node.ArrayNode;
import org.seedstack.coffig.node.MapNode;
import org.seedstack.coffig.node.NamedNode;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

public class ObjectMapperTest {
    private final MapNode accessorFixture = new MapNode(new NamedNode("field1", "field1"), new NamedNode("field2", "field2"));
    private final MapNode prefixFixture = new MapNode(
            new NamedNode("aString", "theValue"),
            new NamedNode("baz", accessorFixture),
            new NamedNode("qux", new MapNode(new NamedNode("innerField", "innerValue")))
    );
    private final MapNode emptyPrefixFixture = new MapNode(
            new NamedNode("aString", "theValue")
    );
    private final MapNode multiTypesFixture = new MapNode(
            new NamedNode("aBoolean", "true"),
            new NamedNode("aByte", "101"),
            new NamedNode("aChar", "A"),
            new NamedNode("aDouble", "3.14"),
            new NamedNode("aFloat", "3.14"),
            new NamedNode("anInt", "42"),
            new NamedNode("aLong", "42"),
            new NamedNode("aShort", "24"),
            new NamedNode("aString", "aString"),

            new NamedNode("someBoolean", "true", "true", "true"),
            new NamedNode("someByte", "101", "101", "101"),
            new NamedNode("someChar", "A", "A", "A"),
            new NamedNode("someInt", "42", "42", "42"),
            new NamedNode("someLong", "42", "42", "42"),
            new NamedNode("someShort", "24", "24", "24"),
            new NamedNode("someString", "aString", "aString", "aString"),
            new NamedNode("someDouble", "3.14", "3.14", "3.14"),
            new NamedNode("someFloat", "3.14", "3.14", "3.14"),

            new NamedNode("stringList", "aString", "aString", "aString"),
            new NamedNode("stringSet", "aString", "aString", "aString"),

            new NamedNode("accessorFixture", accessorFixture),
            new NamedNode("aMap", new MapNode(new NamedNode("1", "true"), new NamedNode("2", "false"))),
            new NamedNode("fixtureArray", new ArrayNode(accessorFixture, accessorFixture)),
            new NamedNode("fixtureList", new ArrayNode(accessorFixture, accessorFixture)),
            new NamedNode("fixtureSet", new ArrayNode(accessorFixture, accessorFixture))
    );
    private final MapNode singleValueFixture1 = new MapNode(
            new NamedNode("innerFixture", "true")
    );
    private final MapNode singleValueFixture2 = new MapNode(
            new NamedNode("innerFixture", new MapNode(
                    new NamedNode("enabled", "true"),
                    new NamedNode("value", "12")
            ))
    );

    private ObjectMapper<AccessorFixture> accessorMapper = initialize(AccessorFixture.class);

    private ObjectMapper<MultiTypesFixture> multiTypesMapper = initialize(MultiTypesFixture.class);

    private ObjectMapper<PrefixFixture> prefixMapper = initialize(PrefixFixture.class);
    private ObjectMapper<EmptyPrefixFixture> emptyPrefixMapper = initialize(EmptyPrefixFixture.class);
    private ObjectMapper<SingleValueFixture> singleValueMapper = initialize(SingleValueFixture.class);

    @Test
    public void testField() {
        AccessorFixture accessorFixture = accessorMapper.map(this.accessorFixture);
        assertThat(accessorFixture.getField1()).isEqualTo("field1");

        TreeNode treeNode = initialize(accessorFixture).unmap();
        assertThat(treeNode.get("field1").get().value()).isEqualTo("field1");
    }

    @Test
    public void testGetterSetter() {
        AccessorFixture accessorFixture = accessorMapper.map(this.accessorFixture);
        assertThat(accessorFixture.getField2()).isEqualTo("field22");

        TreeNode treeNode = initialize(accessorFixture).unmap();
        assertThat(treeNode.get("field2").get().value()).isEqualTo("field22");
    }

    @Test
    public void testMissingProperty() {
        AccessorFixture accessorFixture = accessorMapper.map(new MapNode());
        assertThat(accessorFixture).isNotNull();
        assertThat(accessorFixture.getField1()).isEqualTo("default");

        TreeNode treeNode = initialize(accessorFixture).unmap();
        assertThat(treeNode.get("field1").get().value()).isEqualTo("default");
        assertThat(treeNode.get("field2").isPresent()).isFalse();
    }

    @Test
    public void testMultiTypes() {
        MultiTypesFixture multiTypesFixture = multiTypesMapper.map(this.multiTypesFixture);
        assertThat(multiTypesFixture.aBoolean).isEqualTo(true);
        assertThat(multiTypesFixture.aByte).isEqualTo((byte) 101);
        assertThat(multiTypesFixture.aChar).isEqualTo('A');
        assertThat(multiTypesFixture.aDouble).isEqualTo(3.14d);
        assertThat(multiTypesFixture.aFloat).isEqualTo(3.14f);
        assertThat(multiTypesFixture.anInt).isEqualTo(42);
        assertThat(multiTypesFixture.aLong).isEqualTo(42L);
        assertThat(multiTypesFixture.aShort).isEqualTo((short) 24);
        assertThat(multiTypesFixture.aString).isEqualTo("aString");

        TreeNode treeNode = initialize(multiTypesFixture).unmap();
        assertThat(treeNode.get("aBoolean").get().value()).isEqualTo("true");
        assertThat(treeNode.get("aByte").get().value()).isEqualTo("101");
        assertThat(treeNode.get("aChar").get().value()).isEqualTo("A");
        assertThat(treeNode.get("aDouble").get().value()).isEqualTo("3.14");
        assertThat(treeNode.get("aFloat").get().value()).isEqualTo("3.14");
        assertThat(treeNode.get("anInt").get().value()).isEqualTo("42");
        assertThat(treeNode.get("aLong").get().value()).isEqualTo("42");
        assertThat(treeNode.get("aShort").get().value()).isEqualTo("24");
        assertThat(treeNode.get("aString").get().value()).isEqualTo("aString");
    }

    @Test
    public void testMultiTypesArray() {
        MultiTypesFixture multiTypesFixture = multiTypesMapper.map(this.multiTypesFixture);
        assertThat(multiTypesFixture.someBoolean).containsOnly(true, true, true);
        assertThat(multiTypesFixture.someChar).containsOnly('A', 'A', 'A');
        assertThat(multiTypesFixture.someInt).containsOnly(42, 42, 42);
        assertThat(multiTypesFixture.someLong).containsOnly(42L, 42L, 42L);
        assertThat(multiTypesFixture.someString).containsOnly("aString", "aString", "aString");
        assertThat(multiTypesFixture.someByte).containsOnly((byte) 101, (byte) 101, (byte) 101);
        assertThat(multiTypesFixture.someShort).containsOnly((short) 24, (short) 24, (short) 24);
        assertThat(multiTypesFixture.someDouble).containsOnly(3.14d, 3.14d, 3.14d);
        assertThat(multiTypesFixture.someFloat).containsOnly(3.14f, 3.14f, 3.14f);

        TreeNode treeNode = initialize(multiTypesFixture).unmap();
        assertThat(treeNode.get("someBoolean").get()).isEqualTo(new ArrayNode("true", "true", "true"));
        assertThat(treeNode.get("someChar").get()).isEqualTo(new ArrayNode("A", "A", "A"));
        assertThat(treeNode.get("someInt").get()).isEqualTo(new ArrayNode("42", "42", "42"));
        assertThat(treeNode.get("someLong").get()).isEqualTo(new ArrayNode("42", "42", "42"));
        assertThat(treeNode.get("someString").get()).isEqualTo(new ArrayNode("aString", "aString", "aString"));
        assertThat(treeNode.get("someByte").get()).isEqualTo(new ArrayNode("101", "101", "101"));
        assertThat(treeNode.get("someShort").get()).isEqualTo(new ArrayNode("24", "24", "24"));
        assertThat(treeNode.get("someDouble").get()).isEqualTo(new ArrayNode("3.14", "3.14", "3.14"));
        assertThat(treeNode.get("someFloat").get()).isEqualTo(new ArrayNode("3.14", "3.14", "3.14"));
    }

    @Test
    public void testMultiTypesList() {
        MultiTypesFixture multiTypesFixture = multiTypesMapper.map(this.multiTypesFixture);
        assertThat(multiTypesFixture.stringList).containsOnly("aString", "aString", "aString");

        TreeNode treeNode = initialize(multiTypesFixture).unmap();
        assertThat(treeNode.get("stringList").get()).isEqualTo(new ArrayNode("aString", "aString", "aString"));
    }

    @Test
    public void testMultiTypesSet() {
        MultiTypesFixture multiTypesFixture = multiTypesMapper.map(this.multiTypesFixture);
        assertThat(multiTypesFixture.stringSet).containsOnly("aString", "aString", "aString");

        TreeNode treeNode = initialize(multiTypesFixture).unmap();
        assertThat(treeNode.get("stringList").get()).isEqualTo(new ArrayNode("aString", "aString", "aString"));
    }

    @Test
    public void testMultiTypesObject() {
        MultiTypesFixture multiTypesFixture = multiTypesMapper.map(this.multiTypesFixture);
        assertThat(multiTypesFixture.accessorFixture.getField1()).isEqualTo("field1");
        assertThat(multiTypesFixture.accessorFixture.getField2()).isEqualTo("field22");

        TreeNode treeNode = initialize(multiTypesFixture).unmap();
        assertThat(treeNode.get("accessorFixture.field1").get().value()).isEqualTo("field1");
        assertThat(treeNode.get("accessorFixture.field2").get().value()).isEqualTo("field22");
    }

    @Test
    public void testMultiTypesObjectArray() {
        MultiTypesFixture multiTypesFixture = multiTypesMapper.map(this.multiTypesFixture);
        assertThat(multiTypesFixture.fixtureArray).hasSize(2);
        assertThat(multiTypesFixture.fixtureArray[0].getField1()).isEqualTo("field1");
        assertThat(multiTypesFixture.fixtureArray[1].getField2()).isEqualTo("field22");

        TreeNode treeNode = initialize(multiTypesFixture).unmap();
        assertThat(treeNode.get("fixtureArray").get().nodes()).hasSize(2);
        assertThat(treeNode.get("fixtureArray[0].field1").get().value()).isEqualTo("field1");
        assertThat(treeNode.get("fixtureArray[1].field2").get().value()).isEqualTo("field22");
    }

    @Test
    public void testMultiTypesObjectList() {
        MultiTypesFixture multiTypesFixture = multiTypesMapper.map(this.multiTypesFixture);
        assertThat(multiTypesFixture.fixtureList).hasSize(2);
        assertThat(multiTypesFixture.fixtureList.get(0).getField1()).isEqualTo("field1");
        assertThat(multiTypesFixture.fixtureList.get(1).getField2()).isEqualTo("field22");

        TreeNode treeNode = initialize(multiTypesFixture).unmap();
        assertThat(treeNode.get("fixtureList").get().nodes()).hasSize(2);
        assertThat(treeNode.get("fixtureList[0].field1").get().value()).isEqualTo("field1");
        assertThat(treeNode.get("fixtureList[1].field2").get().value()).isEqualTo("field22");
    }

    @Test
    public void testMultiTypesObjectSet() {
        MultiTypesFixture multiTypesFixture = multiTypesMapper.map(this.multiTypesFixture);
        assertThat(multiTypesFixture.fixtureArray).hasSize(2);
        assertThat(multiTypesFixture.fixtureSet.iterator().next().getField1()).isEqualTo("field1");
        assertThat(multiTypesFixture.fixtureSet.iterator().next().getField2()).isEqualTo("field22");

        TreeNode treeNode = initialize(multiTypesFixture).unmap();
        assertThat(treeNode.get("fixtureSet").get().nodes()).hasSize(2);
        assertThat(treeNode.get("fixtureSet[0].field1").get().value()).isEqualTo("field1");
        assertThat(treeNode.get("fixtureSet[1].field2").get().value()).isEqualTo("field22");
    }

    @Test
    public void testMultiTypesMap() {
        MultiTypesFixture multiTypesFixture = multiTypesMapper.map(this.multiTypesFixture);
        assertThat(multiTypesFixture.aMap).containsOnly(entry(1, true), entry(2, false));

        TreeNode treeNode = initialize(multiTypesFixture).unmap();
        assertThat(treeNode.get("aMap.1").get().value()).isEqualTo("true");
        assertThat(treeNode.get("aMap.2").get().value()).isEqualTo("false");
    }

    @Test
    public void testPrefixedObject() throws Exception {
        PrefixFixture prefixFixture = prefixMapper.map(this.prefixFixture);
        assertThat(prefixFixture.aString).isEqualTo("theValue");
        assertThat(prefixFixture.accessorFixture.getField1()).isEqualTo("field1");
        assertThat(prefixFixture.accessorFixture.getField2()).isEqualTo("field22");

        TreeNode treeNode = initialize(prefixFixture).unmap();
        assertThat(treeNode.get("aString").get().value()).isEqualTo("theValue");
        assertThat(treeNode.get("baz.field1").get().value()).isEqualTo("field1");
        assertThat(treeNode.get("baz.field2").get().value()).isEqualTo("field22");
    }

    @Test
    public void testPrefixedInnerClass() throws Exception {
        PrefixFixture prefixFixture = prefixMapper.map(this.prefixFixture);
        assertThat(prefixFixture.innerClass.innerField).isEqualTo("innerValue");
    }

    @Test
    public void testEmptyPrefixedObject() throws Exception {
        EmptyPrefixFixture emptyPrefixFixture = emptyPrefixMapper.map(this.emptyPrefixFixture);
        assertThat(emptyPrefixFixture.aString).isEqualTo("theValue");

        TreeNode treeNode = initialize(emptyPrefixFixture).unmap();
        assertThat(treeNode.get("aString").get().value()).isEqualTo("theValue");
    }

    @Test
    public void testSingleValueFixture1() throws Exception {
        SingleValueFixture singleValueFixture = singleValueMapper.map(this.singleValueFixture1);
        assertThat(singleValueFixture.getInnerFixture().isEnabled()).isTrue();
        assertThat(singleValueFixture.getInnerFixture().getValue()).isEqualTo(5);
    }

    @Test
    public void testSingleValueFixture2() throws Exception {
        SingleValueFixture singleValueFixture = singleValueMapper.map(this.singleValueFixture2);
        assertThat(singleValueFixture.getInnerFixture().isEnabled()).isTrue();
        assertThat(singleValueFixture.getInnerFixture().getValue()).isEqualTo(12);
    }

    private <T> ObjectMapper<T> initialize(Class<T> aClass) {
        ObjectMapper<T> mapper = new ObjectMapper<>(aClass);
        mapper.initialize(Coffig.basic());
        return mapper;
    }

    private ObjectMapper initialize(Object object) {
        ObjectMapper mapper = new ObjectMapper<>(object);
        mapper.initialize(Coffig.basic());
        return mapper;
    }
}
