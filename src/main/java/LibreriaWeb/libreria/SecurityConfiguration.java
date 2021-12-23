package LibreriaWeb.libreria;

import LibreriaWeb.libreria.servicios.ClienteServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private ClienteServicio clienteServicio;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(clienteServicio).passwordEncoder(new BCryptPasswordEncoder(4));
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/css/*", "/js/*", "/img/*, /**").permitAll()
                .antMatchers("/autor/*", "/prestamo/*", "/libros/*", "/cliente/*", "/editorial/*", "/home/inicio/").access("hasRole('ADMIN')")
                .antMatchers("/home/login", "/home/registro", "/","/home/", "/home/registrar").permitAll()
                .anyRequest().authenticated()
                .and().formLogin()
                .loginPage("/home/login").permitAll()
                    .loginProcessingUrl("/home/logincheck")
                    .usernameParameter("usuario")
                    .passwordParameter("password")
                    .defaultSuccessUrl("/usuario/")
                .and().logout()
                    .logoutUrl("/home/logout")
                    .logoutSuccessUrl("/home/")
                    .permitAll();
    }
}
