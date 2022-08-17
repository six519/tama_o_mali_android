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
            Question(
                "Tuwing ika-apat ng Hulyo ipinagdiriwang ang\nAraw ng Kalayaan sa Pilipinas.",
                false,
            ),
            Question(
                "Manila ang kabisera ng Pilipinas at\nFilipino ang pambansang wika nito.",
                true,
            ),
            Question(
                "Si Andres Bonifacio ang pambansang bayani ng Pilipinas.",
                false,
            ),
            Question(
                "Si Theodore Roosevelt, Jr. ang pinakahuling Amerikanong\nGobernador-Heneral sa Pilipinas.",
                false,
            ),
            Question(
                "Ang kasarian ng pangngalan na Nanay ay PAMBABAE.",
                true,
            ),
            Question(
                "Ang Pilipinas ay mayroong labing-pitong rehiyon.",
                true,
            ),
            Question(
                "Ang ilog Pasig ang pinakamahabang ilog sa Pilipinas.",
                false,
            ),
            Question(
                "Ang guro ay nagtuturo.\nAng kasarian ng GURO ay PAMBABAE.",
                false,
            ),
            Question(
                "Ang lapis ay matulis. Ang kasarian ng LAPIS ay DI-TIYAK.",
                false,
            ),
            Question(
                "Si ate ay naglalaba ng mga damit.\nAng ATE ay may kasarian na PAMBABAE.",
                true,
            ),
            Question(
                "Ang bata ay naglalaro.\nAng kasarian ng BATA ay PANLALAKI.",
                false,
            ),
            Question(
                "Ang kasarian ng pangngalan na TITO ay PAMBABAE.",
                false,
            ),
            Question(
                "Ang labandera ay naglalaba.\nAng kasarian ng LABANDERA ay DI-TIYAK.",
                false,
            ),
            Question(
                "Ang Pilipinas ay isang arkipelago o kapuluan.",
                true,
            ),
            Question(
                "Payak ay mga salitang dinaragdagan ng panlapi.",
                false,
            ),
            Question(
                "Ang tambalan ay dalwang salitang\nmagkaiba ang kahulugan na kapag pinagsama\nay nagkakaroon ng bagong kahulugan.",
                true,
            ),
            Question(
                "Ang barangay ay mula sa salitang balangay.",
                true,
            ),
            Question(
                "Si Tomas Pinpin ang tinaguriang prinsipe ng mga makatang Pilipino.",
                false,
            ),
            Question(
                "Si Franciso Baltazar ang may akda ng Urbana at Feliza.",
                false,
            ),
            Question(
                "Si Jose Rizal ang sumulat ng Noli Me Tangere\nat El Filibusterismo.",
                true,
            ),
            Question(
                "Pangtangi ang tumutukoy sa tiyak na pangngalan.",
                true,
            ),
            Question(
                "Pambalana ang tumutukoy sa karaniwang ngalan\nng tao, bagay, hayop, pook at pangyayari.",
                true,
            ),
            Question(
                "Tagaytay ang tinaguriang Summer Capital ng Pilipinas.",
                false,
            ),
            Question(
                "Si Pangulong Manuel L. Quezon ang Ama ng Wikang Pambansa.",
                true,
            ),
            Question(
                "Ang Mindanao ang pinakamalaking pulo sa Pilipinas.",
                false,
            ),
            Question(
                "Ang Bulkang Mayon ay matatagpuan sa Bohol.",
                false,
            ),
            Question(
                "Mindanao ang pangalawa sa pinakamalaking isla\nna binubuo ng 6 na rehiyon.",
                true,
            ),
            Question(
                "Ang lungsod ng Lipa ay matatagpuan sa lalawigan ng Batangas.",
                true,
            ),
        )
    }
}