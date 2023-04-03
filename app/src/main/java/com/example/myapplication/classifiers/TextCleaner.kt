package com.example.myapplication.classifiers
import edu.stanford.nlp.ling.CoreAnnotations
import edu.stanford.nlp.pipeline.StanfordCoreNLP
import org.atteo.evo.inflector.English
import org.jsoup.Jsoup
import org.jsoup.safety.Safelist
import java.text.Normalizer
import java.util.*


class TextCleaner {
    fun stripHtml(text: String): String {
        val cleaned = Jsoup.clean(text, Safelist .none())
        return Normalizer.normalize(cleaned, Normalizer.Form.NFD).replace("[^\\p{ASCII}]".toRegex(), "")
    }

    fun removeBetweenSquareBrackets(text: String): String {
        return text.replace("\\[[^]]*]".toRegex(), "")
    }

    fun denoiseText(text: String): String {
        var cleanedText = stripHtml(text)
        cleanedText = removeBetweenSquareBrackets(cleanedText)
        return cleanedText
    }

    fun expandContractions(phrase: String): String {
        var result = phrase
        // specific
        result = result.replace("won't", "will not")
        result = result.replace("can't", "can not")

        // general
        result = result.replace("n't", " not")
        result = result.replace("'re", " are")
        result = result.replace("'s", " is")
        result = result.replace("'d", " would")
        result = result.replace("'ll", " will")
        result = result.replace("'t", " not")
        result = result.replace("'ve", " have")
        result = result.replace("'m", " am")

        return result
    }

    fun removeSpecialCharacters(text: String, removeDigits: Boolean = true): String {
        val pattern = if (removeDigits) "[^a-zA-z\\s]".toRegex() else "[^a-zA-z0-9\\s]".toRegex()
        return text.replace(pattern, "")
    }

    fun removeNonAscii(words: List<String>): List<String> {
        val newWords: MutableList<String> = ArrayList()
        for (word in words) {
            val newWord = Normalizer.normalize(word, Normalizer.Form.NFD).replace("[^\\p{ASCII}]".toRegex(), "")
            newWords.add(newWord)
        }
        return newWords
    }

    fun toLowerCase(words: List<String>): List<String> {
        val newWords: MutableList<String> = ArrayList()
        for (word in words) {
            val newWord = word.lowercase(Locale.getDefault())
            newWords.add(newWord)
        }
        return newWords
    }

    fun removePunctuationAndSplchars(words: List<String>): List<String> {
        val newWords: MutableList<String> = ArrayList()
        for (word in words) {
            val newWord = word.replace("\\p{Punct}|\\p{IsWhite_Space}".toRegex(), "")
            if (newWord != "") {
                val cleanedWord = removeSpecialCharacters(newWord, true)
                newWords.add(cleanedWord)
            }
        }
        return newWords
    }



    fun replaceNumbers(words: List<String>): List<String> {
        val newWords: MutableList<String> = ArrayList()
        for (word in words) {
            if (word.matches("[0-9]+".toRegex())) {
                val newWord = English.plural(word.toInt().toString())
                newWords.add(newWord)
            } else {
                newWords.add(word)
            }
        }
        return newWords
    }

    val stopwordList = setOf("i", "me", "my", "myself", "we", "our", "ours", "ourselves", "you", "your", "yours",
        "yourself", "yourselves", "he", "him", "his", "himself", "she", "her","hers", "herself", "it", "its", "itself", "they", "them", "their", "theirs",
        "themselves", "what", "which", "who", "whom", "this", "that", "these",
        "those", "am", "is", "are", "was", "were", "be", "been", "being", "have",
        "has", "had", "having", "do", "does", "did", "doing", "a", "an", "the",
        "and", "but", "if", "or", "because", "as", "until", "while", "of", "at",
        "by", "for", "with", "about", "against", "between", "into", "through",
        "during", "before", "after", "above", "below", "to", "from", "up", "down",
        "in", "out", "on", "off", "over", "under", "again", "further", "then",
        "once", "here", "there", "when", "where", "why", "how", "all", "any",
        "both", "each", "few", "more", "most", "other", "some", "such", "no",
        "nor", "not", "only", "own", "same", "so", "than", "too", "very", "s",
        "t", "can", "will", "just", "don", "should", "now")

    fun removeStopwords(words: List<String>): List<String> {
        return words.filter { !stopwordList.contains(it) }
    }

    fun lemmatizeSentence(sentence: String): List<String> {
        val props = Properties()
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma")
        val pipeline = StanfordCoreNLP(props)
        val document = pipeline.process(sentence)
        val sentences = document.get(CoreAnnotations.SentencesAnnotation::class.java)
        val lemmas = mutableListOf<String>()
        for (sent in sentences) {
            for (token in sent.get(CoreAnnotations.TokensAnnotation::class.java)) {
                val lemma = token.get(CoreAnnotations.LemmaAnnotation::class.java)
                lemmas.add(lemma)
            }
        }
        return lemmas
    }





    fun preprocessText(text: String): String {
        var cleanedText = denoiseText(text)
        cleanedText = expandContractions(cleanedText)
        val words = cleanedText.split(" ")
        var processedWords = removeNonAscii(words)
        processedWords = toLowerCase(processedWords)
        processedWords = removePunctuationAndSplchars(processedWords)
        processedWords = replaceNumbers(processedWords)
        processedWords = removeStopwords(processedWords)
        val lemmatizedWords = lemmatizeSentence(processedWords.joinToString(" "))
        return lemmatizedWords.joinToString(" ")
    }


}

