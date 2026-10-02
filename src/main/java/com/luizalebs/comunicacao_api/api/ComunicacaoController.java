package com.luizalebs.comunicacao_api.api;

import com.luizalebs.comunicacao_api.api.dto.ComunicacaoInDTO;
import com.luizalebs.comunicacao_api.api.dto.ComunicacaoOutDTO;
import com.luizalebs.comunicacao_api.business.service.ComunicacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.swing.*;
import java.util.List;

@RestController
@RequestMapping("/comunicacao")
@Tag(name = "Comunicação", description = "Manda mensagem a um destinatário")
public class ComunicacaoController {

    private final ComunicacaoService service;

    public ComunicacaoController(ComunicacaoService service) {
        this.service = service;
    }

    @PostMapping("/agendar")
    @Operation(summary = "Agenda mensagem", description = "Agenda uma mensagem com data e horário")
    @ApiResponse(responseCode = "200", description = "Mensagem agendada com sucesso")
    @ApiResponse(responseCode = "400", description = "Mensagem já agendada")
    @ApiResponse(responseCode = "500", description = "Erro de servidor")
    public ResponseEntity<ComunicacaoOutDTO> agendar(@RequestBody ComunicacaoInDTO dto)  {
        return ResponseEntity.ok(service.agendarComunicacao(dto));
    }

    @GetMapping()
    @Operation(summary = "Busca Status da Mensagem pelo Email", description = "Busca o status da mensagem")
    @ApiResponse(responseCode = "200", description = "Status encontrado")
    @ApiResponse(responseCode = "500", description = "Erro no servidor")
    public ResponseEntity<ComunicacaoOutDTO> buscarStatus(@RequestParam String emailDestinatario) {
        return ResponseEntity.ok(service.buscarStatusComunicacao(emailDestinatario));
    }

    @GetMapping("/historico")
    @Operation(
            summary = "Consulta o histórico",
            description = "Consulta as comunicações realizadas"
    )
    public ResponseEntity<List<ComunicacaoOutDTO>> historico(
            @RequestParam(required = false)
            String emailDestinatario) {

        return ResponseEntity.ok(
                service.buscarHistorico(emailDestinatario)
        );
    }


    @PatchMapping("/cancelar")
    @Operation(summary = "Cancela o status por Email", description = "Cancela  o status da mensagem")
    @ApiResponse(responseCode = "200", description = "Cancelamento do status ok")
    @ApiResponse(responseCode = "500", description = "Erro no servidor")
    public ResponseEntity<ComunicacaoOutDTO> cancelarStatus(@RequestParam String emailDestinatario) {
        return ResponseEntity.ok(service.alterarStatusComunicacao(emailDestinatario));
    }
}
