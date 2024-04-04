package edu.java.scrapper.service.linkUpdateService.linkUpdateSender;

import edu.java.scrapper.configuration.ApplicationConfig;
import edu.java.scrapper.service.client.botClient.request.LinkUpdateRequest;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import static org.assertj.core.api.Assertions.assertThat;

@EmbeddedKafka(
    topics = "${app.bot-topic.name}",
    brokerProperties = {"listeners=PLAINTEXT://localhost:9092"}
)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(classes = {ScrapperQueueProducerTest.TestConfiguration.class})
@EnableAutoConfiguration(exclude = {
    DataSourceAutoConfiguration.class,
    DataSourceTransactionManagerAutoConfiguration.class,
    HibernateJpaAutoConfiguration.class})
class ScrapperQueueProducerTest {

    @Configuration
    @EnableAutoConfiguration
    static class TestConfiguration {
    }

    @Autowired
    EmbeddedKafkaBroker kafkaEmbedded;
    @Autowired
    KafkaTemplate<String, LinkUpdateRequest> kafkaTemplate;

    private BlockingQueue<ConsumerRecord<String, LinkUpdateRequest>> records;

    private KafkaMessageListenerContainer<String, LinkUpdateRequest> container;

    private ScrapperQueueProducer scrapperQueueProducer;

    @BeforeAll
    void setUp() {
        scrapperQueueProducer = new ScrapperQueueProducer(
            kafkaTemplate,
            new ApplicationConfig(null, null, null, null, new ApplicationConfig.Topic("link-update", null, null), null)
        );
        DefaultKafkaConsumerFactory<String, LinkUpdateRequest> consumerFactory =
            new DefaultKafkaConsumerFactory<>(getConsumerProperties());
        ContainerProperties containerProperties =
            new ContainerProperties(String.valueOf(kafkaEmbedded.getTopics().toArray()[0]));
        container = new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);
        records = new LinkedBlockingQueue<>();
        container.setupMessageListener((MessageListener<String, LinkUpdateRequest>) records::add);
        container.start();
        ContainerTestUtils.waitForAssignment(container, kafkaEmbedded.getPartitionsPerTopic());
    }

    private Map<String, Object> getConsumerProperties() {
        return Map.of(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaEmbedded.getBrokersAsString(),
            ConsumerConfig.GROUP_ID_CONFIG, "test",
            ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true",
            ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "60000",
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class,
            ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest",
            JsonDeserializer.VALUE_DEFAULT_TYPE, LinkUpdateRequest.class,
            JsonDeserializer.USE_TYPE_INFO_HEADERS, false
        );
    }

    @Test
    @DisplayName("Отправка сообщения в очередь")
    void send_shouldSendMessageInQueue() throws InterruptedException {
        LinkUpdateRequest linkUpdateRequest = new LinkUpdateRequest(0L, URI.create("string"), "string", List.of(1L));

        scrapperQueueProducer.send(linkUpdateRequest);

        ConsumerRecord<String, LinkUpdateRequest> consumerRecord = records.poll(3, TimeUnit.SECONDS);

        assertThat(consumerRecord)
            .isNotNull();
        assertThat(consumerRecord.value())
            .isEqualTo(linkUpdateRequest);
    }

    @AfterAll
    void tearDown() {
        container.stop();
    }
}
