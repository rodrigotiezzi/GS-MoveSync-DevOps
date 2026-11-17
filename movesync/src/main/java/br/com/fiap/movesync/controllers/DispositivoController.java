package br.com.fiap.movesync.controllers;

import br.com.fiap.movesync.assemblers.DispositivoAssembler;
import br.com.fiap.movesync.dto.request.DispositivoRequestDTO;
import br.com.fiap.movesync.dto.response.DispositivoResponseDTO;
import br.com.fiap.movesync.enums.TipoDispositivo;
import br.com.fiap.movesync.services.DispositivoService;
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

@Tag(name = "Dispositivo IoT", description = "Gerenciamento dos sensores e câmeras (dados simulados)")
@RestController
@RequestMapping("/dispositivo")
public class DispositivoController {

    @Autowired
    private DispositivoService dispositivoService;

    @Autowired
    private DispositivoAssembler dispositivoAssembler;

    @Autowired
    private PagedResourcesAssembler<DispositivoResponseDTO> pagedAssembler;

    @Operation(summary = "Cria um Dispositivo (sensor ou câmera)")
    @PostMapping
    public ResponseEntity<DispositivoResponseDTO> salvar(@RequestBody @Valid DispositivoRequestDTO dispositivo) {
        return ResponseEntity.status(HttpStatus.CREATED).body(dispositivoService.salvar(dispositivo));
    }

    @Operation(summary = "Lista todos os Dispositivos")
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<DispositivoResponseDTO>>> buscarTodos(
            @PageableDefault(size = 10, sort = "codigo") Pageable pageable) {
        return ResponseEntity.ok(pagedAssembler.toModel(dispositivoService.buscarTodos(pageable), dispositivoAssembler));
    }

    @Operation(summary = "Busca o Dispositivo pelo ID")
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<DispositivoResponseDTO>> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(dispositivoAssembler.toModel(dispositivoService.buscarPorId(id)));
    }

    @Operation(summary = "Busca Dispositivos pelo Tipo")
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<PagedModel<EntityModel<DispositivoResponseDTO>>> buscarPorTipo(
            @PathVariable TipoDispositivo tipo,
            @PageableDefault(size = 10, sort = "codigo") Pageable pageable) {
        return ResponseEntity.ok(pagedAssembler.toModel(dispositivoService.buscarPorTipo(tipo, pageable), dispositivoAssembler));
    }

    @Operation(summary = "Lista os Dispositivos de um Semáforo")
    @GetMapping("/semaforo/{idSemaforo}")
    public ResponseEntity<CollectionModel<EntityModel<DispositivoResponseDTO>>> buscarPorSemaforo(@PathVariable Long idSemaforo) {
        return ResponseEntity.ok(dispositivoAssembler.toCollectionModel(dispositivoService.buscarPorSemaforo(idSemaforo)));
    }

    @Operation(summary = "Atualiza um Dispositivo existente")
    @PutMapping("/{id}")
    public ResponseEntity<DispositivoResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid DispositivoRequestDTO dispositivo) {
        return ResponseEntity.ok(dispositivoService.atualizar(id, dispositivo));
    }

    @Operation(summary = "Deleta um Dispositivo pelo ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        dispositivoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
