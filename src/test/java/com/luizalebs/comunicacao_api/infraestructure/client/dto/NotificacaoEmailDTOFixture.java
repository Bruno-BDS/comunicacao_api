package com.luizalebs.comunicacao_api.infraestructure.client.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.luizalebs.comunicacao_api.infraestructure.enums.StatusEnvioEnum;

import java.time.LocalDateTime;

public class NotificacaoEmailDTOFixture {
    public static NotificacaoEmailDTO build(

            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
            String nomeTarefa,
            String descricao,
            String nomeDestinatario,
            LocalDateTime dataEvento,
            StatusEnvioEnum statusEnvio,
            String mensagem,
            String emailUsuario) {
        return new NotificacaoEmailDTO(nomeTarefa,
                descricao,
                nomeDestinatario,
                dataEvento,
                statusEnvio,
                mensagem,
                emailUsuario);
    }
}
