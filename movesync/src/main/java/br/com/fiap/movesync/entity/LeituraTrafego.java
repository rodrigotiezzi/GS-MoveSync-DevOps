package br.com.fiap.movesync.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDateTime;


@Entity
@Table(name = "TB_LEITURA_TRAFEGO")
public class LeituraTrafego {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_LEITURA")
    private Long id;

    @NotNull(message = "O semáforo é obrigatório.")
    @ManyToOne
    @JoinColumn(name = "ID_SEMAFORO", nullable = false)
    private Semaforo semaforo;

    @NotNull(message = "O dispositivo é obrigatório.")
    @ManyToOne
    @JoinColumn(name = "ID_DISPOSITIVO", nullable = false)
    private Dispositivo dispositivo;

    @NotNull(message = "A data e hora da leitura são obrigatórias.")
    @Column(name = "DT_HORA_LEITURA", nullable = false)
    private LocalDateTime dataHora;

    @NotNull(message = "A quantidade de veículos é obrigatória.")
    @PositiveOrZero(message = "A quantidade de veículos não pode ser negativa.")
    @Column(name = "QTD_VEICULOS", nullable = false)
    private Integer qtdVeiculos;

    @NotNull(message = "A quantidade de pedestres é obrigatória.")
    @PositiveOrZero(message = "A quantidade de pedestres não pode ser negativa.")
    @Column(name = "QTD_PEDESTRES", nullable = false)
    private Integer qtdPedestres;

    @PositiveOrZero(message = "A velocidade média não pode ser negativa.")
    @Column(name = "VELOCIDADE_MEDIA")
    private Double velocidadeMedia;

    public LeituraTrafego(Long id, Semaforo semaforo, Dispositivo dispositivo, LocalDateTime dataHora, Integer qtdVeiculos,
                          Integer qtdPedestres, Double velocidadeMedia) {
        this.id = id;
        this.semaforo = semaforo;
        this.dispositivo = dispositivo;
        this.dataHora = dataHora;
        this.qtdVeiculos = qtdVeiculos;
        this.qtdPedestres = qtdPedestres;
        this.velocidadeMedia = velocidadeMedia;
    }

    public LeituraTrafego() {
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

    public Dispositivo getDispositivo() {
        return dispositivo;
    }
    public void setDispositivo(Dispositivo dispositivo) {
        this.dispositivo = dispositivo;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }
    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public Integer getQtdVeiculos() {
        return qtdVeiculos;
    }
    public void setQtdVeiculos(Integer qtdVeiculos) {
        this.qtdVeiculos = qtdVeiculos;
    }

    public Integer getQtdPedestres() {
        return qtdPedestres;
    }
    public void setQtdPedestres(Integer qtdPedestres) {
        this.qtdPedestres = qtdPedestres;
    }

    public Double getVelocidadeMedia() {
        return velocidadeMedia;
    }
    public void setVelocidadeMedia(Double velocidadeMedia) {
        this.velocidadeMedia = velocidadeMedia;
    }
}
