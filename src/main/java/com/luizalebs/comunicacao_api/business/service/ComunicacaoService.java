package com.luizalebs.comunicacao_api.business.service;

import com.luizalebs.comunicacao_api.api.dto.ComunicacaoInDTO;
import com.luizalebs.comunicacao_api.api.dto.ComunicacaoOutDTO;
import com.luizalebs.comunicacao_api.business.mapper.ComunicacaoMapper;
import com.luizalebs.comunicacao_api.infraestructure.client.NotificacaoClient;
import com.luizalebs.comunicacao_api.infraestructure.client.dto.NotificacaoEmailDTO;
import com.luizalebs.comunicacao_api.infraestructure.entities.ComunicacaoEntity;
import com.luizalebs.comunicacao_api.infraestructure.enums.ModoEnvioEnum;
import com.luizalebs.comunicacao_api.infraestructure.enums.StatusEnvioEnum;
import com.luizalebs.comunicacao_api.infraestructure.exceptions.RecursoNaoEncontradoException;
import com.luizalebs.comunicacao_api.infraestructure.repositories.ComunicacaoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ComunicacaoService {

    private final ComunicacaoRepository repository;
    private final ComunicacaoMapper mapper;
    private final NotificacaoClient notificacaoClient;

    public ComunicacaoService(ComunicacaoRepository repository, ComunicacaoMapper mapper, NotificacaoClient notificacaoClient) {
        this.repository = repository;
        this.mapper = mapper;
        this.notificacaoClient = notificacaoClient;
    }

    public ComunicacaoOutDTO agendarComunicacao(ComunicacaoInDTO dto) {
        if (Objects.isNull(dto)) {
            throw new RecursoNaoEncontradoException(
                    "Os dados estão nulos."
            );
        }
        dto.setStatusEnvio(StatusEnvioEnum.PENDENTE);
        ComunicacaoEntity entity = mapper.paraComunicacaoEntity(dto);
        repository.save(entity);
        if (dto.getModoDeEnvio() == ModoEnvioEnum.EMAIL) {
            try {
                NotificacaoEmailDTO email = new NotificacaoEmailDTO();
                email.setNomeTarefa(entity.getNomeTarefa());
                email.setNomeDestinatario(entity.getNomeDestinatario());
                email.setDescricao(entity.getMensagem());
                email.setEmailUsuario(entity.getEmailDestinatario());
                email.setDataEvento(entity.getDataHoraenvio());
                notificacaoClient.enviarEmail(email);
                entity.setStatusEnvio(StatusEnvioEnum.ENVIADO);
            } catch (Exception e) {
                entity.setStatusEnvio(StatusEnvioEnum.FALHA);
            }
            entity = repository.save(entity);
        }
        return mapper.paraComunicacaoOutDTO(entity);
    }

    public ComunicacaoOutDTO buscarStatusComunicacao(String emailDestinatario) {
        ComunicacaoEntity entity = repository.findByEmailDestinatario(emailDestinatario);
        if (Objects.isNull(entity)) {
            throw new RecursoNaoEncontradoException(
                    "Status não encontrado, email destinatario nulo."
            );
        }
        return mapper.paraComunicacaoOutDTO(entity);
    }

    public ComunicacaoOutDTO alterarStatusComunicacao(String emailDestinatario) {
        ComunicacaoEntity entity = repository.findByEmailDestinatario(emailDestinatario);
        if (Objects.isNull(entity)) {
            throw new RecursoNaoEncontradoException("Email não encontrado para a alteração de status.");
        }
        entity.setStatusEnvio(StatusEnvioEnum.CANCELADO);
        repository.save(entity);
        return (mapper.paraComunicacaoOutDTO(entity));
    }

    public List<ComunicacaoOutDTO> buscarHistorico(
            String emailDestinatario) {

        List<ComunicacaoEntity> lista;

        if (emailDestinatario == null ||
                emailDestinatario.isEmpty()) {

            lista = repository.findAllByOrderByDataHoraenvioDesc();

        } else {

            lista = repository
                    .findAllByEmailDestinatarioOrderByDataHoraenvioDesc(
                            emailDestinatario
                    );
        }
        if (lista.isEmpty() || lista == null) {
            throw new RecursoNaoEncontradoException(
                    "Nenhum histórico encontrado para o email informado."
            );
        }

        return lista.stream()
                .map(mapper::paraComunicacaoOutDTO)
                .collect(Collectors.toList());
    }

}
