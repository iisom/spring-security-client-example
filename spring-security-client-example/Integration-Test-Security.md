# Integration Test Security 

Integration tests require that the requries that the test includes the ***Java Web Token*** in the header `Authorization`.  Here is how you can add this to your integration tests.

## Add a utility class to create a dummy `JWT`
- `JWTUtils` class 
    ```java
    import io.jsonwebtoken.Jwts;
    import io.jsonwebtoken.SignatureAlgorithm;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.stereotype.Service;

    import java.util.Arrays;
    import java.util.Date;
    import java.util.List;

    public class JWTUtils {

        public JwtProperties jwtProperties;

        public JWTUtils(JwtProperties jwtProperties) {
            this.jwtProperties = jwtProperties;
        }

        /**
        * returns a token with the role ROLE_USER
        * @return
        */
        public String getToken(){
            return getToken(Arrays.asList("USER"));
        }

        /**
        * Returns a JWT Token with the roles requested.
        * @param roles - a comma separated list of roles to add begining with @code ROLE_
        * @return a JWT Token
        */
        public String getToken(List roles){
            long now = System.currentTimeMillis();

            String token = Jwts.builder()
                    .setHeaderParam("typ", "JWT")
                    .setSubject("User")
                    .claim("first_name", "user")
                    .claim("last_name", "userName")
                    .claim("email", "tiff.lola.c@tlc.com")
                    .claim("guid", 3L)
                    // Convert to list of strings.
                    // This is important because it affects the way we get them back in the Gateway.
                    .claim("authorities", roles)
                    .setIssuedAt(new Date(now))
                    .setExpiration(new Date(now + 86400 * 1000L))  // in milliseconds
                    .signWith(SignatureAlgorithm.HS512, jwtProperties.getSecret().getBytes())
                    .compact();

            return String.format("Bearer %s", token);
        }
    }
    ```
### `JWTUtils` Constructor

The `JWTUtils` requires an instance of the [`JwtProperties`](/src/java/com/galvanize/security/JwtProperties.java) class included in this project.

```java 
JWTUtls jwtUtils = new JWTUtils(new JwtProperties);
```

Note that `JwtProperties` includes defaults for all the properties.  If modifications are required for testing, they can be replaced in the `application-test.properties` file, or whatever your application uses to configure the test.  Prefix modified properties with `security.jwt`, as in `security.jwt.secret="a better secret"`

### `JWTUtils` methods 

A simple `JWT` can be created using the `getToken()` method.  This will create a toke with the `USER` role that can be used for testing.

If one or more specific roles are required, they can be added in the overloaded method, `getToken(List roles)` method as below;

```java 
List roles = new ArrayList();
roles.add("ROLE-ADMIN")
roles.add("ROLE_SOMETHING-ELSE")
String token = jwtUtils(roles);
```

This token can now be used in the header for a test request.

## Using the Dummy token in a test 

Note that the simplified methods like `restTemplate.getForEntity(...)` cannot include a customized header, Therefore, you must use the more detailed methods like `restTemplat.exchange(..)`.

```java 
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations= "classpath:application-test.properties")
class AutosApiApplicationTests {

    @Autowired
    JwtProperties jwtProperties;

    private JWTUtils jwtUtils;

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    AutosRepository autosRepository;

    @BeforeEach
    void setUp() {
        // Instansiate the JWTUtils with the Jwt Properties that was autowired above
        this.jwtUtils = new JWTUtils(jwtProperties);
    }

    @Test
    void getAutos_exists_returnsAutosList() {
        // Create an HttpHeaders instance
        HttpHeaders headers = new HttpHeaders();
        // Add the token to the header with the name "Authorization
        headers.add("Authorization", jwtUtils.getToken());
        
        // This syntax will NOT work
        // ResponseEntity<AutosList> response = restTemplate.getForEntity("/api/autos",         AutosList.class);
        
        ResponseEntity<AutosList> response = restTemplate.exchange("/api/autos", 
                        HttpMethod.GET, new HttpEntity<>(headers), AutosList.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isEmpty()).isFalse();
        for (Automobile auto : response.getBody().getAutomobiles()){
            System.out.println(auto);
    }
}

```