package ru.maxryazan.bankterminal.model;



import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "clients")
public class Client{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    String lastName;
    @Column(name = "phone_number")
    String phoneNumber;
    @Column(name = "hash_pin")
    String pinCode;
    @Column(name = "balance")
    double balance;
    @OneToMany(mappedBy = "borrower", fetch = FetchType.LAZY)
    List<Credit> credits;
    @OneToMany(mappedBy = "contributor", fetch = FetchType.LAZY)
    List<Contribution> contributions;
    @OneToMany(mappedBy = "investor", fetch = FetchType.LAZY)
    List<Investment> investments;
    @OneToMany(mappedBy = "recipient", fetch = FetchType.LAZY)
    List<Transaction> incoming;
    @OneToMany(mappedBy = "sender", fetch = FetchType.LAZY)
    List<Transaction> outcoming;
}
