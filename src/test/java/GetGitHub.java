
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.hamcrest.CoreMatchers.equalTo;

import static io.restassured.RestAssured.given;

public class GetGitHub {

    private final Logger log = LogManager.getLogger(this.getClass().getName());
    RequestSpecification requestSpec;
    ResponseSpecification responseSpec;

    /**
     * Change user name to get back repos of particular username
     */
    String userName = "faran";

    /**
     * Change created before date to get back repos created before particular date
     */
    String createdBefore = "<2011-01-01";

    @DataProvider(name = "ProgLang")
    public static Object[][] langs() {
        return new Object[][]{
                {"java"},
                {"c++"},
                {"javascript"}
        };
    }

    public static JsonPath rawToJson(Response r) {
        String response = r.asString();
        return new JsonPath(response);
    }

    @BeforeClass
    public void requestSpecs() {
        requestSpec = new RequestSpecBuilder()
                .setBaseUri("https://api.github.com")
                .build();
    }

    @BeforeClass
    public void responseSpecs() {
        responseSpec = new ResponseSpecBuilder()
                .expectStatusCode(200)
                .expectContentType(ContentType.JSON)
                .build();
    }

    /**
     * First Test : Search for User Name
     * updated repo first
     * asserting username
     * If you want to see complete json response please enable line 79 and 80
     */
    @Test
    public void SearchReposForUserName() {
        Response response =
                given().
                    spec(requestSpec).log().all().
                        param("q", "user:" + userName).
                        param("sort", "updated").
                        param("order", "desc").
                    when().
                        get("/search/repositories").
                    then().
                        spec(responseSpec).
                        assertThat().body("items[0].owner.login", equalTo(userName)).
                    extract().response();

        log.info("Total number of repos : " + response.jsonPath().get("total_count") + " for user name: " + userName);
        //JsonPath jp = rawToJson(response);
        //log.info(jp.prettify());

    }

    /**
     * Second Test : Search for repo which has forks
     * greater than 10000
     * sorted by most forks appearing first
     * If you want to see complete json response please enable line 104 and 105
     */
    @Test
    public void SearchReposWithMostForks() {
        Response response =
                given().
                    spec(requestSpec).log().all().
                        param("q", "forks:>=10000").
                        param("s", "forks").
                        param("order", "desc").
                    when().
                        get("/search/repositories").
                    then().
                        spec(responseSpec).
                    extract().response();

        log.info("Total number of repos : " + response.jsonPath().get("total_count") + " with forks greater than 10000: ");
        //JsonPath jp = rawToJson(response);
        //log.info(jp.prettify());
    }

    /**
     * Third Test : Search for repo with specific language
     * It search for three languages , data coming from data providers
     * display updated one first
     * If you want to see complete json response please enable line 128 and 129
     */
    @Test(dataProvider = "ProgLang")
    public void GitHubReposByLanguage(String pl) {
        Response response =
                given().
                    spec(requestSpec).log().all().
                        param("q", "selenium+language:" + pl).
                        param("s", "updated").
                        param("order", "desc").
                    when().
                        get("/search/repositories").
                    then().
                        spec(responseSpec).
                    extract().response();

        log.info("Total number of repos: " + response.jsonPath().get("total_count") + " for language: " + pl);
        //JsonPath jp = rawToJson(response);
        //log.info(jp.prettify());
    }

    /**
     * Third Test (second part) : Search for repo with specific language
     * with created date
     * It search for three languages , data coming from data providers
     * display updated one first
     * If you want to see complete json response please enable line 153 and 154
     */
    @Test(dataProvider = "ProgLang")
    public void GitHubReposLanguageCreateBy(String pl) {
        Response response =
                given().
                    spec(requestSpec).log().all().
                        param("q", "selenium+language:" + pl + " created:" + createdBefore).
                        param("s", "updated").
                        param("order", "desc").
                    when().
                        get("/search/repositories").
                    then().
                        spec(responseSpec).
                    extract().response();

        log.info("Total number of repos: " + response.jsonPath().get("total_count") + " for language: " + pl + " created before: " + createdBefore);
        //JsonPath jp = rawToJson(response);
        //log.info(jp.prettify());
    }

    /**
     * Fourth Test : Search for repo which has starts
     * greater than 2000
     * sorted by most forks appearing first
     * If you want to see complete json response please enable line 178 and 179
     */
    @Test
    public void GitHubMostStarRepos() {
        Response response =
                given().
                    spec(requestSpec).log().all().
                        param("q", "stars:>=2000").
                        param("s", "stars").
                        param("o", "desc").
                    when().
                        get("/search/repositories").
                    then().
                        spec(responseSpec).
                    extract().response();

        JsonPath jp = rawToJson(response);
        log.info("Most Starred repositories : Star count " + response.jsonPath().getInt("items[0].stargazers_count"));
        //JsonPath jp = rawToJson(response);
        //log.info(jp.prettify());
    }

    /**
     * Fifth Test : Search for repos which has apache license 2.0
     * sorted by updated repo first
     * If you want to see complete json response please enable line 202 and 203
     */
    @Test
    public void GitHubApacheLicenseRepos() {
        Response response =
                given().
                    spec(requestSpec).log().all().
                        param("q", "license:apache-2.0").
                        param("s", "updated").
                        param("o", "desc").
                    when().
                        get("/search/repositories").
                    then().
                        spec(responseSpec).
                    extract().response();


        log.info("Total number of repos which are licensed under Apache-2.0 " + response.jsonPath().get("total_count"));
        //JsonPath jp = rawToJson(response);
        //log.info(jp.prettify());
    }
}
