package pl.allegro.experiments.chi.chiserver.domain.interactions;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.kristofa.brave.internal.zipkin.internal.moshi.Json;
import com.google.common.base.Preconditions;

import javax.validation.constraints.NotNull;

public class Interaction {
    private final String userId;
    private final String userCmId;
    @NotNull
    private final String experimentId;
    @NotNull
    private final String variantName;
    private final Boolean internal;
    private final String deviceClass;
    @NotNull
    private final Instant interactionDate;
    @JsonIgnore
    private final String appId;

    @JsonCreator
    public Interaction(
            @JsonProperty("userId") String userId,
            @JsonProperty("userCmId") String userCmId,
            @JsonProperty("experimentId") String experimentId,
            @JsonProperty("variantName") String variantName,
            @JsonProperty("internal") Boolean internal,
            @JsonProperty("deviceClass") String deviceClass,
            @JsonProperty("interactionDate") Instant interactionDate) {
        this(userId, userCmId, experimentId, variantName, internal, deviceClass, interactionDate, null);
    }

    public Interaction(
            String userId,
            String userCmId,
            String experimentId,
            String variantName,
            Boolean internal,
            String deviceClass,
            Instant interactionDate,
            String appId) {
        Preconditions.checkNotNull(experimentId);
        Preconditions.checkNotNull(variantName);
        Preconditions.checkNotNull(interactionDate);
        this.userId = userId;
        this.userCmId = userCmId;
        this.experimentId = experimentId;
        this.variantName = variantName;
        this.internal = internal;
        this.deviceClass = deviceClass;
        this.interactionDate = interactionDate;
        this.appId = appId;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserCmId() {
        return userCmId;
    }

    public String getExperimentId() {
        return experimentId;
    }

    public String getVariantName() {
        return variantName;
    }

    public Boolean getInternal() {
        return internal;
    }

    public String getDeviceClass() {
        return deviceClass;
    }

    public Instant getInteractionDate() {
        return interactionDate;
    }

    public String getAppId() {
        return appId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof Interaction)) return false;

        Interaction that = (Interaction) o;

        if (userId != null ? !userId.equals(that.userId) : that.userId != null) return false;
        if (userCmId != null ? !userCmId.equals(that.userCmId) : that.userCmId != null) return false;
        if (!experimentId.equals(that.experimentId)) return false;
        if (!variantName.equals(that.variantName)) return false;
        if (internal != null ? !internal.equals(that.internal) : that.internal != null) return false;
        if (deviceClass != null ? !deviceClass.equals(that.deviceClass) : that.deviceClass != null) return false;
        if (!interactionDate.equals(that.interactionDate)) return false;
        return appId != null ? appId.equals(that.appId) : that.appId == null;
    }

    @Override
    public int hashCode() {
        int result = userId != null ? userId.hashCode() : 0;
        result += (userCmId != null ? userCmId.hashCode() : 0);
        result += experimentId.hashCode();
        result += variantName.hashCode();
        result += (internal != null ? internal.hashCode() : 0);
        result += (deviceClass != null ? deviceClass.hashCode() : 0);
        result += interactionDate.hashCode();
        result += (appId != null ? appId.hashCode() : 0);
        return result;
    }
}
