/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.coffig.processor;

import org.junit.Before;
import org.junit.Test;
import org.seedstack.coffig.mapper.MapperFactory;
import org.seedstack.coffig.node.ArrayNode;
import org.seedstack.coffig.node.MutableMapNode;
import org.seedstack.coffig.node.NamedNode;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class FunctionProcessorTest {
    private FunctionProcessor functionProcessor = new FunctionProcessor(new MapperFactory());
    private MutableMapNode config;

    @Before
    public void setUp() throws Exception {
        config = new MutableMapNode(
                new NamedNode("object", new MutableMapNode(
                        new NamedNode("field1", "hello"),
                        new NamedNode("field2", new ArrayNode("item1", "item2", "item3"))
                )),
                new NamedNode("test", new MutableMapNode(
                        new NamedNode("noArg", "$prefix()"),
                        new NamedNode("literal", "$greet('World', '?')"),
                        new NamedNode("nestedCall", "$greet('World', $prefix())"),
                        new NamedNode("nestedRef", "$greet('World', test.noArg)"),
                        new NamedNode("unresolvedRef", "$greet('World', test.unknown)"),
                        new NamedNode("mappedArg1", "$greetSeveralTimes('World', '5', '!')"),
                        new NamedNode("mappedArg2", "$verifyObject(object)")
                ))
        );
        functionProcessor.registerFunction("greet", FunctionProcessorTest.class.getDeclaredMethod("greet", String.class, String.class), null);
        functionProcessor.registerFunction("greetSeveralTimes", FunctionProcessorTest.class.getDeclaredMethod("greetSeveralTimes", String.class, int.class, String.class), null);
        functionProcessor.registerFunction("prefix", FunctionProcessorTest.class.getDeclaredMethod("prefix"), null);
        functionProcessor.registerFunction("verifyObject", FunctionProcessorTest.class.getDeclaredMethod("verifyObject", MappedClass.class), null);
        functionProcessor.process(config);
    }

    private static class MappedClass {
        private String field1;
        private List<String> field2;
    }

    @Test
    public void testNoArgument() throws Exception {
        assertThat(config.get("test.noArg").get().value()).isEqualTo("!");
    }

    @Test
    public void tesLiteralArgument() throws Exception {
        assertThat(config.get("test.literal").get().value()).isEqualTo("Hello World?");
    }

    @Test
    public void testNestedFunctions() throws Exception {
        assertThat(config.get("test.nestedCall").get().value()).isEqualTo("Hello World!");
    }

    @Test
    public void testNestedReference() throws Exception {
        assertThat(config.get("test.nestedRef").get().value()).isEqualTo("Hello World!");
    }

    @Test
    public void testUnresolvedReference() throws Exception {
        assertThat(config.get("test.unresolvedRef").get().value()).isEqualTo("Hello World");
    }

    @Test
    public void testMappedLiteral() throws Exception {
        assertThat(config.get("test.mappedArg1").get().value()).isEqualTo("Hello World World World World World!");
    }

    @Test
    public void testMappedReference() throws Exception {
        assertThat(config.get("test.mappedArg2").get().value()).isEqualTo("true");
    }

    private static String greet(String name, String suffix) {
        return "Hello " + name + suffix;
    }

    private static String verifyObject(MappedClass mappedObject) {
        assertThat(mappedObject.field1).isEqualTo("hello");
        assertThat(mappedObject.field2).containsExactly("item1", "item2", "item3");
        return "true";
    }

    private static String greetSeveralTimes(String name, int count, String suffix) {
        StringBuilder sb = new StringBuilder("Hello ");
        for (int i = 0; i < count; i++) {
            sb.append(name);
            if (i < count - 1) {
                sb.append(" ");
            }
        }
        return sb.append(suffix).toString();
    }

    private static String prefix() {
        return "!";
    }
}
