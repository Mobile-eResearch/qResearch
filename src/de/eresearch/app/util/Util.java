package de.eresearch.app.util;

public class Util {

    public static boolean nullSafeEquals(Object a, Object b) {
        if (a == null) {
            return (b == null);
        }
        
        return a.equals(b);
    }
    
}
