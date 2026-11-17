package br.com.fiap.movesync.dto.response;

import java.time.LocalDateTime;

public class LeituraTrafegoResponseDTO {

    private Long id;
    private Long idSemaforo;
    private String codigoSemaforo;
    private Long idDispositivo;
    private String codigoDispositivo;
    private LocalDateTime dataHora;
    private Integer qtdVeiculos;
    private Integer qtdPedestres;
    private Double velocidadeMedia;

    public LeituraTrafegoResponseDTO(Long id, Long idSemaforo, String codigoSemaforo, Long idDispositivo,
                                     String codigoDispositivo, LocalDateTime dataHora,
                                     Integer qtdVeiculos, Integer qtdPedestres, Double velocidadeMedia) {
        this.id = id;
        this.idSemaforo = idSemaforo;
        this.codigoSemaforo = codigoSemaforo;
        this.idDispositivo = idDispositivo;
        this.codigoDispositivo = codigoDispositivo;
        this.dataHora = dataHora;
        this.qtdVeiculos = qtdVeiculos;
        this.qtdPedestres = qtdPedestres;
        this.velocidadeMedia = velocidadeMedia;
    }

    public Long getId() { return id; }
    public Long getIdSemaforo() { return idSemaforo; }
    public String getCodigoSemaforo() { return codigoSemaforo; }
    public Long getIdDispositivo() { return idDispositivo; }
    public String getCodigoDispositivo() { return codigoDispositivo; }
    public LocalDateTime getDataHora() { return dataHora; }
    public Integer getQtdVeiculos() { return qtdVeiculos; }
    public Integer getQtdPedestres() { return qtdPedestres; }
    public Double getVelocidadeMedia() { return velocidadeMedia; }
}
