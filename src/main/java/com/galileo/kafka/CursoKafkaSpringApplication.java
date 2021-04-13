package com.galileo.kafka;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaProducerException;
import org.springframework.kafka.core.KafkaSendCallback;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;

@SpringBootApplication
public class CursoKafkaSpringApplication implements CommandLineRunner{
	
	@Autowired
	private KafkaTemplate<String,String> kafkaTemplate;
	
	private static final Logger log = LoggerFactory.getLogger(CursoKafkaSpringApplication.class);
	
	@KafkaListener(topics = "devs4j-topic", containerFactory="listenerContainerFactory",groupId = "devs4j-group", properties =
		{"max.poll.interval.ms:60000",
		"max.poll.records:100"})
	public void listen(List<ConsumerRecord<String,String>>messages) {
			for(ConsumerRecord<String, String>message : messages) {
			log.info("Offset {} Partition= {} Key = {}Value = {}", message.offset(),message.partition(), message.key(),message.value());
			}
			}

	public static void main(String[] args) {
		SpringApplication.run(CursoKafkaSpringApplication.class, args);
	}
	
	@Override
	public void run(String... args) throws Exception{
		
		for (int i = 0; i<100;i++) {
			kafkaTemplate.send("devs4j-topic", String.valueOf(i),String.format("sample massege %d", i));
		}
		
		/* Forma Asincrona
		ListenableFuture<SendResult<String,String>> future = kafkaTemplate.send("devs4j-topic", "sample massege");
		future.addCallback(new KafkaSendCallback<String,String>(){

			@Override
			public void onSuccess(SendResult<String, String>result) {
			log.info("Message sent", result.getRecordMetadata().offset());
			}
			@Override
			public void onFailure(Throwable ex) {
			log.error("Error sending message ",ex);
			}
			@Override
			public void onFailure(KafkaProducerException ex) {
			log.error("Error sending message ",ex);
			}
			
		});
		
		*/
		/* Forma Sincrona
		kafkaTemplate.send(new
				ProducerRecord<String,String>
				("key","value")).get();
				kafkaTemplate.send(new
				ProducerRecord<String,String>
				("key","value")).get(10,TimeUnit.SECONDS);
		*/
		//kafkaTemplate.send("devs4j-topic", "sample massege");
	}
	
	

}
