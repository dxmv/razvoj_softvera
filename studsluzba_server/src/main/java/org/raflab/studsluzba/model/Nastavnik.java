package org.raflab.studsluzba.model;

import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
@Data
public class Nastavnik {
	 
	 @Id
	 @GeneratedValue(strategy=GenerationType.IDENTITY)
	 private Long id;
	 private String ime;
	 private String prezime;
	 private String srednjeIme;
	 private String email;
	 private String brojTelefona;
	 private String adresa;

	 @OneToMany(mappedBy = "nastavnik")
	 private Set<NastavnikZvanje> zvanja;
	 
	 private LocalDate datumRodjenja;
	 private Character pol;
	 private String jmbg;

}
