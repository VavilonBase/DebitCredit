package com.vavilon.debitcredit.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "document_name", length = 128)
    @Length(min = 1, max = 128, message = "Длина должна быть больше 0, но меньше 129 символов")
    @NotNull(message = "Имя документа не может быть пустым")
    private String documentName;
}
