package com.topru.rawhttp.client

import com.topru.rawhttp.client.options.ClientOptions
import rawhttp.core.RawHttpRequest
import rawhttp.core.RawHttpResponse
import rawhttp.core.client.TcpRawHttpClient
import java.lang.IllegalStateException

class HttpClient(
    https: Boolean,
    private val host: String,
    private val port: Int,
    private val prepareRequest: (RawHttpRequest) -> RawHttpRequest,
    onRequest: (RawHttpRequest) -> Unit,
    private val onResponse: (RawHttpRequest, RawHttpResponse<*>) -> Unit
) : TcpRawHttpClient(ClientOptions(https, onRequest)) {

    val isRuning: Boolean
        get() = !options.executorService.isShutdown

    override fun send(request: RawHttpRequest): RawHttpResponse<Void> {
        var sendRequest = request.run {
            when {
                host != uri.host || port != uri.port -> withRequestLine(startLine.withHost("$host:$port"))
                else -> this
            }
        }

        val preparedRequest = sendRequest.runCatching(prepareRequest).getOrElse {
            throw IllegalStateException("Failed to prepare request: ${request.eagerly()}", it)
        }

        val responce = super.send(preparedRequest)

        responce.runCatching {
            onResponse(preparedRequest, responce)
        }.onFailure {
            println("Failed to execute onResponce hook $it")
            it.printStackTrace()
        }

        return responce

    }


}

