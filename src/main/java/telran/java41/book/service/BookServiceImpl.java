package telran.java41.book.service;

import java.util.Set;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import telran.java41.book.dao.AuthorRepository;
import telran.java41.book.dao.BookRepository;
import telran.java41.book.dao.PublisherRepository;
import telran.java41.book.dto.AuthorDto;
import telran.java41.book.dto.BookDto;
import telran.java41.book.dto.exceptions.EntityNotFoundException;
import telran.java41.book.model.Author;
import telran.java41.book.model.Book;
import telran.java41.book.model.Publisher;

@Service
@AllArgsConstructor
public class BookServiceImpl implements BookService {
	BookRepository bookRepository;
	AuthorRepository authorRepository;
	PublisherRepository publisherRepository;
	ModelMapper modelMapper;

	@Override
	@Transactional
	public boolean addBook(BookDto bookDto) {
		if(bookRepository.existsById(bookDto.getIsbn())) {
			return false;
		}
		//Publisher
		Publisher publisher = publisherRepository.findById(bookDto.getPublisher())
				.orElse(publisherRepository.save(new Publisher(bookDto.getPublisher())));
		//Authors
		Set<Author> authors = bookDto.getAuthors().stream()
								.map(a -> authorRepository.findById(a.getName())
										.orElse(authorRepository.save(new Author(a.getName(), a.getBirthDate()))))
								.collect(Collectors.toSet());
		Book book = new Book(bookDto.getIsbn(), bookDto.getTitle(), authors, publisher);
		bookRepository.save(book);
		return true;
	}

	@Override
	public BookDto findBookByIsbn(Long isbn) {
		Book book = bookRepository.findById(isbn).orElseThrow(() -> new EntityNotFoundException());
		return modelMapper.map(book, BookDto.class);
	}

	@Override
	@Transactional
	public BookDto removeBook(Long isbn) {
		Book book = bookRepository.findById(isbn).orElseThrow(() -> new EntityNotFoundException());
		bookRepository.delete(book);
		return modelMapper.map(book, BookDto.class);
	}

	@Override
	@Transactional
	public BookDto updateBook(Long isbn, String title) {
		Book book = bookRepository.findById(isbn).orElseThrow(() -> new EntityNotFoundException());
		book.setTitle(title);
		return modelMapper.map(book, BookDto.class);
	}

	@Override
	@Transactional(readOnly = true)
	public Iterable<BookDto> findBooksByAuthor(String authorName) {
		return bookRepository.findByAuthorsName(authorName)
				.map(b -> modelMapper.map(b, BookDto.class))
				.collect(Collectors.toList());
	}

	@Override
	public Iterable<BookDto> findBooksByPublisher(String publisherName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterable<AuthorDto> findBookAuthors(Long isbn) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterable<String> findPublishersByAuthor(String authorName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AuthorDto removeAuthor(String authorName) {
		// TODO Auto-generated method stub
		return null;
	}

}
