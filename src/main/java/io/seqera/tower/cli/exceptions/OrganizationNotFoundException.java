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

package io.seqera.tower.cli.exceptions;

public class OrganizationNotFoundException extends TowerException {

    public OrganizationNotFoundException(Long organizationId) {
        super(String.format("Organization '%d' not found", organizationId));
    }

    public OrganizationNotFoundException(String organizationName) {
        super(String.format("Organization '%s' not found", organizationName));
    }
}
