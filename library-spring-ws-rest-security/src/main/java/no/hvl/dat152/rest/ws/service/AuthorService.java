/**
 * 
 */
package no.hvl.dat152.rest.ws.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import no.hvl.dat152.rest.ws.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import no.hvl.dat152.rest.ws.exceptions.AuthorNotFoundException;
import no.hvl.dat152.rest.ws.model.Author;
import no.hvl.dat152.rest.ws.model.Book;
import no.hvl.dat152.rest.ws.repository.AuthorRepository;

/**
 * @author tdoy
 */
@Service
public class AuthorService {

	// TODO copy your solutions from previous tasks!
    @Autowired
    private AuthorRepository authorRepository;


    public Author findById(long id) throws AuthorNotFoundException {

        Author author = authorRepository.findById(id)
                .orElseThrow(()-> new AuthorNotFoundException("Author with the id: "+id+ "not found!"));

        return author;
    }

    // TODO public saveAuthor(Author author)
    public Author saveAuthor(Author author) {
        return authorRepository.save(author);
    }


    // TODO public Author updateAuthor(Author author, int id)
    public Author updateAuthor(Author author, Long id) throws NoSuchElementException, UserNotFoundException {
        Author existingAut = authorRepository.findById(id).orElseThrow(()-> new UserNotFoundException("not found user"));
        existingAut.setFirstname(author.getFirstname());
        existingAut.setLastname(author.getLastname());
        if (author != null){
            return saveAuthor(author);
        }else {
            throw new UserNotFoundException("author not found");
        }
    }


    // TODO public List<Author> findAll()
    public List<Author> findAll(){
        return (List<Author>) authorRepository.findAll();
    }

    // TODO public void deleteById(Long id) throws AuthorNotFoundException
    public void deleteById(Long id) throws AuthorNotFoundException {
        Author author = authorRepository.findById(id).orElseThrow(() -> new AuthorNotFoundException("Author not found"));
        authorRepository.delete(author);

    }

    // TODO public Set<Book> findBooksByAuthorId(Long id)
    public Set<Book> findBooksByAuthorid(Long id) throws AuthorNotFoundException{
        return findById(id).getBooks();
    }
}
