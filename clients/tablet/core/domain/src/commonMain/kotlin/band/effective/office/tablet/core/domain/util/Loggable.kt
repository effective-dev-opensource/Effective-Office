package band.effective.office.tablet.core.domain.util

import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

interface Loggable {
    val loggableCoroutineScope: CoroutineScope?

    fun <T> logOperation(
        operationName: String,
        params: String = "",
        callerClass: KClass<*> = this::class,
        resultMessage: (T) -> String = { result ->
            if (result == Unit) "" else "result=$result"
        },
        block: () -> T
    ): T {
        val className = callerClass.simpleName ?: "UnknownClass"
        Napier.d("Starting $operationName in $className${if (params.isNotEmpty()) ": $params" else ""}")
        return block().also { result ->
            val message = resultMessage(result)
            Napier.d(
                if (message.isEmpty()) "$operationName in $className succeeded"
                else "$operationName in $className completed: $message"
            )
        }
    }

    suspend fun <T> logSuspendOperation(
        operationName: String,
        params: String = "",
        callerClass: KClass<*> = this::class,
        resultMessage: (T) -> String = { result ->
            if (result == Unit) "" else "result=$result"
        },
        block: suspend () -> T
    ): T {
        val className = callerClass.simpleName ?: "UnknownClass"
        Napier.d("Starting $operationName in $className${if (params.isNotEmpty()) ": $params" else ""}")
        return block().also { result ->
            val message = resultMessage(result)
            Napier.d(
                if (message.isEmpty()) "$operationName in $className succeeded"
                else "$operationName in $className completed: $message"
            )
        }
    }

    fun logAsyncOperation(
        operationName: String,
        params: String = "",
        callerClass: KClass<*> = this::class,
        onError: (Throwable) -> Unit = {},
        block: suspend () -> Unit
    ) {
        val className = callerClass.simpleName ?: "UnknownClass"
        loggableCoroutineScope?.launch {
            Napier.d("Starting $operationName in $className${if (params.isNotEmpty()) ": $params" else ""}")
            try {
                block()
                Napier.i("$operationName in $className succeeded")
            } catch (e: Exception) {
                Napier.e("$operationName in $className failed: ${e.message}", e)
                onError(e)
            }
        } ?: Napier.w("Coroutine scope is null, cannot execute $operationName in $className")
    }

    suspend fun <T> logSuspendOperationWithError(
        operationName: String,
        params: String = "",
        callerClass: KClass<*> = this::class,
        onError: (Throwable) -> Unit = {},
        block: suspend () -> T
    ): T? {
        val className = callerClass.simpleName ?: "UnknownClass"
        Napier.d("Starting $operationName in $className${if (params.isNotEmpty()) ": $params" else ""}")
        return try {
            block().also { result ->
                val message = if (result == Unit) "" else "result=$result"
                Napier.i(
                    if (message.isEmpty()) "$operationName in $className succeeded"
                    else "$operationName in $className succeeded: $message"
                )
            }
        } catch (e: Exception) {
            Napier.e("$operationName in $className failed: ${e.message}", e)
            onError(e)
            null
        }
    }
}