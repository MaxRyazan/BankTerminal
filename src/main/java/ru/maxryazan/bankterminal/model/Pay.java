package ru.maxryazan.bankterminal.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "pay")
public class Pay {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "date")
    private String date;

    @Column(name = "sum")
    private double sum;

    @ManyToOne
    @JoinColumn(name = "credit_id")
    private Credit credit;


    @Override
    public String toString() {
        return
                "Платёж по договору: " + credit.getNumberOfCreditContract() + " от: " + date + " на сумму: " + sum + " рублей.";
    }
}
