package pl.allegro.experiments.chi.chiserver

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.reflect.full.companionObject

fun <R : Any> R.logger(): Lazy<Logger> = lazy { LoggerFactory.getLogger(unwrapCompanionClass(this.javaClass).name) }

fun <T: Any> unwrapCompanionClass(ofClass: Class<T>): Class<*> =
    if (ofClass.enclosingClass?.kotlin?.companionObject?.java == ofClass) {
        ofClass.enclosingClass
    } else {
        ofClass
    }
