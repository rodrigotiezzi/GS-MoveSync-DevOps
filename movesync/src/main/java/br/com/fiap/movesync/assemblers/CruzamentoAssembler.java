package br.com.fiap.movesync.assemblers;

import br.com.fiap.movesync.controllers.CruzamentoController;
import br.com.fiap.movesync.controllers.SemaforoController;
import br.com.fiap.movesync.dto.response.CruzamentoResponseDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class CruzamentoAssembler implements RepresentationModelAssembler<CruzamentoResponseDTO, EntityModel<CruzamentoResponseDTO>> {

    @Override
    public EntityModel<CruzamentoResponseDTO> toModel(CruzamentoResponseDTO cruzamento) {
        return EntityModel.of(cruzamento,
                linkTo(methodOn(CruzamentoController.class).buscarPorId(cruzamento.getId())).withSelfRel(),
                linkTo(methodOn(SemaforoController.class).buscarPorCruzamento(cruzamento.getId())).withRel("semaforos"),
                linkTo(CruzamentoController.class).withRel("cruzamentos"));
    }
}
