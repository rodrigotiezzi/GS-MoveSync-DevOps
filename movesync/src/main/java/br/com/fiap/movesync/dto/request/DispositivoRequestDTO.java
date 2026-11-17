package br.com.fiap.movesync.dto.request;

import br.com.fiap.movesync.enums.TipoDispositivo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class DispositivoRequestDTO {

    @NotBlank(message = "O código é obrigatório.")
    @Size(max = 20, message = "O código deve ter no máximo 20 caracteres.")
    private String codigo;

    @NotNull(message = "O tipo é obrigatório.")
    private TipoDispositivo tipo;

    @Size(max = 60, message = "O modelo deve ter no máximo 60 caracteres.")
    private String modelo;

    // Opcional: se não vier, o dispositivo é criado como online
    private Boolean online;

    public DispositivoRequestDTO() {
    }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public TipoDispositivo getTipo() { return tipo; }
    public void setTipo(TipoDispositivo tipo) { this.tipo = tipo; }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }

    public Boolean getOnline() { return online; }
    public void setOnline(Boolean online) { this.online = online; }
}
