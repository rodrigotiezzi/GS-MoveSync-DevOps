package br.com.fiap.movesync.mapper;

import br.com.fiap.movesync.dto.request.SemaforoRequestDTO;
import br.com.fiap.movesync.dto.response.SemaforoResponseDTO;
import br.com.fiap.movesync.entity.Cruzamento;
import br.com.fiap.movesync.entity.Dispositivo;
import br.com.fiap.movesync.entity.Semaforo;
import br.com.fiap.movesync.enums.StatusSemaforo;

import java.util.Collections;
import java.util.List;

public class SemaforoMapper {

    // O cruzamento já vem pelo service (buscado por id a partir do DTO)
    public static Semaforo toEntity(SemaforoRequestDTO dto, Cruzamento cruzamento) {
        Semaforo semaforo = new Semaforo();
        semaforo.setCodigo(dto.getCodigo());
        semaforo.setTipo(dto.getTipo());
        semaforo.setStatus(dto.getStatus() != null ? dto.getStatus() : StatusSemaforo.ATIVO);
        semaforo.setTempoVerde(dto.getTempoVerde());
        semaforo.setTempoAmarelo(dto.getTempoAmarelo());
        semaforo.setTempoVermelho(dto.getTempoVermelho());
        semaforo.setCruzamento(cruzamento);
        return semaforo;
    }

    public static SemaforoResponseDTO toDTO(Semaforo semaforo) {
        List<Long> idsDispositivos = semaforo.getDispositivos() == null
                ? Collections.emptyList()
                : semaforo.getDispositivos().stream()
                .map(Dispositivo::getId)
                .toList();

        return new SemaforoResponseDTO(
                semaforo.getId(),
                semaforo.getCodigo(),
                semaforo.getTipo(),
                semaforo.getStatus(),
                semaforo.getTempoVerde(),
                semaforo.getTempoAmarelo(),
                semaforo.getTempoVermelho(),
                semaforo.getCruzamento().getId(),
                semaforo.getCruzamento().getNome(),
                idsDispositivos
        );
    }
}
