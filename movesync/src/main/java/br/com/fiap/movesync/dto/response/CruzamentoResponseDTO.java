package br.com.fiap.movesync.dto.response;

public class CruzamentoResponseDTO {

    private Long id;
    private String nome;
    private String endereco;
    private String bairro;
    private Double latitude;
    private Double longitude;

    public CruzamentoResponseDTO(Long id, String nome, String endereco, String bairro, Double latitude, Double longitude) {
        this.id = id;
        this.nome = nome;
        this.endereco = endereco;
        this.bairro = bairro;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getEndereco() { return endereco; }
    public String getBairro() { return bairro; }
    public Double getLatitude() { return latitude; }
    public Double getLongitude() { return longitude; }
}
