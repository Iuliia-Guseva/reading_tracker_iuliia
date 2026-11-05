package reading_tracker.config;

import java.time.Duration;
import java.util.Locale;
import java.util.Set;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.server.ResponseStatusException;

@Configuration
public class LocaleConfig implements WebMvcConfigurer {

    // Only these languages have translation files.
    private static final Set<String> SUPPORTED_LANGUAGES = Set.of("en", "fi");

    @Bean
    public LocaleResolver localeResolver() {
        // Remember the selected language in a cookie for one year.
        CookieLocaleResolver resolver = new CookieLocaleResolver("reading-tracker-locale");
        resolver.setCookieMaxAge(Duration.ofDays(365));
        resolver.setDefaultLocale(Locale.ENGLISH);
        return resolver;
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
                    throws ServletException {
                String language = request.getParameter("lang");
                // Do not allow unsupported language values to reach the locale resolver.
                if (language != null && !SUPPORTED_LANGUAGES.contains(language)) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported language");
                }
                return super.preHandle(request, response, handler);
            }
        };
        interceptor.setParamName("lang");
        return interceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Let requests with ?lang=en or ?lang=fi change the current language.
        registry.addInterceptor(localeChangeInterceptor());
    }
}
