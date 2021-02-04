package com.topru.rawhttp.server

import com.topru.rawhttp.server.options.ServerOptions
import rawhttp.core.RawHttpRequest
import rawhttp.core.RawHttpResponse
import rawhttp.core.server.Router
import rawhttp.core.server.TcpRawHttpServer

class HttpServer(private val port: Int, onResponse: (RawHttpRequest, RawHttpResponse<*>) -> Unit) : TcpRawHttpServer(ServerOptions(port, onResponse)) {
    override fun start(router: Router) {
        super.start(router)
        println("Server started on port $port")
    }

}