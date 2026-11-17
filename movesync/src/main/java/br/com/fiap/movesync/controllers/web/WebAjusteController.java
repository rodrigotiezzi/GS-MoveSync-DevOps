package br.com.fiap.movesync.controllers.web;

import br.com.fiap.movesync.dto.response.AjusteTempoResponseDTO;
import br.com.fiap.movesync.enums.StatusSemaforo;
import br.com.fiap.movesync.services.AjusteTempoService;
import br.com.fiap.movesync.services.SemaforoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/web/ajuste")
public class WebAjusteController {

    @Autowired
    private AjusteTempoService ajusteService;

    @Autowired
    private SemaforoService semaforoService;

    @GetMapping
    public String listar(Model model, @RequestParam(defaultValue = "0") int pagina) {
        Page<AjusteTempoResponseDTO> ajustes = ajusteService.buscarTodos(
                PageRequest.of(pagina, 10, Sort.by(Sort.Direction.DESC, "dataHora")));

        model.addAttribute("ajustes", ajustes);
        // Só semáforos ATIVOS aparecem para ajuste
        model.addAttribute("semaforosAtivos", semaforoService.buscarPorStatus(StatusSemaforo.ATIVO,
                PageRequest.of(0, 200, Sort.by("codigo"))).getContent());
        return "ajuste/listar";
    }

    @PostMapping
    public String ajustar(@RequestParam Long idSemaforo, RedirectAttributes redirect) {
        AjusteTempoResponseDTO ajuste = ajusteService.ajustar(idSemaforo);
        redirect.addFlashAttribute("mensagemOk", "Semáforo " + ajuste.getCodigoSemaforo() + ": verde de "
                + ajuste.getTempoVerdeAnterior() + "s para " + ajuste.getTempoVerdeNovo() + "s.");
        return "redirect:/web/ajuste";
    }
}
