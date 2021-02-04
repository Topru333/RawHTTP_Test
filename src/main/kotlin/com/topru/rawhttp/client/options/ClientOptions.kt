package com.topru.rawhttp.client.options

import rawhttp.core.RawHttpRequest
import rawhttp.core.RawHttpResponse
import rawhttp.core.client.TcpRawHttpClient
import java.net.Socket
import java.net.URI

internal class ClientOptions(private val https: Boolean, private val onRequest: (RawHttpRequest) -> Unit) : TcpRawHttpClient.DefaultOptions() {
    override fun onRequest(httpRequest: RawHttpRequest): RawHttpRequest {
        val request = httpRequest.eagerly()
        println("Send request $request")
        request.runCatching(onRequest).onFailure { println("Error ${it.message}") }
        return super.onRequest(httpRequest)
    }

    override fun onResponse(socket: Socket, uri: URI, httpResponse: RawHttpResponse<Void>): RawHttpResponse<Void> {
        val responce = httpResponse.eagerly()
        println("Resive responce $responce")
        return super.onResponse(socket, uri, responce)
    }

    override fun createSocket(useHttps: Boolean, host: String?, port: Int): Socket {
        return super.createSocket(https, host, port)
    }
}