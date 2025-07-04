package com.userdept.system.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAttributeAdvice {
    @Value("${system.name}")
    private String systemName;

    @ModelAttribute("SYSTEM_NAME")
    public String systemName() {
        return systemName;
    }
}
