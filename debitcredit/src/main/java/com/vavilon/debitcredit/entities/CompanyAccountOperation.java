package com.vavilon.debitcredit.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
@Table(name = "company_account_operation")
@Getter
@Setter
@NoArgsConstructor
public class CompanyAccountOperation implements Comparable<CompanyAccountOperation> {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "company_id")
    private Company company;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "document_id")
    @NotNull(message = "Необходимо выбрать документ")
    private Document document;

    @Column(name = "document_date")
    @Temporal(TemporalType.DATE)
    @NotNull(message = "Укажите дату начала действия документа")
    @DateTimeFormat(pattern = "dd.MM.yyyy")
    private Date documentDate;

    @Length(max = 128, message = "Максимальная длина комментария не должна превышать 128 символов")
    private String comment;

    @Column(name = "document_number")
    private String documentNumber;

    @Column(name = "account_operation")
    @NotNull(message = "Укажите сумму списания (начисления)")
    @PositiveOrZero(message = "Сумма списания (начисления) должны быть положительной")
    private BigDecimal accountOperation = new BigDecimal(0);

    @Column(name = "current_balance")
    private BigDecimal currentBalance = new BigDecimal(0);

    @Column(name = "is_debit")
    @NotNull(message = "Выберите дебет или кредит")
    private Boolean isDebit;

    public CompanyAccountOperation(Document document, Date documentDate, String comment,
                                   String documentNumber, Boolean isDebit,
                                   BigDecimal accountOperation) {
        this.document = document;
        this.documentDate = documentDate;
        this.comment = comment;
        this.documentNumber = documentNumber;
        this.isDebit = isDebit;
        this.accountOperation = accountOperation;
    }

    public String getDocumentName() {
        StringBuilder documentName = new StringBuilder(document.getDocumentName());
        if (documentNumber != null && documentNumber.length() != 0) {
            documentName.append(String.format(" №%s", documentNumber));
        }
        if (comment != null && comment.length() != 0) {
            documentName.append(String.format("(%s)", comment));
        }
        documentName.append(String.format(" от %s", this.getDocumentDateString()));
        return documentName.toString();
    }

    public String getDocumentDateString() {
        if (this.documentDate == null) return "";

        String pattern = "dd.MM.yyyy";
        DateFormat df = new SimpleDateFormat(pattern);
        return df.format(this.documentDate);
    }

    @Override
    public int compareTo(CompanyAccountOperation o) {
        if (this.documentDate.after(o.getDocumentDate())) {
            return -1;
        } else if (this.documentDate.equals(o.getDocumentDate())) {
            if (this.id > o.getId()) return -1;
            else if (this.id.equals(o.getId())) return 0;
            else return 1;
        } else {
            return 1;
        }
    }
}
