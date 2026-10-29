package com.luizalebs.comunicacao_api.business.service;

import com.luizalebs.comunicacao_api.api.dto.ComunicacaoInDTO;
import com.luizalebs.comunicacao_api.api.dto.ComunicacaoInDTOFixture;
import com.luizalebs.comunicacao_api.api.dto.ComunicacaoOutDTO;
import com.luizalebs.comunicacao_api.api.dto.ComunicacaoOutDTOFixture;
import com.luizalebs.comunicacao_api.business.mapper.ComunicacaoMapper;
import com.luizalebs.comunicacao_api.infraestructure.client.NotificacaoClient;
import com.luizalebs.comunicacao_api.infraestructure.client.dto.NotificacaoEmailDTO;
import com.luizalebs.comunicacao_api.infraestructure.entities.ComunicacaoEntity;
import com.luizalebs.comunicacao_api.infraestructure.enums.ModoEnvioEnum;
import com.luizalebs.comunicacao_api.infraestructure.enums.StatusEnvioEnum;
import com.luizalebs.comunicacao_api.infraestructure.exceptions.RecursoNaoEncontradoException;
import com.luizalebs.comunicacao_api.infraestructure.repositories.ComunicacaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ComunicacaoServiceTest {
    @InjectMocks
    private ComunicacaoService comunicacaoService;

    @Mock
    private ComunicacaoRepository comunicacaoRepository;

    @Mock
    private ComunicacaoMapper comunicacaoMapper;

    @Mock
    private NotificacaoClient notificacaoClient;

    ComunicacaoEntity entity;
    ComunicacaoInDTO comunicacaoInDTO;
    ComunicacaoOutDTO comunicacaoOutDTO;
    LocalDateTime dataHoraEnvio;

    @BeforeEach
    void setUp() {
        dataHoraEnvio = LocalDateTime.of(2026, 10, 23, 14, 42, 00);
        entity = ComunicacaoEntity.builder().id(1L)
                .dataHoraenvio(dataHoraEnvio)
                .nomeTarefa("Tarefa Teste")
                .nomeDestinatario("Bruno")
                .emailDestinatario("email@teste.com")
                .telefoneDestinatario("1548415215")
                .mensagem("Mensagem Teste")
                .modoDeEnvio(ModoEnvioEnum.EMAIL)
                .statusEnvio(StatusEnvioEnum.PENDENTE)
                .build();

        comunicacaoInDTO = ComunicacaoInDTOFixture.build(dataHoraEnvio,
                "Tarefa Teste",
                "Bruno",
                "email@teste.com",
                "1548415215",
                "Mensagem Teste",
                ModoEnvioEnum.EMAIL,
                StatusEnvioEnum.PENDENTE);

        comunicacaoOutDTO = ComunicacaoOutDTOFixture.build(dataHoraEnvio,
                "Tarefa Teste",
                "Bruno",
                "email@teste.com",
                "1548415215",
                "Mensagem Teste",
                ModoEnvioEnum.EMAIL,
                StatusEnvioEnum.PENDENTE);
    }

    @Test
    void deveAgendarComunicacao() {

        comunicacaoOutDTO.setStatusEnvio(StatusEnvioEnum.ENVIADO);
        when(comunicacaoMapper.paraComunicacaoEntity(comunicacaoInDTO)).thenReturn(entity);
        when(comunicacaoRepository.save(entity)).thenReturn(entity);
        when(comunicacaoMapper.paraComunicacaoOutDTO(entity)).thenReturn(comunicacaoOutDTO);

        ComunicacaoOutDTO resultado = comunicacaoService.agendarComunicacao(comunicacaoInDTO);

        assertNotNull(resultado);
        assertEquals(StatusEnvioEnum.ENVIADO, entity.getStatusEnvio());
        assertEquals(StatusEnvioEnum.PENDENTE, comunicacaoInDTO.getStatusEnvio());
        assertEquals(StatusEnvioEnum.ENVIADO, resultado.getStatusEnvio());
        verify(comunicacaoRepository, times(2)).save(entity);
        verify(notificacaoClient, times(1)).enviarEmail(
                org.mockito.ArgumentMatchers
                        .isA(NotificacaoEmailDTO.class)
        );
        verify(comunicacaoMapper).paraComunicacaoOutDTO(entity);

    }

    @Test
    void deveBuscarStatusComunicacao() {
        when(comunicacaoRepository.findByEmailDestinatario(entity.getEmailDestinatario())).thenReturn(entity);
        when(comunicacaoMapper.paraComunicacaoOutDTO(entity)).thenReturn(comunicacaoOutDTO);

        ComunicacaoOutDTO resultado = comunicacaoService.buscarStatusComunicacao(entity.getEmailDestinatario());

        assertNotNull(resultado);

        verify(comunicacaoRepository).findByEmailDestinatario(entity.getEmailDestinatario());
        verify(comunicacaoMapper).paraComunicacaoOutDTO(entity);
    }

    @Test
    void deveAlterarStatusComunicacaoParaCancelado() {
        comunicacaoOutDTO.setStatusEnvio(StatusEnvioEnum.CANCELADO);

        when(comunicacaoRepository.findByEmailDestinatario(entity.getEmailDestinatario())).thenReturn(entity);
        when(comunicacaoRepository.save(entity)).thenReturn(entity);
        when(comunicacaoMapper.paraComunicacaoOutDTO(entity)).thenReturn(comunicacaoOutDTO);

        ComunicacaoOutDTO resultado = comunicacaoService.alterarStatusComunicacao(entity.getEmailDestinatario());

        assertEquals(StatusEnvioEnum.CANCELADO, entity.getStatusEnvio());
        assertEquals(StatusEnvioEnum.CANCELADO, resultado.getStatusEnvio());

        verify(comunicacaoRepository).findByEmailDestinatario(entity.getEmailDestinatario());
        verify(comunicacaoRepository).save(entity);
        verify(comunicacaoMapper).paraComunicacaoOutDTO(entity);
    }

    @Test
    void deveBuscarHistorico() {
        comunicacaoOutDTO.setStatusEnvio(StatusEnvioEnum.ENVIADO);
        when(comunicacaoRepository
                .findByEmailDestinatario(entity.getEmailDestinatario()))
                .thenReturn(entity);

        when(comunicacaoMapper.paraComunicacaoOutDTO(entity))
                .thenReturn(comunicacaoOutDTO);

        ComunicacaoOutDTO resultado =
                comunicacaoService.buscarStatusComunicacao(
                        entity.getEmailDestinatario());

        assertNotNull(resultado);
        assertEquals(StatusEnvioEnum.ENVIADO, resultado.getStatusEnvio());

        verify(comunicacaoRepository)
                .findByEmailDestinatario(entity.getEmailDestinatario());

        verify(comunicacaoMapper).paraComunicacaoOutDTO(entity);
    }

    @Test
    void deveLancarExcecaoQuandoDTOForNulo() {
        assertThrows(
                RecursoNaoEncontradoException.class,
                () -> comunicacaoService.agendarComunicacao(null)
        );

        verifyNoInteractions(comunicacaoRepository, comunicacaoMapper, notificacaoClient);
    }

    @Test
    void deveBuscarHistoricoPorEmail() {

        when(comunicacaoRepository.findAllByEmailDestinatarioOrderByDataHoraenvioDesc(entity.getEmailDestinatario()))
                .thenReturn(Collections.singletonList(entity));
        when(comunicacaoMapper.paraComunicacaoOutDTO(entity)).thenReturn(comunicacaoOutDTO);

        List<ComunicacaoOutDTO> resultado = comunicacaoService.buscarHistorico(entity.getEmailDestinatario());

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(comunicacaoOutDTO.getEmailDestinatario(), resultado.get(0).getEmailDestinatario());
        verify(comunicacaoRepository).findAllByEmailDestinatarioOrderByDataHoraenvioDesc(entity.getEmailDestinatario());
        verify(comunicacaoMapper).paraComunicacaoOutDTO(entity);
    }
    @Test
    void deveBuscarHistoricoCompletoQuandoDTOForNulo() {
        comunicacaoOutDTO.setStatusEnvio(StatusEnvioEnum.PENDENTE);

        when(comunicacaoRepository.findAllByOrderByDataHoraenvioDesc())
                .thenReturn(Collections.singletonList(entity));

        when(comunicacaoMapper.paraComunicacaoOutDTO(entity)).thenReturn(comunicacaoOutDTO);

        List<ComunicacaoOutDTO> resultado =
                comunicacaoService.buscarHistorico(null);

        assertEquals(1, resultado.size());

        verify(comunicacaoRepository).findAllByOrderByDataHoraenvioDesc();
        verify(comunicacaoMapper).paraComunicacaoOutDTO(entity);
    }
    @Test
    void deveLancarExcecaoQuandoHistoricoEstiverVazio() {
        when(comunicacaoRepository
                .findAllByEmailDestinatarioOrderByDataHoraenvioDesc(comunicacaoOutDTO.getEmailDestinatario()))
                .thenReturn(Collections.emptyList());

        assertThrows(
                RecursoNaoEncontradoException.class,
                () -> comunicacaoService.buscarHistorico(comunicacaoOutDTO.getEmailDestinatario())
        );

        verify(comunicacaoRepository)
                .findAllByEmailDestinatarioOrderByDataHoraenvioDesc(comunicacaoOutDTO.getEmailDestinatario());
        verifyNoInteractions(comunicacaoMapper);
    }
}