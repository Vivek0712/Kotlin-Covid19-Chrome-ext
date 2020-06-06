package com.collokia.browserplugin.chrome.GoogleImageTest

import kotlin.js.dom.html.document
import kotlin.js.dom.html.HTMLDocument
import kotlin.js.dom.html.HTMLImageElement

// This is a port of this sample: https://developer.chrome.com/extensions/getstarted

native fun encodeURIComponent(str: String): String = noImpl

// native("chrome")
// val chrome: dynamic = noImpl
//
// you COULD use this to call chrome API's without any type safety, or you can declare things like follows:

public native object chrome {
    public native object tabs {
        public native trait Tab {
            public var status: String?
            public var index: Number
            public var openerTabId: Number?
            public var title: String?
            public var url: String?
            public var pinned: Boolean
            public var highlighted: Boolean
            public var windowId: Number
            public var active: Boolean
            public var favIconUrl: String?
            public var id: Number
            public var incognito: Boolean
        }

        public fun query(queryInfo: Any, callback: (result: Array<Tab>) -> Unit): Unit = noImpl
    }
}

public class chrome_tabs_CurrentTabQuery() {
    val active = true;
    val currentWindow = true
}

// TODO: don't know why kotlin removed this, probably because it is more complicated to create one than just allocating depending on old browsers.  They could keep the trait though
public native("XMLHttpRequest") class TempXMLHttpRequest() {
    public fun open(method: String, url: String): Unit = noImpl
    public fun send(): Unit = noImpl
    public var responseType: String = noImpl
    public var onload: () -> Unit = noImpl
    public var onerror: () -> Unit = noImpl
    public var response: dynamic = noImpl   // this is dynamic because it can be about anything when JSON, so don't fight the dyanmicness!
}

// TODO: missing in Kotlin DOM
native fun HTMLDocument.addEventListener(event: String, function: () -> Unit): Unit = noImpl



fun getImageUrl(searchTerm: String, successCallback: () -> Unit, errorCallback: () -> Unit) {
    val searchUrl = "https://api.covid19india.org/data.json"


    val req = TempXMLHttpRequest()
    req.open("GET", searchUrl)
    req.responseType = "json"
    req.onload = {
        ->
        val response = req.response
        if (response == null ) {
          errorCallback()
        }
        val fullres = response.statewise[0]
        val active = fullres.active
        val confirmed = fullres.confirmed
        val recovered = fullres.recovered
        val deaths = fullres.deaths
        val res = "Active: ${active} Confirmed: ${confirmed} Recovered: ${recovered} Deaths: ${deaths}"
        renderStatus(res)


    }
    req.onerror = {
        errorCallback()
    }
    req.send()
}

fun renderStatus(statusText: String) {
    document.getElementById("status").textContent = statusText
}

fun main(args: Array<String>) {
    console.log("Kotlin extension popup script running!")

    document.addEventListener("DOMContentLoaded") {
        chrome.tabs.query(chrome_tabs_CurrentTabQuery()) { tabs ->
            if (tabs.size() == 1 && tabs[0].url != null) {
                val url = tabs[0].url!!
                renderStatus("Performing Data fetch")

                }, {
                    renderStatus("Cannot display status, failure to talk to Covid 19 API")
                })
            } else {
                renderStatus("No current tab, cannot do anything")
            }
        }
    }

}