package no.uio.ifi.in2000.team_43.model.dataStore

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import no.uio.ifi.in2000.team_43.model.input.LagreAdresse

val Context.dataStore by preferencesDataStore(name = "addresses")

class DataStoreManager(private val context: Context) {

    private val ADDRESSES_KEY = stringSetPreferencesKey("addresses")

    companion object {
        val FIRSTNAME = stringPreferencesKey("firstname")
        val LASTNAME = stringPreferencesKey("lastname")
        val PROFILE_PHOTO_URI = stringPreferencesKey("profile_photo_uri")
    }

    // Lagre en adresse
    suspend fun saveAddress(adresse: LagreAdresse) {
        context.dataStore.edit { preferences ->
            val currentList = preferences[ADDRESSES_KEY]?.toMutableSet() ?: mutableSetOf()

            // Inkluderer addressString og lagretNavn
            val addressString = "lat:${adresse.latitude}, long:${adresse.longitude}, height:${adresse.height}, width:${adresse.width}, angle:${adresse.angle}, direction:${adresse.direction}, addr:${adresse.addressString}, name:${adresse.lagretNavn}, numPanel:${adresse.numPanel}, totalPanelAreal:${adresse.totalPanelAreal}, kwp:${adresse.kwp}, pris:${adresse.pris}"

            currentList.add(addressString)
            preferences[ADDRESSES_KEY] = currentList
        }
    }

    // Hent lagrede adresser
    val addresses: Flow<List<LagreAdresse>> = context.dataStore.data
        .map { preferences ->
            preferences[ADDRESSES_KEY]?.mapNotNull { addressString ->
                try {
                    val parts = addressString.split(", ")
                        .associate { part -> part.substringBefore(":") to part.substringAfter(":") }

                    LagreAdresse(
                        latitude = parts["lat"]?.toDoubleOrNull() ?: return@mapNotNull null,
                        longitude = parts["long"]?.toDoubleOrNull() ?: return@mapNotNull null,
                        height = parts["height"]?.toDoubleOrNull() ?: return@mapNotNull null,
                        width = parts["width"]?.toDoubleOrNull() ?: return@mapNotNull null,
                        angle = parts["angle"]?.toDoubleOrNull() ?: return@mapNotNull null,
                        direction = parts["direction"]?.toDoubleOrNull() ?: return@mapNotNull null,
                        addressString = parts["addr"] ?: "",
                        lagretNavn = parts["name"] ?: "",
                        numPanel = parts["numPanel"]?.toIntOrNull() ?: 0,
                        totalPanelAreal = parts["totalPanelAreal"]?.toDoubleOrNull() ?: 0.0,
                        kwp = parts["kwp"]?.toIntOrNull() ?: 0,
                        pris = parts["pris"]?.toIntOrNull() ?: 0
                    )
                } catch (e: Exception) {
                    null // Hopper over ugyldige adresser
                }
            } ?: emptyList()
        }

    // Slett alle adresser
    suspend fun deleteAllAddresses() {
        context.dataStore.edit { preferences ->
            preferences.remove(ADDRESSES_KEY)
        }
    }

    val firstnameFlow: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[FIRSTNAME] ?: ""
        }

    val lastnameFlow: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[LASTNAME] ?: ""
        }

    val profilePhotoUriFlow: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[PROFILE_PHOTO_URI] ?: ""
        }

    suspend fun saveFirstname(firstname: String) {
        context.dataStore.edit { preferences ->
            preferences[FIRSTNAME] = firstname
        }
    }

    suspend fun saveLastname(lastname: String) {
        context.dataStore.edit { preferences ->
            preferences[LASTNAME] = lastname
        }
    }

    suspend fun saveProfilePhotoUri(uri: String) {
        context.dataStore.edit { preferences ->
            preferences[PROFILE_PHOTO_URI] = uri
        }
    }
}
