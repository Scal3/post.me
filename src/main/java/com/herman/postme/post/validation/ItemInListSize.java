package com.herman.postme.post.validation;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ItemInListSizeValidator.class)
public @interface ItemInListSize {

    String message() default "Invalid size";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    int min() default 0;

    int max() default Integer.MAX_VALUE;
}

class ItemInListSizeValidator implements ConstraintValidator<ItemInListSize, Object> {

    private int min;
    private int max;

    @Override
    public void initialize(ItemInListSize constraintAnnotation) {
        this.min = constraintAnnotation.min();
        this.max = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Null values should be handled by @NotNull or other annotations
        }

        List<String> list;

        if (value instanceof List) {
            list = ((List<String>) value);
        } else {
            throw new IllegalArgumentException("Unsupported type. Only List is supported.");
        }

        return list.stream()
                .noneMatch(item -> item.length() < min || item.length() > max);
    }
}