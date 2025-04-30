package no.uio.ifi.in2000.team_43.model.dataStore

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map
import no.uio.ifi.in2000.team_43.model.input.LagreAdresse

val Context.dataStore by preferencesDataStore(name = "addresses")
class DataStoreManager(private val context: Context) {

    private val ADDRESSES_KEY = stringSetPreferencesKey("addresses")

    // Lagre en adresse
    suspend fun saveAddress(adresse: LagreAdresse) {
        context.dataStore.edit { preferences ->
            val currentList = preferences[ADDRESSES_KEY]?.toMutableSet() ?: mutableSetOf()

            // Lagre adressen som en string
            val addressString = "lat:${adresse.latitude}, long:${adresse.longitude}, height:${adresse.height}, width:${adresse.width}, angle:${adresse.angle}, direction:${adresse.direction}"

            // Legg til den nye adressen i listen
            currentList.add(addressString)

            preferences[ADDRESSES_KEY] = currentList
        }
    }

    // Hent lagrede adresser
    val addresses = context.dataStore.data
        .map { preferences ->
            preferences[ADDRESSES_KEY]?.map { addressString ->
                val parts = addressString.split(", ")
                val latitude = parts[0].split(":")[1].toDouble()
                val longitude = parts[1].split(":")[1].toDouble()
                val height = parts[2].split(":")[1].toDouble()
                val width = parts[3].split(":")[1].toDouble()
                val angle = parts[4].split(":")[1].toDouble()
                val direction = parts[5].split(":")[1].toDouble()

                LagreAdresse(latitude, longitude, height, width, angle, direction)
            } ?: emptyList()
        }

    // Funksjon for å slette alle lagrede adresser
    suspend fun deleteAllAddresses() {
        context.dataStore.edit { preferences ->
            preferences.remove(ADDRESSES_KEY) // Fjerner alle adresser
        }
    }
}
