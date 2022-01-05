package com.abhat.cleannews_compose.data.models

import org.simpleframework.xml.Element
import org.simpleframework.xml.Root

/**
 * Created by Anirudh Uppunda on 27/10/20.
 */
@Root(name = "item", strict = false)
data class Item @JvmOverloads constructor(
        @field:Element(required = false) var title: String,
        @field:Element(required = false) var link: String,
        @field:Element(required = false) var description: String?,
        @field:Element(required = false) var pubDate: String) {
    constructor() : this("title", "", "description", "pubDate")
}