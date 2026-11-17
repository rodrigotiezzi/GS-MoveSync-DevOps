package br.com.fiap.movesync.dto.response;

import java.time.LocalDateTime;

public class AjusteTempoResponseDTO {

    private Long id;
    private Long idSemaforo;
    private String codigoSemaforo;
    private LocalDateTime dataHora;
    private Integer tempoVerdeAnterior;
    private Integer tempoVerdeNovo;
    private Double mediaVeiculos;
    private String motivo;

    public AjusteTempoResponseDTO(Long id, Long idSemaforo, String codigoSemaforo, LocalDateTime dataHora,
                                  Integer tempoVerdeAnterior, Integer tempoVerdeNovo, Double mediaVeiculos, String motivo) {
        this.id = id;
        this.idSemaforo = idSemaforo;
        this.codigoSemaforo = codigoSemaforo;
        this.dataHora = dataHora;
        this.tempoVerdeAnterior = tempoVerdeAnterior;
        this.tempoVerdeNovo = tempoVerdeNovo;
        this.mediaVeiculos = mediaVeiculos;
        this.motivo = motivo;
    }

    public Long getId() { return id; }
    public Long getIdSemaforo() { return idSemaforo; }
    public String getCodigoSemaforo() { return codigoSemaforo; }
    public LocalDateTime getDataHora() { return dataHora; }
    public Integer getTempoVerdeAnterior() { return tempoVerdeAnterior; }
    public Integer getTempoVerdeNovo() { return tempoVerdeNovo; }
    public Double getMediaVeiculos() { return mediaVeiculos; }
    public String getMotivo() { return motivo; }

    // Diferença em segundos (positivo = verde ficou mais longo)
    public Integer getVariacao() {
        return tempoVerdeNovo - tempoVerdeAnterior;
    }
}
