package com.david.bookms.service;

import com.david.bookms.model.Book;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Properties;

@Slf4j
@Service
public class EventServiceImpl implements EventService {
    @Value("${skfka.server.url}")
    private String serverUrl;
    @Value("${skfka.topics.name}")
    private String topics;

    @Override
    public void bookCreate(Book book) {
        log.info("Book Create event message send {}",book);

    }

    @Override
    public void bookModify(Book book) {

    }

    @Override
    public void bookDelete(Book book) {

    }

    @Override
    public void bookSearch(Book book) {

    }

    @PostConstruct
    public void init(){
        Properties prop = new Properties();
        prop.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"localhost:9092");
        prop.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        prop.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,StringSerializer.class.getName());
    }
}
