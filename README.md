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
