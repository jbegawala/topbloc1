package com.junaid;

import java.util.HashMap;

public class ExcelTableFormat
{
	private HashMap<String, Series.DataType> mapping;

	/**
	 * Creates a list of data series and corresponding formats
	 */
	ExcelTableFormat()
	{
		this.mapping = new HashMap<>();
	}

	/**
	 * Adds series key and data type pair. Doesn't allow keys to be overwritten.
	 * @param dataSeriesKey identifier for series (column header)
	 * @param dataType data format for series. all data must be able to be stored in this format.
	 * @return true if pair is saved, false otherwise
	 */
	boolean add(String dataSeriesKey, Series.DataType dataType)
	{
		if ( dataSeriesKey == null || dataSeriesKey.isEmpty() || dataType == null || this.mapping.containsKey(dataSeriesKey) )
		{
			return false;
		}
		this.mapping.put(dataSeriesKey, dataType);
		return true;
	}

	/**
	 * Returns data type for the given series
	 * @param dataSeriesKey data series to get format
	 * @return data type for the given series
	 */
	Series.DataType getType(String dataSeriesKey)
	{
		if ( dataSeriesKey == null || dataSeriesKey.isEmpty() || !this.mapping.containsKey(dataSeriesKey) )
		{
			return null;
		}
		return this.mapping.get(dataSeriesKey);
	}
}
