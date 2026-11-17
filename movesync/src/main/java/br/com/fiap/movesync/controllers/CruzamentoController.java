package br.com.fiap.movesync.controllers;

import br.com.fiap.movesync.assemblers.CruzamentoAssembler;
import br.com.fiap.movesync.dto.request.CruzamentoRequestDTO;
import br.com.fiap.movesync.dto.response.CruzamentoResponseDTO;
import br.com.fiap.movesync.services.CruzamentoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Cruzamento", description = "Gerenciamento dos cruzamentos monitorados")
@RestController
@RequestMapping("/cruzamento")
public class CruzamentoController {

    @Autowired
    private CruzamentoService cruzamentoService;

    @Autowired
    private CruzamentoAssembler cruzamentoAssembler;

    @Autowired
    private PagedResourcesAssembler<CruzamentoResponseDTO> pagedAssembler;

    @Operation(summary = "Cria um Cruzamento")
    @PostMapping
    public ResponseEntity<CruzamentoResponseDTO> salvar(@RequestBody @Valid CruzamentoRequestDTO cruzamento) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cruzamentoService.salvar(cruzamento));
    }

    @Operation(summary = "Lista todos os Cruzamentos")
    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<CruzamentoResponseDTO>>> buscarTodos(
            @PageableDefault(size = 10, sort = "nome") Pageable pageable) {
        return ResponseEntity.ok(pagedAssembler.toModel(cruzamentoService.buscarTodos(pageable), cruzamentoAssembler));
    }

    @Operation(summary = "Busca o Cruzamento pelo ID")
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<CruzamentoResponseDTO>> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(cruzamentoAssembler.toModel(cruzamentoService.buscarPorId(id)));
    }

    @Operation(summary = "Busca Cruzamentos pelo Nome (contém)")
    @GetMapping("/nome/{nome}")
    public ResponseEntity<PagedModel<EntityModel<CruzamentoResponseDTO>>> buscarPorNome(
            @PathVariable String nome,
            @PageableDefault(size = 10, sort = "nome") Pageable pageable) {
        return ResponseEntity.ok(pagedAssembler.toModel(cruzamentoService.buscarPorNome(nome, pageable), cruzamentoAssembler));
    }

    @Operation(summary = "Busca Cruzamentos pelo Bairro")
    @GetMapping("/bairro/{bairro}")
    public ResponseEntity<PagedModel<EntityModel<CruzamentoResponseDTO>>> buscarPorBairro(
            @PathVariable String bairro,
            @PageableDefault(size = 10, sort = "nome") Pageable pageable) {
        return ResponseEntity.ok(pagedAssembler.toModel(cruzamentoService.buscarPorBairro(bairro, pageable), cruzamentoAssembler));
    }

    @Operation(summary = "Atualiza um Cruzamento existente")
    @PutMapping("/{id}")
    public ResponseEntity<CruzamentoResponseDTO> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid CruzamentoRequestDTO cruzamento) {
        return ResponseEntity.ok(cruzamentoService.atualizar(id, cruzamento));
    }

    @Operation(summary = "Deleta um Cruzamento pelo ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        cruzamentoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
