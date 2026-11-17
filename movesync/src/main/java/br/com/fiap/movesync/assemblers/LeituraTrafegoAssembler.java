package br.com.fiap.movesync.assemblers;

import br.com.fiap.movesync.controllers.DispositivoController;
import br.com.fiap.movesync.controllers.LeituraTrafegoController;
import br.com.fiap.movesync.controllers.SemaforoController;
import br.com.fiap.movesync.dto.response.LeituraTrafegoResponseDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class LeituraTrafegoAssembler implements RepresentationModelAssembler<LeituraTrafegoResponseDTO, EntityModel<LeituraTrafegoResponseDTO>> {

    @Override
    public EntityModel<LeituraTrafegoResponseDTO> toModel(LeituraTrafegoResponseDTO leitura) {
        return EntityModel.of(leitura,
                linkTo(methodOn(LeituraTrafegoController.class).buscarPorId(leitura.getId())).withSelfRel(),
                linkTo(methodOn(SemaforoController.class).buscarPorId(leitura.getIdSemaforo())).withRel("semaforo"),
                linkTo(methodOn(DispositivoController.class).buscarPorId(leitura.getIdDispositivo())).withRel("dispositivo"),
                linkTo(LeituraTrafegoController.class).withRel("leituras"));
    }
}
