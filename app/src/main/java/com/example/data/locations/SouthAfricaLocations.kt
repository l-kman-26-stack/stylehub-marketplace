package com.example.data.locations

data class ProvinceData(
    val name: String,
    val code: String,
    val majorCities: List<CityData>
)

data class CityData(
    val name: String,
    val suburbs: List<String>
)

object SouthAfricaLocations {

    val provinces: List<ProvinceData> = listOf(
        ProvinceData(
            name = "Gauteng",
            code = "GP",
            majorCities = listOf(
                CityData(
                    name = "Johannesburg",
                    suburbs = listOf(
                        "Sandton", "Rosebank", "Braamfontein", "Fourways", "Maboneng",
                        "Soweto (Orlando)", "Soweto (Diepkloof)", "Bryanston", "Melville",
                        "Randburg", "Bedfordview", "Parkhurst", "Linden", "Northcliff",
                        "Midrand", "Roodepoort", "Glenvista", "Houghton"
                    )
                ),
                CityData(
                    name = "Pretoria / Tshwane",
                    suburbs = listOf(
                        "Hatfield", "Menlyn", "Centurion", "Brooklyn", "Arcadia",
                        "Garsfontein", "Faerie Glen", "Silver Lakes", "Mamelodi",
                        "Soshanguve", "Waterkloof", "Pretoria North", "Montana"
                    )
                ),
                CityData(
                    name = "Ekurhuleni (East Rand)",
                    suburbs = listOf(
                        "Bedfordview", "Benoni", "Boksburg", "Kempton Park", "Edenvale",
                        "Alberton", "Springs", "Brakpan", "Germiston"
                    )
                ),
                CityData(
                    name = "West Rand",
                    suburbs = listOf(
                        "Krugersdorp", "Roodepoort", "Randfontein", "Mogale City"
                    )
                ),
                CityData(
                    name = "Sedibeng (Vaal)",
                    suburbs = listOf(
                        "Vanderbijlpark", "Vereeniging", "Meyerton"
                    )
                )
            )
        ),
        ProvinceData(
            name = "Western Cape",
            code = "WC",
            majorCities = listOf(
                CityData(
                    name = "Cape Town",
                    suburbs = listOf(
                        "Cape Town CBD", "Sea Point", "Camps Bay", "Green Point", "Woodstock",
                        "Observatory", "Claremont", "Rondebosch", "Century City", "Bellville",
                        "Durbanville", "Khayelitsha", "Mitchells Plain", "Table View", "Bloubergstrand",
                        "Hout Bay", "Muizenberg", "Constantia", "Wynberg"
                    )
                ),
                CityData(
                    name = "Winelands (Stellenbosch & Paarl)",
                    suburbs = listOf(
                        "Stellenbosch Central", "Franschhoek", "Paarl", "Somerset West",
                        "Strand", "Gordon's Bay", "Wellington"
                    )
                ),
                CityData(
                    name = "Garden Route",
                    suburbs = listOf(
                        "George", "Knysna", "Plettenberg Bay", "Mossel Bay", "Oudtshoorn"
                    )
                ),
                CityData(
                    name = "Overberg & West Coast",
                    suburbs = listOf(
                        "Hermanus", "Saldanha", "Langebaan", "Vredenburg", "Swellendam"
                    )
                )
            )
        ),
        ProvinceData(
            name = "KwaZulu-Natal",
            code = "KZN",
            majorCities = listOf(
                CityData(
                    name = "Durban / eThekwini",
                    suburbs = listOf(
                        "Durban CBD", "Morningside", "Florida Road", "Umhlanga Rocks", "Durban North",
                        "Glenwood", "Westville", "Pinetown", "Chatsworth", "Umlazi",
                        "Kloof", "Hillcrest", "Amanzimtoti", "Phoenix", "KwaMashu"
                    )
                ),
                CityData(
                    name = "North Coast (Dolphin Coast)",
                    suburbs = listOf(
                        "Ballito", "Salt Rock", "Sheffield Beach", "Stanger / KwaDukuza"
                    )
                ),
                CityData(
                    name = "Pietermaritzburg (Midlands)",
                    suburbs = listOf(
                        "Pietermaritzburg Central", "Scottsville", "Hilton", "Howick", "Hayfields"
                    )
                ),
                CityData(
                    name = "Zululand & South Coast",
                    suburbs = listOf(
                        "Richards Bay", "Empangeni", "Newcastle", "Margate", "Port Shepstone"
                    )
                )
            )
        ),
        ProvinceData(
            name = "Eastern Cape",
            code = "EC",
            majorCities = listOf(
                CityData(
                    name = "Gqeberha / Port Elizabeth",
                    suburbs = listOf(
                        "Summerstrand", "Walmer", "Mill Park", "Central", "Newton Park",
                        "Kariega (Uitenhage)", "Motherwell", "Humewood"
                    )
                ),
                CityData(
                    name = "East London / Buffalo City",
                    suburbs = listOf(
                        "Beacon Bay", "Nahoon", "Vincent", "Mdantsane", "Gonubie", "Quigney"
                    )
                ),
                CityData(
                    name = "Makhanda & Interior",
                    suburbs = listOf(
                        "Makhanda Central", "Kingswood", "Rhodes Precinct", "Qonce (King William's Town)"
                    )
                ),
                CityData(
                    name = "Mthatha & Wild Coast",
                    suburbs = listOf(
                        "Mthatha Central", "Norwood", "Fort Gale", "Jeffreys Bay", "Port St Johns"
                    )
                )
            )
        ),
        ProvinceData(
            name = "Free State",
            code = "FS",
            majorCities = listOf(
                CityData(
                    name = "Bloemfontein / Mangaung",
                    suburbs = listOf(
                        "Westdene", "Brandwag", "Dan Pienaar", "Langenhovenpark", "Willows",
                        "Bayswater", "Universitas", "Heidedal", "Mangaung"
                    )
                ),
                CityData(
                    name = "Goldfields & Northern FS",
                    suburbs = listOf(
                        "Welkom", "Sasolburg", "Bethlehem", "Kroonstad", "Parys", "Phuthaditjhaba"
                    )
                )
            )
        ),
        ProvinceData(
            name = "Mpumalanga",
            code = "MP",
            majorCities = listOf(
                CityData(
                    name = "Mbombela / Nelspruit",
                    suburbs = listOf(
                        "Nelspruit Central", "West Acres", "Sonheuwel", "Steiltes", "White River"
                    )
                ),
                CityData(
                    name = "Highveld",
                    suburbs = listOf(
                        "eMalahleni (Witbank)", "Middelburg", "Secunda", "Standerton"
                    )
                )
            )
        ),
        ProvinceData(
            name = "Limpopo",
            code = "LP",
            majorCities = listOf(
                CityData(
                    name = "Polokwane",
                    suburbs = listOf(
                        "Polokwane Central", "Bendor", "Flora Park", "Fauna Park", "Seshego"
                    )
                ),
                CityData(
                    name = "Limpopo Regions",
                    suburbs = listOf(
                        "Tzaneen", "Thohoyandou", "Mokopane", "Bela-Bela", "Phalaborwa", "Lephalale"
                    )
                )
            )
        ),
        ProvinceData(
            name = "North West",
            code = "NW",
            majorCities = listOf(
                CityData(
                    name = "Bojanala & Rustenburg",
                    suburbs = listOf(
                        "Rustenburg Central", "Cashan", "Safren", "Brits", "Hartbeespoort"
                    )
                ),
                CityData(
                    name = "Potchefstroom & Mahikeng",
                    suburbs = listOf(
                        "Potchefstroom Central", "Bult", "Grimbeekpark", "Mahikeng Central", "Klerksdorp"
                    )
                )
            )
        ),
        ProvinceData(
            name = "Northern Cape",
            code = "NC",
            majorCities = listOf(
                CityData(
                    name = "Kimberley",
                    suburbs = listOf(
                        "Monument Heights", "Royldene", "Belgravia", "Kimberley Central", "Galeshewe"
                    )
                ),
                CityData(
                    name = "Northern Cape Towns",
                    suburbs = listOf(
                        "Upington", "Springbok", "Kuruman", "De Aar", "Colesberg"
                    )
                )
            )
        )
    )

    fun getAllProvinces(): List<String> = provinces.map { it.name }

    fun getCitiesForProvince(provinceName: String): List<String> {
        val prov = provinces.find { it.name.equals(provinceName, ignoreCase = true) }
        return prov?.majorCities?.map { it.name } ?: emptyList()
    }

    fun getSuburbsForCity(provinceName: String, cityName: String): List<String> {
        val prov = provinces.find { it.name.equals(provinceName, ignoreCase = true) } ?: return emptyList()
        val city = prov.majorCities.find { it.name.equals(cityName, ignoreCase = true) }
        return city?.suburbs ?: emptyList()
    }

    fun getAllCities(): List<String> {
        return provinces.flatMap { it.majorCities.map { city -> city.name } }.distinct()
    }

    fun searchLocations(query: String): List<Triple<String, String, String>> {
        if (query.isBlank()) return emptyList()
        val q = query.trim().lowercase()
        val results = mutableListOf<Triple<String, String, String>>()

        provinces.forEach { prov ->
            prov.majorCities.forEach { city ->
                city.suburbs.forEach { suburb ->
                    if (suburb.lowercase().contains(q) || city.name.lowercase().contains(q) || prov.name.lowercase().contains(q)) {
                        results.add(Triple(prov.name, city.name, suburb))
                    }
                }
            }
        }
        return results.take(15)
    }
}
