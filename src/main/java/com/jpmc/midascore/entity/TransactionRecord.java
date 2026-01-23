package com.jpmc.midascore.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;

@Entity
public class TransactionRecord {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private float incentive;

  @ManyToOne
  @JoinColumn(name = "sender_id")
  private UserRecord sender;

  @ManyToOne
  @JoinColumn(name = "recipient_id")
  private UserRecord recipient;

  private float amount;

  public TransactionRecord() {}

  public TransactionRecord(UserRecord sender, UserRecord recipient, float amount){
    this.sender = sender;
    this.recipient = recipient;
    this.amount = amount;
  }

  public float getIncentive() {
    return incentive;
  }

  public void setIncentive(float incentive) {
      this.incentive = incentive;
  }

  public UserRecord getSender(){ return sender; }
  public UserRecord getRecipient(){ return recipient; }
  public float getAmount(){ return amount; }
  public long getId(){ return id; }

  public void setSender(){ this.sender = sender; }
  public void setRecipient(){ this.recipient = recipient; }
  public void setAmount(){ this.amount = amount; }
  public void setId(Long id) { this.id = id; }

}
