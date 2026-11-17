package br.com.fiap.movesync.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public class LeituraTrafegoRequestDTO {

    @NotNull(message = "O semáforo é obrigatório.")
    private Long idSemaforo;

    @NotNull(message = "O dispositivo é obrigatório.")
    private Long idDispositivo;

    // Opcional: se não vier, usa a data e hora atual (leitura em tempo real do sensor)
    @PastOrPresent(message = "A leitura não pode estar no futuro.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime dataHora;

    @NotNull(message = "A quantidade de veículos é obrigatória.")
    @PositiveOrZero(message = "A quantidade de veículos não pode ser negativa.")
    private Integer qtdVeiculos;

    @NotNull(message = "A quantidade de pedestres é obrigatória.")
    @PositiveOrZero(message = "A quantidade de pedestres não pode ser negativa.")
    private Integer qtdPedestres;

    @PositiveOrZero(message = "A velocidade média não pode ser negativa.")
    private Double velocidadeMedia;

    public LeituraTrafegoRequestDTO() {
    }

    public Long getIdSemaforo() { return idSemaforo; }
    public void setIdSemaforo(Long idSemaforo) { this.idSemaforo = idSemaforo; }

    public Long getIdDispositivo() { return idDispositivo; }
    public void setIdDispositivo(Long idDispositivo) { this.idDispositivo = idDispositivo; }

    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }

    public Integer getQtdVeiculos() { return qtdVeiculos; }
    public void setQtdVeiculos(Integer qtdVeiculos) { this.qtdVeiculos = qtdVeiculos; }

    public Integer getQtdPedestres() { return qtdPedestres; }
    public void setQtdPedestres(Integer qtdPedestres) { this.qtdPedestres = qtdPedestres; }

    public Double getVelocidadeMedia() { return velocidadeMedia; }
    public void setVelocidadeMedia(Double velocidadeMedia) { this.velocidadeMedia = velocidadeMedia; }
}
