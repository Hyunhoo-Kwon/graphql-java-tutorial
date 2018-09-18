# graphql-java-tutorial
GraphQL-Java API 서버 구현 튜토리얼 (with Spring Boot 2.0)

This tutorial will use:
 - Java8
 - Spring Boot 2.0.4 with an embedded H2 database
 - graphql-java, a GraphQL Java implementation  
 - GraphiQL, an app for editing and testing GraphQL
 - Gradle

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
[스프링 부트 프로젝트 생성](https://start.spring.io/) 옵션:
 - Gradle Project
 - Java
 - As dependencies:
   - Web
   - H2
   - JPA
   - DevTools
   - Lombok

[build.gradle](https://github.com/Hyunhoo-Kwon/graphql-java-tutorial/blob/master/build.gradle) 파일에 디펜던시 추가:
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

H2 데이터베이스 접속 방법: http://localhost:8080/h2-console/login.jsp
- JDBC URL: jdbc:h2:mem:testdb
- User Name: sa
- Password: (empty)

### 2. 데이터 모델
>데이터 모델 전체 코드: https://github.com/Hyunhoo-Kwon/graphql-java-tutorial/tree/feature/domain

 1. 엔티티 추가: model 패키지에 Author, Book 엔티티 추가
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
 2. CRUD repository 구현: repository 패키지에 AuthorRepository, BookRepository 인터페이스 추가
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
 4. 테스트 코드:
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
