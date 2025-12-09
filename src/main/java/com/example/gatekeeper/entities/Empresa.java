package com.example.gatekeeper.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Pattern;
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

    @Column(name = "nit", unique = true, nullable = false)
    @Digits(integer = 10, fraction = 0, message = "Maximo solo 10 digitos")
    private Long nit;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "area")
    @Pattern(regexp = "^[a-zA-Z0-9áéíóúÁÉÍÓÚñÑ ]+$", message = "No se permiten simbolos")
    private String area;

    @Column(name = "contacto")
    @Pattern(regexp = "^\\d{7,10}$", message = "El contacto debe tener entre 7 y 10 dígitos y contener solo números.")
    private String contacto;

    @Column(name = "ubicacion")
    @Pattern(regexp = "^[a-zA-Z0-9áéíóúÁÉÍÓÚñÑ ]+$", message = "No se permiten simbolos")
    private String ubicacion;

    @Column(name = "estado")
    private Boolean estado = true;
    /* @Column(name = "correo")
    private String correo; */
    
    
}
