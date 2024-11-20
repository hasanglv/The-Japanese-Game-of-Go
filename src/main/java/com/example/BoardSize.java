package com.example;

public enum BoardSize {
    NINE(9), THIRTEEN(13), NINETEEN(19);

    private final int size;

    BoardSize(int size) {
        this.size = size;
    }

    public int getSize() {
        return size;
    }

    @Override
    public String toString() {
        return switch (this) {
            case NINE -> "9x9";
            case THIRTEEN -> "13x13";
            case NINETEEN -> "19x19";
        };
    }

    public static BoardSize fromString(String s) {
        return switch (s) {
            case "9x9" -> NINE;
            case "13x13" -> THIRTEEN;
            case "19x19" -> NINETEEN;
            default -> throw new IllegalArgumentException("Invalid board size: " + s);
        };
    }

    public static String[] getStringValues() {
        return java.util.Arrays.stream(BoardSize.values())
                .map(BoardSize::toString)
                .toArray(String[]::new);
    }
}
