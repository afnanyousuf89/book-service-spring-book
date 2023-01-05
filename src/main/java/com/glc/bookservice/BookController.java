package com.glc.bookservice;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RabbitListener(queues = "booklist")
@RequestMapping("/books")  // Any address like https://localhost:8080/books
public class BookController {
	private final BookRepository repository;

	@Autowired
	private Queue queue;

	@Autowired
	private RabbitTemplate template;

	public BookController(BookRepository repository){
		this.repository = repository;
	}

	@PostMapping("")  // (POST) https://localhost:8080/books
	public void createBook(@RequestBody Book book) {
		
		this.repository.save(book);
	}

	@GetMapping("/all") // (GET) https://localhost:8080/books/all
	public Collection<Book> getAllBooks(){
		return this.repository.getAllBooks();
	}

	@RabbitHandler
	public void receive(String in) throws Exception{
		ObjectMapper objectMapper = new ObjectMapper();
		List<Book> books = objectMapper.readValue(in, new TypeReference<List<Book>>() {} );
	books.forEach((book)-> repository.save(book));
	books.forEach((book)-> System.out.println(book.getTitle()));
	}


	@GetMapping("/booklist")  // (POST) https://localhost:8080/books
	public void send() throws Exception{
		ObjectMapper objectMapper = new ObjectMapper();
		Book book1 = new Book(1,"MR X","Unknown",1996,256);
		Book book2 = new Book(2,"Can be done!","Anyone",1989,205);
		List<Book> books = new ArrayList<>();
		books.add(book1);
		books.add(book2);
		template.convertAndSend(queue.getName(),objectMapper.writeValueAsString(books));
	}
}