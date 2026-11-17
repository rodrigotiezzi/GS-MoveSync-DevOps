package br.com.fiap.movesync.controllers.web;

import br.com.fiap.movesync.dto.request.DispositivoRequestDTO;
import br.com.fiap.movesync.dto.response.DispositivoResponseDTO;
import br.com.fiap.movesync.enums.TipoDispositivo;
import br.com.fiap.movesync.services.DispositivoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/web/dispositivo")
public class WebDispositivoController {

    @Autowired
    private DispositivoService dispositivoService;

    @GetMapping
    public String listar(Model model, @RequestParam(defaultValue = "0") int pagina) {
        Page<DispositivoResponseDTO> dispositivos = dispositivoService.buscarTodos(PageRequest.of(pagina, 10, Sort.by("codigo")));
        model.addAttribute("dispositivos", dispositivos);
        return "dispositivo/listar";
    }

    @GetMapping("/novo")
    public String novoForm(Model model) {
        DispositivoRequestDTO dto = new DispositivoRequestDTO();
        dto.setTipo(TipoDispositivo.CAMERA);
        dto.setOnline(true);
        model.addAttribute("dispositivo", dto);
        carregarOpcoes(model, false, null);
        return "dispositivo/form";
    }

    @PostMapping
    public String salvar(@Valid @ModelAttribute("dispositivo") DispositivoRequestDTO dto, BindingResult result, Model model) {
        if (result.hasErrors()) {
            carregarOpcoes(model, false, null);
            return "dispositivo/form";
        }
        dispositivoService.salvar(dto);
        return "redirect:/web/dispositivo";
    }

    @GetMapping("/{id}/editar")
    public String editarForm(@PathVariable Long id, Model model) {
        var dispositivo = dispositivoService.buscarPorId(id);

        DispositivoRequestDTO dto = new DispositivoRequestDTO();
        dto.setCodigo(dispositivo.getCodigo());
        dto.setTipo(dispositivo.getTipo());
        dto.setModelo(dispositivo.getModelo());
        dto.setOnline(dispositivo.getOnline());

        model.addAttribute("dispositivo", dto);
        carregarOpcoes(model, true, id);
        return "dispositivo/form";
    }

    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id, @Valid @ModelAttribute("dispositivo") DispositivoRequestDTO dto,
                            BindingResult result, Model model) {
        if (result.hasErrors()) {
            carregarOpcoes(model, true, id);
            return "dispositivo/form";
        }
        dispositivoService.atualizar(id, dto);
        return "redirect:/web/dispositivo";
    }

    @PostMapping("/{id}/deletar")
    public String deletar(@PathVariable Long id) {
        dispositivoService.deletar(id);
        return "redirect:/web/dispositivo";
    }

    private void carregarOpcoes(Model model, boolean modoEdicao, Long id) {
        model.addAttribute("modoEdicao", modoEdicao);
        model.addAttribute("id", id);
        model.addAttribute("tipos", TipoDispositivo.values());
    }
}
