/*
Copyright 2022 Vasim Mansuri

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */
package com.justmusic.network.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Model class for Songs data
 */
@JsonClass(generateAdapter = true)
data class SongsResponse(
    @Json(name = "feed")
    val feed: Feed?
) {
    @JsonClass(generateAdapter = true)
    data class Feed(
        @Json(name = "author")
        val author: Author?,
        @Json(name = "entry")
        val entry: List<Entry?>?,
        @Json(name = "icon")
        val icon: Icon?,
        @Json(name = "id")
        val id: Id?,
        @Json(name = "link")
        val link: List<Link?>?,
        @Json(name = "rights")
        val rights: Rights?,
        @Json(name = "title")
        val title: Title?,
        @Json(name = "updated")
        val updated: Updated?
    ) {
        @JsonClass(generateAdapter = true)
        data class Author(
            @Json(name = "name")
            val name: Name?,
            @Json(name = "uri")
            val uri: Uri?
        ) {
            @JsonClass(generateAdapter = true)
            data class Name(
                @Json(name = "label")
                val label: String?
            )

            @JsonClass(generateAdapter = true)
            data class Uri(
                @Json(name = "label")
                val label: String?
            )
        }

        @JsonClass(generateAdapter = true)
        data class Entry(
            @Json(name = "category")
            val category: Category?,
            @Json(name = "id")
            val id: Id?,
            @Json(name = "im:artist")
            val imArtist: ImArtist?,
            @Json(name = "im:collection")
            val imCollection: ImCollection?,
            @Json(name = "im:contentType")
            val imContentType: ImContentTypeEntry?,
            @Json(name = "im:image")
            val imImage: List<ImImage?>?,
            @Json(name = "im:name")
            val imName: ImName?,
            @Json(name = "im:price")
            val imPrice: ImPrice?,
            @Json(name = "im:releaseDate")
            val imReleaseDate: ImReleaseDate?,
            @Json(name = "link")
            val link: List<Link?>?,
            @Json(name = "rights")
            val rights: Rights?,
            @Json(name = "title")
            val title: Title?
        ) {
            @JsonClass(generateAdapter = true)
            data class Category(
                @Json(name = "attributes")
                val attributes: Attributes?
            ) {
                @JsonClass(generateAdapter = true)
                data class Attributes(
                    @Json(name = "im:id")
                    val imId: String?,
                    @Json(name = "label")
                    val label: String?,
                    @Json(name = "scheme")
                    val scheme: String?,
                    @Json(name = "term")
                    val term: String?
                )
            }

            @JsonClass(generateAdapter = true)
            data class Id(
                @Json(name = "attributes")
                val attributes: Attributes?,
                @Json(name = "label")
                val label: String?
            ) {
                @JsonClass(generateAdapter = true)
                data class Attributes(
                    @Json(name = "im:id")
                    val imId: Long?
                )
            }

            @JsonClass(generateAdapter = true)
            data class ImArtist(
                @Json(name = "attributes")
                val attributes: Attributes?,
                @Json(name = "label")
                val label: String?
            ) {
                @JsonClass(generateAdapter = true)
                data class Attributes(
                    @Json(name = "href")
                    val href: String?
                )
            }

            @JsonClass(generateAdapter = true)
            data class ImCollection(
                @Json(name = "im:contentType")
                val imContentType: ImContentTypeCollection?,
                @Json(name = "im:name")
                val imName: ImName?,
                @Json(name = "link")
                val link: Link?
            ) {
                @JsonClass(generateAdapter = true)
                data class ImContentTypeCollection(
                    @Json(name = "attributes")
                    val attributes: Attributes?,
                    @Json(name = "im:contentType")
                    val imContentType: ImContentTypeCollectionInner?
                ) {
                    @JsonClass(generateAdapter = true)
                    data class Attributes(
                        @Json(name = "label")
                        val label: String?,
                        @Json(name = "term")
                        val term: String?
                    )

                    @JsonClass(generateAdapter = true)
                    data class ImContentTypeCollectionInner(
                        @Json(name = "attributes")
                        val attributes: Attributes?
                    ) {
                        @JsonClass(generateAdapter = true)
                        data class Attributes(
                            @Json(name = "label")
                            val label: String?,
                            @Json(name = "term")
                            val term: String?
                        )
                    }
                }

                @JsonClass(generateAdapter = true)
                data class ImName(
                    @Json(name = "label")
                    val label: String?
                )

                @JsonClass(generateAdapter = true)
                data class Link(
                    @Json(name = "attributes")
                    val attributes: Attributes?
                ) {
                    @JsonClass(generateAdapter = true)
                    data class Attributes(
                        @Json(name = "href")
                        val href: String?,
                        @Json(name = "rel")
                        val rel: String?,
                        @Json(name = "type")
                        val type: String?
                    )
                }
            }

            @JsonClass(generateAdapter = true)
            data class ImContentTypeEntry(
                @Json(name = "attributes")
                val attributes: Attributes?,
                @Json(name = "im:contentType")
                val imContentType: ImContentTypeEntryInner?
            ) {
                @JsonClass(generateAdapter = true)
                data class Attributes(
                    @Json(name = "label")
                    val label: String?,
                    @Json(name = "term")
                    val term: String?
                )

                @JsonClass(generateAdapter = true)
                data class ImContentTypeEntryInner(
                    @Json(name = "attributes")
                    val attributes: Attributes?
                ) {
                    @JsonClass(generateAdapter = true)
                    data class Attributes(
                        @Json(name = "label")
                        val label: String?,
                        @Json(name = "term")
                        val term: String?
                    )
                }
            }

            @JsonClass(generateAdapter = true)
            data class ImImage(
                @Json(name = "attributes")
                val attributes: Attributes?,
                @Json(name = "label")
                val label: String?
            ) {
                @JsonClass(generateAdapter = true)
                data class Attributes(
                    @Json(name = "height")
                    val height: Int?
                )
            }

            @JsonClass(generateAdapter = true)
            data class ImName(
                @Json(name = "label")
                val label: String?
            )

            @JsonClass(generateAdapter = true)
            data class ImPrice(
                @Json(name = "attributes")
                val attributes: Attributes?,
                @Json(name = "label")
                val label: String?
            ) {
                @JsonClass(generateAdapter = true)
                data class Attributes(
                    @Json(name = "amount")
                    val amount: String?,
                    @Json(name = "currency")
                    val currency: String?
                )
            }

            @JsonClass(generateAdapter = true)
            data class ImReleaseDate(
                @Json(name = "attributes")
                val attributes: Attributes?,
                @Json(name = "label")
                val label: String?
            ) {
                @JsonClass(generateAdapter = true)
                data class Attributes(
                    @Json(name = "label")
                    val label: String?
                )
            }

            @JsonClass(generateAdapter = true)
            data class Link(
                @Json(name = "attributes")
                val attributes: Attributes?,
                @Json(name = "im:duration")
                val imDuration: ImDuration?
            ) {
                @JsonClass(generateAdapter = true)
                data class Attributes(
                    @Json(name = "href")
                    val href: String?,
                    @Json(name = "im:assetType")
                    val imAssetType: String?,
                    @Json(name = "rel")
                    val rel: String?,
                    @Json(name = "title")
                    val title: String?,
                    @Json(name = "type")
                    val type: String?
                )

                @JsonClass(generateAdapter = true)
                data class ImDuration(
                    @Json(name = "label")
                    val label: Int?
                )
            }

            @JsonClass(generateAdapter = true)
            data class Rights(
                @Json(name = "label")
                val label: String?
            )

            @JsonClass(generateAdapter = true)
            data class Title(
                @Json(name = "label")
                val label: String?
            )
        }

        @JsonClass(generateAdapter = true)
        data class Icon(
            @Json(name = "label")
            val label: String?
        )

        @JsonClass(generateAdapter = true)
        data class Id(
            @Json(name = "label")
            val label: String?
        )

        @JsonClass(generateAdapter = true)
        data class Link(
            @Json(name = "attributes")
            val attributes: Attributes?
        ) {
            @JsonClass(generateAdapter = true)
            data class Attributes(
                @Json(name = "href")
                val href: String?,
                @Json(name = "rel")
                val rel: String?,
                @Json(name = "type")
                val type: String?
            )
        }

        @JsonClass(generateAdapter = true)
        data class Rights(
            @Json(name = "label")
            val label: String?
        )

        @JsonClass(generateAdapter = true)
        data class Title(
            @Json(name = "label")
            val label: String?
        )

        @JsonClass(generateAdapter = true)
        data class Updated(
            @Json(name = "label")
            val label: String?
        )
    }
}