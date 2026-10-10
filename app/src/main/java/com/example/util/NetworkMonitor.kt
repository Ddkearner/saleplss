package com.example.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class NetworkMonitor(private val context: Context) {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager

    private val _isOnline = MutableStateFlow(checkInitialConnectivity())
    val isOnline: StateFlow<Boolean> = _isOnline.asStateFlow()

    private val _isSimulatedOffline = MutableStateFlow(false)
    val isSimulatedOffline: StateFlow<Boolean> = _isSimulatedOffline.asStateFlow()

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            updateEffectiveOnlineState(true)
        }

        override fun onLost(network: Network) {
            updateEffectiveOnlineState(false)
        }

        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
        ) {
            val hasInternet = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            updateEffectiveOnlineState(hasInternet)
        }
    }

    init {
        registerCallback()
    }

    private fun checkInitialConnectivity(): Boolean {
        val cm = connectivityManager ?: return true
        val activeNetwork = cm.activeNetwork ?: return true
        val caps = cm.getNetworkCapabilities(activeNetwork) ?: return true
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private fun registerCallback() {
        try {
            val request = NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build()
            connectivityManager?.registerNetworkCallback(request, networkCallback)
        } catch (_: Exception) {
            // In unit tests or sandboxed containers without network service
        }
    }

    private fun updateEffectiveOnlineState(actualOnline: Boolean) {
        if (_isSimulatedOffline.value) {
            _isOnline.value = false
        } else {
            _isOnline.value = actualOnline
        }
    }

    fun setSimulatedOffline(simulated: Boolean) {
        _isSimulatedOffline.value = simulated
        if (simulated) {
            _isOnline.value = false
        } else {
            _isOnline.value = checkInitialConnectivity()
        }
    }

    fun isConnected(): Boolean = _isOnline.value
}
