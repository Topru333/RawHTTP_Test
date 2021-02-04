package com.topru.rawhttp

import com.topru.rawhttp.client.HttpClient
import com.topru.rawhttp.server.HttpServer
import rawhttp.core.RawHttp
import rawhttp.core.RawHttpRequest
import rawhttp.core.RawHttpResponse
import java.util.Optional

import java.time.ZoneOffset

import java.time.ZonedDateTime

import java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME

import java.net.InetAddress

import java.net.Socket
import java.nio.charset.Charset


fun prepareRequest(request: RawHttpRequest) : RawHttpRequest {
    println("Prepare ${request.method} URI PATH:${request.uri.path} HOST:${request.uri.host} PORT:${request.uri.port}")
    return request
}

fun onRequest(request: RawHttpRequest) {
    println("OnRequest ${request.method} URI PATH:${request.uri.path} HOST:${request.uri.host} PORT:${request.uri.port}")
}

fun onResponseСlient(request: RawHttpRequest, response: RawHttpResponse<*>)  {
    println("onResponseClient" +
            "\n--- " +
            "\nRequest: ${request.method} " +
            "URI PATH:${request.uri.path} HOST:${request.uri.host} PORT:${request.uri.port}" +
            "\n---")
    print("\nResponse: ${response.statusCode} " +
            "\n${response}" +
            "\n---")
}

fun onResponseServer(request: RawHttpRequest, response: RawHttpResponse<*>)  {
    println("onResponseServer" +
            "\n--- " +
            "\nRequest: ${request.method} " +
            "URI PATH:${request.uri.path} HOST:${request.uri.host} PORT:${request.uri.port}" +
            "\n---")
    print("\nResponse: ${response.statusCode} " +
            "\n${response}" +
            "\n---\n\n")
}

fun main(args: Array<String>) = try {
    val http = RawHttp()
    val server: HttpServer = HttpServer(8086) { request: RawHttpRequest, response: RawHttpResponse<*> -> onResponseServer(request, response) }
    server.start { request: RawHttpRequest ->
        println("Got Request:\n$request")
        val body = "Hello RawHTTP!"
        val dateString = RFC_1123_DATE_TIME.format(ZonedDateTime.now(ZoneOffset.UTC))
        val response: RawHttpResponse<*> = http.parseResponse(
            """
              HTTP/1.1 200 OK
              Content-Type: plain/text
              Content-Length: ${body.length}
              Server: RawHTTP
              Date: $dateString
              
              $body
              """.trimIndent()
        )
        Optional.of(response)
    }
    Thread.sleep(150L);

    val client = HttpClient(false,
        "localhost",
        8086,
        {request: RawHttpRequest -> prepareRequest(request) },
        {request: RawHttpRequest -> onRequest(request)},
        {request: RawHttpRequest, response: RawHttpResponse<*> -> onResponseСlient(request, response)}
    )
    val request = http.parseRequest("GET /\r\nHost: localhost")
    prepareRequest(request)
    val response: RawHttpResponse<*> = client.send(request)
    server.stop()
} catch (e: Exception) {
    println("${e.message}")
    e.printStackTrace()
} finally {
    println("Shutting down")
}