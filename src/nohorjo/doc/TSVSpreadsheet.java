package nohorjo.doc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Represents a TSV spreadsheet
 * 
 * @author muhammed
 *
 */
public class TSVSpreadsheet implements Iterable<List<String>> {

	private List<List<String>> records = new ArrayList<>();

	/**
	 * Constructs an empty sheet
	 */
	public TSVSpreadsheet() {
	}

	/**
	 * Constructs a sheet from an {@link InputStream}
	 * 
	 * @param tsvInput
	 *            {@link InputStream}
	 * @throws IOException
	 */
	public TSVSpreadsheet(InputStream tsvInput) throws IOException {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(tsvInput))) {
			String record;
			while ((record = reader.readLine()) != null) {
				addRecord(record);
			}
		}
	}

	/**
	 * Constructs a sheet from an existing one specified by the {@link Path}
	 * 
	 * @param path
	 *            path to the spreadsheet
	 * @throws IOException
	 */
	public TSVSpreadsheet(Path path) throws IOException {
		for (String record : Files.readAllLines(path, Charset.defaultCharset())) {
			addRecord(record);
		}
	}

	/**
	 * Constructs a sheet from a {@link String}
	 * 
	 * @param csvString
	 *            the data
	 */
	public TSVSpreadsheet(String csvString) {
		for (String record : csvString.split("\n")) {
			addRecord(record);
		}
	}

	/**
	 * Adds a tab delimited record
	 * 
	 * @param record
	 *            the record to add
	 * @return the record number, or -1 if failed
	 */
	public int addRecord(String record) {
		return addRecord(record.split("\t"));
	}

	/**
	 * Adds a tab delimited record
	 * 
	 * @param cells
	 *            the cells of the record to add
	 * @return the record number, or -1 if failed
	 */
	public int addRecord(String... cells) {
		if (records.add(new ArrayList<>(Arrays.asList(cells)))) {
			return records.size() - 1;
		}
		return -1;
	}

	/**
	 * Converts a base 26 number to base 10
	 * 
	 * @param b26
	 *            the number to convert
	 * @return the number in base 10
	 */
	private int base26toBase10(StringBuilder b26) {
		String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		b26.reverse();
		int rtn = 0;
		for (int i = 0; i < b26.length(); i++) {
			rtn += Math.pow(26, i) * (alphabet.indexOf(b26.charAt(i)) + 1);
		}
		return rtn;
	}

	/**
	 * Changes the contents of a cell
	 * 
	 * @param columnNumber
	 *            the column number
	 * @param rowNumber
	 *            the row number
	 * @param contents
	 *            the new contents
	 */
	public void editCell(int columnNumber, int rowNumber, String contents) {
		List<String> record = records.get(rowNumber);
		record.set(columnNumber, contents);
		records.set(rowNumber, record);
	}

	/**
	 * Changes the contents of a cell
	 * 
	 * @param cellReference
	 *            the cell reference using letters for columns
	 * @param contents
	 *            the new contents
	 */
	public void editCell(String cellReference, String contents) {
		int[] colRow = extractColRowNumbers(cellReference);
		editCell(colRow[0], colRow[1], contents);
	}

	/**
	 * Converts a cell reference to row and column number
	 * 
	 * @param cellReference
	 *            the cell reference using letters for columns
	 * @return an int array [column,row]
	 */
	private int[] extractColRowNumbers(String cellReference) {
		StringBuilder col = new StringBuilder();
		StringBuilder row = new StringBuilder();
		for (int i = 0; i < cellReference.length(); i++) {
			char c = cellReference.charAt(i);
			if (Character.isLetter(c)) {
				col.append(new String(c + "").toUpperCase());
			} else {
				row.append(c);
			}
		}
		return new int[] { base26toBase10(col) - 1, Integer.parseInt(row.toString()) - 1 };
	}

	/**
	 * Gets the contents of a cell
	 * 
	 * @param columnNumber
	 *            the column number
	 * @param rowNumber
	 *            the row number
	 * @return the contents of the cell
	 */
	public String getCell(int columnNumber, int rowNumber) {
		List<String> record = getRecord(rowNumber);
		while (record.size() <= columnNumber) {
			record.add("");
		}
		return record.get(columnNumber);

	}

	/**
	 * Gets the contents of a cell
	 * 
	 * @param cellReference
	 *            the cell reference using letters for columns
	 * @return the contents of the cell
	 */
	public String getCell(String cellReference) {
		int[] colRow = extractColRowNumbers(cellReference);
		return getCell(colRow[0], colRow[1]);
	}

	/**
	 * Gets a column
	 * 
	 * @param columnNumber
	 *            the column
	 * @return a {@link List} of the values in the column
	 */
	public List<String> getColumn(int columnNumber) {
		List<String> column = new ArrayList<>();
		for (List<String> record : records) {
			try {
				column.add(record.get(columnNumber));
			} catch (IndexOutOfBoundsException e) {
				column.add("");
			}
		}
		return column;
	}

	/**
	 * Gets a column
	 * 
	 * @param columnReference
	 *            the column letter
	 * @return a {@link List} of the values in the column
	 */
	public List<String> getColumn(String columnReference) {
		int[] colRow = extractColRowNumbers(columnReference);
		return getColumn(colRow[0]);
	}

	/**
	 * Gets a record
	 * 
	 * @param rowNumber
	 *            the row number
	 * @return a {@link List} of the cells
	 */
	public List<String> getRecord(int rowNumber) {
		if (rowNumber < 0) {
			rowNumber = 0;
		}
		while (records.size() <= rowNumber) {
			List<String> emptyRecord = new ArrayList<>();
			records.add(emptyRecord);
		}
		return records.get(rowNumber);
	}

	/**
	 * Gets a record
	 * 
	 * @param rowNumber
	 *            the row number
	 * @return a {@link List} of the cells
	 */
	public List<String> getLastRecord() {
		return records.get(records.size() - 1);
	}

	@Override
	public String toString() {
		StringBuilder tsv = new StringBuilder();
		for (List<String> record : this.records) {
			for (String cell : record) {
				tsv.append(cell + "\t");
			}
			tsv.append('\n');
		}
		return tsv.toString();
	}

	/**
	 * Writes the sheet to the {@link OutputStream}
	 * 
	 * @param out
	 *            {@link OutputStream}
	 * @throws IOException
	 */
	public void write(OutputStream out) throws IOException {
		try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out))) {
			writer.write(toString());
		}
	}

	@Override
	public Iterator<List<String>> iterator() {
		return new Iterator<List<String>>() {
			int recordNumber = 0;

			@Override
			public List<String> next() {
				return records.get(recordNumber++);
			}

			@Override
			public boolean hasNext() {
				return recordNumber < records.size();
			}

			@Override
			public void remove() {
				records.remove(recordNumber);
			}
		};
	}

	/**
	 * Gets the number of records in the spreadsheet
	 * 
	 * @return the number of records
	 */
	public int size() {
		return records.size();
	}
}
