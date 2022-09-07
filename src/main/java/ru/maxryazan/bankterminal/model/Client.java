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
    private String lastName;
    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "hash_pin")
    private String pinCode;

    @Column(name = "balance")
    private double balance;

    @OneToMany(mappedBy = "borrower", fetch = FetchType.LAZY)
    List<Credit> credits;

    @OneToMany(mappedBy = "recipient", fetch = FetchType.LAZY)
    List<Transaction> incoming;

    @OneToMany(mappedBy = "sender", fetch = FetchType.LAZY)
    List<Transaction> outcoming;

    @Override
    public String toString() {
        return firstName + " "  + lastName + " " + phoneNumber;

    }
}
