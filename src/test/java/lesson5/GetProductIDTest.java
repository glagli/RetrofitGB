package lesson5;

import com.github.javafaker.Faker;
import lesson5.api.ProductService;
import lesson5.dto.GetCategoryResponse;
import lesson5.dto.Product;
import lesson5.utils.RetrofitUtils;
import lombok.SneakyThrows;
import okhttp3.ResponseBody;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import retrofit2.Response;

import java.io.IOException;
import java.io.InputStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;


public class GetProductIDTest {

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
        withTitle =  response.body().getTitle();
        withCategoryTitle =  response.body().getCategoryTitle();
        withPrice = response.body().getPrice();

        assertThat(response.isSuccessful(), CoreMatchers.is(true));

    }


    @Test
    void getProductIdTest() throws IOException {
        Response<Product> response = productService.getProductById(id).execute();

        assertThat(response.isSuccessful(), CoreMatchers.is(true));

        assertThat(response.body().getId(), equalTo(id));
        assertThat(response.body().getTitle(), equalTo(withTitle));
        assertThat(response.body().getCategoryTitle(), equalTo(withCategoryTitle));
        assertThat(response.body().getPrice(), equalTo(withPrice));

    }



    @SneakyThrows
    @AfterEach
    void tearDown() {
        SqlSession session=null;
        String resource="mybatis-config.xml";
        InputStream inputStream= Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory=new SqlSessionFactoryBuilder().build(inputStream);
        session=sqlSessionFactory.openSession();
        db.dao.ProductsMapper productsMapper=session.getMapper(db.dao.ProductsMapper.class);
        long myLong = id;
        productsMapper.deleteByPrimaryKey(myLong);
        session.commit();
        session.close();
    }



}