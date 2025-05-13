package no.uio.ifi.in2000.team_43.model.dataStore

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import no.uio.ifi.in2000.team_43.model.input.SavedAddress
import java.util.UUID


val Context.dataStore by preferencesDataStore(name = "addresses")

class DataStoreManager(private val context: Context) {

    private val ADDRESSES_KEY = stringSetPreferencesKey("addresses")

    companion object {
        val FIRSTNAME = stringPreferencesKey("firstname")
        val LASTNAME = stringPreferencesKey("lastname")
        val PROFILE_PHOTO_URI = stringPreferencesKey("profile_photo_uri")
    }

    // Save an address
    suspend fun saveAddress(address: SavedAddress) {
        context.dataStore.edit { preferences ->
            val currentList = preferences[ADDRESSES_KEY]?.toMutableSet() ?: mutableSetOf()

            // Generates a unique ID (UUID) for each address
            val uniqueId = UUID.randomUUID().toString()

            // Make an addressString with all the address parameters
            val addressString = "lat:${address.latitude}, long:${address.longitude}, height:${address.height}, width:${address.width}, angle:${address.angle}, direction:${address.direction}, address:${address.addressString}, name:${address.savedName}, numPanel:${address.numPanel}, totalPanelAreal:${address.totalPanelAreal}, kwp:${address.kwp}, price:${address.price}, moneySaved:${address.moneySaved}"

            // Combining UUID with addressString
            val addressWithId = "$uniqueId|$addressString"

            // Add new address with UUID in the list
            currentList.add(addressWithId)

            // Save the updated list back to DataStore
            preferences[ADDRESSES_KEY] = currentList
        }
    }


    // Get saved addresses
    val addresses: Flow<List<SavedAddress>> = context.dataStore.data
        .map { preferences ->
            preferences[ADDRESSES_KEY]?.mapNotNull { addressString ->
                try {
                    // Split addressString into uniqueId and address information
                    val parts = addressString.split("|")
                    val uniqueId = parts[0]  // This is UUID
                    val addressDetails = parts[1]  // This is the address information itself

                    // Partition address information
                    val addressParts = addressDetails.split(", ")
                        .associate { part -> part.substringBefore(":") to part.substringAfter(":") }

                    SavedAddress(
                        latitude = addressParts["lat"]?.toDoubleOrNull() ?: return@mapNotNull null,
                        longitude = addressParts["long"]?.toDoubleOrNull() ?: return@mapNotNull null,
                        height = addressParts["height"]?.toDoubleOrNull() ?: return@mapNotNull null,
                        width = addressParts["width"]?.toDoubleOrNull() ?: return@mapNotNull null,
                        angle = addressParts["angle"]?.toDoubleOrNull() ?: return@mapNotNull null,
                        direction = addressParts["direction"]?.toDoubleOrNull() ?: return@mapNotNull null,
                        addressString = addressParts["address"] ?: "",
                        savedName = addressParts["name"] ?: "",
                        numPanel = addressParts["numPanel"]?.toIntOrNull() ?: 0,
                        totalPanelAreal = addressParts["totalPanelAreal"]?.toDoubleOrNull() ?: 0.0,
                        kwp = addressParts["kwp"]?.toIntOrNull() ?: 0,
                        price = addressParts["price"]?.toIntOrNull() ?: 0,
                        moneySaved = addressParts["moneySaved"]?.toFloatOrNull() ?: 0f,
                        uniqueId = uniqueId  // Add uniqueId in object
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                    null // Skip invalid addresses
                }
            } ?: emptyList()
        }

    suspend fun deleteAddress(address: SavedAddress) {
        context.dataStore.edit { preferences ->
            val currentList = preferences[ADDRESSES_KEY]?.toMutableSet() ?: mutableSetOf()

            // Make an addressString with all the address parameters
            val addressString = "lat:${address.latitude}, long:${address.longitude}, height:${address.height}, width:${address.width}, angle:${address.angle}, direction:${address.direction}, address:${address.addressString}, name:${address.savedName}, numPanel:${address.numPanel}, totalPanelAreal:${address.totalPanelAreal}, kwp:${address.kwp}, price:${address.price}, moneySaved:${address.moneySaved}"

            // Combine UUID with addressString
            val addressWithId = "${address.uniqueId}|$addressString"

            // Check if address exists in the list and remove it
            currentList.remove(addressWithId)

            // Save the updated list back to DataStore
            preferences[ADDRESSES_KEY] = currentList
        }
    }


    // Delete all addresses
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
