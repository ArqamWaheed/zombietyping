package com.typingmaster.model;

import java.util.Random;

// internal dictionary — three difficulty buckets
public final class WordBank {
    private static final Random R = new Random();

    private static final String[] SHORT = {
        "run","arm","jaw","gut","rot","bite","claw","skull","blood","limp","groan","dead"
    };
    private static final String[] MEDIUM = {
        "zombie","brains","decay","corpse","plague","horror","virus",
        "shovel","rifle","bunker","escape","scream"
    };
    private static final String[] LONG = {
        "apocalypse","resurrection","contamination","quarantine",
        "extermination","abomination","disinfectant","necropolis"
    };

    private WordBank() {}

    public static String shortWord()  { return SHORT[R.nextInt(SHORT.length)]; }
    public static String mediumWord() { return MEDIUM[R.nextInt(MEDIUM.length)]; }
    public static String longWord()   { return LONG[R.nextInt(LONG.length)]; }
}
