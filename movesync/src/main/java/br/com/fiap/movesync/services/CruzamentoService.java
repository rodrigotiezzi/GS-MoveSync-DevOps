package br.com.fiap.movesync.services;

import br.com.fiap.movesync.dto.request.CruzamentoRequestDTO;
import br.com.fiap.movesync.dto.response.CruzamentoResponseDTO;
import br.com.fiap.movesync.entity.Cruzamento;
import br.com.fiap.movesync.exceptions.EntityNotFoundException;
import br.com.fiap.movesync.mapper.CruzamentoMapper;
import br.com.fiap.movesync.repository.CruzamentoRepository;
import br.com.fiap.movesync.repository.SemaforoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CruzamentoService {

    @Autowired
    private CruzamentoRepository cruzamentoRepository;

    @Autowired
    private SemaforoRepository semaforoRepository;

    public CruzamentoResponseDTO salvar(CruzamentoRequestDTO dto) {
        Cruzamento cruzamento = CruzamentoMapper.toEntity(dto);
        return CruzamentoMapper.toDTO(cruzamentoRepository.save(cruzamento));
    }

    public Page<CruzamentoResponseDTO> buscarTodos(Pageable pageable) {
        return cruzamentoRepository.findAll(pageable).map(CruzamentoMapper::toDTO);
    }

    public CruzamentoResponseDTO buscarPorId(Long id) {
        return CruzamentoMapper.toDTO(buscarEntidadePorId(id));
    }

    public Page<CruzamentoResponseDTO> buscarPorNome(String nome, Pageable pageable) {
        return cruzamentoRepository.findByNomeContainingIgnoreCase(nome, pageable).map(CruzamentoMapper::toDTO);
    }

    public Page<CruzamentoResponseDTO> buscarPorBairro(String bairro, Pageable pageable) {
        return cruzamentoRepository.findByBairroIgnoreCase(bairro, pageable).map(CruzamentoMapper::toDTO);
    }

    public CruzamentoResponseDTO atualizar(Long id, CruzamentoRequestDTO dto) {
        Cruzamento cruzamento = buscarEntidadePorId(id);
        cruzamento.setNome(dto.getNome());
        cruzamento.setEndereco(dto.getEndereco());
        cruzamento.setBairro(dto.getBairro());
        cruzamento.setLatitude(dto.getLatitude());
        cruzamento.setLongitude(dto.getLongitude());
        return CruzamentoMapper.toDTO(cruzamentoRepository.save(cruzamento));
    }

    public void deletar(Long id) {
        buscarEntidadePorId(id);

        // Regra: não remove cruzamento que ainda tem semáforos instalados
        if (!semaforoRepository.findByCruzamentoId(id).isEmpty()) {
            throw new IllegalStateException("Não é possível excluir: o cruzamento possui semáforos cadastrados.");
        }

        cruzamentoRepository.deleteById(id);
    }

    // usado pelo SemaforoService, que precisa da entidade completa
    Cruzamento buscarEntidadePorId(Long id) {
        return cruzamentoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cruzamento não encontrado."));
    }
}
