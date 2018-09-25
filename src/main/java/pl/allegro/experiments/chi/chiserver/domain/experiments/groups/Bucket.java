package pl.allegro.experiments.chi.chiserver.domain.experiments.groups;

class Bucket implements Comparable<Bucket> {
    private final int id;
    private final boolean free;

    Bucket(int id, boolean free) {
        this.id = id;
        this.free = free;
    }

    static Bucket free(int id) {
        return new Bucket(id, true);
    }

    static Bucket taken(int id) {
        return new Bucket(id, false);
    }

    Bucket taken() {
        if (!free) {
            throw new IllegalStateException("This bucket is taken, something is really wrong");
        }
        return taken(id);
    }

    int getId() {
        return id;
    }

    boolean isFree() {
        return free;
    }

    @Override
    public int compareTo(Bucket o) {
        return Integer.valueOf(this.id).compareTo(o.id);
    }
}
