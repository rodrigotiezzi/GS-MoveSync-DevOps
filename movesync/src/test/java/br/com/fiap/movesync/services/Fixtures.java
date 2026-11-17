package br.com.fiap.movesync.services;

import br.com.fiap.movesync.entity.Cruzamento;
import br.com.fiap.movesync.entity.Dispositivo;
import br.com.fiap.movesync.entity.Semaforo;
import br.com.fiap.movesync.enums.StatusSemaforo;
import br.com.fiap.movesync.enums.TipoDispositivo;
import br.com.fiap.movesync.enums.TipoSemaforo;

// Dados fictícios reutilizados pelos testes
final class Fixtures {

    private Fixtures() {
    }

    static Cruzamento cruzamento(Long id) {
        return new Cruzamento(id, "Av. Paulista x R. Augusta", "Av. Paulista, 1000", "Bela Vista", -23.5614, -46.6559);
    }

    static Semaforo semaforo(Long id, StatusSemaforo status) {
        return new Semaforo(id, "SEM-00" + id, TipoSemaforo.VEICULAR, status, 30, 4, 30, cruzamento(1L));
    }

    static Dispositivo dispositivo(Long id, boolean online) {
        return new Dispositivo(id, "CAM-00" + id, TipoDispositivo.CAMERA, "Câmera fictícia MS-CAM v1", online);
    }
}
