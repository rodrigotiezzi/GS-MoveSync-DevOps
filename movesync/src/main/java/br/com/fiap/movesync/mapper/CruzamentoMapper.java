package br.com.fiap.movesync.mapper;

import br.com.fiap.movesync.dto.request.CruzamentoRequestDTO;
import br.com.fiap.movesync.dto.response.CruzamentoResponseDTO;
import br.com.fiap.movesync.entity.Cruzamento;

public class CruzamentoMapper {

    public static Cruzamento toEntity(CruzamentoRequestDTO dto) {
        Cruzamento cruzamento = new Cruzamento();
        cruzamento.setNome(dto.getNome());
        cruzamento.setEndereco(dto.getEndereco());
        cruzamento.setBairro(dto.getBairro());
        cruzamento.setLatitude(dto.getLatitude());
        cruzamento.setLongitude(dto.getLongitude());
        return cruzamento;
    }

    public static CruzamentoResponseDTO toDTO(Cruzamento cruzamento) {
        return new CruzamentoResponseDTO(
                cruzamento.getId(),
                cruzamento.getNome(),
                cruzamento.getEndereco(),
                cruzamento.getBairro(),
                cruzamento.getLatitude(),
                cruzamento.getLongitude()
        );
    }
}
