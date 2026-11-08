package reading_tracker.web;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import jakarta.validation.Validation;

class RegistrationFormTest {

    @Test
    // Checks that valid username, password, and email values pass bean validation.
    void validRegistrationDetailsPassValidation() {
        RegistrationForm form = registrationForm("reader_1", "secure-pass", "reader@example.com");

        try (var validatorFactory = Validation.buildDefaultValidatorFactory()) {
            assertTrue(validatorFactory.getValidator().validate(form).isEmpty());
        }
    }

    @Test
    // Checks that an invalid username, short password, and malformed email are rejected.
    void invalidUsernamePasswordAndEmailAreRejected() {
        RegistrationForm form = registrationForm("ab!", "short", "not-an-email");

        try (var validatorFactory = Validation.buildDefaultValidatorFactory()) {
            var violations = validatorFactory.getValidator().validate(form);

            assertFalse(violations.isEmpty());
            assertTrue(violations.stream().anyMatch(violation ->
                    violation.getPropertyPath().toString().equals("username")));
            assertTrue(violations.stream().anyMatch(violation ->
                    violation.getPropertyPath().toString().equals("password")));
            assertTrue(violations.stream().anyMatch(violation ->
                    violation.getPropertyPath().toString().equals("email")));
        }
    }

    private RegistrationForm registrationForm(String username, String password, String email) {
        RegistrationForm form = new RegistrationForm();
        form.setUsername(username);
        form.setPassword(password);
        form.setEmail(email);
        return form;
    }
}
