package com.example.data.api

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val role: String, // "user" or "model"
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val groundingSources: List<GroundingSource> = emptyList(),
    val isError: Boolean = false
)

data class GroundingSource(
    val title: String,
    val uri: String? = null,
    val snippet: String? = null,
    val isMapPlace: Boolean = false
)

object GeminiClient {
    private const val TAG = "GeminiClient"
    private const val MODEL_NAME = "gemini-2.5-flash"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private const val SYSTEM_INSTRUCTION = """
You are "StyleHub AI", the elite grooming, barbershop, salon, and hair styling concierge for StyleHub South Africa.
Your mission:
1. Provide expert, culturally authentic advice on men's & women's hair styling, grooming, Afro hair textures (Type 3C, 4A, 4B, 4C), dreadlocks, sisterlocks, fades (skin fade, drop fade, burst fade, taper), beard care, hot towel shaves, wig installs, knotless braids, and skincare.
2. Ground your location recommendations with accurate South African geography using the Google Maps tool (Johannesburg, Sandton, Rosebank, Braamfontein, Soweto, Cape Town CBD, Camps Bay, Durban North, Umhlanga, Menlyn Pretoria, Gqeberha, etc.).
3. Provide realistic pricing estimates in South African Rand (ZAR - e.g., R150 - R650).
4. Maintain a warm, stylish, professional, and helpful South African tone ("Sharp sharp", "Looking fresh", etc. used tastefully).
5. When recommending a salon or barber, mention the specific neighborhood, estimated pricing in ZAR, best techniques to request, and suggest they book verified professionals on StyleHub.
"""

    suspend fun sendMessage(
        history: List<ChatMessage>,
        newMessage: String,
        includeMapsGrounding: Boolean = true
    ): Result<ChatMessage> = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY.trim()

        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            // Intelligent fallback for offline / mock when API key not yet set in Secrets
            val fallbackText = generateLocalAdviceFallback(newMessage)
            return@withContext Result.success(
                ChatMessage(
                    role = "model",
                    content = fallbackText,
                    groundingSources = listOf(
                        GroundingSource(
                            title = "StyleHub Local South Africa Directory",
                            snippet = "Explore verified barbers and salons across Gauteng, Western Cape & KZN.",
                            isMapPlace = true
                        )
                    )
                )
            )
        }

        try {
            val url = "$BASE_URL/$MODEL_NAME:generateContent?key=$apiKey"

            val rootJson = JSONObject()

            // System Instruction
            val systemObj = JSONObject()
            val sysParts = JSONArray()
            val sysPart = JSONObject().put("text", SYSTEM_INSTRUCTION)
            sysParts.put(sysPart)
            systemObj.put("parts", sysParts)
            rootJson.put("systemInstruction", systemObj)

            // Tools: Enable Google Maps tool & Search tool for grounded South African location data
            if (includeMapsGrounding) {
                val toolsArray = JSONArray()
                val mapsTool = JSONObject().put("googleMaps", JSONObject())
                val searchTool = JSONObject().put("googleSearch", JSONObject())
                toolsArray.put(mapsTool)
                toolsArray.put(searchTool)
                rootJson.put("tools", toolsArray)
            }

            // Build multi-turn contents array
            val contentsArray = JSONArray()
            
            // Limit history to the last 10 messages for context efficiency
            val recentHistory = history.takeLast(10)
            for (msg in recentHistory) {
                val contentObj = JSONObject()
                contentObj.put("role", if (msg.role == "user") "user" else "model")
                val parts = JSONArray()
                parts.put(JSONObject().put("text", msg.content))
                contentObj.put("parts", parts)
                contentsArray.put(contentObj)
            }

            // Add the new user message
            val newMsgObj = JSONObject()
            newMsgObj.put("role", "user")
            val newParts = JSONArray()
            newParts.put(JSONObject().put("text", newMessage))
            newMsgObj.put("parts", newParts)
            contentsArray.put(newMsgObj)

            rootJson.put("contents", contentsArray)

            // Generation config
            val genConfig = JSONObject()
            genConfig.put("temperature", 0.7)
            genConfig.put("topP", 0.95)
            rootJson.put("generationConfig", genConfig)

            val requestBody = rootJson.toString().toRequestBody("application/json; charset=utf-8".toMediaType())

            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val response = okHttpClient.newCall(request).execute()
            val bodyString = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                Log.e(TAG, "API Error: ${response.code} -> $bodyString")
                // If tools caused an error (e.g. key permissions), retry without tools
                if (includeMapsGrounding && response.code in 400..499) {
                    return@withContext sendMessage(history, newMessage, includeMapsGrounding = false)
                }
                return@withContext Result.failure(Exception("Gemini API Error (${response.code}): $bodyString"))
            }

            val responseJson = JSONObject(bodyString)
            val candidates = responseJson.optJSONArray("candidates")
            if (candidates == null || candidates.length() == 0) {
                return@withContext Result.failure(Exception("No response generated by model."))
            }

            val firstCandidate = candidates.getJSONObject(0)
            val contentObj = firstCandidate.optJSONObject("content")
            val partsArray = contentObj?.optJSONArray("parts")

            val textBuilder = StringBuilder()
            if (partsArray != null) {
                for (i in 0 until partsArray.length()) {
                    val part = partsArray.getJSONObject(i)
                    if (part.has("text")) {
                        textBuilder.append(part.getString("text"))
                    }
                }
            }

            val fullText = textBuilder.toString().ifBlank { "I couldn't formulate a response. Please try rephrasing your question." }

            // Extract Grounding metadata (Maps places / Search sources)
            val sources = mutableListOf<GroundingSource>()
            val groundingMetadata = firstCandidate.optJSONObject("groundingMetadata")
            if (groundingMetadata != null) {
                val chunks = groundingMetadata.optJSONArray("groundingChunks")
                if (chunks != null) {
                    for (i in 0 until chunks.length()) {
                        val chunk = chunks.getJSONObject(i)
                        val web = chunk.optJSONObject("web")
                        val maps = chunk.optJSONObject("maps") ?: chunk.optJSONObject("googleMaps")
                        
                        if (web != null) {
                            sources.add(
                                GroundingSource(
                                    title = web.optString("title", "Web Reference"),
                                    uri = web.optString("uri", null),
                                    isMapPlace = false
                                )
                            )
                        } else if (maps != null) {
                            sources.add(
                                GroundingSource(
                                    title = maps.optString("name", maps.optString("title", "Google Maps Location")),
                                    uri = maps.optString("url", maps.optString("uri", null)),
                                    snippet = maps.optString("address", null),
                                    isMapPlace = true
                                )
                            )
                        }
                    }
                }
            }

            Result.success(
                ChatMessage(
                    role = "model",
                    content = fullText,
                    groundingSources = sources
                )
            )
        } catch (e: Exception) {
            Log.e(TAG, "Network/Processing exception", e)
            Result.failure(e)
        }
    }

    private fun generateLocalAdviceFallback(prompt: String): String {
        val lower = prompt.lowercase()
        return when {
            lower.contains("fade") || lower.contains("haircut") || lower.contains("barber") -> {
                """
### 💈 Expert Fade & Barbershop Guide

Here are top recommendations for sharp grooming in South Africa:

1. **Mid-Skin Fade with Textured Crop**:
   - **Best For**: Oval and square face shapes, Type 3 and 4 hair.
   - **Recommended Asking**: *"Ask for a mid-drop skin fade, lined up crisp with razor finish."*
   - **Average Price in SA**: **R150 – R250** (Standard) | **R300 – R450** (Executive with Beard Balm & Steam).

2. **Top Hotspots on StyleHub**:
   - **Rosebank & Sandton**: *The Royal Razor Barbershop* (Oxford Parks, Rosebank)
   - **Cape Town CBD**: *Cape Town Grooming Lounge* (Kloof Street, Gardens)
   - **Durban North**: *Kingdom Fade Studio* (Broadway)

👉 *Tip: You can book these top-rated studios directly under the **Explore** tab in StyleHub!*
""".trimIndent()
            }
            lower.contains("braid") || lower.contains("loc") || lower.contains("knotless") -> {
                """
### ✨ Braids, Locs & Protective Styles in SA

1. **Knotless Box Braids (Medium / Waist-Length)**:
   - **Why It's Trending**: Pain-free scalp installation, natural movement, and long-lasting protection.
   - **Price Range**: **R450 – R750** (Excluding hair extensions) in Joburg and Cape Town.

2. **Sisterlocks & Micro-locs Maintenance**:
   - **Retightening frequency**: Every 4 to 6 weeks.
   - **Wash Day Tip**: Use clarifying sulfate-free shampoo with tea tree and peppermint oil to avoid buildup.

3. **Featured Braiding Studios**:
   - **Braamfontein / Maboneng**: *AfroCrown Braids & Locs Sanctuary*
   - **Centurion / Menlyn**: *Velvet Glow Salon & Spa*
""".trimIndent()
            }
            lower.contains("beard") || lower.contains("shave") -> {
                """
### 🧔 Premium Beard Care & Shaving Routine

1. **Daily Beard Hydration**:
   - Apply a South African Jojoba & Marula beard oil after washing.
   - Brush with a natural boar bristle brush to distribute natural oils and stimulate blood circulation.

2. **Hot Towel Shave Experience**:
   - **What to Expect**: Pre-shave essential oils, double hot towel wrap, single-blade straight razor shave, followed by an iced towel and calming aftershave balm.
   - **Average Price**: **R180 – R320**.
""".trimIndent()
            }
            else -> {
                """
### 💈 Welcome to StyleHub AI Concierge!

I'm your personal style, hair care, and grooming consultant for South Africa.

**How I can help you today:**
- 📍 **Find Top Grooming Studios**: Look up barbers, braiders, nail techs, and spas in Sandton, Cape Town, Durban, Pretoria, and more.
- ✂️ **Style Recommendations**: Determine what fade, loc style, or beard shape suits your face structure.
- 💰 **Pricing Guidance**: Check average ZAR rates for knotless braids, skin fades, silk press, and dreadlock maintenance.
- 🌿 **Hair Care Routines**: Custom regimens for 4C afro hair, curly textures, moisture retention, and scalp health.

*Try asking: "What's the best fade for a round face?" or "Find knotless braiders near Rosebank under R500."*
""".trimIndent()
            }
        }
    }
}
