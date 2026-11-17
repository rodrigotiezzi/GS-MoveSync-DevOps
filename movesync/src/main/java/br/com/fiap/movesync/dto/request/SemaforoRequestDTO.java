package br.com.fiap.movesync.dto.request;

import br.com.fiap.movesync.enums.StatusSemaforo;
import br.com.fiap.movesync.enums.TipoSemaforo;
import jakarta.validation.constraints.*;

public class SemaforoRequestDTO {

    @NotBlank(message = "O código é obrigatório.")
    @Size(max = 20, message = "O código deve ter no máximo 20 caracteres.")
    private String codigo;

    @NotNull(message = "O tipo é obrigatório.")
    private TipoSemaforo tipo;

    // Opcional: se não vier, o semáforo é criado como ATIVO
    private StatusSemaforo status;

    @NotNull(message = "O tempo de verde é obrigatório.")
    @Positive(message = "O tempo de verde deve ser maior que zero.")
    private Integer tempoVerde;

    @NotNull(message = "O tempo de amarelo é obrigatório.")
    @Positive(message = "O tempo de amarelo deve ser maior que zero.")
    private Integer tempoAmarelo;

    @NotNull(message = "O tempo de vermelho é obrigatório.")
    @Positive(message = "O tempo de vermelho deve ser maior que zero.")
    private Integer tempoVermelho;

    @NotNull(message = "O cruzamento é obrigatório.")
    private Long idCruzamento;

    public SemaforoRequestDTO() {
    }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public TipoSemaforo getTipo() { return tipo; }
    public void setTipo(TipoSemaforo tipo) { this.tipo = tipo; }

    public StatusSemaforo getStatus() { return status; }
    public void setStatus(StatusSemaforo status) { this.status = status; }

    public Integer getTempoVerde() { return tempoVerde; }
    public void setTempoVerde(Integer tempoVerde) { this.tempoVerde = tempoVerde; }

    public Integer getTempoAmarelo() { return tempoAmarelo; }
    public void setTempoAmarelo(Integer tempoAmarelo) { this.tempoAmarelo = tempoAmarelo; }

    public Integer getTempoVermelho() { return tempoVermelho; }
    public void setTempoVermelho(Integer tempoVermelho) { this.tempoVermelho = tempoVermelho; }

    public Long getIdCruzamento() { return idCruzamento; }
    public void setIdCruzamento(Long idCruzamento) { this.idCruzamento = idCruzamento; }
}
