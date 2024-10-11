# Log Parser - Illumio Technical Assessment

This project processes flow log data and analyzes each record using a lookup table. It produces an output CSV file with two key sections:

- **Tag Frequencies**: Shows the count for each tag.
- **Port/Protocol Pair Counts**: Displays the count for each port/protocol combination.

## Files Overview

- **logs.txt**: Input file containing flow log entries (supports only version 2 and skips invalid or unsupported versions).
- **lookup_table.csv**: A CSV file with columns for destination port, protocol, and tag.
- **protocol_map.csv**: A CSV file that maps protocol numbers to their corresponding names.
- **output_results.csv**: Output file that includes both the tag counts and port/protocol pair counts.

## How to Use

### Compilation and Execution

1. Navigate to the `/src` directory where the Java files are located.

2. **To compile the Java files**:

   Use the following command to compile all `.java` files:
   ```bash
   javac *.java

3. **To execute the program**:

   Run the program with the default file names: java LogParser
   Or, specify custom filenames by passing command-line arguments: 
  
   ```bash
   java LogParser --lookup custom_lookup.csv --logs custom_logs.txt --protocol custom_protocol_map.csv -- 
   output custom_results.csv

## Command-Line Options

The program allows for up to four optional arguments, offering flexibility:

--lookup: Specifies the lookup table file (default: lookup_table.csv).
--logs: Specifies the log file (default: logs.txt).
--protocol: Specifies the protocol mapping CSV file (default: protocol_map.csv).
--output: Specifies the output CSV file (default: output_results.csv).

## Key Assumptions

As someone skilled in Java, I chose to implement this project in Java without using external libraries. Below are a few assumptions I made:

Multiple Tags for Port/Protocol Combinations: If a port/protocol combination corresponds to more than one tag, all matching tags are stored and counted.

## Fetching the Protocol Mapping File
Rather than hard-coding protocol numbers and their names, this project retrieves an updated version of this mapping file from the IANA website.

Download: The CSV file from the IANA website contains two columns: Protocol Number and Name, ensuring the system can easily accommodate any newly introduced protocols.

## Handling Source and Destination Ports

Initially, it was unclear whether the port/protocol pair count should only consider the destination port or both source and destination ports. To ensure accuracy, the program counts both source and destination ports.

## ValuePair Class

A ValuePair class was created to efficiently manage pairs of values, particularly for port and protocol pairs. This class plays a key role in correctly tracking unique combinations of ports and protocols.

## Time Complexity

loadLookupMapping: O(N), where N is the number of rows in the lookup table.
loadProtocolDictionary: O(M), where M is the number of rows in the protocol map file.
processFlowLogs: O(L), where L is the number of log entries. Each log entry is processed in constant time to update the corresponding maps, making the overall time complexity linear relative to the number of logs.
saveResultsToFile: O(T + P), where T is the number of unique tags and P is the number of unique port/protocol pairs.

 ## Benefits of Named Arguments

Implementing named arguments makes the program easier to use and more versatile. It can be employed for quick testing or in-depth analysis with varying input and output files.

## Testing
 
The following test cases were executed:

Valid Data: Tested with a log file containing correct data.
No Matching Data: Tested with a lookup table that didn't match any port/protocol combinations in the logs. The output included the count for "Untagged" entries and corresponding port/protocol pairs.
Case Sensitivity: Tested with different cases in the protocol field, and the results correctly showed tag and port/protocol counts.
Empty Log File: Tested with an empty log file, and the output file contained no tag or port/protocol counts.
Corrupted Log Entries: Tested with invalid entries in the log file. The output excluded those entries and only showed results for valid entries.
Empty Lookup Table: Tested with an empty lookup table, and the output displayed "Untagged" entries along with corresponding port/protocol combinations.


