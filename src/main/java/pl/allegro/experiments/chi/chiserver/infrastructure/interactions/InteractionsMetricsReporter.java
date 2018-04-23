package pl.allegro.experiments.chi.chiserver.infrastructure.interactions;

import io.micrometer.core.instrument.MeterRegistry;
import com.github.slugify.Slugify;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import pl.allegro.experiments.chi.chiserver.domain.interactions.Interaction;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class InteractionsMetricsReporter {
    private final MeterRegistry meterRegistry;
    private final Slugify slugify;
    private final LoadingCache slugCache;
    private final static String RECEIVED_INTERACTIONS = "interactions.received";
    private final static String IGNORED_INTERACTIONS = "interactions.ignored";

    public InteractionsMetricsReporter(MeterRegistry metricRegistry) {
        this.meterRegistry = metricRegistry;
        this.slugify = new Slugify();
        this.slugCache = CacheBuilder.newBuilder()
                .maximumSize(1000)
                .build(new CacheLoader<String, String>() {
                    @Override
                    public String load(String text) throws Exception {
                        return slugify.slugify(text);
                    }
                });
    }

    public void meterIgnored(int ignored) {
        if (ignored > 0) {
            meterRegistry.counter(IGNORED_INTERACTIONS).increment(ignored);
        }
    }

    public void meterReceived(List<Interaction> interactions) {
        meterRegistry.counter(RECEIVED_INTERACTIONS + ".all").increment(interactions.size());
        interactions.forEach(it -> {
            String appId = it.getAppId();
            try {
                appId = (String)slugCache.get(appId != null ? appId : "");
                String experiment = (String)slugCache.get(it.getExperimentId() + "-" + it.getVariantName());
                meterRegistry.counter(RECEIVED_INTERACTIONS + "." + appId + "." + experiment).increment();
            } catch (ExecutionException e) {}
        });
    }
}
