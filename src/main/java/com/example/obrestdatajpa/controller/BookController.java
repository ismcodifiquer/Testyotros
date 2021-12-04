package com.example.obrestdatajpa.controller;

import com.example.obrestdatajpa.entities.Book;
import com.example.obrestdatajpa.repository.BookRepository;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class BookController {

    private final Logger log = LoggerFactory.getLogger(BookController.class);

    //atributos
    private BookRepository bookRepository;
    //constructores

    public BookController(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }


    // CRUD sobre la entidad Book

    // Buscar todos los libros (lista de libros)
    @GetMapping("/api/books")
    public List<Book> findAll() {
        // recuperar y devolver los libros de base de datos
        return bookRepository.findAll();
    }

    // Buscar un solo libro en base de datos según su id

    @GetMapping("/api/books/{id}")
    @ApiOperation("Buscar un libro por clave primaria id Long")
    public ResponseEntity<Book> findOneById(@ApiParam("Clave primaria tipo Long") @PathVariable Long id) {
        Optional<Book> bookOpt = bookRepository.findById(id);
        // opcion 1
        if(bookOpt.isPresent())
            return ResponseEntity.ok(bookOpt.get());
        else
            return ResponseEntity.notFound().build();

        //opcion 2
        //return bookOpt.orElse(null);
    }



    // Crear un nuevo libro en base de datos
    @PostMapping("/api/books")
    public ResponseEntity<Book> create(@RequestBody Book book, @RequestHeader HttpHeaders headers) {
        System.out.println(headers.get("User-Agent"));
        if(book.getId() != null){ // quiere decir que existe el id y por tanto no es una creación
            log.warn("trying to create a book with id"); // Creamos un log de warning
            System.out.println("trying to create a book with id");
            return ResponseEntity.badRequest().build(); // Aquí devolvemos una respuesta de error
        }
        Book result = bookRepository.save(book);
        return ResponseEntity.ok(result);
    }

    // Actualizar un libro existente en base de datos
    @PutMapping("/api/books")
    public ResponseEntity<Book> update(@RequestBody Book book) {
        if(book.getId() == null){ // si no tiene id quiere decir que si es una creación
            log.warn("Trying to update a non existent book");
            return ResponseEntity.badRequest().build(); // Error 400 estas enviando mal la peticion
        }
        if(!bookRepository.existsById(book.getId())){
            log.warn("Trying to update a non existent book");
            return ResponseEntity.notFound().build(); // Error 404 no existe
        }

        Book result = bookRepository.save(book);
        return ResponseEntity.ok(result);
    }

    // Borrar un libro en base de datos
    @DeleteMapping("/api/books/{id}")
    public ResponseEntity<Book> delete(@PathVariable Long id){

        if(!bookRepository.existsById(id)){
            log.warn("Trying to delete a non existent book");
            return ResponseEntity.notFound().build(); // Error 404 no existe
        }

        bookRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/api/books")
    public ResponseEntity<Book> deleteAll(){
        log.info("Rest Request for deleting all books");
        bookRepository.deleteAll();
        return ResponseEntity.noContent().build();
    }




}
