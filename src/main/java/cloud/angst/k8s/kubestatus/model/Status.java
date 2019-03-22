package cloud.angst.k8s.kubestatus.model;

import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.lang.NonNull;

public enum Status {
    GREEN("green"),
    YELLOW("yellow"),
    RED("red");

    private final String value;

    Status(String value) {
        this.value = value;
    }

    @NonNull
    @Override
    @JsonValue
    public String toString() {
        return value;
    }
}
