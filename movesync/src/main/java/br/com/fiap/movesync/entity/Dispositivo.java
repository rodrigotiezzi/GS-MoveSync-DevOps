package br.com.fiap.movesync.entity;

import br.com.fiap.movesync.enums.TipoDispositivo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;


// Sensor ou câmera IoT instalado no cruzamento (dados de leitura são simulados/fictícios)
@Entity
@Table(name = "TB_DISPOSITIVO")
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"}, ignoreUnknown = true)
public class Dispositivo {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_DISPOSITIVO")
    private Long id;

    @NotBlank(message = "O código é obrigatório.")
    @Size(max = 20, message = "O código deve ter no máximo 20 caracteres.")
    @Column(name = "CODIGO", nullable = false, unique = true, length = 20)
    private String codigo;

    @NotNull(message = "O tipo é obrigatório.")
    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO", nullable = false, length = 20)
    private TipoDispositivo tipo;

    @Column(name = "MODELO", length = 60)
    private String modelo;

    @Column(name = "FL_ONLINE", nullable = false)
    private Boolean online = true;

    //Relacionamento do dispositivo com os semáforos que ele monitora
    @ManyToMany(mappedBy = "dispositivos")
    @JsonIgnoreProperties("dispositivos")
    private List<Semaforo> semaforos = new ArrayList<>();

    public Dispositivo(Long id, String codigo, TipoDispositivo tipo, String modelo, Boolean online) {
        this.id = id;
        this.codigo = codigo;
        this.tipo = tipo;
        this.modelo = modelo;
        this.online = online;
    }

    public Dispositivo() {
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

    public TipoDispositivo getTipo() {
        return tipo;
    }
    public void setTipo(TipoDispositivo tipo) {
        this.tipo = tipo;
    }

    public String getModelo() {
        return modelo;
    }
    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public Boolean getOnline() {
        return online;
    }
    public void setOnline(Boolean online) {
        this.online = online;
    }

    public List<Semaforo> getSemaforos() {
        return semaforos;
    }
    public void setSemaforos(List<Semaforo> semaforos) {
        this.semaforos = semaforos;
    }
}
