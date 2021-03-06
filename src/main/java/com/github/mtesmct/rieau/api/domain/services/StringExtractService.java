package com.github.mtesmct.rieau.api.domain.services;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.github.mtesmct.rieau.api.domain.entities.Entity;

@DomainService
public class StringExtractService {
    public Optional<String> extract(String regexp, String text, int indexGroup) throws PatternSyntaxException {
        Optional<String> code = Optional.empty();
        Pattern pattern = Pattern.compile(regexp);
        Matcher matcher = pattern.matcher(text);
        if (matcher.find())
            code = Optional.ofNullable(matcher.group(indexGroup));
        return code;
    }

    public Optional<String> entityExtract(String entity, String text) throws PatternSyntaxException {
        return this.extract(entityRegexp(entity), text, 1);
    }

    public Optional<String> attributeExtract(String attribute, String text) throws PatternSyntaxException {
        return this.extract(attributeRegexp(attribute), text, 1);
    }

    public String entityRegexp(String entity) {
        return "\\b" + entity + "\\b" + Entity.REGEXP;
    }

    public String attributeRegexp(String attribute) {
        return "\\b" + attribute + "\\b" + Entity.ATTRIBUTE_REGEXP;
    }
}