package br.com.fiap.movesync.controllers.web;

import br.com.fiap.movesync.dto.request.CruzamentoRequestDTO;
import br.com.fiap.movesync.dto.response.CruzamentoResponseDTO;
import br.com.fiap.movesync.dto.response.SemaforoResponseDTO;
import br.com.fiap.movesync.services.CruzamentoService;
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
@RequestMapping("/web/cruzamento")
public class WebCruzamentoController {

    @Autowired
    private CruzamentoService cruzamentoService;

    @Autowired
    private SemaforoService semaforoService;

    @GetMapping
    public String listar(Model model, @RequestParam(defaultValue = "0") int pagina) {
        Page<CruzamentoResponseDTO> cruzamentos = cruzamentoService.buscarTodos(PageRequest.of(pagina, 10, Sort.by("nome")));

        // Semáforos de cada cruzamento da página (aparecem ao clicar na linha)
        Map<Long, List<SemaforoResponseDTO>> semaforosPorCruzamento = new HashMap<>();
        cruzamentos.getContent().forEach(c -> semaforosPorCruzamento.put(c.getId(), semaforoService.buscarPorCruzamento(c.getId())));

        model.addAttribute("cruzamentos", cruzamentos);
        model.addAttribute("semaforosPorCruzamento", semaforosPorCruzamento);
        return "cruzamento/listar";
    }

    @GetMapping("/novo")
    public String novoForm(Model model) {
        model.addAttribute("cruzamento", new CruzamentoRequestDTO());
        model.addAttribute("modoEdicao", false);
        return "cruzamento/form";
    }

    @PostMapping
    public String salvar(@Valid @ModelAttribute("cruzamento") CruzamentoRequestDTO dto, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("modoEdicao", false);
            return "cruzamento/form";
        }
        cruzamentoService.salvar(dto);
        return "redirect:/web/cruzamento";
    }

    @GetMapping("/{id}/editar")
    public String editarForm(@PathVariable Long id, Model model) {
        var cruzamento = cruzamentoService.buscarPorId(id);

        CruzamentoRequestDTO dto = new CruzamentoRequestDTO();
        dto.setNome(cruzamento.getNome());
        dto.setEndereco(cruzamento.getEndereco());
        dto.setBairro(cruzamento.getBairro());
        dto.setLatitude(cruzamento.getLatitude());
        dto.setLongitude(cruzamento.getLongitude());

        model.addAttribute("cruzamento", dto);
        model.addAttribute("id", id);
        model.addAttribute("modoEdicao", true);
        return "cruzamento/form";
    }

    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id, @Valid @ModelAttribute("cruzamento") CruzamentoRequestDTO dto,
                            BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("id", id);
            model.addAttribute("modoEdicao", true);
            return "cruzamento/form";
        }
        cruzamentoService.atualizar(id, dto);
        return "redirect:/web/cruzamento";
    }

    @PostMapping("/{id}/deletar")
    public String deletar(@PathVariable Long id) {
        cruzamentoService.deletar(id);
        return "redirect:/web/cruzamento";
    }
}
