package com.jpmc.midascore;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRecordRepository;
import com.jpmc.midascore.repository.UserRepository;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class TransactionListener {

  TransactionRecord transactionRecord;

  private final UserRepository userRepository;
  private final TransactionRecordRepository transactionRecordRepository;
  private final RestTemplate restTemplate;

  public TransactionListener(UserRepository userRepository,
                             TransactionRecordRepository transactionRecordRepository, RestTemplate restTemplate) {
    this.userRepository = userRepository;
    this.transactionRecordRepository = transactionRecordRepository;
    this.restTemplate = restTemplate;

  }
  @KafkaListener(topics= "${general.kafka-topic}", groupId = "midas-consumer-group")
  public void listen(Transaction tx){
    System.out.println("ALL USERS = " + userRepository.findAll());

    System.out.println("Received:" + tx);

    UserRecord sender = userRepository.findById(tx.getSenderId()).orElse(null);
    UserRecord recipient = userRepository.findById(tx.getRecipientId()).orElse(null);


    if(sender == null || recipient == null){
      return;
    }

    if(sender.getBalance() < tx.getAmount()){
      return;
    }

    float incentiveAmount = 0f;
    try {
      Incentive incentive = restTemplate.postForObject( "http://localhost:8080/incentive",
          tx,                      // Spring will serialize this Transaction as JSON
          Incentive.class);

        if (incentive != null) {
        incentiveAmount = incentive.getAmount();
        }
    } catch (Exception e) {
      // If anything goes wrong, just fall back to 0 incentive
      incentiveAmount = 0f;
    }

    tx.setIncentive(incentiveAmount);

    sender.setBalance(sender.getBalance() - tx.getAmount());
    recipient.setBalance(recipient.getBalance() + tx.getAmount() + incentiveAmount);


    transactionRecord = new TransactionRecord(sender, recipient, tx.getAmount());
    transactionRecord.setIncentive(incentiveAmount);
    transactionRecordRepository.save(transactionRecord);

    userRepository.save(sender);
    userRepository.save(recipient);


  }
}
