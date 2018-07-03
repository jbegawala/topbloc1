package com.junaid;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExcelTable
{
	private HashMap<String, Series<String>> dataString;
	private HashMap<String, Series<Integer>> dataInteger;

	/**
	 * Data structure for an Excel document with a single sheet containing a table. The table
	 * must contain headers in the first row. All columns must have headers. The data in the
	 * columns must be either all strings or all integers.
	 *
	 * @param file - Excel file to read
	 * @param format - List of columns to import and the corresponding formats
	 * @throws IOException if Excel file can not be opened
	 */
	ExcelTable(File file, ExcelTableFormat format) throws IOException
	{
		List<String> seriesToColumnMap = new ArrayList<>();

		// Decided to store series in their corresponding data types. This way any conversions/casting is only done once
		// when the data is read instead of each time the data is accessed.
		this.dataString = new HashMap<>();
		this.dataInteger = new HashMap<>();

		FileInputStream fis = new FileInputStream(file);
		XSSFWorkbook workbook = new XSSFWorkbook(fis);
		Sheet sheet = workbook.getSheetAt(0);  // Assuming only one sheet. This can easily be extended to
		// read multiple sheets for the series provided in format, but it would require that the Excel
		// document have unique column headers across all tables.

		Row row = sheet.getRow(sheet.getFirstRowNum());  // Headers must be in first row
		String key;
		Cell cell;
		int firstColumn = row.getFirstCellNum();  // Define first column based on header; there might be a data
		// row that doesn't have a value for the first column

		// Parse header row and map the data series to columns
		for ( int columnNumber = firstColumn; columnNumber < row.getLastCellNum(); columnNumber++ )
		{
			cell = row.getCell(columnNumber);
			key = cell.getStringCellValue();
			if ( !key.isEmpty() )
			{
				if ( format.getType(key) == Series.DataType.Integer )
				{
					this.dataInteger.put(key, new Series<>(Series.DataType.Integer));
					seriesToColumnMap.add(columnNumber, key);
				}
				else if ( format.getType(key) == Series.DataType.String )
				{
					this.dataString.put(key, new Series<>(Series.DataType.String));
					seriesToColumnMap.add(columnNumber, key);
				}
			}
		}

		// Parse data rows
		Series series;
		for ( int rowNum = sheet.getFirstRowNum() + 1; rowNum <= sheet.getLastRowNum(); rowNum++ )
		{
			row = sheet.getRow(rowNum);
			for ( int col = firstColumn; col < row.getLastCellNum(); col++ )
			{
				key = seriesToColumnMap.get(col);
				if ( key.isEmpty() )
				{
					continue;  // no header for column. orphaned data
				}
				series = this.getSeries(key);
				if ( series == null )
				{
					continue;
				}
				series.put(row.getCell(col));
			}
		}
		try
		{
			fis.close();
		}
		catch (IOException ignore)
		{
			// Not nice to ignore exceptions. However, we aren't writing data so not a big deal if we
			// ignore failure here since the stream resources will eventually be reclaimed
		}
	}

	/**
	 * Returns size of given data series
	 * @param dataSeries data series to get size
	 * @return size of given data series
	 */
	public int size(String dataSeries)
	{
		Series series = this.getSeries(dataSeries);
		return series == null ? 0 : series.size();
	}

	/**
	 * Returns value at given row for given data series
	 * @param dataSeries data series to get value
	 * @param row entry number in data series
	 * @return value at given row for given data series
	 */
	public String getStringValue(String dataSeries, int row)
	{
		Series series = this.getSeries(dataSeries);
		return series == null ? null : (String) series.get(row);
	}

	/**
	 * Returns value at given row for given data series
	 * @param dataSeries data series to get value
	 * @param row entry number in data series
	 * @return value at given row for given data series
	 */
	public Integer getIntegerValue(String dataSeries, int row)
	{
		Series series = this.getSeries(dataSeries);
		return series == null ? null : (Integer) series.get(row);
	}

	/**
	 * Returns series with given key
	 * @param key unique key for series
	 * @return series for given key
	 */
	private Series getSeries(String key)
	{
		if ( key == null || key.isEmpty() )
		{
			return null;
		}
		if ( this.dataString.containsKey(key) )
		{
			return this.dataString.get(key);
		}
		if ( this.dataInteger.containsKey(key) )
		{
			return this.dataInteger.get(key);
		}
		return null;
	}
}