package geist.hegel.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import geist.hegel.common.R;
import geist.hegel.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Resource
    private SysUserService userDetailsService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
    }

    private void writeJson(HttpServletResponse resp, int status, Object body) {
        try {
            resp.setStatus(status);
            resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
            resp.setContentType(MediaType.APPLICATION_JSON_VALUE);
            resp.getWriter().write(objectMapper.writeValueAsString(body));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .exceptionHandling()
            .authenticationEntryPoint((req, resp, e) -> writeJson(resp, HttpStatus.UNAUTHORIZED.value(), R.fail("未登录或登录过期")))
            .accessDeniedHandler((req, resp, e) -> writeJson(resp, HttpStatus.FORBIDDEN.value(), R.fail("无权限")))
            .and()

            .authorizeRequests()
                .antMatchers("/admin/**").permitAll()
                .antMatchers("/front/**").permitAll()
                .antMatchers("/upload/**").permitAll()
                // ✅ 入口也放行（/front /admin）
            .antMatchers("/api/auth/login", "/api/auth/register", "/api/auth/me","/upload/**").permitAll()
            .anyRequest().permitAll()
            .and()

            .formLogin().disable()
            .logout().disable();
    }

}
