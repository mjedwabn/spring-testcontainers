package xyz.mjedwabn.springtestcontainers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Testcontainers
@SpringBootTest
class TestContainerTest {
	@Container
	static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest")
			.withExposedPorts(5432)
			.withDatabaseName("test")
			.withUsername("postgres")
			.withPassword("example");

	@Autowired
	CustomerRepository customerRepository;

	@DynamicPropertySource
	static void configureProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", postgres::getJdbcUrl);
		registry.add("spring.datasource.username", postgres::getUsername);
		registry.add("spring.datasource.password", postgres::getPassword);
	}

	@Test
	void name() {
		Customer john = customerRepository.save(new Customer(null, "John"));

		assertThat(john.getName()).isEqualTo("John");
		assertThat(john.getId()).isNotNull();
		assertThat(john.getId()).isEqualTo(1L);
	}
}
