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

package io.seqera.tower.cli.commands.pipelines;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.seqera.tower.ApiException;
import io.seqera.tower.JSON;
import io.seqera.tower.cli.commands.global.WorkspaceOptions;
import io.seqera.tower.cli.responses.Response;
import io.seqera.tower.cli.responses.pipelines.PipelinesExport;
import io.seqera.tower.cli.utils.FilesHelper;
import io.seqera.tower.cli.utils.ModelHelper;
import io.seqera.tower.model.CreatePipelineRequest;
import io.seqera.tower.model.DescribeLaunchResponse;
import io.seqera.tower.model.PipelineDbDto;
import io.seqera.tower.model.WorkflowLaunchRequest;
import picocli.CommandLine;

@CommandLine.Command(
        name = "export",
        description = "Export a workspace pipeline for further creation"
)
public class ExportCmd extends AbstractPipelinesCmd{

    @CommandLine.Option(names = {"-n", "--name"}, description = "Pipeline name", required = true)
    public String name;

    @CommandLine.Mixin
    public WorkspaceOptions workspace;

    @CommandLine.Parameters(index = "0", paramLabel = "FILENAME", description = "File name to export", arity = "0..1")
    String fileName = null;

    @Override
    protected Response exec() throws ApiException {
        PipelineDbDto pipeline = pipelineByName(workspace.workspaceId, name);
        DescribeLaunchResponse resp = api().describePipelineLaunch(pipeline.getPipelineId(), workspace.workspaceId);

        WorkflowLaunchRequest workflowLaunchRequest = ModelHelper.createLaunchRequest(resp.getLaunch());

        CreatePipelineRequest createPipelineRequest = new CreatePipelineRequest();
        createPipelineRequest.setDescription(pipeline.getDescription());
        createPipelineRequest.setIcon(pipeline.getIcon());
        createPipelineRequest.setLaunch(workflowLaunchRequest);

        String configOutput = "";

        try {
            configOutput = new JSON().getContext(CreatePipelineRequest.class).writerWithDefaultPrettyPrinter().writeValueAsString(createPipelineRequest);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        if (fileName != null && !fileName.equals("-")) {
            FilesHelper.saveString(fileName, configOutput);
        }

        return new PipelinesExport(configOutput, fileName);
    }
}