package br.com.fiap.movesync.controllers;

import br.com.fiap.movesync.assemblers.AjusteTempoAssembler;
import br.com.fiap.movesync.dto.response.AjusteTempoResponseDTO;
import br.com.fiap.movesync.services.AjusteTempoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Ajuste de Tempo", description = "Histórico dos ajustes automáticos do tempo de verde")
@RestController
@RequestMapping("/ajuste")
public class AjusteTempoController {

    @Autowired
    private AjusteTempoService ajusteService;

    @Autowired
    private AjusteTempoAssembler ajusteAssembler;

    @Autowired
    private PagedResourcesAssembler<AjusteTempoResponseDTO> pagedAssembler;

    @Operation(summary = "Lista todos os Ajustes (mais recentes primeiro)")
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<AjusteTempoResponseDTO>>> buscarTodos(
            @PageableDefault(size = 10, sort = "dataHora", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(pagedAssembler.toModel(ajusteService.buscarTodos(pageable), ajusteAssembler));
    }

    @Operation(summary = "Busca o Ajuste pelo ID")
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<AjusteTempoResponseDTO>> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ajusteAssembler.toModel(ajusteService.buscarPorId(id)));
    }

    @Operation(summary = "Lista os Ajustes de um Semáforo")
    @GetMapping("/semaforo/{idSemaforo}")
    public ResponseEntity<PagedModel<EntityModel<AjusteTempoResponseDTO>>> buscarPorSemaforo(
            @PathVariable Long idSemaforo,
            @PageableDefault(size = 10, sort = "dataHora", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(pagedAssembler.toModel(ajusteService.buscarPorSemaforo(idSemaforo, pageable), ajusteAssembler));
    }
}
