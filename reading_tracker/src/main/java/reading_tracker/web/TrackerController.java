package reading_tracker.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TrackerController {

    @GetMapping("/")
    public String home() {
        return "redirect:/tracker";
    }
    
    @GetMapping("/tracker")
    public String main_page() {
        return "Tracker";
    }
}
