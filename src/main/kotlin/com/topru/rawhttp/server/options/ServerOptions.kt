package com.topru.rawhttp.server.options

import rawhttp.core.EagerHttpResponse
import rawhttp.core.RawHttpRequest
import rawhttp.core.RawHttpResponse
import rawhttp.core.server.TcpRawHttpServer
import java.io.IOException
import java.net.ServerSocket
import java.util.*

internal class ServerOptions(
    val port: Int,
    private val onResponseOpt: (RawHttpRequest, RawHttpResponse<*>) -> Unit)
    : TcpRawHttpServer.TcpRawHttpServerOptions {

    @Throws(IOException::class)
    override fun onResponse(request: RawHttpRequest, response: RawHttpResponse<Void>): RawHttpResponse<Void> {
        response.runCatching {
            onResponseOpt(request, response)
        }.onFailure {
            println("Failed to execute onResponseOpt hook $it")
            it.printStackTrace()
        }
        return super.onResponse(request, response)
    }

    override fun getServerSocket(): ServerSocket {
        return ServerSocket(port)
    }

    override fun serverErrorResponse(request: RawHttpRequest?): Optional<EagerHttpResponse<Void?>?>? {
        return super.serverErrorResponse(request) // use the default 500 error response
    }

    override fun notFoundResponse(request: RawHttpRequest?): Optional<EagerHttpResponse<Void?>?>? {
        return super.notFoundResponse(request) // use the default 404 error response
    }
}