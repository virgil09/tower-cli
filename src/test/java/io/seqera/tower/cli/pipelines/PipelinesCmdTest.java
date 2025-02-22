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

/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package io.seqera.tower.cli.pipelines;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.seqera.tower.JSON;
import io.seqera.tower.cli.BaseCmdTest;
import io.seqera.tower.cli.commands.enums.OutputType;
import io.seqera.tower.cli.exceptions.MultiplePipelinesFoundException;
import io.seqera.tower.cli.exceptions.NoComputeEnvironmentException;
import io.seqera.tower.cli.exceptions.PipelineNotFoundException;
import io.seqera.tower.cli.exceptions.TowerException;
import io.seqera.tower.cli.exceptions.WorkspaceNotFoundException;
import io.seqera.tower.cli.responses.pipelines.PipelinesAdded;
import io.seqera.tower.cli.responses.pipelines.PipelinesDeleted;
import io.seqera.tower.cli.responses.pipelines.PipelinesExport;
import io.seqera.tower.cli.responses.pipelines.PipelinesList;
import io.seqera.tower.cli.responses.pipelines.PipelinesUpdated;
import io.seqera.tower.cli.responses.pipelines.PipelinesView;
import io.seqera.tower.cli.utils.ModelHelper;
import io.seqera.tower.model.ComputeEnv;
import io.seqera.tower.model.CreatePipelineRequest;
import io.seqera.tower.model.Launch;
import io.seqera.tower.model.PipelineDbDto;
import io.seqera.tower.model.WorkflowLaunchRequest;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockserver.client.MockServerClient;
import org.mockserver.model.MediaType;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.List;

import static io.seqera.tower.cli.commands.AbstractApiCmd.USER_WORKSPACE_NAME;
import static io.seqera.tower.cli.commands.AbstractApiCmd.buildWorkspaceRef;
import static io.seqera.tower.cli.utils.JsonHelper.parseJson;
import static org.apache.commons.lang3.StringUtils.chop;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockserver.matchers.Times.exactly;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

class PipelinesCmdTest extends BaseCmdTest {

    @Test
    void testUpdate(MockServerClient mock) {

        mock.when(
                request().withMethod("GET").withPath("/pipelines").withQueryStringParameter("search", "sleep_one_minute"), exactly(1)
        ).respond(
                response().withStatusCode(200).withBody("{\"pipelines\":[{\"pipelineId\":217997727159863,\"name\":\"sleep_one_minute\",\"description\":null,\"icon\":null,\"repository\":\"https://github.com/pditommaso/nf-sleep\",\"userId\":4,\"userName\":\"jordi\",\"userFirstName\":null,\"userLastName\":null,\"orgId\":null,\"orgName\":null,\"workspaceId\":null,\"workspaceName\":null,\"visibility\":null}],\"totalSize\":1}").withContentType(MediaType.APPLICATION_JSON)
        );

        mock.when(
                request().withMethod("GET").withPath("/pipelines/217997727159863/launch"), exactly(1)
        ).respond(
                response().withStatusCode(200).withBody(loadResource("pipelines_update")).withContentType(MediaType.APPLICATION_JSON)
        );

        mock.when(
                request().withMethod("PUT").withPath("/pipelines/217997727159863").withBody("{\"description\":\"Sleep one minute and exit\",\"launch\":{\"computeEnvId\":\"vYOK4vn7spw7bHHWBDXZ2\",\"pipeline\":\"https://github.com/pditommaso/nf-sleep\",\"workDir\":\"s3://nextflow-ci/jordeu\",\"paramsText\":\"timeout: 60\\n\",\"pullLatest\":false,\"stubRun\":false}}"), exactly(1)
        ).respond(
                response().withStatusCode(200).withBody("{\"pipeline\":{\"pipelineId\":217997727159863,\"name\":\"sleep_one_minute\",\"description\":\"Sleep one minute and exit\",\"icon\":null,\"repository\":\"https://github.com/pditommaso/nf-sleep\",\"userId\":4,\"userName\":\"jordi\",\"userFirstName\":null,\"userLastName\":null,\"orgId\":null,\"orgName\":null,\"workspaceId\":null,\"workspaceName\":null,\"visibility\":null}}").withContentType(MediaType.APPLICATION_JSON)
        );

        ExecOut out = exec(mock, "pipelines", "update", "-n", "sleep_one_minute", "-d", "Sleep one minute and exit");

        assertEquals("", out.stdErr);
        assertEquals(new PipelinesUpdated(USER_WORKSPACE_NAME, "sleep_one_minute").toString(), out.stdOut);
    }

    @Test
    void testUpdateComputeEnv(MockServerClient mock) {

        mock.reset();

        mock.when(
                request().withMethod("GET").withPath("/pipelines").withQueryStringParameter("search", "sleep_one_minute"), exactly(1)
        ).respond(
                response().withStatusCode(200).withBody("{\"pipelines\":[{\"pipelineId\":217997727159863,\"name\":\"sleep_one_minute\",\"description\":null,\"icon\":null,\"repository\":\"https://github.com/pditommaso/nf-sleep\",\"userId\":4,\"userName\":\"jordi\",\"userFirstName\":null,\"userLastName\":null,\"orgId\":null,\"orgName\":null,\"workspaceId\":null,\"workspaceName\":null,\"visibility\":null}],\"totalSize\":1}").withContentType(MediaType.APPLICATION_JSON)
        );

        mock.when(
                request().withMethod("GET").withPath("/pipelines/217997727159863/launch"), exactly(1)
        ).respond(
                response().withStatusCode(200).withBody(loadResource("pipelines_update")).withContentType(MediaType.APPLICATION_JSON)
        );

        mock.when(
                request().withMethod("GET").withPath("/compute-envs").withQueryStringParameter("status", "AVAILABLE"), exactly(1)
        ).respond(
                response().withStatusCode(200).withBody("{\"computeEnvs\":[{\"id\":\"isnEDBLvHDAIteOEF44ow\",\"name\":\"demo\",\"platform\":\"aws-batch\",\"status\":\"AVAILABLE\",\"message\":null,\"lastUsed\":null,\"primary\":null,\"workspaceName\":null,\"visibility\":null}]}").withContentType(MediaType.APPLICATION_JSON)
        );

        mock.when(
                request().withMethod("GET").withPath("/compute-envs/isnEDBLvHDAIteOEF44ow"), exactly(1)
        ).respond(
                response().withStatusCode(200).withBody(loadResource("compute_env_view")).withContentType(MediaType.APPLICATION_JSON)
        );

        mock.when(
                request().withMethod("PUT").withPath("/pipelines/217997727159863").withBody("{\"launch\":{\"computeEnvId\":\"isnEDBLvHDAIteOEF44ow\",\"pipeline\":\"https://github.com/pditommaso/nf-sleep\",\"workDir\":\"s3://nextflow-ci/jordeu\",\"paramsText\":\"timeout: 60\\n\",\"pullLatest\":false,\"stubRun\":false}}"), exactly(1)
        ).respond(
                response().withStatusCode(200).withBody("{\"pipeline\":{\"pipelineId\":217997727159863,\"name\":\"sleep_one_minute\",\"description\":\"Sleep one minute and exit\",\"icon\":null,\"repository\":\"https://github.com/pditommaso/nf-sleep\",\"userId\":4,\"userName\":\"jordi\",\"userFirstName\":null,\"userLastName\":null,\"orgId\":null,\"orgName\":null,\"workspaceId\":null,\"workspaceName\":null,\"visibility\":null}}").withContentType(MediaType.APPLICATION_JSON)
        );

        ExecOut out = exec(mock, "pipelines", "update", "-n", "sleep_one_minute", "-c", "demo");

        assertEquals("", out.stdErr);
        assertEquals(new PipelinesUpdated(USER_WORKSPACE_NAME, "sleep_one_minute").toString(), out.stdOut);
    }

    @Test
    void testAdd(MockServerClient mock) throws IOException {

        mock.when(
                request().withMethod("GET").withPath("/compute-envs").withQueryStringParameter("status", "AVAILABLE"), exactly(1)
        ).respond(
                response().withStatusCode(200).withBody("{\"computeEnvs\":[{\"id\":\"vYOK4vn7spw7bHHWBDXZ2\",\"name\":\"demo\",\"platform\":\"aws-batch\",\"status\":\"AVAILABLE\",\"message\":null,\"lastUsed\":null,\"primary\":true,\"workspaceName\":null,\"visibility\":null}]}").withContentType(MediaType.APPLICATION_JSON)
        );

        mock.when(
                request().withMethod("GET").withPath("/compute-envs/vYOK4vn7spw7bHHWBDXZ2"), exactly(1)
        ).respond(
                response().withStatusCode(200).withBody(loadResource("compute_env_demo")).withContentType(MediaType.APPLICATION_JSON)
        );

        mock.when(
                request().withMethod("POST").withPath("/pipelines").withBody("{\"name\":\"sleep_one_minute\",\"launch\":{\"computeEnvId\":\"vYOK4vn7spw7bHHWBDXZ2\",\"pipeline\":\"https://github.com/pditommaso/nf-sleep\",\"workDir\":\"s3://nextflow-ci/jordeu\",\"paramsText\":\"timeout: 60\\n\"}}"), exactly(1)
        ).respond(
                response().withStatusCode(200).withBody("{\"pipeline\":{\"pipelineId\":18388134856008,\"name\":\"sleep_one_minute\",\"description\":null,\"icon\":null,\"repository\":\"https://github.com/pditommaso/nf-sleep\",\"userId\":4,\"userName\":\"jordi\",\"userFirstName\":null,\"userLastName\":null,\"orgId\":null,\"orgName\":null,\"workspaceId\":null,\"workspaceName\":null,\"visibility\":null}}").withContentType(MediaType.APPLICATION_JSON)
        );

        ExecOut out = exec(mock, "pipelines", "add", "-n", "sleep_one_minute", "--params-file", tempFile("timeout: 60\n", "params", ".yml"), "https://github.com/pditommaso/nf-sleep");

        assertEquals("", out.stdErr);
        assertEquals(new PipelinesAdded(USER_WORKSPACE_NAME, "sleep_one_minute").toString(), out.stdOut);
        assertEquals(0, out.exitCode);

    }

    @Test
    void testAddWithComputeEnv(MockServerClient mock) {

        mock.when(
                request().withMethod("GET").withPath("/compute-envs").withQueryStringParameter("status", "AVAILABLE"), exactly(1)
        ).respond(
                response().withStatusCode(200).withBody("{\"computeEnvs\":[{\"id\":\"vYOK4vn7spw7bHHWBDXZ2\",\"name\":\"demo\",\"platform\":\"aws-batch\",\"status\":\"AVAILABLE\",\"message\":null,\"lastUsed\":null,\"primary\":null,\"workspaceName\":null,\"visibility\":null}]}").withContentType(MediaType.APPLICATION_JSON)
        );

        mock.when(
                request().withMethod("GET").withPath("/compute-envs/vYOK4vn7spw7bHHWBDXZ2"), exactly(1)
        ).respond(
                response().withStatusCode(200).withBody(loadResource("compute_env_demo")).withContentType(MediaType.APPLICATION_JSON)
        );

        mock.when(
                request().withMethod("POST").withPath("/pipelines").withBody("{\"name\":\"demo\",\"launch\":{\"computeEnvId\":\"vYOK4vn7spw7bHHWBDXZ2\",\"pipeline\":\"https://github.com/pditommaso/nf-sleep\",\"workDir\":\"s3://nextflow-ci/jordeu\"}}"), exactly(1)
        ).respond(
                response().withStatusCode(200).withBody("{\"pipeline\":{\"pipelineId\":18388134856008,\"name\":\"demo\",\"description\":null,\"icon\":null,\"repository\":\"https://github.com/pditommaso/nf-sleep\",\"userId\":4,\"userName\":\"jordi\",\"userFirstName\":null,\"userLastName\":null,\"orgId\":null,\"orgName\":null,\"workspaceId\":null,\"workspaceName\":null,\"visibility\":null}}").withContentType(MediaType.APPLICATION_JSON)
        );

        ExecOut out = exec(mock, "pipelines", "add", "-n", "demo", "-c", "demo", "https://github.com/pditommaso/nf-sleep");

        assertEquals("", out.stdErr);
        assertEquals(new PipelinesAdded(USER_WORKSPACE_NAME, "demo").toString(), out.stdOut);
        assertEquals(0, out.exitCode);

    }

    @Test
    void testAddWithStagingScripts(MockServerClient mock) throws IOException {

        mock.when(
                request().withMethod("GET").withPath("/compute-envs").withQueryStringParameter("status", "AVAILABLE"), exactly(1)
        ).respond(
                response().withStatusCode(200).withBody("{\"computeEnvs\":[{\"id\":\"vYOK4vn7spw7bHHWBDXZ2\",\"name\":\"demo\",\"platform\":\"aws-batch\",\"status\":\"AVAILABLE\",\"message\":null,\"lastUsed\":null,\"primary\":true,\"workspaceName\":null,\"visibility\":null}]}").withContentType(MediaType.APPLICATION_JSON)
        );

        mock.when(
                request().withMethod("GET").withPath("/compute-envs/vYOK4vn7spw7bHHWBDXZ2"), exactly(1)
        ).respond(
                response().withStatusCode(200).withBody(loadResource("compute_env_demo")).withContentType(MediaType.APPLICATION_JSON)
        );

        mock.when(
                request().withMethod("POST").withPath("/pipelines").withBody("{\"name\":\"staging\",\"launch\":{\"computeEnvId\":\"vYOK4vn7spw7bHHWBDXZ2\",\"pipeline\":\"https://github.com/pditommaso/nf-sleep\",\"workDir\":\"s3://nextflow-ci/staging\",\"preRunScript\":\"pre_run_this\",\"postRunScript\":\"post_run_this\"}}"), exactly(1)
        ).respond(
                response().withStatusCode(200).withBody("{\"pipeline\":{\"pipelineId\":21697594587521,\"name\":\"staging\",\"description\":null,\"icon\":null,\"repository\":\"https://github.com/pditommaso/nf-sleep\",\"userId\":4,\"userName\":\"jordi\",\"userFirstName\":null,\"userLastName\":null,\"orgId\":null,\"orgName\":null,\"workspaceId\":null,\"workspaceName\":null,\"visibility\":null}}").withContentType(MediaType.APPLICATION_JSON)
        );

        ExecOut out = exec(mock, "pipelines", "add", "-n", "staging", "--work-dir", "s3://nextflow-ci/staging", "--pre-run", tempFile("pre_run_this", "pre", "sh"), "--post-run", tempFile("post_run_this", "post", "sh"), "https://github.com/pditommaso/nf-sleep");

        assertEquals("", out.stdErr);
        assertEquals(new PipelinesAdded(USER_WORKSPACE_NAME, "staging").toString(), out.stdOut);
        assertEquals(0, out.exitCode);

    }

    @Test
    void testMissingComputeEnvironment(MockServerClient mock) {

        mock.when(
                request().withMethod("GET").withPath("/compute-envs").withQueryStringParameter("status", "AVAILABLE"), exactly(1)
        ).respond(
                response().withStatusCode(200).withBody("{\"computeEnvs\":[]}").withContentType(MediaType.APPLICATION_JSON)
        );

        ExecOut out = exec(mock, "pipelines", "add", "-n", "sleep_one_minute", "https://github.com/pditommaso/nf-sleep");

        assertEquals(errorMessage(out.app, new NoComputeEnvironmentException(USER_WORKSPACE_NAME)), out.stdErr);
        assertEquals("", out.stdOut);
        assertEquals(1, out.exitCode);

    }

    @Test
    void testDelete(MockServerClient mock) {
        mock.when(
                request().withMethod("GET").withPath("/pipelines").withQueryStringParameter("search", "sleep"), exactly(1)
        ).respond(
                response().withStatusCode(200).withBody(loadResource("pipelines_sleep")).withContentType(MediaType.APPLICATION_JSON)
        );

        mock.when(
                request().withMethod("DELETE").withPath("/pipelines/183522618315672"), exactly(1)
        ).respond(
                response().withStatusCode(204)
        );

        ExecOut out = exec(mock, "pipelines", "delete", "-n", "sleep");

        assertEquals("", out.stdErr);
        assertEquals(new PipelinesDeleted("sleep", USER_WORKSPACE_NAME).toString(), out.stdOut);
        assertEquals(0, out.exitCode);
    }

    @Test
    void testDeleteNotFound(MockServerClient mock) {
        mock.when(
                request().withMethod("GET").withPath("/pipelines").withQueryStringParameter("search", "sleep_all"), exactly(1)
        ).respond(
                response().withStatusCode(200).withBody("{\"pipelines\":[],\"totalSize\":0}").withContentType(MediaType.APPLICATION_JSON)
        );

        ExecOut out = exec(mock, "pipelines", "delete", "-n", "sleep_all");

        assertEquals(errorMessage(out.app, new PipelineNotFoundException("sleep_all", USER_WORKSPACE_NAME)), out.stdErr);
        assertEquals("", out.stdOut);
        assertEquals(1, out.exitCode);
    }

    @Test
    void testDeleteMultipleMatch(MockServerClient mock) {
        mock.when(
                request().withMethod("GET").withPath("/pipelines").withQueryStringParameter("search", "hello"), exactly(1)
        ).respond(
                response().withStatusCode(200).withBody(loadResource("pipelines_multiple")).withContentType(MediaType.APPLICATION_JSON)
        );

        ExecOut out = exec(mock, "pipelines", "delete", "-n", "hello");

        assertEquals(errorMessage(out.app, new MultiplePipelinesFoundException("hello", USER_WORKSPACE_NAME)), out.stdErr);
        assertEquals("", out.stdOut);
        assertEquals(1, out.exitCode);
    }

    @ParameterizedTest
    @EnumSource(OutputType.class)
    void testList(OutputType format, MockServerClient mock) {

        mock.when(
                request().withMethod("GET").withPath("/pipelines"), exactly(1)
        ).respond(
                response().withStatusCode(200).withBody(loadResource("pipelines_list")).withContentType(MediaType.APPLICATION_JSON)
        );

        mock.when(
                request().withMethod("GET").withPath("/user"), exactly(1)
        ).respond(
                response().withStatusCode(200).withBody(loadResource("user")).withContentType(MediaType.APPLICATION_JSON)
        );

        ExecOut out = exec(format, mock, "pipelines", "list");
        assertOutput(format, out, new PipelinesList(USER_WORKSPACE_NAME, List.of(
                new PipelineDbDto()
                        .pipelineId(183522618315672L)
                        .name("sleep_one_minute")
                        .repository("https://github.com/pditommaso/nf-sleep")
                        .userId(4L)
                        .userName("jordi")
        ), baseUserUrl(mock, USER_WORKSPACE_NAME)));
    }

    @Test
    void testListWithOffset(MockServerClient mock) {

        mock.when(
                request().withMethod("GET").withPath("/pipelines")
                        .withQueryStringParameter("offset", "1")
                        .withQueryStringParameter("max", "2"), exactly(1)
        ).respond(
                response().withStatusCode(200).withBody(loadResource("pipelines_list")).withContentType(MediaType.APPLICATION_JSON)
        );

        ExecOut out = exec(mock, "pipelines", "list", "--offset", "1", "--max", "2");

        assertEquals("", out.stdErr);
        assertEquals(chop(new PipelinesList(USER_WORKSPACE_NAME, List.of(
                new PipelineDbDto()
                        .pipelineId(183522618315672L)
                        .name("sleep_one_minute")
                        .repository("https://github.com/pditommaso/nf-sleep")
                        .userId(4L)
                        .userName("jordi")
        ), baseUserUrl(mock, USER_WORKSPACE_NAME)).toString()), out.stdOut);
        assertEquals(0, out.exitCode);
    }

    @Test
    void testListWithPage(MockServerClient mock) {

        mock.when(
                request().withMethod("GET").withPath("/pipelines")
                        .withQueryStringParameter("offset", "0")
                        .withQueryStringParameter("max", "2"), exactly(1)
        ).respond(
                response().withStatusCode(200).withBody(loadResource("pipelines_list")).withContentType(MediaType.APPLICATION_JSON)
        );

        mock.when(
                request().withMethod("GET").withPath("/user"), exactly(1)
        ).respond(
                response().withStatusCode(200).withBody(loadResource("user")).withContentType(MediaType.APPLICATION_JSON)
        );

        ExecOut out = exec(mock, "pipelines", "list", "--page", "1", "--max", "2");

        assertEquals("", out.stdErr);
        assertEquals(chop(new PipelinesList(USER_WORKSPACE_NAME, List.of(
                new PipelineDbDto()
                        .pipelineId(183522618315672L)
                        .name("sleep_one_minute")
                        .repository("https://github.com/pditommaso/nf-sleep")
                        .userId(4L)
                        .userName("jordi")
        ), baseUserUrl(mock, USER_WORKSPACE_NAME)).toString()), out.stdOut);
        assertEquals(0, out.exitCode);
    }

    @Test
    void testListWithConflictingPageable(MockServerClient mock) {

        mock.when(
                request().withMethod("GET").withPath("/pipelines")
                        .withQueryStringParameter("offset", "0")
                        .withQueryStringParameter("max", "2"), exactly(1)
        ).respond(
                response().withStatusCode(200).withBody(loadResource("pipelines_list")).withContentType(MediaType.APPLICATION_JSON)
        );

        ExecOut out = exec(mock, "pipelines", "list", "--page", "1", "--offset", "0", "--max", "2");

        assertEquals(errorMessage(out.app, new TowerException("Please use either --page or --offset as pagination parameter")), out.stdErr);
        assertEquals("", out.stdOut);
        assertEquals(1, out.exitCode);
    }

    @Test
    void testListEmpty(MockServerClient mock) {

        mock.when(
                request().withMethod("GET").withPath("/pipelines"), exactly(1)
        ).respond(
                response().withStatusCode(200).withBody("{ \"pipelines\": [], \"totalSize\": 0 }").withContentType(MediaType.APPLICATION_JSON)
        );

        mock.when(
                request().withMethod("GET").withPath("/user"), exactly(1)
        ).respond(
                response().withStatusCode(200).withBody(loadResource("user")).withContentType(MediaType.APPLICATION_JSON)
        );

        ExecOut out = exec(mock, "pipelines", "list");

        assertEquals("", out.stdErr);
        assertEquals(chop(new PipelinesList(USER_WORKSPACE_NAME, List.of(), baseUserUrl(mock, USER_WORKSPACE_NAME)).toString()), out.stdOut);
        assertEquals(0, out.exitCode);
    }

    @Test
    void testView(MockServerClient mock) throws JsonProcessingException {

        mock.when(
                request().withMethod("GET").withPath("/pipelines").withQueryStringParameter("search", "sleep_one_minute"), exactly(1)
        ).respond(
                response().withStatusCode(200).withBody("{\"pipelines\":[{\"pipelineId\":217997727159863,\"name\":\"sleep_one_minute\",\"description\":null,\"icon\":null,\"repository\":\"https://github.com/pditommaso/nf-sleep\",\"userId\":4,\"userName\":\"jordi\",\"userFirstName\":null,\"userLastName\":null,\"orgId\":null,\"orgName\":null,\"workspaceId\":null,\"workspaceName\":null,\"visibility\":null}],\"totalSize\":1}").withContentType(MediaType.APPLICATION_JSON)
        );

        mock.when(
                request().withMethod("GET").withPath("/pipelines/217997727159863/launch"), exactly(1)
        ).respond(
                response().withStatusCode(200).withBody(loadResource("pipelines_update")).withContentType(MediaType.APPLICATION_JSON)
        );

        mock.when(
                request().withMethod("GET").withPath("/user"), exactly(1)
        ).respond(
                response().withStatusCode(200).withBody(loadResource("user")).withContentType(MediaType.APPLICATION_JSON)
        );


        ExecOut out = exec(mock, "pipelines", "view", "-n", "sleep_one_minute");

        assertEquals("", out.stdErr);
        assertEquals(StringUtils.chop(new PipelinesView(
                        USER_WORKSPACE_NAME,
                        new PipelineDbDto().pipelineId(217997727159863L).name("sleep_one_minute").repository("https://github.com/pditommaso/nf-sleep"),
                        new Launch()
                                .id("oRptz8ekYa3BSA4Nnx7Qn")
                                .pipeline("https://github.com/pditommaso/nf-sleep")
                                .workDir("s3://nextflow-ci/jordeu")
                                .paramsText("timeout: 60\n")
                                .dateCreated(OffsetDateTime.parse("2021-09-08T06:50:54Z"))
                                .lastUpdated(OffsetDateTime.parse("2021-09-08T06:50:54Z"))
                                .resume(false)
                                .pullLatest(false)
                                .stubRun(false)
                                .computeEnv(
                                        parseJson("{\"id\": \"vYOK4vn7spw7bHHWBDXZ2\"}", ComputeEnv.class)
                                                .name("demo")
                                ),
                baseUserUrl(mock, USER_WORKSPACE_NAME)
                ).toString()), out.stdOut
        );
        assertEquals(0, out.exitCode);

    }

    @Test
    void testListFromWorkspace(MockServerClient mock) {

        mock.when(
                request().withMethod("GET").withPath("/user"), exactly(1)
        ).respond(
                response().withStatusCode(200).withBody(loadResource("user")).withContentType(MediaType.APPLICATION_JSON)
        );

        mock.when(
                request().withMethod("GET").withPath("/user/1264/workspaces"), exactly(1)
        ).respond(
                response().withStatusCode(200).withBody("{\"orgsAndWorkspaces\":[{\"orgId\":166815615776895,\"name\":\"Seqera\",\"orgLogoUrl\":null,\"workspaceId\":null,\"workspaceName\":null},{\"orgId\":166815615776895,\"orgName\":\"Seqera\",\"orgLogoUrl\":null,\"workspaceId\":222756650686576,\"workspaceName\":\"cli\"}]}").withContentType(MediaType.APPLICATION_JSON)
        );

        mock.when(
                request().withMethod("GET").withPath("/pipelines").withQueryStringParameter("workspaceId", "222756650686576"), exactly(1)
        ).respond(
                response().withStatusCode(200).withBody("{\"pipelines\":[],\"totalSize\":0}").withContentType(MediaType.APPLICATION_JSON)
        );

        ExecOut out = exec(mock, "pipelines", "list", "-w", "222756650686576");

        assertEquals("", out.stdErr);
        assertEquals(chop(new PipelinesList(buildWorkspaceRef("Seqera", "cli"), List.of(), baseWorkspaceUrl(mock, "Seqera", "cli")).toString()), out.stdOut);
        assertEquals(0, out.exitCode);
    }

    @Test
    void testListFromWorkspaceNotFound(MockServerClient mock) {

        mock.when(
                request().withMethod("GET").withPath("/user"), exactly(1)
        ).respond(
                response().withStatusCode(200).withBody(loadResource("user")).withContentType(MediaType.APPLICATION_JSON)
        );

        mock.when(
                request().withMethod("GET").withPath("/user/1264/workspaces"), exactly(1)
        ).respond(
                response().withStatusCode(200).withBody("{\"orgsAndWorkspaces\":[{\"orgId\":166815615776895,\"name\":\"Seqera\",\"orgLogoUrl\":null,\"workspaceId\":null,\"workspaceName\":null},{\"orgId\":166815615776895,\"orgName\":\"Seqera\",\"orgLogoUrl\":null,\"workspaceId\":222756650686576,\"workspaceName\":\"cli\"}]}").withContentType(MediaType.APPLICATION_JSON)
        );

        ExecOut out = exec(mock, "pipelines", "list", "-w", "222756650686577");

        assertEquals(errorMessage(out.app, new WorkspaceNotFoundException(222756650686577L)), out.stdErr);
        assertEquals("", out.stdOut);
        assertEquals(1, out.exitCode);
    }

    @Test
    void testExport(MockServerClient mock) throws JsonProcessingException {
        mock.when(
                request().withMethod("GET").withPath("/pipelines").withQueryStringParameter("search", "sleep"), exactly(1)
        ).respond(
                response().withStatusCode(200).withBody(loadResource("pipelines_sleep")).withContentType(MediaType.APPLICATION_JSON)
        );

        mock.when(
                request().withMethod("GET").withPath("/pipelines/183522618315672/launch"), exactly(1)
        ).respond(
                response().withStatusCode(200).withBody(loadResource("pipelines_update")).withContentType(MediaType.APPLICATION_JSON)
        );

        ExecOut out = exec(mock, "pipelines", "export", "-n", "sleep");

        WorkflowLaunchRequest workflowLaunchRequest = ModelHelper.createLaunchRequest(parseJson(new String(loadResource("launch"), StandardCharsets.UTF_8), Launch.class));
        PipelineDbDto pipeline = parseJson(new String(loadResource("pipelines_sleep"), StandardCharsets.UTF_8), PipelineDbDto.class);

        CreatePipelineRequest createPipelineRequest = new CreatePipelineRequest();
        createPipelineRequest.setDescription(pipeline.getDescription());
        createPipelineRequest.setIcon(pipeline.getIcon());
        createPipelineRequest.setLaunch(workflowLaunchRequest);

        String configOutput = new JSON().getContext(CreatePipelineRequest.class).writerWithDefaultPrettyPrinter().writeValueAsString(createPipelineRequest);

        assertEquals("", out.stdErr);
        assertEquals(new PipelinesExport(configOutput, null).toString(), out.stdOut);
        assertEquals(0, out.exitCode);
    }

    @Test
    void testImport(MockServerClient mock) throws IOException {

        mock.when(
                request().withMethod("GET").withPath("/compute-envs"), exactly(1)
        ).respond(
                response().withStatusCode(200).withBody("{\"computeEnvs\":[{\"id\":\"isnEDBLvHDAIteOEF44ow\",\"name\":\"demo\",\"platform\":\"aws-batch\",\"status\":\"AVAILABLE\",\"message\":null,\"lastUsed\":null,\"primary\":null,\"workspaceName\":null,\"visibility\":null}]}").withContentType(MediaType.APPLICATION_JSON)
        );

        mock.when(
                request().withMethod("GET").withPath("/compute-envs/isnEDBLvHDAIteOEF44ow"), exactly(1)
        ).respond(
                response().withStatusCode(200).withBody(loadResource("compute_env_view")).withContentType(MediaType.APPLICATION_JSON)
        );

        mock.when(
                request().withMethod("POST").withPath("/pipelines")
                        .withBody("{\"name\":\"pipelineNew\",\"launch\":{\"computeEnvId\":\"isnEDBLvHDAIteOEF44ow\",\"pipeline\":\"https://github.com/grananda/nextflow-hello\",\"workDir\":\"s3://nextflow-ci/julio\",\"revision\":\"main\",\"resume\":false,\"pullLatest\":false,\"stubRun\":false}}")
                        .withContentType(MediaType.APPLICATION_JSON), exactly(1)
        ).respond(
                response()
                        .withStatusCode(200)
                        .withBody(loadResource("pipelines_add_response"))
                        .withContentType(MediaType.APPLICATION_JSON)
        );

        ExecOut out = exec(mock, "pipelines", "import", tempFile(new String(loadResource("pipelines_add"), StandardCharsets.UTF_8), "data", ".json"), "-n", "pipelineNew");

        assertEquals("", out.stdErr);
        assertEquals(new PipelinesAdded(USER_WORKSPACE_NAME, "pipelineNew").toString(), out.stdOut);
        assertEquals(0, out.exitCode);
    }

    @Test
    void testImportWithComputeEnv(MockServerClient mock) throws IOException {

        mock.when(
                request().withMethod("GET").withPath("/compute-envs"), exactly(1)
        ).respond(
                response().withStatusCode(200).withBody("{\"computeEnvs\":[{\"id\":\"isnEDBLvHDAIteOEF44ow\",\"name\":\"demo\",\"platform\":\"aws-batch\",\"status\":\"AVAILABLE\",\"message\":null,\"lastUsed\":null,\"primary\":null,\"workspaceName\":null,\"visibility\":null}]}").withContentType(MediaType.APPLICATION_JSON)
        );

        mock.when(
                request().withMethod("GET").withPath("/compute-envs/isnEDBLvHDAIteOEF44ow"), exactly(1)
        ).respond(
                response().withStatusCode(200).withBody(loadResource("compute_env_view")).withContentType(MediaType.APPLICATION_JSON)
        );

        mock.when(
                request().withMethod("POST").withPath("/pipelines")
                        .withBody("{\"name\":\"pipelineNew\",\"launch\":{\"computeEnvId\":\"isnEDBLvHDAIteOEF44ow\",\"pipeline\":\"https://github.com/grananda/nextflow-hello\",\"workDir\":\"s3://nextflow-ci/julio\",\"revision\":\"main\",\"resume\":false,\"pullLatest\":false,\"stubRun\":false}}")
                        .withContentType(MediaType.APPLICATION_JSON), exactly(1)
        ).respond(
                response()
                        .withStatusCode(200)
                        .withBody(loadResource("pipelines_add_response"))
                        .withContentType(MediaType.APPLICATION_JSON)
        );

        ExecOut out = exec(mock, "pipelines", "import", tempFile(new String(loadResource("pipelines_add"), StandardCharsets.UTF_8), "data", ".json"), "-n", "pipelineNew", "-c", "demo");

        assertEquals("", out.stdErr);
        assertEquals(new PipelinesAdded(USER_WORKSPACE_NAME, "pipelineNew").toString(), out.stdOut);
        assertEquals(0, out.exitCode);
    }

    @Test
    void testImportWithoutWorkdir(MockServerClient mock) throws IOException {

        mock.when(
                request().withMethod("GET").withPath("/compute-envs"), exactly(1)
        ).respond(
                response().withStatusCode(200).withBody("{\"computeEnvs\":[{\"id\":\"isnEDBLvHDAIteOEF44ow\",\"name\":\"demo\",\"platform\":\"aws-batch\",\"status\":\"AVAILABLE\",\"message\":null,\"lastUsed\":null,\"primary\":null,\"workspaceName\":null,\"visibility\":null}]}").withContentType(MediaType.APPLICATION_JSON)
        );

        mock.when(
                request().withMethod("GET").withPath("/compute-envs/isnEDBLvHDAIteOEF44ow"), exactly(1)
        ).respond(
                response().withStatusCode(200).withBody(loadResource("compute_env_view")).withContentType(MediaType.APPLICATION_JSON)
        );

        mock.when(
                request().withMethod("POST").withPath("/pipelines")
                        .withBody("{\"name\":\"pipelineNew\",\"launch\":{\"computeEnvId\":\"isnEDBLvHDAIteOEF44ow\",\"pipeline\":\"https://github.com/grananda/nextflow-hello\",\"workDir\":\"s3://nextflow-ci/jordeu\",\"revision\":\"main\",\"resume\":false,\"pullLatest\":false,\"stubRun\":false}}")
                        .withContentType(MediaType.APPLICATION_JSON), exactly(1)
        ).respond(
                response()
                        .withStatusCode(200)
                        .withBody(loadResource("pipelines_add_response"))
                        .withContentType(MediaType.APPLICATION_JSON)
        );

        ExecOut out = exec(mock, "pipelines", "import", tempFile(new String(loadResource("pipelines_add_no_workdir"), StandardCharsets.UTF_8), "data", ".json"), "-n", "pipelineNew", "-c", "demo");

        assertEquals("", out.stdErr);
        assertEquals(new PipelinesAdded(USER_WORKSPACE_NAME, "pipelineNew").toString(), out.stdOut);
        assertEquals(0, out.exitCode);
    }
}
