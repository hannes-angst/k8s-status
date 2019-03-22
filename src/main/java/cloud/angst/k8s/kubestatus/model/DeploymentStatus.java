package cloud.angst.k8s.kubestatus.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DeploymentStatus {
    private int replicas;
    private int readyReplicas;
    private int availableReplicas;
}
