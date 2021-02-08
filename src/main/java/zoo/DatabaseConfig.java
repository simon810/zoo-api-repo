package zoo;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;
import java.net.URI;
import java.net.URISyntaxException;

@Configuration
@Profile("heroku")
public class DatabaseConfig {
    //postgres://lurststtfvdaez:7067750bbf31a8b39d40e0dd02af55e9c0a71bdabad1914879e94094da2e5dba@ec2-34-239-33-57.compute-1.amazonaws.com:5432/d61m6mqj8udtij

    @Bean
    public DataSource dataSource() throws URISyntaxException {
        URI dbUri = new URI(System.getenv("DATABASE_URL"));

        String username = dbUri.getUserInfo().split(":")[0];
        String password = dbUri.getUserInfo().split(":")[1];
        String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();

        return DataSourceBuilder.create()
                .url(dbUrl)
                .username(username)
                .password(password)
                .build();
    }
}
