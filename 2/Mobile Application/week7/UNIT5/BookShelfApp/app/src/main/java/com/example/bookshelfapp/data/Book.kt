package com.example.bookshelfapp.data

data class BooksResponse(
    val items: List<Book>? = null
)

data class Book(
    val id: String,
    val volumeInfo: VolumeInfo? = null
)

data class VolumeInfo(
    val title: String? = null,
    val authors: List<String>? = null,
    val description: String? = null,
    val publisher: String? = null,
    val publishedDate: String? = null,
    val pageCount: Int? = null,
    val imageLinks: ImageLinks? = null,
    val previewLink: String? = null
)

data class ImageLinks(
    val smallThumbnail: String? = null,
    val thumbnail: String? = null
) {
    val httpsThumbnail: String?
        get() = thumbnail?.replace("http://", "https://")

    val httpsSmallThumbnail: String?
        get() = smallThumbnail?.replace("http://", "https://")
}
