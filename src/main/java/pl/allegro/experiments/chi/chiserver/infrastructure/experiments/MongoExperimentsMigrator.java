package pl.allegro.experiments.chi.chiserver.infrastructure.experiments;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MongoExperimentsMigrator {
    private static final String DEFINITIONS_COLLECTION = "experimentDefinitions";
    private static final String EXPERIMENTS_COLLECTION = "experiments";

    private final MongoTemplate mongoTemplate;

    public MongoExperimentsMigrator(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public String migrateAll() {
        List<Map> all = mongoTemplate.findAll(Map.class, EXPERIMENTS_COLLECTION);

        if (mongoTemplate.findAll(Map.class, DEFINITIONS_COLLECTION).size() > 0) {
            throw new RuntimeException("Cannot migrate as definitions exists");
        }


        List<Map> collect = all.stream().map(this::migrate).collect(Collectors.toList());

        collect.forEach(e -> {
            mongoTemplate.save(e, DEFINITIONS_COLLECTION);
        });

        return "Successfully migrated " + collect.size() + " experiments ";
    }

    private Map migrate(Map e) {
        List variants = (List)e.get("variants");
        Optional<Map> internal = variants.stream().filter(v -> findPredicate((Map) v, "INTERNAL").isPresent()).findAny();
        String internalName = (String)(internal.map(m -> m.get("name")).orElse(null));
        Optional<Map> hash = variants.stream().map(v -> findPredicate((Map) v, "HASH").orElse(null)).filter(x -> x!= null).findAny();
        Optional<Map> deviceClass = variants.stream().map(v -> findPredicate((Map) v, "DEVICE_CLASS").orElse(null)).filter(x -> x!= null).findAny();

        List names = (List)variants.stream()
                .map(v -> ((Map) v).get("name"))
                .filter(v -> !v.equals(internalName))
                .collect(Collectors.toList());

        e.put("variantNames", names);
        internal.ifPresent( i -> {
            e.put("internalVariantName", i.get("name"));
        });
        deviceClass.ifPresent(dc -> {
            e.put("deviceClass", dc.get("device"));
        });
        hash.ifPresent(h -> {
            int from = (Integer)h.get("from");
            int to = (Integer)h.get("to");
            int percentage = to - from;
            int maxp = 100 / names.size();
            if (percentage > maxp) {
                throw new RuntimeException("Percentage is too high, " + percentage + " > " + maxp);
            }
            e.put("percentage", percentage);
        });
        e.remove("variants");
        return e;
    }

    private Optional<Map> findPredicate(Map variant, String type) {
        return ((List)variant.get("predicates"))
                .stream()
                .filter(x -> ((Map)x).get("type").equals(type))
                .findAny();
    }
}
