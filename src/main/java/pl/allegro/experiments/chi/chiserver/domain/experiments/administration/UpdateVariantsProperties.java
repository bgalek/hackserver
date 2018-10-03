package pl.allegro.experiments.chi.chiserver.domain.experiments.administration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class UpdateVariantsProperties {
    private final String internalVariantName;
    private final Integer percentage;
    private final String deviceClass;

    @JsonCreator
    public UpdateVariantsProperties(@JsonProperty("internalVariantName") String internalVariantName,
                                    @JsonProperty("percentage") int percentage,
                                    @JsonProperty("deviceClass") String deviceClass) {
        this.internalVariantName = internalVariantName;
        this.percentage = percentage;
        this.deviceClass = deviceClass;
    }

    public int getPercentage() {
        return this.percentage;
    }

    public Optional<String> getInternalVariantName() {
        return Optional.ofNullable(internalVariantName);
    }

    public Optional<String> getDeviceClass() {
        return Optional.ofNullable(deviceClass);
    }

    @Override
    public String toString() {
        return "UpdateVariantsProperties{" +
                ", internalVariantName='" + internalVariantName + '\'' +
                ", percentage=" + percentage +
                ", deviceClass='" + deviceClass + '\'' +
                '}';
    }
}
