package com.manojmourya.weathernow.data.remote.converter

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

/**
 * Minimal in-house Retrofit [Converter.Factory] bridging kotlinx.serialization
 * for this app's `@Serializable` DTOs.
 *
 * Written in-house rather than relying on a published third-party converter
 * (both `com.squareup.retrofit2:converter-kotlinx-serialization` and
 * `com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter`) because,
 * at the exact versions compatible with this project's toolchain, their public
 * entry points (`Factory`, `Serializer`) are declared `internal` in Kotlin and
 * are unreachable from application call sites despite being public at the JVM
 * bytecode level.
 *
 * This factory only needs to support plain (non-generic) `@Serializable`
 * classes, which is all this app's API services return. Every class compiled
 * by the kotlinx.serialization compiler plugin gets a generated `Companion`
 * object exposing a no-arg `serializer()` method, which we can reach with
 * plain reflection (no kotlin-reflect dependency required).
 */
class JsonConverterFactory(
    private val json: Json,
    private val contentType: MediaType,
) : Converter.Factory() {

    override fun responseBodyConverter(
        type: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit,
    ): Converter<ResponseBody, *> = Converter<ResponseBody, Any?> { body ->
        @Suppress("UNCHECKED_CAST")
        val serializer = serializerFor(type) as KSerializer<Any?>
        body.use { responseBody -> json.decodeFromString(serializer, responseBody.string()) }
    }

    override fun requestBodyConverter(
        type: Type,
        parameterAnnotations: Array<out Annotation>,
        methodAnnotations: Array<out Annotation>,
        retrofit: Retrofit,
    ): Converter<Any?, RequestBody> = Converter<Any?, RequestBody> { value ->
        @Suppress("UNCHECKED_CAST")
        val serializer = serializerFor(type) as KSerializer<Any?>
        json.encodeToString(serializer, value).toRequestBody(contentType)
    }

    private fun serializerFor(type: Type): KSerializer<*> {
        val kClass = type as? Class<*>
            ?: throw IllegalArgumentException("JsonConverterFactory only supports plain classes, got: $type")
        val companionField = kClass.getDeclaredField("Companion")
        val companion = companionField.get(null)
        val serializerMethod = companion.javaClass.getMethod("serializer")
        return serializerMethod.invoke(companion) as KSerializer<*>
    }
}
