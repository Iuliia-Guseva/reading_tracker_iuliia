package reading_tracker.web;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RegistrationForm {

    @NotBlank(message = "{signup.username.required}")
    @Size(min = 3, max = 50, message = "{signup.username.size}")
    @Pattern(regexp = "^[A-Za-z0-9._-]+$", message = "{signup.username.pattern}")
    private String username;

    @NotBlank(message = "{signup.password.required}")
    @Size(min = 8, max = 72, message = "{signup.password.size}")
    private String password;

    @Email(message = "{signup.email.invalid}")
    private String email;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
