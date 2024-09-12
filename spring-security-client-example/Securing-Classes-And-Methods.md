# Securing Classes and Methods

There are many ways to secure classes and method.  Here are a couple options to get you started.  Google "spring security" to find other ways.

# Security Configuration Class

In the Spring Security client code (`src/java/com/galvanize/security`), the class `SecurityCredentialsConfig` handles the configuration of security. The method `configure(HttpSecurity http)` is of particular interest. This method sets up the secure environment, initiating with `.cors().add()`, which adds CORS processing, and `.csrf().disable()`, disabling cross-site request filtering, which is beyond the scope of this document. For more information on this, refer to [A Guide to CSRF Protection in Spring Security](https://www.baeldung.com/spring-security-csrf).

The subsequent method call, `.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)`, designates our application as stateless, a standard for a RESTful API. Following this, we handle errors for unauthorized requests.

Authentication and authorization occur through the addition of filters. This is accomplished with `.addFilterBefore(..)`, which incorporates the `JwtTokenAuthenticationFilter` to process requests that include a Java Web Token (JWT).

The method call `.authorizeRequests()` requires that all HTTP requests, at a minimum, be authenticated. Exceptions to this rule are declared immediately after this call. In this case, we use a matching method, `antMatchers`, an `HttpMethod` type (`GET`, `POST`, etc.), a URI path, and a security type. The class contains commented examples that can be employed in your application. Here are a few simple explanations:

- `.antMatchers(HttpMethod.GET, "/actuator/health").permitAll()`

    This permits unauthorized requests of type `GET` to the URI `/actuator/health`.

- `.antMatchers(HttpMethod.POST, "/api/account/register/**").permitAll()`

    This permits any `POST` request that starts with the URI `/api/account/register`. However, this permission is not recursive. As a result, `/api/account/register/update` will not be allowed unless there is another permission entry for that endpoint.

- `.antMatchers(HttpMethod.PUT, "/api/admin/**").hasRole("ADMIN")`

    The addition of `.hasRole("ROLE")` necessitates that the authenticated user possesses the `ADMIN` role to access the endpoint.

# Method Based Security 

Method-level security is a way of securing specific methods within your application rather than securing URL patterns or whole endpoints. In the Spring Security framework, you can achieve method-level security using annotations such as `@PreAuthorize`, `@PostAuthorize`, `@Secured`, etc.

`@PreAuthorize` is a particularly powerful annotation that allows access-control expressions to be written directly in the code that secures individual methods. It is evaluated before the method is invoked, hence the name "PreAuthorize". These expressions can leverage methods available in the security context, for example, to refer to the current user's roles or details.

Here's how it works:

1. First, you need to enable global method security in your configuration file using `@EnableGlobalMethodSecurity(prePostEnabled = true)`. This tells Spring Security to look for `@PreAuthorize` and `@PostAuthorize` annotations on methods and enforce the security constraints they define.

2. Now, in your service or controller classes, you can annotate methods with `@PreAuthorize`. Inside the annotation, you specify an expression that decides whether the method can be accessed or not.

Here are a few examples:

- `@PreAuthorize("isAuthenticated()")`: This means the method can only be accessed if the user is authenticated.  Note, our configuration already requires this, so it is redundant. 

- `@PreAuthorize("hasRole('ROLE_ADMIN')")`: This allows only users with the `ROLE_ADMIN` to access the method.

- `@PreAuthorize("#username == principal.username")`: In this example, the `#username` is a method parameter, and `principal.username` refers to the username of the currently authenticated user. This ensures that the currently authenticated user's username matches the username parameter passed to the method.

Remember that `@PreAuthorize` is applied before the method is executed. If the security condition is not met, the method will not be invoked, and an access denied error will be returned.

Also, note that you should be cautious when using method security, as it can lead to a cluttered codebase if overused. Typically, it's used for fine-grained access control where URL-based security is not sufficient.

