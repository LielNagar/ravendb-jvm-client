package net.ravendb.client.documents.commands.batches;

import java.util.ArrayList;
import java.util.List;

/**
 * An RFC 6902 JSON Patch document: an ordered list of operations applied to a single document.
 */
public class JsonPatchDocument {

    private final List<Operation> operations = new ArrayList<>();

    public List<Operation> getOperations() {
        return operations;
    }

    /**
     * Adds a value at the given JSON pointer. For an object member it creates or replaces the member,
     * for an array index it inserts before that index ("-" appends to the end).
     * @param path JSON pointer
     * @param value value to add
     * @return this document
     */
    public JsonPatchDocument add(String path, Object value) {
        operations.add(new Operation("add", path, value));
        return this;
    }

    /**
     * Removes the value at the given JSON pointer.
     * @param path JSON pointer
     * @return this document
     */
    public JsonPatchDocument remove(String path) {
        operations.add(new Operation("remove", path, null));
        return this;
    }

    /**
     * Replaces the existing value at the given JSON pointer.
     * @param path JSON pointer
     * @param value new value
     * @return this document
     */
    public JsonPatchDocument replace(String path, Object value) {
        operations.add(new Operation("replace", path, value));
        return this;
    }

    public static class Operation {
        private final String op;
        private final String path;
        private final Object value;

        public Operation(String op, String path, Object value) {
            this.op = op;
            this.path = path;
            this.value = value;
        }

        public String getOp() {
            return op;
        }

        public String getPath() {
            return path;
        }

        public Object getValue() {
            return value;
        }
    }
}
