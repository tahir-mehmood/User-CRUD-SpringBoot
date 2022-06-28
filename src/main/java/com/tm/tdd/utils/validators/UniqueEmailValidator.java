package com.tm.tdd.utils.validators;

import com.tm.tdd.domain.repository.UserRepository;
import com.tm.tdd.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Component
public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {


    @Autowired
    IUserService userService;

    public UniqueEmailValidator() {
    }

    public UniqueEmailValidator(IUserService userService) {
        this.userService = userService;
    }

    public void setUserService(IUserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        return !userService.existsByEmail(email);
    }
}