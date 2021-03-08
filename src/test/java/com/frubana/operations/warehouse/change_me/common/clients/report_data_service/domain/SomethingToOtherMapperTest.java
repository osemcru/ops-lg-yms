package com.frubana.operations.warehouse.change_me.common.clients
        .report_data_service.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.frubana.operations.test.utils.ValidationUtils;
import com.frubana.operations.warehouse.change_me.common.utils.JsonUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/** Tests the mapping that converts a report data service orders response
 * in a list of some objects.
 */
@ExtendWith(SpringExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SomethingToOtherMapperTest {

    // Resource loader to get the JSON files for testing.
    @Autowired
    private ResourceLoader resourceLoader;

    private String frubanaOrdersJson;
    private String labelsSomeObjectsJson;

    private final ObjectMapper mapper = JsonUtils.instance;

    @BeforeAll
    void prepareTestData() throws IOException {
        try (InputStream stream = resourceLoader.getResource("classpath:" +
                "frubana/orders/reports_main_orders.json")
                .getInputStream()) {
            frubanaOrdersJson = new String(
                    FileCopyUtils.copyToByteArray(stream),
                    StandardCharsets.UTF_8);
        }
        try (InputStream stream = resourceLoader.getResource("classpath:" +
                "change_me/some/some_objects_from_orders.json")
                .getInputStream()) {
            labelsSomeObjectsJson = new String(
                    FileCopyUtils.copyToByteArray(stream),
                    StandardCharsets.UTF_8);
        }
    }

    /**
     * Test the mapping using the mapper.
     */
    @Test
    void mapOrdersToSomeObjectTest()
            throws JsonProcessingException {

        // Subject test
        SomethingToOtherMapper tasksMapper = mapper.readValue(
                frubanaOrdersJson, SomethingToOtherMapper.class);

        String generatedTasks =
                mapper.writeValueAsString(tasksMapper.getGeneratedTasks());

        // Convert all in Json nodes
        JsonNode expectedNode = mapper.readValue(
                labelsSomeObjectsJson, JsonNode.class);
        JsonNode generatedNode = mapper.readValue(
                generatedTasks, JsonNode.class);

        // Compare each generated value with its contract.
        ValidationUtils.compareWithExpected(generatedNode,
                expectedNode, null);
    }
}
