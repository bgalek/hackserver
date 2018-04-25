package pl.allegro.experiments.chi.chiserver.infrastructure.interactions;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = {"/status/resources"}, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
class StatusResourcesController {
  @GetMapping
  Map<String, Object> getSystemResources() {
    Runtime runtime = Runtime.getRuntime();
    Map<String, Object> systemResources = new HashMap<>();
    systemResources.put("cpu", runtime.availableProcessors());
    systemResources.put("mem", runtime.maxMemory());
    return systemResources;
  }
}
