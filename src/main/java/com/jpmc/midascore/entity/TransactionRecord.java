package com.jpmc.midascore.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class TransactionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private UserRecord sender;

    @ManyToOne
    @JoinColumn(name = "recipient_id")
    private UserRecord recipient;

    private float amount;
    
    private float incentive; 

    private LocalDateTime timestamp;

    public TransactionRecord() {}

    public TransactionRecord(UserRecord sender, UserRecord recipient, float amount,float incentive) {
        this.sender = sender;
        this.recipient = recipient;
        this.setAmount(amount);
        this.incentive = incentive;
        this.setTimestamp(LocalDateTime.now());
        
    }

    // getters & setters
	public float getAmount() {
		return amount;
	}

	public void setAmount(float amount) {
		this.amount = amount;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	public float getIncentive() {
		return incentive;
	}

	public void setIncentive(float incentive) {
		this.incentive = incentive;
	}

    
}
