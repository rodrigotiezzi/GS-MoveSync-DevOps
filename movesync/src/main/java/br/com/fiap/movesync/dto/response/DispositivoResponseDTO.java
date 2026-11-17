package br.com.fiap.movesync.dto.response;

import br.com.fiap.movesync.enums.TipoDispositivo;

import java.util.List;

public class DispositivoResponseDTO {

    private Long id;
    private String codigo;
    private TipoDispositivo tipo;
    private String modelo;
    private Boolean online;
    private List<Long> idsSemaforos;

    public DispositivoResponseDTO(Long id, String codigo, TipoDispositivo tipo, String modelo, Boolean online, List<Long> idsSemaforos) {
        this.id = id;
        this.codigo = codigo;
        this.tipo = tipo;
        this.modelo = modelo;
        this.online = online;
        this.idsSemaforos = idsSemaforos;
    }

    public Long getId() { return id; }
    public String getCodigo() { return codigo; }
    public TipoDispositivo getTipo() { return tipo; }
    public String getModelo() { return modelo; }
    public Boolean getOnline() { return online; }
    public List<Long> getIdsSemaforos() { return idsSemaforos; }
}
