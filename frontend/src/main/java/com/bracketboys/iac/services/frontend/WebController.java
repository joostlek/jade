package com.bracketboys.iac.services.frontend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class WebController {

    @Autowired
    private FrontendServiceInterface frontendService;

    @GetMapping("/convert")
    public String convertForm(Model model) {
        model.addAttribute("link", new LinkDTO());
        return "convertForm";
    }

    @PostMapping("/convert")
    public String convertYoutubeLink(@ModelAttribute LinkDTO link) {
        frontendService.convertYoutubeLink(link.getLink());
        return "convert";
    }

}
