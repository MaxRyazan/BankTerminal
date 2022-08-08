package ru.maxryazan.bankterminal.model;


import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "contribution")
public class Contribution {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    @Column(name = "sum", nullable = false)
    int sumOfContribution;

    @Column(name = "date_of_begin", nullable = false)
    String dateOfBegin;

    @Column(name = "duration", nullable = false)
    int durationOfContributionInYears;

    @Column(name = "percent", nullable = false)
    private double percentByContribution;

    @Column(name = "sum_with_percent")
    private double sumWithPercent;

    @Column(name = "date_of_end", nullable = false)
    private String dateOfEnd;
    @ManyToOne
    @JoinColumn(name = "contributor_id")
    Client contributor;
}
