package geist.hegel.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path uploadDir = Paths.get(System.getProperty("user.dir"), "upload")
                .toAbsolutePath()
                .normalize();

        String location = uploadDir.toUri().toString(); // ✅ file:/E:/.../upload/
        registry.addResourceHandler("/upload/**")
                .addResourceLocations(location);
        registry.addResourceHandler("/admin/**")
                .addResourceLocations("classpath:/webapp/backend/dist/");

        registry.addResourceHandler("/front/**")
                .addResourceLocations("classpath:/webapp/front/dist/");
    }
}