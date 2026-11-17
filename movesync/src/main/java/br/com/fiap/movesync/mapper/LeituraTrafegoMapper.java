package br.com.fiap.movesync.mapper;

import br.com.fiap.movesync.dto.request.LeituraTrafegoRequestDTO;
import br.com.fiap.movesync.dto.response.LeituraTrafegoResponseDTO;
import br.com.fiap.movesync.entity.Dispositivo;
import br.com.fiap.movesync.entity.LeituraTrafego;
import br.com.fiap.movesync.entity.Semaforo;

import java.time.LocalDateTime;

public class LeituraTrafegoMapper {

    public static LeituraTrafego toEntity(LeituraTrafegoRequestDTO dto, Semaforo semaforo, Dispositivo dispositivo) {
        LeituraTrafego leitura = new LeituraTrafego();
        leitura.setSemaforo(semaforo);
        leitura.setDispositivo(dispositivo);
        leitura.setDataHora(dto.getDataHora() != null ? dto.getDataHora() : LocalDateTime.now());
        leitura.setQtdVeiculos(dto.getQtdVeiculos());
        leitura.setQtdPedestres(dto.getQtdPedestres());
        leitura.setVelocidadeMedia(dto.getVelocidadeMedia());
        return leitura;
    }

    public static LeituraTrafegoResponseDTO toDTO(LeituraTrafego leitura) {
        return new LeituraTrafegoResponseDTO(
                leitura.getId(),
                leitura.getSemaforo().getId(),
                leitura.getSemaforo().getCodigo(),
                leitura.getDispositivo().getId(),
                leitura.getDispositivo().getCodigo(),
                leitura.getDataHora(),
                leitura.getQtdVeiculos(),
                leitura.getQtdPedestres(),
                leitura.getVelocidadeMedia()
        );
    }
}
