package edu.java.scrapper.controller.validator;

import edu.java.scrapper.service.linkUpdateService.clientUpdate.ClientUpdater;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.net.URI;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class SupportedValidator implements ConstraintValidator<SupportedLink, URI> {

    private final ClientUpdater clientUpdater;

    @Override
    public boolean isValid(URI uri, ConstraintValidatorContext constraintValidatorContext) {
        return uri == null || clientUpdater.supports(uri);
    }
}
