/*
 * Copyright © 2013-2021, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.coffig.provider;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.seedstack.coffig.Coffig;
import org.seedstack.coffig.fixture.PrefixFixture;
import org.seedstack.coffig.fixture.ProgrammaticFixture;
import org.seedstack.coffig.node.MapNode;

public class ProgrammaticProviderTest {
    private ProgrammaticProvider programmaticProvider;

    @Before
    public void setUp() throws Exception {
        programmaticProvider = new ProgrammaticProvider();
        programmaticProvider.initialize(Coffig.basic());
    }

    @Test
    public void testProvideEmpty() throws Exception {
        MapNode mapNode = programmaticProvider.provide();

        assertThat(mapNode).isNotNull();
    }

    @Test
    public void testProvideWithSupplier() throws Exception {
        programmaticProvider.addSupplier(PrefixFixture::new);
        MapNode mapNode = programmaticProvider.provide();

        assertThat(mapNode).isNotNull();
        assertThat(mapNode.get("foo.bar").isPresent()).isTrue();
    }

    @Test
    public void testProvideWithObject() throws Exception {
        programmaticProvider.addObject(new ProgrammaticFixture());
        MapNode mapNode = programmaticProvider.provide();

        assertThat(mapNode).isNotNull();
        assertThat(mapNode.get("foo.bar.aString").get().value()).isEqualTo("provided");
        assertThat(mapNode.get("overridden.aString").get().value()).isEqualTo("provided");
    }
}
