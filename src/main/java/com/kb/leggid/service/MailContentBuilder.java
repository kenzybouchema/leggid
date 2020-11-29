package com.kb.leggid.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@AllArgsConstructor
public class MailContentBuilder {

    private final TemplateEngine templateEngine;

    public String build(String message) {
        Context context = new Context();
        // "messae" le nom de la variable dans le template
        context.setVariable("message", message);
        // "mailTemplate" le nom du template dans :
        // .../main/ressources/templates/
        return templateEngine.process("mailTemplate", context);
    }
}
