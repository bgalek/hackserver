package pl.allegro.experiments.chi.chiserver.domain.experiments.administration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class UpdateVariantsProperties {
    private final List<String> variantNames;
    private final String internalVariantName;
    private final Integer percentage;
    private final String deviceClass;

    @JsonCreator
    public UpdateVariantsProperties(@JsonProperty("variantNames") List<String> variantNames,
                                    @JsonProperty("internalVariantName") String internalVariantName,
                                    @JsonProperty("percentage") int percentage,
                                    @JsonProperty("deviceClass") String deviceClass) {
        Objects.requireNonNull(variantNames);
        this.variantNames = variantNames;
        this.internalVariantName = internalVariantName;
        this.percentage = percentage;
        this.deviceClass = deviceClass;
    }

    public int getPercentage() {
        return this.percentage;
    }

    public List<String> getVariantNames() {
        return variantNames;
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
                "variantNames=" + variantNames +
                ", internalVariantName='" + internalVariantName + '\'' +
                ", percentage=" + percentage +
                ", deviceClass='" + deviceClass + '\'' +
                '}';
    }
}
