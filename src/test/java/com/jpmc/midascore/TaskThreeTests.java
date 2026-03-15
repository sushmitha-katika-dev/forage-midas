package com.jpmc.midascore;

import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
public class TaskThreeTests {

    static final Logger logger = LoggerFactory.getLogger(TaskThreeTests.class);

    @Autowired
    private KafkaProducer kafkaProducer;

    @Autowired
    private UserPopulator userPopulator;

    @Autowired
    private FileLoader fileLoader;

    @Autowired
    private UserRepository userRepository;

    @Test
    void task_three_verifier() throws InterruptedException {
        // Populate users
        userPopulator.populate();

        // Send all transactions
        String[] transactionLines = fileLoader.loadStrings("/test_data/mnbvcxz.vbnm");
        for (String transactionLine : transactionLines) {
            kafkaProducer.send(transactionLine);
        }

        // Wait a short time for Kafka to process everything
        Thread.sleep(3000); // adjust if needed

        // Fetch Waldorf's balance and round it
        UserRecord waldorf = userRepository.findByName("waldorf");
        if (waldorf != null) {
            int roundedBalance = (int) Math.floor(waldorf.getBalance());
            System.out.println("✅ Waldorf's final rounded balance: " + roundedBalance);
        } else {
            System.out.println("❌ Waldorf not found!");
        }

        // Stop the test immediately
        logger.info("Test finished, check Waldorf's balance above.");
    }
}
