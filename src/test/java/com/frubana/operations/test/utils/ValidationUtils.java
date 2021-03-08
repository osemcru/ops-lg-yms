package com.frubana.operations.test.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** Test util to validate data.
 * Contains methods to validate or comparte test generated and expected data.
 */
public class ValidationUtils {

    /**
     * Compares two JSONodes to see if the data of the generated is the same as
     * the expected.
     * @param expectedNode The expected node to compare.
     * @param generatedNode The generated node to compare.
     * @param relationship If the name of the fields.
     */
    public static void compareWithExpected(
            JsonNode expectedNode, JsonNode generatedNode,
            Map<String, String> relationship) {
        // If is lists vs list we must separate each element.
        if (expectedNode instanceof ArrayNode) {
            List<JsonNode> expectedElements =
                    Lists.newArrayList(expectedNode.elements());
            List<JsonNode> generatedElements =
                    Lists.newArrayList(generatedNode.elements());
            assertEquals(expectedElements.size(), generatedElements.size()
                    , "The size of the expected list values and generated" +
                            " ones is not the same.");
            for (int i = 0; i < expectedElements.size(); i++) {
                compareWithExpected(expectedElements.get(i),
                        generatedElements.get(i), relationship);
            }
        }
        // Extracting all the fields of the expected contract.
        Iterator<Map.Entry<String, JsonNode>> elements = expectedNode.fields();
        while (elements.hasNext()) {
            Map.Entry<String, JsonNode> element = elements.next();
            String key = element.getKey();
            // Validating if there is a relationship between the expected
            // key and the generated field.
            if (relationship != null) {
                key = relationship.getOrDefault(key, key);
            }
            // Extracting the data of each Node.
            JsonNode generatedElement = generatedNode.get(key);
            JsonNode expectedElement = element.getValue();

            // Validating by the type what we have to compare.
            if (expectedElement instanceof ObjectNode) {
                // If the values are Objects nodes then lets compare each
                // field calling this same method recursively.
                compareWithExpected(expectedElement,
                        generatedElement, relationship);
            } else if (expectedElement instanceof ArrayNode) {
                // If is a list of nodes then extract each element and
                // compare it, ALERT: the order must be the same in the
                // expected result and the generated one.
                List<JsonNode> expArrayElements =
                        Lists.newArrayList(expectedElement.elements());
                List<JsonNode> genArrayElements =
                        Lists.newArrayList(generatedElement.elements());
                assertEquals(expArrayElements.size(), genArrayElements.size()
                        , "The size of the expected list values and generated" +
                                " ones is not the same.");
                for (int i = 0; i < expArrayElements.size(); i++) {
                    JsonNode expArrayElement = expArrayElements.get(i);
                    JsonNode genArrayElement = genArrayElements.get(i);

                    // Now compare each element using this same method
                    // recursively.
                    compareWithExpected(expArrayElement,
                            genArrayElement, relationship);
                }
            } else if (expectedElement != null) {
                // Finally if the expected element is not null lets compare
                // the two elements.
                assertEquals(generatedElement, expectedElement, "Different " +
                        "values were encountered comparing the expected data " +
                        "with the generated one, for key: " + key + ".");
            }
        }
    }
}
