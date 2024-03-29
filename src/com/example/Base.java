package com.example;

import org.codehaus.jackson.map.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


public abstract class Base {
    private Socket connection;
    private Integer[] gems;
    private final String serverIp;
    private final int serverPort;
    private PrintWriter printWriter;
    private BufferedReader bufferedReader;
    private int gridHeight, gridWidth, id, score, maxTurnCount, agentCount, trapCount, turnCount;
    private char character;
    private int[] agentScores;
    private String[][] grid;
    private LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, Float>>> probabilities;

    public static String DEFAULT_SERVER_IP = "127.0.0.1";
    public static int DEFAULT_SERVER_PORT = 9921;

    public int getTurnCount() {
        return turnCount;
    }

    public int getGridHeight() {
        return gridHeight;
    }

    public int getGridWidth() {
        return gridWidth;
    }

    public char getCharacter() {
        return character;
    }

    public int getId() {
        return id;
    }

    public int getScore() {
        return score;
    }

    public int getMaxTurnCount() {
        return maxTurnCount;
    }

    public int getAgentCount() {
        return agentCount;
    }

    public int getTrapCount() {
        return trapCount;
    }

    public int[] getAgentScores() {
        return agentScores;
    }

    public String[][] getGrid() {
        return grid;
    }

    public LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, Float>>> getProbabilities() {
        return probabilities;
    }

    public void setProbabilities(LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, Float>>> probabilities) {
        this.probabilities = probabilities;
    }


    public Base() {
        this(DEFAULT_SERVER_IP, DEFAULT_SERVER_PORT);
    }

    public Base(String serverIp) {
        this(serverIp, DEFAULT_SERVER_PORT);
    }

    public Base(int serverPort) {
        this(DEFAULT_SERVER_IP, serverPort);
    }

    public Base(String serverIp, int serverPort) {
        this.serverPort = serverPort;
        this.serverIp = serverIp;
        try {
            connect();
            String data = this.bufferedReader.readLine();
            Map<String, Object> result = new ObjectMapper().readValue(data, HashMap.class);
            System.out.println(result);

            this.gridHeight = (Integer) result.get("height");
            this.gridWidth = (Integer) result.get("width");
            this.character = ((String) result.get("character")).charAt(0);
            this.id = (Integer) result.get("id");
            this.score = (Integer) result.get("score");
            this.maxTurnCount = (Integer) result.get("max_turn_count");
            this.agentCount = (Integer) result.get("agent_count");
            this.agentScores = new int[agentCount];
            this.probabilities = (LinkedHashMap<String, LinkedHashMap<String, LinkedHashMap<String, Float>>>) result.get("probabilities");
            this.printWriter.println("CONFIRM");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void connect() throws IOException {
        if (connection != null && connection.isConnected()) {
            return;
        }
        connection = new Socket(this.serverIp, this.serverPort);
        this.printWriter = new PrintWriter(connection.getOutputStream(), true);
        this.bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
    }

    public enum Action {
        Up("UP"), Down("DOWN"), Left("LEFT"), Right("RIGHT"), UpRight("UP_RIGHT"), UpLeft("UP_LEFT"), DownRight("DOWN_RIGHT"), DownLeft("DOWN_LEFT"), NoOp("NOOP");

        private final String command;

        Action(String command) {
            this.command = command;
        }

    }

    private void parseTurnData(String data) {
        String[] dataArray = data.trim().split(" ");
        this.turnCount = Integer.parseInt(dataArray[0]);
        int count = 1;
        for (int i = 0; i < this.agentCount; i++, count++) {
            agentScores[i] = Integer.parseInt(dataArray[count]);
        }
        this.gems = new Integer[this.agentCount * 4];
        for (int i = 0; i < this.agentCount * 4; i++, count++) {
            this.gems[i] = Integer.parseInt(dataArray[count]);
        }
        this.grid = new String[gridHeight][gridWidth];
        for (int i = 0; i < gridHeight; i++) {
            for (int j = 0; j < gridWidth; j++, count++) {
                this.grid[i][j] = dataArray[count];
            }
        }
    }


    protected abstract Action doTurn();

    public void play() throws IOException {
        connect();
        while (true) {
            String data = this.bufferedReader.readLine();
            while (this.bufferedReader.ready()) {
                data = this.bufferedReader.readLine();
            }
            if (data.contains("finish!")) {
                return;
            }
            parseTurnData(data);
            String action = doTurn().command;
            printWriter.println(action);
        }
    }
}
