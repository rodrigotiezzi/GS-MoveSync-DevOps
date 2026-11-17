package br.com.fiap.movesync.controllers;

import br.com.fiap.movesync.assemblers.LeituraTrafegoAssembler;
import br.com.fiap.movesync.dto.request.LeituraTrafegoRequestDTO;
import br.com.fiap.movesync.dto.response.LeituraTrafegoResponseDTO;
import br.com.fiap.movesync.services.LeituraTrafegoService;
import br.com.fiap.movesync.services.SimuladorIotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Tag(name = "Leitura de Tráfego", description = "Leituras enviadas pelos sensores e câmeras IoT")
@RestController
@RequestMapping("/leitura")
public class LeituraTrafegoController {

    @Autowired
    private LeituraTrafegoService leituraService;

    @Autowired
    private SimuladorIotService simuladorIotService;

    @Autowired
    private LeituraTrafegoAssembler leituraAssembler;

    @Autowired
    private PagedResourcesAssembler<LeituraTrafegoResponseDTO> pagedAssembler;

    @Operation(summary = "Registra uma Leitura de tráfego")
    @PostMapping
    public ResponseEntity<LeituraTrafegoResponseDTO> registrar(@RequestBody @Valid LeituraTrafegoRequestDTO leitura) {
        return ResponseEntity.status(HttpStatus.CREATED).body(leituraService.registrar(leitura));
    }

    @Operation(summary = "Simula leituras FICTÍCIAS dos sensores para todos os semáforos ativos")
    @PostMapping("/simular")
    public ResponseEntity<CollectionModel<EntityModel<LeituraTrafegoResponseDTO>>> simular(
            @RequestParam(defaultValue = "5") int quantidade) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(leituraAssembler.toCollectionModel(simuladorIotService.simular(quantidade)));
    }

    @Operation(summary = "Lista todas as Leituras (mais recentes primeiro)")
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<LeituraTrafegoResponseDTO>>> buscarTodas(
            @PageableDefault(size = 10, sort = "dataHora", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(pagedAssembler.toModel(leituraService.buscarTodas(pageable), leituraAssembler));
    }

    @Operation(summary = "Busca a Leitura pelo ID")
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<LeituraTrafegoResponseDTO>> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(leituraAssembler.toModel(leituraService.buscarPorId(id)));
    }

    @Operation(summary = "Lista as Leituras de um Semáforo")
    @GetMapping("/semaforo/{idSemaforo}")
    public ResponseEntity<PagedModel<EntityModel<LeituraTrafegoResponseDTO>>> buscarPorSemaforo(
            @PathVariable Long idSemaforo,
            @PageableDefault(size = 10, sort = "dataHora", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(pagedAssembler.toModel(leituraService.buscarPorSemaforo(idSemaforo, pageable), leituraAssembler));
    }

    @Operation(summary = "Lista as Leituras enviadas por um Dispositivo")
    @GetMapping("/dispositivo/{idDispositivo}")
    public ResponseEntity<PagedModel<EntityModel<LeituraTrafegoResponseDTO>>> buscarPorDispositivo(
            @PathVariable Long idDispositivo,
            @PageableDefault(size = 10, sort = "dataHora", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(pagedAssembler.toModel(leituraService.buscarPorDispositivo(idDispositivo, pageable), leituraAssembler));
    }

    @Operation(summary = "Lista as Leituras de um período")
    @GetMapping("/periodo")
    public ResponseEntity<CollectionModel<EntityModel<LeituraTrafegoResponseDTO>>> buscarPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {
        return ResponseEntity.ok(leituraAssembler.toCollectionModel(leituraService.buscarPorPeriodo(inicio, fim)));
    }

    @Operation(summary = "Atualiza uma Leitura existente")
    @PutMapping("/{id}")
    public ResponseEntity<LeituraTrafegoResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid LeituraTrafegoRequestDTO leitura) {
        return ResponseEntity.ok(leituraService.atualizar(id, leitura));
    }

    @Operation(summary = "Deleta uma Leitura pelo ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        leituraService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
