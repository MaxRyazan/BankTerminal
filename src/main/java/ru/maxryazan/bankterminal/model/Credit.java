package ru.maxryazan.bankterminal.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "credits")
public class Credit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "credit_sum")
    private int sumOfCredit;

    @Column(name = "credit_percent")
    private double creditPercent;

    @Column(name = "sum_with_percents")
    private double sumWithPercents;

    @Column(name = "monthly_payment")
    private double everyMonthPay;

    @Column(name = "rest_of_credit")
    private double restOfCredit;

    @ManyToOne
    @JoinColumn(name = "borrower_id")
    private Client borrower;

}
