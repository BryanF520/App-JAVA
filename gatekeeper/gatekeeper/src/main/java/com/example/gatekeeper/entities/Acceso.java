package com.example.gatekeeper.entities;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

@Table(name = "accesos")

public class Acceso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    
    @ManyToOne
    @JoinColumn(name = "persona_id", referencedColumnName = "id")
    @Valid
    private Persona persona;

    @Column(name = "motivo")
    private String motivo;

    @Column(name = "fecha_ingreso")
    private LocalDateTime fecha_ingreso;

    @ManyToOne
    @JoinColumn(name = "empresa_id", referencedColumnName = "id")
    private Empresa empresa;

    @Column(name = "estado")
    private Boolean estado =  true;
}
