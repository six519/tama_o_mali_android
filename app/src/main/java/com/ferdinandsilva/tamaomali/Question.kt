package com.ferdinandsilva.tamaomali

class Question(cq: String, ca: Boolean) {
    var q: String = ""
    var a: Boolean = false

    init {
        q = cq
        a = ca
    }

    companion object {
        val questions: List<Question> = listOf(
            Question(
                "Si Ferdinand Magellan ay namatay noong\nAbril 27, 1521 sa isla ng Mactan.",
                true,
            ),
            Question(
                "Si Elpidio Quirino ang kauna-unahang pinoy\nna lumabas sa telebisyon.",
                true,
            ),
        )
    }
}