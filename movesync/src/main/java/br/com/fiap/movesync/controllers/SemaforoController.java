package br.com.fiap.movesync.controllers;

import br.com.fiap.movesync.assemblers.SemaforoAssembler;
import br.com.fiap.movesync.dto.request.SemaforoRequestDTO;
import br.com.fiap.movesync.dto.response.AjusteTempoResponseDTO;
import br.com.fiap.movesync.dto.response.SemaforoResponseDTO;
import br.com.fiap.movesync.enums.StatusSemaforo;
import br.com.fiap.movesync.services.AjusteTempoService;
import br.com.fiap.movesync.services.SemaforoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Semáforo", description = "Gerenciamento dos semáforos inteligentes")
@RestController
@RequestMapping("/semaforo")
public class SemaforoController {

    @Autowired
    private SemaforoService semaforoService;

    @Autowired
    private AjusteTempoService ajusteTempoService;

    @Autowired
    private SemaforoAssembler semaforoAssembler;

    @Autowired
    private PagedResourcesAssembler<SemaforoResponseDTO> pagedAssembler;

    @Operation(summary = "Cria um Semáforo vinculado a um cruzamento")
    @PostMapping
    public ResponseEntity<SemaforoResponseDTO> salvar(@RequestBody @Valid SemaforoRequestDTO semaforo) {
        return ResponseEntity.status(HttpStatus.CREATED).body(semaforoService.salvar(semaforo));
    }

    @Operation(summary = "Lista todos os Semáforos")
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<SemaforoResponseDTO>>> buscarTodos(
            @PageableDefault(size = 10, sort = "codigo") Pageable pageable) {
        return ResponseEntity.ok(pagedAssembler.toModel(semaforoService.buscarTodos(pageable), semaforoAssembler));
    }

    @Operation(summary = "Busca o Semáforo pelo ID")
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<SemaforoResponseDTO>> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(semaforoAssembler.toModel(semaforoService.buscarPorId(id)));
    }

    @Operation(summary = "Busca o Semáforo pelo Código (ex.: SEM-001)")
    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<EntityModel<SemaforoResponseDTO>> buscarPorCodigo(@PathVariable String codigo) {
        return ResponseEntity.ok(semaforoAssembler.toModel(semaforoService.buscarPorCodigo(codigo)));
    }

    @Operation(summary = "Busca Semáforos pelo Status")
    @GetMapping("/status/{status}")
    public ResponseEntity<PagedModel<EntityModel<SemaforoResponseDTO>>> buscarPorStatus(
            @PathVariable StatusSemaforo status,
            @PageableDefault(size = 10, sort = "codigo") Pageable pageable) {
        return ResponseEntity.ok(pagedAssembler.toModel(semaforoService.buscarPorStatus(status, pageable), semaforoAssembler));
    }

    @Operation(summary = "Lista os Semáforos de um Cruzamento")
    @GetMapping("/cruzamento/{idCruzamento}")
    public ResponseEntity<CollectionModel<EntityModel<SemaforoResponseDTO>>> buscarPorCruzamento(@PathVariable Long idCruzamento) {
        return ResponseEntity.ok(semaforoAssembler.toCollectionModel(semaforoService.buscarPorCruzamento(idCruzamento)));
    }

    @Operation(summary = "Atualiza um Semáforo existente")
    @PutMapping("/{id}")
    public ResponseEntity<SemaforoResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid SemaforoRequestDTO semaforo) {
        return ResponseEntity.ok(semaforoService.atualizar(id, semaforo));
    }

    @Operation(summary = "Vincula um Dispositivo (sensor/câmera) ao Semáforo")
    @PostMapping("/{idSemaforo}/dispositivo/{idDispositivo}")
    public ResponseEntity<SemaforoResponseDTO> vincularDispositivo(
            @PathVariable Long idSemaforo,
            @PathVariable Long idDispositivo) {
        return ResponseEntity.ok(semaforoService.vincularDispositivo(idSemaforo, idDispositivo));
    }

    @Operation(summary = "Remove o vínculo do Dispositivo com o Semáforo")
    @DeleteMapping("/{idSemaforo}/dispositivo/{idDispositivo}")
    public ResponseEntity<SemaforoResponseDTO> desvincularDispositivo(
            @PathVariable Long idSemaforo,
            @PathVariable Long idDispositivo) {
        return ResponseEntity.ok(semaforoService.desvincularDispositivo(idSemaforo, idDispositivo));
    }

    @Operation(summary = "Altera o Status do Semáforo (ATIVO, INATIVO, MANUTENCAO)")
    @PatchMapping("/{id}/status/{status}")
    public ResponseEntity<SemaforoResponseDTO> alterarStatus(@PathVariable Long id, @PathVariable StatusSemaforo status) {
        return ResponseEntity.ok(semaforoService.alterarStatus(id, status));
    }

    @Operation(summary = "Ajusta o tempo de verde com base nas últimas leituras de tráfego")
    @PostMapping("/{id}/ajustar-tempo")
    public ResponseEntity<AjusteTempoResponseDTO> ajustarTempo(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ajusteTempoService.ajustar(id));
    }

    @Operation(summary = "Deleta um Semáforo pelo ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        semaforoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
