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

package io.seqera.tower.cli.responses.teams;

import io.seqera.tower.cli.responses.Response;

public class TeamAdded extends Response {

    public final String organizationName;
    public final String teamName;

    public TeamAdded(String organizationName, String teamName) {
        this.organizationName = organizationName;
        this.teamName = teamName;
    }

    @Override
    public String toString() {
        return ansi(String.format("%n  @|yellow A '%s' team added for '%s' organization|@%n", teamName, organizationName));
    }

}
