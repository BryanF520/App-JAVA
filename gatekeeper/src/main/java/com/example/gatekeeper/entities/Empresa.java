package com.example.gatekeeper.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "empresas")

public class Empresa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nit")
    private int nit;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "area")
    private String area;

    @Column(name = "contacto")
    private String contacto;

    @Column(name = "ubicacion")
    private String ubicacion;
}
