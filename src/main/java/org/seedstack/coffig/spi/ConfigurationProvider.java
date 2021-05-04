/*
 * Copyright © 2013-2021, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.coffig.spi;

import java.util.concurrent.Callable;
import org.seedstack.coffig.node.MapNode;

@FunctionalInterface
public interface ConfigurationProvider extends Callable<MapNode>, ConfigurationComponent {
    MapNode provide();

    @Override
    default MapNode call() throws Exception {
        return provide();
    }
}
