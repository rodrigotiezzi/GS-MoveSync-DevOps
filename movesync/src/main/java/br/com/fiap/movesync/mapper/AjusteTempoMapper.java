package br.com.fiap.movesync.mapper;

import br.com.fiap.movesync.dto.response.AjusteTempoResponseDTO;
import br.com.fiap.movesync.entity.AjusteTempo;

public class AjusteTempoMapper {

    public static AjusteTempoResponseDTO toDTO(AjusteTempo ajuste) {
        return new AjusteTempoResponseDTO(
                ajuste.getId(),
                ajuste.getSemaforo().getId(),
                ajuste.getSemaforo().getCodigo(),
                ajuste.getDataHora(),
                ajuste.getTempoVerdeAnterior(),
                ajuste.getTempoVerdeNovo(),
                ajuste.getMediaVeiculos(),
                ajuste.getMotivo()
        );
    }
}
