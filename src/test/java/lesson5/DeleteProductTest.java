package lesson5;

import com.github.javafaker.Faker;
import lesson5.api.ProductService;
import lesson5.dto.Product;
import lesson5.utils.RetrofitUtils;
import lombok.SneakyThrows;
import okhttp3.ResponseBody;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import retrofit2.Response;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;


public class DeleteProductTest {

    static ProductService productService;
    Product product = null;
    Faker faker = new Faker();
    int id;
    String withTitle;
    String withCategoryTitle;
    int withPrice;


    @BeforeAll
    static void beforeAll() {
        productService = RetrofitUtils.getRetrofit()
                .create(ProductService.class);
    }

    @BeforeEach
    void setUp() throws IOException {
        product = new Product()
                .withTitle(faker.food().ingredient())
                .withCategoryTitle("Food")
                .withPrice((int) (Math.random() * 10000));


        Response<Product> response = productService.createProduct(product)
                .execute();
        id =  response.body().getId();

        assertThat(response.isSuccessful(), CoreMatchers.is(true));

    }


    @Test
    void deleteProductIdTest() throws IOException {
        Response<ResponseBody> response = productService.deleteProduct(id).execute();
        assertThat(response.isSuccessful(), CoreMatchers.is(true));

        Response<Product> response2 = productService.getProductById(id).execute();
        assertThat(response2.isSuccessful(), CoreMatchers.is(false));

    }


}