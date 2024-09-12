## Unit tests

Once you add security to your project, you may find that your unit tests no longer work.  This happens when a secured endpoint is accessed.  In order to get these tests to pass, you must add things to your tests.

In order to test your Spring Security configuration, it's often necessary to assume the identity of a user with specific roles and permissions. The `@WithMockUser` annotation in Spring Security Test is designed to help with this. It allows you to define a fake (or "mock") user for the purpose of a single test method or an entire test class.

`@WithMockUser` sets up a `SecurityContext` for your test and populates it with an `Authentication` token that represents a user with the specified username, password, and authorities (roles).

Here's how to use `@WithMockUser` in a test:

```java
@Test
@WithMockUser(username = "testuser", roles = {"USER"})
public void testWithUser() {
    // your test code here
}
```

In this example, the `testWithUser` method will be run with a `SecurityContext` that has been populated with an `Authentication` token representing a user with the username "testuser" and the role "USER". Within this method, you can perform actions and make assertions as though you were this user.

You can customize the properties of the mock user using the `@WithMockUser` annotation's attributes:

- `username`: the username for the user (defaults to "user").
- `password`: the password for the user (defaults to "password").
- `roles`: a list of roles for the user (each role will be prefixed with "ROLE_").
- `authorities`: a list of authorities for the user.

Note that you should not use `roles` and `authorities` together, as they are intended to be used separately.

One important point to remember is that `@WithMockUser` only sets up a `SecurityContext` with a mock user; it does not load any user details from your `UserDetailsService`. If your method under test needs to access the details of a real user from your user store, you might need to use `@WithUserDetails` instead.

Remember to use `@WithMockUser` judiciously as you are creating a mock user with specific roles or authorities that bypass the normal authentication flow. As such, it may not fully test the authentication process of your application.