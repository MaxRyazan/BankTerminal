package ru.maxryazan.bankterminal.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "investment")
public class Investment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "date_of_investment")
    private String dateOfInvestment;

    @Column(name = "base_price_of_investment")
    private double basePriceOfInvestment;

    @Column(name = "curr_price_of_investment")
    private double currPriceOfInvestment;

    @Column(name = "margin")
    private double margin;

    @ManyToOne
    @JoinColumn(name = "investment_id")
    private Client investor;

}
