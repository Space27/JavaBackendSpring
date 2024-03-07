package edu.java.bot.controller.validator;

import edu.java.bot.util.LinksUtil;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.net.URI;

public class IsURIValidator implements ConstraintValidator<IsURI, URI> {

    @Override
    public boolean isValid(URI uri, ConstraintValidatorContext constraintValidatorContext) {
        return uri == null || LinksUtil.checkURI(uri);
    }
}
