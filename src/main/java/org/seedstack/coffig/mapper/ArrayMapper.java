/*
 * Copyright © 2013-2021, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.coffig.mapper;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.seedstack.coffig.Coffig;
import org.seedstack.coffig.TreeNode;
import org.seedstack.coffig.node.ArrayNode;
import org.seedstack.coffig.spi.ConfigurationMapper;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.seedstack.shed.reflect.Types.rawClassOf;

public class ArrayMapper implements ConfigurationMapper {
    private Coffig coffig;

    @Override
    public void initialize(Coffig coffig) {
        this.coffig = coffig;
    }

    @Override
    public boolean canHandle(Type type) {
        return type instanceof GenericArrayType || (type instanceof Class && ((Class<?>) type).isArray());
    }

    @Override
    @SuppressFBWarnings(value = "BC_UNCONFIRMED_CAST", justification = "Cast is verified in canHandle() method")
    public Object map(TreeNode treeNode, Type type) {
        Type componentType;
        if (type instanceof GenericArrayType) {
            componentType = ((GenericArrayType) type).getGenericComponentType();
        } else {
            componentType = ((Class<?>) type).getComponentType();
        }
        Collection<TreeNode> values = treeNode.nodes().collect(Collectors.toList());
        Object array = Array.newInstance(rawClassOf(componentType), values.size());
        AtomicInteger index = new AtomicInteger();
        values.stream()
                .map(childNode -> coffig.getMapper().map(childNode, componentType))
                .forEach(item -> Array.set(array, index.getAndIncrement(), item));
        return array;
    }

    @Override
    @SuppressFBWarnings(value = "BC_UNCONFIRMED_CAST", justification = "Cast is verified in canHandle() method")
    public TreeNode unmap(Object object, Type type) {
        ArrayNode arrayNode = new ArrayNode();
        Class<?> componentType = ((Class<?>) type).getComponentType();
        int length = Array.getLength(object);
        for (int i = 0; i < length; i++) {
            TreeNode treeNode = coffig.getMapper().unmap(Array.get(object, i), componentType);
            if (treeNode != null) {
                arrayNode.set(null, treeNode);
            }
        }
        return arrayNode;
    }
}
