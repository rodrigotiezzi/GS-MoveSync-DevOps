package br.com.fiap.movesync.assemblers;

import br.com.fiap.movesync.controllers.AjusteTempoController;
import br.com.fiap.movesync.controllers.SemaforoController;
import br.com.fiap.movesync.dto.response.AjusteTempoResponseDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class AjusteTempoAssembler implements RepresentationModelAssembler<AjusteTempoResponseDTO, EntityModel<AjusteTempoResponseDTO>> {

    @Override
    public EntityModel<AjusteTempoResponseDTO> toModel(AjusteTempoResponseDTO ajuste) {
        return EntityModel.of(ajuste,
                linkTo(methodOn(AjusteTempoController.class).buscarPorId(ajuste.getId())).withSelfRel(),
                linkTo(methodOn(SemaforoController.class).buscarPorId(ajuste.getIdSemaforo())).withRel("semaforo"),
                linkTo(AjusteTempoController.class).withRel("ajustes"));
    }
}
