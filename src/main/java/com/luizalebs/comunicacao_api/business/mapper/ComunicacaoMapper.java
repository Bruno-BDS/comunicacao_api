package com.luizalebs.comunicacao_api.business.mapper;

import com.luizalebs.comunicacao_api.api.dto.ComunicacaoInDTO;
import com.luizalebs.comunicacao_api.api.dto.ComunicacaoOutDTO;
import com.luizalebs.comunicacao_api.infraestructure.entities.ComunicacaoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ComunicacaoMapper {

    @Mapping(
            source = "dataHoraEnvio",
            target = "dataHoraenvio"
    )
    @Mapping(
            source = "nomeDestinatario",
            target = "nomeDestinatario"
    )
    ComunicacaoEntity paraComunicacaoEntity(ComunicacaoInDTO dto);

    @Mapping(
            source = "dataHoraenvio",
            target = "dataHoraEnvio"
    )
    ComunicacaoOutDTO paraComunicacaoOutDTO(ComunicacaoEntity entity);

}
