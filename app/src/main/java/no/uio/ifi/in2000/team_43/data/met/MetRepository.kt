package no.uio.ifi.in2000.team_43.data.met

import android.os.Build
import androidx.annotation.RequiresApi
import no.uio.ifi.in2000.team_43.model.met.MetRes

class MetRepository(private val metDataSource: MetDataSource) {
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getMetData(): MetRes? {
        return metDataSource.getMetData()
    }
}