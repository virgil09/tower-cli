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

package io.seqera.tower.cli.commands.teams;

import io.seqera.tower.ApiException;
import io.seqera.tower.cli.responses.Response;
import io.seqera.tower.cli.responses.teams.TeamAdded;
import io.seqera.tower.model.CreateTeamRequest;
import io.seqera.tower.model.CreateTeamResponse;
import io.seqera.tower.model.OrgAndWorkspaceDbDto;
import io.seqera.tower.model.Team;
import picocli.CommandLine;

import java.io.IOException;

@CommandLine.Command(
        name = "add",
        description = "Add a new organization team."
)
public class AddCmd extends AbstractTeamsCmd {

    @CommandLine.Option(names = {"-n", "--name"}, description = "Team name.", required = true)
    public String teamName;

    @CommandLine.Option(names = {"-o", "--organization"}, description = "Organization name or identifier.", required = true)
    public String organizationRef;

    @CommandLine.Option(names = {"-d", "--description"}, description = "Team description.")
    public String teamDescription;

    @Override
    protected Response exec() throws ApiException, IOException {
        OrgAndWorkspaceDbDto orgAndWorkspaceDbDto = findOrganizationByRef(organizationRef);

        Team team = new Team();
        team.setName(teamName);
        team.setDescription(teamDescription);

        CreateTeamRequest request = new CreateTeamRequest();
        request.setTeam(team);

        CreateTeamResponse response = api().createOrganizationTeam(orgAndWorkspaceDbDto.getOrgId(), request);

        return new TeamAdded(organizationRef, teamName);
    }
}
