package br.com.fiap.movesync.mapper;

import br.com.fiap.movesync.dto.request.DispositivoRequestDTO;
import br.com.fiap.movesync.dto.response.DispositivoResponseDTO;
import br.com.fiap.movesync.entity.Dispositivo;
import br.com.fiap.movesync.entity.Semaforo;

import java.util.Collections;
import java.util.List;

public class DispositivoMapper {

    public static Dispositivo toEntity(DispositivoRequestDTO dto) {
        Dispositivo dispositivo = new Dispositivo();
        dispositivo.setCodigo(dto.getCodigo());
        dispositivo.setTipo(dto.getTipo());
        dispositivo.setModelo(dto.getModelo());
        dispositivo.setOnline(dto.getOnline() != null ? dto.getOnline() : Boolean.TRUE);
        return dispositivo;
    }

    public static DispositivoResponseDTO toDTO(Dispositivo dispositivo) {
        List<Long> idsSemaforos = dispositivo.getSemaforos() == null
                ? Collections.emptyList()
                : dispositivo.getSemaforos().stream()
                .map(Semaforo::getId)
                .toList();

        return new DispositivoResponseDTO(
                dispositivo.getId(),
                dispositivo.getCodigo(),
                dispositivo.getTipo(),
                dispositivo.getModelo(),
                dispositivo.getOnline(),
                idsSemaforos
        );
    }
}
