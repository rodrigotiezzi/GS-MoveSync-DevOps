package br.com.fiap.movesync.controllers.web;

import br.com.fiap.movesync.dto.request.LeituraTrafegoRequestDTO;
import br.com.fiap.movesync.dto.response.LeituraTrafegoResponseDTO;
import br.com.fiap.movesync.services.DispositivoService;
import br.com.fiap.movesync.services.LeituraTrafegoService;
import br.com.fiap.movesync.services.SemaforoService;
import br.com.fiap.movesync.services.SimuladorIotService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/web/leitura")
public class WebLeituraController {

    @Autowired
    private LeituraTrafegoService leituraService;

    @Autowired
    private SemaforoService semaforoService;

    @Autowired
    private DispositivoService dispositivoService;

    @Autowired
    private SimuladorIotService simuladorIotService;

    @GetMapping
    public String listar(Model model, @RequestParam(defaultValue = "0") int pagina) {
        Page<LeituraTrafegoResponseDTO> leituras = leituraService.buscarTodas(
                PageRequest.of(pagina, 10, Sort.by(Sort.Direction.DESC, "dataHora")));
        model.addAttribute("leituras", leituras);
        return "leitura/listar";
    }

    @GetMapping("/novo")
    public String novoForm(Model model) {
        model.addAttribute("leitura", new LeituraTrafegoRequestDTO());
        carregarOpcoes(model, false, null);
        return "leitura/form";
    }

    @PostMapping
    public String salvar(@Valid @ModelAttribute("leitura") LeituraTrafegoRequestDTO dto, BindingResult result, Model model) {
        if (result.hasErrors()) {
            carregarOpcoes(model, false, null);
            return "leitura/form";
        }
        leituraService.registrar(dto);
        return "redirect:/web/leitura";
    }

    // Gera leituras FICTÍCIAS para os semáforos ativos (simula os sensores IoT)
    @PostMapping("/simular")
    public String simular(@RequestParam(defaultValue = "5") int quantidade, RedirectAttributes redirect) {
        int geradas = simuladorIotService.simular(quantidade).size();
        redirect.addFlashAttribute("mensagemOk", geradas + " leituras simuladas foram registradas.");
        return "redirect:/web/leitura";
    }

    @GetMapping("/{id}/editar")
    public String editarForm(@PathVariable Long id, Model model) {
        var leitura = leituraService.buscarPorId(id);

        LeituraTrafegoRequestDTO dto = new LeituraTrafegoRequestDTO();
        dto.setIdSemaforo(leitura.getIdSemaforo());
        dto.setIdDispositivo(leitura.getIdDispositivo());
        dto.setDataHora(leitura.getDataHora());
        dto.setQtdVeiculos(leitura.getQtdVeiculos());
        dto.setQtdPedestres(leitura.getQtdPedestres());
        dto.setVelocidadeMedia(leitura.getVelocidadeMedia());

        model.addAttribute("leitura", dto);
        carregarOpcoes(model, true, id);
        return "leitura/form";
    }

    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id, @Valid @ModelAttribute("leitura") LeituraTrafegoRequestDTO dto,
                            BindingResult result, Model model) {
        if (result.hasErrors()) {
            carregarOpcoes(model, true, id);
            return "leitura/form";
        }
        leituraService.atualizar(id, dto);
        return "redirect:/web/leitura";
    }

    @PostMapping("/{id}/deletar")
    public String deletar(@PathVariable Long id) {
        leituraService.deletar(id);
        return "redirect:/web/leitura";
    }

    private void carregarOpcoes(Model model, boolean modoEdicao, Long id) {
        model.addAttribute("modoEdicao", modoEdicao);
        model.addAttribute("id", id);
        model.addAttribute("semaforos", semaforoService.buscarTodos(PageRequest.of(0, 200, Sort.by("codigo"))).getContent());
        model.addAttribute("dispositivos", dispositivoService.buscarTodos(PageRequest.of(0, 200, Sort.by("codigo"))).getContent());
    }
}
