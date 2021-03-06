package com.galileo.kafka.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;


@Configuration
public class KafkaConfiguration {
	@Bean
	public Map<String, Object> consumerProperties() { Map<String, Object>props=new HashMap<>();
	props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,"localhost:9092");
	props.put(ConsumerConfig.GROUP_ID_CONFIG,"devs4j-group");
	props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,true);
	props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG,"100");
	props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG,"15000");
	props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,IntegerDeserializer.class);
	props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,StringDeserializer.class);
	return props;
	}
	
	
	@Bean
	public ConsumerFactory<String,String> consumerFactory(){
		return new DefaultKafkaConsumerFactory<>(consumerProperties());
	}
	@Bean(name="listenerContainerFactory")
	public ConcurrentKafkaListenerContainerFactory<String, String>listenerContainerFactory() {
	ConcurrentKafkaListenerContainerFactory<String, String>factory = new ConcurrentKafkaListenerContainerFactory<>();
	factory.setConsumerFactory(consumerFactory());
	factory.setBatchListener(true);
	factory.setConcurrency(3);
	return factory;
	}

	private Map<String, Object> producerProperties() { Map<String, Object> props=new HashMap<>();
	props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
	"localhost:9092");
	props.put(ProducerConfig.RETRIES_CONFIG, 0);
	props.put(ProducerConfig.BATCH_SIZE_CONFIG, 16384);
	props.put(ProducerConfig.LINGER_MS_CONFIG, 1);
	props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, 33554432);
	props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,IntegerSerializer.class);
	props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,StringSerializer.class);
	return props;
	}
	@Bean
	public KafkaTemplate<String, String> kafkaTemplate() {
		DefaultKafkaProducerFactory<String,String> producerFactory = new DefaultKafkaProducerFactory<>(producerProperties());
		KafkaTemplate<String,String> template = new KafkaTemplate<>(producerFactory);
		return template;
	}
	@Bean
	public MeterRegistry meterRegistry() {
	PrometheusMeterRegistry prometheusMeterRegistry=new
	PrometheusMeterRegistry (PrometheusConfig.DEFAULT);
	return prometheusMeterRegistry;
	}

}
