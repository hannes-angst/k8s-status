package cloud.angst.k8s.kubestatus.controller;

import cloud.angst.k8s.kubestatus.config.KubeConfig;
import cloud.angst.k8s.kubestatus.model.Deployment;
import cloud.angst.k8s.kubestatus.model.DeploymentList;
import cloud.angst.k8s.kubestatus.model.Status;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;

@RestController
public class PodController {
    @NonNull
    private final KubeConfig api;
    @NonNull
    private final RestTemplate restTemplate;
    @NonNull
    private final String namespace;

    public PodController(
            @NonNull KubeConfig api,
            @NonNull RestTemplate restTemplate,
            @Value("${app.namespace}")
            @NonNull String namespace) {
        this.api = api;
        this.restTemplate = restTemplate;
        this.namespace = namespace;
    }

    @GetMapping(path = "/health", produces = TEXT_PLAIN_VALUE)
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("ok");
    }


    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/simple", produces = TEXT_PLAIN_VALUE)
    public String getStatus() {

        MultiValueMap<String, String> headerMap = new LinkedMultiValueMap<>();
        headerMap.add("Accept", MediaType.APPLICATION_JSON_VALUE);
        if (api.hasToken()) {
            headerMap.add("Authorization", "Bearer ".concat(api.getToken()));
        }
        HttpEntity<Void> requestEntity = new HttpEntity<>(headerMap);

        return determineStatus(restTemplate
                .exchange(
                        api.getEndpoint().concat("/apis/apps/v1/namespaces/{namespace}/deployments"),
                        HttpMethod.GET,
                        requestEntity,
                        DeploymentList.class, namespace).getBody()).toString();
    }

    private Status determineStatus(DeploymentList replicaSets) {
        if (replicaSets == null) {
            return Status.RED;
        }
        List<Deployment> items = replicaSets.getItems();
        if (items == null || items.isEmpty()) {
            return Status.RED;
        }

        Status result = Status.GREEN;
        for (Deployment item : items) {
            Status status = item.determineStatus();
            if (status == Status.RED) {
                return Status.RED;
            }
            if (status == Status.YELLOW) {
                result = Status.YELLOW;
            }
        }
        return result;
    }
}
