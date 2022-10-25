package com.vavilon.debitcredit.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Company implements Comparable<Company>{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(length = 128)
    @Length(min = 1, max = 128, message = "Длина должна быть больше 0, но меньше 129 символов")
    @NotNull(message = "Имя компании не может быть пустым")
    private String name;

    @Column(length = 6)
    @Length(max = 10, message = "ИНН - 10 знаков для юридических лиц")
    @NotNull(message = "Введите ИНН компании")
    private String inn;

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Set<CompanyAccountOperation> companyAccountOperationSet = new HashSet<>();

    @Override
    public int compareTo(Company o) {
        if (this.id > o.getId()) return 1;
        else if (this.id.equals(o.getId())) return 0;
        else return -1;
    }
}
