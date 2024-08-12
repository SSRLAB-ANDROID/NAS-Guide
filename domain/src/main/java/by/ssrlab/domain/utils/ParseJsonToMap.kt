package by.ssrlab.domain.utils

import org.json.JSONObject

fun parseJsonToMap(jsonString: String): Map<String, String> {
    val jsonObject = JSONObject(jsonString)
    val map = HashMap<String, String>()

    val keys = jsonObject.keys()
    while (keys.hasNext()) {
        val key = keys.next()
        val value = jsonObject.getString(key)
        map[key] = value
    }

    return map
}