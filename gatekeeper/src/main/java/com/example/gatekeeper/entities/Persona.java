package com.example.gatekeeper.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.example.gatekeeper.entities.Tipo_doc;
import com.example.gatekeeper.entities.Rol;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "personas")


public class Persona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "tipo_doc", referencedColumnName = "id")
    private Tipo_doc tipoDoc;

    @Column(name = "num_doc", unique = true, nullable = false)
    private String numDoc;

    @Column(name = "nombre_uno", nullable = false)
    private String nombreUno;

    @Column(name = "nombre_dos")
    private String nombreDos;

    @Column(name = "apellido_uno", nullable = false)
    private String apellidoUno;

    @Column(name = "apellido_dos")
    private String apellidoDos;

    @Column(name = "telefono", unique = true, nullable = false)
    private String telefono;

    @ManyToOne
    @JoinColumn(name = "rol_id", referencedColumnName = "id")
    private Rol rol;

    @Column(name = "password")
    private String password;

}
