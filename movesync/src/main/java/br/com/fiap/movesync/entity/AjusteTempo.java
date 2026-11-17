package br.com.fiap.movesync.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;


// Histórico de cada ajuste automático do tempo de verde feito pelo MoveSync
@Entity
@Table(name = "TB_AJUSTE_TEMPO")
public class AjusteTempo {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_AJUSTE")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ID_SEMAFORO", nullable = false)
    private Semaforo semaforo;

    @Column(name = "DT_HORA_AJUSTE", nullable = false)
    private LocalDateTime dataHora;

    @Column(name = "TEMPO_VERDE_ANTERIOR", nullable = false)
    private Integer tempoVerdeAnterior;

    @Column(name = "TEMPO_VERDE_NOVO", nullable = false)
    private Integer tempoVerdeNovo;

    @Column(name = "MEDIA_VEICULOS", nullable = false)
    private Double mediaVeiculos;

    @Column(name = "MOTIVO")
    private String motivo;

    public AjusteTempo(Long id, Semaforo semaforo, LocalDateTime dataHora, Integer tempoVerdeAnterior,
                       Integer tempoVerdeNovo, Double mediaVeiculos, String motivo) {
        this.id = id;
        this.semaforo = semaforo;
        this.dataHora = dataHora;
        this.tempoVerdeAnterior = tempoVerdeAnterior;
        this.tempoVerdeNovo = tempoVerdeNovo;
        this.mediaVeiculos = mediaVeiculos;
        this.motivo = motivo;
    }

    public AjusteTempo() {
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Semaforo getSemaforo() {
        return semaforo;
    }
    public void setSemaforo(Semaforo semaforo) {
        this.semaforo = semaforo;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }
    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public Integer getTempoVerdeAnterior() {
        return tempoVerdeAnterior;
    }
    public void setTempoVerdeAnterior(Integer tempoVerdeAnterior) {
        this.tempoVerdeAnterior = tempoVerdeAnterior;
    }

    public Integer getTempoVerdeNovo() {
        return tempoVerdeNovo;
    }
    public void setTempoVerdeNovo(Integer tempoVerdeNovo) {
        this.tempoVerdeNovo = tempoVerdeNovo;
    }

    public Double getMediaVeiculos() {
        return mediaVeiculos;
    }
    public void setMediaVeiculos(Double mediaVeiculos) {
        this.mediaVeiculos = mediaVeiculos;
    }

    public String getMotivo() {
        return motivo;
    }
    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
}
