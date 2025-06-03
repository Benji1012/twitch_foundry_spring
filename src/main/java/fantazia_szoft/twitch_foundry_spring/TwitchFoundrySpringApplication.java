package fantazia_szoft.twitch_foundry_spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@SpringBootApplication
public class TwitchFoundrySpringApplication {

	public static void main(String[] args) {
		SpringApplication.run(TwitchFoundrySpringApplication.class, args);
	}

	@Bean
	public WebMvcConfigurer webMvcConfigurer() {
	    return new WebMvcConfigurer() {
	        @Override
	        public void addCorsMappings(CorsRegistry registry) {
	            registry.addMapping("/**")
	                    .allowedOrigins("http://127.0.0.1:8082", "https://zed4vietrc5tn65xtn8eg5y01hr14k.ext-twitch.tv")
	                    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
	                    .allowedHeaders("*")
	                    .exposedHeaders("*")
	                    .allowCredentials(true);
	        }

	        @Override
	        public void addInterceptors(InterceptorRegistry registry) {
	            registry.addInterceptor(cspInterceptor());
	        }
	    };
	}

	@Bean
	public HandlerInterceptor cspInterceptor() {
	    return new HandlerInterceptor() {
	        @Override
	        public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
	                               org.springframework.web.servlet.ModelAndView modelAndView) {
	            response.setHeader("Content-Security-Policy",
	                    "frame-ancestors https://*.twitch.tv https://*.ext-twitch.tv");
	        }
	    };
	}
}
