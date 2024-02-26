package edu.java.bot.service.api.validators;

import edu.java.bot.util.Link;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.net.URI;

public class IsURIValidator implements ConstraintValidator<IsURI, URI> {

    @Override
    public boolean isValid(URI uri, ConstraintValidatorContext constraintValidatorContext) {
        return uri == null || Link.checkURI(uri);
    }
}
