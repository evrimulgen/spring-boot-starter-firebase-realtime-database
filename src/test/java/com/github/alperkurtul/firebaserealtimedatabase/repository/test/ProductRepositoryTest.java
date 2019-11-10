package com.github.alperkurtul.firebaserealtimedatabase.repository.test;

import com.github.alperkurtul.firebaserealtimedatabase.bean.FirebaseSaveResponse;
import com.github.alperkurtul.firebaserealtimedatabase.configuration.FirebaseConfiguration;
import com.github.alperkurtul.firebaserealtimedatabase.configuration.FirebaseDbConfig;
import com.github.alperkurtul.firebaserealtimedatabase.exception.HttpNotFoundException;
import com.github.alperkurtul.firebaserealtimedatabase.repository.model.Product;
import com.github.alperkurtul.firebaserealtimedatabase.repository.model.ProductRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

@SpringBootTest(classes = FirebaseConfiguration.class)
@RunWith(SpringRunner.class)
@Import(ProductRepositoryTest.RepositoryTestConfiguration.class)
@EnableConfigurationProperties(FirebaseDbConfig.class)
public class ProductRepositoryTest {

    private String userAuthKey = "eyJhbGciOiJSUzI1NiIsImtpZCI6IjI1MDgxMWNkYzYwOWQ5MGY5ODE1MTE5MWIyYmM5YmQwY2ViOWMwMDQiLCJ0eXAiOiJKV1QifQ.eyJpc3MiOiJodHRwczovL3NlY3VyZXRva2VuLmdvb2dsZS5jb20vZmx1dHRlci1wcm9kdWN0cy1hYzIwMyIsImF1ZCI6ImZsdXR0ZXItcHJvZHVjdHMtYWMyMDMiLCJhdXRoX3RpbWUiOjE1NzMzODA3NjAsInVzZXJfaWQiOiJJVHh5dXY5Q1lKaHA0SUVodnF2eFowYjZEQngyIiwic3ViIjoiSVR4eXV2OUNZSmhwNElFaHZxdnhaMGI2REJ4MiIsImlhdCI6MTU3MzM4MDc2MCwiZXhwIjoxNTczMzg0MzYwLCJlbWFpbCI6InRlc3Q3QHRlc3QuY29tIiwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJmaXJlYmFzZSI6eyJpZGVudGl0aWVzIjp7ImVtYWlsIjpbInRlc3Q3QHRlc3QuY29tIl19LCJzaWduX2luX3Byb3ZpZGVyIjoicGFzc3dvcmQifX0.YJtIxCDwvi81IleZJI1juA5xD5W7IDUtHPrDExI4Cgi9yPNMA16gxjh5e8FlSTvkgpcFMATNhrUMEMpLNVMrqckgapyqlUqBlS5kv6fIIaykTpqPfYtkrAZnzFV72XsL7UKm9SiKduZ1WgNvTtgpOHUjnp1Qx6bSUUwQv0AeT07xjP_JImJM2G91SJR3SA_GRKUIG-zoX1j2U7Sq6JXJ1YZjS2HbFdlgDKJG58SxYtxVS8I_dfZyMOQ837-l_tN-mqtOP1JGBIvoMnN2k0XqZvQUeeycFkA5hh4f5DXXvDDu0rwm5tmjoZiFL2wO8HisD9_5GRieXcrSVY8LNvzfPg";
    private String firebaseId = "";

    @TestConfiguration
    public static class RepositoryTestConfiguration {

        @Bean
        public RestTemplate restTemplate() {
            return new RestTemplate();
        }

        @Bean
        public ProductRepository productRepository() {
            return new ProductRepository();
        }

    }

    @Autowired
    private ProductRepository productRepository;

    @Test
    public void itShouldSaveReadUpdateDeleteAndSaveAgainWithSuccess() {

        Product product = new Product();
        product.setAuthKey(this.userAuthKey);
        product.setId("JUnit-0001");
        product.setName("Spring Boot JUnit Test");
        product.setPrice(BigDecimal.valueOf(12,55));
        FirebaseSaveResponse savedResponse = productRepository.save(product);
        assertNotNull(savedResponse);
        assertNotNull(savedResponse.getName());
        assertNotEquals(savedResponse.getName(), "");
        this.firebaseId = savedResponse.getName();
        product.setFirebaseId(this.firebaseId);

        Product read = productRepository.read(product);
        assertNotNull(read);
        assertNotNull(read.getId());
        assertThat(read.getId(), is(product.getId()));

        product.setName("Spring Boot JUnit Test-UPDATED");
        productRepository.update(product);
        Product updatedResponse = productRepository.read(product);
        assertNotNull(updatedResponse);
        assertNotNull(updatedResponse.getId());
        assertThat(read.getId(), is(updatedResponse.getId()));
        assertThat(updatedResponse.getName(), is(product.getName()));

        productRepository.delete(product);
        try {
            Product deletedResponse = productRepository.read(product);
        } catch (HttpNotFoundException e) {
            assertThat(e.getMessage(), is("FirebaseDocumentId Not Found"));
        }

    }

}
