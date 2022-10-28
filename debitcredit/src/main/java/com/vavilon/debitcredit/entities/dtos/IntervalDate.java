package com.vavilon.debitcredit.entities.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Setter
@Getter
@NoArgsConstructor
public class IntervalDate {
    @DateTimeFormat(pattern = "dd.MM.yyyy")
    private Date dateStart;

    @DateTimeFormat(pattern = "dd.MM.yyyy")

    private Date dateEnd;

    public IntervalDate(Date dateStart, Date dateEnd) {
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
    }

    public String getDateStartString() {
        if (this.dateStart == null) return "";

        String pattern = "dd.MM.yyyy";
        DateFormat df = new SimpleDateFormat(pattern);
        return df.format(this.dateStart);
    }

    public String getDateEndString() {
        if (this.dateEnd == null) return "";

        String pattern = "dd.MM.yyyy";
        DateFormat df = new SimpleDateFormat(pattern);
        return df.format(this.dateEnd);
    }
}
