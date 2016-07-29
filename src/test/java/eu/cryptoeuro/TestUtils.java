package eu.cryptoeuro;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

public class TestUtils {

	public static MockMvc getMockMvc(Object... controllers) {
		final StaticApplicationContext applicationContext = new StaticApplicationContext();
		final WebMvcConfigurationSupport webMvcConfigurationSupport = new WebMvcConfigurationSupport();
		webMvcConfigurationSupport.setApplicationContext(applicationContext);

		ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		converter.setObjectMapper(objectMapper);
		return org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup(controllers)
				.setHandlerExceptionResolvers(webMvcConfigurationSupport.handlerExceptionResolver())
				.setMessageConverters(converter)
				.build();
	}
}
