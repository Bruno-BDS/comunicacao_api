package com.luizalebs.comunicacao_api.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.luizalebs.comunicacao_api.api.dto.ComunicacaoInDTO;
import com.luizalebs.comunicacao_api.api.dto.ComunicacaoInDTOFixture;
import com.luizalebs.comunicacao_api.api.dto.ComunicacaoOutDTO;
import com.luizalebs.comunicacao_api.api.dto.ComunicacaoOutDTOFixture;
import com.luizalebs.comunicacao_api.business.service.ComunicacaoService;
import com.luizalebs.comunicacao_api.infraestructure.enums.ModoEnvioEnum;
import com.luizalebs.comunicacao_api.infraestructure.enums.StatusEnvioEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ComunicacaoControllerTest {
    @InjectMocks
    private ComunicacaoController controller;

    @Mock
    private ComunicacaoService comunicacaoService;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private String url;
    private String json;

    private ComunicacaoInDTO comunicacaoInDTO;
    private ComunicacaoOutDTO comunicacaoOutDTO;

    @BeforeEach
    void setUp() throws JsonProcessingException {
        LocalDateTime dataHoraEnvio = LocalDateTime.of(2026, 10, 23, 17, 0, 0);

        comunicacaoInDTO = ComunicacaoInDTOFixture.build(
                dataHoraEnvio,
                "Tarefa Teste",
                "Bruno",
                "email@teste.com",
                "489648421531",
                "Mensagem Teste",
                ModoEnvioEnum.EMAIL,
                StatusEnvioEnum.PENDENTE
        );
        comunicacaoOutDTO = ComunicacaoOutDTOFixture.build(
                dataHoraEnvio,
                "Tarefa Teste",
                "Bruno",
                "email@teste.com",
                "489648421531",
                "Mensagem Teste",
                ModoEnvioEnum.EMAIL,
                StatusEnvioEnum.PENDENTE
        );

        mockMvc = MockMvcBuilders.standaloneSetup(controller).alwaysDo(print()).build();
        objectMapper.findAndRegisterModules();
        json =  objectMapper.writeValueAsString(comunicacaoInDTO);
        url = "/comunicacao";
    }

    @Test
    void deveAgendarComunicacao() throws Exception {
        comunicacaoOutDTO.setStatusEnvio(StatusEnvioEnum.ENVIADO);
        when(comunicacaoService.agendarComunicacao(isA(ComunicacaoInDTO.class))).thenReturn(comunicacaoOutDTO);
        mockMvc.perform(post(url + "/agendar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                comunicacaoInDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nomeTarefa")
                        .value("Tarefa Teste"))
                .andExpect(jsonPath("$.emailDestinatario")
                        .value("email@teste.com"))
                .andExpect(jsonPath("$.statusEnvio")
                        .value("ENVIADO"));

        verify(comunicacaoService)
                .agendarComunicacao(isA(ComunicacaoInDTO.class));
    }

    @Test
    void deveBuscarStatusComunicacao() throws Exception {
        String email = "email@teste.com";
        comunicacaoOutDTO.setStatusEnvio(StatusEnvioEnum.ENVIADO);
        when(comunicacaoService.buscarStatusComunicacao(email)).thenReturn(comunicacaoOutDTO);

        mockMvc.perform(get(url)
                        .param("emailDestinatario", email))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.emailDestinatario")
                        .value(email))
                .andExpect(jsonPath("$.statusEnvio")
                        .value("ENVIADO"));

        verify(comunicacaoService)
                .buscarStatusComunicacao(email);
    }

    @Test
    void deveBuscarhistorico() throws Exception {

        String email = "email@teste.com";
        when(comunicacaoService.buscarHistorico(email)).thenReturn(Collections.singletonList(comunicacaoOutDTO));

        mockMvc.perform(get(url + "/historico")
                        .param("emailDestinatario", email))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].emailDestinatario")
                        .value(email));


        verify(comunicacaoService).buscarHistorico(email);
    }

    @Test
    void cancelarStatus() throws Exception {
        String email = "email@teste.com";

        comunicacaoOutDTO.setStatusEnvio(
                StatusEnvioEnum.CANCELADO);

        when(comunicacaoService.alterarStatusComunicacao(email))
                .thenReturn(comunicacaoOutDTO);

        mockMvc.perform(patch(url + "/cancelar")
                        .param("emailDestinatario", email))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.emailDestinatario")
                        .value(email))
                .andExpect(jsonPath("$.statusEnvio")
                        .value("CANCELADO"));

        verify(comunicacaoService)
                .alterarStatusComunicacao(email);
    }
}