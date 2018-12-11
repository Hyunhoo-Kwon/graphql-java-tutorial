# graphql-java-tutorial
GraphQL-Java API 서버 구현 튜토리얼 (with Spring Boot 2.0)

This tutorial will use:
 - Java8
 - Spring Boot 2.0.4 with an embedded H2 database
 - graphql-java, a GraphQL Java implementation  
 - GraphiQL, an app for editing and testing GraphQL
 - Gradle
 
## 목차
 1. [GraphQL 이란](https://github.com/Hyunhoo-Kwon/graphql-java-tutorial/blob/master/README.md#graphql-%EC%9D%B4%EB%9E%80)
 2. [GraphQL API 서버 구현 예제](https://github.com/Hyunhoo-Kwon/graphql-java-tutorial/blob/master/README.md#graphql-api-%EC%84%9C%EB%B2%84-%EA%B5%AC%ED%98%84-%EC%98%88%EC%A0%9C)
	1. [프로젝트 환경설정](https://github.com/Hyunhoo-Kwon/graphql-java-tutorial/blob/master/README.md#1-%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8-%ED%99%98%EA%B2%BD%EC%84%A4%EC%A0%95)
	2. [데이터 모델](https://github.com/Hyunhoo-Kwon/graphql-java-tutorial/blob/master/README.md#2-%EB%8D%B0%EC%9D%B4%ED%84%B0-%EB%AA%A8%EB%8D%B8)
	3. [GraphQL 구현](https://github.com/Hyunhoo-Kwon/graphql-java-tutorial/blob/master/README.md#3-graphql-%EA%B5%AC%ED%98%84)
	4. [API 호출 테스트](https://github.com/Hyunhoo-Kwon/graphql-java-tutorial/blob/master/README.md#4-api-%ED%98%B8%EC%B6%9C-%ED%85%8C%EC%8A%A4%ED%8A%B8)
 3. [참고](https://github.com/Hyunhoo-Kwon/graphql-java-tutorial/blob/master/README.md#%EC%B0%B8%EA%B3%A0)

## GraphQL 이란
[GraphQL](https://graphql.org/)은 페이스북이 2012년 개발하여 2015년 공개적으로 발표된 API를 위한 쿼리 언어이며 REST 아키텍처를 대체할 수 있습니다. 클라이언트는 필요한 데이터의 구조를 지정할 수 있으며, 서버는 정확히 동일한 구조로 데이터를 반환합니다. GraphQL은 특정 데이터베이스 또는 스토리지 엔진과 연결되어 있지 않으며 대신 기존 코드 및 데이터에 의해 뒷받침됩니다.
 - REST의 단점
   > 기존의 RESTful API는 개별 리소스의 URI에 HTTP 요청을 보내는 방식으로, 새로운 리소스가 필요할 때마다 새로운 endpoint를 만들어야 합니다. 또한, REST 방식은 클라이언트가 API 요청에 대한 응답에 어떤 데이터가 포함되는지 선택할 수 없습니다. 이는 불필요한 데이터가 응답에 포함되는 OverFetching과 필요한 데이터가 응답에 포함되지 않아 여러 번 요청을 보내야 하는 UnderFetching의 원인이 됩니다.
 - GraphQL 쿼리
   - 쿼리
   ```
   query {
	  findAllBooks {
		id
		title
		isbn
		pageCount
		author {
		  firstName
		  lastName
		}
	  }
	}
   ```
   - 결과
   ```
   {
	  "data": {
		"findAllBooks": [
		  {
			"id": "2",
			"title": "Java: A Beginner's Guide, Sixth Edition",
			"isbn": "0071809252",
			"pageCount": 728,
			"author": {
			  "firstName": "elon",
			  "lastName": "kwon"
			}
		  }
		]
	  }
	}
   ```
 - GraphQL 스키마 & 타입
   - 객체 타입
   > GraphQL 스키마의 가장 기본적인 구성 요소는 객체 타입입니다. 객체 타입은 서비스에서 가져올 수 있는 객체의 종류와 그 객체의 필드를 나타내며 객체 타입의 모든 필드는 0개 이상의 인수를 가질 수 있습니다.
   ```
   type Book {
	    id: ID!
	    title: String!
	    isbn: String!
	    pageCount: Int
	    author: Author
	}
   ```
       - !는 필드가 non-nullable임을 의미합니다.
       - ID는 객체를 다시 요청하거나 캐시의 키로써 자주 사용되는 고유 식별자를 나타냅니다. ID 타입은 String과 같은 방법으로 직렬화됩니다.
   - query & mutation 타입
   > GraphQL 스키마 내에는 특수한 두 가지 타입, query와 mutation이 있습니다. 모든 GraphQL 서비스는 query 타입을 가지며 mutation 타입은 가질 수도 있고 가지지 않을 수도 있습니다. 이러한 타입은 일반 객체 타입과 동일하지만 모든 GraphQL 쿼리의 진입점을 정의하므로 특별합니다.
   ```
   type Query {
	    findAllBooks: [Book]!
	}

	type Mutation {
	    newBook(title: String!, isbn: String!, pageCount: Int, author: ID!) : Book!
	}
   ```
       - [Book]!은 Book 객체의 array 를 나타냅니다. 또한 non-nullable 이기 때문에 findAllBooks 필드를 쿼리할 때 항상(0개 이상의 아이템을 가진) 배열을 기대할 수 있습니다.

## GraphQL API 서버 구현 예제
### 1. 프로젝트 환경설정
 1. [스프링 부트 프로젝트 생성](https://start.spring.io/) 옵션:
	 - Gradle Project
	 - Java
	 - As dependencies:
	   - Web
	   - H2
	   - JPA
	   - DevTools
	   - Lombok

 2. [build.gradle](https://github.com/Hyunhoo-Kwon/graphql-java-tutorial/blob/master/build.gradle) 파일에 디펜던시 추가:
	- [graphql-spring-boot-starter](https://github.com/graphql-java/graphql-spring-boot)
	- [graphiql-spring-boot-starter](https://github.com/graphql-java/graphql-spring-boot)
	- [graphql-java-tools](https://github.com/graphql-java/graphql-java-tools)
	```
	dependencies {
		compile('org.springframework.boot:spring-boot-starter-data-jpa')
		compile('org.springframework.boot:spring-boot-starter-web')
		runtime('org.springframework.boot:spring-boot-devtools')
		runtime('com.h2database:h2')
		compileOnly('org.projectlombok:lombok')
		testCompile('org.springframework.boot:spring-boot-starter-test')
		compile('com.graphql-java:graphql-spring-boot-starter:5.0.2')
		compile('com.graphql-java:graphiql-spring-boot-starter:5.0.2')
		compile('com.graphql-java:graphql-java-tools:5.2.0')
	}
	```

 3. H2 데이터베이스 접속 방법: http://localhost:8080/h2-console/login.jsp
	- JDBC URL: jdbc:h2:mem:testdb
	- User Name: sa
	- Password: (empty)

### 2. 데이터 모델
>데이터 모델 전체 코드: https://github.com/Hyunhoo-Kwon/graphql-java-tutorial/tree/domain/src/main/java/com/elon/graphql

 1. 엔티티 구현: model 패키지에 Author, Book 엔티티 구현
	 - Author.java
	 ```
	 @Entity
	@NoArgsConstructor @Data
	public class Author {
		@Id
		@GeneratedValue(strategy= GenerationType.AUTO)
		private Long id;

		private String firstName;

		private String lastName;

		public Author(Long id) {
		this.id = id;
		}

		public Author(String firstName, String lastName) {
		this.firstName = firstName;
		this.lastName = lastName;
		}
	}
	 ```
	 - Book.java
	 ```
	 @Entity
	@NoArgsConstructor @Data
	public class Book {
		@Id
		@GeneratedValue(strategy= GenerationType.AUTO)
		private Long id;

		private String title;

		private String isbn;

		private int pageCount;

		@ManyToOne
		@JoinColumn(name = "author_id", nullable = false, updatable = false)
		private Author author;

		public Book(String title, String isbn, int pageCount, Author author) {
		this.title = title;
		this.isbn = isbn;
		this.pageCount = pageCount;
		this.author = author;
		}
	}
	 ```
 2. CRUD repository 구현: repository 패키지에 AuthorRepository, BookRepository 인터페이스 구현
	 - AuthorRepository.java
	 ```
	 @Repository
	public interface AuthorRepository extends CrudRepository<Author, Long> { }
	 ```
	 - BookRepository.java
	 ```
	 @Repository
	public interface BookRepository extends CrudRepository<Book, Long> { }
	 ```
 3. (Option) 스프링 애플리케이션 실행 시 데이터베이스에 데이터 insert:
	 - GraphqlApplication.java에 CommandLineRunner로 데이터 insert 구현
	 ```
	 @Bean
	public CommandLineRunner demo(AuthorRepository authorRepository, BookRepository bookRepository) {
		return (args) -> {
			Author author = new Author("elon", "kwon");
			authorRepository.save(author);
			bookRepository.save(new Book("Java: A Beginner's Guide, Sixth Edition", "0071809252", 728, author));

		};
	}
	 ```
 4. 테스트 코드 작성:
	 - AuthorRepositoryTest.java
	 ```
	 @RunWith(SpringRunner.class)
	@SpringBootTest
	public class AuthorRepositoryTest {
		@Autowired
		AuthorRepository authorRepository;

		@Test
		public void findAll() {
		Assert.assertNotNull(authorRepository.findAll());
		}

	}
	 ```
	 - BookRepositoryTest.java
	 ```
	 @RunWith(SpringRunner.class)
	@SpringBootTest
	public class BookRepositoryTest {
		@Autowired
		BookRepository bookRepository;

		@Test
		public void findAll() {
			Assert.assertNotNull(bookRepository.findAll());
		}

	}
	 ```
### 3. GraphQL 구현
#### 3-1. Author GraphQL 구현
> Author GraphQL 구현 전체 코드: https://github.com/Hyunhoo-Kwon/graphql-java-tutorial/tree/author/src/main
 1. Author 스키마 추가: GraphQL 스키마는 .graphqls 파일에 정의한다. /src/main/resources/graphql 폴더에 author.graphqls 작성
	 - author.graphqls
	 ```
	 type Author {
	    id: ID!
	    firstName: String!
	    lastName: String!
	}

	type Query {
	    findAllAuthors: [Author]!
	    countAuthors: Long!
	}

	type Mutation {
	    newAuthor(firstName: String!, lastName: String!) : Author!
	}
	 ```
 2. Author query 구현: resolver 패키지에 Query 클래스 구현. Query는 GraphQLQueryResolver를 구현하여 작성할 수 있다.
	 - Query.java
	 ```
	 @Service
	public class Query implements GraphQLQueryResolver {
	    @Autowired
	    private AuthorRepository authorRepository;

	    public Iterable<Author> findAllAuthors() {
		return authorRepository.findAll();
	    }

	    public long countAuthors() {
		return authorRepository.count();
	    }
	}
	 ```
3. Author mutaion 구현: resolver 패키지에 Mutation 클래스 구현. Mutation는 GraphQLMutationResolver를 구현하여 작성할 수 있다.
	- Mutation.java
	```
	@Service
	public class Mutation implements GraphQLMutationResolver {
	    @Autowired
	    private AuthorRepository authorRepository;

	    public Author newAuthor(String firstName, String lastName) {
		Author author = new Author(firstName, lastName);
		authorRepository.save(author);
		return author;
	    }
	}
	```
4. 테스트 코드 작성:
	- QueryTest.java
	> graphql-spring-boot-starter:5.0.2 버전 junit test 버그로 @SpringBootTest에 webEnvironment 설정 필요. (참조: [graphql-spring-boot issue #113](https://github.com/graphql-java/graphql-spring-boot/issues/113))
	```
	@RunWith(SpringRunner.class)
	@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
	public class QueryTest {
	    @Autowired
	    Query query;

	    @Test
	    public void findAllAuthors() {
		Assert.assertNotNull(query.findAllAuthors());
	    }

	    @Test
	    public void countAuthors() {
		Assert.assertNotNull(query.countAuthors());
	    }

	}
	```
API 호출 테스트: https://github.com/Hyunhoo-Kwon/graphql-java-tutorial/blob/master/README.md#4-api-%ED%98%B8%EC%B6%9C-%ED%85%8C%EC%8A%A4%ED%8A%B8

#### 3-2. Book GraphQL 구현
> Book GraphQL 구현 전체 코드: https://github.com/Hyunhoo-Kwon/graphql-java-tutorial/tree/book/src/main
 1. Book 스키마 추가:
	 - book.graphqls
	 ```
	 type Book {
	    id: ID!
	    title: String!
	    isbn: String!
	    pageCount: Int
	    author: Author
	}

	extend type Query {
	    findAllBooks: [Book]!
	    countBooks: Long!
	}

	extend type Mutation {
	    newBook(title: String!, isbn: String!, pageCount: Int, author: ID!) : Book!
	    deleteBook(id: ID!) : Boolean
	    updateBookPageCount(pageCount: Int!, id: ID!) : Book!
	}
	 ```
	 > extend Query 타입, extend Mutation 타입: 런타임시 다음과 같이 Query, Mutaion에 포함된다
	 ```
	 type Query {
	    findAllAuthors: [Author]!
	    countAuthors: Long!
	    findAllBooks: [Book]!
	    countBooks: Long!
	}
	 ```
 2. Book resolver 구현: field resolver를 사용하여 해당 필드의 값을 할당할 수 있습니다. field resolver와 데이터 bean 둘 다 동일한 GraphQL 필드의 메소드를 가질 경우 field resolver를 우선합니다.
 	 - BookResolver.java: Book 엔티티에 정의된 getAuthor 메소드 보다 BookResolver에 정의된 getAuthor 메소드를 우선합니다.
	 ```
	 @Service
	public class BookResolver implements GraphQLResolver<Book> {
	    @Autowired
	    AuthorRepository authorRepository;

	    public Author getAuthor(Book book) {
		return authorRepository.findById(book.getAuthor().getId()).orElse(null);
	    }
	}
	 ```
 3. Book query 구현:
	 - Query.java
	 ```
	 // ...

	 @Autowired
	 private BookRepository bookRepository;

	 public Iterable<Book> findAllBooks() {
	     return bookRepository.findAll();
	 }

	 public long countBooks() {
	     return bookRepository.count();
	 }
	 ```
 4. Book mutation 구현:
	 - Mutation.java
	 ```
	 // ...

	 @Autowired
	 private BookRepository bookRepository;

	 public Book newBook(String title, String isbn, Integer pageCount, Long authorId) {
	     Book book = new Book();
	     book.setAuthor(new Author(authorId));
	     book.setTitle(title);
	     book.setIsbn(isbn);
	     book.setPageCount(pageCount != null ? pageCount : 0);
	     bookRepository.save(book);
	     return book;
	 }

	 public boolean deleteBook(Long id) {
	     bookRepository.deleteById(id);
	     return true;
	 }

	 public Book updateBookPageCount(Integer pageCount, Long id) {
	     Book book = bookRepository.findById(id).orElse(null);
	     book.setPageCount(pageCount);
	     bookRepository.save(book);
	     return book;
	 }
	 ```
API 호출 테스트: https://github.com/Hyunhoo-Kwon/graphql-java-tutorial/blob/master/README.md#4-api-%ED%98%B8%EC%B6%9C-%ED%85%8C%EC%8A%A4%ED%8A%B8

#### 3-3. 예외처리
> 예외처리 구현 전체 코드: https://github.com/Hyunhoo-Kwon/graphql-java-tutorial/tree/errorHandler/src/main/java/com/elon/graphql

 1. 미구현 예외 처리방법 살펴보기:
   - 서버 측에서 처리되지 않은 예외는 [DefaultGraphQLErrorHandler](https://github.com/graphql-java/graphql-java-servlet/blob/master/src/main/java/graphql/servlet/DefaultGraphQLErrorHandler.java)에 의해 클라이언트에 Internal server error를 반환합니다.
   ```
   public List<GraphQLError> processErrors(List<GraphQLError> errors) {
        final List<GraphQLError> clientErrors = filterGraphQLErrors(errors);
        if (clientErrors.size() < errors.size()) {

            // Some errors were filtered out to hide implementation - put a generic error in place.
            clientErrors.add(new GenericGraphQLError("Internal Server Error(s) while executing query"));

            // ...
        }

        return clientErrors;
    }
   ```
   - 클라이언트 Internal server error 메세지
   ```
   {
  "data": {
    "deleteBook": null
  },
  "errors": [
    {
      "message": "Internal Server Error(s) while executing query",
      "path": null,
      "extensions": null
    }
  ]
}
   ```
 2. GraphQLError 구현:
   - 클라이언트에 올바른 에러를 반환하기 위해서 GraphQLError를 구현하여 예외를 작성해야 합니다.
   - BookNotFoundException 구현: exception 패키지에 BookNotFoundException 예외 작성.
   ```
   public class BookNotFoundException extends RuntimeException implements GraphQLError {

    private Map<String, Object> extensions = new HashMap<>();

    public BookNotFoundException(String message, Long invalidBookId) {
        super(message);
        extensions.put("invalidBookId", invalidBookId);
    }

    @Override
    public List<SourceLocation> getLocations() {
        return null;
    }

    @Override
    public ErrorType getErrorType() {
        return ErrorType.DataFetchingException;
    }

    @Override
    public Map<String, Object> getExtensions() {
        return extensions;
    }
}
   ```
 3. updateBookPageCount 메소드 수정: Mutation 클래스 - updateBookPageCount 메소드 실행시 존재하지 않는 Book Id 일때 BookNotFoundException 발생.
 ```
 @Service
public class Mutation implements GraphQLMutationResolver {
    // ...

    public Book updateBookPageCount(Integer pageCount, Long id) {
        Book book = bookRepository.findById(id).orElseThrow(
                ()->new BookNotFoundException("The book to be updated was not found", id));
        book.setPageCount(pageCount);
        bookRepository.save(book);
        return book;
    }
}
 ```
 4. 테스트 코드 작성:
 ```
 @RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MutationTest {
    //...

    @Test(expected = BookNotFoundException.class)
    public void updateBookPageCountWithInvalidId() {
        mutation.updateBookPageCount(340, (long) 10);
    }
}
 ```
 - 클라이언트 BookNotFoundException 에러 메세지: 클라이언트는 에러 메세지와 함께 전체 stack trace를 반환받습니다.
 ```
 {
  "data": null,
  "errors": [
    {
      "message": "Exception while fetching data (/updateBookPageCount) : The book to be updated was not found",
      "path": [
        "updateBookPageCount"
      ],
      "exception": {
        "cause": null,
        "stackTrace": [
          {
            "methodName": "lambda$updateBookPageCount$0",
            "fileName": "Mutation.java",
            "lineNumber": 43,
            "className": "com.elon.graphql.resolver.Mutation",
            "nativeMethod": false
          },
          {
            "methodName": "orElseThrow",
            "fileName": "Optional.java",
            "lineNumber": 290,
            "className": "java.util.Optional",
            "nativeMethod": false
          },
          ...
        ],
        ...
      },
      "locations": [
        {
          "line": 2,
          "column": 3,
          "sourceName": null
        }
      ],
      "extensions": {
        "invalidBookId": 200
      },
      "errorType": "DataFetchingException"
    }
  ]
}
 ```
#### (Option) 3-4. 예외처리 - 클라이언트에 stack trace 감추기
> 예외처리 - 클라이언트에 stack trace 감추기 전체코드: https://github.com/Hyunhoo-Kwon/graphql-java-tutorial/tree/graphql1.0.0/src/main/java/com/elon/graphql
 
 1. GraphQLErrorAdapter 구현: 
   - _(Internal server error를 반환할 때 사용하는 [GenericGraphQLError](https://github.com/graphql-java/graphql-java-servlet/blob/master/src/main/java/graphql/servlet/GenericGraphQLError.java) 클래스를 참고하여 GraphQLErrorAdapter 클래스 구현)_
   - GraphQLError 예외를 감싸기 위한 GraphQLErrorAdapter 클래스
   ```
   public class GraphQLErrorAdapter implements GraphQLError {

    private GraphQLError error;

    public GraphQLErrorAdapter(GraphQLError error) {
        this.error = error;
    }

    @Override
    public String getMessage() {
        return error.getMessage();
    }

    @JsonIgnore
    @Override
    public List<SourceLocation> getLocations() {
        return error.getLocations();
    }

    @JsonIgnore
    @Override
    public ErrorType getErrorType() {
        return error.getErrorType();
    }

    @Override
    public List<Object> getPath() {
        return error.getPath();
    }

    @Override
    public Map<String, Object> toSpecification() {
        return error.toSpecification();
    }

    @Override
    public Map<String, Object> getExtensions() {
        return error.getExtensions();
    }
}
   ```
   
 2. GraphQLErrorHandler 재정의:
   - 스프링 @Configuration 클래스에 [DefaultGraphQLErrorHandler](https://github.com/graphql-java/graphql-java-servlet/blob/master/src/main/java/graphql/servlet/DefaultGraphQLErrorHandler.java)를 대체할 GraphQLErrorHandler 재정의
   - GraphQLErrorHandlerConfig 클래스에 GraphQLErrorHandler 재정의
     - DefaultGraphQLErrorHandler과 비교: filterGraphQLErrors 메소드에서 클라이언트 에러를 GraphQLErrorAdapter로 감싸도록 변경
   ```
   @Configuration
public class GraphQLErrorHandlerConfig {

    public static final Logger log = LoggerFactory.getLogger(DefaultGraphQLErrorHandler.class);

    @Bean
    public GraphQLErrorHandler errorHandler() {
        return new GraphQLErrorHandler() {
            @Override
            public List<GraphQLError> processErrors(List<GraphQLError> errors) {
                List<GraphQLError> clientErrors = this.filterGraphQLErrors(errors);
                // ...
            }

            private List<GraphQLError> filterGraphQLErrors(List<GraphQLError> errors) {
                return (List)errors.stream().filter(this::isClientError).map(GraphQLErrorAdapter::new).collect(Collectors.toList());
            }

            private boolean isClientError(GraphQLError error) {
                // ...
            }
        };
    }
}
   ```
   - GraphqlApplication 클래스에 GraphQLErrorHandlerConfig 임포트
   ```
   @SpringBootApplication
@Import(GraphQLErrorHandlerConfig.class)
public class GraphqlApplication {
// ...
}
   ```
 - 클라이언트 BookNotFoundException 에러 메세지: 클라이언트는 stack trace를 제외한 에러 메세지를 반환받습니다.
 ```
 {
  "data": null,
  "errors": [
    {
      "message": "Exception while fetching data (/updateBookPageCount) : The book to be updated was not found",
      "path": [
        "updateBookPageCount"
      ],
      "extensions": {
        "invalidBookId": 200
      }
    }
  ]
}
 ```

### 4. API 호출 테스트
 1. GraphQL 스키마 구조 확인: http://localhost:8080/graphql/schema.json
 2. curl을 이용한 HTTP 호출 테스트: 요청은 다음과 같이 /graphql endpoint에 JSON으로 전송합니다
	 - query - finaAllAuthors 호출
	 ```
	 curl \
	  -X POST \
	  -H "Content-Type: application/json" \
	  -d '{ "query": "query { findAllAuthors { id firstName lastName } }" }' \
	  http://localhost:8080/graphql
	 ```
	 - mutation - deleteBook 호출
	 ```
	 curl \
	  -X POST \
	  -H "Content-Type: application/json" \
	  -d '{ "query": "mutation { deleteBook(id: 2) }" }' \
	  http://localhost:8080/graphql
	 ```
 3. [GraphiQL](https://github.com/graphql/graphiql)을 이용한 HTTP 호출 테스트:
	 - query - findAllAuthors 호출
	 ```
	 query {
	  findAllAuthors {
	    id
	    firstName
	    lastName
	  }
	}
	 ```
	 - query - countAuthor 호출
	 ```
	 query {
	  countAuthors 
	}
	 ```
	 - query - findAllBooks 호출
	 ```
	 query {
	  findAllBooks {
	    id
	    title
	    isbn
	    pageCount
	    author {
	      firstName
	      lastName
	    }
	  }
	}
	 ```
	 - mutation - newAuthor 호출
	 ```
	 mutation {
	  newAuthor(firstName: "star", lastName: "bucks") {
	    id,
	    firstName,
	    lastName
	  } 
	}
	```
	- mutation - newBook 호출
	```
	mutation {
	  newBook(title: "GraphQL Guide Book", isbn: "003728402", pageCount: 530, author: 1) {
	    id
	    title
	    isbn
	    pageCount
			author {
	      firstName
	      lastName
	    }
	  } 
	}
	```
	- mutation - updateBookPageCount 호출
	```
	mutation {
	  updateBookPageCount(pageCount: 200, id: 2) {
	    id
	    title
	    isbn
	    pageCount
			author {
	      firstName
	      lastName
	    }
	  } 
	}
	```
	- mutation - deleteBook 호출
	```
	mutation {
	  deleteBook(id: 2)
	}
	```

## 참고
 1. GraphQL documentation: 영문 - https://graphql.org/, 한글 - https://graphql-kr.github.io/
 2. GraphQL Java documentation: https://graphql-java.readthedocs.io/en/latest/
 3. 서버 구현 참고 블로그: https://www.pluralsight.com/guides/building-a-graphql-server-with-spring-boot
