/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package io.seqera.tower.cli.credentials.providers;

import io.seqera.tower.ApiException;
import io.seqera.tower.cli.BaseCmdTest;
import io.seqera.tower.cli.exceptions.CredentialsNotFoundException;
import io.seqera.tower.cli.responses.CredentialsCreated;
import io.seqera.tower.cli.responses.CredentialsUpdated;
import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.model.MediaType;

import static io.seqera.tower.cli.commands.AbstractApiCmd.USER_WORKSPACE_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockserver.matchers.Times.exactly;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

class AwsProviderTest extends BaseCmdTest {

    @Test
    void testCreateWithOnlyAssumeRole(MockServerClient mock) {

        // Create server expectation
        mock.when(
                request().withMethod("POST").withPath("/credentials").withBody("{\"credentials\":{\"keys\":{\"assumeRoleArn\":\"arn_role\"},\"name\":\"test_credentials\",\"provider\":\"aws\"}}"), exactly(1)
        ).respond(
                response().withStatusCode(200).withBody("{\"credentialsId\":\"6Kyn17toiABGu47qpBXsVX\"}").withContentType(MediaType.APPLICATION_JSON)
        );

        // Run the command
        ExecOut out = exec(mock, "credentials", "create", "aws", "--name=test_credentials", "--assume-role-arn=arn_role");

        // Assert results
        assertEquals("", out.stdErr);
        assertEquals(new CredentialsCreated("aws", "6Kyn17toiABGu47qpBXsVX", "test_credentials", USER_WORKSPACE_NAME).toString(), out.stdOut);
        assertEquals(0, out.exitCode);

    }

    @Test
    void testCreate(MockServerClient mock) {

        mock.when(
                request().withMethod("POST").withPath("/credentials").withBody("{\"credentials\":{\"keys\":{\"accessKey\":\"access_key\",\"secretKey\":\"secret_key\"},\"name\":\"aws\",\"provider\":\"aws\"}}"), exactly(1)
        ).respond(
                response().withStatusCode(200).withBody("{\"credentialsId\":\"1cz5A8cuBkB5iJliCwJCFU\"}").withContentType(MediaType.APPLICATION_JSON)
        );

        ExecOut out = exec(mock, "credentials", "create", "aws", "-n", "aws", "-a", "access_key", "-s", "secret_key");

        assertEquals("", out.stdErr);
        assertEquals(new CredentialsCreated("aws", "1cz5A8cuBkB5iJliCwJCFU", "aws", USER_WORKSPACE_NAME).toString(), out.stdOut);
        assertEquals(0, out.exitCode);

    }

    @Test
    void testUpdate(MockServerClient mock) {

        mock.when(
                request().withMethod("GET").withPath("/credentials/kfKx9xRgzpIIZrbCMOcU4"), exactly(1)
        ).respond(
                response().withStatusCode(200).withBody("{\"credentials\":{\"id\":\"kfKx9xRgzpIIZrbCMOcU4\",\"name\":\"aws\",\"description\":null,\"discriminator\":\"aws\",\"baseUrl\":null,\"category\":null,\"deleted\":null,\"lastUsed\":\"2021-09-06T15:16:52Z\",\"dateCreated\":\"2021-09-03T13:23:37Z\",\"lastUpdated\":\"2021-09-03T13:23:37Z\"}}").withContentType(MediaType.APPLICATION_JSON)
        );

        mock.when(
                request().withMethod("PUT").withPath("/credentials/kfKx9xRgzpIIZrbCMOcU4").withBody("{\"credentials\":{\"keys\":{\"assumeRoleArn\":\"changeAssumeRole\"},\"id\":\"kfKx9xRgzpIIZrbCMOcU4\",\"name\":\"aws\",\"provider\":\"aws\"}}").withContentType(MediaType.APPLICATION_JSON)
        ).respond(
                response().withStatusCode(204)
        );

        ExecOut out = exec(mock, "credentials", "update", "aws", "-i", "kfKx9xRgzpIIZrbCMOcU4", "-r", "changeAssumeRole");

        assertEquals("", out.stdErr);
        assertEquals(new CredentialsUpdated("aws", "aws", USER_WORKSPACE_NAME).toString(), out.stdOut);
    }

    @Test
    void testUpdateNotFound(MockServerClient mock) {

        mock.when(
                request().withMethod("GET").withPath("/credentials/kfKx9xRgzpIIZrbCMOcU5"), exactly(1)
        ).respond(
                response().withStatusCode(403)
        );

        ExecOut out = exec(mock, "credentials", "update", "aws", "-i", "kfKx9xRgzpIIZrbCMOcU5", "-r", "changeAssumeRole");

        assertEquals(errorMessage(out.app, new CredentialsNotFoundException("kfKx9xRgzpIIZrbCMOcU5", USER_WORKSPACE_NAME)), out.stdErr);
        assertEquals("", out.stdOut);
        assertEquals(-1, out.exitCode);
    }

    @Test
    void testInvalidAuth(MockServerClient mock) {
        mock.when(
                request().withMethod("GET").withPath("/credentials/kfKx9xRgzpIIZrbCMOcU5"), exactly(1)
        ).respond(
                response().withStatusCode(401)
        );

        ExecOut out = exec(mock, "credentials", "update", "aws", "-i", "kfKx9xRgzpIIZrbCMOcU5", "-r", "changeAssumeRole");

        assertEquals(errorMessage(out.app, new ApiException(401, "Unauthorized")), out.stdErr);
        assertEquals("", out.stdOut);
        assertEquals(-1, out.exitCode);
    }

}
