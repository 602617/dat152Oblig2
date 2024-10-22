/**
 * 
 */
package no.hvl.dat152.rest.ws.controller;

import java.util.List;
import java.util.Set;

import no.hvl.dat152.rest.ws.exceptions.BookNotFoundException;
import no.hvl.dat152.rest.ws.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import no.hvl.dat152.rest.ws.exceptions.AuthorNotFoundException;
import no.hvl.dat152.rest.ws.model.Author;
import no.hvl.dat152.rest.ws.model.Book;
import no.hvl.dat152.rest.ws.service.AuthorService;

/**
 * 
 */
@RestController
@RequestMapping("/elibrary/api/v1")
public class AuthorController {

	// TODO authority annotation
    @Autowired
    AuthorService authorService;

    // TODO - getAllAuthor (@Mappings, URI, and method)
    @GetMapping("/authors")
    public ResponseEntity<Object> getAllAuthor(){
        List<Author> authors = authorService.findAll();

        if (authors.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(authors, HttpStatus.OK);
    }

    // TODO - getAuthor (@Mappings, URI, and method)
    @GetMapping("/authors/{id}")
    public ResponseEntity<Author> getAuthor(@PathVariable("id") Long id) throws AuthorNotFoundException{
        Author author = authorService.findById(id);

        if (author == null){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(author, HttpStatus.OK);
    }

    // TODO - getBooksByAuthorId (@Mappings, URI, and method)
    @GetMapping("/authors/{id}/books")
    public ResponseEntity<Object> getBooksByAuthorId(@PathVariable("id") Long id) throws AuthorNotFoundException, BookNotFoundException {
        Set<Book> books = authorService.findBooksByAuthorid(id);
        if(books.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    // TODO - createAuthor (@Mappings, URI, and method)
    @PostMapping("/authors")
    public ResponseEntity<Author> createAuthor(@RequestBody Author author){
        Author aut = authorService.saveAuthor(author);
        return new ResponseEntity<>(aut, HttpStatus.CREATED);
    }

    // TODO - updateAuthor (@Mappings, URI, and method)
    @PutMapping("/authors/{id}")
    public ResponseEntity<Author> updateAuthor(@PathVariable("id")Long id, @RequestBody Author author) throws AuthorNotFoundException, BookNotFoundException, UserNotFoundException {

        Author updatedAut = authorService.updateAuthor(author,id);

        return new ResponseEntity<>(updatedAut,HttpStatus.OK);


    }
}
