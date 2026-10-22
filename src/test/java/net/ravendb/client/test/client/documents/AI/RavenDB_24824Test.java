package net.ravendb.client.test.client.documents.AI;

import net.ravendb.client.RemoteTestBase;
import net.ravendb.client.documents.AI.AiConversation;
import net.ravendb.client.documents.AI.AiConversationCreationOptions;
import net.ravendb.client.documents.AI.AiOutputOptions;
import net.ravendb.client.documents.IDocumentStore;
import net.ravendb.client.infrastructure.EnableOnServer;
import net.ravendb.client.test.client.documents.AI.AiOutputOptionsTest.AlternativeSchema;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@EnableOnServer(thresholdVersion = "7.2")
public class RavenDB_24824Test extends RemoteTestBase {

    @Test
    public void invalidOutputOptionsCombinationsShouldThrow() throws Exception {
        try (IDocumentStore store = getDocumentStore()) {
            AiConversation chat = store.ai().conversation("agents/test", "chats/123", new AiConversationCreationOptions());
            chat.setUserPrompt("hello");

            assertThatThrownBy(() -> chat.runWithSchema((AiOutputOptions) null))
                    .isInstanceOf(IllegalArgumentException.class);

            AiOutputOptions schemaWithNoSchema = new AiOutputOptions("{}");
            schemaWithNoSchema.setNoSchema(true);
            assertThatThrownBy(() -> chat.runWithSchema(schemaWithNoSchema))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("noSchema");

            AiOutputOptions sampleWithNoSchema = new AiOutputOptions(new AlternativeSchema("x", 1));
            sampleWithNoSchema.setNoSchema(true);
            assertThatThrownBy(() -> chat.runWithSchema(sampleWithNoSchema))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("noSchema");
            assertThatThrownBy(() -> chat.streamWithSchema("summary", chunk -> CompletableFuture.completedFuture(null), sampleWithNoSchema))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("noSchema");

            // a structured answer still needs a property to stream
            assertThatThrownBy(() -> chat.streamWithSchema("", chunk -> CompletableFuture.completedFuture(null), new AiOutputOptions("{}")))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }
}
