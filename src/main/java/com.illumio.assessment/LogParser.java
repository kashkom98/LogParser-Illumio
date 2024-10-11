package src.main.java.com.illumio.assessment;

import java.io.*;
import java.util.*;


public class LogParser {

  private Map<ValuePair<Integer, String>, List<String>> lookupMapping = new HashMap<>();
  private Map<String, Integer> tagFrequency = new HashMap<>();
  private Map<ValuePair<Integer, String>, Integer> portProtocolCounter = new HashMap<>();
  private Map<Integer, String> protocolDictionary = new HashMap<>();
  private int unidentifiedTagCount = 0;

  /**
   * Loads the lookup table from a CSV file. Each line should include destination port, protocol, and tag.
   * Skips the header if present.
   * @param filePath the path to the lookup file.
   * @throws IOException
   */
  public void loadLookupMapping(String filePath) throws IOException {
    try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath))) {
      String line = bufferedReader.readLine();
      if (line != null && !isValidNumber(line.split(",")[0].trim())) {
        line = bufferedReader.readLine();  // Skip header if exists
      }

      while (line != null) {
        String[] columns = line.trim().split(",");
        if (columns.length == 3) {
          try {
            int port = Integer.parseInt(columns[0].trim());
            String protocol = columns[1].trim().toLowerCase();
            String tag = columns[2].trim();
            lookupMapping.computeIfAbsent(new ValuePair<>(port, protocol), k -> new ArrayList<>()).add(tag);
          } catch (NumberFormatException e) {
            System.err.println("Invalid port number found: " + columns[0]);
          }
        }
        line = bufferedReader.readLine();
      }
    }
  }

  private boolean isValidNumber(String str) {
    return str.matches("\\d+");
  }

  /**
   * Loads the protocol mapping from a CSV file.
   * @param filePath the path to the protocol map file.
   * @throws IOException
   */
  public void loadProtocolDictionary(String filePath) throws IOException {
    try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
      String line;
      reader.readLine();  // Skip header
      while ((line = reader.readLine()) != null) {
        line = line.trim();
        if (line.isEmpty()) continue;

        String[] parts = line.split(",");
        if (parts.length < 2) continue;

        try {
          int protocolNum = Integer.parseInt(parts[0].trim());
          String protocolName = parts[1].trim().toLowerCase();
          protocolDictionary.put(protocolNum, protocolName);
        } catch (NumberFormatException e) {
          System.out.println("Skipped corrupt protocol number: " + parts[0]);
        }
      }
    }
  }

  /**
   * Parses the flow logs, maps each record to a tag, and updates the tag and port/protocol count mappings.
   * @param filePath the path to the log file.
   * @throws IOException
   */
  public void processFlowLogs(String filePath) throws IOException {
    try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath))) {
      String line;
      while ((line = bufferedReader.readLine()) != null) {
        line = line.trim();
        if (line.isEmpty()) continue;

        String[] columns = line.split("\\s+");
        if (columns.length < 14) continue;

        try {
          int sourcePort = Integer.parseInt(columns[5].trim());
          int destinationPort = Integer.parseInt(columns[6].trim());
          int protocolCode = Integer.parseInt(columns[7].trim());
          String protocol = protocolDictionary.getOrDefault(protocolCode, "uncommon").toLowerCase();

          ValuePair<Integer, String> destinationPortProtocol = new ValuePair<>(destinationPort, protocol);
          ValuePair<Integer, String> sourcePortProtocol = new ValuePair<>(sourcePort, protocol);
          List<String> associatedTags = lookupMapping.getOrDefault(destinationPortProtocol, Collections.singletonList("Unidentified"));

          for (String tag : associatedTags) {
            tagFrequency.put(tag, tagFrequency.getOrDefault(tag, 0) + 1);
          }

          portProtocolCounter.put(sourcePortProtocol, portProtocolCounter.getOrDefault(sourcePortProtocol, 0) + 1);
          portProtocolCounter.put(destinationPortProtocol, portProtocolCounter.getOrDefault(destinationPortProtocol, 0) + 1);
        } catch (NumberFormatException e) {
          System.err.println("Invalid port or protocol number in log entry: " + Arrays.toString(columns));
        }
      }
    }
  }

  /**
   * Saves the results (tag counts and port/protocol counts) to an output file.
   * @param outputFilePath the path to the output file.
   * @throws IOException
   */
  public void saveResultsToFile(String outputFilePath) throws IOException {
    File outputFile = new File(outputFilePath);
    if (!outputFile.exists()) {
      outputFile.createNewFile();
    }

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
      writer.write("Tag Frequencies:\n");
      writer.write("Tag,Count\n");
      for (Map.Entry<String, Integer> entry : tagFrequency.entrySet()) {
        writer.write(entry.getKey() + "," + entry.getValue() + "\n");
      }

      writer.write("\nPort/Protocol Count:\n");
      writer.write("Port,Protocol,Count\n");
      for (Map.Entry<ValuePair<Integer, String>, Integer> entry : portProtocolCounter.entrySet()) {
        ValuePair<Integer, String> key = entry.getKey();
        writer.write(key.firstValue + "," + key.secondValue + "," + entry.getValue() + "\n");
      }
    }
  }

  public static void main(String[] args) {
    // Default paths for files
    String lookupPath = "resources/lookup_table.csv";
    String logFilePath = "resources/logs.txt";
    String protocolFilePath = "resources/protocol_map.csv";
    String resultPath = "resources/output_results.txt";

    // Parse named arguments
    for (int i = 0; i < args.length; i++) {
      switch (args[i]) {
        case "--lookup":
          if (i + 1 < args.length) lookupPath = args[++i];
          break;
        case "--logs":
          if (i + 1 < args.length) logFilePath = args[++i];
          break;
        case "--protocol":
          if (i + 1 < args.length) protocolFilePath = args[++i];
          break;
        case "--output":
          if (i + 1 < args.length) resultPath = args[++i];
          break;
        default:
          System.out.println("Unknown argument: " + args[i]);
          return;
      }
    }

    LogParser analyzer = new LogParser();
    try {
      analyzer.loadLookupMapping(lookupPath);
      analyzer.loadProtocolDictionary(protocolFilePath);
      analyzer.processFlowLogs(logFilePath);
      analyzer.saveResultsToFile(resultPath);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
