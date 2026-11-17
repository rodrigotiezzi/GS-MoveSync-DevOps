package br.com.fiap.movesync.controllers.web;

import br.com.fiap.movesync.dto.request.SemaforoRequestDTO;
import br.com.fiap.movesync.dto.response.DispositivoResponseDTO;
import br.com.fiap.movesync.dto.response.SemaforoResponseDTO;
import br.com.fiap.movesync.enums.StatusSemaforo;
import br.com.fiap.movesync.enums.TipoSemaforo;
import br.com.fiap.movesync.services.CruzamentoService;
import br.com.fiap.movesync.services.DispositivoService;
import br.com.fiap.movesync.services.SemaforoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/web/semaforo")
public class WebSemaforoController {

    @Autowired
    private SemaforoService semaforoService;

    @Autowired
    private CruzamentoService cruzamentoService;

    @Autowired
    private DispositivoService dispositivoService;

    @GetMapping
    public String listar(Model model, @RequestParam(defaultValue = "0") int pagina) {
        Page<SemaforoResponseDTO> semaforos = semaforoService.buscarTodos(PageRequest.of(pagina, 10, Sort.by("codigo")));
        // Sensores e câmeras de cada semáforo da página (aparecem ao clicar na linha)
        Map<Long, List<DispositivoResponseDTO>> dispositivosPorSemaforo = new HashMap<>();
        semaforos.getContent().forEach(s -> dispositivosPorSemaforo.put(s.getId(), dispositivoService.buscarPorSemaforo(s.getId())));

        model.addAttribute("semaforos", semaforos);
        model.addAttribute("dispositivosPorSemaforo", dispositivosPorSemaforo);
        return "semaforo/listar";
    }

    @GetMapping("/novo")
    public String novoForm(Model model) {
        SemaforoRequestDTO dto = new SemaforoRequestDTO();
        dto.setTipo(TipoSemaforo.VEICULAR);
        dto.setStatus(StatusSemaforo.ATIVO);
        dto.setTempoVerde(30);
        dto.setTempoAmarelo(4);
        dto.setTempoVermelho(30);

        model.addAttribute("semaforo", dto);
        carregarOpcoes(model, false, null);
        return "semaforo/form";
    }

    @PostMapping
    public String salvar(@Valid @ModelAttribute("semaforo") SemaforoRequestDTO dto, BindingResult result, Model model) {
        if (result.hasErrors()) {
            carregarOpcoes(model, false, null);
            return "semaforo/form";
        }
        semaforoService.salvar(dto);
        return "redirect:/web/semaforo";
    }

    @GetMapping("/{id}/editar")
    public String editarForm(@PathVariable Long id, Model model) {
        var semaforo = semaforoService.buscarPorId(id);

        SemaforoRequestDTO dto = new SemaforoRequestDTO();
        dto.setCodigo(semaforo.getCodigo());
        dto.setTipo(semaforo.getTipo());
        dto.setStatus(semaforo.getStatus());
        dto.setTempoVerde(semaforo.getTempoVerde());
        dto.setTempoAmarelo(semaforo.getTempoAmarelo());
        dto.setTempoVermelho(semaforo.getTempoVermelho());
        dto.setIdCruzamento(semaforo.getIdCruzamento());

        model.addAttribute("semaforo", dto);
        carregarOpcoes(model, true, id);
        return "semaforo/form";
    }

    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id, @Valid @ModelAttribute("semaforo") SemaforoRequestDTO dto,
                            BindingResult result, Model model) {
        if (result.hasErrors()) {
            carregarOpcoes(model, true, id);
            return "semaforo/form";
        }
        semaforoService.atualizar(id, dto);
        return "redirect:/web/semaforo";
    }

    @PostMapping("/{id}/deletar")
    public String deletar(@PathVariable Long id) {
        semaforoService.deletar(id);
        return "redirect:/web/semaforo";
    }

    @PostMapping("/{idSemaforo}/dispositivo")
    public String vincularDispositivo(@PathVariable Long idSemaforo, @RequestParam Long idDispositivo) {
        semaforoService.vincularDispositivo(idSemaforo, idDispositivo);
        return "redirect:/web/semaforo/" + idSemaforo + "/editar";
    }

    @PostMapping("/{idSemaforo}/dispositivo/{idDispositivo}/remover")
    public String desvincularDispositivo(@PathVariable Long idSemaforo, @PathVariable Long idDispositivo) {
        semaforoService.desvincularDispositivo(idSemaforo, idDispositivo);
        return "redirect:/web/semaforo/" + idSemaforo + "/editar";
    }

    private void carregarOpcoes(Model model, boolean modoEdicao, Long id) {
        model.addAttribute("modoEdicao", modoEdicao);
        model.addAttribute("id", id);
        model.addAttribute("tipos", TipoSemaforo.values());
        model.addAttribute("statusOpcoes", StatusSemaforo.values());
        model.addAttribute("cruzamentos", cruzamentoService.buscarTodos(PageRequest.of(0, 200, Sort.by("nome"))).getContent());

        // Vínculo com sensores/câmeras: só faz sentido depois que o semáforo já existe
        if (modoEdicao) {
            List<DispositivoResponseDTO> vinculados = dispositivoService.buscarPorSemaforo(id);
            List<Long> idsVinculados = vinculados.stream().map(DispositivoResponseDTO::getId).toList();

            List<DispositivoResponseDTO> disponiveis = dispositivoService.buscarTodos(PageRequest.of(0, 200, Sort.by("codigo")))
                    .getContent().stream()
                    .filter(d -> !idsVinculados.contains(d.getId()))
                    .toList();

            model.addAttribute("dispositivosVinculados", vinculados);
            model.addAttribute("dispositivosDisponiveis", disponiveis);
            model.addAttribute("podeVincular", vinculados.size() < SemaforoService.LIMITE_DISPOSITIVOS && !disponiveis.isEmpty());
            model.addAttribute("limiteDispositivos", SemaforoService.LIMITE_DISPOSITIVOS);
        }
    }
}
