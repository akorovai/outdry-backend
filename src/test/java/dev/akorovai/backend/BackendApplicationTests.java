package dev.akorovai.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

@SpringBootTest(properties = "spring.main.lazy-initialization=true",
		classes = {BackendApplication.class})
class BackendApplicationTests {

	@Test
	void contextLoads() {
	}

}
