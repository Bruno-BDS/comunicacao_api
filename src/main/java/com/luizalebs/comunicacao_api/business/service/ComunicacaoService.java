package com.luizalebs.comunicacao_api.business.service;

import com.luizalebs.comunicacao_api.api.dto.ComunicacaoInDTO;
import com.luizalebs.comunicacao_api.api.dto.ComunicacaoOutDTO;
import com.luizalebs.comunicacao_api.business.converter.ComunicacaoConverter;
import com.luizalebs.comunicacao_api.infraestructure.client.NotificacaoClient;
import com.luizalebs.comunicacao_api.infraestructure.client.dto.NotificacaoEmailDTO;
import com.luizalebs.comunicacao_api.infraestructure.entities.ComunicacaoEntity;
import com.luizalebs.comunicacao_api.infraestructure.enums.ModoEnvioEnum;
import com.luizalebs.comunicacao_api.infraestructure.enums.StatusEnvioEnum;
import com.luizalebs.comunicacao_api.infraestructure.repositories.ComunicacaoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ComunicacaoService {

    private final ComunicacaoRepository repository;
    private final ComunicacaoConverter converter;
    private final NotificacaoClient notificacaoClient;

    public ComunicacaoService(ComunicacaoRepository repository, ComunicacaoConverter converter, NotificacaoClient notificacaoClient) {
        this.repository = repository;
        this.converter = converter;
        this.notificacaoClient = notificacaoClient;
    }

    public ComunicacaoOutDTO agendarComunicacao(ComunicacaoInDTO dto) {
        if (Objects.isNull(dto)) {
            throw new RuntimeException();
        }
        dto.setStatusEnvio(StatusEnvioEnum.PENDENTE);
        ComunicacaoEntity entity = converter.paraEntity(dto);
        repository.save(entity);
        if (dto.getModoDeEnvio() == ModoEnvioEnum.EMAIL){
            try {
                NotificacaoEmailDTO email = new NotificacaoEmailDTO();
                email.setNomeTarefa(entity.getNomeDestinatario());
                email.setDescricao(entity.getMensagem());
                email.setEmailUsuario(entity.getEmailDestinatario());
                email.setDataEvento(
                        entity.getDataHoraenvio()
                                .toInstant()
                                .atZone(
                                        java.time.ZoneId.systemDefault()
                                )
                                .toLocalDateTime()
                );
                notificacaoClient.enviarEmail(email);
                entity.setStatusEnvio(StatusEnvioEnum.ENVIADO);
            }catch (Exception e){
                entity.setStatusEnvio(StatusEnvioEnum.FALHA);
            }
            entity = repository.save(entity);
        }
        return converter.paraDTO(entity);
    }

    public ComunicacaoOutDTO buscarStatusComunicacao(String emailDestinatario) {
        ComunicacaoEntity entity = repository.findByEmailDestinatario(emailDestinatario);
        if (Objects.isNull(entity)) {
            throw new RuntimeException();
        }
        return converter.paraDTO(entity);
    }

    public ComunicacaoOutDTO alterarStatusComunicacao(String emailDestinatario) {
        ComunicacaoEntity entity = repository.findByEmailDestinatario(emailDestinatario);
        if (Objects.isNull(entity)) {
            throw new RuntimeException();
        }
        entity.setStatusEnvio(StatusEnvioEnum.CANCELADO);
        repository.save(entity);
        return (converter.paraDTO(entity));
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

        return lista.stream()
                .map(converter::paraDTO)
                .collect(Collectors.toList());
    }

}
