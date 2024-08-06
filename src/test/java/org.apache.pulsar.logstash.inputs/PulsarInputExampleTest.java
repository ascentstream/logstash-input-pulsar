package org.apache.pulsar.logstash.inputs;

import co.elastic.logstash.api.Configuration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import org.junit.Test;
import org.logstash.plugins.ConfigurationImpl;

public class PulsarInputExampleTest {

    String serviceUrl = "pulsar://localhost:6650";
    String topic = "public/default/logstash";
    String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJhZG1pbiJ9.cKtgbRPTxRpucpJ_5RCF2fQxrXaDkXPGw3nQfnvNU7c";

    @Test
    public void testInputWithPulsarToken() {
        Map<String, Object> configValues = new HashMap<>();
        configValues.put("serviceUrl", serviceUrl);
        configValues.put("codec", "plain");
        configValues.put("topics", Collections.singletonList(topic));
        configValues.put("subscriptionName", "logstash_sub");
        configValues.put("subscriptionType", "Shared");
        configValues.put("subscriptionInitialPosition", "Earliest");
        configValues.put("auth_plugin_class_name", "org.apache.pulsar.client.impl.auth.AuthenticationToken");
        configValues.put("auth_params", "token:" + token);
        Configuration config = new ConfigurationImpl(configValues);
        Pulsar input = new Pulsar("test-id", config, null);
        TestConsumer testConsumer = new TestConsumer();
        input.start(testConsumer);
        List<Map<String, Object>> events = testConsumer.getEvents();
        for (int k = 1; k <= events.size(); k++) {
            System.out.println(events.get(k - 1).get("message"));
        }

    }

    private static class TestConsumer implements Consumer<Map<String, Object>> {

        private List<Map<String, Object>> events = new ArrayList<>();

        @Override
        public void accept(Map<String, Object> event) {
            synchronized (this) {
                events.add(event);
            }
        }

        public List<Map<String, Object>> getEvents() {
            return events;
        }
    }


}
