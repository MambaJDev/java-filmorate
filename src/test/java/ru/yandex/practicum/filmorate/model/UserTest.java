package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserTest {
    private Validator validator;
    private final User user = User.builder()
            .id(1)
            .email("king@mail.ru")
            .login("King")
            .name("Dmitry")
            .birthday("1996-11-02")
            .build();

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void passValidatedUser() {
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertEquals(0, violations.size(), "User не прошел валидацию");
    }

    @Test
    void failValidateUserWithEmptyEmail() {
        user.setEmail("");
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertEquals(1, violations.size(), "Валидация не работает");
    }
    @Test
    void failValidateUserWhenEmailIsNull() {
        user.setEmail(null);
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertEquals(1, violations.size(), "Валидация не работает");
    }

    @Test
    void failValidateUserIfWrongEmail() {
        user.setEmail("king.mail.ru");
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertEquals(1, violations.size(), "Валидация не работает");
    }

    @Test
    void failValidateUserLoginIsEmpty() {
        user.setLogin(" ");
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertEquals(1, violations.size(), "Валидация не работает");
    }

    @Test
    void failValidateUserLoginIsNull() {
        user.setLogin(null);
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertEquals(1, violations.size(), "Валидация не работает");
    }

    @Test
    void passValidateWhenUserNameIsEmpty() {
        user.setName("");
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertEquals(0, violations.size(), "Валидация не работает");
    }

    @Test
    void passValidateWhenUserNameIsNull() {
        user.setName(null);
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertEquals(0, violations.size(), "Валидация не работает");
    }

    @Test
    void failValidateUserBirthdayIsNow() {
        user.setBirthday(LocalDate.now().toString());
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertEquals(1, violations.size(), "Валидация не работает");
    }

    @Test
    void failValidateUserBirthdayAfterNow() {
        user.setBirthday(LocalDate.now().plusDays(1).toString());
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertEquals(1, violations.size(), "Валидация не работает");
    }

    @Test
    void passValidateUserBirthdayBeforeNow() {
        user.setBirthday(LocalDate.now().minusDays(1).toString());
        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertEquals(0, violations.size(), "Валидация не работает");
    }
}