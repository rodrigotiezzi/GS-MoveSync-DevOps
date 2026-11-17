package br.com.fiap.movesync.entity;

import br.com.fiap.movesync.enums.StatusSemaforo;
import br.com.fiap.movesync.enums.TipoSemaforo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "TB_SEMAFORO")
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"}, ignoreUnknown = true)
public class Semaforo {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_SEMAFORO")
    private Long id;

    @NotBlank(message = "O código é obrigatório.")
    @Size(max = 20, message = "O código deve ter no máximo 20 caracteres.")
    @Column(name = "CODIGO", nullable = false, unique = true, length = 20)
    private String codigo;

    @NotNull(message = "O tipo é obrigatório.")
    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO", nullable = false, length = 20)
    private TipoSemaforo tipo;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false, length = 20)
    private StatusSemaforo status = StatusSemaforo.ATIVO; // status padrão

    @NotNull(message = "O tempo de verde é obrigatório.")
    @Positive(message = "O tempo de verde deve ser maior que zero.")
    @Column(name = "TEMPO_VERDE", nullable = false)
    private Integer tempoVerde;

    @NotNull(message = "O tempo de amarelo é obrigatório.")
    @Positive(message = "O tempo de amarelo deve ser maior que zero.")
    @Column(name = "TEMPO_AMARELO", nullable = false)
    private Integer tempoAmarelo;

    @NotNull(message = "O tempo de vermelho é obrigatório.")
    @Positive(message = "O tempo de vermelho deve ser maior que zero.")
    @Column(name = "TEMPO_VERMELHO", nullable = false)
    private Integer tempoVermelho;

    //Relacionamento do semáforo com o cruzamento onde está instalado
    @NotNull(message = "O cruzamento é obrigatório.")
    @ManyToOne
    @JoinColumn(name = "ID_CRUZAMENTO", nullable = false)
    private Cruzamento cruzamento;

    //Relacionamento N:N do semáforo com os sensores/câmeras que o monitoram
    @ManyToMany
    @JoinTable(name = "TB_SEMAFORO_DISPOSITIVO",
            joinColumns = @JoinColumn(name = "ID_SEMAFORO"),
            inverseJoinColumns = @JoinColumn(name = "ID_DISPOSITIVO")
    )
    @JsonIgnoreProperties("semaforos")
    private List<Dispositivo> dispositivos = new ArrayList<>();

    public Semaforo(Long id, String codigo, TipoSemaforo tipo, StatusSemaforo status, Integer tempoVerde,
                    Integer tempoAmarelo, Integer tempoVermelho, Cruzamento cruzamento) {
        this.id = id;
        this.codigo = codigo;
        this.tipo = tipo;
        this.status = status;
        this.tempoVerde = tempoVerde;
        this.tempoAmarelo = tempoAmarelo;
        this.tempoVermelho = tempoVermelho;
        this.cruzamento = cruzamento;
    }

    public Semaforo() {
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public TipoSemaforo getTipo() {
        return tipo;
    }
    public void setTipo(TipoSemaforo tipo) {
        this.tipo = tipo;
    }

    public StatusSemaforo getStatus() {
        return status;
    }
    public void setStatus(StatusSemaforo status) {
        this.status = status;
    }

    public Integer getTempoVerde() {
        return tempoVerde;
    }
    public void setTempoVerde(Integer tempoVerde) {
        this.tempoVerde = tempoVerde;
    }

    public Integer getTempoAmarelo() {
        return tempoAmarelo;
    }
    public void setTempoAmarelo(Integer tempoAmarelo) {
        this.tempoAmarelo = tempoAmarelo;
    }

    public Integer getTempoVermelho() {
        return tempoVermelho;
    }
    public void setTempoVermelho(Integer tempoVermelho) {
        this.tempoVermelho = tempoVermelho;
    }

    public Cruzamento getCruzamento() {
        return cruzamento;
    }
    public void setCruzamento(Cruzamento cruzamento) {
        this.cruzamento = cruzamento;
    }

    public List<Dispositivo> getDispositivos() {
        return dispositivos;
    }
    public void setDispositivos(List<Dispositivo> dispositivos) {
        this.dispositivos = dispositivos;
    }
}
