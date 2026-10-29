package com.luizalebs.comunicacao_api.infraestructure.client.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.luizalebs.comunicacao_api.infraestructure.enums.StatusEnvioEnum;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificacaoEmailDTO {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private String nomeTarefa;
    private String descricao;
    private String nomeDestinatario;
    private LocalDateTime dataEvento;
    private StatusEnvioEnum statusEnvio;
    private String mensagem;
    private String emailUsuario;

}
