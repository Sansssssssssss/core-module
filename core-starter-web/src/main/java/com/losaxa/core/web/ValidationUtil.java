package com.losaxa.core.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

import javax.validation.Validator;

/**
 * 验证工具
 */
@Component
public class ValidationUtil {

    private static SpringValidatorAdapter springValidator;

    @Autowired
    public void setValidator(Validator validator) {
        ValidationUtil.springValidator = new SpringValidatorAdapter(validator);
    }

    public static <T> BindingResult validate(T target, Class<?>... groups) {
        BeanPropertyBindingResult errors = new BeanPropertyBindingResult(target, target.getClass().getName());
        springValidator.validate(target, errors, groups);
        return errors;
    }

    public static <T> void validateAndThrow(T target, Class<?>... groups) throws BindException {
        BindingResult errors = validate(target, groups);
        if (errors.hasErrors()) {
            throw new BindException(errors);
        }
    }
}
