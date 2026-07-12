package com.example.istanbulapp.data

// ─────────────────────────────────────────────
//  🗂️ SAMPLE DATA
//
//  In a real app, this data would come from a server or database.
//  For learning purposes we hard-code 15 Istanbul places —
//  3 places for each of the 5 categories.
//
//  📚 KEY CONCEPT — object (Singleton):
//    Using `object` instead of `class` means only ONE instance of
//    SampleData ever exists in the app. This is called a singleton.
//    You access it with SampleData.places — no need to create an instance.
// ─────────────────────────────────────────────
object SampleData {

    /**
     * The master list of all 15 Istanbul places.
     * `listOf(...)` creates an immutable list that cannot be changed after creation.
     */
    val places: List<Place> = listOf(

        // ══════════════════════════════════════════
        //  ☕ COFFEE SHOPS (3 places)
        // ══════════════════════════════════════════

        Place(
            id              = 1,
            name            = "Mandabatmaz",
            description     = "Legendary tiny café serving dense Turkish coffee since 1967.",
            fullDescription = "Nestled in a narrow alley off İstiklal Avenue, Mandabatmaz is " +
                "one of Istanbul's most famous Turkish coffee spots. The name literally means " +
                "'a water buffalo won't sink' — referring to the thick, rich consistency of " +
                "their brew. This hole-in-the-wall café has been serving its legendary coffee " +
                "since 1967. Prepare to stand outside with locals on tiny stools!",
            location        = "Beyoğlu",
            address         = "Olivia Geçidi No:1/A, Beyoğlu",
            category        = Category.COFFEE_SHOPS,
            rating          = 4.8f,
            openingHours    = "08:00 – 23:00"
        ),

        Place(
            id              = 2,
            name            = "Fazıl Bey'in Kahvesi",
            description     = "Historic traditional coffee house operating since 1923 in Kadıköy.",
            fullDescription = "Fazıl Bey's Coffee is a true institution in Kadıköy's busy " +
                "market district. Operating since 1923, this shop grinds its own beans and " +
                "prepares each cup with painstaking care. The aroma of freshly ground coffee " +
                "fills the narrow street outside. They also sell packaged coffee to take home " +
                "— a perfect Istanbul souvenir for coffee lovers.",
            location        = "Kadıköy",
            address         = "Muvakkithane Caddesi No:1, Kadıköy",
            category        = Category.COFFEE_SHOPS,
            rating          = 4.7f,
            openingHours    = "07:30 – 22:00"
        ),

        Place(
            id              = 3,
            name            = "Kronotrop",
            description     = "Istanbul's pioneering specialty coffee roastery with a stunning venue.",
            fullDescription = "Kronotrop helped introduce third-wave specialty coffee culture " +
                "to Turkey. They source single-origin beans from around the world and roast " +
                "them on-site. Their Bomonti location sits inside a beautifully renovated " +
                "historic brewery building, offering a perfect blend of old-world charm and " +
                "modern coffee craft. Perfect for pour-over enthusiasts.",
            location        = "Bomonti / Şişli",
            address         = "Birahane Sokak No:1, Bomonti, Şişli",
            category        = Category.COFFEE_SHOPS,
            rating          = 4.6f,
            openingHours    = "08:00 – 22:00"
        ),

        // ══════════════════════════════════════════
        //  🍽️ RESTAURANTS (3 places)
        // ══════════════════════════════════════════

        Place(
            id              = 4,
            name            = "Çiya Sofrası",
            description     = "Award-winning restaurant showcasing rare, forgotten Anatolian recipes.",
            fullDescription = "Çiya Sofrası is more than a restaurant — it's a living culinary " +
                "museum of Anatolian cuisine. Chef Musa Dağdeviren has spent decades researching " +
                "and reviving forgotten recipes from across Turkey's many cultural regions. Every " +
                "dish tells a story. The menu changes seasonally. A must-visit for anyone who " +
                "wants to understand Turkish food beyond the tourist staples.",
            location        = "Kadıköy",
            address         = "Güneşlibahçe Sokak No:43, Kadıköy",
            category        = Category.RESTAURANTS,
            rating          = 4.9f,
            openingHours    = "11:00 – 22:00"
        ),

        Place(
            id              = 5,
            name            = "Karaköy Lokantası",
            description     = "Stylish modern tavern serving elevated Turkish classics by the Bosphorus.",
            fullDescription = "Karaköy Lokantası beautifully bridges traditional Turkish meyhane " +
                "(tavern) culture with contemporary design. Set in a lovingly restored building " +
                "in Karaköy's trendy neighborhood, it serves elevated versions of classic Turkish " +
                "mezes and mains. The fish dishes are exceptional and the wine list features " +
                "excellent Turkish labels. Reserve ahead — it fills up fast!",
            location        = "Karaköy",
            address         = "Kemankeş Caddesi No:37/A, Karaköy",
            category        = Category.RESTAURANTS,
            rating          = 4.7f,
            openingHours    = "12:00 – 00:00"
        ),

        Place(
            id              = 6,
            name            = "Hamdi Et Lokantası",
            description     = "Iconic kebab restaurant with panoramic Golden Horn views since 1963.",
            fullDescription = "For the ultimate kebab experience with a spectacular view, Hamdi " +
                "Et Lokantası is unbeatable. Located steps from the Grand Bazaar, this multi-story " +
                "restaurant offers stunning panoramas of the Golden Horn and old Istanbul's skyline. " +
                "Their İskender and Urfa kebabs have been satisfying guests since 1963. The rooftop " +
                "terrace is especially magical at sunset.",
            location        = "Eminönü",
            address         = "Tahmis Caddesi, Kalçın Sokak No:17, Eminönü",
            category        = Category.RESTAURANTS,
            rating          = 4.5f,
            openingHours    = "11:30 – 23:00"
        ),

        // ══════════════════════════════════════════
        //  🌿 PARKS (3 places)
        // ══════════════════════════════════════════

        Place(
            id              = 7,
            name            = "Emirgan Korusu",
            description     = "Bosphorus-side park famous for its breathtaking April tulip festival.",
            fullDescription = "Emirgan Korusu is one of Istanbul's most beloved green spaces, " +
                "stretching along the European shore of the Bosphorus. The park is especially " +
                "famous during April's Istanbul Tulip Festival when millions of tulips bloom in " +
                "spectacular displays. Throughout the year, visitors enjoy forested walking paths, " +
                "Bosphorus viewpoints, and three historic Ottoman pavilions now serving as cafes.",
            location        = "Sarıyer",
            address         = "Emirgan Korusu, Emirgan, Sarıyer",
            category        = Category.PARKS,
            rating          = 4.8f,
            openingHours    = "Open 24 hours"
        ),

        Place(
            id              = 8,
            name            = "Gülhane Parkı",
            description     = "Ottoman-era park next to Topkapı Palace with sea views.",
            fullDescription = "Gülhane Park (Rose House Park) was once part of the outer gardens " +
                "of Topkapı Palace. Today it's a beautifully maintained public park offering a " +
                "peaceful retreat in the heart of the historic peninsula. Stroll under centuries-old " +
                "plane trees, and enjoy stunning views of the Bosphorus and the Princes' Islands. " +
                "The park is especially lovely in spring when roses bloom.",
            location        = "Sultanahmet / Fatih",
            address         = "Kennedy Caddesi, Gülhane, Fatih",
            category        = Category.PARKS,
            rating          = 4.6f,
            openingHours    = "Open 24 hours"
        ),

        Place(
            id              = 9,
            name            = "Yıldız Parkı",
            description     = "Grand imperial park surrounding the historic Yıldız Palace complex.",
            fullDescription = "Yıldız Park was the private hunting grounds and gardens of the Ottoman " +
                "sultans, particularly beloved by Sultan Abdülhamid II. Spread across a wooded hillside " +
                "above the Bosphorus in Beşiktaş, it offers shaded forest paths, ornate kiosks, and " +
                "serene lake views. The Malta and Çadır Pavilions now function as atmospheric cafes " +
                "serving traditional Turkish refreshments.",
            location        = "Beşiktaş",
            address         = "Çırağan Caddesi, Yıldız, Beşiktaş",
            category        = Category.PARKS,
            rating          = 4.5f,
            openingHours    = "09:00 – 21:00"
        ),

        // ══════════════════════════════════════════
        //  🛍️ SHOPPING (3 places)
        // ══════════════════════════════════════════

        Place(
            id              = 10,
            name            = "Kapalıçarşı (Grand Bazaar)",
            description     = "One of the world's oldest covered markets with 4,000+ shops since 1461.",
            fullDescription = "The Grand Bazaar (Kapalıçarşı) is one of the largest and oldest " +
                "covered markets in the world, with over 4,000 shops spread across 61 covered streets. " +
                "Built by Sultan Mehmed II after the Ottoman conquest of Constantinople, it has been " +
                "Istanbul's commercial heart for over 560 years. Browse jewelry, ceramics, spices, " +
                "textiles, leather goods, and countless handmade souvenirs.",
            location        = "Beyazıt / Fatih",
            address         = "Kalpakçılar Caddesi, Beyazıt, Fatih",
            category        = Category.SHOPPING,
            rating          = 4.7f,
            openingHours    = "Mon–Sat: 08:30 – 19:00"
        ),

        Place(
            id              = 11,
            name            = "Kanyon",
            description     = "Award-winning open-air mall with breathtaking canyon-inspired architecture.",
            fullDescription = "Kanyon is one of Istanbul's most architecturally distinctive shopping " +
                "centers, designed by the Jerde Partnership firm. Its open-air canyon design, winding " +
                "walkways, and dramatic glass elevators create a unique retail experience. The mall " +
                "hosts international luxury brands, excellent restaurants, and a cinema multiplex. " +
                "Outdoor terraces are especially enjoyable in good weather.",
            location        = "Levent / Şişli",
            address         = "Büyükdere Caddesi No:185, Levent",
            category        = Category.SHOPPING,
            rating          = 4.4f,
            openingHours    = "10:00 – 22:00"
        ),

        Place(
            id              = 12,
            name            = "Arasta Bazaar",
            description     = "Charming 17th-century market behind the Blue Mosque with authentic crafts.",
            fullDescription = "The Arasta Bazaar is a 17th-century Ottoman marketplace located directly " +
                "behind the Blue Mosque in Sultanahmet. Unlike the tourist-heavy Grand Bazaar, Arasta " +
                "offers a more relaxed shopping experience with high-quality Turkish handicrafts, " +
                "Iznik ceramics, hand-painted tiles, silk scarves, and carpets. Many shops are owned " +
                "by artisans who produce their goods on-site — a hidden gem!",
            location        = "Sultanahmet / Fatih",
            address         = "Arasta Çarşısı, Sultanahmet, Fatih",
            category        = Category.SHOPPING,
            rating          = 4.6f,
            openingHours    = "09:00 – 20:00"
        ),

        // ══════════════════════════════════════════
        //  🎠 KID-FRIENDLY (3 places)
        // ══════════════════════════════════════════

        Place(
            id              = 13,
            name            = "İstanbul Oyuncak Müzesi",
            description     = "Magical museum with 4,000+ antique toys from across the world.",
            fullDescription = "The Istanbul Toy Museum is one of Turkey's most charming private " +
                "museums, founded by beloved Turkish poet Sunay Akın. Housed in a beautiful 1950s " +
                "villa in Göztepe, it displays over 4,000 antique and vintage toys from around the " +
                "world spanning many decades — from tin robots to porcelain dolls. Each toy tells a " +
                "piece of childhood history. Children and adults alike will be enchanted.",
            location        = "Göztepe / Kadıköy",
            address         = "Dr. Zeki Zeren Sokak No:17, Göztepe, Kadıköy",
            category        = Category.KID_FRIENDLY,
            rating          = 4.8f,
            openingHours    = "Tue–Sun: 09:30 – 18:00"
        ),

        Place(
            id              = 14,
            name            = "Miniatürk",
            description     = "Open-air park with 135+ miniature models of Turkey's greatest landmarks.",
            fullDescription = "Miniatürk is a fascinating open-air miniature park on the shores of " +
                "the Golden Horn, featuring 135+ scale models of Turkey's most iconic landmarks at " +
                "1:25 scale. Walk past tiny replicas of the Hagia Sophia, Ephesus ruins, Cappadocia " +
                "formations, and more in a single afternoon. The park also has a mini railway, boat " +
                "rides, playgrounds, and 3D cinema — perfect for a full family day out.",
            location        = "Sütlüce / Eyüp",
            address         = "Sütlüce Caddesi No:2, Sütlüce, Eyüp",
            category        = Category.KID_FRIENDLY,
            rating          = 4.5f,
            openingHours    = "09:00 – 19:00"
        ),

        Place(
            id              = 15,
            name            = "İstanbul Akvaryum",
            description     = "Europe's largest themed aquarium with 15 ocean zones and 10,000 creatures.",
            fullDescription = "Istanbul Aquarium is Europe's largest themed aquarium, spread across " +
                "15 themed zones representing ocean regions worldwide — from the Black Sea to the " +
                "Amazon River, the Mediterranean to the Pacific Ocean. Home to over 10,000 sea " +
                "creatures including sharks, rays, seahorses, and colorful tropical fish. The " +
                "walk-through 'deep sea tunnel' is a highlight kids will never forget.",
            location        = "Florya / Bakırköy",
            address         = "Şenlikköy Mahallesi, Florya, Bakırköy",
            category        = Category.KID_FRIENDLY,
            rating          = 4.4f,
            openingHours    = "09:00 – 21:00"
        )
    )

    // ─────────────────────────────────────────
    //  Helper Functions
    // ─────────────────────────────────────────

    /**
     * Returns only the places that belong to the given category.
     * `filter { }` keeps items where the condition inside is true.
     */
    fun getPlacesByCategory(category: Category): List<Place> =
        places.filter { it.category == category }

    /**
     * Returns a single place by its unique ID, or null if not found.
     * `find { }` returns the first item matching the condition.
     */
    fun getPlaceById(id: Int): Place? =
        places.find { it.id == id }
}
