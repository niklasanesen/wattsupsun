package no.uio.ifi.in2000.team_43.data.stromPris

import no.uio.ifi.in2000.team_43.model.stromPris.StromprisRes

class StromprisRepository (private val stromprisDataSource: StromprisDataSource){
    suspend fun geStromPris(): List<StromprisRes>? {
        return stromprisDataSource.getStromprisData()
    }

    suspend fun getStromPrisListe(): List<StromprisRes>? {
        return stromprisDataSource.getStromprisData()
    }
}