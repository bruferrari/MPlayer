package com.ferrarib.mplayer.core.network

/**
 * Retrofit contract for the iTunes Search API.
 *
 * Endpoints are added in Phase 2 (search) and Phase 4 (lookup). This interface
 * is the single seam between the network implementation and the rest of the app —
 * replacing iTunes with another source should not require changes outside this
 * package or the repositories that depend on it.
 */
interface ItunesApi
