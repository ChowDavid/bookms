package com.david.bookms.service;

import com.david.bookms.model.Book;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.MockProducer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;


import static org.junit.jupiter.api.Assertions.*;


class EventServiceImplTest {
    @InjectMocks EventServiceImpl eventService;
    MockProducer<String,String> mockProducer;

    @BeforeEach
    public void init() throws IllegalAccessException {
        MockitoAnnotations.initMocks(this);
        mockProducer = new MockProducer<>(true, new StringSerializer(), new StringSerializer());
        ReflectionTestUtils.setField(eventService, "producer", mockProducer);
        ReflectionTestUtils.setField(eventService, "serverUrl", "localhost:9092");
        ReflectionTestUtils.setField(eventService, "topics", "bookms-topic");
        ReflectionTestUtils.setField(eventService, "key", "books");
    }

    @Test
    public void creatBookMessage(){
        eventService.bookCreate(new Book());
        assertEquals(1,mockProducer.history().size());
        assertTrue(mockProducer.history().get(0).value().indexOf("Book Created")>0);
    }
    @Test
    public void creatBookSearch(){
        eventService.bookSearch(new Book());
        assertEquals(1,mockProducer.history().size());
        assertTrue(mockProducer.history().get(0).value().indexOf("Book Search")>0);
    }
    @Test
    public void creatBookModify(){
        eventService.bookModify(new Book(),new Book());
        assertEquals(1,mockProducer.history().size());
        assertTrue(mockProducer.history().get(0).value().indexOf("Book Modify")>0);
    }
    @Test
    public void creatBookDelete(){
        eventService.bookDelete(new Book());
        assertEquals(1,mockProducer.history().size());
        assertTrue(mockProducer.history().get(0).value().indexOf("Book Delete for")>0);
    }

}