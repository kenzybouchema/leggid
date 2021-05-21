package com.kb.leggid.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

@Service
@AllArgsConstructor
public class MailContentBuilder {

    @Autowired
    private final SpringTemplateEngine templateEngine;

    public String build(String message) {
        Context context = new Context();
        // "message" le nom de la variable dans le template
        context.setVariable("message", message);
        // "mailTemplate" le nom du template dans :
        // .../main/ressources/templates/
        return templateEngine.process("mailTemplate", context);
    }
}
