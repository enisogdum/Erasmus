package com.example.bookshelfapp.data

object FallbackBooksData {
    fun getFallbackBooks(query: String): List<Book> {
        val q = query.lowercase().trim()
        return when {
            q.contains("kotlin") -> listOf(
                Book(
                    id = "kotlin1",
                    volumeInfo = VolumeInfo(
                        title = "Kotlin in Action",
                        authors = listOf("Dmitry Jemerov", "Svetlana Isakova"),
                        description = "Kotlin in Action teaches you to write production-quality code with Kotlin. This revised best-seller is your guide to the Kotlin language. You'll start with fluent language basics, and progress to advanced topics like building DSLs and concurrent programming.",
                        publisher = "Manning Publications",
                        publishedDate = "2017",
                        pageCount = 360,
                        imageLinks = ImageLinks(
                            thumbnail = "https://covers.openlibrary.org/b/id/10543787-L.jpg",
                            smallThumbnail = "https://covers.openlibrary.org/b/id/10543787-M.jpg"
                        ),
                        previewLink = "https://books.google.com/books?id=kotlin1"
                    )
                ),
                Book(
                    id = "kotlin2",
                    volumeInfo = VolumeInfo(
                        title = "Head First Kotlin",
                        authors = listOf("Dawn Griffiths", "David Griffiths"),
                        description = "Head First Kotlin is a complete introduction to coding in Kotlin. This hands-on book helps you learn the Kotlin language with a unique method that goes beyond syntax and how-to manuals.",
                        publisher = "O'Reilly Media",
                        publishedDate = "2019",
                        pageCount = 450,
                        imageLinks = ImageLinks(
                            thumbnail = "https://covers.openlibrary.org/b/id/10557454-L.jpg",
                            smallThumbnail = "https://covers.openlibrary.org/b/id/10557454-M.jpg"
                        ),
                        previewLink = "https://books.google.com/books?id=kotlin2"
                    )
                ),
                Book(
                    id = "kotlin3",
                    volumeInfo = VolumeInfo(
                        title = "Atomic Kotlin",
                        authors = listOf("Bruce Eckel", "Svetlana Isakova"),
                        description = "Atomic Kotlin is for both experienced programmers and beginners. It breaks the language down into small, digestible pieces called 'atoms'. Perfect for quick self-study and deep comprehension.",
                        publisher = "Mindview LLC",
                        publishedDate = "2021",
                        pageCount = 580,
                        imageLinks = ImageLinks(
                            thumbnail = "https://covers.openlibrary.org/b/id/11422709-L.jpg",
                            smallThumbnail = "https://covers.openlibrary.org/b/id/11422709-M.jpg"
                        ),
                        previewLink = "https://books.google.com/books?id=kotlin3"
                    )
                )
            )
            q.contains("android") -> listOf(
                Book(
                    id = "android1",
                    volumeInfo = VolumeInfo(
                        title = "Android Programming: The Big Nerd Ranch Guide",
                        authors = listOf("Kristin Marsicano", "Brian Gardner", "Bill Phillips"),
                        description = "Android Programming: The Big Nerd Ranch Guide is an introductory Android book for programmers with Kotlin experience. Based on Big Nerd Ranch's popular Android bootcamp course, this guide leads you through the concepts and APIs in a detailed and practical manner.",
                        publisher = "Big Nerd Ranch",
                        publishedDate = "2021",
                        pageCount = 510,
                        imageLinks = ImageLinks(
                            thumbnail = "https://covers.openlibrary.org/b/id/8408544-L.jpg",
                            smallThumbnail = "https://covers.openlibrary.org/b/id/8408544-M.jpg"
                        ),
                        previewLink = "https://books.google.com/books?id=android1"
                    )
                ),
                Book(
                    id = "android2",
                    volumeInfo = VolumeInfo(
                        title = "Kotlin for Android App Development",
                        authors = listOf("Peter Sommerhoff"),
                        description = "Write beautiful, robust, and maintainable Android apps using Kotlin, Google's preferred language for Android development. Leverage modern Jetpack libraries and architectural best practices.",
                        publisher = "Addison-Wesley Professional",
                        publishedDate = "2020",
                        pageCount = 380,
                        imageLinks = ImageLinks(
                            thumbnail = "https://covers.openlibrary.org/b/id/10646193-L.jpg",
                            smallThumbnail = "https://covers.openlibrary.org/b/id/10646193-M.jpg"
                        ),
                        previewLink = "https://books.google.com/books?id=android2"
                    )
                )
            )
            q.contains("design") -> listOf(
                Book(
                    id = "design1",
                    volumeInfo = VolumeInfo(
                        title = "Designing Data-Intensive Applications",
                        authors = listOf("Martin Kleppmann"),
                        description = "Key principles, algorithms, and trade-offs when building data systems. This book covers databases, caching, streaming, replication, partition, and large-scale data processing in deep detail.",
                        publisher = "O'Reilly Media",
                        publishedDate = "2017",
                        pageCount = 610,
                        imageLinks = ImageLinks(
                            thumbnail = "https://covers.openlibrary.org/b/id/10034608-L.jpg",
                            smallThumbnail = "https://covers.openlibrary.org/b/id/10034608-M.jpg"
                        ),
                        previewLink = "https://books.google.com/books?id=design1"
                    )
                ),
                Book(
                    id = "design2",
                    volumeInfo = VolumeInfo(
                        title = "Design Patterns: Reusable Software",
                        authors = listOf("Erich Gamma", "Richard Helm", "Ralph Johnson", "John Vlissides"),
                        description = "Captures the industry-standard design patterns to help you write flexible, elegant, and reusable object-oriented software. A classic text for every developer's shelf.",
                        publisher = "Addison-Wesley",
                        publishedDate = "1994",
                        pageCount = 395,
                        imageLinks = ImageLinks(
                            thumbnail = "https://covers.openlibrary.org/b/id/10292544-L.jpg",
                            smallThumbnail = "https://covers.openlibrary.org/b/id/10292544-M.jpg"
                        ),
                        previewLink = "https://books.google.com/books?id=design2"
                    )
                ),
                Book(
                    id = "design3",
                    volumeInfo = VolumeInfo(
                        title = "Don't Make Me Think, Revisited",
                        authors = listOf("Steve Krug"),
                        description = "A Common Sense Approach to Web and Mobile Usability. Five million designers and developers have read it, making it one of the most loved books on UX.",
                        publisher = "New Riders",
                        publishedDate = "2014",
                        pageCount = 216,
                        imageLinks = ImageLinks(
                            thumbnail = "https://covers.openlibrary.org/b/id/8323631-L.jpg",
                            smallThumbnail = "https://covers.openlibrary.org/b/id/8323631-M.jpg"
                        ),
                        previewLink = "https://books.google.com/books?id=design3"
                    )
                )
            )
            q.contains("fiction") -> listOf(
                Book(
                    id = "fiction1",
                    volumeInfo = VolumeInfo(
                        title = "The Hobbit",
                        authors = listOf("J.R.R. Tolkien"),
                        description = "Whisked away from his comfortable hobbit-hole by Gandalf the wizard and a band of dwarves, Bilbo Baggins finds himself caught up in a plot to raid the treasure hoard of Smaug the Magnificent, a large and very dangerous dragon.",
                        publisher = "George Allen & Unwin",
                        publishedDate = "1937",
                        pageCount = 310,
                        imageLinks = ImageLinks(
                            thumbnail = "https://covers.openlibrary.org/b/id/8226065-L.jpg",
                            smallThumbnail = "https://covers.openlibrary.org/b/id/8226065-M.jpg"
                        ),
                        previewLink = "https://books.google.com/books?id=fiction1"
                    )
                )
            )
            q.contains("history") -> listOf(
                Book(
                    id = "history1",
                    volumeInfo = VolumeInfo(
                        title = "Sapiens: A Brief History of Humankind",
                        authors = listOf("Yuval Noah Harari"),
                        description = "Explores how biological evolution combined with cognitive, agricultural, and scientific revolutions have shaped homo sapiens and our modern world.",
                        publisher = "Harper",
                        publishedDate = "2011",
                        pageCount = 443,
                        imageLinks = ImageLinks(
                            thumbnail = "https://covers.openlibrary.org/b/id/8225266-L.jpg",
                            smallThumbnail = "https://covers.openlibrary.org/b/id/8225266-M.jpg"
                        ),
                        previewLink = "https://books.google.com/books?id=history1"
                    )
                )
            )
            q.contains("science") -> listOf(
                Book(
                    id = "science1",
                    volumeInfo = VolumeInfo(
                        title = "A Brief History of Time",
                        authors = listOf("Stephen Hawking"),
                        description = "Stephen Hawking's phenomenal bestseller explores the structure, origin, development and eventual fate of the universe, introducing concepts like space-time and black holes to the general public.",
                        publisher = "Bantam Books",
                        publishedDate = "1988",
                        pageCount = 212,
                        imageLinks = ImageLinks(
                            thumbnail = "https://covers.openlibrary.org/b/id/8405011-L.jpg",
                            smallThumbnail = "https://covers.openlibrary.org/b/id/8405011-M.jpg"
                        ),
                        previewLink = "https://books.google.com/books?id=science1"
                    )
                )
            )
            q.contains("biography") -> listOf(
                Book(
                    id = "biography1",
                    volumeInfo = VolumeInfo(
                        title = "Steve Jobs",
                        authors = listOf("Walter Isaacson"),
                        description = "Based on more than forty interviews with Steve Jobs conducted over two years—as well as interviews with more than a hundred family members, friends, adversaries, competitors, and colleagues—Walter Isaacson has written a riveting story of the roller-coaster life and searingly intense personality of a creative entrepreneur.",
                        publisher = "Simon & Schuster",
                        publishedDate = "2011",
                        pageCount = 656,
                        imageLinks = ImageLinks(
                            thumbnail = "https://covers.openlibrary.org/b/id/8393042-L.jpg",
                            smallThumbnail = "https://covers.openlibrary.org/b/id/8393042-M.jpg"
                        ),
                        previewLink = "https://books.google.com/books?id=biography1"
                    )
                )
            )
            else -> listOf(
                Book(
                    id = "general1",
                    volumeInfo = VolumeInfo(
                        title = "The Pragmatic Programmer",
                        authors = listOf("David Thomas", "Andrew Hunt"),
                        description = "A handbook for software developers to cultivate dynamic career growth, robust coding methodologies, and a deep appreciation for technical craftsmanship.",
                        publisher = "Addison-Wesley",
                        publishedDate = "1999",
                        pageCount = 352,
                        imageLinks = ImageLinks(
                            thumbnail = "https://covers.openlibrary.org/b/id/10363294-L.jpg",
                            smallThumbnail = "https://covers.openlibrary.org/b/id/10363294-M.jpg"
                        ),
                        previewLink = "https://books.google.com/books?id=general1"
                    )
                ),
                Book(
                    id = "general2",
                    volumeInfo = VolumeInfo(
                        title = "Code Complete",
                        authors = listOf("Steve McConnell"),
                        description = "Widely considered one of the best practical guides to programming, Code Complete helps you write high-quality code through robust construction techniques and concrete examples.",
                        publisher = "Microsoft Press",
                        publishedDate = "2004",
                        pageCount = 960,
                        imageLinks = ImageLinks(
                            thumbnail = "https://covers.openlibrary.org/b/id/9254871-L.jpg",
                            smallThumbnail = "https://covers.openlibrary.org/b/id/9254871-M.jpg"
                        ),
                        previewLink = "https://books.google.com/books?id=general2"
                    )
                )
            )
        }
    }
}
