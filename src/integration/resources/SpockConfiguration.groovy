import pl.allegro.experiments.chi.chiserver.ui.UiTest

runner {
    if (System.getenv()['EXCLUDE_UI'] == null || Boolean.valueOf(System.getenv()['EXCLUDE_UI'])) {
        exclude UiTest
    }
}