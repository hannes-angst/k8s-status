package cloud.angst.k8s.kubestatus.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DeploymentSpec {
    private int replicas;
}
