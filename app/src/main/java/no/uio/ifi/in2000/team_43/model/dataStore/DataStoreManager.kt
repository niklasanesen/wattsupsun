package no.uio.ifi.in2000.team_43.model.dataStore

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import no.uio.ifi.in2000.team_43.model.input.LagreAdresse
import java.util.UUID


val Context.dataStore by preferencesDataStore(name = "addresses")

class DataStoreManager(private val context: Context) {

    private val ADDRESSES_KEY = stringSetPreferencesKey("addresses")

    companion object {
        val FIRSTNAME = stringPreferencesKey("firstname")
        val LASTNAME = stringPreferencesKey("lastname")
        val PROFILE_PHOTO_URI = stringPreferencesKey("profile_photo_uri")
    }

    // Lagre en adresse
// Lagre en adresse
    suspend fun saveAddress(adresse: LagreAdresse) {
        context.dataStore.edit { preferences ->
            val currentList = preferences[ADDRESSES_KEY]?.toMutableSet() ?: mutableSetOf()

            // Generer en unik ID (UUID) for hver adresse
            val uniqueId = UUID.randomUUID().toString()

            // Lag en addressString med alle adresseparametrene
            val addressString = "lat:${adresse.latitude}, long:${adresse.longitude}, height:${adresse.height}, width:${adresse.width}, angle:${adresse.angle}, direction:${adresse.direction}, addr:${adresse.addressString}, name:${adresse.lagretNavn}, numPanel:${adresse.numPanel}, totalPanelAreal:${adresse.totalPanelAreal}, kwp:${adresse.kwp}, pris:${adresse.pris}"

            // Kombinerer UUID med addressString
            val addressWithId = "$uniqueId|$addressString"

            // Legg til den nye adressen med UUID i listen
            currentList.add(addressWithId)

            // Lagre den oppdaterte listen tilbake til DataStore
            preferences[ADDRESSES_KEY] = currentList
        }
    }


    // Hent lagrede adresser
    val addresses: Flow<List<LagreAdresse>> = context.dataStore.data
        .map { preferences ->
            preferences[ADDRESSES_KEY]?.mapNotNull { addressString ->
                try {
                    // Split addressString i uniqueId og adresseinformasjon
                    val parts = addressString.split("|")
                    val uniqueId = parts[0]  // Dette er UUID
                    val addressDetails = parts[1]  // Dette er selve adresseinformasjonen

                    // Del opp adresseinformasjonen
                    val addressParts = addressDetails.split(", ")
                        .associate { part -> part.substringBefore(":") to part.substringAfter(":") }

                    LagreAdresse(
                        latitude = addressParts["lat"]?.toDoubleOrNull() ?: return@mapNotNull null,
                        longitude = addressParts["long"]?.toDoubleOrNull() ?: return@mapNotNull null,
                        height = addressParts["height"]?.toDoubleOrNull() ?: return@mapNotNull null,
                        width = addressParts["width"]?.toDoubleOrNull() ?: return@mapNotNull null,
                        angle = addressParts["angle"]?.toDoubleOrNull() ?: return@mapNotNull null,
                        direction = addressParts["direction"]?.toDoubleOrNull() ?: return@mapNotNull null,
                        addressString = addressParts["addr"] ?: "",
                        lagretNavn = addressParts["name"] ?: "",
                        numPanel = addressParts["numPanel"]?.toIntOrNull() ?: 0,
                        totalPanelAreal = addressParts["totalPanelAreal"]?.toDoubleOrNull() ?: 0.0,
                        kwp = addressParts["kwp"]?.toIntOrNull() ?: 0,
                        pris = addressParts["pris"]?.toIntOrNull() ?: 0,
                        uniqueId = uniqueId  // Legg til uniqueId i objet
                    )
                } catch (e: Exception) {
                    null // Hopper over ugyldige adresser
                }
            } ?: emptyList()
        }



    suspend fun deleteAddress(address: LagreAdresse) {
        context.dataStore.edit { preferences ->
            val currentList = preferences[ADDRESSES_KEY]?.toMutableSet() ?: mutableSetOf()

            // Lag en addressString med alle adresseparametrene
            val addressString = "lat:${address.latitude}, long:${address.longitude}, height:${address.height}, width:${address.width}, angle:${address.angle}, direction:${address.direction}, addr:${address.addressString}, name:${address.lagretNavn}, numPanel:${address.numPanel}, totalPanelAreal:${address.totalPanelAreal}, kwp:${address.kwp}, pris:${address.pris}"

            // Kombiner UUID med addressString
            val addressWithId = "${address.uniqueId}|$addressString"

            // Sjekk om adressen finnes i listen og fjern den
            currentList.remove(addressWithId)

            // Lagre den oppdaterte listen tilbake til DataStore
            preferences[ADDRESSES_KEY] = currentList
        }
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
