package LibreriaWeb.libreria;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableAutoConfiguration
@EnableJpaRepositories
@EntityScan("LibreriaWeb.libreria.entidades")
@ComponentScan("LibreriaWeb.libreria")

@SpringBootApplication
public class LibreriaApplication {

    public static void main(String[] args) {
        SpringApplication.run(LibreriaApplication.class, args);
    }


}
