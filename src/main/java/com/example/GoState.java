package com.example;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class GoState implements Predicate<Point>, Serializable {
    public final BoardSpace[][] board;
    public Stone turn;
    private int blackCaptured;
    private int whiteCaptured;
    private final Set<GoState> previousStates = new HashSet<>();

    public BoardSpace[][] getBoard() {
        return board;
    }

    public Stone getTurn() {
        return turn;
    }

    public void setTurn(Stone turn) {
        this.turn = turn;
    }

    public int getBlackCaptured() {
        return blackCaptured;
    }

    public void setBlackCaptured(int blackCaptured) {
        this.blackCaptured = blackCaptured;
    }

    public int getWhiteCaptured() {
        return whiteCaptured;
    }

    public void setWhiteCaptured(int whiteCaptured) {
        this.whiteCaptured = whiteCaptured;
    }

    public Set<GoState> getPreviousStates() {
        return previousStates;
    }

    public GoState(int size) {
        board = new BoardSpace[size][size];
        for (int i = 0; i < size; i++) {
            Arrays.fill(board[i], BoardSpace.EMPTY);
        }
        turn = Stone.BLACK;
        blackCaptured = 0;
        whiteCaptured = 0;
    }

    // Copy constructor
    public GoState(GoState other) {
        board = Arrays.stream(other.board)
                .map(row -> Arrays.copyOf(row, row.length))
                .toArray(BoardSpace[][]::new);
        turn = other.turn;
    }

    @Override
    public boolean test(Point p) {
        return p.x >= 0 && p.x < board.length && p.y >= 0 && p.y < board.length;
    }

    public Point[] getNeighbors(Point p) {
        return Arrays.stream(new Point[]{
                new Point(p.x - 1, p.y), new Point(p.x + 1, p.y),
                new Point(p.x, p.y - 1), new Point(p.x, p.y + 1)
        }).filter(this::test).toArray(Point[]::new);
    }

    public Point[] getLiberties(Stone s, Point p, Set<Point> scanned) {
        Set<Point> toScan = new HashSet<>();
        toScan.add(p);

        Set<Point> liberties = new HashSet<>();
        while (!toScan.isEmpty()) {
            Point current = toScan.iterator().next();
            toScan.remove(current);

            if (!test(current) || scanned.contains(current)) continue;
            scanned.add(current);

            BoardSpace space = board[current.x][current.y];
            if (space == BoardSpace.EMPTY) {
                liberties.add(current);
            } else if (space.stone == s) {
                toScan.addAll(Arrays.asList(getNeighbors(current)));
            }
        }
        return liberties.toArray(new Point[0]);
    }

    public void checkCaptured(Point p) {
        Stone opponent = turn.opposite();
        Arrays.stream(getNeighbors(p))
                .filter(neighbor -> board[neighbor.x][neighbor.y].stone == opponent)
                .forEach(neighbor -> {
                    Set<Point> scanned = new HashSet<>();
                    Point[] liberties = getLiberties(opponent, neighbor, scanned);
                    if (liberties.length == 0) {
                        scanned.forEach(captured -> {
                            board[captured.x][captured.y] = BoardSpace.EMPTY;
                        });
                        if (opponent == Stone.BLACK) {
                            whiteCaptured += scanned.size();
                        } else {
                            blackCaptured += scanned.size();
                        }
                    }
                });
    }

    public GoState placeStone(Point p) {
        board[p.x][p.y] = BoardSpace.fromStone(turn);
        Arrays.stream(getNeighbors(p)).forEach(this::checkCaptured);
        return this;
    }

    public boolean isLegalMove(Point p) {
        if (!test(p) || board[p.x][p.y] != BoardSpace.EMPTY) return false;
        Set<Point> scanned = new HashSet<>();
        Point[] liberties = getLiberties(turn, p, scanned);
        return liberties.length > 0;
    }

    public boolean makeMove(Point p) {
        if (p == null) {
            previousStates.add(new GoState(this));
            turn = turn.opposite();
            return previousStates.contains(this);
        }
        if (!isLegalMove(p)) return false;
        previousStates.add(new GoState(this));
        placeStone(p);
        turn = turn.opposite();
        return false;
    }

    @Override
    public String toString() {
        return "Black Captured: " + blackCaptured + "\nWhite Captured: " + whiteCaptured;
    }

    public void saveGame(File file) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
            out.writeObject(this);
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to save game file to: " + file.getAbsolutePath(), e);
        }
    }

    public static GoState loadGame(File file) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            return (GoState) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalArgumentException("Failed to load game file to: " + file.getAbsolutePath(), e);
        }
    }
}
