package edu.java.bot.service;

import edu.java.bot.configuration.KafkaConfiguration;
import edu.java.bot.controller.request.LinkUpdateRequest;
import edu.java.bot.controller.updatesApi.UpdateHandler;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@EmbeddedKafka(topics = {"${app.scrapper-topic.name}", "${app.scrapper-topic.name}-dlq"})
@SpringBootTest(classes = {ScrapperListenerTest.TestConfiguration.class, KafkaConfiguration.class}, properties = {"app.use-queue=true"})
@DirtiesContext
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ScrapperListenerTest {

    @Configuration
    @EnableAutoConfiguration
    static class TestConfiguration {
    }

    @Autowired
    private EmbeddedKafkaBroker embeddedKafka;
    @Autowired
    private KafkaTemplate<String, LinkUpdateRequest> producer;

    @MockBean
    UpdateHandler updateHandler;

    @SpyBean
    private ScrapperListener scrapperListener;

    @Captor
    ArgumentCaptor<LinkUpdateRequest> linkArgumentCaptor;
    @Captor
    ArgumentCaptor<Acknowledgment> ackArgumentCaptor;

    @Value("${app.scrapper-topic.name}")
    private String topic;

    private BlockingQueue<ConsumerRecord<String, LinkUpdateRequest>> records;

    private KafkaMessageListenerContainer<String, LinkUpdateRequest> container;

    @BeforeAll
    void setUp() {
        DefaultKafkaConsumerFactory<String, LinkUpdateRequest> consumerFactory =
            new DefaultKafkaConsumerFactory<>(getConsumerProperties());
        ContainerProperties containerProperties =
            new ContainerProperties(String.valueOf(embeddedKafka.getTopics().toArray()[1]));
        container = new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);
        records = new LinkedBlockingQueue<>();
        container.setupMessageListener((MessageListener<String, LinkUpdateRequest>) records::add);
        container.start();
        ContainerTestUtils.waitForAssignment(container, embeddedKafka.getPartitionsPerTopic());
    }

    private Map<String, Object> getConsumerProperties() {
        return Map.of(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, embeddedKafka.getBrokersAsString(),
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
    @DisplayName("Отправка корректного обновления должна дойти до Consumer")
    void listen_shouldActivateAfterDeliverCorrectMessage() {
        LinkUpdateRequest linkUpdateRequest = new LinkUpdateRequest(1L, URI.create("string"), "string", List.of(1L));

        producer.send(new ProducerRecord<>(topic, linkUpdateRequest));

        verify(scrapperListener, timeout(1000).times(1))
            .listen(
                linkArgumentCaptor.capture(),
                ackArgumentCaptor.capture()
            );

        assertThat(linkArgumentCaptor.getValue())
            .isEqualTo(linkUpdateRequest);
    }

    @Test
    @DisplayName("Отправка некорректного обновления в DLQ")
    void listen_shouldSendToDLQIncorrectMessage() throws InterruptedException {
        LinkUpdateRequest linkUpdateRequest = new LinkUpdateRequest(0L, URI.create("string"), "string", List.of(1L));

        producer.send(new ProducerRecord<>(topic, linkUpdateRequest));

        verifyNoInteractions(scrapperListener);

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
