package br.com.fiap.movesync.assemblers;

import br.com.fiap.movesync.controllers.AjusteTempoController;
import br.com.fiap.movesync.controllers.CruzamentoController;
import br.com.fiap.movesync.controllers.DispositivoController;
import br.com.fiap.movesync.controllers.LeituraTrafegoController;
import br.com.fiap.movesync.controllers.SemaforoController;
import br.com.fiap.movesync.dto.response.SemaforoResponseDTO;
import br.com.fiap.movesync.enums.StatusSemaforo;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class SemaforoAssembler implements RepresentationModelAssembler<SemaforoResponseDTO, EntityModel<SemaforoResponseDTO>> {

    @Override
    public EntityModel<SemaforoResponseDTO> toModel(SemaforoResponseDTO semaforo) {
        EntityModel<SemaforoResponseDTO> model = EntityModel.of(semaforo,
                linkTo(methodOn(SemaforoController.class).buscarPorId(semaforo.getId())).withSelfRel(),
                linkTo(methodOn(CruzamentoController.class).buscarPorId(semaforo.getIdCruzamento())).withRel("cruzamento"),
                linkTo(methodOn(DispositivoController.class).buscarPorSemaforo(semaforo.getId())).withRel("dispositivos"),
                linkTo(methodOn(LeituraTrafegoController.class).buscarPorSemaforo(semaforo.getId(), Pageable.unpaged())).withRel("leituras"),
                linkTo(methodOn(AjusteTempoController.class).buscarPorSemaforo(semaforo.getId(), Pageable.unpaged())).withRel("ajustes"),
                linkTo(SemaforoController.class).withRel("semaforos"));

        // Ação disponível depende do estado: só semáforo ATIVO pode ter o tempo ajustado
        if (semaforo.getStatus() == StatusSemaforo.ATIVO) {
            model.add(linkTo(methodOn(SemaforoController.class).ajustarTempo(semaforo.getId())).withRel("ajustar-tempo"));
        }

        return model;
    }
}
