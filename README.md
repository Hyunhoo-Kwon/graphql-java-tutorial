# graphql-java-tutorial

This tutorial will use:
 - Java8
 - Spring Boot 2.0.4 with an embedded H2 database
 - graphql-java, a GraphQL Java implementation  
 - GraphiQL, an app for editing and testing GraphQL
 - Gradle


## 1. 프로젝트 환경설정
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
