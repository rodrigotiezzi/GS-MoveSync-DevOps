package br.com.fiap.movesync.controllers.web;

import br.com.fiap.movesync.enums.StatusSemaforo;
import br.com.fiap.movesync.services.AjusteTempoService;
import br.com.fiap.movesync.services.CruzamentoService;
import br.com.fiap.movesync.services.DispositivoService;
import br.com.fiap.movesync.services.LeituraTrafegoService;
import br.com.fiap.movesync.services.SemaforoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebHomeController {

    @Autowired
    private CruzamentoService cruzamentoService;

    @Autowired
    private SemaforoService semaforoService;

    @Autowired
    private DispositivoService dispositivoService;

    @Autowired
    private LeituraTrafegoService leituraService;

    @Autowired
    private AjusteTempoService ajusteService;

    @GetMapping("/")
    public String raiz() {
        return "redirect:/web";
    }

    @GetMapping("/web")
    public String inicio(Model model) {
        var umaLinha = PageRequest.of(0, 1);

        model.addAttribute("totalCruzamentos", cruzamentoService.buscarTodos(umaLinha).getTotalElements());
        model.addAttribute("totalSemaforos", semaforoService.buscarTodos(umaLinha).getTotalElements());
        model.addAttribute("semaforosAtivos", semaforoService.contarPorStatus(StatusSemaforo.ATIVO));
        model.addAttribute("semaforosManutencao", semaforoService.contarPorStatus(StatusSemaforo.MANUTENCAO));
        model.addAttribute("totalDispositivos", dispositivoService.buscarTodos(umaLinha).getTotalElements());
        model.addAttribute("totalLeituras", leituraService.buscarTodas(umaLinha).getTotalElements());

        model.addAttribute("ultimosAjustes", ajusteService.buscarTodos(
                PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "dataHora"))).getContent());
        return "home";
    }
}
