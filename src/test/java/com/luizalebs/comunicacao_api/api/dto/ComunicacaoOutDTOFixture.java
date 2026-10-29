package com.luizalebs.comunicacao_api.api.dto;

import com.luizalebs.comunicacao_api.infraestructure.enums.ModoEnvioEnum;
import com.luizalebs.comunicacao_api.infraestructure.enums.StatusEnvioEnum;

import java.time.LocalDateTime;

public class ComunicacaoOutDTOFixture {
    public static ComunicacaoOutDTO build(
            LocalDateTime dataHoraEnvio,
            String nomeTarefa,
            String nomeDestinatario,
            String emailDestinatario,
            String telefoneDestinatario,
            String mensagem,
            ModoEnvioEnum modoDeEnvio,
            StatusEnvioEnum statusEnvio
            ){
        return new ComunicacaoOutDTO(dataHoraEnvio,
                nomeTarefa,
                nomeDestinatario,
                emailDestinatario,
                telefoneDestinatario,
                mensagem,
                modoDeEnvio,
                statusEnvio);
    }
}
