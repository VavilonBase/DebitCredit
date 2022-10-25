package com.vavilon.debitcredit.configs;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class MyCompanyProperties {
    @Value("${mycompany.name}")
    private String myCompanyName;

    @Value("${mycompany.INN}")
    private String myCompanyINN;

    @Value("${mycompany.director.lastName}")
    private String directorLastName;

    @Value("${mycompany.director.firstName}")
    private String directorFirstName;

    @Value("${mycompany.director.middleName}")
    private String directorMiddleName;

    public String getDirectorFullName() {
        return String.format("%s %s %s",
                directorLastName,
                directorFirstName,
                directorMiddleName);
    }
}
