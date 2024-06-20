/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package de.sga.cache_simulator;

/**
 *
 * @author salih
 */
public class CacheCoherenceSimulator {

    public static void main(String[] args) {
        Cache cache = new Cache(512, 4, 0);
        cache.write(1);
    }
}
