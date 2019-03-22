package cloud.angst.k8s.kubestatus.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class DeploymentList {
    private List<Deployment> items;
}
