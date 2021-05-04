/*
 * Copyright © 2013-2021, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.coffig.provider;

import java.util.HashMap;
import java.util.Map;
import mockit.Mock;
import mockit.MockUp;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.seedstack.coffig.node.MapNode;

public class EnvironmentProviderTest {
    private EnvironmentProvider underTest = new EnvironmentProvider();

    @Test
    public void testProvide() {
        new MockUp<System>() {
            @Mock
            java.util.Map<String, String> getenv() {
                Map<String, String> env = new HashMap<>();
                env.put("PROFILE", "DEV");
                return env;
            }
        };

        MapNode conf = underTest.provide();

        Assertions.assertThat(conf.get("PROFILE").get().value()).isEqualTo("DEV");
    }

    @Test
    public void testProvideEmptyMap() {
        new MockUp<System>() {
            @Mock
            java.util.Map<String, String> getenv() {
                return new HashMap<>();
            }
        };

        MapNode conf = underTest.provide();

        Assertions.assertThat(conf).isNotNull();
    }
}
