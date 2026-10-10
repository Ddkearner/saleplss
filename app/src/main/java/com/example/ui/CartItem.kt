package com.example.ui

import java.util.UUID

/**
 * Single item in a multi-product quick billing entry.
 */
data class CartItem(
    val id: String = UUID.randomUUID().toString(),
    val category: String = "",
    val quantity: Int = 1,
    val price: Double = 0.0
) {
    val total: Double get() = quantity * price
}

/**
 * Suggested garment catalogue categories for ultra-fast 1-tap/auto-complete selection.
 */
val GarmentCatalogueCategories = listOf(
    "Saree",
    "Suit / Kurti",
    "Menswear",
    "Footwear / Access.",
    "Lehenga / Choli",
    "Sherwani / Indo-West",
    "Dupatta / Shawl",
    "Kids Wear",
    "Shirt / T-Shirt",
    "Jeans / Trousers",
    "Dress Material",
    "Nightwear / Lingerie"
)
