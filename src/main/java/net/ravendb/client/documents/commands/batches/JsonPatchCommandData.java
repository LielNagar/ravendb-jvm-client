package net.ravendb.client.documents.commands.batches;

import com.fasterxml.jackson.core.JsonGenerator;
import net.ravendb.client.documents.conventions.DocumentConventions;
import net.ravendb.client.documents.session.InMemoryDocumentSessionOperations;

import java.io.IOException;

public class JsonPatchCommandData implements ICommandData {

    private final String id;
    private final JsonPatchDocument jsonPatch;
    private boolean returnDocument;

    public JsonPatchCommandData(String id, JsonPatchDocument jsonPatch) {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }

        this.id = id;
        this.jsonPatch = jsonPatch;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getChangeVector() {
        return null;
    }

    @Override
    public CommandType getType() {
        return CommandType.JSON_PATCH;
    }

    public JsonPatchDocument getJsonPatch() {
        return jsonPatch;
    }

    @Override
    public void serialize(JsonGenerator generator, DocumentConventions conventions) throws IOException {
        generator.writeStartObject();

        generator.writeStringField("Id", id);
        generator.writeNullField("ChangeVector");

        generator.writeFieldName("JsonPatch");
        generator.writeStartObject();
        generator.writeFieldName("Operations");
        generator.writeStartArray();
        for (JsonPatchDocument.Operation operation : jsonPatch.getOperations()) {
            generator.writeStartObject();
            generator.writeStringField("op", operation.getOp());
            generator.writeStringField("path", operation.getPath());
            if (!"remove".equals(operation.getOp())) {
                // the server requires "value" for add/replace, even when it is null
                generator.writeFieldName("value");
                conventions.getEntityMapper().writeValue(generator, operation.getValue());
            }
            generator.writeEndObject();
        }
        generator.writeEndArray();
        generator.writeEndObject();

        generator.writeBooleanField("ReturnDocument", returnDocument);
        generator.writeStringField("Type", "JsonPatch");

        generator.writeEndObject();
    }

    @Override
    public void onBeforeSaveChanges(InMemoryDocumentSessionOperations session) {
        returnDocument = session.isLoaded(id);
    }
}
