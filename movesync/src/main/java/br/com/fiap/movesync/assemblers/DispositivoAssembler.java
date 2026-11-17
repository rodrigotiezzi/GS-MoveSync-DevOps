package br.com.fiap.movesync.assemblers;

import br.com.fiap.movesync.controllers.DispositivoController;
import br.com.fiap.movesync.controllers.LeituraTrafegoController;
import br.com.fiap.movesync.dto.response.DispositivoResponseDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class DispositivoAssembler implements RepresentationModelAssembler<DispositivoResponseDTO, EntityModel<DispositivoResponseDTO>> {

    @Override
    public EntityModel<DispositivoResponseDTO> toModel(DispositivoResponseDTO dispositivo) {
        return EntityModel.of(dispositivo,
                linkTo(methodOn(DispositivoController.class).buscarPorId(dispositivo.getId())).withSelfRel(),
                linkTo(methodOn(LeituraTrafegoController.class).buscarPorDispositivo(dispositivo.getId(), Pageable.unpaged())).withRel("leituras"),
                linkTo(DispositivoController.class).withRel("dispositivos"));
    }
}
