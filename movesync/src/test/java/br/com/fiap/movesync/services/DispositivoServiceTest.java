package br.com.fiap.movesync.services;

import br.com.fiap.movesync.dto.request.DispositivoRequestDTO;
import br.com.fiap.movesync.dto.response.DispositivoResponseDTO;
import br.com.fiap.movesync.entity.Dispositivo;
import br.com.fiap.movesync.enums.StatusSemaforo;
import br.com.fiap.movesync.enums.TipoDispositivo;
import br.com.fiap.movesync.repository.DispositivoRepository;
import br.com.fiap.movesync.repository.LeituraTrafegoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DispositivoServiceTest {

    @Mock
    private DispositivoRepository dispositivoRepository;

    @Mock
    private LeituraTrafegoRepository leituraRepository;

    @InjectMocks
    private DispositivoService dispositivoService;

    @Test
    @DisplayName("Dispositivo novo nasce online quando a situação não é informada")
    void salvarOnlinePorPadrao() {
        DispositivoRequestDTO dto = new DispositivoRequestDTO();
        dto.setCodigo("cam-100");
        dto.setTipo(TipoDispositivo.CAMERA);
        when(dispositivoRepository.existsByCodigo("CAM-100")).thenReturn(false);
        when(dispositivoRepository.save(any(Dispositivo.class))).thenAnswer(inv -> inv.getArgument(0));

        DispositivoResponseDTO salvo = dispositivoService.salvar(dto);

        assertEquals("CAM-100", salvo.getCodigo());
        assertTrue(salvo.getOnline());
    }

    @Test
    @DisplayName("Não permite código de dispositivo repetido")
    void salvarCodigoDuplicado() {
        DispositivoRequestDTO dto = new DispositivoRequestDTO();
        dto.setCodigo("CAM-001");
        dto.setTipo(TipoDispositivo.CAMERA);
        when(dispositivoRepository.existsByCodigo("CAM-001")).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> dispositivoService.salvar(dto));
    }

    @Test
    @DisplayName("Não exclui dispositivo ainda vinculado a semáforo")
    void deletarVinculado() {
        Dispositivo camera = Fixtures.dispositivo(5L, true);
        camera.getSemaforos().add(Fixtures.semaforo(1L, StatusSemaforo.ATIVO));
        when(dispositivoRepository.findById(5L)).thenReturn(Optional.of(camera));

        assertThrows(IllegalStateException.class, () -> dispositivoService.deletar(5L));
        verify(dispositivoRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Exclui dispositivo sem vínculos e sem leituras")
    void deletarLivre() {
        when(dispositivoRepository.findById(5L)).thenReturn(Optional.of(Fixtures.dispositivo(5L, true)));
        when(leituraRepository.existsByDispositivoId(5L)).thenReturn(false);

        dispositivoService.deletar(5L);

        verify(dispositivoRepository).deleteById(5L);
    }
}
