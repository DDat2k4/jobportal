package com.example.jobportal.util;

import java.util.*;
import java.util.stream.Collectors;

public final class FuzzyMatcher {

    private FuzzyMatcher() {}

    private static String normalize(String s) {
        if (s == null) return "";
        return s.trim().toLowerCase().replaceAll("[^a-z0-9\\s+]", " ");
    }

    private static List<String> tokens(String s) {
        return Arrays.stream(normalize(s).split("\\s+"))
                .filter(t -> t.length() > 0)
                .collect(Collectors.toList());
    }

    public static double tokenOverlapRatio(String a, String b) {
        List<String> ta = tokens(a);
        List<String> tb = tokens(b);
        if (tb.isEmpty()) return 0.0;
        Set<String> sa = new HashSet<>(ta);
        long match = tb.stream().filter(sa::contains).count();
        return (double) match / tb.size();
    }

    public static int levenshtein(String a, String b) {
        a = normalize(a); b = normalize(b);
        int n = a.length(), m = b.length();
        if (n == 0) return m;
        if (m == 0) return n;
        int[] prev = new int[m+1], cur = new int[m+1];
        for (int j=0;j<=m;j++) prev[j]=j;
        for (int i=1;i<=n;i++){
            cur[0]=i;
            for (int j=1;j<=m;j++){
                int cost = a.charAt(i-1)==b.charAt(j-1)?0:1;
                cur[j] = Math.min(Math.min(cur[j-1]+1, prev[j]+1), prev[j-1]+cost);
            }
            int[] tmp = prev; prev = cur; cur = tmp;
        }
        return prev[m];
    }

    public static double normalizedLevenshtein(String a, String b){
        a = normalize(a); b = normalize(b);
        int dist = levenshtein(a,b);
        int max = Math.max(a.length(), b.length());
        if (max==0) return 1.0;
        return 1.0 - ((double)dist / max);
    }

    public static boolean matches(String s, String target) {
        if (s == null || target == null) return false;
        String ns = normalize(s), nt = normalize(target);
        if (ns.equals(nt) || ns.contains(nt) || nt.contains(ns)) return true;

        double overlap = tokenOverlapRatio(s, target);
        if (overlap >= 0.6) return true;

        double lev = normalizedLevenshtein(s, target);
        return lev >= 0.75;
    }
}
