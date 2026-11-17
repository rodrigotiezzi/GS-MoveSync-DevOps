package br.com.fiap.movesync.dto.response;

import br.com.fiap.movesync.enums.StatusSemaforo;
import br.com.fiap.movesync.enums.TipoSemaforo;

import java.util.List;

public class SemaforoResponseDTO {

    private Long id;
    private String codigo;
    private TipoSemaforo tipo;
    private StatusSemaforo status;
    private Integer tempoVerde;
    private Integer tempoAmarelo;
    private Integer tempoVermelho;
    private Long idCruzamento;
    private String nomeCruzamento;
    private List<Long> idsDispositivos;

    public SemaforoResponseDTO(Long id, String codigo, TipoSemaforo tipo, StatusSemaforo status, Integer tempoVerde,
                               Integer tempoAmarelo, Integer tempoVermelho, Long idCruzamento, String nomeCruzamento,
                               List<Long> idsDispositivos) {
        this.id = id;
        this.codigo = codigo;
        this.tipo = tipo;
        this.status = status;
        this.tempoVerde = tempoVerde;
        this.tempoAmarelo = tempoAmarelo;
        this.tempoVermelho = tempoVermelho;
        this.idCruzamento = idCruzamento;
        this.nomeCruzamento = nomeCruzamento;
        this.idsDispositivos = idsDispositivos;
    }

    public Long getId() { return id; }
    public String getCodigo() { return codigo; }
    public TipoSemaforo getTipo() { return tipo; }
    public StatusSemaforo getStatus() { return status; }
    public Integer getTempoVerde() { return tempoVerde; }
    public Integer getTempoAmarelo() { return tempoAmarelo; }
    public Integer getTempoVermelho() { return tempoVermelho; }
    public Long getIdCruzamento() { return idCruzamento; }
    public String getNomeCruzamento() { return nomeCruzamento; }
    public List<Long> getIdsDispositivos() { return idsDispositivos; }

    // Ciclo completo do semáforo (verde + amarelo + vermelho), em segundos
    public Integer getCicloTotal() {
        return tempoVerde + tempoAmarelo + tempoVermelho;
    }
}
