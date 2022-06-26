package lesson5;

import com.github.javafaker.Faker;
import lesson5.api.ProductService;
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
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;


public class ModifyProductTest {

    static ProductService productService;
    Product product = null;
    Product modifyProduct = null;
    Faker faker = new Faker();
    int id;

    @BeforeAll
    static void beforeAll() {
        productService = RetrofitUtils.getRetrofit()
                .create(ProductService.class);
    }

    @BeforeEach
    void setUp() {
        product = new Product()
                .withTitle(faker.food().ingredient())
                .withCategoryTitle("Food")
                .withPrice((int) (Math.random() * 10000));

    }

    @Test
    void createProductInFoodCategoryTest() throws IOException {
        Response<Product> response = productService.createProduct(product)
                .execute();
        id =  response.body().getId();
        assertThat(response.isSuccessful(), CoreMatchers.is(true));
    }

    @Test
    void modifyProductInFoodCategoryTest() throws IOException {
        Response<Product> response = productService.createProduct(product)
                .execute();
        id =  response.body().getId();
        assertThat(response.isSuccessful(), CoreMatchers.is(true));

        modifyProduct = new Product()
                .withId(id)
                .withTitle("newFood")
                .withCategoryTitle("Electronic")
                .withPrice(999);

        Response<Product> response2 = productService.modifyProduct(modifyProduct)
                .execute();
        assertThat(response2.isSuccessful(), CoreMatchers.is(true));

        assertThat(response2.body().getId(), equalTo(id));
        assertThat(response2.body().getTitle(), equalTo("newFood"));
        assertThat(response2.body().getPrice(), equalTo(999));
        assertThat(response2.body().getCategoryTitle(), equalTo("Electronic"));
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
        db.model.ProductsExample example = new db.model.ProductsExample();

        example.createCriteria().andIdEqualTo(myLong);
        List<db.model.Products> list = productsMapper.selectByExample(example);

        System.out.println(list.get(0).getTitle());
        assertThat(list.get(0).getTitle(), equalTo("newFood"));
        System.out.println(list.get(0).getPrice());
        assertThat(list.get(0).getPrice(), equalTo(999));
        System.out.println(list.get(0).getCategory_id());
        assertThat(list.get(0).getCategory_id(), equalTo(2L));

        productsMapper.deleteByPrimaryKey(myLong);
        session.commit();
        session.close();
    }



}