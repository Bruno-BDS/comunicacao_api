package com.luizalebs.comunicacao_api.infraestructure.repositories;

import com.luizalebs.comunicacao_api.infraestructure.entities.ComunicacaoEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ComunicacaoRepository extends CrudRepository<ComunicacaoEntity, Long> {

    ComunicacaoEntity findByEmailDestinatario(String nomeDestinatario);

    List<ComunicacaoEntity>
    findAllByEmailDestinatarioOrderByDataHoraenvioDesc(
            String emailDestinatario
    );

    List<ComunicacaoEntity>
    findAllByOrderByDataHoraenvioDesc();
}
