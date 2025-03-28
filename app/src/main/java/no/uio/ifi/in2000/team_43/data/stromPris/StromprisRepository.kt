package no.uio.ifi.in2000.team_43.data.stromPris

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import no.uio.ifi.in2000.team_43.data.met.MetDataSource
import no.uio.ifi.in2000.team_43.model.met.MetRes
import no.uio.ifi.in2000.team_43.model.stromPris.StromprisRes

class StromprisRepository (private val stromprisDataSource: StromprisDataSource){
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun geStromPris(): List<StromprisRes>? {
        return stromprisDataSource.getStromprisData()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getStromPrisListe(): List<StromprisRes>? {
        return stromprisDataSource.getStromprisData()
    }
}