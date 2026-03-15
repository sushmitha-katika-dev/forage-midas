package com.jpmc.midascore.kafka;

import com.jpmc.midascore.entity.Incentive;
import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRepository;
import com.jpmc.midascore.repository.UserRepository;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Component
public class TransactionListener {

    private final UserRepository userRepository;
	
    private final TransactionRepository transactionRepository;
	
    private final RestTemplate restTemplate;

    public TransactionListener(UserRepository userRepository,
                               TransactionRepository transactionRepository, RestTemplate restTemplate) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
		this.restTemplate = restTemplate;
    }

    @KafkaListener(topics = "${general.kafka-topic}", groupId = "midas-core-group")
    @Transactional
    public void listen(Transaction transaction) {

        Optional<UserRecord> senderOpt = userRepository.findById(transaction.getSenderId());
        Optional<UserRecord> recipientOpt = userRepository.findById(transaction.getRecipientId());

        if (senderOpt.isPresent() && recipientOpt.isPresent()) {
            UserRecord sender = senderOpt.get();
            UserRecord recipient = recipientOpt.get();

            if (sender.getBalance() >= transaction.getAmount()) {
            	
            	// Call the Incentive API
            	Incentive incentive = restTemplate.postForObject(
                        "http://localhost:8080/incentive",
                        transaction,
                        Incentive.class
                );

                float incentiveAmount = (incentive != null) ? incentive.getAmount() : 0f;

                // Update balances
                sender.setBalance(sender.getBalance() - transaction.getAmount()); //only transaction amount
                recipient.setBalance(recipient.getBalance() + transaction.getAmount() + incentiveAmount); // incentive Amont

                // Save updated users
                userRepository.save(sender);
                userRepository.save(recipient);

                // Persist transaction record
                TransactionRecord record = new TransactionRecord(sender, recipient, transaction.getAmount(), incentiveAmount);
                transactionRepository.save(record);

                System.out.println("Recorded transaction: " + record);

                // Fetch Wilbur and print current balance for debugging
                UserRecord wilbur = userRepository.findByName("wilbur");
                if (wilbur != null) {
                    int roundedBalance = (int) Math.floor(wilbur.getBalance());
                    System.out.println("Wilbur's rounded balance: " + roundedBalance);
                }

                return;
            }
        }

        System.out.println("Invalid transaction: " + transaction);
    }
}
