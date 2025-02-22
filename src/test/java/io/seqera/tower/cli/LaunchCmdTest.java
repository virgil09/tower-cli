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
package io.seqera.tower.cli;

import io.seqera.tower.ApiException;
import io.seqera.tower.cli.commands.enums.OutputType;
import io.seqera.tower.cli.exceptions.InvalidResponseException;
import io.seqera.tower.cli.responses.runs.RunSubmited;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockserver.client.MockServerClient;
import org.mockserver.model.MediaType;

import java.io.IOException;

import static io.seqera.tower.cli.commands.AbstractApiCmd.USER_WORKSPACE_NAME;
import static io.seqera.tower.cli.commands.AbstractApiCmd.buildWorkspaceRef;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockserver.matchers.Times.exactly;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

class LaunchCmdTest extends BaseCmdTest {

    @Test
    void testInvalidAuth(MockServerClient mock) {

        // Create server expectation
        mock.when(
                request().withMethod("GET").withPath("/pipelines"), exactly(1)
        ).respond(
                response().withStatusCode(401)
        );

        // Run the command
        ExecOut out = exec(mock, "launch", "hello");

        // Assert results
        assertEquals(errorMessage(out.app, new ApiException(401, "Unauthorized")), out.stdErr);
        assertEquals("", out.stdOut);
        assertEquals(1, out.exitCode);
    }

    @Test
    void testPipelineNotfound(MockServerClient mock) {

        // Create server expectation
        mock.when(
                request().withMethod("GET").withPath("/pipelines"), exactly(1)
        ).respond(
                response().withStatusCode(200).withBody(loadResource("pipelines_none")).withContentType(MediaType.APPLICATION_JSON)
        );

        // Run the command
        ExecOut out = exec(mock, "launch", "hello");

        // Assert results
        assertEquals(errorMessage(out.app, new InvalidResponseException("Pipeline 'hello' not found on this workspace.")), out.stdErr);
        assertEquals(1, out.exitCode);
    }

    @Test
    void testMultiplePipelinesFound(MockServerClient mock) {

        mock.reset();

        // Create server expectation
        mock.when(
                request().withMethod("GET").withPath("/pipelines"), exactly(1)
        ).respond(
                response().withStatusCode(200).withBody(loadResource("pipelines_multiple")).withContentType(MediaType.APPLICATION_JSON)
        );

        // Run the command
        ExecOut out = exec(mock, "launch", "hello");

        // Assert results
        assertEquals(errorMessage(out.app, new InvalidResponseException("Multiple pipelines match 'hello'")), out.stdErr);
        assertEquals(1, out.exitCode);
    }

    @ParameterizedTest
    @EnumSource(OutputType.class)
    void testSubmitLaunchpadPipeline(OutputType format, MockServerClient mock) {

        // Create server expectation
        mock.when(
                request().withMethod("GET").withPath("/pipelines"), exactly(1)
        ).respond(
                response().withStatusCode(200).withBody(loadResource("pipelines_sarek")).withContentType(MediaType.APPLICATION_JSON)
        );

        mock.when(
                request().withMethod("GET").withPath("/pipelines/250911634275687/launch"), exactly(1)
        ).respond(
                response().withStatusCode(200).withBody(loadResource("pipeline_launch_describe")).withContentType(MediaType.APPLICATION_JSON)
        );

        mock.when(
                request().withMethod("POST").withPath("/workflow/launch").withBody("{\"launch\":{\"id\":\"5nmCvXcarkvv8tELMF4KyY\",\"computeEnvId\":\"4X7YrYJp9B1d1DUpfur7DS\",\"pipeline\":\"https://github.com/nf-core/sarek\",\"workDir\":\"/efs\",\"pullLatest\":false,\"stubRun\":false}}"), exactly(1)
        ).respond(
                response().withStatusCode(200).withBody(loadResource("workflow_launch")).withContentType(MediaType.APPLICATION_JSON)
        );

        mock.when(
                request().withMethod("GET").withPath("/user"), exactly(1)
        ).respond(
                response().withStatusCode(200).withBody(loadResource("user")).withContentType(MediaType.APPLICATION_JSON)
        );

        // Run the command
        ExecOut out = exec(format, mock, "launch", "sarek");

        // Assert results
        assertOutput(format, out, new RunSubmited("35aLiS0bIM5efd", null, baseUserUrl(mock, "jordi"), USER_WORKSPACE_NAME));
    }

    @ParameterizedTest
    @EnumSource(OutputType.class)
    void testSubmitGithubPipeline(OutputType format, MockServerClient mock) {

        mock.when(
                request().withMethod("GET").withPath("/compute-envs").withQueryStringParameter("status", "AVAILABLE"), exactly(1)
        ).respond(
                response().withStatusCode(200).withBody("{\"computeEnvs\":[{\"id\":\"1uJweHHZTo7gydE6pyDt7x\",\"name\":\"demo\",\"platform\":\"aws-batch\",\"status\":\"AVAILABLE\",\"message\":null,\"lastUsed\":null,\"primary\":true,\"workspaceName\":null,\"visibility\":null},{\"id\":\"3bBgyqQrehvoihCVjunUaJ\",\"name\":\"google\",\"platform\":\"google-lifesciences\",\"status\":\"AVAILABLE\",\"message\":null,\"lastUsed\":null,\"primary\":null,\"workspaceName\":null,\"visibility\":null},{\"id\":\"53aWhB2qJroy0i51FOrFAC\",\"name\":\"manual\",\"platform\":\"aws-batch\",\"status\":\"AVAILABLE\",\"message\":null,\"lastUsed\":null,\"primary\":null,\"workspaceName\":null,\"visibility\":null},{\"id\":\"7TZgco4ZknMHk4W4DzB8dH\",\"name\":\"google\",\"platform\":\"google-lifesciences\",\"status\":\"AVAILABLE\",\"message\":null,\"lastUsed\":null,\"primary\":null,\"workspaceName\":null,\"visibility\":null},{\"id\":\"NDEIULtY1a08q16osv8kg\",\"name\":\"demo\",\"platform\":\"aws-batch\",\"status\":\"AVAILABLE\",\"message\":null,\"lastUsed\":null,\"primary\":null,\"workspaceName\":null,\"visibility\":null},{\"id\":\"isnEDBLvHDAIteOEF44ow\",\"name\":\"demo\",\"platform\":\"aws-batch\",\"status\":\"AVAILABLE\",\"message\":null,\"lastUsed\":null,\"primary\":null,\"workspaceName\":null,\"visibility\":null}]}").withContentType(MediaType.APPLICATION_JSON)
        );

        mock.when(
                request().withMethod("GET").withPath("/compute-envs/1uJweHHZTo7gydE6pyDt7x"), exactly(1)
        ).respond(
                response().withStatusCode(200).withBody("{\"computeEnv\":{\"id\":\"1uJweHHZTo7gydE6pyDt7x\",\"name\":\"demo\",\"description\":null,\"platform\":\"aws-batch\",\"config\":{\"region\":\"eu-west-1\",\"computeQueue\":\"TowerForge-1uJweHHZTo7gydE6pyDt7x-work\",\"computeJobRole\":null,\"headQueue\":\"TowerForge-1uJweHHZTo7gydE6pyDt7x-head\",\"headJobRole\":null,\"cliPath\":\"/home/ec2-user/miniconda/bin/aws\",\"volumes\":[],\"workDir\":\"s3://nextflow-ci/jordeu\",\"preRunScript\":null,\"postRunScript\":null,\"headJobCpus\":null,\"headJobMemoryMb\":null,\"forge\":{\"type\":\"SPOT\",\"minCpus\":0,\"maxCpus\":123,\"gpuEnabled\":false,\"ebsAutoScale\":true,\"instanceTypes\":null,\"allocStrategy\":null,\"imageId\":null,\"vpcId\":null,\"subnets\":null,\"securityGroups\":null,\"fsxMount\":null,\"fsxName\":null,\"fsxSize\":null,\"disposeOnDeletion\":true,\"ec2KeyPair\":null,\"allowBuckets\":null,\"ebsBlockSize\":null,\"fusionEnabled\":false,\"bidPercentage\":null,\"efsCreate\":true,\"efsId\":null,\"efsMount\":null},\"forgedResources\":[{\"IamRole\":\"arn:aws:iam::195996028523:role/TowerForge-1uJweHHZTo7gydE6pyDt7x-ServiceRole\"},{\"IamRole\":\"arn:aws:iam::195996028523:role/TowerForge-1uJweHHZTo7gydE6pyDt7x-FleetRole\"},{\"IamInstanceProfile\":\"arn:aws:iam::195996028523:instance-profile/TowerForge-1uJweHHZTo7gydE6pyDt7x-InstanceRole\"},{\"EfsId\":\"fs-cb8117ff\"},{\"Ec2LaunchTemplate\":\"TowerForge-1uJweHHZTo7gydE6pyDt7x\"},{\"BatchEnv\":\"arn:aws:batch:eu-west-1:195996028523:compute-environment/TowerForge-1uJweHHZTo7gydE6pyDt7x-head\"},{\"BatchQueue\":\"arn:aws:batch:eu-west-1:195996028523:job-queue/TowerForge-1uJweHHZTo7gydE6pyDt7x-head\"},{\"BatchEnv\":\"arn:aws:batch:eu-west-1:195996028523:compute-environment/TowerForge-1uJweHHZTo7gydE6pyDt7x-work\"},{\"BatchQueue\":\"arn:aws:batch:eu-west-1:195996028523:job-queue/TowerForge-1uJweHHZTo7gydE6pyDt7x-work\"}],\"discriminator\":\"aws-batch\"},\"dateCreated\":\"2021-09-08T18:52:10Z\",\"lastUpdated\":\"2021-09-08T18:54:16Z\",\"lastUsed\":null,\"deleted\":null,\"status\":\"AVAILABLE\",\"message\":null,\"primary\":null,\"credentialsId\":\"6g0ER59L4ZoE5zpOmUP48D\"}}").withContentType(MediaType.APPLICATION_JSON)
        );

        mock.when(
                request().withMethod("POST").withPath("/workflow/launch").withBody("{\"launch\":{\"computeEnvId\":\"1uJweHHZTo7gydE6pyDt7x\",\"pipeline\":\"https://github.com/nextflow-io/hello\",\"workDir\":\"s3://nextflow-ci/jordeu\"}}"), exactly(1)
        ).respond(
                response().withStatusCode(200).withBody("{\"workflowId\":\"57ojrWRzTyous\"}").withContentType(MediaType.APPLICATION_JSON)
        );

        mock.when(
                request().withMethod("GET").withPath("/user"), exactly(1)
        ).respond(
                response().withStatusCode(200).withBody(loadResource("user")).withContentType(MediaType.APPLICATION_JSON)
        );

        ExecOut out = exec(format, mock, "launch", "https://github.com/nextflow-io/hello");

        assertOutput(format, out, new RunSubmited("57ojrWRzTyous", null, baseUserUrl(mock, "jordi"), USER_WORKSPACE_NAME));
    }

    @Test
    void testSubmitLaunchpadPipelineWithAdvancedOptions(MockServerClient mock) throws IOException {

        // Create server expectation
        mock.when(
                request().withMethod("GET").withPath("/pipelines"), exactly(1)
        ).respond(
                response().withStatusCode(200).withBody(loadResource("pipelines_sarek")).withContentType(MediaType.APPLICATION_JSON)
        );

        mock.when(
                request().withMethod("GET").withPath("/pipelines/250911634275687/launch"), exactly(1)
        ).respond(
                response().withStatusCode(200).withBody(loadResource("pipeline_launch_describe")).withContentType(MediaType.APPLICATION_JSON)
        );

        mock.when(
                request().withMethod("POST").withPath("/workflow/launch").withBody("{\"launch\":{\"id\":\"5nmCvXcarkvv8tELMF4KyY\",\"computeEnvId\":\"4X7YrYJp9B1d1DUpfur7DS\",\"pipeline\":\"https://github.com/nf-core/sarek\",\"workDir\":\"/my_work_dir\",\"revision\":\"develop\",\"configProfiles\":[\"test\",\"docker\"],\"configText\":\"extra_config\",\"preRunScript\":\"pre_run_me\",\"postRunScript\":\"post_run_me\",\"mainScript\":\"alternate.nf\",\"entryName\":\"dsl2\",\"schemaName\":\"my_schema.json\",\"pullLatest\":true,\"stubRun\":true}}"), exactly(1)
        ).respond(
                response().withStatusCode(200).withBody(loadResource("workflow_launch")).withContentType(MediaType.APPLICATION_JSON)
        );

        mock.when(
                request().withMethod("GET").withPath("/user"), exactly(1)
        ).respond(
                response().withStatusCode(200).withBody(loadResource("user")).withContentType(MediaType.APPLICATION_JSON)
        );

        // Run the command
        ExecOut out = exec(mock, "launch", "sarek", "-p", "test,docker", "-r", "develop", "--work-dir", "/my_work_dir",
                "--config", tempFile("extra_config", "nextflow", "config"), "--pull-latest", "--stub-run",
                "--pre-run", tempFile("pre_run_me", "pre", "sh"), "--post-run", tempFile("post_run_me", "post", "sh"),
                "--main-script", "alternate.nf", "--entry-name", "dsl2", "--schema-name", "my_schema.json");

        // Assert results
        assertEquals("", out.stdErr);
        assertEquals(new RunSubmited("35aLiS0bIM5efd", 1L, baseUserUrl(mock, "jordi"), USER_WORKSPACE_NAME).toString(), out.stdOut);
        assertEquals(0, out.exitCode);
    }

    @ParameterizedTest
    @EnumSource(OutputType.class)
    void testSubmitLaunchpadPipelineWithCustomName(OutputType format, MockServerClient mock) {

        // Create server expectation
        mock.when(
                request().withMethod("GET").withPath("/pipelines"), exactly(1)
        ).respond(
                response().withStatusCode(200).withBody(loadResource("pipelines_sarek")).withContentType(MediaType.APPLICATION_JSON)
        );

        mock.when(
                request().withMethod("GET").withPath("/pipelines/250911634275687/launch"), exactly(1)
        ).respond(
                response().withStatusCode(200).withBody(loadResource("pipeline_launch_describe")).withContentType(MediaType.APPLICATION_JSON)
        );

        mock.when(
                request().withMethod("POST").withPath("/workflow/launch").withBody("{\"launch\":{\"id\":\"5nmCvXcarkvv8tELMF4KyY\",\"computeEnvId\":\"4X7YrYJp9B1d1DUpfur7DS\",\"runName\":\"custom_run_name\",\"pipeline\":\"https://github.com/nf-core/sarek\",\"workDir\":\"/efs\",\"pullLatest\":false,\"stubRun\":false}}"), exactly(1)
        ).respond(
                response().withStatusCode(200).withBody(loadResource("workflow_launch")).withContentType(MediaType.APPLICATION_JSON)
        );

        mock.when(
                request().withMethod("GET").withPath("/user"), exactly(1)
        ).respond(
                response().withStatusCode(200).withBody(loadResource("user")).withContentType(MediaType.APPLICATION_JSON)
        );

        // Run the command
        ExecOut out = exec(format, mock, "launch", "sarek", "-n", "custom_run_name");

        // Assert results
        assertOutput(format, out, new RunSubmited("35aLiS0bIM5efd", null, baseUserUrl(mock, "jordi"), USER_WORKSPACE_NAME));
    }

    @Test
    void testSubmitToAWorkspace(MockServerClient mock) {

        mock.when(
                request().withMethod("GET").withPath("/compute-envs").withQueryStringParameter("status", "AVAILABLE").withQueryStringParameter("workspaceId", "222756650686576"), exactly(1)
        ).respond(
                response().withStatusCode(200).withBody("{\"computeEnvs\":[{\"id\":\"4iqCDE6C2Stq0jzBsHJvHn\",\"name\":\"aws\",\"platform\":\"aws-batch\",\"status\":\"AVAILABLE\",\"message\":null,\"lastUsed\":null,\"primary\":true,\"workspaceName\":\"cli\",\"visibility\":\"PRIVATE\"}]}").withContentType(MediaType.APPLICATION_JSON)
        );

        mock.when(
                request().withMethod("GET").withPath("/compute-envs/4iqCDE6C2Stq0jzBsHJvHn").withQueryStringParameter("workspaceId", "222756650686576"), exactly(1)
        ).respond(
                response().withStatusCode(200).withBody("{\"computeEnv\":{\"id\":\"4iqCDE6C2Stq0jzBsHJvHn\",\"name\":\"aws\",\"description\":null,\"platform\":\"aws-batch\",\"config\":{\"region\":\"eu-west-1\",\"computeQueue\":\"TowerForge-4iqCDE6C2Stq0jzBsHJvHn-work\",\"computeJobRole\":null,\"headQueue\":\"TowerForge-4iqCDE6C2Stq0jzBsHJvHn-head\",\"headJobRole\":null,\"cliPath\":\"/home/ec2-user/miniconda/bin/aws\",\"volumes\":[],\"workDir\":\"s3://nextflow-ci/jordeu\",\"preRunScript\":null,\"postRunScript\":null,\"headJobCpus\":null,\"headJobMemoryMb\":null,\"forge\":{\"type\":\"SPOT\",\"minCpus\":0,\"maxCpus\":123,\"gpuEnabled\":false,\"ebsAutoScale\":true,\"instanceTypes\":[],\"allocStrategy\":null,\"imageId\":null,\"vpcId\":null,\"subnets\":[],\"securityGroups\":[],\"fsxMount\":null,\"fsxName\":null,\"fsxSize\":null,\"disposeOnDeletion\":true,\"ec2KeyPair\":null,\"allowBuckets\":[],\"ebsBlockSize\":null,\"fusionEnabled\":false,\"bidPercentage\":null,\"efsCreate\":false,\"efsId\":null,\"efsMount\":null},\"forgedResources\":[{\"IamRole\":\"arn:aws:iam::195996028523:role/TowerForge-4iqCDE6C2Stq0jzBsHJvHn-ServiceRole\"},{\"IamRole\":\"arn:aws:iam::195996028523:role/TowerForge-4iqCDE6C2Stq0jzBsHJvHn-FleetRole\"},{\"IamInstanceProfile\":\"arn:aws:iam::195996028523:instance-profile/TowerForge-4iqCDE6C2Stq0jzBsHJvHn-InstanceRole\"},{\"Ec2LaunchTemplate\":\"TowerForge-4iqCDE6C2Stq0jzBsHJvHn\"},{\"BatchEnv\":\"arn:aws:batch:eu-west-1:195996028523:compute-environment/TowerForge-4iqCDE6C2Stq0jzBsHJvHn-head\"},{\"BatchQueue\":\"arn:aws:batch:eu-west-1:195996028523:job-queue/TowerForge-4iqCDE6C2Stq0jzBsHJvHn-head\"},{\"BatchEnv\":\"arn:aws:batch:eu-west-1:195996028523:compute-environment/TowerForge-4iqCDE6C2Stq0jzBsHJvHn-work\"},{\"BatchQueue\":\"arn:aws:batch:eu-west-1:195996028523:job-queue/TowerForge-4iqCDE6C2Stq0jzBsHJvHn-work\"}],\"discriminator\":\"aws-batch\"},\"dateCreated\":\"2021-09-09T08:53:37Z\",\"lastUpdated\":\"2021-09-09T08:54:13Z\",\"lastUsed\":null,\"deleted\":null,\"status\":\"AVAILABLE\",\"message\":null,\"primary\":null,\"credentialsId\":\"3WzBlcFy1nSE9dSqFT1xPS\"}}").withContentType(MediaType.APPLICATION_JSON)
        );

        mock.when(
                request().withMethod("POST").withPath("/workflow/launch").withQueryStringParameter("workspaceId", "222756650686576").withBody("{\"launch\":{\"computeEnvId\":\"4iqCDE6C2Stq0jzBsHJvHn\",\"pipeline\":\"https://github.com/nextflow-io/hello\",\"workDir\":\"s3://nextflow-ci/jordeu\"}}"), exactly(1)
        ).respond(
                response().withStatusCode(200).withBody("{\"workflowId\":\"52KAMEcqXFyhZ9\"}").withContentType(MediaType.APPLICATION_JSON)
        );

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

        // Run the command
        ExecOut out = exec(mock, "launch", "https://github.com/nextflow-io/hello", "-w", "222756650686576");

        // Assert results
        assertEquals("", out.stdErr);
        assertEquals(new RunSubmited("52KAMEcqXFyhZ9", 1L, baseWorkspaceUrl(mock, "Seqera", "cli"), buildWorkspaceRef("Seqera", "cli")).toString(), out.stdOut);
        assertEquals(0, out.exitCode);
    }

}
