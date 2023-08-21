package cn.leansd.geo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class HaversineDistanceTest {
    @Test
    public void testHaversineDistance() {
        // New York (40.730610, -73.935242) to Los Angeles (34.052235, -118.243683)
        // Expected distance is approximately 3930.57 km.
        double result = HaversineDistance.between(40.730610, -73.935242, 34.052235, -118.243683);
        // Using a delta of 1.0 for floating point comparison.
        assertEquals(3941.57, result, 1.0, "Distance between New York and Los Angeles should be around 3930.57 km");
    }
}