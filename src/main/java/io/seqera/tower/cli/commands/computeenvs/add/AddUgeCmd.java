/*
 * Copyright (c) 2021, Seqera Labs.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This Source Code Form is "Incompatible With Secondary Licenses", as
 * defined by the Mozilla Public License, v. 2.0.
 */

package io.seqera.tower.cli.commands.computeenvs.add;

import io.seqera.tower.cli.commands.computeenvs.platforms.Platform;
import io.seqera.tower.cli.commands.computeenvs.platforms.UnivaPlatform;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;

@Command(
        name = "uge",
        description = "Add new UNIVA grid engine compute environment."
)
public class AddUgeCmd extends AbstractAddCmd {

    @Mixin
    public UnivaPlatform platform;

    @Override
    protected Platform getPlatform() {
        return platform;
    }
}
