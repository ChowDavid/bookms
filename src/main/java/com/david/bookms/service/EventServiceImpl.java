package com.david.bookms.service;

import com.david.bookms.model.Book;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import java.time.LocalDateTime;
import java.util.Properties;

@Slf4j
@Service
public class EventServiceImpl implements EventService {
    @Value("${skfka.server.url}")
    private String serverUrl;
    @Value("${skfka.topics.name}")
    private String topics;
    @Value("${skfka.record.key}")
    private String key;

    private Producer<String ,String> producer;
    private ProducerRecord<String, String> record;



    private String log(String formated, Book... books){
        String  message = String.format(formated,books);
        return LocalDateTime.now()+" "+message;
    }

    @Async
    @Override
    public void bookCreate(Book book) {
        log.info("Book Create event message send {}",book);

        record = new ProducerRecord<>(topics, key, log("Book Created %s", book));
        producer.send(record);
        producer.flush();
    }
    @Async
    @Override
    public void bookModify(Book bookBefore,Book bookAfter) {
        log.info("Book modify event message send {} {}",bookBefore,bookAfter);
        record = new ProducerRecord<>(topics, key,  log("Book Modify from %s to %s",bookBefore,bookAfter));
        producer.send(record);
        producer.flush();
    }
    @Async
    @Override
    public void bookDelete(Book book) {
        log.info("Book Delete event message send {}",book);
        record = new ProducerRecord<>(topics, key, log("Book Delete for %s",book));
        producer.send(record);
        producer.flush();
    }
    @Async
    @Override
    public void bookSearch(Book book) {
        log.info("Book Search event message send {}",book);
        record = new ProducerRecord<>(topics, key, log("Book Searched for %s",book));
        producer.send(record);
        producer.flush();
    }

    @PostConstruct
    public void init(){
        Properties prop = new Properties();
        prop.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,serverUrl);
        prop.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        prop.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,StringSerializer.class.getName());
        prop.setProperty(ProducerConfig.ACKS_CONFIG,"all");
        prop.setProperty(ProducerConfig.RETRIES_CONFIG,"10");
        prop.setProperty(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG,"100");
        prop.setProperty("enable.idempotence","true");
        producer = new KafkaProducer<>(prop);
    }
    @PreDestroy
    public void close(){
        if (producer!=null){
            producer.flush();
            try {
                producer.close();
            } catch (Exception e){
                log.error(e.getMessage(),e);
            }
        }

    }
}
