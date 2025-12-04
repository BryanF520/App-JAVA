package com.example.gatekeeper.service.Impl;

import com.example.gatekeeper.entities.Tipo_doc;
import com.example.gatekeeper.repositories.Tipo_docRepository;
import com.example.gatekeeper.service.Tipo_docService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class Tipo_docServiceImpl implements Tipo_docService {

    @Autowired
    private Tipo_docRepository tipoDocRepository;

    @Override
    public List<Tipo_doc> listarTipo_doc(){
        return tipoDocRepository.findAll();
    }

    @Override
    public Tipo_doc crearTipo_doc(Tipo_doc tipo_doc){
        return tipoDocRepository.save(tipo_doc);
    }

    @Override
    public Tipo_doc obtenerTipo_doc(Long id){
        return tipoDocRepository.findById(id).orElse(null);
    }

    @Override
    public Tipo_doc actualizarTipo_doc(Long id, Tipo_doc nuevosDatos){
        Optional<Tipo_doc> optionalTipoDoc = tipoDocRepository.findById(id);
        if (optionalTipoDoc.isPresent()){
            Tipo_doc tipoDoc = optionalTipoDoc.get();

            tipoDoc.setTipo_doc(nuevosDatos.getTipo_doc());

            return tipoDocRepository.save(tipoDoc);
        }

        return null;
    }

    @Override
    public void desactivarTipo_doc(Long id){
        Optional<Tipo_doc> optionalTipoDoc = tipoDocRepository.findById(id);
        optionalTipoDoc.ifPresent(tipoDocRepository::delete);
    }
}
