package dilash;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "dilash")
public class DilashApplication {

	public static void main(String[] args) {
		SpringApplication.run(DilashApplication.class, args);
	}
}
