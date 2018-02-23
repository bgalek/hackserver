package pl.allegro.experiments.chi.chiserver.domain.interactions;

import java.time.Instant;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Preconditions;

public class Interaction {
    private String userId;
    private String userCmId;
    private String experimentId;
    private String variantName;
    private Boolean internal;
    private String deviceClass;
    private Instant interactionDate;
    @JsonIgnore
    private String appId;

    public Interaction() {}

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

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserCmId() {
        return userCmId;
    }

    public void setUserCmId(String userCmId) {
        this.userCmId = userCmId;
    }

    public String getExperimentId() {
        return experimentId;
    }

    public void setExperimentId(String experimentId) {
        this.experimentId = experimentId;
    }

    public String getVariantName() {
        return variantName;
    }

    public void setVariantName(String variantName) {
        this.variantName = variantName;
    }

    public Boolean getInternal() {
        return internal;
    }

    public void setInternal(Boolean internal) {
        this.internal = internal;
    }

    public String getDeviceClass() {
        return deviceClass;
    }

    public void setDeviceClass(String deviceClass) {
        this.deviceClass = deviceClass;
    }

    public Instant getInteractionDate() {
        return interactionDate;
    }

    public void setInteractionDate(Instant interactionDate) {
        this.interactionDate = interactionDate;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

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
        result = 31 * result + (userCmId != null ? userCmId.hashCode() : 0);
        result = 31 * result + experimentId.hashCode();
        result = 31 * result + variantName.hashCode();
        result = 31 * result + (internal != null ? internal.hashCode() : 0);
        result = 31 * result + (deviceClass != null ? deviceClass.hashCode() : 0);
        result = 31 * result + interactionDate.hashCode();
        result = 31 * result + (appId != null ? appId.hashCode() : 0);
        return result;
    }
}
