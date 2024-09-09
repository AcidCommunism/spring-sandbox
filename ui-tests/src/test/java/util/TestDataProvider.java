package util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springsandbox.domain.CustomerTestDataYaml;
import org.springsandbox.util.AppConfig;
import org.springsandbox.util.ObjectMapperProvider;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class TestDataProvider {
    private static final ObjectMapper yamlMapper = ObjectMapperProvider.getInstance();

    public static CustomerTestDataYaml provideCustomerData() {
        String customerTestDataFilePath = "src/test/resources/data/customer-tests-data-provider.yml";
        try {
            customerTestDataFilePath = System.getProperty("customerTestDataFilePath");
        } catch (IllegalArgumentException | NullPointerException ignored) {
        }
        try {
            return yamlMapper.readValue(new File(customerTestDataFilePath),
                    CustomerTestDataYaml.class);
        } catch (IOException e) {
            Logger logger = LoggerFactory.getLogger(AppConfig.class.getSimpleName());
            logger.error(e.getMessage());
            logger.error(Arrays.toString(e.getStackTrace()));
        }
        return null;
    }
}