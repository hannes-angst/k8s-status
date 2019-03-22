package cloud.angst.k8s.kubestatus.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Deployment {
    private DeploymentMetaData metadata;
    private DeploymentStatus status;
    private DeploymentSpec spec;

    @JsonIgnore
    public Status determineStatus() {
        if (spec == null)
            return Status.RED;

        if (spec.getReplicas() == 0) {
            //ignore
            return Status.GREEN;
        }

        if (status == null || status.getAvailableReplicas() < 1) {
            return Status.RED;
        }

        if (status.getReplicas() < spec.getReplicas()) {
            return Status.YELLOW;
        }

        if (status.getAvailableReplicas() < spec.getReplicas()) {
            return Status.YELLOW;
        }

        if (status.getReadyReplicas() < spec.getReplicas()) {
            return Status.YELLOW;
        }

        return Status.GREEN;
    }
}
