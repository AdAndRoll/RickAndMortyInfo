package com.example.domain.model

/**
 * Класс данных для представления полной информации о локации.
 *
 * Этот класс используется для экрана с деталями локации и содержит
 * всю необходимую информацию, загруженную из API, в отличие от
 * LocationRM, который является лишь ссылкой на локацию.
 */
data class LocationDetail(
    val id: Int,
    val name: String,
    val type: String,
    val dimension: String,
    val residentNames: List<String> // Теперь здесь хранятся имена, а не URL
)
